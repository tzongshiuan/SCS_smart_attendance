package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/2/12.
 */

public class RecordsReplyModel {
    public static final String KEY_STATUS = "status";
    public static final String KEY_ERROR = "error";

    private String status;
    private ErrorModel error;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public ErrorModel getError() {
        return error;
    }
    public void setError(ErrorModel error) {
        this.error = error;
    }

}
