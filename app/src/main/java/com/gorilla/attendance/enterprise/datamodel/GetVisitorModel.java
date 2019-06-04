package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/2/14.
 */

public class GetVisitorModel {
    public static final String KEY_STATUS = "status";
    public static final String KEY_DATA = "data";

    private String status;
    private VisitorDataModel visitorData;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public VisitorDataModel getVisitorData() {
        return visitorData;
    }
    public void setVisitorData(VisitorDataModel visitorData) {
        this.visitorData = visitorData;
    }
}
