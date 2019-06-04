package com.gorilla.attendance.enterprise.datamodel;

import java.util.ArrayList;

/**
 * Created by ggshao on 2017/2/9.
 */

public class LoginDataModel {

    public static final String KEY_LOCALE = "locale";
    public static final String KEY_BG_IMAGE = "bgImage";
    public static final String KEY_DEVICE_NAME = "deviceName";
    public static final String KEY_TITLE_IMAGE = "titleImage";
    public static final String KEY_MODULE_MODES = "modulesModes";

    private String locale;
    private String bgImage;
    private String titleImage;
    private String deviceName;

    private ArrayList<ModuleModesModel> moduleModesModel = new ArrayList<ModuleModesModel>();


    public String getLocale() {
        return locale;
    }
    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getBgImage() {
        return bgImage;
    }
    public void setBgImage(String bgImage) {
        this.bgImage = bgImage;
    }

    public String getTitleImage() {
        return titleImage;
    }
    public void setTitleImage(String titleImage) {
        this.titleImage = titleImage;
    }

    public String getDeviceName() {
        return deviceName;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }


    public ArrayList<ModuleModesModel> getModuleModes() {
        return moduleModesModel;
    }
    public void setModuleModes(ArrayList<ModuleModesModel> moduleModesModel) {
        this.moduleModesModel = moduleModesModel;
    }

    public void addModuleModes(ModuleModesModel moduleModesModel) {
        this.moduleModesModel.add(moduleModesModel);
    }

}
