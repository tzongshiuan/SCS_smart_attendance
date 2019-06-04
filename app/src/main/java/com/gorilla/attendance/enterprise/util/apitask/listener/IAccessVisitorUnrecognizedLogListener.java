package com.gorilla.attendance.enterprise.util.apitask.listener;

import com.gorilla.attendance.enterprise.datamodel.RecordsReplyModel;

/**
 * Created by ggshao on 2017/6/13.
 */

public interface IAccessVisitorUnrecognizedLogListener {
    public void onAccessVisitorUnrecognizedLog(RecordsReplyModel model);
}
