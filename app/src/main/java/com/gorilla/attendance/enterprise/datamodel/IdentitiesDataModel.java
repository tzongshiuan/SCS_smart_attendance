package com.gorilla.attendance.enterprise.datamodel;

import java.util.ArrayList;

/**
 * Created by ggshao on 2017/2/16.
 */

public class IdentitiesDataModel {
    public static final String KEY_IDENTITIES = "identities";
    public static final String KEY_EMPLOYEES = "employees";
    public static final String KEY_VISITORS = "visitors";


//    private ArrayList<IdentitiesModel> identities = new ArrayList<IdentitiesModel>();

    private ArrayList<IdentitiesModel> employeesIdentities = new ArrayList<IdentitiesModel>();
    private ArrayList<IdentitiesModel> visitorsIdentities = new ArrayList<IdentitiesModel>();


    public ArrayList<IdentitiesModel> getEmployeesIdentifyData() {
        return employeesIdentities;
    }
    public void setEmployeesIdentifyData(ArrayList<IdentitiesModel> employeesIdentities) {
        this.employeesIdentities = employeesIdentities;
    }

    public void addEmployeesIdentifyData(IdentitiesModel employeesIdentities) {
        this.employeesIdentities.add(employeesIdentities);
    }

    public void removeEmployeesIdentifyData(Object obj) {
        employeesIdentities.remove(obj);
    }

    public void addEmployeesIdentifyDataDeepCopy(IdentitiesModel identities) {

        IdentitiesModel temp = new IdentitiesModel();

        temp.setBapModelId(identities.getBapModelId());
        temp.setId(identities.getId());
        temp.setEmployeeId(identities.getEmployeeId());
        temp.setEmployeeName(identities.getEmployeeName());
        temp.setCreatedTime(identities.getCreatedTime());
        temp.setModel(identities.getModel());

        this.employeesIdentities.add(temp);
    }



    public ArrayList<IdentitiesModel> getVisitorsIdentifyData() {
        return visitorsIdentities;
    }
    public void setVisitorsIdentifyData(ArrayList<IdentitiesModel> visitorsIdentities) {
        this.visitorsIdentities = visitorsIdentities;
    }

    public void addVisitorsIdentifyData(IdentitiesModel visitorsIdentities) {
        this.visitorsIdentities.add(visitorsIdentities);
    }

    public void removeVisitorsIdentifyData(Object obj) {
        visitorsIdentities.remove(obj);
    }

    public void addVisitorsIdentifyDataDeepCopy(IdentitiesModel identities) {

        IdentitiesModel temp = new IdentitiesModel();

        temp.setBapModelId(identities.getBapModelId());
        temp.setId(identities.getId());
        temp.setEmployeeId(identities.getEmployeeId());
        temp.setEmployeeName(identities.getEmployeeName());
        temp.setCreatedTime(identities.getCreatedTime());
        temp.setModel(identities.getModel());

        this.visitorsIdentities.add(temp);
    }

}
