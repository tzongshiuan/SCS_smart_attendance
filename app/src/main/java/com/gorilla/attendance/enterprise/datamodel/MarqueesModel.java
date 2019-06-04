package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/2/13.
 */

public class MarqueesModel {
    public static final String KEY_TEXT = "text";
    public static final String KEY_SPEED = "speed";
    public static final String KEY_DIRECTION = "direction";

    private MarqueesTextModel marqueesText;
    private int speed;
    private int direction;

    public MarqueesTextModel getMarqueesText() {
        return marqueesText;
    }
    public void setMarqueesText(MarqueesTextModel marqueesText) {
        this.marqueesText = marqueesText;
    }

    public int getSpeed() {
        return speed;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDirecton() {
        return direction;
    }
    public void setDirecton(int direction) {
        this.direction = direction;
    }



}
