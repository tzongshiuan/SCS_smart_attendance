package com.gorilla.attendance.enterprise.util.apitask;

import android.content.Context;
import android.os.AsyncTask;

import com.gorilla.attendance.enterprise.database.DatabaseAdapter;
import com.gorilla.attendance.enterprise.database.bean.UserClockBean;
import com.gorilla.attendance.enterprise.datamodel.ErrorLogsModel;
import com.gorilla.attendance.enterprise.datamodel.RecordsReplyModel;
import com.gorilla.attendance.enterprise.util.ApiAccessor;
import com.gorilla.attendance.enterprise.util.ApiResultParser;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.apitask.listener.IVisitorAttendanceUnrecognizedLogFromDbListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class VisitorsAttendanceUnrecognizedLogFromDbTask extends AsyncTask<Object, Integer, RecordsReplyModel> {

    public static final String TAG = "VisitorsAttendanceUnrecognizedLogFromDbTask";

    IVisitorAttendanceUnrecognizedLogFromDbListener callback;

    @Override
    protected RecordsReplyModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        String deviceToken = (String) params[1];
        callback = (IVisitorAttendanceUnrecognizedLogFromDbListener) params[2];

        RecordsReplyModel model = null;
        //get data from DB

        ArrayList<UserClockBean> attendanceBean = DatabaseAdapter.getInstance(context).getUserClockByModuleRecordMode(Constants.MODULES_VISITORS, Constants.RECORD_MODE_UNRECOGNIZED);

        LOG.D(TAG, "VisitorsAttendanceUnrecognizedLogFromDbTask attendanceBean = " + attendanceBean);
        if (attendanceBean != null) {
            for (int i = 0; i < attendanceBean.size(); i++) {

                JSONArray jsonErrorLogArr = new JSONArray();
                JSONObject jsonErrorLogObj = new JSONObject();
                try {

                    jsonErrorLogObj.put(ErrorLogsModel.KEY_SERIAL, attendanceBean.get(i).getSerial());

                    if (attendanceBean.get(i).getSecurityCode() == null) {
                        jsonErrorLogObj.put(ErrorLogsModel.KEY_SECURITY_CODE, "");
                    } else {
                        jsonErrorLogObj.put(ErrorLogsModel.KEY_SECURITY_CODE, attendanceBean.get(i).getSecurityCode());
                    }


                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

                    String dateString = sdf.format(new Date(attendanceBean.get(i).getClientTime()));
                    jsonErrorLogObj.put(ErrorLogsModel.KEY_DEVICE_TIME, dateString);

//                        if(attendanceBean.get(i) != null && attendanceBean.get(i).getPngInfo1() != null){
//                            jsonErrorLogObj.put(ErrorLogsModel.KEY_FACE_IMG, Base64.encodeToString(attendanceBean.get(i).getPngInfo1(),Base64.DEFAULT));
//                        }else{
//                            jsonErrorLogObj.put(ErrorLogsModel.KEY_FACE_IMG, "");
//                        }

                    jsonErrorLogObj.put(ErrorLogsModel.KEY_FACE_IMG, "");

                    jsonErrorLogObj.put(ErrorLogsModel.KEY_LIVENESS, attendanceBean.get(i).getLiveness());
                    jsonErrorLogObj.put(ErrorLogsModel.KEY_MODE, attendanceBean.get(i).getMode());
                    if (attendanceBean.get(i).getRfid() == null) {
                        jsonErrorLogObj.put(ErrorLogsModel.KEY_RFID, "");
                    } else {
                        jsonErrorLogObj.put(ErrorLogsModel.KEY_RFID, attendanceBean.get(i).getRfid());
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonErrorLogArr.put(jsonErrorLogObj);

                LOG.D(TAG, "VisitorsAttendanceUnrecognizedLogFromDbTask jsonErrorLogArr.toString() = " + jsonErrorLogArr.toString());


                if (jsonErrorLogArr.length() > 0) {
                    try {
                        model = ApiResultParser.visitorsUnrecognizedLogParser(ApiAccessor.visitorsUnrecognizedLog(context, deviceToken, jsonErrorLogArr.toString()));

                        LOG.D(TAG, "VisitorsAttendanceUnrecognizedLogFromDbTask attendanceBean.get(i).getIsVisitorOpenDoor()  = "
                                + attendanceBean.get(i).getIsVisitorOpenDoor());
                        if (attendanceBean.get(i).getIsVisitorOpenDoor() == false) {

                        } else {
                            model = ApiResultParser.accessVisitorUnrecognizedLogParser(ApiAccessor.accessVisitorUnrecognizedLog(context, deviceToken, jsonErrorLogArr.toString()));
                        }

                        if(model != null){
                            LOG.D(TAG,"VisitorsAttendanceUnrecognizedLogFromDbTask model.getStatus()  = " + model.getStatus());
                            LOG.D(TAG,"VisitorsAttendanceUnrecognizedLogFromDbTask attendanceBean.get(i).getId()  = " + attendanceBean.get(i).getId());
                            LOG.D(TAG,"VisitorsAttendanceUnrecognizedLogFromDbTask attendanceBean.get(i).getSecurityCode()  = " + attendanceBean.get(i).getSecurityCode());
                            LOG.D(TAG,"VisitorsAttendanceUnrecognizedLogFromDbTask attendanceBean.get(i).getClientTime()  = " + attendanceBean.get(i).getClientTime());
                            if(model.getStatus().equals(Constants.STATUS_SUCCESS)){


                                if(attendanceBean.get(i).getSecurityCode() == null){
                                    DatabaseAdapter.getInstance(context).deleteUserClockByRfidAndTime(attendanceBean.get(i).getRfid(), attendanceBean.get(i).getClientTime());
                                }

                                if(attendanceBean.get(i).getRfid() == null){
                                    DatabaseAdapter.getInstance(context).deleteUserClockBySecurityCodeAndTime(attendanceBean.get(i).getSecurityCode(), attendanceBean.get(i).getClientTime());
                                }


//                                DatabaseAdapter.getInstance(context).deleteUserClockBySecurityCodeAndTime(attendanceBean.get(i).getId(), attendanceBean.get(i).getClientTime());
                            }else{
                                LOG.D(TAG, "Error log model.getError().getMessage() = " + model.getError().getMessage() + " Name = " + attendanceBean.get(i).getClientName());
                            }
                        }else{
                            LOG.D(TAG, "Error log model.getError().getMessage() = " + model.getError().getMessage() + " Name = " + attendanceBean.get(i).getClientName());
                        }


                    } catch (Throwable tr) {
                        LOG.E(TAG, "VisitorsAttendanceUnrecognizedLogFromDbTask() - failed.", tr);
                    }
                } else {

                }
            }
        }


        return model;
    }

    @Override
    protected void onPostExecute(RecordsReplyModel result) {

        if (callback != null)
            callback.onVisitorAttendanceUnrecognizedLogFromDb(result);
    }

}