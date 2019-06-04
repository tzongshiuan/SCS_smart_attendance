package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/5/4.
 */

public class MemberDataModel {
    public static final String KEY_UUID = "id";
    public static final String KEY_EMPLOYEE_ID = "employeeId";
    public static final String KEY_MOBILE_NO = "mobileNo";
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_PHOTO_URL = "photoUrl";

    private String id;
    private String employeeId;
    private String mobileNo;
    private String firstName;
    private String lastName;
    private String photoUrl;

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

    public String getMobileNo() {
        return mobileNo;
    }
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
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

}
