package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/2/12.
 */

public class GetVerifiedIdAndImageModel {
    public static final String KEY_STATUS = "status";
    public static final String KEY_DATA = "data";

    private String status;
//    private ArrayList<IdentifyDataModel> mIdentifyData = new ArrayList<IdentifyDataModel>();


    private IdentitiesDataModel identitiesData;


    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public IdentitiesDataModel getIdentitiesData() {
        return identitiesData;
    }
    public void setIdentitiesData(IdentitiesDataModel identitiesData) {
        this.identitiesData = identitiesData;
    }


//    public ArrayList<IdentifyDataModel> getIdentifyData() {
//        return mIdentifyData;
//    }
//    public void setIdentifyData(ArrayList<IdentifyDataModel> data) {
//        mIdentifyData = data;
//    }
//
//    public void addIdentifyData(IdentifyDataModel data) {
//        mIdentifyData.add(data);
//    }
//
//    public void removeIdentifyData(Object obj) {
//        mIdentifyData.remove(obj);
//    }
//
//    public void addIdentifyDataDeepCopy(IdentifyDataModel data) {
//
//        IdentifyDataModel temp = new IdentifyDataModel();
//
//        temp.setModelId(data.getModelId());
//        temp.setEmployeeId(data.getEmployeeId());
//        temp.setEmployeeName(data.getEmployeeName());
//        temp.setCreatedTime(data.getCreatedTime());
//        temp.setModel(data.getModel());
//
//        mIdentifyData.add(temp);
//    }
}
