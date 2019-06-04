package com.gorilla.attendance.enterprise.util.apitask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import com.gorilla.attendance.enterprise.datamodel.RegisterModel;
import com.gorilla.attendance.enterprise.datamodel.RegisterReplyModel;
import com.gorilla.attendance.enterprise.util.ApiAccessor;
import com.gorilla.attendance.enterprise.util.ApiResultParser;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.apitask.listener.IRegisterEmployeeV2Listener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ggshao on 2018/1/25.
 */

public class RegisterEmployeeV2RfidTask extends AsyncTask<Object, Integer, RegisterReplyModel> {

    public static final String TAG = "RegisterEmployeeV2RfidTask";

    IRegisterEmployeeV2Listener callback;

    @Override
    protected RegisterReplyModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        String deviceToken = (String) params[1];
        String employeeId = (String) params[2];
        String name = (String) params[3];
        String email = (String) params[4];
        String password = (String) params[5];
        String securityCode = (String) params[6];
        String createTime = (String) params[7];
        String imageFormat = (String) params[8];
        byte[] imageList = (byte[]) params[9];
        String rfid = (String) params[10];
        callback = (IRegisterEmployeeV2Listener) params[11];

        JSONArray imageListJsonArr = new JSONArray();
        JSONObject imageListJsonObj = new JSONObject();
        try {


            imageListJsonObj.put(RegisterModel.KEY_IMAGE_FORMAT, imageFormat);
            imageListJsonObj.put(RegisterModel.KEY_DATA_IN_BASE_64, Base64.encodeToString(imageList,Base64.DEFAULT));
//            imageListJsonObj.
//            if(EnterpriseUtils.mFacePngList != null && EnterpriseUtils.mFacePngList.size() > 0){
//                imageListJsonObj.put(RecordsModel.KEY_FACE_IMG, Base64.encodeToString(EnterpriseUtils.mFacePngList.get(0),Base64.DEFAULT));
//            }else{
//                imageListJsonObj.put(RecordsModel.KEY_FACE_IMG, "");
//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        imageListJsonArr.put(imageListJsonObj);


        RegisterReplyModel model = null;

        try {
            model = ApiResultParser.registerEmployeeV2Parser(ApiAccessor.registerEmployeeV2Rfid(context, deviceToken, employeeId, name, email,
                    password, securityCode, createTime, imageFormat, imageListJsonArr.toString(), rfid));
        } catch (Throwable tr) {
            LOG.E(TAG, "RegisterEmployeeTask() - failed.", tr);
        }

        return model;
    }

    @Override
    protected void onPostExecute(RegisterReplyModel result) {

        if(callback != null)
            callback.onRegisterEmployee(result);
    }

}
