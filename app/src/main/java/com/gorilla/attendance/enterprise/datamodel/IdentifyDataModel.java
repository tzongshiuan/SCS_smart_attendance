package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/2/12.
 */

public class IdentifyDataModel {
    public static final String KEY_MODEL_ID = "bapModelId";
    public static final String KEY_ID = "id";
    public static final String KEY_EMPLOYEE_ID = "employeeId";
    public static final String KEY_EMPLOYEE_NAME = "employeeName";
    public static final String KEY_CREATED_TIME = "createdTime";
    public static final String KEY_MODEL = "model";

    public final static String IS_ALREADY_FIX = "isAlreadyFix";


    private String bapModelId;
    private String id;
    private String employeeName;
    private String employeeId;
    private String createdTime;
    private String model;

    private boolean mIsAlreadyFix = false;

    public String getBapModelId() {
        return bapModelId;
    }
    public void setBapModelId(String bapModelId) {
        this.bapModelId = bapModelId;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getEmployeeName() {
        return employeeName;
    }
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }

    public boolean getIsAlreadyFix() {
        return mIsAlreadyFix;
    }
    public void setIsAlreadyFix(boolean isAlreadyFix) {
        mIsAlreadyFix = isAlreadyFix;
    }
}
