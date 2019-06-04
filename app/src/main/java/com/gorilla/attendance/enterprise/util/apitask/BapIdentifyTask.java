package com.gorilla.attendance.enterprise.util.apitask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import com.gorilla.attendance.enterprise.datamodel.BapIdentifyModel;
import com.gorilla.attendance.enterprise.datamodel.RegisterModel;
import com.gorilla.attendance.enterprise.util.ApiAccessor;
import com.gorilla.attendance.enterprise.util.ApiResultParser;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.apitask.listener.IBapIdentifyListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ggshao on 2018/5/14.
 */

public class BapIdentifyTask extends AsyncTask<Object, Integer, BapIdentifyModel> {

    public static final String TAG = "BapIdentifyTask";

    IBapIdentifyListener callback;

    @Override
    protected BapIdentifyModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        String type = (String) params[1];
        String imageFormat = (String) params[2];
        byte[] imageList = (byte[]) params[3];
        callback = (IBapIdentifyListener) params[4];

        BapIdentifyModel model = null;


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


        try {

            model = ApiResultParser.bapIdentifyParser(ApiAccessor.bapIdentify(context, type, imageListJsonArr.toString()));
        } catch (Throwable tr) {
            LOG.E(TAG, "BapIdentifyTask() - failed.", tr);
        }

        return model;
    }

    @Override
    protected void onPostExecute(BapIdentifyModel result) {

        if(callback != null)
            callback.onBapIdentify(result);
    }

}
