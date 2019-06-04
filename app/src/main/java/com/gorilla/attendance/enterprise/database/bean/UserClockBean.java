package com.gorilla.attendance.enterprise.database.bean;

import android.provider.SyncStateContract;

import java.io.Serializable;

/**
 * Created by ggshao on 2017/2/20.
 */

public class UserClockBean implements Serializable, SyncStateContract.Columns {
    private static final long serialVersionUID = 6432149491212748597L;

    public final static String CLIENT_NAME = "clientName";
    public final static String SERIAL = "serial";
    public final static String ID = "id";
    public final static String SECURITY_CODE = "securityCode";
    public final static String TYPE = "type";
    public final static String PNG_INFO_1 = "pngInfo1";
    public final static String PNG_INFO_2 = "pngInfo2";
    public final static String PNG_INFO_3 = "pngInfo3";
    public final static String PNG_INFO_4 = "pngInfo4";
    public final static String PNG_INFO_5 = "pngInfo5";
    public final static String FACE_VERIFY = "faceVerify";
    public final static String CLIENT_TIME = "clientTime";
    public final static String CLOCK_TYPE = "clockType";
    public final static String LIVENESS = "liveness";
    public final static String MODE = "mode";
    public final static String MODULE = "module";

    public final static String RFID = "rfid";
    public final static String RECORD_MODE = "recordMode";

    public final static String IS_VISITOR_OPEN_DOOR = "isVisitorOpenDoor";
    public final static String IS_EMPLOYEE_OPEN_DOOR = "isEmployeeOpenDoor";
    private String clientName = null;
    private int serial = 0;
    private String id = null;
    private String securityCode = null;
    private String type = null;
    private byte[] pngInfo1 = null;
    private byte[] pngInfo2 = null;
    private byte[] pngInfo3 = null;
    private byte[] pngInfo4 = null;
    private byte[] pngInfo5 = null;
    private String faceVerify = null;
    private long clientTime = 0;
    private String clockType = null;
    private String liveness = null;
    private int mode = -1;
    private int module = -1;
    private String rfid = null;
    private String recordMode = null;

    private boolean isVisitorOpenDoor = false;
    private boolean isEmployeeOpenDoor = false;

    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getSerial() {
        return serial;
    }
    public void setSerial(int serial) {
        this.serial = serial;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getSecurityCode() {
        return securityCode;
    }
    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public byte[] getPngInfo1() {
        return pngInfo1;
    }

    public void setPngInfo1(byte[] pngInfo1) {
        this.pngInfo1 = pngInfo1;
    }

    public byte[] getPngInfo2() {
        return pngInfo2;
    }

    public void setPngInfo2(byte[] pngInfo2) {
        this.pngInfo2 = pngInfo2;
    }

    public byte[] getPngInfo3() {
        return pngInfo3;
    }

    public void setPngInfo3(byte[] pngInfo3) {
        this.pngInfo3 = pngInfo3;
    }

    public byte[] getPngInfo4() {
        return pngInfo4;
    }
    public void setPngInfo4(byte[] pngInfo4) {
        this.pngInfo4 = pngInfo4;
    }

    public byte[] getPngInfo5() {
        return pngInfo5;
    }
    public void setPngInfo5(byte[] pngInfo5) {
        this.pngInfo5 = pngInfo5;
    }

    public String getFaceVerify() {
        return faceVerify;
    }
    public void setFaceVerify(String faceVerify) {
        this.faceVerify = faceVerify;
    }

    public long getClientTime() {
        return clientTime;
    }
    public void setClientTime(long clientTime) {
        this.clientTime = clientTime;
    }

    public String getClockType() {
        return clockType;
    }
    public void setClockType(String clockType) {
        this.clockType = clockType;
    }

    public String getLiveness() {
        return liveness;
    }
    public void setLiveness(String liveness) {
        this.liveness = liveness;
    }

    public int getMode() {
        return mode;
    }
    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getModule() {
        return module;
    }
    public void setModule(int module) {
        this.module = module;
    }

    public String getRfid() {
        return rfid;
    }
    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getRecordMode() {
        return recordMode;
    }
    public void setRecordMode(String recordMode) {
        this.recordMode = recordMode;
    }

    public boolean getIsEmployeeOpenDoor() {
        return isEmployeeOpenDoor;
    }
    public void setIsEmployeeOpenDoor(boolean isEmployeeOpenDoor) {
        this.isEmployeeOpenDoor = isEmployeeOpenDoor;
    }

    public boolean getIsVisitorOpenDoor() {
        return isVisitorOpenDoor;
    }
    public void setIsVisitorOpenDoor(boolean isVisitorOpenDoor) {
        this.isVisitorOpenDoor = isVisitorOpenDoor;
    }

}
