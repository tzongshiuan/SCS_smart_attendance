package com.gorilla.attendance.enterprise.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.gorilla.attendance.enterprise.database.bean.AcceptancesBean;
import com.gorilla.attendance.enterprise.database.bean.DeviceLoginBean;
import com.gorilla.attendance.enterprise.database.bean.RegisterBean;
import com.gorilla.attendance.enterprise.database.bean.UserClockBean;
import com.gorilla.attendance.enterprise.database.bean.UserLoginInfoBean;
import com.gorilla.attendance.enterprise.database.bean.VerifiedFaceBean;
import com.gorilla.attendance.enterprise.datamodel.IdentitiesModel;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.LOG;

import java.util.ArrayList;

/**
 * Created by ggshao on 2017/2/7.
 */

public class DatabaseAdapter {
    public static final String TAG = "DatabaseAdapter";
    private Context m_Context;
    private final static String LOCK = "DatabaseAdapter - lock";

    /**
     * database file name on device storage
     */
    private final static String DATABASE_NAME = "attendanceEnterprise.db";
    public static final int VALUE_ON = 1;
    public static final int VALUE_OFF = 0;

    /**
     * increment this number every time there is a change in db schema
     */
    private final static int DATABASE_VERSION = 1;

    /**
     * helper class to handle database creation and version upgrade (e.g. schema
     * change)
     */
    private DatabaseHelper m_DatabaseHelper;

    /**
     * internal reference of actual database object
     */
    private SQLiteDatabase db;

    /**
     * internal reference of database adapter
     */
    private static volatile DatabaseAdapter mAdapter;

    /**
     * do not use this constructor. use DatabaseAdapter.getInstance(Context
     * context) instead
     */
    private DatabaseAdapter(Context context) {
        this.m_Context = context;
        m_DatabaseHelper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * double checked locking
     */
    public static DatabaseAdapter getInstance(Context context) {
        DatabaseAdapter adapter = mAdapter;
        if (adapter == null) {
            synchronized (DatabaseAdapter.class) {
                adapter = mAdapter;
                if (adapter == null) {
                    mAdapter = adapter = new DatabaseAdapter(context);
                }
            }
        }
        return adapter;
    }

//    public DatabaseAdapter open() throws SQLException {
//        db = m_DatabaseHelper.getWritableDatabase();
//        LOG.V(TAG, "open() - database ready db = " + db);
//        return this;
//    }
//
//    public void close() {
//        LOG.V(TAG, "close() - database ready db = " + db);
//        m_DatabaseHelper.close();
//    }

    public void createUserLoginInfo(UserLoginInfoBean userLoginInfoBean) {
        try {

//            long count = DatabaseUtils.queryNumEntries(
//                    db,
//                    DatabaseHelper.USER_LOGIN_INFO_TABLE,
//                    UserLoginInfoBean.IDENTITY_ID + "= ?",
//                    new String[]{userLoginInfoBean.getIdentityId()});
//
//            LOG.V(TAG, "createUserLoginInfo() - count = " + count);
//            if (count > 0) {
//                LOG.V(TAG, "createUserLoginInfo() - data duplicatetd");
//                return;
//            }

            ContentValues values = new ContentValues();
            values.put(UserLoginInfoBean.EMPLOYEE_ID, userLoginInfoBean.getEmployeeId());
            values.put(UserLoginInfoBean.CLIENT_NAME, userLoginInfoBean.getClientName());
            values.put(UserLoginInfoBean.PNG_INFO_1, userLoginInfoBean.getPngInfo1());
            values.put(UserLoginInfoBean.PNG_INFO_2, userLoginInfoBean.getPngInfo2());
            values.put(UserLoginInfoBean.PNG_INFO_3, userLoginInfoBean.getPngInfo3());
            values.put(UserLoginInfoBean.PNG_INFO_4, userLoginInfoBean.getPngInfo4());
            values.put(UserLoginInfoBean.PNG_INFO_5, userLoginInfoBean.getPngInfo5());
            values.put(UserLoginInfoBean.FACE_VERIFY, userLoginInfoBean.getFaceVerify());
            values.put(UserLoginInfoBean.LOGIN_TIME, userLoginInfoBean.getLoginTime());
            values.put(UserLoginInfoBean.CLOCK_TYPE, userLoginInfoBean.getClockType());

            LOG.V(TAG, "createUserLoginInfo() - userLoginInfoBean.getEmployeeId() = " + userLoginInfoBean.getEmployeeId());
            LOG.V(TAG, "createUserLoginInfo() - userLoginInfoBean.getPngInfo1() = " + userLoginInfoBean.getPngInfo1());

            db.insert(DatabaseHelper.USER_LOGIN_INFO_TABLE, null, values);

        } catch (Throwable tr) {
            LOG.E(TAG, "createUserLoginInfo() - failed.", tr);
        }
    }


    public UserLoginInfoBean[] getAllUserLoginInfo() {
        LOG.V(TAG, "getAllUserLoginInfo");
        UserLoginInfoBean[] bean = null;

        Cursor c;

        c = db.query(DatabaseHelper.USER_LOGIN_INFO_TABLE, null, null, null, null, null, null);

        LOG.D(TAG, "getAllUserLoginInfo c.getCount() = " + c.getCount());
        if (c.getCount() > 0) {
            try {
                bean = new UserLoginInfoBean[c.getCount()];

                if (c.moveToFirst()) {
                    LOG.V(TAG, "getAllUserLoginInfo cccc");
                    bean = createUserLoginInfoBeanFromCursor(c);
                }
            } catch (Throwable tr) {
                LOG.E(TAG, "getAllUserLoginInfo() - failed.", tr);
            } finally {
                c.close();
            }

            return bean;
        } else {
            return null;
        }

    }

    public void deleteAllUserLoginInfo() {

        try {
            db.delete(DatabaseHelper.USER_LOGIN_INFO_TABLE,
                    null, null);
        } catch (Throwable tr) {
            LOG.E(TAG, "deleteMusicRecordByMemberIdSongId() - failed.", tr);
        }

    }

    private UserLoginInfoBean[] createUserLoginInfoBeanFromCursor(Cursor c) {
        LOG.D(TAG, "createUserLoginInfoBeanFromCursor c = " + c);
        LOG.D(TAG, "createUserLoginInfoBeanFromCursor c.getCount() = " + c.getCount());
        UserLoginInfoBean[] bean = new UserLoginInfoBean[c.getCount()];
        LOG.D(TAG, "createUserLoginInfoBeanFromCursor c.getString(c.getColumnIndexOrThrow(UserLoginInfoBean.EMPLOYEE_ID)) = " +
                c.getString(c.getColumnIndexOrThrow(UserLoginInfoBean.EMPLOYEE_ID)));
        int i = 0;
        do {
            bean[i] = new UserLoginInfoBean();

            bean[i].setEmployeeId(c.getString(c.getColumnIndexOrThrow(UserLoginInfoBean.EMPLOYEE_ID)));
            bean[i].setClientName(c.getString(c.getColumnIndexOrThrow(UserLoginInfoBean.CLIENT_NAME)));
            bean[i].setPngInfo1(c.getBlob(c.getColumnIndexOrThrow(UserLoginInfoBean.PNG_INFO_1)));
            bean[i].setPngInfo2(c.getBlob(c.getColumnIndexOrThrow(UserLoginInfoBean.PNG_INFO_2)));
            bean[i].setPngInfo3(c.getBlob(c.getColumnIndexOrThrow(UserLoginInfoBean.PNG_INFO_3)));
            bean[i].setPngInfo4(c.getBlob(c.getColumnIndexOrThrow(UserLoginInfoBean.PNG_INFO_4)));
            bean[i].setPngInfo5(c.getBlob(c.getColumnIndexOrThrow(UserLoginInfoBean.PNG_INFO_5)));
            bean[i].setFaceVerify(c.getString(c.getColumnIndexOrThrow(UserLoginInfoBean.FACE_VERIFY)));
            bean[i].setLoginTime(c.getString(c.getColumnIndexOrThrow(UserLoginInfoBean.LOGIN_TIME)));
            bean[i].setClockType(c.getString(c.getColumnIndexOrThrow(UserLoginInfoBean.CLOCK_TYPE)));
            i++;
        } while (c.moveToNext());

        return bean;
    }


    //***************************************************  USER_CLOCK_TABLE START *********************************************************

//    public void createUserClock(UserClockBean userClockBean) {
//        try {
//
////            long count = DatabaseUtils.queryNumEntries(
////                    db,
////                    DatabaseHelper.USER_LOGIN_INFO_TABLE,
////                    UserLoginInfoBean.IDENTITY_ID + "= ?",
////                    new String[]{userLoginInfoBean.getIdentityId()});
////
////            LOG.V(TAG, "createUserLoginInfo() - count = " + count);
////            if (count > 0) {
////                LOG.V(TAG, "createUserLoginInfo() - data duplicatetd");
////                return;
////            }
//
//            ContentValues values = new ContentValues();
//            values.put(UserClockBean.CLIENT_NAME, userClockBean.getClientName());
//            values.put(UserClockBean.SERIAL, userClockBean.getSerial());
//            values.put(UserClockBean.ID, userClockBean.getId());
//            values.put(UserClockBean.SECURITY_CODE, userClockBean.getSecurityCode());
//            values.put(UserClockBean.TYPE, userClockBean.getType());
//            values.put(UserClockBean.PNG_INFO_1, userClockBean.getPngInfo1());
//            values.put(UserClockBean.PNG_INFO_2, userClockBean.getPngInfo2());
//            values.put(UserClockBean.PNG_INFO_3, userClockBean.getPngInfo3());
//            values.put(UserClockBean.PNG_INFO_4, userClockBean.getPngInfo4());
//            values.put(UserClockBean.PNG_INFO_5, userClockBean.getPngInfo5());
//            values.put(UserClockBean.FACE_VERIFY, userClockBean.getFaceVerify());
//            values.put(UserClockBean.CLIENT_TIME, userClockBean.getClientTime());
//            values.put(UserClockBean.CLOCK_TYPE, userClockBean.getClockType());
//            values.put(UserClockBean.LIVENESS, userClockBean.getLiveness());
//            values.put(UserClockBean.MODE, userClockBean.getMode());
//            values.put(UserClockBean.MODULE, userClockBean.getModule());
//
//            values.put(UserClockBean.RFID, userClockBean.getRfid());
//            values.put(UserClockBean.RECORD_MODE, userClockBean.getRecordMode());
//
//            LOG.V(TAG, "createUserClock() - userClockBean.getClientName() = " + userClockBean.getClientName());
//            LOG.V(TAG, "createUserClock() - userClockBean.getMode() = " + userClockBean.getMode());
//            LOG.V(TAG, "createUserClock() - userClockBean.getClockType() = " + userClockBean.getClockType());
//            LOG.V(TAG, "createUserClock() - userClockBean.getType() = " + userClockBean.getType());
//
//            db.insert(DatabaseHelper.USER_CLOCK_TABLE, null, values);
//
//        } catch (Throwable tr) {
//            LOG.E(TAG, "createUserClock() - failed.", tr);
//        }
//    }


    public void createUserClock(UserClockBean userClockBean) {


        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {

                    ContentValues values = new ContentValues();
                    values.put(UserClockBean.CLIENT_NAME, userClockBean.getClientName());
                    values.put(UserClockBean.SERIAL, userClockBean.getSerial());
                    values.put(UserClockBean.ID, userClockBean.getId());
                    values.put(UserClockBean.SECURITY_CODE, userClockBean.getSecurityCode());
                    values.put(UserClockBean.TYPE, userClockBean.getType());
                    values.put(UserClockBean.PNG_INFO_1, userClockBean.getPngInfo1());
                    values.put(UserClockBean.PNG_INFO_2, userClockBean.getPngInfo2());
                    values.put(UserClockBean.PNG_INFO_3, userClockBean.getPngInfo3());
                    values.put(UserClockBean.PNG_INFO_4, userClockBean.getPngInfo4());
                    values.put(UserClockBean.PNG_INFO_5, userClockBean.getPngInfo5());
                    values.put(UserClockBean.FACE_VERIFY, userClockBean.getFaceVerify());
                    values.put(UserClockBean.CLIENT_TIME, userClockBean.getClientTime());
                    values.put(UserClockBean.CLOCK_TYPE, userClockBean.getClockType());
                    values.put(UserClockBean.LIVENESS, userClockBean.getLiveness());
                    values.put(UserClockBean.MODE, userClockBean.getMode());
                    values.put(UserClockBean.MODULE, userClockBean.getModule());

                    values.put(UserClockBean.RFID, userClockBean.getRfid());
                    values.put(UserClockBean.RECORD_MODE, userClockBean.getRecordMode());
                    values.put(UserClockBean.IS_EMPLOYEE_OPEN_DOOR, userClockBean.getIsEmployeeOpenDoor() ? 1 : 0);
                    values.put(UserClockBean.IS_VISITOR_OPEN_DOOR, userClockBean.getIsVisitorOpenDoor() ? 1 : 0);

                    LOG.V(TAG, "createUserClock() - userClockBean.getClientName() = " + userClockBean.getClientName());
                    LOG.V(TAG, "createUserClock() - userClockBean.getMode() = " + userClockBean.getMode());
                    LOG.V(TAG, "createUserClock() - userClockBean.getModule() = " + userClockBean.getModule());
                    LOG.V(TAG, "createUserClock() - userClockBean.getClockType() = " + userClockBean.getClockType());
                    LOG.V(TAG, "createUserClock() - userClockBean.getType() = " + userClockBean.getType());

                    db.insert(DatabaseHelper.USER_CLOCK_TABLE, null, values);

                }

            } catch (Throwable tr) {
                LOG.E(TAG, "createUserClock() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }

    }

    public UserClockBean[] getAllUserClock() {
        LOG.V(TAG, "getAllUserClock");
        UserClockBean[] bean = null;

        Cursor c;

        c = db.query(DatabaseHelper.USER_CLOCK_TABLE, null, null, null, null, null, null);

        LOG.D(TAG, "getAllUserClock c.getCount() = " + c.getCount());
        if (c.getCount() > 0) {
            try {
                bean = new UserClockBean[c.getCount()];

                if (c.moveToFirst()) {
                    LOG.V(TAG, "getAllUserClock cccc");
                    bean = createUserClockBeanFromCursor(c);
                }
            } catch (Throwable tr) {
                LOG.E(TAG, "getAllUserClock() - failed.", tr);
            } finally {
                c.close();
            }

            return bean;
        } else {
            return null;
        }

    }

    public ArrayList<UserClockBean> getUserClockByModule(int module) {
        LOG.D(TAG, "getUserClockByModule(), module =" + module);

        //SQLiteDatabase db = null;
//        UserClockBean bean = null;
        ArrayList<UserClockBean> bean = null;
        Cursor c;
        synchronized (LOCK) {
            try {

                db = m_DatabaseHelper.getWritableDatabase();

                synchronized (db) {
                    c = db.query(DatabaseHelper.USER_CLOCK_TABLE, null,
                            UserClockBean.MODULE + "= ?",
                            new String[]{String.valueOf(module)}, null, null, null);
                }


                if (c != null) {
                    if (c.getCount() > 0) {
                        try {

                            bean = new ArrayList<>();
                            c.moveToFirst();
                            do {
                                LOG.V(TAG, "getUserClockByModule cccc");
                                bean.add(createUserClockBeanFromSingleCursor(c));
                            } while (c.moveToNext());


                        } catch (Throwable tr) {
                            LOG.E(TAG, "getUserClockByModule() - failed.", tr);
                        } finally {
                            c.close();
                        }
                    }
                }

                c.close();

            } catch (Throwable tr) {
                LOG.E(TAG, "getUserClockByModule() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }
        return bean;
    }

    public ArrayList<UserClockBean> getUserClockByModuleRecordMode(int module, String recordMode) {
        LOG.D(TAG, "getUserClockByModuleRecordMode(), module =" + module + " recordMode =" + recordMode);

        //SQLiteDatabase db = null;
//        UserClockBean bean = null;
        ArrayList<UserClockBean> bean = null;
        Cursor c = null;
        synchronized (LOCK) {
            try {

                db = m_DatabaseHelper.getWritableDatabase();

                synchronized (db) {
                    c = db.query(DatabaseHelper.USER_CLOCK_TABLE, null,
                            "(" + UserClockBean.MODULE + "= ? and " + UserClockBean.RECORD_MODE + "= ?" + ") or (" +
                                    UserClockBean.MODULE + "= ? and " + UserClockBean.RECORD_MODE + "= ?)",
                            new String[]{String.valueOf(module), recordMode, String.valueOf(Constants.MODULES_ATTENDANCE_ACCESS), recordMode}, null, null, null);
                }

                LOG.D(TAG, "getUserClockByModuleRecordMode(), c =" + c);

                if (c != null) {
                    LOG.D(TAG, "getUserClockByModuleRecordMode(), c.getCount() =" + c.getCount());
                    if (c.getCount() > 0) {
                        try {

                            bean = new ArrayList<>();
                            c.moveToFirst();
                            do {
                                LOG.V(TAG, "getUserClockByModuleRecordMode cccc");
                                bean.add(createUserClockBeanFromSingleCursor(c));
                            } while (c.moveToNext());


                        } catch (Throwable tr) {
                            LOG.E(TAG, "getUserClockByModuleRecordMode() - failed.", tr);
                        } finally {
                            c.close();
                        }
                    }
                }

                c.close();

            } catch (Throwable tr) {
                LOG.E(TAG, "getUserClockByModuleRecordMode() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }
        return bean;
    }

    public ArrayList<UserClockBean> getUserClockByModuleRecordMode(int module, String recordMode, int moduleSecond) {
        LOG.D(TAG, "getUserClockByModuleRecordMode(), module =" + module + " recordMode =" + recordMode);

        //SQLiteDatabase db = null;
//        UserClockBean bean = null;
        ArrayList<UserClockBean> bean = null;
        Cursor c = null;
        synchronized (LOCK) {
            try {

                db = m_DatabaseHelper.getWritableDatabase();

//                synchronized (db) {
                c = db.query(DatabaseHelper.USER_CLOCK_TABLE, null,
                        "(" + UserClockBean.MODULE + "= ? and " + UserClockBean.RECORD_MODE + "= ?" + ") or (" +
                                UserClockBean.MODULE + "= ? and " + UserClockBean.RECORD_MODE + "= ?)",
                        new String[]{String.valueOf(module), recordMode, String.valueOf(moduleSecond), recordMode}, null, null, null);
//                }

                LOG.D(TAG, "getUserClockByModuleRecordMode(), c =" + c);

                if (c != null) {
                    LOG.D(TAG, "getUserClockByModuleRecordMode(), c.getCount() =" + c.getCount());
                    if (c.getCount() > 0) {
                        try {

                            bean = new ArrayList<>();
                            c.moveToFirst();
                            do {
                                LOG.V(TAG, "getUserClockByModuleRecordMode cccc");
                                bean.add(createUserClockBeanFromSingleCursor(c));
                            } while (c.moveToNext());


                        } catch (Throwable tr) {
                            LOG.E(TAG, "getUserClockByModuleRecordMode() - failed.", tr);
                        } finally {
                            c.close();
                        }
                    }
                }

                c.close();

            } catch (Throwable tr) {
                LOG.E(TAG, "getUserClockByModuleRecordMode() - failed.", tr);
            } finally {
//                if (db != null && db.isOpen())
//                    db.close();
            }
        }
        return bean;
    }


    public void deleteAllUserClock() {

        try {
            db.delete(DatabaseHelper.USER_CLOCK_TABLE,
                    null, null);
        } catch (Throwable tr) {
            LOG.E(TAG, "deleteAllUserClock() - failed.", tr);
        }

    }

    public synchronized void deleteUserClockByModule(int module) {

        try {
            db.delete(DatabaseHelper.USER_CLOCK_TABLE,
                    UserClockBean.MODULE + "= ?",
                    new String[]{String.valueOf(module)});
        } catch (Throwable tr) {
            LOG.E(TAG, "deleteUserClockByModule() - failed.", tr);
        }

    }

    public synchronized void deleteUserClockByModuleIdRecordMode(int module, String recordMode) {

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    db.delete(DatabaseHelper.USER_CLOCK_TABLE,
                            UserClockBean.MODULE + "= ? and " + UserClockBean.RECORD_MODE + "= ?",
                            new String[]{String.valueOf(module), recordMode});
                }
            } catch (Throwable tr) {
                LOG.E(TAG, "deleteUserClockByModuleIdRecordMode() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }


    }

    public synchronized void deleteUserClockByModuleIdRecordModeVisitorOpenDoor(int module, String recordMode, boolean isVisitorOpenDoor) {

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                int visitorOpenDoor = (isVisitorOpenDoor) ? 1 : 0;
                synchronized (db) {
                    db.delete(DatabaseHelper.USER_CLOCK_TABLE,
                            UserClockBean.MODULE + "= ? and " + UserClockBean.RECORD_MODE + "= ? and " + UserClockBean.IS_VISITOR_OPEN_DOOR + "= ?",
                            new String[]{String.valueOf(module), recordMode, String.valueOf(visitorOpenDoor)});
                }
            } catch (Throwable tr) {
                LOG.E(TAG, "deleteUserClockByModuleIdRecordModeVisitorOpenDoor() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }


    }

    public synchronized void deleteUserClockByIdAndTime(String id, long createTime) {

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    db.delete(DatabaseHelper.USER_CLOCK_TABLE,
                            UserClockBean.ID + "= ? and " + UserClockBean.CLIENT_TIME + "= ?",
                            new String[]{id, String.valueOf(createTime)});
                }
            } catch (Throwable tr) {
                LOG.D(TAG, "deleteUserClockByIdAndTime() - failed." + tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }


    }

    public synchronized void deleteUserClockBySecurityCodeAndTime(String securityCode, long createTime) {

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    db.delete(DatabaseHelper.USER_CLOCK_TABLE,
                            UserClockBean.SECURITY_CODE + "= ? and " + UserClockBean.CLIENT_TIME + "= ?",
                            new String[]{securityCode, String.valueOf(createTime)});
                }
            } catch (Throwable tr) {
                LOG.D(TAG, "deleteUserClockBySecurityCodeAndTime() - failed." + tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }


    }

    public synchronized void deleteUserClockByRfidAndTime(String Rfid, long createTime) {

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    db.delete(DatabaseHelper.USER_CLOCK_TABLE,
                            UserClockBean.RFID + "= ? and " + UserClockBean.CLIENT_TIME + "= ?",
                            new String[]{Rfid, String.valueOf(createTime)});
                }
            } catch (Throwable tr) {
                LOG.D(TAG, "deleteUserClockBySecurityCodeAndTime() - failed." + tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }


    }

    public synchronized void deleteUserClockByTime(long createTime) {

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    db.delete(DatabaseHelper.USER_CLOCK_TABLE,
                            UserClockBean.CLIENT_TIME + "= ?",
                            new String[]{String.valueOf(createTime)});
                }
            } catch (Throwable tr) {
                LOG.D(TAG, "deleteUserClockBySecurityCodeAndTime() - failed." + tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }

    }


    public synchronized void deleteEmployeeRegisterByEmployeeId(String employeeId) {

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    db.delete(DatabaseHelper.EMPLOYEES_REGISTER_TABLE,
                            RegisterBean.EMPLOYEE_ID + "= ? ",
                            new String[]{employeeId});
                }
            } catch (Throwable tr) {
                LOG.E(TAG, "deleteEmployeeRegisterByEmployeeId() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }


    }

    public synchronized void deleteVisitorRegisterByMobileNo(String mobileNo) {

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    db.delete(DatabaseHelper.VISITORS_REGISTER_TABLE,
                            RegisterBean.MOBILE_NO + "= ? ",
                            new String[]{mobileNo});
                }
            } catch (Throwable tr) {
                LOG.E(TAG, "deleteVisitorRegisterByMobileNo() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }


    }


    private UserClockBean[] createUserClockBeanFromCursor(Cursor c) {
        LOG.D(TAG, "createUserClockBeanFromCursor c = " + c);
        LOG.D(TAG, "createUserClockBeanFromCursor c.getCount() = " + c.getCount());
        UserClockBean[] bean = new UserClockBean[c.getCount()];
        LOG.D(TAG, "createUserClockBeanFromCursor c.getString(c.getColumnIndexOrThrow(UserLoginInfoBean.CLIENT_NAME)) = " +
                c.getString(c.getColumnIndexOrThrow(UserClockBean.CLIENT_NAME)));
        int i = 0;
        do {
            bean[i] = new UserClockBean();

            bean[i].setClientName(c.getString(c.getColumnIndexOrThrow(UserClockBean.CLIENT_NAME)));
            bean[i].setSerial(c.getInt(c.getColumnIndexOrThrow(UserClockBean.SERIAL)));
            bean[i].setId(c.getString(c.getColumnIndexOrThrow(UserClockBean.ID)));
            bean[i].setSecurityCode(c.getString(c.getColumnIndexOrThrow(UserClockBean.SECURITY_CODE)));
            bean[i].setType(c.getString(c.getColumnIndexOrThrow(UserClockBean.TYPE)));
            bean[i].setPngInfo1(c.getBlob(c.getColumnIndexOrThrow(UserClockBean.PNG_INFO_1)));
            bean[i].setPngInfo2(c.getBlob(c.getColumnIndexOrThrow(UserClockBean.PNG_INFO_2)));
            bean[i].setPngInfo3(c.getBlob(c.getColumnIndexOrThrow(UserClockBean.PNG_INFO_3)));
            bean[i].setPngInfo4(c.getBlob(c.getColumnIndexOrThrow(UserClockBean.PNG_INFO_4)));
            bean[i].setPngInfo5(c.getBlob(c.getColumnIndexOrThrow(UserClockBean.PNG_INFO_5)));
            bean[i].setFaceVerify(c.getString(c.getColumnIndexOrThrow(UserClockBean.FACE_VERIFY)));
            bean[i].setClientTime(c.getLong(c.getColumnIndexOrThrow(UserClockBean.CLIENT_TIME)));
            bean[i].setClockType(c.getString(c.getColumnIndexOrThrow(UserClockBean.CLOCK_TYPE)));
            bean[i].setLiveness(c.getString(c.getColumnIndexOrThrow(UserClockBean.LIVENESS)));
            bean[i].setMode(c.getInt(c.getColumnIndexOrThrow(UserClockBean.MODE)));
            bean[i].setModule(c.getInt(c.getColumnIndexOrThrow(UserClockBean.MODULE)));
            bean[i].setRfid(c.getString(c.getColumnIndexOrThrow(UserClockBean.RFID)));
            bean[i].setRecordMode(c.getString(c.getColumnIndexOrThrow(UserClockBean.RECORD_MODE)));
            bean[i].setIsEmployeeOpenDoor(c.getInt(c.getColumnIndexOrThrow(UserClockBean.IS_EMPLOYEE_OPEN_DOOR)) == 1);
            bean[i].setIsVisitorOpenDoor(c.getInt(c.getColumnIndexOrThrow(UserClockBean.IS_VISITOR_OPEN_DOOR)) == 1);
            i++;
        } while (c.moveToNext());

        return bean;
    }

    private UserClockBean createUserClockBeanFromSingleCursor(Cursor c) {
        LOG.D(TAG, "createUserClockBeanFromSingleCursor c = " + c);
        LOG.D(TAG, "createUserClockBeanFromSingleCursor c.getCount() = " + c.getCount());
        UserClockBean bean = new UserClockBean();
//        bean = new VerifiedFaceBean();
        bean.setClientName(c.getString(c.getColumnIndexOrThrow(UserClockBean.CLIENT_NAME)));
        bean.setSerial(c.getInt(c.getColumnIndexOrThrow(UserClockBean.SERIAL)));
        bean.setId(c.getString(c.getColumnIndexOrThrow(UserClockBean.ID)));
        bean.setSecurityCode(c.getString(c.getColumnIndexOrThrow(UserClockBean.SECURITY_CODE)));
        bean.setType(c.getString(c.getColumnIndexOrThrow(UserClockBean.TYPE)));
        bean.setPngInfo1(c.getBlob(c.getColumnIndexOrThrow(UserClockBean.PNG_INFO_1)));
        bean.setPngInfo2(c.getBlob(c.getColumnIndexOrThrow(UserClockBean.PNG_INFO_2)));
        bean.setPngInfo3(c.getBlob(c.getColumnIndexOrThrow(UserClockBean.PNG_INFO_3)));
        bean.setPngInfo4(c.getBlob(c.getColumnIndexOrThrow(UserClockBean.PNG_INFO_4)));
        bean.setPngInfo5(c.getBlob(c.getColumnIndexOrThrow(UserClockBean.PNG_INFO_5)));
        bean.setFaceVerify(c.getString(c.getColumnIndexOrThrow(UserClockBean.FACE_VERIFY)));
        bean.setClientTime(c.getLong(c.getColumnIndexOrThrow(UserClockBean.CLIENT_TIME)));
        bean.setClockType(c.getString(c.getColumnIndexOrThrow(UserClockBean.CLOCK_TYPE)));
        bean.setLiveness(c.getString(c.getColumnIndexOrThrow(UserClockBean.LIVENESS)));
        bean.setMode(c.getInt(c.getColumnIndexOrThrow(UserClockBean.MODE)));
        bean.setModule(c.getInt(c.getColumnIndexOrThrow(UserClockBean.MODULE)));
        bean.setRfid(c.getString(c.getColumnIndexOrThrow(UserClockBean.RFID)));
        bean.setRecordMode(c.getString(c.getColumnIndexOrThrow(UserClockBean.RECORD_MODE)));
        bean.setIsEmployeeOpenDoor(c.getInt(c.getColumnIndexOrThrow(UserClockBean.IS_EMPLOYEE_OPEN_DOOR)) == 1);
        bean.setIsVisitorOpenDoor(c.getInt(c.getColumnIndexOrThrow(UserClockBean.IS_VISITOR_OPEN_DOOR)) == 1);

        return bean;
    }


    //***************************************************  USER_CLOCK_TABLE END ***********************************************************


    //Verified face DB

//    public void createEmployeeIdentitiesFace(VerifiedFaceBean verifiedFaceBean) {
//        try {
//
//            ContentValues values = new ContentValues();
//            values.put(VerifiedFaceBean.BAP_MODEL_ID, verifiedFaceBean.getBapModelId());
//            values.put(VerifiedFaceBean.ID, verifiedFaceBean.getId());
//            values.put(VerifiedFaceBean.SECURITY_CODE, verifiedFaceBean.getSecurityCode());
//            values.put(VerifiedFaceBean.EMPLOYEE_ID, verifiedFaceBean.getEmployeeId());
//            values.put(VerifiedFaceBean.EMPLOYEE_NAME, verifiedFaceBean.getEmployeeName());
//            values.put(VerifiedFaceBean.VISITOR_NAME, verifiedFaceBean.getVisitorName());
//            values.put(VerifiedFaceBean.CREATED_TIME, verifiedFaceBean.getCreatedTime());
//            values.put(VerifiedFaceBean.MODEL, verifiedFaceBean.getModel());
//
//            LOG.V(TAG, "createEmployeeIdentitiesFace() - verifiedFaceBean.getEmployeeId() = " + verifiedFaceBean.getEmployeeId());
//            LOG.V(TAG, "createEmployeeIdentitiesFace() - verifiedFaceBean.getCreatedTime() = " + verifiedFaceBean.getCreatedTime());
//
//            db.insert(DatabaseHelper.EMPLOYEE_IDENTITIES_TABLE, null, values);
//
//        } catch (Throwable tr) {
//            LOG.E(TAG, "createEmployeeIdentitiesFace() - failed.", tr);
//        }
//    }

    public void createEmployeeIdentitiesFace(VerifiedFaceBean verifiedFaceBean) {
        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    ContentValues values = new ContentValues();
                    values.put(VerifiedFaceBean.BAP_MODEL_ID, verifiedFaceBean.getBapModelId());
                    values.put(VerifiedFaceBean.ID, verifiedFaceBean.getId());
                    values.put(VerifiedFaceBean.SECURITY_CODE, verifiedFaceBean.getSecurityCode());
                    values.put(VerifiedFaceBean.EMPLOYEE_ID, verifiedFaceBean.getEmployeeId());
                    values.put(VerifiedFaceBean.EMPLOYEE_NAME, verifiedFaceBean.getEmployeeName());
                    values.put(VerifiedFaceBean.VISITOR_NAME, verifiedFaceBean.getVisitorName());
                    values.put(VerifiedFaceBean.CREATED_TIME, verifiedFaceBean.getCreatedTime());
                    values.put(VerifiedFaceBean.MODEL, verifiedFaceBean.getModel());

                    LOG.V(TAG, "createEmployeeIdentitiesFace() - verifiedFaceBean.getEmployeeId() = " + verifiedFaceBean.getEmployeeId());
                    LOG.V(TAG, "createEmployeeIdentitiesFace() - verifiedFaceBean.getCreatedTime() = " + verifiedFaceBean.getCreatedTime());

                    db.insert(DatabaseHelper.EMPLOYEE_IDENTITIES_TABLE, null, values);
                }

            } catch (Throwable tr) {
                LOG.E(TAG, "getUserClockByModuleRecordMode() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }

    }

//    public void createVisitorIdentitiesFace(VerifiedFaceBean verifiedFaceBean) {
//        try {
//
//            ContentValues values = new ContentValues();
//            values.put(VerifiedFaceBean.BAP_MODEL_ID, verifiedFaceBean.getBapModelId());
//            values.put(VerifiedFaceBean.ID, verifiedFaceBean.getId());
//            values.put(VerifiedFaceBean.SECURITY_CODE, verifiedFaceBean.getSecurityCode());
//            values.put(VerifiedFaceBean.EMPLOYEE_ID, verifiedFaceBean.getEmployeeId());
//            values.put(VerifiedFaceBean.EMPLOYEE_NAME, verifiedFaceBean.getEmployeeName());
//            values.put(VerifiedFaceBean.VISITOR_NAME, verifiedFaceBean.getVisitorName());
//            values.put(VerifiedFaceBean.CREATED_TIME, verifiedFaceBean.getCreatedTime());
//            values.put(VerifiedFaceBean.MODEL, verifiedFaceBean.getModel());
//
//            LOG.V(TAG, "createVisitorIdentitiesFace() - verifiedFaceBean.getEmployeeId() = " + verifiedFaceBean.getEmployeeId());
//            LOG.V(TAG, "createVisitorIdentitiesFace() - verifiedFaceBean.getCreatedTime() = " + verifiedFaceBean.getCreatedTime());
//
//            db.insert(DatabaseHelper.VISITOR_IDENTITIES_TABLE, null, values);
//
//        } catch (Throwable tr) {
//            LOG.E(TAG, "createVisitorIdentitiesFace() - failed.", tr);
//        }
//    }

    public void createVisitorIdentitiesFace(VerifiedFaceBean verifiedFaceBean) {

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    ContentValues values = new ContentValues();
                    values.put(VerifiedFaceBean.BAP_MODEL_ID, verifiedFaceBean.getBapModelId());
                    values.put(VerifiedFaceBean.ID, verifiedFaceBean.getId());
                    values.put(VerifiedFaceBean.SECURITY_CODE, verifiedFaceBean.getSecurityCode());
                    values.put(VerifiedFaceBean.EMPLOYEE_ID, verifiedFaceBean.getEmployeeId());
                    values.put(VerifiedFaceBean.EMPLOYEE_NAME, verifiedFaceBean.getEmployeeName());
                    values.put(VerifiedFaceBean.VISITOR_NAME, verifiedFaceBean.getVisitorName());
                    values.put(VerifiedFaceBean.CREATED_TIME, verifiedFaceBean.getCreatedTime());
                    values.put(VerifiedFaceBean.MODEL, verifiedFaceBean.getModel());

                    LOG.V(TAG, "createVisitorIdentitiesFace() - verifiedFaceBean.getEmployeeId() = " + verifiedFaceBean.getEmployeeId());
                    LOG.V(TAG, "createVisitorIdentitiesFace() - verifiedFaceBean.getCreatedTime() = " + verifiedFaceBean.getCreatedTime());

                    db.insert(DatabaseHelper.VISITOR_IDENTITIES_TABLE, null, values);
                }

            } catch (Throwable tr) {
                LOG.E(TAG, "getUserClockByModuleRecordMode() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }

    }

//    public ArrayList<VerifiedFaceBean> getAllEmployeeIdentitiesArrayList() {
//        LOG.V(TAG, "getAllEmployeeIdentitiesArrayList");
//        ArrayList<VerifiedFaceBean> bean = null;
//
//
//        Cursor c;
////        synchronized (LOCK) {
////            try {
////                db = m_DatabaseHelper.getWritableDatabase();
////
////                c = db.query(DatabaseHelper.EMPLOYEE_IDENTITIES_TABLE, null, null, null, null, null, null);
////
////                LOG.D(TAG,"getAllEmployeeIdentitiesArrayList c.getCount() = " + c.getCount());
////                if(c.getCount() > 0){
////                    bean = new ArrayList<>();
////                    c.moveToFirst();
////                    do{
////                        LOG.V(TAG, "getAllEmployeeIdentitiesArrayList cccc");
////                        bean.add(createVerifiedFaceBeanFromSingleCursor(c));
////                    }while(c.moveToNext());
////
////                }
////                c.close();
////
////            } catch (Throwable tr) {
////                LOG.E(TAG, "getAllVisitorIdentitiesArrayList() - failed.", tr);
////            } finally {
////                if (db != null && db.isOpen())
////                    db.close();
////            }
////
////            return bean;
////        }
//
//
//
//        c = db.query(DatabaseHelper.EMPLOYEE_IDENTITIES_TABLE, null, null, null, null, null, null);
//
//        LOG.D(TAG,"getAllEmployeeIdentitiesArrayList c.getCount() = " + c.getCount());
//        if(c.getCount() > 0){
//            try {
////                if (c.moveToFirst()) {
////
////                }
//                bean = new ArrayList<>();
//                c.moveToFirst();
//                do{
//                    LOG.V(TAG, "getAllEmployeeIdentitiesArrayList cccc");
//                    bean.add(createVerifiedFaceBeanFromSingleCursor(c));
//                }while(c.moveToNext());
//
//
//            } catch (Throwable tr) {
//                LOG.E(TAG, "getAllEmployeeIdentitiesArrayList() - failed.", tr);
//            } finally {
//                c.close();
//            }
//
//            return bean;
//        }else{
//            return null;
//        }
//
//    }


    public ArrayList<VerifiedFaceBean> getAllEmployeeIdentitiesArrayList() {
        LOG.V(TAG, "getAllEmployeeIdentitiesArrayList");
        ArrayList<VerifiedFaceBean> bean = null;


        Cursor c;

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    c = db.query(DatabaseHelper.EMPLOYEE_IDENTITIES_TABLE, null, null, null, null, null, null);
                }

                LOG.D(TAG, "getAllEmployeeIdentitiesArrayList c.getCount() = " + c.getCount());
                if (c.getCount() > 0) {
                    try {
//                if (c.moveToFirst()) {
//
//                }
                        bean = new ArrayList<>();
                        c.moveToFirst();
                        do {
                            LOG.V(TAG, "getAllEmployeeIdentitiesArrayList cccc");
                            bean.add(createVerifiedFaceBeanFromSingleCursor(c));
                        } while (c.moveToNext());


                    } catch (Throwable tr) {
                        LOG.E(TAG, "getAllEmployeeIdentitiesArrayList() - failed.", tr);
                    } finally {
                        c.close();
                    }

                }

            } catch (Throwable tr) {
                LOG.E(TAG, "getUserClockByModuleRecordMode() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }


        return bean;


    }

    public ArrayList<RegisterBean> getAllEmployeeRegisterArrayList() {
        LOG.V(TAG, "getAllEmployeeRegisterArrayList");
        ArrayList<RegisterBean> bean = null;


        Cursor c;

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    c = db.query(DatabaseHelper.EMPLOYEES_REGISTER_TABLE, null, null, null, null, null, null);
                }

                LOG.D(TAG, "getAllEmployeeRegisterArrayList c.getCount() = " + c.getCount());
                if (c.getCount() > 0) {
                    try {
//                if (c.moveToFirst()) {
//
//                }
                        bean = new ArrayList<>();
                        c.moveToFirst();
                        do {
                            LOG.V(TAG, "getAllEmployeeRegisterArrayList cccc");
                            bean.add(createEmployeeRegisterBeanFromSingleCursor(c));
                        } while (c.moveToNext());


                    } catch (Throwable tr) {
                        LOG.E(TAG, "getAllEmployeeRegisterArrayList() - failed.", tr);
                    } finally {
                        c.close();
                    }

                }

            } catch (Throwable tr) {
                LOG.E(TAG, "getAllEmployeeRegisterArrayList() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }


        return bean;


    }

    public ArrayList<RegisterBean> getAllVisitorRegisterArrayList() {
        LOG.V(TAG, "getAllVisitorRegisterArrayList");
        ArrayList<RegisterBean> bean = null;


        Cursor c;

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    c = db.query(DatabaseHelper.VISITORS_REGISTER_TABLE, null, null, null, null, null, null);
                }

                LOG.D(TAG, "getAllVisitorRegisterArrayList c.getCount() = " + c.getCount());
                if (c.getCount() > 0) {
                    try {
//                if (c.moveToFirst()) {
//
//                }
                        bean = new ArrayList<>();
                        c.moveToFirst();
                        do {
                            LOG.V(TAG, "getAllVisitorRegisterArrayList cccc");
                            bean.add(createEmployeeRegisterBeanFromSingleCursor(c));
                        } while (c.moveToNext());


                    } catch (Throwable tr) {
                        LOG.E(TAG, "getAllVisitorRegisterArrayList() - failed.", tr);
                    } finally {
                        c.close();
                    }

                }

            } catch (Throwable tr) {
                LOG.E(TAG, "getAllVisitorRegisterArrayList() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }


        return bean;

    }


//    public ArrayList<VerifiedFaceBean> getAllVisitorIdentitiesArrayList() {
//        LOG.V(TAG, "getAllVisitorIdentitiesArrayList");
//        ArrayList<VerifiedFaceBean> bean = null;
//
//
//        Cursor c;
//
////        synchronized (LOCK) {
////            try {
////
////                db = m_DatabaseHelper.getWritableDatabase();
////                c = db.query(DatabaseHelper.VISITOR_IDENTITIES_TABLE, null, null, null, null, null, null);
////
////                LOG.D(TAG,"getAllVisitorIdentitiesArrayList c.getCount() = " + c.getCount());
////                if(c.getCount() > 0){
////                    bean = new ArrayList<>();
////                    c.moveToFirst();
////                    do{
////                        LOG.V(TAG, "getAllVisitorIdentitiesArrayList cccc");
////                        bean.add(createVerifiedFaceBeanFromSingleCursor(c));
////                    }while(c.moveToNext());
////
////                }
////                c.close();
////
////            } catch (Throwable tr) {
////                LOG.E(TAG, "getAllVisitorIdentitiesArrayList() - failed.", tr);
////            } finally {
////                if (db != null && db.isOpen())
////                    db.close();
////            }
////            return bean;
////
////        }
//
//
//            c = db.query(DatabaseHelper.VISITOR_IDENTITIES_TABLE, null, null, null, null, null, null);
//
//            LOG.D(TAG,"getAllVisitorIdentitiesArrayList c.getCount() = " + c.getCount());
//            if(c.getCount() > 0){
//                try {
////                if (c.moveToFirst()) {
////
////                }
//                    bean = new ArrayList<>();
//                    c.moveToFirst();
//                    do{
//                        LOG.V(TAG, "getAllVisitorIdentitiesArrayList cccc");
//                        bean.add(createVerifiedFaceBeanFromSingleCursor(c));
//                    }while(c.moveToNext());
//
//
//                } catch (Throwable tr) {
//                    LOG.E(TAG, "getAllVisitorIdentitiesArrayList() - failed.", tr);
//                } finally {
//                    c.close();
//                }
//
//                return bean;
//            }else{
//                return null;
//            }
//
//
//
//    }


    public ArrayList<VerifiedFaceBean> getAllVisitorIdentitiesArrayList() {
        LOG.V(TAG, "getAllVisitorIdentitiesArrayList");
        ArrayList<VerifiedFaceBean> bean = null;


        Cursor c;

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    c = db.query(DatabaseHelper.VISITOR_IDENTITIES_TABLE, null, null, null, null, null, null);
                }

                LOG.D(TAG, "getAllVisitorIdentitiesArrayList c.getCount() = " + c.getCount());
                if (c.getCount() > 0) {
                    try {
//                if (c.moveToFirst()) {
//
//                }
                        bean = new ArrayList<>();
                        c.moveToFirst();
                        do {
                            LOG.V(TAG, "getAllVisitorIdentitiesArrayList cccc");
                            bean.add(createVerifiedFaceBeanFromSingleCursor(c));
                        } while (c.moveToNext());


                    } catch (Throwable tr) {
                        LOG.E(TAG, "getAllVisitorIdentitiesArrayList() - failed.", tr);
                    } finally {
                        c.close();
                    }

                }

            } catch (Throwable tr) {
                LOG.E(TAG, "getUserClockByModuleRecordMode() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }


        return bean;

    }

    public VerifiedFaceBean getEmployeeIdentitiesByBapModelId(String bapModelId) {
        LOG.D(TAG, "getEmployeeIdentitiesByBapModelId(), bapModelId =" + bapModelId);

        //SQLiteDatabase db = null;
        VerifiedFaceBean bean = null;
        Cursor c;
        synchronized (LOCK) {
            try {

                db = m_DatabaseHelper.getWritableDatabase();

                synchronized (db) {
                    c = db.query(DatabaseHelper.EMPLOYEE_IDENTITIES_TABLE, null,
                            VerifiedFaceBean.BAP_MODEL_ID + "= ?",
                            new String[]{bapModelId}, null, null, null);
                }

                if (c.moveToFirst()) {
                    bean = createVerifiedFaceBeanFromSingleCursor(c);
                }

                c.close();

            } catch (Throwable tr) {
                LOG.E(TAG, "getEmployeeIdentitiesByBapModelId() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }
        return bean;
    }

    public VerifiedFaceBean getVisitorIdentitiesByBapModelId(String bapModelId) {
        LOG.D(TAG, "getVisitorIdentitiesByBapModelId(), bapModelId =" + bapModelId);

        //SQLiteDatabase db = null;
        VerifiedFaceBean bean = null;
        Cursor c;
        synchronized (LOCK) {
            try {

                db = m_DatabaseHelper.getWritableDatabase();

                synchronized (db) {
                    c = db.query(DatabaseHelper.VISITOR_IDENTITIES_TABLE, null,
                            VerifiedFaceBean.BAP_MODEL_ID + "= ?",
                            new String[]{bapModelId}, null, null, null);
                }

                if (c.moveToFirst()) {
                    bean = createVerifiedFaceBeanFromSingleCursor(c);
                }

                c.close();

            } catch (Throwable tr) {
                LOG.E(TAG, "getVisitorIdentitiesByBapModelId() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }
        return bean;
    }

    public String getVisitorNameByUuid(String loginUuid) {
        LOG.D(TAG, "getVisitorNameByUuid(), loginUuid =" + loginUuid);

        //SQLiteDatabase db = null;
        AcceptancesBean bean = null;
        Cursor c;
        synchronized (LOCK) {
            try {

                db = m_DatabaseHelper.getWritableDatabase();

                synchronized (db) {
                    c = db.query(DatabaseHelper.VISITORS_ACCEPTANCES_TABLE, null,
                            AcceptancesBean.ID + "= ?",
                            new String[]{loginUuid}, null, null, null);
                }

                if (c.moveToFirst()) {
                    bean = createAcceptancesBeanFromSingleCursor(c);
                }

                c.close();

            } catch (Throwable tr) {
                LOG.E(TAG, "getVisitorNameByUuid() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }

        if (bean != null) {
            LOG.D(TAG, "Name = " + bean.getFirstName() + " " + bean.getLastName());
            return bean.getFirstName() + " " + bean.getLastName();
        } else {
            return null;
        }

    }


    public String getVisitorNameBySecurityCode(String securityCode) {
        LOG.D(TAG, "getVisitorNameBySecurityCode(), securityCode =" + securityCode);

        //SQLiteDatabase db = null;
        AcceptancesBean bean = null;
        Cursor c;
        synchronized (LOCK) {
            try {

                db = m_DatabaseHelper.getWritableDatabase();

                synchronized (db) {
                    c = db.query(DatabaseHelper.VISITORS_ACCEPTANCES_TABLE, null,
                            AcceptancesBean.SECURITY_CODE + "= ?",
                            new String[]{securityCode}, null, null, null);
                }

                if (c.moveToFirst()) {
                    bean = createAcceptancesBeanFromSingleCursor(c);
                }

                c.close();

            } catch (Throwable tr) {
                LOG.E(TAG, "getVisitorNameBySecurityCode() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }

        if (bean != null) {
            LOG.D(TAG, "Name = " + bean.getFirstName() + " " + bean.getLastName());
            return bean.getFirstName() + " " + bean.getLastName();
        } else {
            return null;
        }

    }


    private VerifiedFaceBean[] createVerifiedFaceBeanFromCursor(Cursor c) {
        LOG.D(TAG, "createVerifiedFaceBeanFromCursor c = " + c);
        LOG.D(TAG, "createVerifiedFaceBeanFromCursor c.getCount() = " + c.getCount());
        VerifiedFaceBean[] bean = new VerifiedFaceBean[c.getCount()];
        LOG.D(TAG, "createVerifiedFaceBeanFromCursor c.getString(c.getColumnIndexOrThrow(UserLoginInfoBean.EMPLOYEE_ID)) = " +
                c.getString(c.getColumnIndexOrThrow(UserLoginInfoBean.EMPLOYEE_ID)));
        int i = 0;
        do {
            bean[i] = new VerifiedFaceBean();
            bean[i].setBapModelId(c.getString(c.getColumnIndexOrThrow(VerifiedFaceBean.BAP_MODEL_ID)));
            bean[i].setId(c.getString(c.getColumnIndexOrThrow(VerifiedFaceBean.ID)));
            bean[i].setSecurityCode(c.getString(c.getColumnIndexOrThrow(VerifiedFaceBean.SECURITY_CODE)));
            bean[i].setEmployeeId(c.getString(c.getColumnIndexOrThrow(VerifiedFaceBean.EMPLOYEE_ID)));
            bean[i].setEmployeeName(c.getString(c.getColumnIndexOrThrow(VerifiedFaceBean.EMPLOYEE_NAME)));
            bean[i].setVisitorName(c.getString(c.getColumnIndexOrThrow(VerifiedFaceBean.VISITOR_NAME)));
            bean[i].setCreatedTime(c.getString(c.getColumnIndexOrThrow(VerifiedFaceBean.CREATED_TIME)));
            bean[i].setModel(c.getBlob(c.getColumnIndexOrThrow(VerifiedFaceBean.MODEL)));
            i++;
        } while (c.moveToNext());

        return bean;
    }

    private VerifiedFaceBean createVerifiedFaceBeanFromSingleCursor(Cursor c) {
        LOG.D(TAG, "createVerifiedFaceBeanFromSingleCursor c = " + c);
        LOG.D(TAG, "createVerifiedFaceBeanFromSingleCursor c.getCount() = " + c.getCount());
        VerifiedFaceBean bean = new VerifiedFaceBean();
        LOG.D(TAG, "createVerifiedFaceBeanFromSingleCursor c.getString(c.getColumnIndexOrThrow(VerifiedFaceBean.EMPLOYEE_ID)) = " +
                c.getString(c.getColumnIndexOrThrow(VerifiedFaceBean.EMPLOYEE_ID)));
//        bean = new VerifiedFaceBean();
        bean.setBapModelId(c.getString(c.getColumnIndexOrThrow(VerifiedFaceBean.BAP_MODEL_ID)));
        bean.setId(c.getString(c.getColumnIndexOrThrow(VerifiedFaceBean.ID)));
        bean.setSecurityCode(c.getString(c.getColumnIndexOrThrow(VerifiedFaceBean.SECURITY_CODE)));
        bean.setEmployeeId(c.getString(c.getColumnIndexOrThrow(VerifiedFaceBean.EMPLOYEE_ID)));
        bean.setEmployeeName(c.getString(c.getColumnIndexOrThrow(VerifiedFaceBean.EMPLOYEE_NAME)));
        bean.setVisitorName(c.getString(c.getColumnIndexOrThrow(VerifiedFaceBean.VISITOR_NAME)));

        bean.setCreatedTime(c.getString(c.getColumnIndexOrThrow(VerifiedFaceBean.CREATED_TIME)));
        bean.setModel(c.getBlob(c.getColumnIndexOrThrow(VerifiedFaceBean.MODEL)));

        return bean;
    }

    private RegisterBean createEmployeeRegisterBeanFromSingleCursor(Cursor c) {
        LOG.D(TAG, "createEmployeeRegisterBeanFromSingleCursor c = " + c);
        LOG.D(TAG, "createEmployeeRegisterBeanFromSingleCursor c.getCount() = " + c.getCount());
        RegisterBean bean = new RegisterBean();
//        bean = new VerifiedFaceBean();
        bean.setDeviceToken(c.getString(c.getColumnIndexOrThrow(RegisterBean.DEVICE_TOKEN)));
        bean.setEmployeeId(c.getString(c.getColumnIndexOrThrow(RegisterBean.EMPLOYEE_ID)));
        bean.setName(c.getString(c.getColumnIndexOrThrow(RegisterBean.NAME)));
        bean.setEmail(c.getString(c.getColumnIndexOrThrow(RegisterBean.EMAIL)));
        bean.setCreateTime(c.getString(c.getColumnIndexOrThrow(RegisterBean.CREATE_TIME)));
        bean.setFormat(c.getString(c.getColumnIndexOrThrow(RegisterBean.FORMAT)));
        bean.setDataInBase64(c.getBlob(c.getColumnIndexOrThrow(RegisterBean.DATA_IN_BASE64)));
        bean.setMobileNo(c.getString(c.getColumnIndexOrThrow(RegisterBean.MOBILE_NO)));
        bean.setDepartment(c.getString(c.getColumnIndexOrThrow(RegisterBean.DEPARTMENT)));
        bean.setTitle(c.getString(c.getColumnIndexOrThrow(RegisterBean.TITLE)));
        bean.setModelId(c.getInt(c.getColumnIndexOrThrow(RegisterBean.MODEL_ID)));
        bean.setModel(c.getBlob(c.getColumnIndexOrThrow(RegisterBean.MODEL)));
        bean.setRfid(c.getString(c.getColumnIndexOrThrow(RegisterBean.RFID)));

        return bean;
    }


    public void updateEmployeeIdentitiesByBapModelId(IdentitiesModel model, String bapModelId) {
        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();

                LOG.D(TAG, "updateEmployeeIdentitiesByBapModelId need update model.getCreatedTime() = " + model.getCreatedTime());
                LOG.D(TAG, "updateEmployeeIdentitiesByBapModelId need update model.getModel() = " + model.getModel());
                LOG.D(TAG, "updateEmployeeIdentitiesByBapModelId need update model.getEmployeeId() = " + model.getEmployeeId());
                LOG.D(TAG, "updateEmployeeIdentitiesByBapModelId need update model.getEmployeeName() = " + model.getEmployeeName());
                LOG.D(TAG, "updateEmployeeIdentitiesByBapModelId need update model.getId() = " + model.getId());

                ContentValues cv = new ContentValues();
                cv.put(VerifiedFaceBean.ID, model.getId());
                cv.put(VerifiedFaceBean.CREATED_TIME, model.getCreatedTime());
                cv.put(VerifiedFaceBean.MODEL, model.getModel());
                cv.put(VerifiedFaceBean.EMPLOYEE_ID, model.getEmployeeId());
                cv.put(VerifiedFaceBean.VISITOR_NAME, model.getVisitorName());
                cv.put(VerifiedFaceBean.EMPLOYEE_NAME, model.getEmployeeName());
                db.update(DatabaseHelper.EMPLOYEE_IDENTITIES_TABLE, cv,
                        VerifiedFaceBean.BAP_MODEL_ID + "= ?",
                        new String[]{bapModelId});

            } catch (Throwable tr) {
                LOG.E(TAG, "updateEmployeeIdentitiesByBapModelId() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }
    }

    public void updateVisitorIdentitiesByBapModelId(IdentitiesModel model, String bapModelId) {
        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();

                LOG.D(TAG, "updateVisitorIdentitiesByBapModelId need update model.getCreatedTime() = " + model.getCreatedTime());
                LOG.D(TAG, "updateVisitorIdentitiesByBapModelId need update model.getModel() = " + model.getModel());
                LOG.D(TAG, "updateVisitorIdentitiesByBapModelId need update model.getEmployeeId() = " + model.getEmployeeId());
                LOG.D(TAG, "updateVisitorIdentitiesByBapModelId need update model.getEmployeeName() = " + model.getEmployeeName());
                LOG.D(TAG, "updateVisitorIdentitiesByBapModelId need update model.getId() = " + model.getId());

                ContentValues cv = new ContentValues();
                cv.put(VerifiedFaceBean.ID, model.getId());
                cv.put(VerifiedFaceBean.CREATED_TIME, model.getCreatedTime());
                cv.put(VerifiedFaceBean.MODEL, model.getModel());
                cv.put(VerifiedFaceBean.EMPLOYEE_ID, model.getEmployeeId());
                cv.put(VerifiedFaceBean.VISITOR_NAME, model.getVisitorName());
                cv.put(VerifiedFaceBean.EMPLOYEE_NAME, model.getEmployeeName());
                db.update(DatabaseHelper.VISITOR_IDENTITIES_TABLE, cv,
                        VerifiedFaceBean.BAP_MODEL_ID + "= ?",
                        new String[]{bapModelId});

            } catch (Throwable tr) {
                LOG.E(TAG, "updateVisitorIdentitiesByBapModelId() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }
    }

    public void deleteEmployeeIdentitiesByBapModelId(String bapModelId) {
        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                db.delete(DatabaseHelper.EMPLOYEE_IDENTITIES_TABLE,
                        VerifiedFaceBean.BAP_MODEL_ID + "= ?",
                        new String[]{bapModelId});

            } catch (Throwable tr) {
                LOG.E(TAG, "deleteEmployeeIdentitiesByBapModelId() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }
    }

    public void deleteVisitorIdentitiesByBapModelId(String bapModelId) {
        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                db.delete(DatabaseHelper.VISITOR_IDENTITIES_TABLE,
                        VerifiedFaceBean.BAP_MODEL_ID + "= ?",
                        new String[]{bapModelId});

            } catch (Throwable tr) {
                LOG.E(TAG, "deleteVisitorIdentitiesByBapModelId() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }
    }


    //******************************************************************Acceptances use Start ***********************************************************************


//    public void deleteAllEmployeesAcceptances() {
//        LOG.D(TAG, "deleteAllEmployeesAcceptances");
//        try {
//            db.delete(DatabaseHelper.EMPLOYEES_ACCEPTANCES_TABLE,
//                    null,null);
//        } catch (Throwable tr) {
//            LOG.E(TAG, "deleteAllEmployeesAcceptances() - failed.", tr);
//        }
//
//    }

    public void deleteAllEmployeesAcceptances() {
        LOG.D(TAG, "deleteAllEmployeesAcceptances");
        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    db.delete(DatabaseHelper.EMPLOYEES_ACCEPTANCES_TABLE,
                            null, null);
                }

            } catch (Throwable tr) {
                LOG.E(TAG, "getUserClockByModuleRecordMode() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }


    }

//    public void deleteAllVisitorsAcceptances() {
//        LOG.D(TAG, "deleteAllVisitorsAcceptances");
//
//        try {
//            db.delete(DatabaseHelper.VISITORS_ACCEPTANCES_TABLE,
//                    null,null);
//        } catch (Throwable tr) {
//            LOG.E(TAG, "deleteAllEmployeesAcceptances() - failed.", tr);
//        }
//
//    }

    public void deleteAllVisitorsAcceptances() {
        LOG.D(TAG, "deleteAllVisitorsAcceptances");

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    db.delete(DatabaseHelper.VISITORS_ACCEPTANCES_TABLE,
                            null, null);
                }

            } catch (Throwable tr) {
                LOG.E(TAG, "getUserClockByModuleRecordMode() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }

    }

    public void deleteAllEmployeesIdentities() {
        LOG.D(TAG, "deleteAllEmployeesIdentities");
        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    db.delete(DatabaseHelper.EMPLOYEE_IDENTITIES_TABLE,
                            null, null);
                }

            } catch (Throwable tr) {
                LOG.E(TAG, "deleteAllEmployeesIdentities() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }


    }

    public void deleteAllVisitorsIdentities() {
        LOG.D(TAG, "deleteAllVisitorsIdentities");
        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    db.delete(DatabaseHelper.VISITOR_IDENTITIES_TABLE,
                            null, null);
                }

            } catch (Throwable tr) {
                LOG.E(TAG, "deleteAllVisitorsIdentities() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }


    }


//    public void createEmployeesAttendances(AcceptancesBean acceptancesBean) {
//        LOG.D(TAG, "createEmployeesAttendances");
//        try {
//
//            ContentValues values = new ContentValues();
//            values.put(AcceptancesBean.ID, acceptancesBean.getId());
//            values.put(AcceptancesBean.EMPLOYEE_ID, acceptancesBean.getEmployeeId());
//            values.put(AcceptancesBean.SECURITY_CODE, acceptancesBean.getSecurityCode());
//            values.put(AcceptancesBean.RFID, acceptancesBean.getRfid());
//            values.put(AcceptancesBean.FIRST_NAME, acceptancesBean.getFirstName());
//            values.put(AcceptancesBean.LAST_NAME, acceptancesBean.getLastName());
//            values.put(AcceptancesBean.PHOTO_URL, acceptancesBean.getPhotoUrl());
//            values.put(AcceptancesBean.INT_ID, acceptancesBean.getIntId());
//            values.put(AcceptancesBean.PHOTO, acceptancesBean.getPhoto());
//
//            LOG.V(TAG, "createEmployeesAttendances() - acceptancesBean.getId() = " + acceptancesBean.getId());
//
//            db.insert(DatabaseHelper.EMPLOYEES_ACCEPTANCES_TABLE, null, values);
//
//        } catch (Throwable tr) {
//            LOG.E(TAG, "createEmployeesAttendances() - failed.", tr);
//        }
//    }

    public void createEmployeesAttendances(AcceptancesBean acceptancesBean) {
        LOG.D(TAG, "createEmployeesAttendances");

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    ContentValues values = new ContentValues();
                    values.put(AcceptancesBean.ID, acceptancesBean.getId());
                    values.put(AcceptancesBean.EMPLOYEE_ID, acceptancesBean.getEmployeeId());
                    values.put(AcceptancesBean.SECURITY_CODE, acceptancesBean.getSecurityCode());
                    values.put(AcceptancesBean.RFID, acceptancesBean.getRfid());
                    values.put(AcceptancesBean.FIRST_NAME, acceptancesBean.getFirstName());
                    values.put(AcceptancesBean.LAST_NAME, acceptancesBean.getLastName());
                    values.put(AcceptancesBean.PHOTO_URL, acceptancesBean.getPhotoUrl());
                    values.put(AcceptancesBean.INT_ID, acceptancesBean.getIntId());
                    values.put(AcceptancesBean.PHOTO, acceptancesBean.getPhoto());
                    values.put(AcceptancesBean.START_TIME, acceptancesBean.getStartTime());
                    values.put(AcceptancesBean.END_TIME, acceptancesBean.getEndTime());

                    LOG.V(TAG, "createEmployeesAttendances() - acceptancesBean.getId() = " + acceptancesBean.getId());

                    db.insert(DatabaseHelper.EMPLOYEES_ACCEPTANCES_TABLE, null, values);
                }

            } catch (Throwable tr) {
                LOG.E(TAG, "getUserClockByModuleRecordMode() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }

    }

    public void createVisitorsAttendances(AcceptancesBean acceptancesBean) {
        LOG.D(TAG, "createVisitorsAttendances");

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    ContentValues values = new ContentValues();
                    values.put(AcceptancesBean.ID, acceptancesBean.getId());
                    values.put(AcceptancesBean.EMPLOYEE_ID, acceptancesBean.getEmployeeId());
                    values.put(AcceptancesBean.SECURITY_CODE, acceptancesBean.getSecurityCode());
                    values.put(AcceptancesBean.RFID, acceptancesBean.getRfid());
                    values.put(AcceptancesBean.FIRST_NAME, acceptancesBean.getFirstName());
                    values.put(AcceptancesBean.LAST_NAME, acceptancesBean.getLastName());
                    values.put(AcceptancesBean.PHOTO_URL, acceptancesBean.getPhotoUrl());
                    values.put(AcceptancesBean.INT_ID, acceptancesBean.getIntId());
                    values.put(AcceptancesBean.PHOTO, acceptancesBean.getPhoto());
                    values.put(AcceptancesBean.START_TIME, acceptancesBean.getStartTime());
                    values.put(AcceptancesBean.END_TIME, acceptancesBean.getEndTime());

                    LOG.V(TAG, "createEmployeesAttendances() - acceptancesBean.getId() = " + acceptancesBean.getId());

                    db.insert(DatabaseHelper.VISITORS_ACCEPTANCES_TABLE, null, values);
                }

            } catch (Throwable tr) {
                LOG.E(TAG, "createVisitorsAttendances() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }
    }

//    public ArrayList<AcceptancesBean> getAllEmployeesAcceptancesArrayList() {
//        LOG.D(TAG, "getAllEmployeesAcceptancesArrayList");
//        ArrayList<AcceptancesBean> bean = null;
//
//
//        Cursor c;
//
//        c = db.query(DatabaseHelper.EMPLOYEES_ACCEPTANCES_TABLE, null, null, null, null, null, null);
//
//        LOG.D(TAG,"getAllEmployeesAcceptancesArrayList c.getCount() = " + c.getCount());
//        if(c.getCount() > 0){
//            try {
////                if (c.moveToFirst()) {
////
////                }
//                bean = new ArrayList<>();
//                c.moveToFirst();
//                do{
//                    LOG.V(TAG, "getAllEmployeesAcceptancesArrayList Cursor");
//                    bean.add(createAcceptancesBeanFromSingleCursor(c));
//                }while(c.moveToNext());
//
//
//            } catch (Throwable tr) {
//                LOG.E(TAG, "getAllEmployeesAcceptancesArrayList() - failed.", tr);
//            } finally {
//                c.close();
//            }
//
//            return bean;
//        }else{
//            return null;
//        }
//
//    }

    public ArrayList<AcceptancesBean> getAllEmployeesAcceptancesArrayList() {
        LOG.D(TAG, "getAllEmployeesAcceptancesArrayList");
        ArrayList<AcceptancesBean> bean = null;

        Cursor c = null;

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    c = db.query(DatabaseHelper.EMPLOYEES_ACCEPTANCES_TABLE, null, null, null, null, null, null);
                }

                LOG.D(TAG, "getAllEmployeesAcceptancesArrayList c.getCount() = " + c.getCount());
                if (c.getCount() > 0) {
                    try {
//                if (c.moveToFirst()) {
//
//                }
                        bean = new ArrayList<>();
                        c.moveToFirst();
                        do {
                            LOG.V(TAG, "getAllEmployeesAcceptancesArrayList Cursor");
                            bean.add(createAcceptancesBeanFromSingleCursor(c));
                        } while (c.moveToNext());


                    } catch (Throwable tr) {
                        LOG.E(TAG, "getAllEmployeesAcceptancesArrayList() - failed.", tr);
                    } finally {
                        c.close();
                    }
                }

            } catch (Throwable tr) {
                LOG.E(TAG, "getUserClockByModuleRecordMode() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }
        if (bean != null) {
            LOG.D(TAG, "getAllEmployeesAcceptancesArrayList bean.size() = " + bean.size());
        }

        return bean;


    }

//    public ArrayList<AcceptancesBean> getAllVisitorsAcceptancesArrayList() {
//        LOG.D(TAG, "getAllVisitorsAcceptancesArrayList");
//        ArrayList<AcceptancesBean> bean = null;
//
//
//        Cursor c;
//
//        c = db.query(DatabaseHelper.VISITORS_ACCEPTANCES_TABLE, null, null, null, null, null, null);
//
//        LOG.D(TAG,"getAllVisitorsAcceptancesArrayList c.getCount() = " + c.getCount());
//        if(c.getCount() > 0){
//            try {
////                if (c.moveToFirst()) {
////
////                }
//                bean = new ArrayList<>();
//                c.moveToFirst();
//                do{
//                    LOG.V(TAG, "getAllVisitorsAcceptancesArrayList Cursor");
//                    bean.add(createAcceptancesBeanFromSingleCursor(c));
//                }while(c.moveToNext());
//
//
//            } catch (Throwable tr) {
//                LOG.E(TAG, "getAllEmployeesAcceptancesArrayList() - failed.", tr);
//            } finally {
//                c.close();
//            }
//
//            return bean;
//        }else{
//            return null;
//        }
//
//    }

    public ArrayList<AcceptancesBean> getAllVisitorsAcceptancesArrayList() {
        LOG.D(TAG, "getAllVisitorsAcceptancesArrayList");
        ArrayList<AcceptancesBean> bean = null;


        Cursor c;

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    c = db.query(DatabaseHelper.VISITORS_ACCEPTANCES_TABLE, null, null, null, null, null, null);
                }

                LOG.D(TAG, "getAllVisitorsAcceptancesArrayList c.getCount() = " + c.getCount());
                if (c.getCount() > 0) {
                    try {
//                if (c.moveToFirst()) {
//
//                }
                        bean = new ArrayList<>();
                        c.moveToFirst();
                        do {
                            LOG.V(TAG, "getAllVisitorsAcceptancesArrayList Cursor");
                            bean.add(createAcceptancesBeanFromSingleCursor(c));
                        } while (c.moveToNext());


                    } catch (Throwable tr) {
                        LOG.E(TAG, "getAllEmployeesAcceptancesArrayList() - failed.", tr);
                    } finally {
                        c.close();
                    }

                }

            } catch (Throwable tr) {
                LOG.E(TAG, "getUserClockByModuleRecordMode() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }


        return bean;


    }

    public AcceptancesBean getEmployeesAcceptancesBySecurityId(String securityId) {
        LOG.D(TAG, "getEmployeesAcceptancesBySecurityId(), securityId =" + securityId);

        //SQLiteDatabase db = null;
        AcceptancesBean bean = null;
        Cursor c;
        synchronized (LOCK) {
            try {

                db = m_DatabaseHelper.getWritableDatabase();

                synchronized (db) {
                    c = db.query(DatabaseHelper.EMPLOYEES_ACCEPTANCES_TABLE, null,
                            AcceptancesBean.SECURITY_CODE + "= ?",
                            new String[]{securityId}, null, null, null);
                }

                if (c.moveToFirst()) {
                    bean = createAcceptancesBeanFromSingleCursor(c);
                }

                c.close();

            } catch (Throwable tr) {
                LOG.E(TAG, "getEmployeesAcceptancesBySecurityId() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }
        return bean;
    }

    public AcceptancesBean getVisitorsAcceptancesBySecurityId(String securityId) {
        LOG.D(TAG, "getVisitorsAcceptancesBySecurityId(), securityId =" + securityId);

        //SQLiteDatabase db = null;
        AcceptancesBean bean = null;
        Cursor c;
        synchronized (LOCK) {
            try {

                db = m_DatabaseHelper.getWritableDatabase();

                synchronized (db) {
                    c = db.query(DatabaseHelper.VISITORS_ACCEPTANCES_TABLE, null,
                            AcceptancesBean.SECURITY_CODE + "= ?",
                            new String[]{securityId}, null, null, null);
                }

                if (c.moveToFirst()) {
                    bean = createAcceptancesBeanFromSingleCursor(c);
                }

                c.close();

            } catch (Throwable tr) {
                LOG.E(TAG, "getVisitorsAcceptancesBySecurityId() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }
        return bean;
    }


    public AcceptancesBean getEmployeesAcceptancesById(String id) {
        LOG.D(TAG, "getEmployeesAcceptancesById(), id =" + id);

        //SQLiteDatabase db = null;
        AcceptancesBean bean = null;
        Cursor c;
        synchronized (LOCK) {
            try {

                db = m_DatabaseHelper.getWritableDatabase();

                synchronized (db) {
                    c = db.query(DatabaseHelper.EMPLOYEES_ACCEPTANCES_TABLE, null,
                            AcceptancesBean.ID + "= ?",
                            new String[]{id}, null, null, null);
                }

                if (c.moveToFirst()) {
                    bean = createAcceptancesBeanFromSingleCursor(c);
                }

                c.close();

            } catch (Throwable tr) {
                LOG.E(TAG, "getEmployeesAcceptancesById() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }
        return bean;
    }

    public AcceptancesBean getVisitorsAcceptancesById(String id) {
        LOG.D(TAG, "getVisitorsAcceptancesById(), id =" + id);

        //SQLiteDatabase db = null;
        AcceptancesBean bean = null;
        Cursor c;
        synchronized (LOCK) {
            try {

                db = m_DatabaseHelper.getWritableDatabase();

                synchronized (db) {
                    c = db.query(DatabaseHelper.VISITORS_ACCEPTANCES_TABLE, null,
                            AcceptancesBean.ID + "= ?",
                            new String[]{id}, null, null, null);
                }

                if (c.moveToFirst()) {
                    bean = createAcceptancesBeanFromSingleCursor(c);
                }

                c.close();

            } catch (Throwable tr) {
                LOG.E(TAG, "getVisitorsAcceptancesById() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }
        return bean;
    }


    public void updateEmployeeAcceptancesBySecurityCode(AcceptancesBean bean, String securityCode) {
        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();

                LOG.D(TAG, "updateEmployeeAcceptancesBySecurityCode bean.getSecurityCode() = " + bean.getSecurityCode());

                ContentValues cv = new ContentValues();
                cv.put(AcceptancesBean.ID, bean.getId());
                cv.put(AcceptancesBean.EMPLOYEE_ID, bean.getEmployeeId());
                cv.put(AcceptancesBean.RFID, bean.getRfid());
                cv.put(AcceptancesBean.FIRST_NAME, bean.getFirstName());
                cv.put(AcceptancesBean.LAST_NAME, bean.getLastName());
                cv.put(AcceptancesBean.PHOTO_URL, bean.getPhotoUrl());
                cv.put(AcceptancesBean.INT_ID, bean.getIntId());
                cv.put(AcceptancesBean.START_TIME, bean.getStartTime());
                cv.put(AcceptancesBean.END_TIME, bean.getEmployeeId());
                cv.put(AcceptancesBean.PHOTO, bean.getPhoto());
                cv.put(AcceptancesBean.SECURITY_CODE, bean.getSecurityCode());
//                cv.put(AcceptancesBean.ID, bean.getId());

                db.update(DatabaseHelper.EMPLOYEES_ACCEPTANCES_TABLE, cv,
                        AcceptancesBean.SECURITY_CODE + "= ?",
                        new String[]{securityCode});

            } catch (Throwable tr) {
                LOG.E(TAG, "updateEmployeeAcceptancesBySecurityCode() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }
    }

    public void updateVisitorAcceptancesBySecurityCode(AcceptancesBean bean, String securityCode) {
        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();

                LOG.D(TAG, "updateVisitorAcceptancesBySecurityCode bean.getSecurityCode() = " + bean.getSecurityCode());

                ContentValues cv = new ContentValues();
                cv.put(AcceptancesBean.ID, bean.getId());
                cv.put(AcceptancesBean.EMPLOYEE_ID, bean.getEmployeeId());
                cv.put(AcceptancesBean.RFID, bean.getRfid());
                cv.put(AcceptancesBean.FIRST_NAME, bean.getFirstName());
                cv.put(AcceptancesBean.LAST_NAME, bean.getLastName());
                cv.put(AcceptancesBean.PHOTO_URL, bean.getPhotoUrl());
                cv.put(AcceptancesBean.INT_ID, bean.getIntId());
                cv.put(AcceptancesBean.START_TIME, bean.getStartTime());
                cv.put(AcceptancesBean.END_TIME, bean.getEndTime());
                cv.put(AcceptancesBean.PHOTO, bean.getPhoto());
//                cv.put(AcceptancesBean.ID, bean.getId());

                db.update(DatabaseHelper.VISITORS_ACCEPTANCES_TABLE, cv,
                        AcceptancesBean.SECURITY_CODE + "= ?",
                        new String[]{securityCode});

            } catch (Throwable tr) {
                LOG.E(TAG, "updateEmployeeAcceptancesBySecurityCode() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }
    }


    public boolean SearchEmployeeAcceptanceBySecurityCode(String securityCode) {
        //SQLiteDatabase db = null;
        long count = 0;
        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                count = DatabaseUtils.queryNumEntries(
                        db,
                        DatabaseHelper.EMPLOYEES_ACCEPTANCES_TABLE,
                        AcceptancesBean.SECURITY_CODE + "= ?",
                        new String[]{securityCode});
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
            return count > 0 ? true : false;
        }
    }

    public boolean SearchVisitorAcceptanceBySecurityCode(String securityCode) {
        //SQLiteDatabase db = null;
        long count = 0;
        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                count = DatabaseUtils.queryNumEntries(
                        db,
                        DatabaseHelper.VISITORS_ACCEPTANCES_TABLE,
                        AcceptancesBean.SECURITY_CODE + "= ?",
                        new String[]{securityCode});
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
            return count > 0 ? true : false;
        }
    }


    public boolean SearchEmployeeIdentityBySecurityCode(String securityCode) {
        //SQLiteDatabase db = null;
        long count = 0;
        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                count = DatabaseUtils.queryNumEntries(
                        db,
                        DatabaseHelper.EMPLOYEE_IDENTITIES_TABLE,
                        VerifiedFaceBean.SECURITY_CODE + "= ?",
                        new String[]{securityCode});
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
            return count > 0 ? true : false;
        }
    }

    public boolean SearchVisitorIdentityByMobileNo(String mobileNo) {
        //SQLiteDatabase db = null;
        long count = 0;
        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                count = DatabaseUtils.queryNumEntries(
                        db,
                        DatabaseHelper.VISITOR_IDENTITIES_TABLE,
                        VerifiedFaceBean.SECURITY_CODE + "= ?",
                        new String[]{mobileNo});
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
            return count > 0 ? true : false;
        }
    }


    public void updateEmployeeIdentitiesFaceBySecurityCode(VerifiedFaceBean bean, String securityCode) {
        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();

                LOG.D(TAG, "updateEmployeeIdentitiesFaceBySecurityCode bean.getSecurityCode() = " + bean.getSecurityCode());
                LOG.D(TAG, "updateEmployeeIdentitiesFaceBySecurityCode bean.getEmployeeId() = " + bean.getEmployeeId());
                LOG.D(TAG, "updateEmployeeIdentitiesFaceBySecurityCode bean.getEmployeeName() = " + bean.getEmployeeName());

                ContentValues cv = new ContentValues();
                cv.put(VerifiedFaceBean.ID, bean.getId());
                cv.put(VerifiedFaceBean.BAP_MODEL_ID, bean.getBapModelId());
                cv.put(VerifiedFaceBean.EMPLOYEE_ID, bean.getEmployeeId());
                cv.put(VerifiedFaceBean.EMPLOYEE_NAME, bean.getEmployeeName());
                cv.put(VerifiedFaceBean.VISITOR_NAME, bean.getVisitorName());
                cv.put(VerifiedFaceBean.CREATED_TIME, bean.getCreatedTime());
                cv.put(VerifiedFaceBean.MODEL, bean.getModel());

//                cv.put(AcceptancesBean.ID, bean.getId());

                db.update(DatabaseHelper.EMPLOYEE_IDENTITIES_TABLE, cv,
                        VerifiedFaceBean.SECURITY_CODE + "= ?",
                        new String[]{securityCode});

            } catch (Throwable tr) {
                LOG.E(TAG, "updateEmployeeIdentitiesFaceBySecurityCode() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }
    }

    public void updateVisitorIdentitiesFaceByMobileNo(VerifiedFaceBean bean, String securityCode) {
        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();

                LOG.D(TAG, "updateVisitorIdentitiesFaceByMobileNo bean.getSecurityCode() = " + bean.getSecurityCode());
                LOG.D(TAG, "updateVisitorIdentitiesFaceByMobileNo bean.getEmployeeId() = " + bean.getEmployeeId());
                LOG.D(TAG, "updateVisitorIdentitiesFaceByMobileNo bean.getEmployeeName() = " + bean.getEmployeeName());

                ContentValues cv = new ContentValues();
                cv.put(VerifiedFaceBean.ID, bean.getId());
                cv.put(VerifiedFaceBean.BAP_MODEL_ID, bean.getBapModelId());
                cv.put(VerifiedFaceBean.EMPLOYEE_ID, bean.getEmployeeId());
                cv.put(VerifiedFaceBean.EMPLOYEE_NAME, bean.getEmployeeName());
                cv.put(VerifiedFaceBean.VISITOR_NAME, bean.getVisitorName());
                cv.put(VerifiedFaceBean.CREATED_TIME, bean.getCreatedTime());
                cv.put(VerifiedFaceBean.MODEL, bean.getModel());

//                cv.put(AcceptancesBean.ID, bean.getId());

                db.update(DatabaseHelper.VISITOR_IDENTITIES_TABLE, cv,
                        VerifiedFaceBean.SECURITY_CODE + "= ?",
                        new String[]{securityCode});

            } catch (Throwable tr) {
                LOG.E(TAG, "updateEmployeeIdentitiesFaceBySecurityCode() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }
    }


    private AcceptancesBean createAcceptancesBeanFromSingleCursor(Cursor c) {
        LOG.D(TAG, "createAcceptancesBeanFromSingleCursor c = " + c);
        LOG.D(TAG, "createAcceptancesBeanFromSingleCursor c.getCount() = " + c.getCount());
        AcceptancesBean bean = new AcceptancesBean();
        LOG.D(TAG, "createAcceptancesBeanFromSingleCursor c.getString(c.getColumnIndexOrThrow(AcceptancesBean.EMPLOYEE_ID)) = " +
                c.getString(c.getColumnIndexOrThrow(AcceptancesBean.EMPLOYEE_ID)));

        bean.setId(c.getString(c.getColumnIndexOrThrow(AcceptancesBean.ID)));
        bean.setEmployeeId(c.getString(c.getColumnIndexOrThrow(AcceptancesBean.EMPLOYEE_ID)));
        bean.setSecurityCode(c.getString(c.getColumnIndexOrThrow(AcceptancesBean.SECURITY_CODE)));
        bean.setRfid(c.getString(c.getColumnIndexOrThrow(AcceptancesBean.RFID)));
        bean.setFirstName(c.getString(c.getColumnIndexOrThrow(AcceptancesBean.FIRST_NAME)));
        bean.setLastName(c.getString(c.getColumnIndexOrThrow(AcceptancesBean.LAST_NAME)));
        bean.setPhotoUrl(c.getString(c.getColumnIndexOrThrow(AcceptancesBean.PHOTO_URL)));
        bean.setIntId(c.getInt(c.getColumnIndexOrThrow(AcceptancesBean.INT_ID)));
        bean.setStartTime(c.getInt(c.getColumnIndexOrThrow(AcceptancesBean.START_TIME)));
        bean.setEndTime(c.getInt(c.getColumnIndexOrThrow(AcceptancesBean.END_TIME)));
//        bean.setPhoto(c.getString(c.getColumnIndexOrThrow(AcceptancesBean.PHOTO)));


        return bean;
    }


    //****************************************************************** Acceptances use End ***********************************************************************


    //****************************************************************** Register use Start ***********************************************************************
    public void createEmployeesRegister(RegisterBean registerBean) {
        LOG.D(TAG, "createEmployeesRegister");

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    ContentValues values = new ContentValues();
                    values.put(RegisterBean.DEVICE_TOKEN, registerBean.getDeviceToken());
                    values.put(RegisterBean.EMPLOYEE_ID, registerBean.getEmployeeId());
                    values.put(RegisterBean.NAME, registerBean.getName());
                    values.put(RegisterBean.EMAIL, registerBean.getEmail());
                    values.put(RegisterBean.PASSWORD, registerBean.getPassword());
                    values.put(RegisterBean.CREATE_TIME, registerBean.getCreateTime());
                    values.put(RegisterBean.FORMAT, registerBean.getFormat());
                    values.put(RegisterBean.DATA_IN_BASE64, registerBean.getDataInBase64());
                    values.put(RegisterBean.MOBILE_NO, registerBean.getMobileNo());
                    values.put(RegisterBean.DEPARTMENT, registerBean.getDepartment());
                    values.put(RegisterBean.TITLE, registerBean.getTitle());
                    values.put(RegisterBean.MODEL_ID, registerBean.getModelId());
                    values.put(RegisterBean.MODEL, registerBean.getModel());
                    values.put(RegisterBean.RFID, registerBean.getRfid());


                    LOG.V(TAG, "createEmployeesRegister() - registerBean.getDeviceToken() = " + registerBean.getDeviceToken());

                    db.insert(DatabaseHelper.EMPLOYEES_REGISTER_TABLE, null, values);
                }

            } catch (Throwable tr) {
                LOG.E(TAG, "createEmployeesRegister() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }

    }


    public void createVisitorsRegister(RegisterBean registerBean) {
        LOG.D(TAG, "createVisitorsRegister");

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    ContentValues values = new ContentValues();
                    values.put(RegisterBean.DEVICE_TOKEN, registerBean.getDeviceToken());
                    values.put(RegisterBean.EMPLOYEE_ID, registerBean.getEmployeeId());
                    values.put(RegisterBean.NAME, registerBean.getName());
                    values.put(RegisterBean.EMAIL, registerBean.getEmail());
                    values.put(RegisterBean.PASSWORD, registerBean.getPassword());
                    values.put(RegisterBean.CREATE_TIME, registerBean.getCreateTime());
                    values.put(RegisterBean.FORMAT, registerBean.getFormat());
                    values.put(RegisterBean.DATA_IN_BASE64, registerBean.getDataInBase64());
                    values.put(RegisterBean.MOBILE_NO, registerBean.getMobileNo());
                    values.put(RegisterBean.DEPARTMENT, registerBean.getDepartment());
                    values.put(RegisterBean.TITLE, registerBean.getTitle());
                    values.put(RegisterBean.MODEL_ID, registerBean.getModelId());
                    values.put(RegisterBean.MODEL, registerBean.getModel());
                    values.put(RegisterBean.RFID, registerBean.getRfid());


                    LOG.V(TAG, "createVisitorsRegister() - registerBean.getDeviceToken() = " + registerBean.getDeviceToken());

                    db.insert(DatabaseHelper.VISITORS_REGISTER_TABLE, null, values);
                }

            } catch (Throwable tr) {
                LOG.E(TAG, "createVisitorsRegister() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }

    }


    //****************************************************************** Register use End ***********************************************************************


    //*********************************************************  Device login start  *********************************************************
//    public void deleteAllDeviceLogin() {
//        LOG.D(TAG, "deleteAllDeviceLogin");
//
//        try {
//            db.delete(DatabaseHelper.DEVICE_LOGIN_TABLE,
//                    null,null);
//        } catch (Throwable tr) {
//            LOG.E(TAG, "deleteAllDeviceLogin() - failed.", tr);
//        }
//
//    }


    public void deleteAllDeviceLogin() {
        LOG.D(TAG, "deleteAllDeviceLogin");

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    db.delete(DatabaseHelper.DEVICE_LOGIN_TABLE,
                            null, null);
                }

            } catch (Throwable tr) {
                LOG.E(TAG, "getUserClockByModuleRecordMode() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }

    }


//    public void createDeviceLogin(DeviceLoginBean deviceLoginBean) {
//        LOG.D(TAG, "createDeviceLogin");
//        try {
//
//            ContentValues values = new ContentValues();
//            values.put(DeviceLoginBean.LOCALE, deviceLoginBean.getLocale());
//            values.put(DeviceLoginBean.MODULE, deviceLoginBean.getModule());
//            values.put(DeviceLoginBean.MODES, deviceLoginBean.getModes());
//
//            LOG.V(TAG, "createDeviceLogin() - deviceLoginBean.getModes() = " + deviceLoginBean.getModes());
//
//            db.insert(DatabaseHelper.DEVICE_LOGIN_TABLE, null, values);
//
//        } catch (Throwable tr) {
//            LOG.E(TAG, "createDeviceLogin() - failed.", tr);
//        }
//    }

    public void createDeviceLogin(DeviceLoginBean deviceLoginBean) {
        LOG.D(TAG, "createDeviceLogin");

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    ContentValues values = new ContentValues();
                    values.put(DeviceLoginBean.LOCALE, deviceLoginBean.getLocale());
                    values.put(DeviceLoginBean.MODULE, deviceLoginBean.getModule());
                    values.put(DeviceLoginBean.MODES, deviceLoginBean.getModes());

                    LOG.V(TAG, "createDeviceLogin() - deviceLoginBean.getModes() = " + deviceLoginBean.getModes());

                    db.insert(DatabaseHelper.DEVICE_LOGIN_TABLE, null, values);
                }

            } catch (Throwable tr) {
                LOG.E(TAG, "getUserClockByModuleRecordMode() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }

    }


//    public ArrayList<DeviceLoginBean> getAllDeviceInfo() {
//        LOG.D(TAG, "getAllDeviceInfo");
//        ArrayList<DeviceLoginBean> bean = null;
//
//
//
//
//
//        Cursor c;
//
//        c = db.query(DatabaseHelper.DEVICE_LOGIN_TABLE, null, null, null, null, null, null);
//
//        LOG.D(TAG,"getAllDeviceInfo c.getCount() = " + c.getCount());
//        if(c.getCount() > 0){
//            try {
////                if (c.moveToFirst()) {
////
////                }
//                bean = new ArrayList<>();
//                c.moveToFirst();
//                do{
//                    LOG.V(TAG, "getAllDeviceInfo Cursor");
//                    bean.add(createDeviceInfoFromSingleCursor(c));
//                }while(c.moveToNext());
//
//
//            } catch (Throwable tr) {
//                LOG.E(TAG, "getAllDeviceInfo() - failed.", tr);
//            } finally {
//                c.close();
//            }
//
//            return bean;
//        }else{
//            return null;
//        }
//
//    }


    public ArrayList<DeviceLoginBean> getAllDeviceInfo() {
        LOG.D(TAG, "getAllDeviceInfo");
        ArrayList<DeviceLoginBean> bean = null;


        Cursor c;

        synchronized (LOCK) {
            try {
                db = m_DatabaseHelper.getWritableDatabase();
                synchronized (db) {
                    c = db.query(DatabaseHelper.DEVICE_LOGIN_TABLE, null, null, null, null, null, null);
                }

                LOG.D(TAG, "getAllDeviceInfo c.getCount() = " + c.getCount());
                if (c.getCount() > 0) {
                    try {
//                if (c.moveToFirst()) {
//
//                }
                        bean = new ArrayList<>();
                        c.moveToFirst();
                        do {
                            LOG.V(TAG, "getAllDeviceInfo Cursor");
                            bean.add(createDeviceInfoFromSingleCursor(c));
                        } while (c.moveToNext());


                    } catch (Throwable tr) {
                        LOG.E(TAG, "getAllDeviceInfo() - failed.", tr);
                    } finally {
                        c.close();
                    }

                }

            } catch (Throwable tr) {
                LOG.E(TAG, "getUserClockByModuleRecordMode() - failed.", tr);
            } finally {
                if (db != null && db.isOpen())
                    db.close();
            }
        }


        return bean;


    }


    private DeviceLoginBean createDeviceInfoFromSingleCursor(Cursor c) {
        LOG.D(TAG, "createDeviceInfoFromSingleCursor c = " + c);
        LOG.D(TAG, "createDeviceInfoFromSingleCursor c.getCount() = " + c.getCount());
        DeviceLoginBean bean = new DeviceLoginBean();

        bean.setLocale(c.getString(c.getColumnIndexOrThrow(DeviceLoginBean.LOCALE)));
        bean.setModule(c.getString(c.getColumnIndexOrThrow(DeviceLoginBean.MODULE)));
        bean.setModes(c.getString(c.getColumnIndexOrThrow(DeviceLoginBean.MODES)));

        return bean;
    }


    //*********************************************************  Device login end    *********************************************************


}
