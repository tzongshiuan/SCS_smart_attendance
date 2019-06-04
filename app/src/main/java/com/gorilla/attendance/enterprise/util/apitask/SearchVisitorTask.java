package com.gorilla.attendance.enterprise.util.apitask;

import android.content.Context;
import android.os.AsyncTask;

import com.gorilla.attendance.enterprise.datamodel.SearchUserModel;
import com.gorilla.attendance.enterprise.util.ApiAccessor;
import com.gorilla.attendance.enterprise.util.ApiResultParser;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.apitask.listener.ISearchVisitorListener;

/**
 * Created by ggshao on 2017/10/11.
 */

public class SearchVisitorTask extends AsyncTask<Object, Integer, SearchUserModel> {

    public static final String TAG = "SearchVisitorTask";

    ISearchVisitorListener callback;

    @Override
    protected SearchUserModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        String deviceToken = (String) params[1];
        String mobileNo = (String) params[2];
        callback = (ISearchVisitorListener) params[3];

        SearchUserModel model = null;

        try {
            model = ApiResultParser.searchVisitorParser(ApiAccessor.searchVisitor(context, deviceToken, mobileNo));
        } catch (Throwable tr) {
            LOG.E(TAG, "SearchUserTask() - failed.", tr);
        }

        return model;
    }

    @Override
    protected void onPostExecute(SearchUserModel result) {

        if(callback != null)
            callback.onSearchVisitor(result);
    }

}
