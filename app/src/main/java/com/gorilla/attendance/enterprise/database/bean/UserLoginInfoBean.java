package com.gorilla.attendance.enterprise.database.bean;

import android.provider.SyncStateContract;

import java.io.Serializable;

/**
 * Created by ggshao on 2017/2/7.
 */

public class UserLoginInfoBean implements Serializable, SyncStateContract.Columns {
    private static final long serialVersionUID = 6432249691614721547L;

    public final static String EMPLOYEE_ID = "employeeId";
    public final static String CLIENT_NAME = "clientName";
    public final static String PNG_INFO_1 = "pngInfo1";
    public final static String PNG_INFO_2 = "pngInfo2";
    public final static String PNG_INFO_3 = "pngInfo3";
    public final static String PNG_INFO_4 = "pngInfo4";
    public final static String PNG_INFO_5 = "pngInfo5";
    public final static String FACE_VERIFY = "faceVerify";
    public final static String LOGIN_TIME = "loginTime";
    public final static String CLOCK_TYPE = "clockType";
    public final static String LIVENESS = "liveness";


    private String mEmployeeId = null;
    private String mClientName = null;
    private byte[] mPngInfo1 = null;
    private byte[] mPngInfo2 = null;
    private byte[] mPngInfo3 = null;
    private byte[] mPngInfo4 = null;
    private byte[] mPngInfo5 = null;
    private String mFaceVerify = null;
    private String mLoginTime = null;
    private String mClockType = null;
    private int mLiveness = -1;

    public String getEmployeeId() {
        return mEmployeeId;
    }
    public void setEmployeeId(String employeeId) {
        mEmployeeId = employeeId;
    }

    public String getClientName() {
        return mClientName;
    }
    public void setClientName(String clientName) {
        mClientName = clientName;
    }

    public byte[] getPngInfo1() {
        return mPngInfo1;
    }

    public void setPngInfo1(byte[] pngInfo1) {
        mPngInfo1 = pngInfo1;
    }

    public byte[] getPngInfo2() {
        return mPngInfo2;
    }

    public void setPngInfo2(byte[] pngInfo2) {
        mPngInfo2 = pngInfo2;
    }

    public byte[] getPngInfo3() {
        return mPngInfo3;
    }

    public void setPngInfo3(byte[] pngInfo3) {
        mPngInfo3 = pngInfo3;
    }

    public byte[] getPngInfo4() {
        return mPngInfo4;
    }
    public void setPngInfo4(byte[] pngInfo4) {
        mPngInfo4 = pngInfo4;
    }

    public byte[] getPngInfo5() {
        return mPngInfo5;
    }
    public void setPngInfo5(byte[] pngInfo5) {
        mPngInfo5 = pngInfo5;
    }

    public String getFaceVerify() {
        return mFaceVerify;
    }
    public void setFaceVerify(String faceVerify) {
        mFaceVerify = faceVerify;
    }

    public String getLoginTime() {
        return mLoginTime;
    }
    public void setLoginTime(String loginTime) {
        mLoginTime = loginTime;
    }

    public String getClockType() {
        return mClockType;
    }
    public void setClockType(String clockType) {
        mClockType = clockType;
    }

    public int getLiveness() {
        return mLiveness;
    }
    public void setLiveness(int liveness) {
        mLiveness = liveness;
    }
}
