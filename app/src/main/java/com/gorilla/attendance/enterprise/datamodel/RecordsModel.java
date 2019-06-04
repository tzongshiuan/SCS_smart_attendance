package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/2/12.
 */

public class RecordsModel {
    public static final String KEY_SERIAL = "serial";
    public static final String KEY_ID = "id";
    public static final String KEY_SECURITY_CODE = "securityCode";
    public static final String KEY_CLIENT_TIME = "deviceTime";
    public static final String KEY_TYPE = "type";
    public static final String KEY_FACE_VERIFY = "faceVerify";
    public static final String KEY_FACE_IMG = "faceImg";
    public static final String KEY_LIVENESS = "liveness";
    public static final String KEY_MODE = "mode";

    private String serial;
    private String id;
    private String securityCode;
    private String clientTime;
    private String type;
    private String faceVerify;
    private String faceImg;
    private boolean liveness;
    private int mode;

    public String getSerial() {
        return serial;
    }
    public void setSerial(String serial) {
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

    public String getClientTime() {
        return clientTime;
    }
    public void setClientTime(String clientTime) {
        this.clientTime = clientTime;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getFaceVerify() {
        return faceVerify;
    }
    public void setFaceVerify(String faceVerify) {
        this.faceVerify = faceVerify;
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

    public int getMode() {
        return mode;
    }
    public void setMode(int mode) {
        this.mode = mode;
    }

}
