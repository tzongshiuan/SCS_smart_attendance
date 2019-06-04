package com.gorilla.attendance.enterprise.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.gorilla.enroll.lib.listener.OnNFCReaderListener;
import com.gorilla.enroll.lib.util.NFCManager;

/**
 * Created by ggshao on 2018/1/25.
 */

public class RFIDScannerManager {
    private static final String TAG = "FDRControlManager";
    private static RFIDScannerManager mInstance = null;

    private NFCManager mNFCManager;
    private Context mContext = null;
    private Handler mActivityCallbackHandler = null;
    private Handler mFragmentCallbackHandler = null;


    public static RFIDScannerManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new RFIDScannerManager(context);
        }
        return mInstance;
    }

    public RFIDScannerManager(Context context){
        mContext = context;
        mInstance = this;

    }

    public void initRFIDManager(Handler activityCallback, Handler fragmentCallback){

        mActivityCallbackHandler = activityCallback;
        mFragmentCallbackHandler = fragmentCallback;

        mNFCManager = new NFCManager.Builder(mContext)
                .setListener(new OnNFCReaderListener() {
                    @Override
                    public void onEndInitReader(boolean success) {
                        LOG.D(TAG,"onEndInitReader");
//                        mProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onStartInitReader() {
                        LOG.D(TAG,"onStartInitReader");
//                        mProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onStartCardRead() {
                        LOG.D(TAG,"onStartCardRead");
//                        mProgress.setVisibility(View.VISIBLE);
                        mNFCManager.setEnableReadCard(false);
                    }

                    @Override
                    public void onEndCardRead(String data) {
                        LOG.D(TAG,"onEndCardRead data = " + data);
//                        mProgress.setVisibility(View.GONE);

                        //callback data

                        mFragmentCallbackHandler.removeMessages(Constants.GET_FACE_TOO_LONG);

                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.KEY_RFID_CARD_NUMBER, data);
                        message.what = Constants.MSG_RFID_CARD_READ;
                        message.setData(bundle);

                        mFragmentCallbackHandler.sendMessage(message);


                    }
                }).build();
    }


    public void start(){
        if(mNFCManager != null){
            mNFCManager.start();
        }
    }

    public void setEnableReadCard(boolean enableReadCard){
        if(mNFCManager != null){
            mNFCManager.setEnableReadCard(enableReadCard);
        }
    }

    public void stop(){
        if(mNFCManager != null){
            mNFCManager.stop();
        }
    }




}
