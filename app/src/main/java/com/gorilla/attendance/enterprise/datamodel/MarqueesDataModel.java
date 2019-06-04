package com.gorilla.attendance.enterprise.datamodel;

import java.util.ArrayList;

/**
 * Created by ggshao on 2017/2/13.
 */

public class MarqueesDataModel {
    public static final String KEY_TOTAL_COUNTS = "totalCounts";
    public static final String KEY_MARQUEES = "marquees";

    private int totalCounts;
    private ArrayList<MarqueesModel> marquees = new ArrayList<MarqueesModel>();

    public int getTotalCounts() {
        return totalCounts;
    }
    public void setTotalCounts(int totalCounts) {
        this.totalCounts = totalCounts;
    }

    public ArrayList<MarqueesModel> getMarquees() {
        return marquees;
    }
    public void setMarquees(ArrayList<MarqueesModel> marquees) {
        this.marquees = marquees;
    }

    public void addMarquees(MarqueesModel marquees) {
        this.marquees.add(marquees);
    }
}
