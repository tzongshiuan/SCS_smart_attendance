package com.gorilla.attendance.enterprise.util.apitask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import com.gorilla.attendance.enterprise.datamodel.RegisterModel;
import com.gorilla.attendance.enterprise.datamodel.RegisterReplyModel;
import com.gorilla.attendance.enterprise.util.ApiAccessor;
import com.gorilla.attendance.enterprise.util.ApiResultParser;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.apitask.listener.IRegisterVisitorV2Listener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ggshao on 2018/1/30.
 */

public class RegisterVisitorV2RfidTask extends AsyncTask<Object, Integer, RegisterReplyModel> {

    public static final String TAG = "RegisterVisitorV2Task";

    IRegisterVisitorV2Listener callback;

    @Override
    protected RegisterReplyModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        String deviceToken = (String) params[1];
        String mobileNo = (String) params[2];
        String name = (String) params[3];
        String company = (String) params[4];
        String title = (String) params[5];
        String createTime = (String) params[6];
        String imageFormat = (String) params[7];
        byte[] imageList = (byte[]) params[8];
        String email = (String) params[9];
        String securityCode = (String) params[10];
        String rfid = (String) params[11];
        callback = (IRegisterVisitorV2Listener) params[12];

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

//            model = ApiResultParser.registerVisitorParser(ApiAccessor.registerVisitor(context, deviceToken, mobileNo, name, department,
//                    title, createTime, imageFormat, imageListJsonArr.toString()));

            model = ApiResultParser.registerVisitorV2Parser(ApiAccessor.registerVisitorV2Rfid(context, deviceToken, mobileNo, name, company,
                    title, createTime, imageFormat, imageListJsonArr.toString(), email, securityCode, rfid));

        } catch (Throwable tr) {
            LOG.E(TAG, "RegisterVisitorEmailTask() - failed.", tr);
        }

        return model;
    }

    @Override
    protected void onPostExecute(RegisterReplyModel result) {

        if(callback != null)
            callback.onRegisterVisitorV2(result);
    }

}
