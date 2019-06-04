package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/2/16.
 */

public class IdentitiesModel {
    public static final String KEY_BAP_MODEL_ID = "bapModelId";
    public static final String KEY_ID = "id";
    public static final String KEY_INT_ID = "intId";
    public static final String KEY_EMPLOYEE_ID = "employeeId";
    public static final String KEY_EMPLOYEE_NAME = "employeeName";
    public static final String KEY_VISITOR_NAME = "visitorName";
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_CREATED_TIME = "createdTime";
    public static final String KEY_MODEL = "model";

    public final static String IS_ALREADY_FIX = "isAlreadyFix";


    private String bapModelId;
    private String intId;
    private String id;
    private String employeeName;
    private String visitorName;
    private String firstName;
    private String lastName;
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

    public String getIntId() {
        return intId;
    }
    public void setIntId(String intId) {
        this.intId = intId;
    }

    public String getVisitorName() {
        return visitorName;
    }
    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getEmployeeName() {
        return employeeName;
    }
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
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
