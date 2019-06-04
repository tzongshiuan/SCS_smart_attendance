package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2018/5/14.
 */

public class BapIdentifyDataModel {
    public static final String KEY_UUID = "id";
    public static final String KEY_EMPLOYEE_ID = "employeeId";
    public static final String KEY_MOBILE_NO = "mobileNo";
    public static final String KEY_SECURITY_CODE = "securityCode";
    public static final String KEY_RFID = "rfid";
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_PHOTO_URL = "photoUrl";
    public static final String KEY_INT_ID = "intId";
    public static final String KEY_START_TIME = "startTime";
    public static final String KEY_END_TIME = "endTime";
    public static final String KEY_TYPE = "type";

    private String uuid;
    private String employeeId;
    private String mobileNo;
    private String securityCode;
    private String rfid;
    private String firstName;
    private String lastName;
    private String photoUrl;
    private int intId;
    private long startTime;
    private long endTime;
    private String type;

    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getMobileNo() {
        return mobileNo;
    }
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
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

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
