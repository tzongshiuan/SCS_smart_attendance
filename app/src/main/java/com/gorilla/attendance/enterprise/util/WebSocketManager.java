package com.gorilla.attendance.enterprise.util;

import android.content.Context;
import android.os.Handler;

import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;

import java.net.URI;
import java.util.Map;

/**
 * Created by ggshao on 2017/2/15.
 */

public class WebSocketManager {
    private static final String TAG = "WebSockerManager";

    private static WebSocketManager mInstance = null;
    private WebSocket mWebSocket = null;

    private Context mContext = null;

    public static WebSocketManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new WebSocketManager(context);
        }
        return mInstance;
    }

    public WebSocketManager(Context context){
        mContext = context;
        mInstance = this;

    }

    public void connect(URI serverUri, int timeout, Context context, Handler callback){
        LOG.D(TAG,"connect serverUri = " + serverUri);
        LOG.D(TAG,"connect timeout = " + timeout);
        //DeviceUtils.WEB_SOCKET_TIME_OUT
        if(mWebSocket == null){
            mWebSocket = new WebSocket(serverUri, new Draft_17(), null, timeout, context, callback);

        }

        try{
            mWebSocket.connect();
        }catch(Exception e){
            LOG.D(TAG,"WebSocket connectBlocking Exception");
        }

    }

    public void connect(URI serverUri, Map<String, String> headers, int timeout, Context context){

    }

    public void connect(URI serverUri, Draft draft, Map<String, String> headers, int timeout, Context context){

    }

    public synchronized void reconnect(URI serverUri, int timeout, Context context, Handler callback){
        LOG.D(TAG,"reconnect mWebSocket = " + mWebSocket);
        if(mWebSocket != null){
            mWebSocket.close();
            mWebSocket = null;

            mWebSocket = new WebSocket(serverUri, new Draft_17(), null, timeout, context, callback);
        }else{
            mWebSocket = new WebSocket(serverUri, new Draft_17(), null, timeout, context, callback);
        }

        try{
            mWebSocket.connect();
        }catch(Exception e){
            LOG.D(TAG,"WebSocket connectBlocking Exception");
        }
    }

    public synchronized void disconnect(){
        LOG.D(TAG,"disconnect");
        if(mWebSocket != null){
            mWebSocket.close();
            mWebSocket = null;

        }else{

        }

    }




}
