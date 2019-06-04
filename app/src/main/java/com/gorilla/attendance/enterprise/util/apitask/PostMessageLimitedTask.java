package com.gorilla.attendance.enterprise.util.apitask;

import android.content.Context;
import android.os.AsyncTask;

import com.gorilla.attendance.enterprise.datamodel.PostMessageLimitedModel;
import com.gorilla.attendance.enterprise.util.ApiAccessor;
import com.gorilla.attendance.enterprise.util.ApiResultParser;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.apitask.listener.IPostMessageLimitedListener;

/**
 * Created by ggshao on 2017/8/9.
 */

public class PostMessageLimitedTask extends AsyncTask<Object, Integer, PostMessageLimitedModel> {

    public static final String TAG = "PostMessageLimitedTask";

    IPostMessageLimitedListener callback;

    @Override
    protected PostMessageLimitedModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        String account = (String) params[1];
        String apiKey = (String) params[2];
        String teamSn = (String) params[3];
        String contentType = (String) params[4];
        String textContent = (String) params[5];
        String mediaContent = (String) params[6];
        String fileShowName = (String) params[7];
        String subject = (String) params[8];
        String accountList = (String) params[9];
        callback = (IPostMessageLimitedListener) params[10];

        PostMessageLimitedModel model = null;

        try {
            model = ApiResultParser.postMessageLimitedParser(ApiAccessor.postMessageLimited(context, account, apiKey, teamSn, contentType, textContent, mediaContent, fileShowName, subject, accountList));
        } catch (Throwable tr) {
            LOG.E(TAG, "PostMessageLimitedTask() - failed.", tr);
        }

        return model;
    }

    @Override
    protected void onPostExecute(PostMessageLimitedModel result) {

        if(callback != null)
            callback.onPostMessageLimited(result);
    }

}
