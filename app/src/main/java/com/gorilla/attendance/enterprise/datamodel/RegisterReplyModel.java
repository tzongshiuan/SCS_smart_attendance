package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/5/4.
 */

public class RegisterReplyModel {
    public static final String KEY_STATUS = "status";
    public static final String KEY_DATA = "data";
    public static final String KEY_ERROR = "error";

    private String status;
    private RegisterReplyDataModel registerReplyData;
    private ErrorModel error;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public RegisterReplyDataModel getRegisterData() {
        return registerReplyData;
    }
    public void setRegisterData(RegisterReplyDataModel registerReplyData) {
        this.registerReplyData = registerReplyData;
    }

    public ErrorModel getError() {
        return error;
    }
    public void setError(ErrorModel error) {
        this.error = error;
    }
}
