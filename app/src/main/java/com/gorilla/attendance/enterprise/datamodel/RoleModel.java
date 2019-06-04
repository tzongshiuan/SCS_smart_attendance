package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/2/10.
 */

public class RoleModel {

    protected int modules = 0;
    protected int[] modes = null;

    public int value = 0;


    public RoleModel(){

    }

    public int getModules() {
        return modules;
    }
    public void setModules(int modules) {
        this.modules = modules;
    }

    public int[] getModes() {
        return modes;
    }
    public void setModes(int[] modes) {
        this.modes = modes;
    }


}
