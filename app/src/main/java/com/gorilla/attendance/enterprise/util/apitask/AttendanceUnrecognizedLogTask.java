package com.gorilla.attendance.enterprise.util.apitask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import com.gorilla.attendance.enterprise.database.DatabaseAdapter;
import com.gorilla.attendance.enterprise.database.bean.UserClockBean;
import com.gorilla.attendance.enterprise.datamodel.ErrorLogsModel;
import com.gorilla.attendance.enterprise.datamodel.RecordsReplyModel;
import com.gorilla.attendance.enterprise.util.ApiAccessor;
import com.gorilla.attendance.enterprise.util.ApiResultParser;
import com.gorilla.attendance.enterprise.util.ClockUtils;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.EnterpriseUtils;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.apitask.listener.IAttendanceUnrecognizedLogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ggshao on 2017/2/12.
 */

public class AttendanceUnrecognizedLogTask extends AsyncTask<Object, Integer, RecordsReplyModel> {

    public static final String TAG = "AttendanceUnrecognizedLogTask";

    IAttendanceUnrecognizedLogListener callback;

    @Override
    protected RecordsReplyModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        String deviceToken = (String) params[1];
        boolean isPassDbData = (boolean) params[2];
        callback = (IAttendanceUnrecognizedLogListener) params[3];

        JSONArray jsonErrorLogArr = new JSONArray();

        if(isPassDbData == false){
            //get data from ClockUtils

            JSONObject jsonErrorLogObj = new JSONObject();
            try {

                jsonErrorLogObj.put(ErrorLogsModel.KEY_SERIAL, ClockUtils.mSerialNumber);

                LOG.D(TAG,"EnterpriseUtils.mFacePngList = " + EnterpriseUtils.mFacePngList);
                if(EnterpriseUtils.mFacePngList != null && EnterpriseUtils.mFacePngList.size() > 0){
                    LOG.D(TAG,"EnterpriseUtils.mFacePngList.size() 111 = " + EnterpriseUtils.mFacePngList.size());
                    //已臉找臉 USE
                    jsonErrorLogObj.put(ErrorLogsModel.KEY_SECURITY_CODE, "");
                }else{
                    //PIN CODE USE
                    LOG.D(TAG,"EnterpriseUtils.mFacePngList.size() 222 = " + EnterpriseUtils.mFacePngList.size());
                    jsonErrorLogObj.put(ErrorLogsModel.KEY_SECURITY_CODE, String.valueOf(ClockUtils.mLoginAccount));
                }
                LOG.D(TAG,"EnterpriseUtils.mFacePngList jsonErrorLogObj = " + jsonErrorLogObj.toString());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

                String dateString = sdf.format(new Date(ClockUtils.mLoginTime));
                jsonErrorLogObj.put(ErrorLogsModel.KEY_DEVICE_TIME, dateString);

//                byte test[] = {'1','2'};
//
//                jsonRecordsObj.put(RecordsModel.KEY_FACE_IMG, Base64.encodeToString(test,Base64.DEFAULT));
                if(EnterpriseUtils.mFacePngList != null && EnterpriseUtils.mFacePngList.size() > 0){
                    jsonErrorLogObj.put(ErrorLogsModel.KEY_FACE_IMG, Base64.encodeToString(EnterpriseUtils.mFacePngList.get(0),Base64.DEFAULT));
                }else{
                    jsonErrorLogObj.put(ErrorLogsModel.KEY_FACE_IMG, "");
                }

                jsonErrorLogObj.put(ErrorLogsModel.KEY_LIVENESS, ClockUtils.mLiveness);
                jsonErrorLogObj.put(ErrorLogsModel.KEY_MODE, ClockUtils.mMode);
                jsonErrorLogObj.put(ErrorLogsModel.KEY_RFID, "");


            } catch (JSONException e) {
                e.printStackTrace();
            }

            jsonErrorLogArr.put(jsonErrorLogObj);


        }else{
            //get data from DB

            ArrayList<UserClockBean> attendanceBean = DatabaseAdapter.getInstance(context).getUserClockByModuleRecordMode(Constants.MODULES_ATTENDANCE, Constants.RECORD_MODE_UNRECOGNIZED);
//            ArrayList<UserClockBean> attendanceBean = DatabaseAdapter.getInstance(context).getUserClockByModuleRecordMode(Constants.MODULES_ATTENDANCE, Constants.RECORD_MODE_UNRECOGNIZED);

            LOG.D(TAG,"AttendanceUnrecognizedLogTask attendanceBean = " + attendanceBean);
            if(attendanceBean != null){
                for(int i = 0 ; i < attendanceBean.size() ; i++){
                    JSONObject jsonErrorLogObj = new JSONObject();
                    try {

                        jsonErrorLogObj.put(ErrorLogsModel.KEY_SERIAL, attendanceBean.get(i).getSerial());

                        if(attendanceBean.get(i).getSecurityCode() == null){
                            jsonErrorLogObj.put(ErrorLogsModel.KEY_SECURITY_CODE, "");
                        }else{
                            jsonErrorLogObj.put(ErrorLogsModel.KEY_SECURITY_CODE, attendanceBean.get(i).getSecurityCode());
                        }


                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

                        String dateString = sdf.format(new Date(attendanceBean.get(i).getClientTime()));
                        jsonErrorLogObj.put(ErrorLogsModel.KEY_DEVICE_TIME, dateString);

                        jsonErrorLogObj.put(ErrorLogsModel.KEY_FACE_IMG, Base64.encodeToString(attendanceBean.get(i).getPngInfo1(),Base64.DEFAULT));
//                        jsonErrorLogObj.put(ErrorLogsModel.KEY_FACE_IMG, "");

                        jsonErrorLogObj.put(ErrorLogsModel.KEY_LIVENESS, attendanceBean.get(i).getLiveness());
                        jsonErrorLogObj.put(ErrorLogsModel.KEY_MODE, attendanceBean.get(i).getMode());
                        if(attendanceBean.get(i).getSecurityCode() == null){
                            jsonErrorLogObj.put(ErrorLogsModel.KEY_RFID, "");
                        }else{
                            jsonErrorLogObj.put(ErrorLogsModel.KEY_RFID, attendanceBean.get(i).getRfid());
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    jsonErrorLogArr.put(jsonErrorLogObj);

                    LOG.D(TAG,"jsonErrorLogArr.toString() = " + jsonErrorLogArr.toString());
                }
            }


        }

        RecordsReplyModel model = null;

        if(jsonErrorLogArr.length() > 0){
            try {
                model = ApiResultParser.attendanceUnrecognizedLogParser(ApiAccessor.attendanceUnrecognizedLog(context, deviceToken, jsonErrorLogArr.toString()));
            } catch (Throwable tr) {
                LOG.E(TAG, "AttendanceUnrecognizedLogTask() - failed.", tr);
            }
        }else{

        }

        return model;
    }

    @Override
    protected void onPostExecute(RecordsReplyModel result) {

        if(callback != null)
            callback.onAttendanceUnrecognizedLog(result);
    }

}
