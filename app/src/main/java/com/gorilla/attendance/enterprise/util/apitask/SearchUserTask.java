package com.gorilla.attendance.enterprise.util.apitask;

import android.content.Context;
import android.os.AsyncTask;

import com.gorilla.attendance.enterprise.datamodel.SearchUserModel;
import com.gorilla.attendance.enterprise.util.ApiAccessor;
import com.gorilla.attendance.enterprise.util.ApiResultParser;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.apitask.listener.ISearchUserListener;

/**
 * Created by ggshao on 2017/5/4.
 */

public class SearchUserTask extends AsyncTask<Object, Integer, SearchUserModel> {

    public static final String TAG = "SearchUserTask";

    ISearchUserListener callback;

    @Override
    protected SearchUserModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        String deviceToken = (String) params[1];
        String type = (String) params[2];
        String securityCode = (String) params[3];
        String rfid = (String) params[4];
        callback = (ISearchUserListener) params[5];

        SearchUserModel model = null;

        try {
            model = ApiResultParser.searchUserParser(ApiAccessor.searchUser(context, deviceToken, type, securityCode, rfid));
        } catch (Throwable tr) {
            LOG.E(TAG, "SearchUserTask() - failed.", tr);
        }

        return model;
    }

    @Override
    protected void onPostExecute(SearchUserModel result) {

        if(callback != null)
            callback.onSearchUser(result);
    }

}
