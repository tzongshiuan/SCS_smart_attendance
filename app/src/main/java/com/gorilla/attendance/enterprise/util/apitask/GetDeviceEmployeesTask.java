package com.gorilla.attendance.enterprise.util.apitask;

import android.content.Context;
import android.os.AsyncTask;

import com.gorilla.attendance.enterprise.datamodel.GetEmployeeModel;
import com.gorilla.attendance.enterprise.util.ApiAccessor;
import com.gorilla.attendance.enterprise.util.ApiResultParser;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.apitask.listener.IGetDeviceEmployeesListener;

/**
 * Created by ggshao on 2017/3/20.
 */

public class GetDeviceEmployeesTask extends AsyncTask<Object, Integer, GetEmployeeModel> {

    public static final String TAG = "GetDeviceEmployeesTask";

    IGetDeviceEmployeesListener callback;

    @Override
    protected GetEmployeeModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        String deviceToken = (String) params[1];
        callback = (IGetDeviceEmployeesListener) params[2];

        GetEmployeeModel model = null;

        try {

            model = ApiResultParser.getEmployeeParser(ApiAccessor.getDeviceEmployees(context, deviceToken));
        } catch (Throwable tr) {
            LOG.E(TAG, "GetDeviceEmployeesTask() - failed.", tr);
        }

        return model;
    }

    @Override
    protected void onPostExecute(GetEmployeeModel result) {

        if(callback != null)
            callback.onGetDeviceEmployees(result);
    }

}
