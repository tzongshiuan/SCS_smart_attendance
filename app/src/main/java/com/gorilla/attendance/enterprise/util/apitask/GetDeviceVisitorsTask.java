package com.gorilla.attendance.enterprise.util.apitask;

import android.content.Context;
import android.os.AsyncTask;

import com.gorilla.attendance.enterprise.datamodel.GetVisitorModel;
import com.gorilla.attendance.enterprise.util.ApiAccessor;
import com.gorilla.attendance.enterprise.util.ApiResultParser;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.apitask.listener.IGetDeviceVisitorsListener;

/**
 * Created by ggshao on 2017/3/20.
 */

public class GetDeviceVisitorsTask extends AsyncTask<Object, Integer, GetVisitorModel> {

    public static final String TAG = "GetDeviceVisitorsTask";

    IGetDeviceVisitorsListener callback;

    @Override
    protected GetVisitorModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        String deviceToken = (String) params[1];
        callback = (IGetDeviceVisitorsListener) params[2];

        GetVisitorModel model = null;

        try {
            model = ApiResultParser.getVisitorParser(ApiAccessor.getDeviceVisitors(context, deviceToken));
        } catch (Throwable tr) {
            LOG.E(TAG, "GetDeviceVisitorsTask() - failed.", tr);
        }

        return model;
    }

    @Override
    protected void onPostExecute(GetVisitorModel result) {

        if(callback != null)
            callback.onGetDeviceVisitors(result);
    }

}
