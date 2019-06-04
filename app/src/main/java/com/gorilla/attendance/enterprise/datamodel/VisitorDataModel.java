package com.gorilla.attendance.enterprise.datamodel;

import java.util.ArrayList;

/**
 * Created by ggshao on 2017/2/14.
 */

public class VisitorDataModel {
    public static final String KEY_TOTAL_COUNTS = "totalCounts";
    public static final String KEY_ACCEPTANCES = "acceptances";

    private int totalCounts;
    private ArrayList<AcceptancesModel> acceptances = new ArrayList<AcceptancesModel>();

    public int getTotalCounts() {
        return totalCounts;
    }
    public void setTotalCounts(int totalCounts) {
        this.totalCounts = totalCounts;
    }

    public ArrayList<AcceptancesModel> getAcceptances() {
        return acceptances;
    }
    public void setAcceptances(ArrayList<AcceptancesModel> acceptances) {
        this.acceptances = acceptances;
    }

    public void addAcceptances(AcceptancesModel acceptances) {
        this.acceptances.add(acceptances);
    }

    public void updateAcceptance(int index, AcceptancesModel acceptances){
        this.acceptances.get(index).setIntId(acceptances.getIntId());
        this.acceptances.get(index).setSecurityCode(acceptances.getSecurityCode());
        this.acceptances.get(index).setLastName(acceptances.getLastName());
        this.acceptances.get(index).setRfid(acceptances.getRfid());
        this.acceptances.get(index).setUuid(acceptances.getUuid());
        this.acceptances.get(index).setFirstName(acceptances.getFirstName());
        this.acceptances.get(index).setEmployeeId(acceptances.getEmployeeId());
        this.acceptances.get(index).setPhotoUrl(acceptances.getPhotoUrl());

    }
}
