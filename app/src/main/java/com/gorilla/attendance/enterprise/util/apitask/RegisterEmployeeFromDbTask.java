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
import com.gorilla.attendance.enterprise.util.apitask.listener.IRegisterEmployeeFromDbListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ggshao on 2017/5/12.
 */

public class RegisterEmployeeFromDbTask extends AsyncTask<Object, Integer, RegisterReplyModel> {

    public static final String TAG = "RegisterEmployeeFromDbTask";

    IRegisterEmployeeFromDbListener callback;

    @Override
    protected RegisterReplyModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        callback = (IRegisterEmployeeFromDbListener) params[1];

        RegisterReplyModel model = null;

        ArrayList<RegisterBean> employeeRegisterData =  DatabaseAdapter.getInstance(context).getAllEmployeeRegisterArrayList();

        if(employeeRegisterData == null){
            return null;
        }

        LOG.D(TAG,"employeeRegisterData.size() = " + employeeRegisterData.size());

        for(int i = 0 ; i < employeeRegisterData.size() ; i++){

            LOG.D(TAG,"employeeRegisterData.get(i).getDeviceToken() = " + employeeRegisterData.get(i).getDeviceToken());
            LOG.D(TAG,"employeeRegisterData.get(i).getEmployeeId() = " + employeeRegisterData.get(i).getEmployeeId());
            LOG.D(TAG,"employeeRegisterData.get(i).getName() = " + employeeRegisterData.get(i).getName());
            LOG.D(TAG,"employeeRegisterData.get(i).getEmail() = " + employeeRegisterData.get(i).getEmail());
            LOG.D(TAG,"employeeRegisterData.get(i).getPassword() = " + employeeRegisterData.get(i).getPassword());
            LOG.D(TAG,"employeeRegisterData.get(i).getCreateTime() = " + employeeRegisterData.get(i).getCreateTime());
            LOG.D(TAG,"employeeRegisterData.get(i).getFormat() = " + employeeRegisterData.get(i).getFormat());
            LOG.D(TAG,"employeeRegisterData.get(i).getDataInBase64() = " + employeeRegisterData.get(i).getDataInBase64());

            if(employeeRegisterData.get(i).getDataInBase64() == null){
                continue;
            }

            JSONArray imageListJsonArr = new JSONArray();
            JSONObject imageListJsonObj = new JSONObject();
            try {


                imageListJsonObj.put(RegisterModel.KEY_IMAGE_FORMAT, employeeRegisterData.get(i).getFormat());
                imageListJsonObj.put(RegisterModel.KEY_DATA_IN_BASE_64, Base64.encodeToString(employeeRegisterData.get(i).getDataInBase64(),Base64.DEFAULT));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            imageListJsonArr.put(imageListJsonObj);


            try {
                model = ApiResultParser.registerEmployeeParser(ApiAccessor.registerEmployee(context, employeeRegisterData.get(i).getDeviceToken(),
                        employeeRegisterData.get(i).getEmployeeId(), employeeRegisterData.get(i).getName(),
                        employeeRegisterData.get(i).getEmail(), employeeRegisterData.get(i).getPassword(),
                        employeeRegisterData.get(i).getCreateTime(), employeeRegisterData.get(i).getFormat(),
                        imageListJsonArr.toString()));
            } catch (Throwable tr) {
                LOG.E(TAG, "RegisterEmployeeTask() - failed.", tr);
            }


            LOG.D(TAG,"model = " + model);
            if(model != null){
                if(model.getStatus().equals(Constants.STATUS_SUCCESS)){
                    LOG.D(TAG,"employeeRegisterData.get(i).getEmployeeId() = " + employeeRegisterData.get(i).getEmployeeId());

                    DatabaseAdapter.getInstance(context).deleteEmployeeRegisterByEmployeeId(employeeRegisterData.get(i).getEmployeeId());

                }
            }else{

            }




        }


        return model;
    }

    @Override
    protected void onPostExecute(RegisterReplyModel result) {

        if(callback != null)
            callback.onRegisterEmployeeFromDb(result);
    }

}
