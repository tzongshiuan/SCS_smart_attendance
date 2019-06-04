package com.gorilla.attendance.enterprise.util;

import android.util.Log;

/**
 * Created by ggshao on 2017/2/7.
 */

public class LOG {
    private static boolean needShowLogs = true;

    public static void setShowLog(boolean needShow)
    {
        needShowLogs = needShow;
    }

    public static boolean getShowLog()
    {
        return needShowLogs;
    }

    public static void E(String tagName, String message) {
        if (needShowLogs) {
            Log.e(tagName, message);
        }
    }
    public static void E(String tagName, String message, Throwable tr) {
        if (needShowLogs) {
            Log.e(tagName, message, tr);
        }
    }

    public static void W(String tagName, String message) {
        if (needShowLogs) {
            Log.w(tagName, message);
        }
    }

    public static void D(String tagName, String message) {
        if (needShowLogs) {
            Log.d(tagName, message);
        }
    }

    public static void I(String tagName, String message) {
        if (needShowLogs) {
            Log.i(tagName, message);
        }
    }


    public static void V(String tagName, String message) {
        if (needShowLogs) {
            Log.v(tagName, message);
        }
    }

    public static void WTF(String tagName, String message) {
        if (needShowLogs) {
            Log.wtf(tagName, message);
        }
    }
}
