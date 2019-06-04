package com.gorilla.attendance.enterprise.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ggshao on 2017/2/7.
 */

public class NetworkManager {
    /*======================================================================
     * Constant Fields
     *=======================================================================*/
    public static final String TAG = "NetworkManager";

    /*======================================================================
     * Fields
     *=======================================================================*/
    private Context m_Context;
    private ConnectivityManager m_Connectivity;

    /*======================================================================
     * Constructor
     *=======================================================================*/
    public NetworkManager(Context context)
    {
        this.m_Context = context;
        this.m_Connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /*======================================================================
     * Check if network is enabled
     *=======================================================================*/
    public boolean isNetworkAvailable() {

        NetworkInfo activeNetworkInfo = m_Connectivity.getActiveNetworkInfo();

        boolean isAvailable = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        LOG.V(TAG,"isNetworkAvailable() - isAvailable : "+isAvailable);

        return isAvailable;
    }
}
