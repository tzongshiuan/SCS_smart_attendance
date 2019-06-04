package com.gorilla.attendance.enterprise.util.apitask.listener;

import com.gorilla.attendance.enterprise.datamodel.RecordsReplyModel;

public interface IAccessUnrecognizedLogFromDbListener {
    void onAccessUnrecognizedLogFromDb(RecordsReplyModel model);
}
