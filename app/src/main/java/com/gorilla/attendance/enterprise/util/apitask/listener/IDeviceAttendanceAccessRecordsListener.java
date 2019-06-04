package com.gorilla.attendance.enterprise.util.apitask.listener;

import com.gorilla.attendance.enterprise.datamodel.RecordsReplyModel;

/**
 * Created by ggshao on 2017/3/28.
 */

public interface IDeviceAttendanceAccessRecordsListener {
    public void onDeviceAttendanceAccessRecords(RecordsReplyModel model);
}
