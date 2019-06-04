package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2018/5/14.
 */

public class BapIdentifyModel {
    public static final String KEY_STATUS = "status";
    public static final String KEY_DATA = "data";
    public static final String KEY_ERROR = "error";

    private String status;
    private BapIdentifyDataModel bapIdentifyData;
    private ErrorModel error;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public BapIdentifyDataModel getBapIdentifyData() {
        return bapIdentifyData;
    }
    public void setBapIdentifyData(BapIdentifyDataModel bapIdentifyData) {
        this.bapIdentifyData = bapIdentifyData;
    }

    public ErrorModel getError() {
        return error;
    }
    public void setError(ErrorModel error) {
        this.error = error;
    }
}
