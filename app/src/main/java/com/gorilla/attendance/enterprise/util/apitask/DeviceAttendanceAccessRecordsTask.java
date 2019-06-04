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
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceAttendanceRecordsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ggshao on 2017/3/28.
 */

public class DeviceAttendanceAccessRecordsTask extends AsyncTask<Object, Integer, RecordsReplyModel> {

    public static final String TAG = "DeviceAttendanceAccessRecordsTask";

    IDeviceAttendanceRecordsListener callback;

    @Override
    protected RecordsReplyModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        String deviceToken = (String) params[1];
        boolean isPassDbData = (boolean) params[2];
        String attendanceType = (String) params[3];
        int attendanceSerialNumber = (int) params[4];
        String accessType = (String) params[5];
        int accessSerialNumber = (int) params[6];
        callback = (IDeviceAttendanceRecordsListener) params[7];

        JSONArray jsonRecordsArr = new JSONArray();

        if(isPassDbData == false){
            //get data from ClockUtils

//            JSONObject jsonRecordsObj = new JSONObject();
//            try {
//
//                jsonRecordsObj.put(RecordsModel.KEY_ID, ClockUtils.mLoginUuid);
//                jsonRecordsObj.put(RecordsModel.KEY_SERIAL, serialNumber);
//                LOG.D(TAG,"ClockUtils.mLoginUuid = " + ClockUtils.mLoginUuid);
//                jsonRecordsObj.put(RecordsModel.KEY_SECURITY_CODE, String.valueOf(ClockUtils.mLoginAccount));
//
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//
//                String dateString = sdf.format(new Date(ClockUtils.mLoginTime));
//                jsonRecordsObj.put(RecordsModel.KEY_CLIENT_TIME, dateString);
//
//                jsonRecordsObj.put(RecordsModel.KEY_TYPE, type);
//                jsonRecordsObj.put(RecordsModel.KEY_FACE_VERIFY, ClockUtils.mFaceVerify);
//
////                byte test[] = {'1','2'};
////
////                jsonRecordsObj.put(RecordsModel.KEY_FACE_IMG, Base64.encodeToString(test,Base64.DEFAULT));
//                if(EnterpriseUtils.mFacePngList != null && EnterpriseUtils.mFacePngList.size() > 0){
//                    jsonRecordsObj.put(RecordsModel.KEY_FACE_IMG, Base64.encodeToString(EnterpriseUtils.mFacePngList.get(0),Base64.DEFAULT));
//                }else{
//                    jsonRecordsObj.put(RecordsModel.KEY_FACE_IMG, "");
//                }
//
//                jsonRecordsObj.put(RecordsModel.KEY_LIVENESS, ClockUtils.mLiveness);
//                jsonRecordsObj.put(RecordsModel.KEY_MODE, ClockUtils.mMode);
//
//                LOG.D(TAG,"RecordsReplyModel dateString = " + dateString);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            jsonRecordsArr.put(jsonRecordsObj);


        }else{
            //get data from DB

//            ArrayList<UserClockBean> attendanceBean = DatabaseAdapter.getInstance(context).getUserClockByModule(Constants.MODULES_ATTENDANCE);
            ArrayList<UserClockBean> attendanceBean = DatabaseAdapter.getInstance(context).getUserClockByModuleRecordMode(Constants.MODULES_ATTENDANCE_ACCESS , Constants.RECORD_MODE_RECORD);

            LOG.D(TAG,"DeviceAttendanceAccessRecordsTask attendanceBean = " + attendanceBean);
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

                        LOG.D(TAG,"AttendanceRecordsTask dateString  = " + dateString);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    jsonRecordsArr.put(jsonRecordsObj);
                }
            }

        }

        RecordsReplyModel attendanceModel = null;
        RecordsReplyModel accessModel = null;

        try {
            attendanceModel = ApiResultParser.attendanceRecordsParser(ApiAccessor.deviceAttendanceRecords(context, deviceToken, jsonRecordsArr.toString()));
            accessModel = ApiResultParser.attendanceRecordsParser(ApiAccessor.deviceAttendanceRecords(context, deviceToken, jsonRecordsArr.toString()));
        } catch (Throwable tr) {
            LOG.E(TAG, "DeviceAttendanceAccessRecordsTask() - failed.", tr);
        }

        if(attendanceModel!= null && attendanceModel.getStatus().equals(Constants.STATUS_SUCCESS)){
            if(accessModel!= null && accessModel.getStatus().equals(Constants.STATUS_SUCCESS)){
                return attendanceModel;
            }
        }
        return null;

    }

    @Override
    protected void onPostExecute(RecordsReplyModel result) {

        if(callback != null)
            callback.onDeviceAttendanceRecords(result);
    }

}
