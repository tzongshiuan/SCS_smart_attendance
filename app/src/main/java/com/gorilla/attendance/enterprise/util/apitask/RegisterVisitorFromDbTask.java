package com.gorilla.attendance.enterprise.util.apitask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import com.gorilla.attendance.enterprise.database.DatabaseAdapter;
import com.gorilla.attendance.enterprise.database.bean.RegisterBean;
import com.gorilla.attendance.enterprise.datamodel.RegisterModel;
import com.gorilla.attendance.enterprise.datamodel.RegisterReplyModel;
import com.gorilla.attendance.enterprise.util.ApiAccessor;
import com.gorilla.attendance.enterprise.util.ApiResultParser;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.apitask.listener.IRegisterVisitorFromDbListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ggshao on 2017/5/15.
 */

public class RegisterVisitorFromDbTask extends AsyncTask<Object, Integer, RegisterReplyModel> {

    public static final String TAG = "RegisterVisitorFromDbTask";

    IRegisterVisitorFromDbListener callback;

    @Override
    protected RegisterReplyModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        callback = (IRegisterVisitorFromDbListener) params[1];

        RegisterReplyModel model = null;

//        ArrayList<RegisterBean> employeeRegisterData =  DatabaseAdapter.getInstance(context).getAllEmployeeRegisterArrayList();
        ArrayList<RegisterBean> visitorRegisterData =  DatabaseAdapter.getInstance(context).getAllVisitorRegisterArrayList();

        if(visitorRegisterData == null){
            return null;
        }

        LOG.D(TAG,"visitorRegisterData.size() = " + visitorRegisterData.size());

        for(int i = 0 ; i < visitorRegisterData.size() ; i++){

            LOG.D(TAG,"visitorRegisterData.get(i).getDeviceToken() = " + visitorRegisterData.get(i).getDeviceToken());
            LOG.D(TAG,"visitorRegisterData.get(i).getEmployeeId() = " + visitorRegisterData.get(i).getEmployeeId());
            LOG.D(TAG,"visitorRegisterData.get(i).getMobileNo() = " + visitorRegisterData.get(i).getMobileNo());
            LOG.D(TAG,"visitorRegisterData.get(i).getName() = " + visitorRegisterData.get(i).getName());
            LOG.D(TAG,"visitorRegisterData.get(i).getEmail() = " + visitorRegisterData.get(i).getEmail());
            LOG.D(TAG,"visitorRegisterData.get(i).getPassword() = " + visitorRegisterData.get(i).getPassword());
            LOG.D(TAG,"visitorRegisterData.get(i).getCreateTime() = " + visitorRegisterData.get(i).getCreateTime());
            LOG.D(TAG,"visitorRegisterData.get(i).getFormat() = " + visitorRegisterData.get(i).getFormat());
            LOG.D(TAG,"visitorRegisterData.get(i).getDataInBase64() = " + visitorRegisterData.get(i).getDataInBase64());

            if(visitorRegisterData.get(i).getDataInBase64() == null){
                continue;
            }

            JSONArray imageListJsonArr = new JSONArray();
            JSONObject imageListJsonObj = new JSONObject();
            try {


                imageListJsonObj.put(RegisterModel.KEY_IMAGE_FORMAT, visitorRegisterData.get(i).getFormat());
                imageListJsonObj.put(RegisterModel.KEY_DATA_IN_BASE_64, Base64.encodeToString(visitorRegisterData.get(i).getDataInBase64(),Base64.DEFAULT));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            imageListJsonArr.put(imageListJsonObj);


            try {

//                model = ApiResultParser.registerVisitorParser(ApiAccessor.registerVisitor(context, visitorRegisterData.get(i).getDeviceToken(),
//                        visitorRegisterData.get(i).getMobileNo(), visitorRegisterData.get(i).getName(),
//                        visitorRegisterData.get(i).getDepartment(), visitorRegisterData.get(i).getTitle(),
//                        visitorRegisterData.get(i).getCreateTime(), visitorRegisterData.get(i).getFormat(), imageListJsonArr.toString()));

                model = ApiResultParser.registerVisitorEmailParser(ApiAccessor.registerVisitorEmail(context, visitorRegisterData.get(i).getDeviceToken(),
                        visitorRegisterData.get(i).getMobileNo(), visitorRegisterData.get(i).getName(),
                        visitorRegisterData.get(i).getDepartment(), visitorRegisterData.get(i).getTitle(),
                        visitorRegisterData.get(i).getCreateTime(), visitorRegisterData.get(i).getFormat(), imageListJsonArr.toString(), visitorRegisterData.get(i).getEmail()));

            } catch (Throwable tr) {
                LOG.E(TAG, "RegisterVisitorFromDbTask() - failed.", tr);
            }


            LOG.D(TAG,"model = " + model);
            if(model != null){
                if(model.getStatus().equals(Constants.STATUS_SUCCESS)){
                    LOG.D(TAG,"visitorRegisterData.get(i).getMobileNo() = " + visitorRegisterData.get(i).getMobileNo());

//                    DatabaseAdapter.getInstance(context).deleteEmployeeRegisterByEmployeeId(employeeRegisterData.get(i).getEmployeeId());
                    DatabaseAdapter.getInstance(context).deleteVisitorRegisterByMobileNo(visitorRegisterData.get(i).getMobileNo());

                }
            }else{

            }




        }


        return model;
    }

    @Override
    protected void onPostExecute(RegisterReplyModel result) {

        if(callback != null)
            callback.onRegisterVisitorFromDb(result);
    }

}
