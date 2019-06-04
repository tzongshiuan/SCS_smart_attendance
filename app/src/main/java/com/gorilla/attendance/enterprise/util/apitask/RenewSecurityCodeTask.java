package com.gorilla.attendance.enterprise.util.apitask;

import android.content.Context;
import android.os.AsyncTask;

import com.gorilla.attendance.enterprise.datamodel.RenewSecurityCodeModel;
import com.gorilla.attendance.enterprise.util.ApiAccessor;
import com.gorilla.attendance.enterprise.util.ApiResultParser;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.apitask.listener.IRenewSecurityCodeListener;

/**
 * Created by ggshao on 2017/6/21.
 */

public class RenewSecurityCodeTask extends AsyncTask<Object, Integer, RenewSecurityCodeModel> {

    public static final String TAG = "RenewSecurityCodeTask";

    IRenewSecurityCodeListener callback;

    @Override
    protected RenewSecurityCodeModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        String deviceToken = (String) params[1];
        String type = (String) params[2];
        String securityCode = (String) params[3];
        callback = (IRenewSecurityCodeListener) params[4];

        RenewSecurityCodeModel model = null;

        try {
            model = ApiResultParser.renewSecurityCodeParser(ApiAccessor.renewSecurityCode(context, deviceToken, type, securityCode));
        } catch (Throwable tr) {
            LOG.E(TAG, "RenewSecurityCodeModel() - failed.", tr);
        }

        return model;
    }

    @Override
    protected void onPostExecute(RenewSecurityCodeModel result) {

        if(callback != null)
            callback.onRenewSecurityCode(result);
    }

}
