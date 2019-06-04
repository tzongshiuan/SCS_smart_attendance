package com.gorilla.attendance.enterprise.util.apitask;

import android.content.Context;
import android.os.AsyncTask;

import com.gorilla.attendance.enterprise.datamodel.GetVerifiedIdAndImageModel;
import com.gorilla.attendance.enterprise.util.ApiAccessor;
import com.gorilla.attendance.enterprise.util.ApiResultParser;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.apitask.listener.IGetDeviceVerifiedIdAndImageListener;

/**
 * Created by ggshao on 2017/4/12.
 */

public class GetDeviceVerifiedIdAndImageTask extends AsyncTask<Object, Integer, GetVerifiedIdAndImageModel> {
    public static final String TAG = "GetDeviceVerifiedIdAndImageTask";

    IGetDeviceVerifiedIdAndImageListener callback;

    @Override
    protected GetVerifiedIdAndImageModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        String deviceToken = (String) params[1];
        callback = (IGetDeviceVerifiedIdAndImageListener) params[2];

        GetVerifiedIdAndImageModel model = null;

        //GGGG TEST mIdentify = new Identify(libPath);

//        final String libPath = context.getFilesDir()+"/Bin";
//        LOG.D(TAG,"test NEW11111");
//        LOG.D(TAG,"test NEW11111");
//        Identify identify = new Identify(libPath);
//        LOG.D(TAG,"test NEW22222 identify = " + identify);


        try {

            model = ApiResultParser.getVerifiedIdAndImageDataParser(ApiAccessor.getDeviceVerifiedIdAndImage(context, deviceToken));
        } catch (Throwable tr) {
            LOG.E(TAG, "GetDeviceVerifiedIdAndImageTask() - failed. ", tr);
        }

        return model;

    }


    @Override
    protected void onPostExecute(GetVerifiedIdAndImageModel result) {

        if(callback != null)
            callback.onGetDeviceVerifiedIdAndImage(result);
    }
}
