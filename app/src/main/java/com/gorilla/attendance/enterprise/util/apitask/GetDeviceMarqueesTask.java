package com.gorilla.attendance.enterprise.util.apitask;

import android.content.Context;
import android.os.AsyncTask;

import com.gorilla.attendance.enterprise.datamodel.GetMarqueesModel;
import com.gorilla.attendance.enterprise.util.ApiAccessor;
import com.gorilla.attendance.enterprise.util.ApiResultParser;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.apitask.listener.IGetDeviceMarqueesListener;

/**
 * Created by ggshao on 2017/3/20.
 */

public class GetDeviceMarqueesTask extends AsyncTask<Object, Integer, GetMarqueesModel> {

    public static final String TAG = "GetDeviceMarqueesTask";

    IGetDeviceMarqueesListener callback;

    @Override
    protected GetMarqueesModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        String deviceToken = (String) params[1];
        callback = (IGetDeviceMarqueesListener) params[2];

        GetMarqueesModel model = null;

        try {

            model = ApiResultParser.getMarqueesParser(ApiAccessor.getDeviceMarquees(context, deviceToken));
        } catch (Throwable tr) {
            LOG.E(TAG, "GetEmployeeTask() - failed.", tr);
        }

        return model;
    }

    @Override
    protected void onPostExecute(GetMarqueesModel result) {

        if(callback != null)
            callback.onGetDeviceMarquees(result);
    }

}
