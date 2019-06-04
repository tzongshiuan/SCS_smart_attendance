package com.gorilla.attendance.enterprise.util.apitask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import com.gorilla.attendance.enterprise.database.DatabaseAdapter;
import com.gorilla.attendance.enterprise.database.bean.UserClockBean;
import com.gorilla.attendance.enterprise.datamodel.RecordsModel;
import com.gorilla.attendance.enterprise.datamodel.RecordsReplyModel;
import com.gorilla.attendance.enterprise.util.ApiAccessor;
import com.gorilla.attendance.enterprise.util.ApiResultParser;
import com.gorilla.attendance.enterprise.util.ClockUtils;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.EnterpriseUtils;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceAttendanceRecordsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ggshao on 2017/3/22.
 */

public class DeviceAttendanceRecordsTask extends AsyncTask<Object, Integer, RecordsReplyModel> {

    public static final String TAG = "DeviceAttendanceRecordsTask";

    IDeviceAttendanceRecordsListener callback;

    @Override
    protected RecordsReplyModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        String deviceToken = (String) params[1];
        boolean isPassDbData = (boolean) params[2];
        String type = (String) params[3];
        int serialNumber = (int) params[4];
        callback = (IDeviceAttendanceRecordsListener) params[5];

        JSONArray jsonRecordsArr = new JSONArray();

        if(isPassDbData == false){
            //get data from ClockUtils

            JSONObject jsonRecordsObj = new JSONObject();
            try {

                jsonRecordsObj.put(RecordsModel.KEY_ID, ClockUtils.mLoginUuid);
                jsonRecordsObj.put(RecordsModel.KEY_SERIAL, serialNumber);
                LOG.D(TAG,"ClockUtils.mLoginUuid = " + ClockUtils.mLoginUuid);
                jsonRecordsObj.put(RecordsModel.KEY_SECURITY_CODE, String.valueOf(ClockUtils.mLoginAccount));

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

                String dateString = sdf.format(new Date(ClockUtils.mLoginTime));
                jsonRecordsObj.put(RecordsModel.KEY_CLIENT_TIME, dateString);

                jsonRecordsObj.put(RecordsModel.KEY_TYPE, type);
                jsonRecordsObj.put(RecordsModel.KEY_FACE_VERIFY, ClockUtils.mFaceVerify);
                jsonRecordsObj.put(RecordsModel.KEY_LIVENESS, ClockUtils.mLiveness);
                jsonRecordsObj.put(RecordsModel.KEY_MODE, ClockUtils.mMode);
//                byte test[] = {'1','2'};
//
//                jsonRecordsObj.put(RecordsModel.KEY_FACE_IMG, Base64.encodeToString(test,Base64.DEFAULT));
                if(EnterpriseUtils.mFacePngList != null && EnterpriseUtils.mFacePngList.size() > 0){
                    jsonRecordsObj.put(RecordsModel.KEY_FACE_IMG, Base64.encodeToString(EnterpriseUtils.mFacePngList.get(0),Base64.DEFAULT));
                }else{
                    jsonRecordsObj.put(RecordsModel.KEY_FACE_IMG, "");
                }





                LOG.D(TAG,"RecordsReplyModel dateString = " + dateString);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            jsonRecordsArr.put(jsonRecordsObj);


        }else{
            //get data from DB

//            ArrayList<UserClockBean> attendanceBean = DatabaseAdapter.getInstance(context).getUserClockByModule(Constants.MODULES_ATTENDANCE);
//            ArrayList<UserClockBean> attendanceBean = DatabaseAdapter.getInstance(context).getUserClockByModuleRecordMode(Constants.MODULES_ATTENDANCE , Constants.RECORD_MODE_RECORD, Constants.MODULES_ATTENDANCE_ACCESS);
            ArrayList<UserClockBean> attendanceBean = DatabaseAdapter.getInstance(context).getUserClockByModuleRecordMode(Constants.MODULES_ATTENDANCE , Constants.RECORD_MODE_RECORD);

            LOG.D(TAG,"DeviceAttendanceRecordsTask attendanceBean = " + attendanceBean);
            if(attendanceBean != null){
                for(int i = 0 ; i < attendanceBean.size() ; i++){
                    JSONObject jsonRecordsObj = new JSONObject();
                    try {

                        jsonRecordsObj.put(RecordsModel.KEY_ID, attendanceBean.get(i).getId());
                        jsonRecordsObj.put(RecordsModel.KEY_SERIAL, attendanceBean.get(i).getSerial());


                        jsonRecordsObj.put(RecordsModel.KEY_SECURITY_CODE, attendanceBean.get(i).getSecurityCode());

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

                        String dateString = sdf.format(new Date(attendanceBean.get(i).getClientTime()));
                        jsonRecordsObj.put(RecordsModel.KEY_CLIENT_TIME, dateString);

                        jsonRecordsObj.put(RecordsModel.KEY_TYPE, attendanceBean.get(i).getType());
                        jsonRecordsObj.put(RecordsModel.KEY_FACE_VERIFY, attendanceBean.get(i).getFaceVerify());

                        jsonRecordsObj.put(RecordsModel.KEY_FACE_IMG, Base64.encodeToString(attendanceBean.get(i).getPngInfo1(),Base64.DEFAULT));

                        jsonRecordsObj.put(RecordsModel.KEY_LIVENESS, attendanceBean.get(i).getLiveness());
                        jsonRecordsObj.put(RecordsModel.KEY_MODE, attendanceBean.get(i).getMode());

                        LOG.D(TAG,"DeviceAttendanceRecordsTask dateString  = " + dateString);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    jsonRecordsArr.put(jsonRecordsObj);
                }
            }

        }

        RecordsReplyModel model = null;

        if(jsonRecordsArr.length() > 0){
            try {
                model = ApiResultParser.attendanceRecordsParser(ApiAccessor.deviceAttendanceRecords(context, deviceToken, jsonRecordsArr.toString()));
            } catch (Throwable tr) {
                LOG.E(TAG, "DeviceAttendanceRecordsTask() - failed.", tr);
            }
        }else{

        }



        return model;
    }

    @Override
    protected void onPostExecute(RecordsReplyModel result) {

        if(callback != null)
            callback.onDeviceAttendanceRecords(result);
    }

}
