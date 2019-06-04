package com.gorilla.attendance.enterprise.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.gorilla.attendance.enterprise.util.LOG;

import java.util.ArrayList;

/**
 * Created by ggshao on 2017/2/21.
 */

public class RemoteService extends Service
{
    private static final String TAG = "RemoteService";
    private Messenger messenger; //receives remote invocations

    private ArrayList<Messenger> mClients = new ArrayList<Messenger>();

    private static final int MSG_REGISTER_CLIENT = 1;
    //    private static final int MSG_SET_VALUE = 3;
    public static final int MSG_SET_IDENTIFY_RESULTS = 3;

    public static final String KEY_REGISTER_RESULT = "registerResult";

    public static final String KEY_FACE_IDENTIFY_IS_PASS = "faceIdentifyIsPass";
    public static final String KEY_FACE_IDENTIFY_NAME = "faceIdentifyName";
    public static final String KEY_FACE_IDENTIFY_ID = "faceIdentifyId";




    @Override
    public IBinder onBind(Intent intent)
    {
        LOG.D(TAG,"onBind messenger = " + messenger);
        LOG.D(TAG,"onBind intent = " + intent);
        if(this.messenger == null)
        {
            synchronized(RemoteService.class)
            {
                if(this.messenger == null)
                {
                    this.messenger = new Messenger(new IncomingHandler());
                }
            }
        }
        //Return the proper IBinder instance
        return this.messenger.getBinder();
    }

    private class IncomingHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
//            System.out.println("*****************************************");
//            System.out.println("Remote Service successfully invoked!!!!!!");
//            System.out.println("*****************************************");


            LOG.D(TAG,"handleMessage Remote Service successfully invoked!!!!!!");
            LOG.D(TAG,"msg.what = " + msg.what);
            LOG.D(TAG,"msg.obj = " + msg.obj);

            if(msg.obj != null){
                String message = (String)msg.obj;
                LOG.D(TAG,"message = " + message);
            }

            Bundle bundle = msg.getData();
            LOG.D(TAG,"bundle = " + bundle);

            int what = msg.what;
            LOG.D(TAG,"what = " + what);
            Message message = null;
            Bundle bundleSend = new Bundle();
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:

                    if(mClients.size() > 0){
                        mClients.remove(0);
                    }

                    mClients.add(msg.replyTo);

                    message = Message.obtain(null, MSG_REGISTER_CLIENT, 0, 0);
                    bundleSend.putString(KEY_REGISTER_RESULT, "register success");
                    message.setData(bundleSend);

                    try {
                        if(mClients.size() > 0){

                            mClients.get(0).send(message);

                        }
                    } catch (RemoteException e) {
                        LOG.D(TAG,"MSG_REGISTER_CLIENT RemoteException e = " + e);
                        // The client is dead.  Remove it from the list;
                        // we are going through the list from back to front
                        // so this is safe to do inside the loop.
//                        mClients.remove(0);
                    }
                    break;



//                case MSG_SET_VALUE:
//                    message = Message.obtain(null, 100, 0, 0);
//
//                    try {
//                        mClients.get(0).send(message);
//                    } catch (RemoteException e) {
//                        LOG.D(TAG,"RemoteException e = " + e);
//                        // The client is dead.  Remove it from the list;
//                        // we are going through the list from back to front
//                        // so this is safe to do inside the loop.
//                        mClients.remove(0);
//                    }
//
//                    break;

                case MSG_SET_IDENTIFY_RESULTS :
                    boolean faceIdentifyIsPass = false;
                    String faceIdentifyName = "";
                    String faceIdentifyId = "";
                    if(bundle != null){
                        faceIdentifyIsPass = bundle.getBoolean(KEY_FACE_IDENTIFY_IS_PASS);
                        faceIdentifyName = bundle.getString(KEY_FACE_IDENTIFY_NAME);
                        faceIdentifyId = bundle.getString(KEY_FACE_IDENTIFY_ID);
                    }

                    message = Message.obtain(null, MSG_SET_IDENTIFY_RESULTS, 0, 0);
                    bundleSend.putBoolean(KEY_FACE_IDENTIFY_IS_PASS, faceIdentifyIsPass);
                    bundleSend.putString(KEY_FACE_IDENTIFY_NAME, faceIdentifyName);
                    bundleSend.putString(KEY_FACE_IDENTIFY_ID, faceIdentifyId);
                    message.setData(bundleSend);
                    LOG.D(TAG,"mClients.size() = " + mClients.size());
                    try {
                        if(mClients.size() > 0){

                            mClients.get(0).send(message);

                        }
                    } catch (RemoteException e) {
                        LOG.D(TAG,"RemoteException e = " + e);
                        // The client is dead.  Remove it from the list;
                        // we are going through the list from back to front
                        // so this is safe to do inside the loop.
                        mClients.remove(0);
                    }
                    break;

            }

        }
    }
}
