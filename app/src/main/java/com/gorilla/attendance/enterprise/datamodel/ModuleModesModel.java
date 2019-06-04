package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/2/9.
 */

public class ModuleModesModel {
    public static final String KEY_MODULE = "module";
    public static final String KEY_MODE = "modes";

    private int module;
    private int[] mode;


    public int getModule() {
        return module;
    }
    public void setModule(int module) {
        this.module = module;
    }

    public int[] getMode() {
        return mode;
    }
    public void setMode(int[] mode) {
        this.mode = mode;
    }


    /*
    *
    * TODO : String to int array
        String arr = "[1,2]";
        String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");

        int[] results = new int[items.length];

        for (int i = 0; i < items.length; i++) {
            try {
                results[i] = Integer.parseInt(items[i]);
            } catch (NumberFormatException nfe) {
                //NOTE: write something here if you need to recover from formatting errors
            };
        }
    *
    *
    * */






}
