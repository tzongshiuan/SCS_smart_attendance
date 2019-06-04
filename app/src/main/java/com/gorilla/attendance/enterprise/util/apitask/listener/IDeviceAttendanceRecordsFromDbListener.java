package com.gorilla.attendance.enterprise.util.apitask.listener;

import com.gorilla.attendance.enterprise.datamodel.RecordsReplyModel;

public interface IDeviceAttendanceRecordsFromDbListener {
    public void onDeviceAttendanceRecordsFromDb(RecordsReplyModel model);
}
