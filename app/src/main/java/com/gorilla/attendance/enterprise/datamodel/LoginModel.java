package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/2/9.
 */

public class LoginModel {
    public static final String KEY_STATUS = "status";
    public static final String KEY_DATA = "data";
    public static final String KEY_ERROR = "error";


    private String status;
    private LoginDataModel loginData;

    private ErrorModel error;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public LoginDataModel getLoginData() {
        return loginData;
    }
    public void setLoginData(LoginDataModel loginData) {
        this.loginData = loginData;
    }

    public ErrorModel getError() {
        return error;
    }
    public void setError(ErrorModel error) {
        this.error = error;
    }




}
