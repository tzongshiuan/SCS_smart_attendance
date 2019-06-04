package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/2/9.
 */

public class ErrorModel {
    public static final String KEY_CODE = "code";
    public static final String KEY_MESSAGE = "message";

    private String code;
    private String message;

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }


}
