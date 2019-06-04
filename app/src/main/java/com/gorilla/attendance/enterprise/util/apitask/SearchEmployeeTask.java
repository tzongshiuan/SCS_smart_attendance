package com.gorilla.attendance.enterprise.util.apitask;

import android.content.Context;
import android.os.AsyncTask;

import com.gorilla.attendance.enterprise.datamodel.SearchUserModel;
import com.gorilla.attendance.enterprise.util.ApiAccessor;
import com.gorilla.attendance.enterprise.util.ApiResultParser;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.apitask.listener.ISearchEmployeeListener;

/**
 * Created by ggshao on 2017/10/2.
 */

public class SearchEmployeeTask extends AsyncTask<Object, Integer, SearchUserModel> {

    public static final String TAG = "SearchEmployeeTask";

    ISearchEmployeeListener callback;

    @Override
    protected SearchUserModel doInBackground(Object... params) {
        Context context = (Context) params[0];
        String deviceToken = (String) params[1];
        String employeeId = (String) params[2];
        callback = (ISearchEmployeeListener) params[3];

        SearchUserModel model = null;

        try {
            model = ApiResultParser.searchEmployeeParser(ApiAccessor.searchEmployee(context, deviceToken, employeeId));
        } catch (Throwable tr) {
            LOG.E(TAG, "SearchUserTask() - failed.", tr);
        }

        return model;
    }

    @Override
    protected void onPostExecute(SearchUserModel result) {

        if(callback != null)
            callback.onSearchEmployee(result);
    }

}
