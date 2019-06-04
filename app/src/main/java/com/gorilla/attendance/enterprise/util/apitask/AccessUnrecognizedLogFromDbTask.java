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
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.apitask.listener.IAccessUnrecognizedLogFromDbListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AccessUnrecognizedLogFromDbTask extends AsyncTask<Object, Integer, RecordsReplyModel> {

    public static final String TAG = "AccessUnrecognizedLogFromDbTask";

    IAccessUnrecognizedLogFromDbListener callback;

    @Override
    protected RecordsReplyModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        String deviceToken = (String) params[1];
        callback = (IAccessUnrecognizedLogFromDbListener) params[2];

        RecordsReplyModel model = null;

        //get data from DB
        ArrayList<UserClockBean> attendanceBean = DatabaseAdapter.getInstance(context).getUserClockByModuleRecordMode(Constants.MODULES_ACCESS, Constants.RECORD_MODE_UNRECOGNIZED);

        LOG.D(TAG,"AccessUnrecognizedLogFromDbTask attendanceBean = " + attendanceBean);
        if(attendanceBean != null){
            for(int i = 0 ; i < attendanceBean.size() ; i++){
                JSONArray jsonErrorLogArr = new JSONArray();
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

                LOG.D(TAG,"AccessUnrecognizedLogFromDbTask jsonErrorLogArr.toString() = " + jsonErrorLogArr.toString());

                if(jsonErrorLogArr.length() > 0){
                    try {
                        model = ApiResultParser.accessUnrecognizedLogParser(ApiAccessor.accessUnrecognizedLog(context, deviceToken, jsonErrorLogArr.toString()));
                        //if success delete this data from db
                        if(model != null){
                            LOG.D(TAG,"AccessUnrecognizedLogFromDbTask model.getStatus()  = " + model.getStatus());
                            LOG.D(TAG,"AccessUnrecognizedLogFromDbTask attendanceBean.get(i).getId()  = " + attendanceBean.get(i).getId());
                            LOG.D(TAG,"AccessUnrecognizedLogFromDbTask attendanceBean.get(i).getClientTime()  = " + attendanceBean.get(i).getClientTime());
//                            if(model.getStatus().equals(Constants.STATUS_SUCCESS)){
//                                DatabaseAdapter.getInstance(context).deleteUserClockByIdAndTime(attendanceBean.get(i).getId(), attendanceBean.get(i).getClientTime());
//                            }else{
//                                LOG.D(TAG, "Error log model.getError().getMessage() = " + model.getError().getMessage() + " Name = " + attendanceBean.get(i).getClientName());
//                            }

                            if(model.getStatus().equals(Constants.STATUS_SUCCESS)){

                                if(attendanceBean.get(i).getSecurityCode() == null && attendanceBean.get(i).getRfid() == null){
                                    DatabaseAdapter.getInstance(context).deleteUserClockByTime(attendanceBean.get(i).getClientTime());
                                }else if(attendanceBean.get(i).getSecurityCode() == null){
                                    DatabaseAdapter.getInstance(context).deleteUserClockByRfidAndTime(attendanceBean.get(i).getRfid(), attendanceBean.get(i).getClientTime());
                                }else if(attendanceBean.get(i).getRfid() == null){
                                    DatabaseAdapter.getInstance(context).deleteUserClockBySecurityCodeAndTime(attendanceBean.get(i).getSecurityCode(), attendanceBean.get(i).getClientTime());
                                }



                            }else{
                                LOG.D(TAG, "Error log model.getError().getMessage() = " + model.getError().getMessage() + " Name = " + attendanceBean.get(i).getClientName());
                            }


                        }else{
                            LOG.D(TAG, "Error log model.getError().getMessage() = " + model.getError().getMessage() + " Name = " + attendanceBean.get(i).getClientName());
                        }


                    } catch (Throwable tr) {
                        LOG.E(TAG, "AccessUnrecognizedLogFromDbTask() - failed.", tr);
                    }
                }else{

                }
            }
        }



        return model;
    }

    @Override
    protected void onPostExecute(RecordsReplyModel result) {

        if(callback != null)
            callback.onAccessUnrecognizedLogFromDb(result);
    }

}
