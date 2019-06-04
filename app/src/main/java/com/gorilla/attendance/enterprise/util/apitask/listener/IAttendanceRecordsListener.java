package com.gorilla.attendance.enterprise.util.apitask.listener;

import com.gorilla.attendance.enterprise.datamodel.RecordsReplyModel;

/**
 * Created by ggshao on 2017/2/12.
 */

public interface IAttendanceRecordsListener {
        public void onAttendanceRecords(RecordsReplyModel model);
}
