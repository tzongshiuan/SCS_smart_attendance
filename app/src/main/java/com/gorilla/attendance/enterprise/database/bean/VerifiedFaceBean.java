package com.gorilla.attendance.enterprise.database.bean;

import android.provider.SyncStateContract;

import java.io.Serializable;

/**
 * Created by ggshao on 2017/2/12.
 */

public class VerifiedFaceBean implements Serializable, SyncStateContract.Columns {
    private static final long serialVersionUID = 6432249691618724073L;

    public final static String BAP_MODEL_ID = "bapModelId";
    public final static String ID = "id";
    public final static String INT_ID = "intId";
    public final static String SECURITY_CODE = "securityCode";
    public final static String EMPLOYEE_ID = "employeeId";
    public final static String EMPLOYEE_NAME = "employeeName";
    public final static String VISITOR_NAME = "visitorName";
    public final static String CREATED_TIME = "createdTime";
    public final static String MODEL = "model";


    public final static String IS_ALREADY_FIX = "isAlreadyFix";

    private String mBapModelId = null;
    private String mId = null;
    private String mSecurityCode = null;
    private String mIntId = null;
    private String mEmployeeId = null;
    private String mEmployeeName = null;
    private String mVisitorName = null;
    private String mCreatedTime = null;
    private byte[] mModel = null;

    private boolean mIsAlreadyFix = false;

    public String getBapModelId() {
        return mBapModelId;
    }
    public void setBapModelId(String bapModelId) {
        mBapModelId = bapModelId;
    }

    public String getIntId() {
        return mIntId;
    }
    public void setIntId(String intId) {
        mIntId = intId;
    }

    public String getId() {
        return mId;
    }
    public void setId(String id) {
        mId = id;
    }

    public String getSecurityCode() {
        return mSecurityCode;
    }
    public void setSecurityCode(String securityCode) {
        mSecurityCode = securityCode;
    }

    public String getEmployeeId() {
        return mEmployeeId;
    }
    public void setEmployeeId(String employeeId) {
        mEmployeeId = employeeId;
    }

    public String getEmployeeName() {
        return mEmployeeName;
    }
    public void setEmployeeName(String employeeName) {
        mEmployeeName = employeeName;
    }

    public String getVisitorName() {
        return mVisitorName;
    }
    public void setVisitorName(String visitorName) {
        mVisitorName = visitorName;
    }

    public byte[] getModel() {
        return mModel;
    }

    public void setModel(byte[] model) {
        mModel = model;
    }

    public String getCreatedTime() {
        return mCreatedTime;
    }
    public void setCreatedTime(String createdTime) {
        mCreatedTime = createdTime;
    }

    public boolean getIsAlreadyFix() {
        return mIsAlreadyFix;
    }
    public void setIsAlreadyFix(boolean isAlreadyFix) {
        mIsAlreadyFix = isAlreadyFix;
    }

}
