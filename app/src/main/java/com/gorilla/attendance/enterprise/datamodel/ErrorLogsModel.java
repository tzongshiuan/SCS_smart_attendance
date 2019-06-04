package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/2/12.
 */

public class ErrorLogsModel {
    public static final String KEY_SERIAL = "serial";
    public static final String KEY_MODE = "mode";
    public static final String KEY_RFID = "rfid";
    public static final String KEY_SECURITY_CODE = "securityCode";
    public static final String KEY_DEVICE_TIME = "deviceTime";
    public static final String KEY_FACE_IMG = "faceImg";
    public static final String KEY_LIVENESS = "liveness";

    private String serial;
    private String mode;
    private String rfid;
    private String securityCode;
    private String deviceTime;
    private String faceImg;
    private boolean liveness;


    public String getSerial() {
        return serial;
    }
    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getMode() {
        return mode;
    }
    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getRfid() {
        return rfid;
    }
    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getSecurityCode() {
        return securityCode;
    }
    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public String getDeviceTime() {
        return deviceTime;
    }
    public void setDeviceTime(String deviceTime) {
        this.deviceTime = deviceTime;
    }


    public String getFaceImg() {
        return faceImg;
    }
    public void setFaceImg(String faceImg) {
        this.faceImg = faceImg;
    }

    public boolean getLiveness() {
        return liveness;
    }
    public void setLiveness(boolean liveness) {
        this.liveness = liveness;
    }

}
