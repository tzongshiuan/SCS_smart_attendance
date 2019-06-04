package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/5/4.
 */

public class UsersDataModel {
    public static final String KEY_MEMBER = "member";

    private MemberDataModel memberDataModel;

    public MemberDataModel getMemberData() {
        return memberDataModel;
    }
    public void setMemberData(MemberDataModel memberDataModel) {
        this.memberDataModel = memberDataModel;
    }

}
