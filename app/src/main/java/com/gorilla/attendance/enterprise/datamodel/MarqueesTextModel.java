package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/2/13.
 */

public class MarqueesTextModel {
    public static final String KEY_ZH_TW = "zh_TW";
    public static final String KEY_EN_US = "en_US";
    public static final String KEY_ZH_CN = "zh_CN";

    private String zh_TW;
    private String en_US;
    private String zh_CN;

    public String getZhTw() {
        return zh_TW;
    }
    public void setZhTw(String zh_TW) {
        this.zh_TW = zh_TW;
    }

    public String getEnUs() {
        return en_US;
    }
    public void setEnUs(String en_US) {
        this.en_US = en_US;
    }

    public String getZhCn() {
        return zh_CN;
    }
    public void setZhCn(String zh_CN) {
        this.zh_CN = zh_CN;
    }
}
