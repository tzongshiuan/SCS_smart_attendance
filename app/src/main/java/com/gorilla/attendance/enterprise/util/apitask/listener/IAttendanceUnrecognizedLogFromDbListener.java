package com.gorilla.attendance.enterprise.util.apitask.listener;

import com.gorilla.attendance.enterprise.datamodel.RecordsReplyModel;

public interface IAttendanceUnrecognizedLogFromDbListener {
    void onAttendanceUnrecognizedLogFromDb(RecordsReplyModel model);
}
