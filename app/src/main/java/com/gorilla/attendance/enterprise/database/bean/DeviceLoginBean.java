package com.gorilla.attendance.enterprise.database.bean;

import android.provider.SyncStateContract;

import java.io.Serializable;

/**
 * Created by ggshao on 2017/3/6.
 */

public class DeviceLoginBean implements Serializable, SyncStateContract.Columns {
    private static final long serialVersionUID = 644214349782748597L;

    public final static String LOCALE = "locale";
    public final static String MODULE = "module";
    public final static String MODES = "modes";

    private String locale = null;
    private String module = null;
    private String modes = null;

    public String getLocale() {
        return locale;
    }
    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getModes() {
        return modes;
    }
    public void setModes(String modes) {
        this.modes = modes;
    }

    public String getModule() {
        return module;
    }
    public void setModule(String module) {
        this.module = module;
    }


}
