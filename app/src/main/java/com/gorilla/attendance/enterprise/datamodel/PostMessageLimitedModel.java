package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/8/9.
 */

public class PostMessageLimitedModel {
    public static final String KEY_BATCH_ID = "BatchID";
    public static final String KEY_ERROR_CODE = "ErrorCode";
    public static final String KEY_IS_SUCCESS = "IsSuccess";
    public static final String KEY_DESCRIPTION = "Description";


    private String batchID;
    private int errorCode;
    private boolean isSuccess;
    private String description;


    public String getBatchID() {
        return batchID;
    }
    public void setBatchID(String batchID) {
        this.batchID = batchID;
    }

    public int getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }
    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

}
