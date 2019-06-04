package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/2/10.
 */

public class GetEmployeeModel {
    public static final String KEY_STATUS = "status";
    public static final String KEY_DATA = "data";

    private String status;
    private EmployeeDataModel employeeData;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public EmployeeDataModel getEmployeeData() {
        return employeeData;
    }
    public void setEmployeeData(EmployeeDataModel employeeData) {
        this.employeeData = employeeData;
    }
}
