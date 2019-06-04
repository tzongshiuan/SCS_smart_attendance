package com.gorilla.attendance.enterprise.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gorilla.attendance.enterprise.MainActivity;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.LocalBroadcastHelper;

/**
 * Created by ggshao on 2017/2/18.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "BootBroadcastReceiver";
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {

        LOG.D(TAG,"intent.getAction()  = " + intent.getAction());
        if (intent.getAction().equals(ACTION)){
            Intent enterpriseIntent=new Intent(context, MainActivity.class);
            enterpriseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(enterpriseIntent);
        }else if (intent.getAction().equals(Constants.BROADCAST_RECEIVER_DAY_PASS)){
            intent.setAction(Constants.BROADCAST_RECEIVER_DAY_PASS);
            LocalBroadcastHelper.sendBroadcast(context, intent);

        }
    }

}
