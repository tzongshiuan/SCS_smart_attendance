package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/6/21.
 */

public class RenewSecurityCodeModel {
    public static final String KEY_STATUS = "status";
    public static final String KEY_DATA = "data";
    public static final String KEY_ERROR = "error";

    private String status;
    private RenewSecurityCodeDataModel renewSecurityCodeData;
    private ErrorModel error;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public RenewSecurityCodeDataModel getRenewSecurityCodeData() {
        return renewSecurityCodeData;
    }
    public void setRenewSecurityCodeData(RenewSecurityCodeDataModel renewSecurityCodeData) {
        this.renewSecurityCodeData = renewSecurityCodeData;
    }

    public ErrorModel getError() {
        return error;
    }
    public void setError(ErrorModel error) {
        this.error = error;
    }

}
