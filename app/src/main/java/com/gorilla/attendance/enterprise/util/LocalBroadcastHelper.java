package com.gorilla.attendance.enterprise.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by ggshao on 2017/2/7.
 */

public class LocalBroadcastHelper {
    private static final String TAG = "LocalBroadcastHelper";

    public static boolean sendBroadcast(Context context, Intent intent) {
        if (context == null || intent == null) {
            LOG.W(TAG, "sendBroadcast, context: " + context + ", intent: " + intent);
            return false;
        }

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(context);
        if (lbm != null) {
            return lbm.sendBroadcast(intent);
        } else {
            LOG.W(TAG, "sendBroadcast, lbm is null!");
        }

        return false;
    }

    public static void registerReceiver(Context context, BroadcastReceiver receiver, IntentFilter intentFilter) {
        if (context == null || receiver == null || intentFilter == null) {
            LOG.W(TAG, "registerReceiver, context: " + context + ", receiver: " + receiver + ", intentFilter: " + intentFilter);
            return;
        }

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(context);
        if (lbm != null) {
            lbm.registerReceiver(receiver, intentFilter);
        } else {
            LOG.W(TAG, "registerReceiver, lbm is null!");
        }
    }

    public static void unregisterReceiver(Context context, BroadcastReceiver receiver) {
        if (context == null || receiver == null) {
            LOG.W(TAG, "unregisterReceiver, context: " + context + ", receiver: " + receiver);
            return;
        }

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(context);
        if (lbm != null) {
            try {
                lbm.unregisterReceiver(receiver);
            } catch (Exception ex) {
                LOG.W(TAG, "unregisterReceiver, ex: " + ex.toString());
                ex.printStackTrace();
            }
        } else {
            LOG.W(TAG, "unregisterReceiver, lbm is null!");
        }
    }
}
