package com.gorilla.attendance.enterprise.util.apitask;

import android.content.Context;
import android.os.AsyncTask;

import com.gorilla.attendance.enterprise.datamodel.GetVideoModel;
import com.gorilla.attendance.enterprise.util.ApiAccessor;
import com.gorilla.attendance.enterprise.util.ApiResultParser;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.apitask.listener.IGetDeviceVideosListener;

/**
 * Created by ggshao on 2017/3/20.
 */

public class GetDeviceVideosTask extends AsyncTask<Object, Integer, GetVideoModel> {

    public static final String TAG = "GetDeviceVideosTask";

    IGetDeviceVideosListener callback;

    @Override
    protected GetVideoModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        String deviceToken = (String) params[1];
        boolean isFromWebSocket = (boolean) params[2];
        callback = (IGetDeviceVideosListener) params[3];

        GetVideoModel model = null;

        try {

            model = ApiResultParser.getVideoParser(ApiAccessor.getDeviceVideos(context, deviceToken));
        } catch (Throwable tr) {
            LOG.E(TAG, "GetDeviceVideosTask() - failed.", tr);
        }

        model.setIsFromWebSocket(isFromWebSocket);

        return model;
    }

    @Override
    protected void onPostExecute(GetVideoModel result) {

        if(callback != null)
            callback.onGetDeviceVideos(result);
    }

}
