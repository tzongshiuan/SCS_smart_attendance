package com.gorilla.attendance.enterprise;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by ggshao on 2017/2/7.
 */

public class MainApplication extends Application {

    private static Context context;

    public static Context getAppContext() {
        return MainApplication.context;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        MainApplication.context = getApplicationContext();
//        CrashHandler.getInstance().initCrashHandler(this);

//        Fresco.initialize(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}
