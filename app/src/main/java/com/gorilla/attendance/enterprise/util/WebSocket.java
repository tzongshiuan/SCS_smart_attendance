package com.gorilla.attendance.enterprise.util;

import android.content.Context;
import android.os.Handler;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;


/**
 * Created by GGShao on 2016/11/24.
 */

public class WebSocket extends WebSocketClient{
    private static final String TAG = "WebSocket";
    private Context mContext = null;
    private Handler mActivityHandler = null;

    @Deprecated
    public WebSocket(URI serverUri, Context context) {
        super(serverUri);

        mContext = context;
//        connect();

        try{
            connect();
        }catch(Exception e){
            LOG.D(TAG,"WebSocket connectBlocking Exception");
        }

    }

    public WebSocket(URI serverUri, Draft draft, Map<String, String> headers, int timeout, Context context, Handler callback) {
        super( serverUri, draft, headers, timeout );

        LOG.D(TAG,"serverUri = " + serverUri);
        LOG.D(TAG,"headers = " + headers);
        LOG.D(TAG,"timeout = " + timeout);
        LOG.D(TAG,"context = " + context);
        mContext = context;
        mActivityHandler = callback;

    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        LOG.D(TAG,"onOpen serverHandshake = " + serverHandshake);

//        send("clientName " + DeviceUtils.mDeviceName);
        send("deviceToken " + DeviceUtils.mDeviceName);

        mActivityHandler.sendEmptyMessage(Constants.MSG_WEB_SOCKET_CONNECT);
        mActivityHandler.removeMessages(Constants.MSG_WEB_SOCKET_DISCONNECT);

//        TSAAttendanceUtils.mWebSocket.send("clientName " + TSAAttendanceUtils.mClientName);
//
//        Intent broadcastIntent = new Intent();
//        broadcastIntent.putExtra(Constants.BROADCAST_SOCKET_STATE, true);
//        broadcastIntent.setAction(Constants.SOCKET_STATE_CHANGE);
//        LocalBroadcastHelper.sendBroadcast(mContext, broadcastIntent);

    }

    @Override
    public void onMessage(String message) {
        LOG.D(TAG,"onMessage message = " + message);

        if(message.indexOf("map_session_id") > -1){
            if(message.indexOf("0") > -1){
                send("map_session_id ok");
            }else{
                // DISCONNECT
                mActivityHandler.removeMessages(Constants.MSG_WEB_SOCKET_DISCONNECT);
                mActivityHandler.sendEmptyMessageDelayed(Constants.MSG_WEB_SOCKET_DISCONNECT, DeviceUtils.CHECK_WEB_SOCKET_TIME);
            }
        }else{
            send(message + " ok");
        }

//        TSAAttendanceUtils.mWebSocket.send(message + " ok");
//

        if(message.equals(Constants.PUSH_MESSAGE_SYNC_EMPLOYEE)){

            ApiUtils.getDeviceEmployees(TAG, mContext, DeviceUtils.mDeviceName, CallbackUtils.getDeviceEmployeesListener);
            ApiUtils.getDeviceVisitors(TAG, mContext, DeviceUtils.mDeviceName, CallbackUtils.getDeviceVisitorsListener);
            ApiUtils.getDeviceVerifiedIdAndImage(TAG, mContext, DeviceUtils.mDeviceName, CallbackUtils.getDeviceVerifiedIdAndImageListener);

        } else if(message.equals(Constants.PUSH_MESSAGE_SYNC_VISITOR)){

            ApiUtils.getDeviceEmployees(TAG, mContext, DeviceUtils.mDeviceName, CallbackUtils.getDeviceEmployeesListener);
            ApiUtils.getDeviceVisitors(TAG, mContext, DeviceUtils.mDeviceName, CallbackUtils.getDeviceVisitorsListener);
            ApiUtils.getDeviceVerifiedIdAndImage(TAG, mContext, DeviceUtils.mDeviceName, CallbackUtils.getDeviceVerifiedIdAndImageListener);

        } else if(message.equals(Constants.PUSH_MESSAGE_SYNC_MARQUEE)){

            ApiUtils.getDeviceMarquees(TAG, mContext, DeviceUtils.mDeviceName, CallbackUtils.getDeviceMarqueesListener);

        } else if(message.equals(Constants.PUSH_MESSAGE_SYNC_VIDEO)){

            mActivityHandler.removeMessages(Constants.MSG_CLOSE_VIDEO);
            mActivityHandler.sendEmptyMessage(Constants.MSG_CLOSE_VIDEO);

            ApiUtils.getDeviceVideos(TAG, mContext, DeviceUtils.mDeviceName, true, CallbackUtils.getDeviceVideosListener);

        } else if(message.equals(Constants.PUSH_MESSAGE_RESTART)){

            mActivityHandler.sendEmptyMessage(Constants.MSG_RESTART);

        }else if(message.equals(Constants.PUSH_MESSAGE_TEST_CONNECTION)){

            mActivityHandler.removeMessages(Constants.MSG_CHECK_WEB_SOCKET_ALIVE);
            mActivityHandler.sendEmptyMessageDelayed(Constants.MSG_CHECK_WEB_SOCKET_ALIVE, DeviceUtils.CHECK_WEB_SOCKET_TIME);


        }else if(message.equals(Constants.PUSH_MESSAGE_SYNC_ALL)){

            mActivityHandler.removeMessages(Constants.MSG_CLOSE_VIDEO);
            mActivityHandler.sendEmptyMessage(Constants.MSG_CLOSE_VIDEO);

            ApiUtils.getDeviceEmployees(TAG, mContext, DeviceUtils.mDeviceName, CallbackUtils.getDeviceEmployeesListener);
            ApiUtils.getDeviceVisitors(TAG, mContext, DeviceUtils.mDeviceName, CallbackUtils.getDeviceVisitorsListener);
            ApiUtils.getDeviceVerifiedIdAndImage(TAG, mContext, DeviceUtils.mDeviceName, CallbackUtils.getDeviceVerifiedIdAndImageListener);
            ApiUtils.getDeviceMarquees(TAG, mContext, DeviceUtils.mDeviceName, CallbackUtils.getDeviceMarqueesListener);
            ApiUtils.getDeviceVideos(TAG, mContext, DeviceUtils.mDeviceName, true, CallbackUtils.getDeviceVideosListener);

//            mActivityHandler.removeMessages(Constants.MSG_CHECK_WEB_SOCKET_ALIVE);
//            mActivityHandler.sendEmptyMessageDelayed(Constants.MSG_CHECK_WEB_SOCKET_ALIVE, DeviceUtils.VIDEO_DELAYED_TIME);

        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LOG.D(TAG,"onClose code = " + code + " reason = " + reason + " remote = " + remote);

//        mActivityHandler.sendEmptyMessage(Constants.MSG_WEB_SOCKET_DISCONNECT);

        mActivityHandler.removeMessages(Constants.MSG_WEB_SOCKET_DISCONNECT);
//        mActivityHandler.sendEmptyMessage(Constants.MSG_WEB_SOCKET_DISCONNECT);
        mActivityHandler.sendEmptyMessageDelayed(Constants.MSG_WEB_SOCKET_DISCONNECT, DeviceUtils.CHECK_WEB_SOCKET_CLOSE_ERROR_TIME);

    }

    @Override
    public void onError(Exception ex) {
        LOG.D(TAG,"onError ex = " + ex);

        mActivityHandler.removeMessages(Constants.MSG_WEB_SOCKET_DISCONNECT);
//        mActivityHandler.sendEmptyMessage(Constants.MSG_WEB_SOCKET_DISCONNECT);
        mActivityHandler.sendEmptyMessageDelayed(Constants.MSG_WEB_SOCKET_DISCONNECT, DeviceUtils.CHECK_WEB_SOCKET_CLOSE_ERROR_TIME);
    }


}
