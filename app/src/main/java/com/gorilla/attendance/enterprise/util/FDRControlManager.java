package com.gorilla.attendance.enterprise.util;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;

import com.gorilla.attendance.enterprise.MainActivity;
import com.gorilla.attendance.enterprise.R;
import com.gorilla.attendance.enterprise.fragment.VideoFragment;
import com.gorillatechnology.fdrcontrol.FDRControl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import gorilla.iod.IntelligentObjectDetector;

/**
 * Created by ggshao on 2017/2/7.
 */

public class FDRControlManager {
    private static final String TAG = "FDRControlManager";

    private FDRControl mFdrCtrl = null;

    private static FDRControlManager mInstance = null;
    private FDRControl.Mode mMode = FDRControl.Mode.RECOGNIZE;
    private FDRControl.FaceSource mFaceSource = FDRControl.FaceSource.CAMERA;
    public static int CAMERA_ID = 1;

    private static int IOD_OBJ_WIDTH_MIN = 150;
    private static int IOD_OBJ_WIDTH_MAX = 1000;
    private static int IOD_OBJ_WIDTH_MIN_BEST = 150;
    private static int IOD_OBJ_WIDTH_MAX_BEST = 900;
    private static int CAMERA_DETECT_LOC_LEFT = 5;
    private static int CAMERA_DETECT_LOC_TOP = 5;
    private static int CAMERA_DETECT_LOC_RIGHT = 1080;
    private static int CAMERA_DETECT_LOC_BOTTOM = 1920;

    private static final int SAMPLING_NUM = 3;
    private static final int SAMPLING_TIME = 1000;
    private static final int MAX_IMAGE_WIDTH = 320;
    private static final int SAVE_FACE_NUM = 1;

    private static final int MAX_FACE_DETECTION = 1;
    private static final int MAX_LIVENESS_FAIL = 3;


    private Context mContext = null;
    private boolean mIsOnPause = false;
    private Handler mActivityCallbackHandler = null;
    private Handler mFragmentCallbackHandler = null;
    private int mLivenessCount = 0;

    private MainActivity mMainActivity = null;

    public static FDRControlManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new FDRControlManager(context);
        }
        return mInstance;
    }

    public FDRControlManager(Context context){
        mContext = context;
        mInstance = this;

        mLivenessCount = 0;

    }

    public FDRControlManager(Context context, MainActivity mainActivity){
        mContext = context;
        mMainActivity = mainActivity;
        mInstance = this;

        mLivenessCount = 0;

    }

    private void refreshDevices()
    {
        UsbManager manager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        // Get the list of attached devices
        HashMap<String, UsbDevice> devices = manager.getDeviceList();

//        txtBox.setText("");
//        txtBox.setText("Number of devices: " + devices.size() + "\n");
        LOG.D(TAG,"Number of devices: " + devices.size() + "\n");

        // Iterate over all devices
        Iterator<String> it = devices.keySet().iterator();
        while (it.hasNext())
        {
            String deviceName = it.next();
            UsbDevice device = devices.get(deviceName);

            String VID = Integer.toHexString(device.getVendorId()).toUpperCase();
            String PID = Integer.toHexString(device.getProductId()).toUpperCase();
//            txtBox.append(deviceName + " " +  VID + ":" + PID + " " + manager.hasPermission(device) + "\n");
            LOG.D(TAG,deviceName + " " +  VID + ":" + PID + " " + manager.hasPermission(device) + "\n");
        }
    }


    public static FDRControlManager getInstance(Context context, MainActivity mainActivity){
        if(mInstance == null){
            mInstance = new FDRControlManager(context, mainActivity);
        }
        return mInstance;
    }

    public void initFdr(){

        LOG.D(TAG,"initFdr mFdrCtrl = " + mFdrCtrl);
        if (mFdrCtrl != null) {
//            mFdrCtrl.stopIdentify();
//            mFdrCtrl.logoutFDRService();
//            mFdrCtrl.release();
//            mFdrCtrl = null;
            return;

        }
        LOG.D(TAG,"initFdr DeviceUtils.mIsRtspSetting = " + DeviceUtils.mIsRtspSetting);
        LOG.D(TAG,"initFdr DeviceUtils.mRtspUrl = " + DeviceUtils.mRtspUrl);
        LOG.D(TAG,"initFdr DeviceUtils.mRtspAccount = " + DeviceUtils.mRtspAccount);
        LOG.D(TAG,"initFdr DeviceUtils.mRtspPassword = " + DeviceUtils.mRtspPassword);

        String libPath = mContext.getFilesDir()+"/Bin";
        if(DeviceUtils.mIsRtspSetting == true){

            LOG.D(TAG,"initFdr DeviceUtils.mIsRtspSetting = " + DeviceUtils.mIsRtspSetting);
            mFaceSource = FDRControl.FaceSource.RTSP;
//            mMode = FDRControl.Mode.IOD;

//            mFdrCtrl = new FDRControl(mContext, mMode, mFaceSource, CAMERA_ID);

            mFdrCtrl = new FDRControl(mContext, mMode, mFaceSource, CAMERA_ID, 0, libPath);
//            mFdrCtrl.connectRTSP(DeviceUtils.mRtspUrl, DeviceUtils.mRtspAccount, DeviceUtils.mRtspPassword);
            mFdrCtrl.setRTSPStatusListener(rtspListener);
        }else{
//            mFdrCtrl = new FDRControl(mContext, mMode, mFaceSource, CAMERA_ID);
            mFdrCtrl = new FDRControl(mContext, mMode, mFaceSource, CAMERA_ID, 0, libPath);
        }

        mFdrCtrl.setIODTriggerResponseListener(iodTrgrRespListener);
        mFdrCtrl.setCameraPreviewCallback(camPreviewCallback);
//        mFdrCtrl.setFDRControlResponseListener(onFdrCtrlResponse);

        mFdrCtrl.setIODLivenessEnable(true);
        // init for the FDR mode
        mFdrCtrl.setIODObjWidth(IOD_OBJ_WIDTH_MIN, IOD_OBJ_WIDTH_MAX,
                IOD_OBJ_WIDTH_MIN_BEST, IOD_OBJ_WIDTH_MAX_BEST); // TODO:

        //set FD range
        setFdrRange(DeviceUtils.mFdrRange);
        mFdrCtrl.setIODFRSuitableEnable(true);
        mFdrCtrl.setIODOcclusionEnable(true);

//        mFdrCtrl.setFaceListParam(SAMPLING_NUM, SAMPLING_TIME, FDRControl.CaptureMode.ByTime, MAX_IMAGE_WIDTH);
        mFdrCtrl.setFaceListParam(SAMPLING_NUM, SAMPLING_TIME, FDRControl.CaptureMode.ByQuality, MAX_IMAGE_WIDTH);


//        mFdrCtrl.setIODImageLogEnable(IOD_IMAGE_SAVE,
//                TSAAttendanceUtils.SD_CARD_APP_IOD_IMAGE); // TODO: TEST

//        mFdrCtrl.setIODGenderEnable(true, 0);
//        mFdrCtrl.setIODGlassesEnable(true);

//        mIsOnPause = false;

    }

    public String getCamera(CameraManager manager) {

        String cameraIndex = "0";
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                LOG.D(TAG, "cameraId " + cameraId);
                int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
//                if (cOrientation != CAMERACHOICE) {
//                    cameraIndex = cameraId;
//                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return cameraIndex;
    }


    public void releaseFdr(){
        if (mFdrCtrl != null) {
            mFdrCtrl.stopIdentify();
            mFdrCtrl.logoutFDRService();
            mFdrCtrl.release();
            mFdrCtrl = null;
        }

        mIsOnPause = true;


    }

    public void releaseCamera(int cameraId){
        if (mFdrCtrl != null) {
            mFdrCtrl.releaseCamera(cameraId);
        }

    }

    public void reInitCamera(int cameraId){
        if (mFdrCtrl != null) {
            mFdrCtrl.reInitCamera(cameraId);
        }

    }

    public void startFdr(Handler activityCallback, Handler fragmentCallback){
        LOG.D(TAG,"startFdr ");

        mIsOnPause = false;
        mActivityCallbackHandler = activityCallback;
        mFragmentCallbackHandler = fragmentCallback;

        mFdrCtrl.hideFaceView(false);

        if(DeviceUtils.mIsRtspSetting){
//            mFdrCtrl.connectRTSP();
//            mFdrCtrl.is
            LOG.D(TAG,"DeviceUtils.mRtspUrl = " + DeviceUtils.mRtspUrl);
            mFdrCtrl.connectRTSP(DeviceUtils.mRtspUrl, DeviceUtils.mRtspAccount, DeviceUtils.mRtspPassword);
        }

        LOG.D(TAG,"startFdr OK");

        mFdrCtrl.startFaceDetection();
//        mFdrCtrl.startCamera();


        mActivityCallbackHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
        mActivityCallbackHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
        mActivityCallbackHandler.sendEmptyMessageDelayed(Constants.GET_FACE_TOO_LONG ,Constants.GET_FACE_DELAYED_TIME);
        mFragmentCallbackHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
        mFragmentCallbackHandler.sendEmptyMessageDelayed(Constants.GET_FACE_TOO_LONG ,Constants.GET_FACE_DELAYED_TIME);

        //new Thread(mFaceDetectThread).start();

    }

    public void startFdrForVideoFragment(){
        LOG.D(TAG,"startFdrForVideoFragment");

        mIsOnPause = false;

        if(DeviceUtils.mIsRtspSetting){
//            mFdrCtrl.connectRTSP();
//            mFdrCtrl.is
            mFdrCtrl.connectRTSP(DeviceUtils.mRtspUrl, DeviceUtils.mRtspAccount, DeviceUtils.mRtspPassword);
        }

        mFdrCtrl.startFaceDetection();

        //new Thread(mFaceDetectThread).start();

    }

    public FDRControl getFdrCtrl(){
        LOG.D(TAG,"getFdrCtrl mFdrCtrl = " + mFdrCtrl);
        if(mFdrCtrl != null){
            return mFdrCtrl;
        }else{
            return null;
        }

    }

    public void stopFdr(Handler activityCallback, Handler fragmentCallback){
        LOG.D(TAG,"stopFdr activityCallback fragmentCallback");
        mIsOnPause = true;
        activityCallback.removeMessages(Constants.GET_FACE_TOO_LONG);
        fragmentCallback.removeMessages(Constants.GET_FACE_TOO_LONG);

        FDRControlManager.getInstance(mContext).getFdrCtrl().stopFaceDetection();

        if(DeviceUtils.mIsRtspSetting){
            FDRControlManager.getInstance(mContext).getFdrCtrl().disconnectRTSP();
        }



    }

    public void stopFdr(){
        LOG.D(TAG,"stopFdr ");

        mIsOnPause = true;

        if(mFdrCtrl != null){
            mFdrCtrl.stopFaceDetection();
            if(DeviceUtils.mIsRtspSetting){
                FDRControlManager.getInstance(mContext).getFdrCtrl().disconnectRTSP();
            }
        }


    }

    public void setFdrRange(int fdrRange){
        LOG.D(TAG,"setFdrRange  fdrRange = " + fdrRange);
        float widthPx = 0;
        float heightPx = 0;

        int widthDp = 0;
        int heightDp = 0;

        if ((mContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            LOG.D(TAG,"X Large");
            widthPx = mContext.getResources().getDimension(R.dimen.fdr_xlarge_width);
            heightPx = mContext.getResources().getDimension(R.dimen.fdr_xlarge_height);

            widthDp = (int)(mContext.getResources().getDimension(R.dimen.fdr_xlarge_width) / mContext.getResources().getDisplayMetrics().density);
            heightDp = (int)(mContext.getResources().getDimension(R.dimen.fdr_xlarge_height) / mContext.getResources().getDisplayMetrics().density);

        }else if ((mContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_LARGE) {
            LOG.D(TAG,"Large");
            widthPx = mContext.getResources().getDimension(R.dimen.fdr_large_width);
            heightPx = mContext.getResources().getDimension(R.dimen.fdr_large_height);

            widthDp = (int)(mContext.getResources().getDimension(R.dimen.fdr_large_width) / mContext.getResources().getDisplayMetrics().density);
            heightDp = (int)(mContext.getResources().getDimension(R.dimen.fdr_large_height) / mContext.getResources().getDisplayMetrics().density);

        }else if ((mContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            LOG.D(TAG,"Normal");
            widthPx = mContext.getResources().getDimension(R.dimen.fdr_normal_width);
            heightPx = mContext.getResources().getDimension(R.dimen.fdr_normal_height);

            widthDp = (int)(mContext.getResources().getDimension(R.dimen.fdr_normal_width) / mContext.getResources().getDisplayMetrics().density);
            heightDp = (int)(mContext.getResources().getDimension(R.dimen.fdr_normal_height) / mContext.getResources().getDisplayMetrics().density);
//            widthDp = R.dimen.fdr_normal_width;
//            heightDp = R.dimen.fdr_normal_height;


        }


        LOG.D(TAG,"widthPx = " + widthPx);
        LOG.D(TAG,"heightPx = " + heightPx);
        LOG.D(TAG,"widthDp = " + widthDp);
        LOG.D(TAG,"heightDp = " + heightDp);


        float topX = (float) (widthDp *  ((float)((100.0 - fdrRange)/2)/100));
        float topY = (float) (heightDp * ((float)((100.0 - fdrRange)/2)/100));
//        int topY = heightPx * (((100 - fdrRange)/2)/100);
        float bottomX = (float)widthDp - topX;
        float bottomY = (float)heightDp - topY;

//        float rangeWidth = (float)widthDp - (topX * 2);
//        float rangeHeight = (float)heightDp - (topY * 2);


        LOG.D(TAG,"topX = " + topX);
        LOG.D(TAG,"topY = " + topY);
        LOG.D(TAG,"bottomX = " + bottomX);
        LOG.D(TAG,"bottomY = " + bottomY);
//        LOG.D(TAG,"rangeWidth = " + rangeWidth);
//        LOG.D(TAG,"rangeHeight = " + rangeHeight);

//        Rect rect = new Rect(topX, topY, bottomX, bottomY);

//        Paint paint = new Paint();
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(Color.RED);
//        paint.setStrokeWidth(1);
//
//        mFdrCtrl.drawDetectRect(topX, topY, bottomX, bottomY, paint, true);
        mFdrCtrl.setBestObjLoc(topX, topY, bottomX, bottomY); // TODO: TEST

//        int topX = (int) (widthPx *  ((float)((100.0 - fdrRange)/2)/100));
//        int topY = (int) (heightPx * ((float)((100.0 - fdrRange)/2)/100));
////        int topY = heightPx * (((100 - fdrRange)/2)/100);
//        int bottomX = (int)widthPx - topX;
//        int bottomY = (int)heightPx - topY;
//
//
//        LOG.D(TAG,"topX = " + topX);
//        LOG.D(TAG,"topY = " + topY);
//        LOG.D(TAG,"bottomX = " + bottomX);
//        LOG.D(TAG,"bottomY = " + bottomY);
//
//        Rect rect = new Rect(topX, topY, bottomX, bottomY);
//
//        Paint paint = new Paint();
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(Color.RED);
//        paint.setStrokeWidth(10);
//        mFdrCtrl.drawDetectRect(rect,paint,true);
//
//        mFdrCtrl.setBestObjLoc(new Rect(topX, topY, bottomX, bottomY)); // TODO: TEST
    }

    private FDRControl.RTSPStatusListener rtspListener = new FDRControl.RTSPStatusListener() {
        @Override
        public void onClosed() {

        }

        @Override
        public void onStoped() {

        }

        @Override
        public void onConnecting() {

        }

        @Override
        public void onPlaying() {

        }

        @Override
        public void onWaitRetry() {

        }

        @Override
        public void onError(int code) {

        }
    };


    private FDRControl.IODTriggerResponseListener iodTrgrRespListener = new FDRControl.IODTriggerResponseListener() {

        @Override
        public void onIODTriggerResponse(FDRControl.TriggerType trigger, IntelligentObjectDetector.Type_IODInfo iodInfo) {
            LOG.D(TAG, "onIODTriggerResponse11 trigger = " + trigger);
            LOG.D(TAG, "onIODTriggerResponse iodInfo.width = " + iodInfo.width);
            LOG.D(TAG, "onIODTriggerResponse iodInfo.height = " + iodInfo.height);
            LOG.D(TAG, "onIODTriggerResponse iodInfo.x = " + iodInfo.x);
            LOG.D(TAG, "onIODTriggerResponse iodInfo.y = " + iodInfo.y);

            LOG.D(TAG, "onIODTriggerResponse iodInfo.liveness_result = " + iodInfo.liveness_result);


            /*
            *   liveness_result
            *   活體
                0: NO_RESULT
                1: SPOOF
                2: UNKNOWN
                3: REAL
            *
            *
            * */

            LOG.D(TAG,"mMainActivity.getSupportFragmentManager().findFragmentByTag(VideoFragment.TAG) = " + mMainActivity.getSupportFragmentManager().findFragmentByTag(VideoFragment.TAG));
            if(mMainActivity.getSupportFragmentManager().findFragmentByTag(VideoFragment.TAG) != null){
                VideoFragment videoFragment = (VideoFragment)mMainActivity.getSupportFragmentManager().findFragmentByTag(VideoFragment.TAG);
                videoFragment.closeVideo();
                return;
            }


            if(trigger.equals(FDRControl.TriggerType.DETERMINED)){

                //show Identifying
                mMainActivity.setIdentifyResult(mMainActivity.getString(R.string.identifying_the_face));

                EnterpriseUtils.mFacePngList = new ArrayList<byte[]>();
                FDRControl.IODfeature iodfeature = new FDRControl.IODfeature();

                int r = 0;
                List<byte[]> currentPngList = new ArrayList<byte[]>();
                List<String> iniList = new ArrayList<String>();

                r = mFdrCtrl.getRecognizedFaceList(currentPngList, iniList, iodfeature);
//                    LOG.D(TAG, "getRecognizedFaceList(), r=" + r);
                LOG.D(TAG,"mFaceDetectThread start r = " + r);


                //GG TEST DATA, test live fail
//                if (r != 0){
//                    for(int i = 0 ; i < currentPngList.size() ; i++){
//                        if(i == (currentPngList.size() - 1)){
//                            EnterpriseUtils.mFacePngList.add(currentPngList.get(i));
//                        }
//                    }
////                        mLivenessCount++;
//                    ClockUtils.mLiveness = Constants.LIVENESS_FAILED;
//
//                    mActivityCallbackHandler.sendEmptyMessage(Constants.GET_FACE_FAIL);
//                    mFragmentCallbackHandler.sendEmptyMessage(Constants.GET_FACE_FAIL);
//                    mIsOnPause = true;
//                    return;
//                }

                if (r != 0){

                    LOG.D(TAG,"mFaceDetectThread DeviceUtils.mIsLivenessOn = " + DeviceUtils.mIsLivenessOn);

                    LOG.D(TAG,"mFaceDetectThread iodfeature.frSuitableType = " + iodfeature.frSuitableType);
                    LOG.D(TAG,"mFaceDetectThread iodfeature.occlusionType = " + iodfeature.occlusionType);

                    if(iodfeature.occlusionType == IntelligentObjectDetector.Face_Occlusion_Type.GORILLA_FACE_OCCLUSION_UNKNOWN ||
                            iodfeature.occlusionType == IntelligentObjectDetector.Face_Occlusion_Type.GORILLA_FACE_OCCLUSION_DETECTED){
                        //mask on face
                        return;
                    }


                    if(DeviceUtils.mIsLivenessOn){
                        LOG.D(TAG,"mFaceDetectThread start iodfeature.liveness = " + iodfeature.liveness);
                        if(iodfeature.liveness.equals(IntelligentObjectDetector.Liveness_Type.IOD_LIVENESS_REAL)){
                            for(int i = 0 ; i < currentPngList.size() ; i++){
                                if(i == (currentPngList.size() - 1)){
                                    EnterpriseUtils.mFacePngList.add(currentPngList.get(i));
                                }
                            }

                            if(mLivenessCount >= MAX_LIVENESS_FAIL){
                                ClockUtils.mLiveness = Constants.LIVENESS_FAILED;
                            }else{
                                ClockUtils.mLiveness = Constants.LIVENESS_SUCCEED;
                            }

                            mLivenessCount = 0;
                        }else{
                            for(int i = 0 ; i < currentPngList.size() ; i++){
                                if(i == (currentPngList.size() - 1)){
                                    EnterpriseUtils.mFacePngList.add(currentPngList.get(i));
                                }
                            }
//                        mLivenessCount++;
                            ClockUtils.mLiveness = Constants.LIVENESS_FAILED;

                            mActivityCallbackHandler.sendEmptyMessage(Constants.GET_FACE_FAIL);
                            mFragmentCallbackHandler.sendEmptyMessage(Constants.GET_FACE_FAIL);
                            mIsOnPause = true;
                        }

                    }else{
                        for(int i = 0 ; i < currentPngList.size() ; i++){
                            if(i == (currentPngList.size() - 1)){
                                EnterpriseUtils.mFacePngList.add(currentPngList.get(i));
                            }
                        }

                        mLivenessCount = 0;

//                        if(mLivenessCount >= MAX_LIVENESS_FAIL){
//                            ClockUtils.mLiveness = "FAILED";
//                        }else{
//                            ClockUtils.mLiveness = "SUCCEED";
//                        }


                        ClockUtils.mLiveness = Constants.LIVENESS_OFF;

                    }

                    LOG.D(TAG,"mFaceDetectThread start mIsOnPause = " + mIsOnPause);
                    if (!mIsOnPause){
                        mActivityCallbackHandler.sendEmptyMessage(Constants.GET_FACE_SUCCESS);
                        mFragmentCallbackHandler.sendEmptyMessage(Constants.GET_FACE_SUCCESS);


                        //Test
//                        mActivityCallbackHandler.sendEmptyMessage(Constants.GET_FACE_FAIL);
//                        mFragmentCallbackHandler.sendEmptyMessage(Constants.GET_FACE_FAIL);
                        mIsOnPause = true;
                    }

                }else{
                    //no get face

                }


                //end determine
            } else{
                // show face forward
                mMainActivity.setIdentifyResult(mMainActivity.getString(R.string.please_face_forward_the_camera));
            }



        }

    };

    private FDRControl.FDRControlResponseListener onFdrCtrlResponse = new FDRControl.FDRControlResponseListener() {

        @Override
        public void onSearchFaceEventsResponse(int respCode, int faceNum, Date firstTime) {

            LOG.D(TAG,String.format("onSearchFaceEventsResponse - code:%d, num:%d, first:%s", respCode, faceNum, firstTime.toString()));
//            Log.i(TAG, String.format("onSearchFaceEventsResponse - code:%d, num:%d, first:%s", respCode, faceNum, firstTime.toString()));
//
//            if (respCode == 0) {
//
//                showEnroll();
//            } else {
//
//                Toast.makeText(MainActivity.this, String.format("Search face event error %d.", respCode), Toast.LENGTH_SHORT)
//                        .show();
//            }
        }

    };

    private FDRControl.cameraPreviewCallback camPreviewCallback = new FDRControl.cameraPreviewCallback()
    {
        @Override
        public void onCameraStarted() {
            LOG.D(TAG, "onCameraStarted");

            LOG.D(TAG, "onCameraStarted " + mFdrCtrl.getMaxExposure());
            LOG.D(TAG, "onCameraStarted " + mFdrCtrl.getMinExposure());
            LOG.D(TAG, "onCameraStarted " + mFdrCtrl.getExposure());
            LOG.D(TAG, "onCameraStarted " + mFdrCtrl.getExposureStep());
            LOG.D(TAG, "onCameraStarted " + mFdrCtrl.getExposure());
//            fdrCtrl.setExposure(10);
        }
        @Override
        public void onCameraPreview() {
//            LOG.D(TAG, "onCameraPreview");
//            fdrCtrl.setExposure(-10);
        }
    };

}
