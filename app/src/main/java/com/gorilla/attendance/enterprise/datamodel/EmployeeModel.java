package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/2/10.
 */

public class EmployeeModel extends RoleModel{

    public int value = 1;
    private EmployeeDataModel mEmployeeDataModel = null;

    public EmployeeModel(){
        super();
    }

    public EmployeeDataModel getEmployeeData() {
        return mEmployeeDataModel;
    }
    public void setEmployeeData(EmployeeDataModel employeeData) {
        this.mEmployeeDataModel = employeeData;
    }





}
