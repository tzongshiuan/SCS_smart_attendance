package com.gorilla.attendance.enterprise.database.bean;

import android.provider.SyncStateContract;

import java.io.Serializable;

/**
 * Created by ggshao on 2017/3/3.
 */

public class AcceptancesBean implements Serializable, SyncStateContract.Columns {
    private static final long serialVersionUID = 6432142491318744597L;


    public static final String ID = "id";
    public static final String EMPLOYEE_ID = "employeeId";
    public static final String SECURITY_CODE = "securityCode";
    public static final String RFID = "rfid";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String PHOTO_URL = "photoUrl";
    public final static String INT_ID = "intId";//int
    public final static String PHOTO = "photo";
    public final static String START_TIME = "startTime";
    public final static String END_TIME = "endTime";

    private String id;
    private String employeeId;
    private String securityCode;
    private String rfid;
    private String firstName;
    private String lastName;
    private String photoUrl;
    private int intId = -1;
    private byte[] photo = null;
    private long startTime = -1;
    private long endTime = -1;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
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

    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getIntId() {
        return intId;
    }
    public void setIntId(int intId) {
        this.intId = intId;
    }

    public byte[] getPhoto() {
        return photo;
    }
    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public long getStartTime() {
        return startTime;
    }
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

}
