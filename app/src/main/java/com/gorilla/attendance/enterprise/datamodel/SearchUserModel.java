package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/5/4.
 */

public class SearchUserModel {
    public static final String KEY_STATUS = "status";
    public static final String KEY_DATA = "data";

    private String status;
    private UsersDataModel users;
    private MemberDataModel memberDataModel;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public UsersDataModel getUsers() {
        return users;
    }
    public void setUsers(UsersDataModel users) {
        this.users = users;
    }

    public MemberDataModel getMemberData() {
        return memberDataModel;
    }
    public void setMemberData(MemberDataModel memberDataModel) {
        this.memberDataModel = memberDataModel;
    }


}
