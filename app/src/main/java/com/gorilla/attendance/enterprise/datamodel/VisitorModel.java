package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/2/10.
 */

public class VisitorModel extends RoleModel{

    public int value = 2;

    private VisitorDataModel mVisitorDataModel = null;

    public VisitorDataModel getVisitorData() {
        return mVisitorDataModel;
    }
    public void setVisitorData(VisitorDataModel visitorDataModel) {
        this.mVisitorDataModel = visitorDataModel;
    }

    public VisitorModel(){
        super();
    }

}
