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
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceAttendanceRecordsFromDbListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DeviceAttendanceRecordsFromDbTask extends AsyncTask<Object, Integer, RecordsReplyModel> {

    public static final String TAG = "DeviceAttendanceRecordsFromDbTask";

    IDeviceAttendanceRecordsFromDbListener callback;

    @Override
    protected RecordsReplyModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        String deviceToken = (String) params[1];
        callback = (IDeviceAttendanceRecordsFromDbListener) params[2];

        //get data from DB

        ArrayList<UserClockBean> attendanceBean = DatabaseAdapter.getInstance(context).getUserClockByModuleRecordMode(Constants.MODULES_ATTENDANCE , Constants.RECORD_MODE_RECORD);

        RecordsReplyModel model = null;

        LOG.D(TAG,"DeviceAttendanceRecordsFromDbTask attendanceBean = " + attendanceBean);
        if(attendanceBean != null){
            LOG.D(TAG,"DeviceAttendanceRecordsFromDbTask attendanceBean.size() = " + attendanceBean.size());
            for(int i = 0 ; i < attendanceBean.size() ; i++){
                JSONArray jsonRecordsArr = new JSONArray();
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

                    LOG.D(TAG,"DeviceAttendanceRecordsFromDbTask dateString  = " + dateString);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonRecordsArr.put(jsonRecordsObj);

                if(jsonRecordsArr.length() > 0){
                    try {
                        model = ApiResultParser.attendanceRecordsParser(ApiAccessor.deviceAttendanceRecords(context, deviceToken, jsonRecordsArr.toString()));

                        //if success delete this data from db
                        if(model != null){
                            LOG.D(TAG,"DeviceAttendanceRecordsFromDbTask model.getStatus()  = " + model.getStatus());
                            LOG.D(TAG,"DeviceAttendanceRecordsFromDbTask attendanceBean.get(i).getId()  = " + attendanceBean.get(i).getId());
                            LOG.D(TAG,"DeviceAttendanceRecordsFromDbTask attendanceBean.get(i).getClientTime()  = " + attendanceBean.get(i).getClientTime());
                            if(model.getStatus().equals(Constants.STATUS_SUCCESS)){
                                if(attendanceBean.get(i).getId() == null){
                                    //means register from local
                                    DatabaseAdapter.getInstance(context).deleteUserClockByTime(attendanceBean.get(i).getClientTime());
                                }else{
                                    DatabaseAdapter.getInstance(context).deleteUserClockByIdAndTime(attendanceBean.get(i).getId(), attendanceBean.get(i).getClientTime());
                                }

                            }else{
                                LOG.D(TAG, "Error log model.getError().getMessage() = " + model.getError().getMessage() + " Name = " + attendanceBean.get(i).getClientName());
                            }
                        }else{
                            LOG.D(TAG, "Error log model.getError().getMessage() = " + model.getError().getMessage() + " Name = " + attendanceBean.get(i).getClientName());
                        }

                    } catch (Throwable tr) {
                        LOG.E(TAG, "DeviceAttendanceRecordsFromDbTask() - failed.", tr);
                    }
                }else{
                    //no people
                }

            }
        }


        return model;
    }

    @Override
    protected void onPostExecute(RecordsReplyModel result) {

        if(callback != null)
            callback.onDeviceAttendanceRecordsFromDb(result);
    }

}
