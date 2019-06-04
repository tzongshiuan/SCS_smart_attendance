package com.gorilla.attendance.enterprise.database.bean;

import android.provider.SyncStateContract;

import java.io.Serializable;

/**
 * Created by ggshao on 2017/5/5.
 */

public class RegisterBean implements Serializable, SyncStateContract.Columns {
    private static final long serialVersionUID = 6432129491338744597L;


    public static final String DEVICE_TOKEN = "deviceToken";
    public static final String EMPLOYEE_ID = "employeeId";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String CREATE_TIME = "createTime";
    public static final String FORMAT = "format";
    public final static String DATA_IN_BASE64 = "dataInBase64";//int
    public final static String MOBILE_NO = "mobileNo";
    public final static String DEPARTMENT = "department";
    public final static String TITLE = "title";
    public final static String MODEL_ID = "modelId";
    public final static String MODEL = "model";
    public final static String RFID = "rfid";


    public final static String REGISTER_TYPE = "registerType";
    public final static String SEARCH_USER_SUCCESS = "isSearchUserSuccess";
    public final static String SECURITY_CODE = "securityCode";

    private String deviceToken;
    private String employeeId;
    private String name;
    private String email;
    private String password;
    private String createTime;
    private String format;

    private byte[] dataInBase64 = null;
    private byte[] model = null;
    private int modelId = -1;
    private String mobileNo;
    private String department;
    private String title;

    private int registerType;           //no record in db
    private boolean isSearchUserSuccess;    //no record in db
    private String securityCode;
    private String rfid;




    public String getDeviceToken() {
        return deviceToken;
    }
    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreateTime() {
        return createTime;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format;
    }

    public int getModelId() {
        return modelId;
    }
    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public String getMobileNo() {
        return mobileNo;
    }
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getDataInBase64() {
        return dataInBase64;
    }
    public void setDataInBase64(byte[] dataInBase64) {
        this.dataInBase64 = dataInBase64;
    }

    public byte[] getModel() {
        return model;
    }
    public void setModel(byte[] model) {
        this.model = model;
    }

    public int getRegisterType() {
        return registerType;
    }
    public void setRegisterType(int registerType) {
        this.registerType = registerType;
    }

    public boolean getIsSearchUserSuccess() {
        return isSearchUserSuccess;
    }
    public void setIsSearchUserSuccess(boolean isSearchUserSuccess) {
        this.isSearchUserSuccess = isSearchUserSuccess;
    }

    public String getSecurityCode() {
        return securityCode;
    }
    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public String getRfid() {
        return rfid;
    }
    public void setRfid(String rfid) {
        this.rfid = rfid;
    }


}
