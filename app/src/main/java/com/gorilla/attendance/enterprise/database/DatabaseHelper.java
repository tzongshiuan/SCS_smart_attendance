package com.gorilla.attendance.enterprise.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gorilla.attendance.enterprise.database.bean.AcceptancesBean;
import com.gorilla.attendance.enterprise.database.bean.DeviceLoginBean;
import com.gorilla.attendance.enterprise.database.bean.RegisterBean;
import com.gorilla.attendance.enterprise.database.bean.UserClockBean;
import com.gorilla.attendance.enterprise.database.bean.UserLoginInfoBean;
import com.gorilla.attendance.enterprise.database.bean.VerifiedFaceBean;
import com.gorilla.attendance.enterprise.util.LOG;

/**
 * Created by ggshao on 2017/2/7.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TAG = "DatabaseHelper";

    public final static String DEVICE_LOGIN_TABLE = "deviceLogin";

    public final static String USER_LOGIN_INFO_TABLE = "userLoginInfo";
    public final static String USER_CLOCK_TABLE = "userClock";
    public final static String EMPLOYEE_IDENTITIES_TABLE = "employeeIdentities";
    public final static String VISITOR_IDENTITIES_TABLE = "visitorIdentities";

    public final static String EMPLOYEES_ACCEPTANCES_TABLE = "employeesAcceptances";
    public final static String VISITORS_ACCEPTANCES_TABLE = "visitorsAcceptances";

    public final static String EMPLOYEES_REGISTER_TABLE = "employeesRegister";
    public final static String VISITORS_REGISTER_TABLE = "visitorsRegister";

    public final static String CREATE_DEVICE_LOGIN_TABLE = "create table "
            + DEVICE_LOGIN_TABLE
            + " (" + DeviceLoginBean._ID + " integer primary key autoincrement, "
            + DeviceLoginBean.LOCALE + " text, "
            + DeviceLoginBean.MODULE + " text, "
            + DeviceLoginBean.MODES + " text "
            + ")";

    public final static String CREATE_USER_LOGIN_INFO_TABLE = "create table "
            + USER_LOGIN_INFO_TABLE
            + " (" + UserLoginInfoBean._ID + " integer primary key autoincrement, "
            + UserLoginInfoBean.EMPLOYEE_ID + " text, "
            + UserLoginInfoBean.CLIENT_NAME + " text, "
            + UserLoginInfoBean.PNG_INFO_1 + " text, "
            + UserLoginInfoBean.PNG_INFO_2 + " text, "
            + UserLoginInfoBean.PNG_INFO_3 + " text, "
            + UserLoginInfoBean.PNG_INFO_4 + " text, "
            + UserLoginInfoBean.PNG_INFO_5 + " text, "
            + UserLoginInfoBean.FACE_VERIFY + " text, "
            + UserLoginInfoBean.LOGIN_TIME + " text, "
            + UserLoginInfoBean.CLOCK_TYPE + " text, "
            + UserLoginInfoBean.LIVENESS + " integer "
            + ")";

    public final static String CREATE_USER_CLOCK_TABLE = "create table "
            + USER_CLOCK_TABLE
            + " (" + UserClockBean._ID + " integer primary key autoincrement, "
            + UserClockBean.CLIENT_NAME + " text, "
            + UserClockBean.SERIAL + " text, "
            + UserClockBean.ID + " text, "
            + UserClockBean.SECURITY_CODE + " text, "
            + UserClockBean.TYPE + " text, "
            + UserClockBean.PNG_INFO_1 + " text, "
            + UserClockBean.PNG_INFO_2 + " text, "
            + UserClockBean.PNG_INFO_3 + " text, "
            + UserClockBean.PNG_INFO_4 + " text, "
            + UserClockBean.PNG_INFO_5 + " text, "
            + UserClockBean.FACE_VERIFY + " text, "
            + UserClockBean.CLIENT_TIME + " text, "
            + UserClockBean.CLOCK_TYPE + " text, "
            + UserClockBean.LIVENESS + " text, "
            + UserClockBean.MODE + " integer, "
            + UserClockBean.MODULE + " integer, "
            + UserClockBean.RFID + " text, "
            + UserClockBean.RECORD_MODE + " text, "
            + UserClockBean.IS_EMPLOYEE_OPEN_DOOR + " integer, "
            + UserClockBean.IS_VISITOR_OPEN_DOOR + " integer"
            + ")";


    public final static String CREATE_EMPLOYEE_IDENTITIES_TABLE = "create table "
            + EMPLOYEE_IDENTITIES_TABLE
            + " (" + VerifiedFaceBean._ID + " integer primary key autoincrement, "
            + VerifiedFaceBean.BAP_MODEL_ID + " text, "
            + VerifiedFaceBean.ID + " text, "
            + VerifiedFaceBean.SECURITY_CODE + " text, "
            + VerifiedFaceBean.EMPLOYEE_ID + " text, "
            + VerifiedFaceBean.EMPLOYEE_NAME + " text, "
            + VerifiedFaceBean.VISITOR_NAME + " text, "
            + VerifiedFaceBean.CREATED_TIME + " text, "
            + VerifiedFaceBean.MODEL + " text"
            + ")";

    public final static String CREATE_VISITOR_IDENTITIES_TABLE = "create table "
            + VISITOR_IDENTITIES_TABLE
            + " (" + VerifiedFaceBean._ID + " integer primary key autoincrement, "
            + VerifiedFaceBean.BAP_MODEL_ID + " text, "
            + VerifiedFaceBean.ID + " text, "
            + VerifiedFaceBean.SECURITY_CODE + " text, "
            + VerifiedFaceBean.EMPLOYEE_ID + " text, "
            + VerifiedFaceBean.EMPLOYEE_NAME + " text, "
            + VerifiedFaceBean.VISITOR_NAME + " text, "
            + VerifiedFaceBean.CREATED_TIME + " text, "
            + VerifiedFaceBean.MODEL + " text"
            + ")";

    public final static String CREATE_EMPLOYEES_ACCEPTANCES_TABLE = "create table "
            + EMPLOYEES_ACCEPTANCES_TABLE
            + " (" + AcceptancesBean._ID + " integer primary key autoincrement, "
            + AcceptancesBean.ID + " text, "
            + AcceptancesBean.EMPLOYEE_ID + " text, "
            + AcceptancesBean.SECURITY_CODE + " text, "
            + AcceptancesBean.RFID + " text, "
            + AcceptancesBean.FIRST_NAME + " text, "
            + AcceptancesBean.LAST_NAME + " text, "
            + AcceptancesBean.PHOTO_URL + " text, "
            + AcceptancesBean.INT_ID + " integer, "
            + AcceptancesBean.START_TIME + " integer, "
            + AcceptancesBean.END_TIME + " integer, "
            + AcceptancesBean.PHOTO + " text"
            + ")";

    public final static String CREATE_VISITORS_ACCEPTANCES_TABLE = "create table "
            + VISITORS_ACCEPTANCES_TABLE
            + " (" + AcceptancesBean._ID + " integer primary key autoincrement, "
            + AcceptancesBean.ID + " text, "
            + AcceptancesBean.EMPLOYEE_ID + " text, "
            + AcceptancesBean.SECURITY_CODE + " text, "
            + AcceptancesBean.RFID + " text, "
            + AcceptancesBean.FIRST_NAME + " text, "
            + AcceptancesBean.LAST_NAME + " text, "
            + AcceptancesBean.PHOTO_URL + " text, "
            + AcceptancesBean.INT_ID + " integer, "
            + AcceptancesBean.START_TIME + " integer, "
            + AcceptancesBean.END_TIME + " integer, "
            + AcceptancesBean.PHOTO + " text"
            + ")";

    public final static String CREATE_EMPLOYEES_REGISTER_TABLE = "create table "
            + EMPLOYEES_REGISTER_TABLE
            + " (" + RegisterBean._ID + " integer primary key autoincrement, "
            + RegisterBean.DEVICE_TOKEN + " text, "
            + RegisterBean.EMPLOYEE_ID + " text, "
            + RegisterBean.NAME + " text, "
            + RegisterBean.EMAIL + " text, "
            + RegisterBean.PASSWORD + " text, "
            + RegisterBean.CREATE_TIME + " text, "
            + RegisterBean.FORMAT + " text, "
            + RegisterBean.DATA_IN_BASE64 + " text, "
            + RegisterBean.MOBILE_NO + " text, "
            + RegisterBean.DEPARTMENT + " text, "
            + RegisterBean.TITLE + " text, "
            + RegisterBean.MODEL + " text, "
            + RegisterBean.MODEL_ID + " integer, "
            + RegisterBean.RFID + " text "
            + ")";

    public final static String CREATE_VISITORS_REGISTER_TABLE = "create table "
            + VISITORS_REGISTER_TABLE
            + " (" + RegisterBean._ID + " integer primary key autoincrement, "
            + RegisterBean.DEVICE_TOKEN + " text, "
            + RegisterBean.EMPLOYEE_ID + " text, "
            + RegisterBean.NAME + " text, "
            + RegisterBean.EMAIL + " text, "
            + RegisterBean.PASSWORD + " text, "
            + RegisterBean.CREATE_TIME + " text, "
            + RegisterBean.FORMAT + " text, "
            + RegisterBean.DATA_IN_BASE64 + " text, "
            + RegisterBean.MOBILE_NO + " text, "
            + RegisterBean.DEPARTMENT + " text, "
            + RegisterBean.TITLE + " text, "
            + RegisterBean.MODEL + " text, "
            + RegisterBean.MODEL_ID + " integer, "
            + RegisterBean.RFID + " text "
            + ")";


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("drop table if exists " + DEVICE_LOGIN_TABLE);
        db.execSQL("drop table if exists " + USER_LOGIN_INFO_TABLE);
        db.execSQL("drop table if exists " + USER_CLOCK_TABLE);
        db.execSQL("drop table if exists " + EMPLOYEE_IDENTITIES_TABLE);
        db.execSQL("drop table if exists " + VISITOR_IDENTITIES_TABLE);
        db.execSQL("drop table if exists " + EMPLOYEES_ACCEPTANCES_TABLE);
        db.execSQL("drop table if exists " + VISITORS_ACCEPTANCES_TABLE);
        db.execSQL("drop table if exists " + EMPLOYEES_REGISTER_TABLE);
        db.execSQL("drop table if exists " + VISITORS_REGISTER_TABLE);
        db.execSQL(CREATE_DEVICE_LOGIN_TABLE);
        db.execSQL(CREATE_USER_LOGIN_INFO_TABLE);
        db.execSQL(CREATE_USER_CLOCK_TABLE);
        db.execSQL(CREATE_EMPLOYEE_IDENTITIES_TABLE);
        db.execSQL(CREATE_VISITOR_IDENTITIES_TABLE);
        db.execSQL(CREATE_EMPLOYEES_ACCEPTANCES_TABLE);
        db.execSQL(CREATE_VISITORS_ACCEPTANCES_TABLE);
        db.execSQL(CREATE_EMPLOYEES_REGISTER_TABLE);
        db.execSQL(CREATE_VISITORS_REGISTER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LOG.D(TAG,"onUpgrade(), oldVersion=" + oldVersion + ", newVersion=" + newVersion);
        boolean success = false;

        if (newVersion > oldVersion) {

            db.beginTransaction();

//            switch(oldVersion){
//                case 1:
//                    db.execSQL("ALTER TABLE "+COURSE_DOWNLOAD_TABLE+" ADD COLUMN " + DownloadCourseBean.ISEXAMPASSED +" integer DEFAULT '0'");
//                    oldVersion++;
//                    success = true;
//            }

            if (success) {
                db.setTransactionSuccessful();
            }

            db.endTransaction();

        }else {
            onCreate(db);
        }
    }

}
