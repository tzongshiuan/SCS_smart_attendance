package com.gorilla.attendance.enterprise.util.apitask;

import android.content.Context;
import android.os.AsyncTask;

import com.gorilla.attendance.enterprise.datamodel.LoginModel;
import com.gorilla.attendance.enterprise.util.ApiAccessor;
import com.gorilla.attendance.enterprise.util.ApiResultParser;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceLoginListener;

/**
 * Created by ggshao on 2017/3/15.
 */

public class DeviceLoginTask extends AsyncTask<Object, Integer, LoginModel> {

    public static final String TAG = "DeviceLoginTask";

    IDeviceLoginListener callback;

    @Override
    protected LoginModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        String deviceToken = (String) params[1];
        String deviceType = (String) params[2];
        String deviceIp = (String) params[3];
        callback = (IDeviceLoginListener) params[4];

        LoginModel model = null;
        try {

            model = ApiResultParser.loginParser(ApiAccessor.deviceLogin(context, deviceToken, deviceType, deviceIp));
        } catch (Throwable tr) {
            LOG.E(TAG, "DeviceLoginTask() - failed.", tr);
        }

        return model;
    }

    @Override
    protected void onPostExecute(LoginModel result) {

        if(callback != null)
            callback.onDeviceLogin(result);
    }

}
