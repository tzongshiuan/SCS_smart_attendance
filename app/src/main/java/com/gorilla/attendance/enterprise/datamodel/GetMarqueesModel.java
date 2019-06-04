package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/2/13.
 */

public class GetMarqueesModel {
    public static final String KEY_STATUS = "status";
    public static final String KEY_DATA = "data";

    private String status;
    private MarqueesDataModel marqueesData;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public MarqueesDataModel getMarqueesDat() {
        return marqueesData;
    }
    public void setMarqueesDat(MarqueesDataModel marqueesData) {
        this.marqueesData = marqueesData;
    }
}
