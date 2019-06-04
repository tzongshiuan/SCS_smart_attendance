package com.gorilla.attendance.enterprise.util;

import com.gorilla.attendance.enterprise.database.bean.RegisterBean;
import com.gorilla.attendance.enterprise.datamodel.RoleModel;

/**
 * Created by ggshao on 2017/2/10.
 */

public class ClockUtils {

    public static RoleModel mRoleModel = null;
    public static String mLoginAccount = null;
    public static String mLoginName = null;
    public static String mLoginVerifyStatus = null;
    public static long mLoginTime = 0;
    public static String mType = null;
    public static String mFaceVerify = null;
    public static String mLiveness = null;
    public static int mMode = 0;
    public static int mModule = 0;

    public static int mSerialNumber = 1;
    public static String mLoginUuid = null;

    public static String mRfid = null;
    public static String mRecordMode = null;

    public static long mStartTime = 0;
    public static long mEndTime = 0;

    public static RegisterBean mRegisterBean = null;

    public static String mVisitorRenewSecurityCode = null;

    public static int mLoginIntId = -1;



    public static void clearClockData(){
//        mLoginAccount = null;
        mLoginName = null;
//        mLoginVerifyStatus = null;
//        mLoginTime = 0;
//        mType = null;
//        mFaceVerify = null;
//        mLiveness = null;
        mLoginUuid = null;
        mRfid = null;
        mRecordMode = null;
//        mRegisterBean = null;
        mStartTime = 0;
        mEndTime = 0;


    }

}
