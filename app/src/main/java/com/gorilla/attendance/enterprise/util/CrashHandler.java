package com.gorilla.attendance.enterprise.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.gorilla.attendance.enterprise.MainActivity;
import com.gorilla.attendance.enterprise.MainApplication;

/**
 * Created by ggshao on 2017/3/29.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";

    public static CrashHandler mAppCrashHandler;

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private MainApplication mAppContext;

    public void initCrashHandler(MainApplication application) {
        this.mAppContext = application;
        //獲取系統默認的UncaughtException處理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static CrashHandler getInstance() {
        if (mAppCrashHandler == null) {
            mAppCrashHandler = new CrashHandler();
        }
        return mAppCrashHandler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        LOG.D(TAG,"uncaughtException mDefaultHandler = " + mDefaultHandler);
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用戶沒有處理則讓系統默認的異常處理器來處理
            LOG.D(TAG,"uncaughtException Here");
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            LOG.D(TAG,"uncaughtException ex = " + ex);
            AlarmManager mgr = (AlarmManager) mAppContext.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(mAppContext, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra("crash", true);
            PendingIntent restartIntent = PendingIntent.getActivity(mAppContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, restartIntent);//500 ms 後重新啟動

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
            System.gc();
        }
    }

    /**
     * 錯誤處理，收集錯誤信息發送錯誤報告等操作均在此完成。
     * @param ex
     * @return true:如果處理了該異常信息;否則返回false。
     */
    private boolean handleException(Throwable ex) {
        LOG.D(TAG,"handleException ex = " + ex);
        if (ex == null) {
            return false;
        }
        //自定義處理錯誤信息
        return true;
    }
}
