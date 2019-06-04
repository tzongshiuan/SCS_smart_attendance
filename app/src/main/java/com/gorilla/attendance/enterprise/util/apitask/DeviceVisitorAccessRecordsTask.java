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
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceVisitorAccessRecordsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ggshao on 2017/6/13.
 */

public class DeviceVisitorAccessRecordsTask extends AsyncTask<Object, Integer, RecordsReplyModel> {

    public static final String TAG = "DeviceVisitorAccessRecordsTask";

    IDeviceVisitorAccessRecordsListener callback;

    @Override
    protected RecordsReplyModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        String clientName = (String) params[1];
        boolean isPassDbData = (boolean) params[2];
        String type = (String) params[3];
        int serialNumber = (int) params[4];
        callback = (IDeviceVisitorAccessRecordsListener) params[5];

        JSONArray jsonRecordsArr = new JSONArray();

        if(isPassDbData == false){
            JSONObject jsonRecordsObj = new JSONObject();
            try {
                jsonRecordsObj.put(RecordsModel.KEY_SERIAL, String.valueOf(serialNumber));
                jsonRecordsObj.put(RecordsModel.KEY_SECURITY_CODE, String.valueOf(ClockUtils.mLoginAccount));
                jsonRecordsObj.put(RecordsModel.KEY_ID, ClockUtils.mLoginUuid);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

                String dateString = sdf.format(new Date(ClockUtils.mLoginTime));
                jsonRecordsObj.put(RecordsModel.KEY_CLIENT_TIME, dateString);
                jsonRecordsObj.put(RecordsModel.KEY_TYPE, type);

                jsonRecordsObj.put(RecordsModel.KEY_FACE_VERIFY, ClockUtils.mFaceVerify);
                if(EnterpriseUtils.mFacePngList != null && EnterpriseUtils.mFacePngList.size() > 0){
                    jsonRecordsObj.put(RecordsModel.KEY_FACE_IMG, Base64.encodeToString(EnterpriseUtils.mFacePngList.get(0),Base64.DEFAULT));
                }else{
                    jsonRecordsObj.put(RecordsModel.KEY_FACE_IMG, "");
                }


                jsonRecordsObj.put(RecordsModel.KEY_LIVENESS, ClockUtils.mLiveness);
                jsonRecordsObj.put(RecordsModel.KEY_MODE, ClockUtils.mMode);



            } catch (JSONException e) {
                e.printStackTrace();
            }

            jsonRecordsArr.put(jsonRecordsObj);
        }else{

//get data from DBf
//            ArrayList<UserClockBean> attendanceBean = DatabaseAdapter.getInstance(context).getUserClockByModuleRecordMode(Constants.MODULES_ACCESS, Constants.RECORD_MODE_RECORD, Constants.MODULES_ATTENDANCE_ACCESS);


            ArrayList<UserClockBean> attendanceBean = DatabaseAdapter.getInstance(context).getUserClockByModuleRecordMode(Constants.MODULES_VISITORS, Constants.RECORD_MODE_RECORD);


            LOG.D(TAG,"DeviceVisitorAccessRecordsTask attendanceBean = " + attendanceBean);
            if(attendanceBean != null){
                LOG.D(TAG,"DeviceVisitorAccessRecordsTask attendanceBean.size() = " + attendanceBean.size());
                for(int i = 0 ; i < attendanceBean.size() ; i++){
                    LOG.D(TAG,"DeviceVisitorAccessRecordsTask attendanceBean.get(i).getIsVisitorOpenDoor() = " + attendanceBean.get(i).getIsVisitorOpenDoor());
                    if(attendanceBean.get(i).getIsVisitorOpenDoor() == false){
                        continue;
                    }
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

                        if(attendanceBean.get(i) != null && attendanceBean.get(i).getPngInfo1() != null){
                            jsonRecordsObj.put(RecordsModel.KEY_FACE_IMG, Base64.encodeToString(attendanceBean.get(i).getPngInfo1(),Base64.DEFAULT));
                        }else{
                            jsonRecordsObj.put(RecordsModel.KEY_FACE_IMG, "");
                        }

                        jsonRecordsObj.put(RecordsModel.KEY_LIVENESS, attendanceBean.get(i).getLiveness());
                        jsonRecordsObj.put(RecordsModel.KEY_MODE, attendanceBean.get(i).getMode());


                        LOG.D(TAG,"DeviceVisitorAccessRecordsTask dateString  = " + dateString);

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
                model = ApiResultParser.deviceVisitorAccessRecordsParser(ApiAccessor.deviceVisitorAccessRecords(context, clientName, jsonRecordsArr.toString()));
//                model = ApiResultParser.accessRecordsParser(ApiAccessor.deviceAccessRecords(context, clientName, jsonRecordsArr.toString()));
            } catch (Throwable tr) {
                LOG.E(TAG, "DeviceAccessRecordsTask() - failed.", tr);
            }
        }else{

        }



        return model;
    }

    @Override
    protected void onPostExecute(RecordsReplyModel result) {

        if(callback != null)
            callback.onDeviceVisitorAccessRecords(result);
    }

}
