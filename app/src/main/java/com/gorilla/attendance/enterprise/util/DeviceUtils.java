package com.gorilla.attendance.enterprise.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import com.gorilla.attendance.enterprise.datamodel.EmployeeModel;
import com.gorilla.attendance.enterprise.datamodel.PlayVideoModel;
import com.gorilla.attendance.enterprise.datamodel.VideosModel;
import com.gorilla.attendance.enterprise.datamodel.VisitorModel;
import com.gorilla.attendance.enterprise.service.BluetoothLeService;

import java.net.URI;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by ggshao on 2017/2/10.
 */

public class DeviceUtils {
    public static int mLoginStatus = Constants.LOGIN_STATUS_LOGOUT;

    public static final int FDR_DELAYED_TIME = 20000;
    public static final int PAGE_DELAYED_TIME = 10000;
    //    public static final int VIDEO_DELAYED_TIME = 180000;//3 minutes
    public static int VIDEO_DELAYED_TIME = 30000;//30s
    public static int SYNC_TIME_OUT = 60000;//60s
    public static final int MESSAGE_DIALOG_DELAYED_TIME = 2000;//2 seconds
    public static final int CLOCK_DIALOG_DELAY_TIME = 2000;// 2 seconds
    public static final int TIMER_DELAYED_TIME = 1000;//1 seconds
    public static final int WEB_SOCKET_TIME_OUT = 10000;//10 seconds
    //    public static final int CHECK_USER_CLOCK_DB_DELAYED_TIME = 300000;//300 seconds
    public static final int CHECK_USER_CLOCK_DB_DELAYED_TIME = 60000;//60 seconds
//    public static final int CHECK_USER_CLOCK_DB_DELAYED_TIME = 20000;//20 seconds

    //    public static final int LIVING_LEARNING_TIME = 30000;//30 seconds
    public static final int LIVING_LEARNING_TIME = 5000;//5 seconds
    //    public static final int LIVING_LEARNING_TIME = 30000;//10 seconds
    public static final int CHECK_WEB_SOCKET_TIME = 90000;//90 seconds
    public static final int CHECK_WEB_SOCKET_CLOSE_ERROR_TIME = 10000;//10 seconds
    public static final int SETTING_DELAYED_TIME = 40000;//40 seconds
    public static final int SETTING_DETAIL_DELAYED_TIME = 600000;//600 seconds

    public static final int RENEW_VISITOR_SECURITY_CODE_DELAYED_TIME = 60000;//60 seconds

    public static final int EMPLOYEE_THREE_CHANCES_DELAYED_TIME = 10000;//10 seconds

    public static final String CONF_SETTING_ACCOUNT_1 = "system01";
    public static final String CONF_SETTING_ACCOUNT_2 = "system02";
    public static final String CONF_SETTING_PASSWORD_1 = "admin01";
    public static final String CONF_SETTING_PASSWORD_2 = "admin02";

    public static boolean mIsOffLineMode = true;

    public static String mLocale = null;
    public static String mBgImage = null;
    public static String mTitleImage = null;
    public static String mDeviceName = null;
    public static String mDeviceShowName = null;
    public static String mDeviceIp = null;//no use
    public static String mMarqueeString = null;//no use
    public static String mFtpAccount = null;
    public static String mFtpPassword = null;

    public static EmployeeModel mEmployeeModel = null;
    public static VisitorModel mVisitorModel = null;

    public static String mWsIp = null;
    public static URI mWsUri = null;

    public static boolean mIsEmployeeOpenDoor = true;
    public static boolean mIsVisitorOpenDoor = false;

    public static boolean mIsInnoLux = false;


    public static boolean mIsLivenessOn = false;
    public static boolean mIsBTConnected = false;

//    public static String mWsUrl = null;

    public static boolean mIsRtspSetting = false;

    public static boolean mIsClockAuto = false;
    public static boolean mIsRadioClockIn = false;
    public static boolean mIsRadioClockOut = false;
    public static String mRtspIp = null;
    public static String mRtspUrl = null;
    public static String mRtspAccount = null;
    public static String mRtspPassword = null;
    public static boolean mIsOnlineMode = false;


    public static BluetoothLeService mBluetoothLeService = null;
    public static boolean[] mCheckIsDownloadingVideo = null;
    public static ArrayList<PlayVideoModel> mVideoList;

    public static int mFdrRange = 80;
    public static int mFdrTopXCoordinate = 0;
    public static int mFdrTopYCoordinate = 0;
    public static int mFdrBottomXCoordinate = 0;
    public static int mFdrBottomYCoordinate = 0;

    public static int mResultMessageDelayTime = 1300;


    public static void setDeviceInfo(Context context, String locale, String bgImage, String titleImage) {
        DeviceUtils.mLocale = locale;
        DeviceUtils.mBgImage = bgImage;
        DeviceUtils.mTitleImage = titleImage;


        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();

        Configuration config = resources.getConfiguration();


        if (DeviceUtils.mLocale.equals(Constants.LOCALE_EN)) {
            config.locale = Locale.ENGLISH;
        } else if (DeviceUtils.mLocale.equals(Constants.LOCALE_TW)) {
            config.locale = Locale.TRADITIONAL_CHINESE;
        } else if (DeviceUtils.mLocale.equals(Constants.LOCALE_CN)) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else {
            config.locale = Locale.ENGLISH;
        }

        resources.updateConfiguration(config, dm);


    }

    public static void checkSettingLanguage(Context context) {
        if (DeviceUtils.mLoginStatus == Constants.LOGIN_STATUS_LOGOUT) {
            //check if in register page

        } else {
            //set language
            DeviceUtils.setLanguage(context, DeviceUtils.mLocale);

        }

    }

    public static void setLanguage(Context context, String locale) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();

        if (locale.equals(Constants.LOCALE_EN)) {
            config.locale = Locale.ENGLISH;
        } else if (locale.equals(Constants.LOCALE_TW)) {
            config.locale = Locale.TRADITIONAL_CHINESE;
        } else if (locale.equals(Constants.LOCALE_CN)) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else {
            config.locale = Locale.ENGLISH;
        }

//        config.locale = new Locale("en", "US");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLayoutDirection(config.locale);
        }

        resources.updateConfiguration(config, dm);

//        ((MainActivity)context).recreate();
    }


//    public static void setDeviceInfoFromDb(Context context, )

    public static void clearDeviceSetting() {
        mLocale = null;
        mBgImage = null;
        mTitleImage = null;
        mDeviceName = null;
        mDeviceIp = null;//no use
        mMarqueeString = null;//no use
        mFtpAccount = null;
        mFtpPassword = null;

        mEmployeeModel = null;
        mVisitorModel = null;
        mWsIp = null;
        mWsUri = null;
    }


}
