package com.gorilla.attendance.enterprise;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gorilla.attendance.enterprise.Dialolg.ClockDialog;
import com.gorilla.attendance.enterprise.Dialolg.ImageDialog;
import com.gorilla.attendance.enterprise.Dialolg.MessageDialog;
import com.gorilla.attendance.enterprise.Dialolg.RegisterDialog;
import com.gorilla.attendance.enterprise.database.bean.DeviceLoginBean;
import com.gorilla.attendance.enterprise.datamodel.BapIdentifyModel;
import com.gorilla.attendance.enterprise.datamodel.BapVerifyModel;
import com.gorilla.attendance.enterprise.datamodel.EmployeeModel;
import com.gorilla.attendance.enterprise.datamodel.LoginModel;
import com.gorilla.attendance.enterprise.datamodel.RecordsReplyModel;
import com.gorilla.attendance.enterprise.datamodel.VisitorModel;
import com.gorilla.attendance.enterprise.fragment.ChooseModeFragment;
import com.gorilla.attendance.enterprise.fragment.ChooseModuleFragment;
import com.gorilla.attendance.enterprise.fragment.ConfigureSettingBluetoothFragment;
import com.gorilla.attendance.enterprise.fragment.ConfigureSettingEndPointFragment;
import com.gorilla.attendance.enterprise.fragment.ConfigureSettingLoginFragment;
import com.gorilla.attendance.enterprise.fragment.EnglishPinCodeFragment;
import com.gorilla.attendance.enterprise.fragment.FaceIconFragment;
import com.gorilla.attendance.enterprise.fragment.FaceIdentificationFragment;
import com.gorilla.attendance.enterprise.fragment.QrCodeFragment;
import com.gorilla.attendance.enterprise.fragment.RFIDFragment;
import com.gorilla.attendance.enterprise.fragment.RegisterEmployeeFragment;
import com.gorilla.attendance.enterprise.fragment.RegisterInputIdFragment;
import com.gorilla.attendance.enterprise.fragment.RegisterVisitorFragment;
import com.gorilla.attendance.enterprise.fragment.VideoFragment;
import com.gorilla.attendance.enterprise.receiver.BootBroadcastReceiver;
import com.gorilla.attendance.enterprise.service.BluetoothLeService;
import com.gorilla.attendance.enterprise.service.RemoteService;
import com.gorilla.attendance.enterprise.util.ApiAccessor;
import com.gorilla.attendance.enterprise.util.ApiUtils;
import com.gorilla.attendance.enterprise.util.CallbackUtils;
import com.gorilla.attendance.enterprise.util.ClockUtils;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.DeviceUtils;
import com.gorilla.attendance.enterprise.util.EnterpriseUtils;
import com.gorilla.attendance.enterprise.util.FDRControlManager;
import com.gorilla.attendance.enterprise.util.IdentifyEmployeeManager;
import com.gorilla.attendance.enterprise.util.IdentifyVisitorManager;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.LocalBroadcastHelper;
import com.gorilla.attendance.enterprise.util.WebSocketManager;
import com.gorilla.attendance.enterprise.util.apitask.BapIdentifyTask;
import com.gorilla.attendance.enterprise.util.apitask.BapVerifyTask;
import com.gorilla.attendance.enterprise.util.apitask.listener.IAccessUnrecognizedLogListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IAccessVisitorUnrecognizedLogListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IAttendanceUnrecognizedLogListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IBapIdentifyListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IBapVerifyListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceAccessRecordsListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceAttendanceRecordsListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceLoginListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceVisitorAccessRecordsListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceVisitorRecordsListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IVisitorsUnrecognizedLogListener;
import com.gorilla.enroll.lib.util.EnrollUtil;
import com.gorilla.enroll.lib.util.ObbFileManager;
import com.gorilla.enroll.lib.util.RetryStateManager;
import com.gorilla.enroll.lib.widget.OBBProgressView;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.gorilla.attendance.enterprise.util.ClockUtils.mRoleModel;
import static com.gorilla.attendance.enterprise.util.Constants.CLOSE_IMAGE_DIALOG;
import static com.gorilla.attendance.enterprise.util.DeviceUtils.MESSAGE_DIALOG_DELAYED_TIME;
import static com.gorilla.attendance.enterprise.util.DeviceUtils.PAGE_DELAYED_TIME;

//import com.gorilla.enroll.util.EnrollUtil;

public class MainActivity extends FragmentActivity implements EasyPermissions.PermissionCallbacks {
    private final static String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 1001;

    private Context mContext;

    private RelativeLayout mLayoutActionbar = null;
    private TextView mTxtActionBarTitle = null;
    private TextView mTxtActionBarSubTitle = null;
    private TextView mTxtActionCurrentTime = null;

    private RelativeLayout mLayoutFooterBar = null;
    private RelativeLayout mLayoutFooterBarLeft = null;
    private TextView mTxtBackToIndex = null;

    private RelativeLayout mLayoutMarqeuu = null;
    private TextView mTxtMarquee = null;
    private RelativeLayout mLayoutIdentifyResult = null;
    private TextView mTxtIdentifyResult = null;

    private TextView mTxtDeviceName = null;
    private ImageView mImgSocketState = null;

    private ChooseModuleFragment mChooseModuleFragment = null;
    private ChooseModeFragment mChooseModeFragment = null;
    //    private PinCodeFragment mPinCodeFragment = null;
    private EnglishPinCodeFragment mEnglishPinCodeFragment = null;
    private FaceIconFragment mFaceIconFragment = null;
    private FaceIdentificationFragment mFaceIdentificationFragment = null;
    private QrCodeFragment mQrCodeFragment = null;
    private RFIDFragment mRFIDFragment = null;

    private VideoFragment mVideoFragment = null;
    private ConfigureSettingLoginFragment mConfigureSettingLoginFragment = null;

    private boolean mIsFromMotp = false;
    private ProgressBar mProgress = null;
    private ClockDialog mClockDialog = null;
    private MessageDialog mMessageDialog = null;
    private ImageDialog mImageDialog = null;
    private RegisterDialog mRegisterDialog = null;

    private SharedPreferences mSharedPreference = null;

    private Handler mCurrentTimeHandler = new Handler();

    private Messenger mMessenger;
    private boolean mBounded;

    private LinearLayout mLayoutSocketState = null;
    private static int mSocketStateClickCount = 0;
    private static final int SOCKET_CLICK_INTERVAL = 5000;
    private static final int MAX_SOCKET_CLICK_COUNT = 5;
    private long mSocketStatePressTime = -1;

    private boolean mIsBluetoothServiceBinded = false;

    private MediaPlayer mPlayer = null;

    private ObbFileManager mObbFileManager = null;
    private OBBProgressView mOBBProgressView = null;

//    private RelativeLayout mLayoutAllView = null;
//    private SurfaceView mSurfaceViewSplash = null;
//    private MediaPlayer mMediaPlayer = null;

    private BapIdentifyTask bapIdentifyTask = null;
    private BapVerifyTask bapVerifyTask = null;

    private int retry;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LOG.D(TAG, "onRequestPermissionsResult permissions = " + permissions);
        LOG.D(TAG, "onRequestPermissionsResult grantResults = " + grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        LOG.D(TAG, "onPermissionsGranted");
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsDenied() called with: requestCode = [" + requestCode + "], perms = [" + perms.size() + "]");

        //不要再詢問
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setRationale(R.string.common_permisson_message)
                    .build()
                    .show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LOG.D(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        mSharedPreference = this.getSharedPreferences(Constants.PREF_NAME_ATTENDANCE_ENTERPRISE, Context.MODE_PRIVATE);
        retry = mSharedPreference.getInt(Constants.PREF_KEY_RETRY, 3);


        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
//            initiateAudio();
            // Already have permission, do the thing
            // ...
        } else {
            // Do not have permissions, request them now
            LOG.D(TAG, "HERE");
            EasyPermissions.requestPermissions(this, "WRITE_EXTERNAL_STORAGE",
                    1, perms);
        }

        CallbackUtils.setContext(mContext, mHandler);
        ClockUtils.mSerialNumber = mSharedPreference.getInt(Constants.PREF_KEY_SERIAL_NUMBER, 0);
        initActionBar();
        initFooterBar();
        initView();
        initImageLoader();
        createFolder();
        initScreenTimeout();

//        loadAppSetting(this);
        loadAppSettingFromSharedPreference();

//        turnBlueToothOn();

        EnrollUtil.init(mContext);
        mObbFileManager = new ObbFileManager.Builder(this)
                .setListener(mOBBProgressView)
                .setPublicKey(EnterpriseUtils.PUBLIC_KEY)
                .setAPKS(EnterpriseUtils.xAPKS)
                .setFDRSoFileLength(223159052L)//.so file size 1.5.2
//                .setFDRSoFileLength(223158988L)//.so file size 1.5.1
//                .setFDRSoFileLength(223158988L)//.so file size 1.5.0
//                .setFDRSoFileLength(229801860L)//.so file size 1.4.18, 20
//                .setFDRSoFileLength(229801796L)//.so file size 1.4.16
//                .setFDRSoFileLength(223158988L)//.so file size 1.4.15
//                .setFDRSoFileLength(252076052L)//.so file size 1.4.14
//                .setFDRSoFileLength(251959268L)//.so file size
                .setCompleteListener(new RetryStateManager.CompleteListener() {
                    @Override
                    public void onComplete(boolean success) {
                        LOG.D(TAG, "onComplete success = " + success);
                        mOBBProgressView.onComplete(success);
                        if (!success) {
                            return;
                        }
//                        LOG.D(TAG,"EnrollUtil.FDR_CONTROL = " + EnrollUtil.FDR_CONTROL);
//                        if(EnrollUtil.FDR_CONTROL == null){
//                            EnrollUtil.FDR_CONTROL = new FDRControl(MainActivity.this, FDRControl.Mode.RECOGNIZE, FDRControl.FaceSource.CAMERA, 1, 0,EnrollUtil.ENROLL_INTERNAL_BIN_FOLDER);
//                        }
//                        EnrollUtil.checkCameraPermission(MainActivity.this);

                        EnterpriseUtils.checkCameraPermission(MainActivity.this);

                        LOG.D(TAG, "onComplete DeviceUtils.mLoginStatus = " + DeviceUtils.mLoginStatus);
                        if (DeviceUtils.mLoginStatus == Constants.LOGIN_STATUS_LOGOUT) {
                            //check if in register page
                            if (getSupportFragmentManager().findFragmentByTag(ConfigureSettingLoginFragment.TAG) != null ||
                                    getSupportFragmentManager().findFragmentByTag(ConfigureSettingEndPointFragment.TAG) != null ||
                                    getSupportFragmentManager().findFragmentByTag(ConfigureSettingBluetoothFragment.TAG) != null ||
                                    getSupportFragmentManager().findFragmentByTag(RegisterInputIdFragment.TAG) != null ||
                                    getSupportFragmentManager().findFragmentByTag(RegisterEmployeeFragment.TAG) != null ||
                                    getSupportFragmentManager().findFragmentByTag(RegisterVisitorFragment.TAG) != null) {
                                LOG.D(TAG, "onComplete in config or register page");
                                return;
                            }

                            //show message
                            initMessageDialog(getString(R.string.txt_initial), 0, false, mHandler);
                            mMessageDialog.show();

                            String clientIp = EnterpriseUtils.getDeviceIPAddress(true);
                            //Api Login
                            mProgress.setVisibility(View.VISIBLE);

                            //            ApiUtils.login(TAG, mContext, DeviceUtils.mDeviceName, clientIp, loginListener);
                            //            ApiUtils.deviceLogin(TAG, mContext, "0EABFCE5-2AD7-4A66-B829-95F4F84A8663", "Android", clientIp, deviceLoginListener);
                            ApiUtils.deviceLogin(TAG, mContext, DeviceUtils.mDeviceName, "Android", clientIp, deviceLoginListener);

                        } else {

                            if (getSupportFragmentManager().findFragmentByTag(ConfigureSettingLoginFragment.TAG) != null ||
                                    getSupportFragmentManager().findFragmentByTag(ConfigureSettingEndPointFragment.TAG) != null ||
                                    getSupportFragmentManager().findFragmentByTag(ConfigureSettingBluetoothFragment.TAG) != null ||
                                    getSupportFragmentManager().findFragmentByTag(RegisterInputIdFragment.TAG) != null ||
                                    getSupportFragmentManager().findFragmentByTag(RegisterEmployeeFragment.TAG) != null ||
                                    getSupportFragmentManager().findFragmentByTag(RegisterVisitorFragment.TAG) != null) {
                                LOG.D(TAG, "login onComplete in config or register page  ");

                            } else {
                                mHandler.removeMessages(Constants.LAUNCH_VIDEO);
                                mHandler.sendEmptyMessageDelayed(Constants.LAUNCH_VIDEO, DeviceUtils.VIDEO_DELAYED_TIME);
                            }


                            mHandler.removeMessages(Constants.MSG_CHECK_USER_CLOCK_DB);
                            mHandler.sendEmptyMessageDelayed(Constants.MSG_CHECK_USER_CLOCK_DB, DeviceUtils.CHECK_USER_CLOCK_DB_DELAYED_TIME);

                            mHandler.removeMessages(Constants.MSG_CHECK_WEB_SOCKET_ALIVE);
                            mHandler.sendEmptyMessageDelayed(Constants.MSG_CHECK_WEB_SOCKET_ALIVE, DeviceUtils.CHECK_WEB_SOCKET_TIME);

                            LOG.D(TAG, "DeviceUtils.mLocale = " + DeviceUtils.mLocale);
                            //set language
                            DeviceUtils.setLanguage(mContext, DeviceUtils.mLocale);

                        }

                        mTxtDeviceName.setText(DeviceUtils.mDeviceShowName);

                        mTxtActionBarTitle.setVisibility(View.VISIBLE);
                        mTxtActionBarSubTitle.setVisibility(View.VISIBLE);
                        mLayoutFooterBarLeft.setVisibility(View.VISIBLE);

                        IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction(Constants.BROADCAST_RECEIVER_DAY_PASS);
                        LocalBroadcastHelper.registerReceiver(mContext, mStatusListener, intentFilter);
                    }
                }).build();

        mOBBProgressView.setOnPauseButtonListener(new OBBProgressView.OnButtonListener() {
            @Override
            public void onPauseClick(boolean isPause) {
                if (isPause) {
                    mObbFileManager.pause();
                } else {
                    mObbFileManager.start();
                }
            }

            @Override
            public void onRetry() {
                mObbFileManager.start();
            }
        });


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bindService(new Intent(mContext, RemoteService.class), mConnection, BIND_AUTO_CREATE);

                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                if (mBluetoothAdapter.isEnabled()) {
                    //blue id enable
                    String bluetoothDoorAddress = mSharedPreference.getString(Constants.PREF_KEY_BLUETOOTH_DOOR_ADDRESS, null);
                    LOG.D(TAG, "onStart bluetoothDoorAddress = " + bluetoothDoorAddress);
                    if (bluetoothDoorAddress != null) {
                        Intent bluetoothLeService = new Intent(mContext, BluetoothLeService.class);
                        startService(bluetoothLeService);
                        bindService(bluetoothLeService, mBluetoothServiceConnection, BIND_AUTO_CREATE);

                    }
                }

                registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            }
        }, 3000);


    }

    @Override
    protected void onStart() {
        super.onStart();
        LOG.V(TAG, "onStart() - start");


    }


    @Override
    protected void onResume() {
        LOG.D(TAG, "onResume");
        super.onResume();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Animation fadeInAnimation = AnimationUtils.loadAnimation(
//                        MainActivity.this, R.anim.fade_in_view);
//                mLayoutAllView.startAnimation(fadeInAnimation);
//                fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
//
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//                        mLayoutAllView.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//                        // TODO Auto-generated method stub
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        // TODO Auto-generated method stub
//
//                    }
//                });
//            }
//
//        }, 1000);


        //Temp commend
        final Intent intent = getIntent();
        mOBBProgressView.setVisiableWithAnimate(View.VISIBLE);
        mObbFileManager.start();

        if (intent.hasExtra("IsBackFromMotp")) {
            mIsFromMotp = intent.getBooleanExtra("IsBackFromMotp", false);
            intent.removeExtra("IsBackFromMotp");
        } else {
            mIsFromMotp = false;
        }

        LOG.D(TAG, "mIsFromMotp = " + mIsFromMotp);
//        String account = ApiAccessor.INNO_LUX_ACCOUNT;
//        String apiKey = ApiAccessor.INNO_LUX_POST_MESSAGE_API_KEY;
//        String teamSn = ApiAccessor.INNO_LUX_TEAM_SN;
//        String contentType = "1";
//        String textContent = "Hi";
//        String mediaContent = "";
//        String fileShowName = "";
//        String subject = "Title1";
//        JSONArray accountJsonArray = new JSONArray();
//        accountJsonArray.put("YUNGYIN.HSU");
//
//        LOG.D(TAG,"accountJsonArray.toString() = " + accountJsonArray.toString());
//
//        ApiUtils.postMessageLimited(TAG, mContext, account, apiKey, teamSn, contentType, textContent, mediaContent, fileShowName, subject, accountJsonArray.toString(), postMessageLimitedListener);


//        EnterpriseUtils.checkCameraPermission(MainActivity.this);
//
//        LOG.D(TAG,"onResume DeviceUtils.mLoginStatus = " + DeviceUtils.mLoginStatus);
//        if(DeviceUtils.mLoginStatus == Constants.LOGIN_STATUS_LOGOUT){
//            //check if in register page
//            if(getSupportFragmentManager().findFragmentByTag(ConfigureSettingLoginFragment.TAG) != null ||
//                    getSupportFragmentManager().findFragmentByTag(ConfigureSettingEndPointFragment.TAG) != null ||
//                    getSupportFragmentManager().findFragmentByTag(ConfigureSettingBluetoothFragment.TAG) != null ||
//                    getSupportFragmentManager().findFragmentByTag(RegisterInputIdFragment.TAG) != null ||
//                    getSupportFragmentManager().findFragmentByTag(RegisterEmployeeFragment.TAG) != null ||
//                    getSupportFragmentManager().findFragmentByTag(RegisterVisitorFragment.TAG) != null ){
//                LOG.D(TAG,"onResume in config or register page");
//                return;
//            }
//
//            //show message
//            initMessageDialog(getString(R.string.txt_initial), 0, false, mHandler);
//            mMessageDialog.show();
//
//            String clientIp = EnterpriseUtils.getDeviceIPAddress(true);
//            //Api Login
//            mProgress.setVisibility(View.VISIBLE);
//
////            ApiUtils.login(TAG, mContext, DeviceUtils.mDeviceName, clientIp, loginListener);
////            ApiUtils.deviceLogin(TAG, mContext, "0EABFCE5-2AD7-4A66-B829-95F4F84A8663", "Android", clientIp, deviceLoginListener);
//            ApiUtils.deviceLogin(TAG, mContext, DeviceUtils.mDeviceName, "Android", clientIp, deviceLoginListener);
//
//        }else{
//
//            if(getSupportFragmentManager().findFragmentByTag(ConfigureSettingLoginFragment.TAG) != null ||
//                    getSupportFragmentManager().findFragmentByTag(ConfigureSettingEndPointFragment.TAG) != null ||
//                    getSupportFragmentManager().findFragmentByTag(ConfigureSettingBluetoothFragment.TAG) != null ||
//                    getSupportFragmentManager().findFragmentByTag(RegisterInputIdFragment.TAG) != null ||
//                    getSupportFragmentManager().findFragmentByTag(RegisterEmployeeFragment.TAG) != null ||
//                    getSupportFragmentManager().findFragmentByTag(RegisterVisitorFragment.TAG) != null ){
//                LOG.D(TAG,"login onResume in config or register page  ");
//
//            }else{
//                mHandler.removeMessages(Constants.LAUNCH_VIDEO);
//                mHandler.sendEmptyMessageDelayed(Constants.LAUNCH_VIDEO, DeviceUtils.VIDEO_DELAYED_TIME);
//            }
//
//
//            mHandler.removeMessages(Constants.MSG_CHECK_USER_CLOCK_DB);
//            mHandler.sendEmptyMessageDelayed(Constants.MSG_CHECK_USER_CLOCK_DB, DeviceUtils.CHECK_USER_CLOCK_DB_DELAYED_TIME);
//
//            mHandler.removeMessages(Constants.MSG_CHECK_WEB_SOCKET_ALIVE);
//            mHandler.sendEmptyMessageDelayed(Constants.MSG_CHECK_WEB_SOCKET_ALIVE, DeviceUtils.CHECK_WEB_SOCKET_TIME);
//
//            //set language
//            DeviceUtils.setLanguage(mContext, DeviceUtils.mLocale);
//
//        }
//
//        mTxtDeviceName.setText(DeviceUtils.mDeviceShowName);
//
//        mTxtActionBarTitle.setVisibility(View.VISIBLE);
//        mTxtActionBarSubTitle.setVisibility(View.VISIBLE);
//        mLayoutFooterBarLeft.setVisibility(View.VISIBLE);
//
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(Constants.BROADCAST_RECEIVER_DAY_PASS);
//        LocalBroadcastHelper.registerReceiver(this, mStatusListener, intentFilter);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        LOG.D(TAG, "onNewIntent intent.getAction() = " + intent.getAction());
//        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
//            Log.d(TAG, "onNewIntent Tag Id : " + ByteArrayToHexString(intent
//                    .getByteArrayExtra(NfcAdapter.EXTRA_ID)));
//        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LOG.D(TAG, "onBackPressed ");
        if (bapIdentifyTask != null) {
            bapIdentifyTask.cancel(true);
        }
        if (bapVerifyTask != null) {
            bapVerifyTask.cancel(true);
        }

        mProgress.setVisibility(View.GONE);
        mTxtIdentifyResult.setText("");

    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO Auto-generated method stub
        LOG.V(TAG, "onPause() - start");


        if (getSupportFragmentManager().findFragmentByTag(ConfigureSettingLoginFragment.TAG) != null ||
                getSupportFragmentManager().findFragmentByTag(ConfigureSettingEndPointFragment.TAG) != null ||
                getSupportFragmentManager().findFragmentByTag(ConfigureSettingBluetoothFragment.TAG) != null ||
                getSupportFragmentManager().findFragmentByTag(RegisterInputIdFragment.TAG) != null ||
                getSupportFragmentManager().findFragmentByTag(RegisterEmployeeFragment.TAG) != null ||
                getSupportFragmentManager().findFragmentByTag(RegisterVisitorFragment.TAG) != null) {

        } else {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ClockUtils.clearClockData();
        }


        //remove all message
        mHandler.removeMessages(Constants.GET_FACE_SUCCESS);
        mHandler.removeMessages(Constants.GET_FACE_FAIL);
        mHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
//        mHandler.removeMessages(Constants.SET_ACTIVITY_UI);
        mHandler.removeMessages(Constants.OFFLINE_FACE_VERIFY_SUCCESS);
        mHandler.removeMessages(Constants.OFFLINE_FACE_VERIFY_FAIL);
        mHandler.removeMessages(Constants.CLOSE_CLOCK_DIALOG);
        mHandler.removeMessages(Constants.SEND_ATTENDANCE_RECOGNIZED_LOG);
        mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
        mHandler.removeMessages(Constants.VIDEO_PATH_DONE);
        mHandler.removeMessages(Constants.LAUNCH_VIDEO);
        mHandler.removeMessages(Constants.CLOSE_MESSAGE_DIALOG);
        mHandler.removeMessages(Constants.CLOSE_VIDEO);
        mHandler.removeMessages(Constants.CLOSE_APP);

        mHandler.removeMessages(Constants.CLOSE_IMAGE_DIALOG);
        mHandler.removeMessages(Constants.MSG_WEB_SOCKET_CONNECT);
        mHandler.removeMessages(Constants.MSG_WEB_SOCKET_DISCONNECT);

        mHandler.removeMessages(Constants.MSG_UPDATE_MARQUEE);
        mHandler.removeMessages(Constants.MSG_RESTART);
        mHandler.removeMessages(Constants.MSG_CHECK_WEB_SOCKET_ALIVE);
        mHandler.removeMessages(Constants.MSG_DO_API);


        mObbFileManager.stop();

        LOG.V(TAG, "onPause() - end");
    }

    @Override
    protected void onStop() {
        super.onStop();

        LOG.V(TAG, "onStop() - start");

        LOG.V(TAG, "onStop() - mBounded = " + mBounded);
//        if(mBounded) {
//            unbindService(mConnection);
//        }
//
//        if(mIsBluetoothServiceBinded) {
//            unbindService(mBluetoothServiceConnection);
//            mIsBluetoothServiceBinded = false;
//        }

        LocalBroadcastHelper.unregisterReceiver(this, mStatusListener);

//        unregisterReceiver(mGattUpdateReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LOG.D(TAG, "onDestroy() - start");

        mHandler.removeMessages(Constants.MSG_CHECK_USER_CLOCK_DB);
        mHandler.removeMessages(Constants.SET_ACTIVITY_UI);
//        mHandler.removeMessages(Constants.OFFLINE_FACE_VERIFY_SUCCESS);
//        mHandler.removeMessages(Constants.OFFLINE_FACE_VERIFY_FAIL);
//        mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
//        mHandler.re

        DeviceUtils.mLoginStatus = Constants.LOGIN_STATUS_LOGOUT;
        DeviceUtils.clearDeviceSetting();

        WebSocketManager.getInstance(mContext).disconnect();

        if (mBounded) {
            unbindService(mConnection);
        }

        if (mIsBluetoothServiceBinded) {
//            mBluetoothService = null;
            DeviceUtils.mBluetoothLeService.disconnect();
            unbindService(mBluetoothServiceConnection);
            mIsBluetoothServiceBinded = false;

        }

        unregisterReceiver(mGattUpdateReceiver);

//        if(mMediaPlayer != null){
//            mMediaPlayer.stop();
//            mMediaPlayer.reset();
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//        }


    }

    @Override
    public void onUserInteraction() {
        LOG.D(TAG, "onUserInteraction getSupportFragmentManager().getBackStackEntryCount() = " + getSupportFragmentManager().getBackStackEntryCount());
        super.onUserInteraction();

        //check if in index page
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, PAGE_DELAYED_TIME);

            //setting page not do this
            if (getSupportFragmentManager().findFragmentByTag(ConfigureSettingLoginFragment.TAG) != null) {
                mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.SETTING_DELAYED_TIME);
//                return;
            }

            if (getSupportFragmentManager().findFragmentByTag(ConfigureSettingEndPointFragment.TAG) != null) {
                mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.SETTING_DETAIL_DELAYED_TIME);
//                return;
            }

            if (getSupportFragmentManager().findFragmentByTag(ConfigureSettingBluetoothFragment.TAG) != null) {
                mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.SETTING_DETAIL_DELAYED_TIME);
//                return;
            }

            if (getSupportFragmentManager().findFragmentByTag(QrCodeFragment.TAG) != null) {
                mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.SETTING_DETAIL_DELAYED_TIME);
//                return;
            }

        } else {
            //in index page
            mHandler.removeMessages(Constants.LAUNCH_VIDEO);
            mHandler.sendEmptyMessageDelayed(Constants.LAUNCH_VIDEO, DeviceUtils.VIDEO_DELAYED_TIME);
        }

    }

    private void turnBlueToothOn() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        LOG.D(TAG, "mBluetoothAdapter.isEnabled() = " + mBluetoothAdapter.isEnabled());
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    private void turnGpsOn() {


        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
            //ask to turn gps on
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(mContext.getString(R.string.location_permission))
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }

    }

    private void initActionBar() {
        mLayoutActionbar = (RelativeLayout) findViewById(R.id.top_action_bar);
        mTxtActionBarTitle = (TextView) mLayoutActionbar.findViewById(R.id.txt_action_bar_title);
        mTxtActionBarSubTitle = (TextView) mLayoutActionbar.findViewById(R.id.txt_action_bar_sub_title);
        mTxtActionCurrentTime = (TextView) mLayoutActionbar.findViewById(R.id.txt_current_time);

//        mTimerHandler.removeMessages(Constants.SET_TIMER);
//        mTimerHandler.sendEmptyMessageDelayed(Constants.SET_TIMER, DeviceUtils.TIMER_DELAYED_TIME);


        mCurrentTimeHandler.postDelayed(updateTimerThread, DeviceUtils.TIMER_DELAYED_TIME);


    }

    private void initFooterBar() {
        mLayoutFooterBar = (RelativeLayout) findViewById(R.id.footer_bar);

        mLayoutFooterBarLeft = (RelativeLayout) mLayoutFooterBar.findViewById(R.id.layout_footer_bar_left);

        mLayoutMarqeuu = (RelativeLayout) mLayoutFooterBar.findViewById(R.id.layout_marquee);
        mTxtMarquee = (TextView) mLayoutFooterBar.findViewById(R.id.txt_marquee);

        mLayoutIdentifyResult = (RelativeLayout) mLayoutFooterBar.findViewById(R.id.layout_identify_result);
        mTxtIdentifyResult = (TextView) mLayoutFooterBar.findViewById(R.id.txt_identify_result);

        mTxtDeviceName = (TextView) mLayoutFooterBar.findViewById(R.id.txt_client_name);
        mLayoutSocketState = (LinearLayout) mLayoutFooterBar.findViewById(R.id.layout_socket_state);

        mImgSocketState = (ImageView) mLayoutFooterBar.findViewById(R.id.img_socket_state);
        mTxtBackToIndex = (TextView) mLayoutFooterBar.findViewById(R.id.btn_back_to_index);


        mTxtBackToIndex.setOnClickListener(mTxtBackToIndexClickListener);


    }

    private void initView() {
        mProgress = (ProgressBar) findViewById(R.id.marker_progress);

        mOBBProgressView = (OBBProgressView) findViewById(R.id.obb_download_progress);
    }

    private void initImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
//            .diskCache(new UnlimitedDiscCache(cacheDir)) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(20 * 1024 * 1024))
                .diskCacheFileCount(100)
                .defaultDisplayImageOptions(EnterpriseUtils.getCustomDisplayImageOptions(R.mipmap.icon_default_photo))
//            .defaultDisplayImageOptions(options)
//                .diskCacheExtraOptions(480, 320, null)
                .threadPoolSize(10)
                .build();

        ImageLoader.getInstance().init(config);
    }

    private void initMessageDialog(String message, int delayedTime, boolean isNeedToFinishApp, Handler callback) {
        if (mMessageDialog != null) {
            if (mMessageDialog.isShowing()) {
                mMessageDialog.dismiss();
            }
        }
        mMessageDialog = new MessageDialog(mContext, message, isNeedToFinishApp, callback);
        if (delayedTime != 0) {
            mHandler.removeMessages(Constants.CLOSE_MESSAGE_DIALOG);
            mHandler.sendEmptyMessageDelayed(Constants.CLOSE_MESSAGE_DIALOG, MESSAGE_DIALOG_DELAYED_TIME);
            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, MESSAGE_DIALOG_DELAYED_TIME);
        }

    }

    private void initImageDialog(int delayedTime, int imageId, String message, long time, Handler callback) {
        mImageDialog = new ImageDialog(mContext, imageId, message, time, callback);
        if (delayedTime != 0) {
            mHandler.removeMessages(CLOSE_IMAGE_DIALOG);
            mHandler.sendEmptyMessageDelayed(CLOSE_IMAGE_DIALOG, delayedTime);
            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, delayedTime);
        }


    }

    private void initRegisterDialog(int delayedTime, String message, String idTitle, String id, String nameTitle, String name, Handler callback) {
        mRegisterDialog = new RegisterDialog(mContext, message, idTitle, id, nameTitle, name, callback);
        if (delayedTime != 0) {
            mHandler.removeMessages(Constants.MSG_CLOSE_REGISTER_DIALOG);
            mHandler.sendEmptyMessageDelayed(Constants.MSG_CLOSE_REGISTER_DIALOG, delayedTime);
            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, delayedTime);
        } else {
            mHandler.removeMessages(Constants.MSG_CLOSE_REGISTER_DIALOG);
            mHandler.sendEmptyMessageDelayed(Constants.MSG_CLOSE_REGISTER_DIALOG, DeviceUtils.MESSAGE_DIALOG_DELAYED_TIME);
            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.MESSAGE_DIALOG_DELAYED_TIME);
        }
    }

    private void doVisitorClockApi(boolean isRadioClockIn, boolean isRadioClockOut, boolean isVisitorAccessDoor) {
        LOG.D(TAG, "doVisitorClockApi isRadioClockIn = " + isRadioClockIn);
        LOG.D(TAG, "doVisitorClockApi isRadioClockOut = " + isRadioClockOut);
        LOG.D(TAG, "doVisitorClockApi isVisitorAccessDoor = " + isRadioClockIn);
        String type = "";
        int serialNumber = -1;
        if (isRadioClockIn == true) {
            ClockUtils.mType = Constants.VISITOR_VISIT;
            type = Constants.VISITOR_VISIT;
            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);

            ++ClockUtils.mSerialNumber;
            mSharedPreference.edit().putInt(Constants.PREF_KEY_SERIAL_NUMBER, ClockUtils.mSerialNumber).commit();
            mHandler.removeMessages(Constants.CLOSE_CLOCK_DIALOG);
            mProgress.setVisibility(View.VISIBLE);
//                        ApiUtils.visitorRecords(TAG, mContext, DeviceUtils.mDeviceName, false, visitorRecordsListener);
            ClockUtils.mRecordMode = Constants.RECORD_MODE_RECORD;

            LOG.D(TAG, "doVisitorClockApi isRadioClockIn ClockUtils.mLoginAccount = " + ClockUtils.mLoginAccount);
            ApiUtils.deviceVisitorRecords(TAG, mContext, DeviceUtils.mDeviceName, false, deviceVisitorRecordsListener);

            //check if do access door
            LOG.D(TAG, "doVisitorClockApi isRadioClockIn DeviceUtils.mIsVisitorOpenDoor = " + DeviceUtils.mIsVisitorOpenDoor);
            if (isVisitorAccessDoor == true) {

                LOG.D(TAG, "doVisitorClockApi isRadioClockIn type = " + type);
                LOG.D(TAG, "doVisitorClockApi isRadioClockIn ClockUtils.mLoginName = " + ClockUtils.mLoginName);
                LOG.D(TAG, "doVisitorClockApi isRadioClockIn ClockUtils.mLoginAccount = " + ClockUtils.mLoginAccount);
                serialNumber = ClockUtils.mSerialNumber;
                ApiUtils.deviceVisitorAccessRecords(TAG, mContext, DeviceUtils.mDeviceName, false, type, serialNumber, deviceVisitorAccessRecordsListener);
                setFaceIdentifyResultToClient(true, ClockUtils.mLoginName, ClockUtils.mLoginAccount);
                EnterpriseUtils.openDoorOne(mContext);

            }
        } else {


            ClockUtils.mType = Constants.VISITOR_LEAVE;
            type = Constants.VISITOR_LEAVE;
            //  TEMP FAKE
//                        ClockUtils.mLoginAccount = "0000";
//                        ClockUtils.mLoginUuid = "5b989ec7-ded7-4061-bb68-ec821e6a25cc";
            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);

            ++ClockUtils.mSerialNumber;
            mSharedPreference.edit().putInt(Constants.PREF_KEY_SERIAL_NUMBER, ClockUtils.mSerialNumber).commit();
            mHandler.removeMessages(Constants.CLOSE_CLOCK_DIALOG);
            mProgress.setVisibility(View.VISIBLE);
//                        ApiUtils.visitorRecords(TAG, mContext, DeviceUtils.mDeviceName, false, visitorRecordsListener);
            ClockUtils.mRecordMode = Constants.RECORD_MODE_RECORD;
            ApiUtils.deviceVisitorRecords(TAG, mContext, DeviceUtils.mDeviceName, false, deviceVisitorRecordsListener);

            //check if do access door
            LOG.D(TAG, "doVisitorClockApi isRadioClockOut DeviceUtils.mIsVisitorOpenDoor = " + DeviceUtils.mIsVisitorOpenDoor);
            if (DeviceUtils.mIsVisitorOpenDoor == true) {

                LOG.D(TAG, "doVisitorClockApi isRadioClockOut type = " + type);
                LOG.D(TAG, "doVisitorClockApi isRadioClockOut ClockUtils.mLoginName = " + ClockUtils.mLoginName);
                LOG.D(TAG, "doVisitorClockApi isRadioClockOut ClockUtils.mLoginAccount = " + ClockUtils.mLoginAccount);
                serialNumber = ClockUtils.mSerialNumber;
                ApiUtils.deviceVisitorAccessRecords(TAG, mContext, DeviceUtils.mDeviceName, false, type, serialNumber, deviceVisitorAccessRecordsListener);
                setFaceIdentifyResultToClient(true, ClockUtils.mLoginName, ClockUtils.mLoginAccount);

                EnterpriseUtils.openDoorOne(mContext);
            }

        }

    }

    private void launchFragment(int tag) {
        LOG.V(TAG, "[launchFragment] tag = " + tag);


        FragmentManager fm = getSupportFragmentManager();

        if (fm == null) {
            LOG.W(TAG, "[launchFragment] FragmentManager is null.");
            return;
        }

        // Clear back stack and keep base fragment when change main category.
        LOG.V(TAG, "onHomeButtonPressed() - fm.getBackStackEntryCount() = " + fm.getBackStackEntryCount());
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        FragmentTransaction ft = fm.beginTransaction();
        if (ft == null) {
            LOG.W(TAG, "[launchFragment] FragmentTransaction is null.");
            return;
        }

        Bundle arguments = new Bundle();

        switch (tag) {

            case Constants.FRAGMENT_TAG_CHOOSE_MODULE:
                mChooseModuleFragment = new ChooseModuleFragment();

//                arguments.putString(WebViewFragment.KEY_WEB_VIEW_URL, webViewUrl);
//                mHomeFragment.setArguments(arguments);

                ft.replace(Constants.CONTENT_FRAME_ID, mChooseModuleFragment, ChooseModuleFragment.TAG).commitAllowingStateLoss();
                break;

            case Constants.FRAGMENT_TAG_CHOOSE_MODE:
                mChooseModeFragment = new ChooseModeFragment();

                ft.replace(Constants.CONTENT_FRAME_ID, mChooseModeFragment, ChooseModeFragment.TAG).commitAllowingStateLoss();
                break;

            case Constants.FRAGMENT_TAG_PIN_CODE:
//                mPinCodeFragment = new PinCodeFragment();
//
//                ft.replace(Constants.CONTENT_FRAME_ID, mPinCodeFragment, PinCodeFragment.TAG).commitAllowingStateLoss();

//                mEnglishPinCodeFragment = new EnglishPinCodeFragment();
//
//                ft.replace(Constants.CONTENT_FRAME_ID, mEnglishPinCodeFragment, EnglishPinCodeFragment.TAG).commitAllowingStateLoss();
                break;

            case Constants.FRAGMENT_TAG_ENGLISH_PIN_CODE:

                mEnglishPinCodeFragment = new EnglishPinCodeFragment();

                ft.replace(Constants.CONTENT_FRAME_ID, mEnglishPinCodeFragment, EnglishPinCodeFragment.TAG).commitAllowingStateLoss();
                break;

            case Constants.FRAGMENT_TAG_FACE_ICON:
                mFaceIconFragment = new FaceIconFragment();

                ft.replace(Constants.CONTENT_FRAME_ID, mFaceIconFragment, FaceIconFragment.TAG).commitAllowingStateLoss();
                break;

            case Constants.FRAGMENT_TAG_FACE_IDENTIFICATION:
                mFaceIdentificationFragment = new FaceIdentificationFragment();

                ft.replace(Constants.CONTENT_FRAME_ID, mFaceIdentificationFragment, FaceIdentificationFragment.TAG).commitAllowingStateLoss();
                break;

            case Constants.FRAGMENT_TAG_QR_CODE:
                mQrCodeFragment = new QrCodeFragment();

                ft.replace(Constants.CONTENT_FRAME_ID, mQrCodeFragment, QrCodeFragment.TAG).commitAllowingStateLoss();
                break;

            case Constants.FRAGMENT_TAG_RFID:
                mRFIDFragment = new RFIDFragment();

                ft.replace(Constants.CONTENT_FRAME_ID, mRFIDFragment, RFIDFragment.TAG).commitAllowingStateLoss();
                break;


            case Constants.FRAGMENT_TAG_VIDEO:
                mVideoFragment = new VideoFragment();
                mHandler.removeMessages(Constants.LAUNCH_VIDEO);

                ft.replace(Constants.CONTENT_FRAME_ID, mVideoFragment, VideoFragment.TAG).addToBackStack(null).commitAllowingStateLoss();

                break;

            case Constants.FRAGMENT_TAG_CONF_LOGIN:
                mConfigureSettingLoginFragment = new ConfigureSettingLoginFragment();
                mHandler.removeMessages(Constants.LAUNCH_VIDEO);

                ft.replace(Constants.CONTENT_FRAME_ID, mConfigureSettingLoginFragment, ConfigureSettingLoginFragment.TAG).addToBackStack(null).commitAllowingStateLoss();

                break;


        }

    }

    private void launchFragment(int tag, String renewSecurityCode) {
        LOG.V(TAG, "[launchFragment] tag = " + tag);


        FragmentManager fm = getSupportFragmentManager();

        if (fm == null) {
            LOG.W(TAG, "[launchFragment] FragmentManager is null.");
            return;
        }

        // Clear back stack and keep base fragment when change main category.
        LOG.V(TAG, "onHomeButtonPressed() - fm.getBackStackEntryCount() = " + fm.getBackStackEntryCount());
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        FragmentTransaction ft = fm.beginTransaction();
        if (ft == null) {
            LOG.W(TAG, "[launchFragment] FragmentTransaction is null.");
            return;
        }

        Bundle arguments = new Bundle();

        switch (tag) {

            case Constants.FRAGMENT_TAG_ENGLISH_PIN_CODE:

                mEnglishPinCodeFragment = new EnglishPinCodeFragment();

                arguments.putString(EnglishPinCodeFragment.KEY_RENEW_SECURITY_CODE, renewSecurityCode);
                mEnglishPinCodeFragment.setArguments(arguments);

                ft.replace(Constants.CONTENT_FRAME_ID, mEnglishPinCodeFragment, EnglishPinCodeFragment.TAG).addToBackStack(EnglishPinCodeFragment.TAG).commitAllowingStateLoss();
                break;


        }

    }

    private void launchFragment(int tag, int pinCodeType) {
        LOG.V(TAG, "[launchFragment] tag = " + tag);


        FragmentManager fm = getSupportFragmentManager();

        if (fm == null) {
            LOG.W(TAG, "[launchFragment] FragmentManager is null.");
            return;
        }

        FragmentTransaction ft = fm.beginTransaction();
        if (ft == null) {
            LOG.W(TAG, "[launchFragment] FragmentTransaction is null.");
            return;
        }

        Bundle arguments = new Bundle();

        switch (tag) {

            case Constants.FRAGMENT_TAG_ENGLISH_PIN_CODE:

                mEnglishPinCodeFragment = new EnglishPinCodeFragment();

//                arguments.putString(EnglishPinCodeFragment.KEY_RENEW_SECURITY_CODE, renewSecurityCode);
                arguments.putInt(EnglishPinCodeFragment.KEY_PIN_CODE_TYPE, pinCodeType);
                mEnglishPinCodeFragment.setArguments(arguments);

                ft.replace(Constants.CONTENT_FRAME_ID, mEnglishPinCodeFragment, EnglishPinCodeFragment.TAG).addToBackStack(EnglishPinCodeFragment.TAG).commitAllowingStateLoss();
                break;


        }

    }


    private void createFolder() {
        File dir = new File(EnterpriseUtils.SD_CARD_APP);
        if (!dir.exists()) {
            dir.mkdir();
        }

        File dirContent = new File(EnterpriseUtils.SD_CARD_APP_CONTENT);// save video
        if (!dirContent.exists()) {
            dirContent.mkdir();
        }

        File dirApk = new File(EnterpriseUtils.SD_CARD_APP_APK);// save apk
        if (!dirApk.exists()) {
            dirApk.mkdir();
        }

        File dirLog = new File(EnterpriseUtils.SD_CARD_APP_LOG);// save log
        if (!dirLog.exists()) {
            dirLog.mkdir();
        }

        File dirFace = new File(EnterpriseUtils.SD_CARD_APP_FACE_IMAGE);// save image
        if (!dirFace.exists()) {
            dirFace.mkdir();
        }

    }

    private void initScreenTimeout() {
//        int time = Integer.MAX_VALUE;
//        android.provider.Settings.System.putInt(getContentResolver(),
//                Settings.System.SCREEN_OFF_TIMEOUT, time);
    }


    private void loadAppSettingFromSharedPreference() {

        String serverIp = mSharedPreference.getString(Constants.PREF_KEY_SERVER_IP, null);
        String ftpIp = mSharedPreference.getString(Constants.PREF_KEY_FTP_IP, null);
        String wsIp = mSharedPreference.getString(Constants.PREF_KEY_WS_IP, null);
        String ftpAccount = mSharedPreference.getString(Constants.PREF_KEY_FTP_ACCOUNT, null);
        String ftpPassword = mSharedPreference.getString(Constants.PREF_KEY_FTP_PASSWORD, null);
        String deviceToken = mSharedPreference.getString(Constants.PREF_KEY_DEVICE_TOKEN, null);
//        String deviceName = mSharedPreference.getString(Constants.PREF_KEY_DEVICE_NAME, null);
        int videoIdleSeconds = mSharedPreference.getInt(Constants.PREF_KEY_VIDEO_IDLE_SECONDS, 0);
        int syncTimeOutSecond = mSharedPreference.getInt(Constants.PREF_KEY_SYNC_TIME_OUT_SECONDS, 60);
        int identifyResultIdleMilliSeconds = mSharedPreference.getInt(Constants.PREF_KEY_IDENTIFY_RESULT_IDLE_MILLI_SECONDS, DeviceUtils.mResultMessageDelayTime);

        String rtspIp = mSharedPreference.getString(Constants.PREF_KEY_RTSP_IP, null);
        String rtspUrl = mSharedPreference.getString(Constants.PREF_KEY_RTSP_URL, null);
        String rtspAccount = mSharedPreference.getString(Constants.PREF_KEY_RTSP_ACCOUNT, null);
        String rtspPassword = mSharedPreference.getString(Constants.PREF_KEY_RTSP_PASSWORD, null);

        int fdrRange = mSharedPreference.getInt(Constants.PREF_KEY_FDR_RANGE, Constants.FDR_DEFAULT_RANGE);

        boolean isEmployeeOpenDoor = mSharedPreference.getBoolean(Constants.PREF_KEY_EMPLOYEE_OPEN_DOOR, true);
        boolean isVisitorOpenDoor = mSharedPreference.getBoolean(Constants.PREF_KEY_VISITOR_OPEN_DOOR, true);
        boolean isLiveness = mSharedPreference.getBoolean(Constants.PREF_KEY_LIVENESS, true);

        boolean isRtspSetting = mSharedPreference.getBoolean(Constants.PREF_KEY_RTSP_SETTING, false);

        boolean isClockAuto = mSharedPreference.getBoolean(Constants.PREF_KEY_CLOCK_AUTO, true);
        boolean isRadioClockIn = mSharedPreference.getBoolean(Constants.PREF_KEY_RADIO_CLOCK_IN, true);
        boolean isRadioClockOut = mSharedPreference.getBoolean(Constants.PREF_KEY_RADIO_CLOCK_OUT, false);
        boolean isOnlineMode = mSharedPreference.getBoolean(Constants.PREF_KEY_ONLINE_MODE, false);


        if (serverIp == null) {
            serverIp = "http://52.33.108.242";
//            serverIp = "http://116.62.217.179";
//            serverIp = "http://192.168.11.178";
//            serverIp = "http://192.168.11.158";
//            serverIp = "http://192.168.11.184";
//            serverIp = "http://203.67.6.200:8091";
//            serverIp = "http://192.168.11.151";


            mSharedPreference.edit().putString(Constants.PREF_KEY_SERVER_IP, serverIp).apply();
        }

        if (ftpIp == null) {
            ftpIp = "52.33.108.242";
//            ftpIp = "116.62.217.179";
//            ftpIp = "192.168.11.178";
//            ftpIp = "192.168.11.158";
//            ftpIp = "192.168.11.184";
//            ftpIp = "203.67.6.200:8091";
//            ftpIp = "192.168.11.151";

            mSharedPreference.edit().putString(Constants.PREF_KEY_FTP_IP, ftpIp).apply();
        }

        if (wsIp == null) {
            wsIp = "52.33.108.242";
//            wsIp = "116.62.217.179";
//            wsIp = "192.168.11.178";
//            wsIp = "192.168.11.158";
//            wsIp = "192.168.11.184";
//            wsIp = "203.67.6.200:8091";
//            wsIp = "192.168.11.151";

            mSharedPreference.edit().putString(Constants.PREF_KEY_WS_IP, wsIp).apply();
        }

        if (ftpAccount == null) {
            ftpAccount = "gorilla";
            mSharedPreference.edit().putString(Constants.PREF_KEY_FTP_ACCOUNT, ftpAccount).apply();
        }

        if (ftpPassword == null) {
            ftpPassword = "gorillakm";
            mSharedPreference.edit().putString(Constants.PREF_KEY_FTP_PASSWORD, ftpPassword).apply();
        }

        if (deviceToken == null) {
            deviceToken = "";
//            deviceToken = "120cfb23862e-4998aad4-2e86-d38d-c6d4";

            //GG Test
//            deviceToken = "GorillaVisitorSystem";

//            deviceToken = "69CA5053-1910-4505-AF6C-90D3D0A339D4";//178
//            deviceToken = "76eeb7f642b7-4ed48372-f41f-3aa8-e98b";//158
//            deviceToken = "67e5af04a02f-4d0e8580-0729-a930-b3ec";//184
//            deviceToken = "add10e7cd4c9-4cad9eb9-3bfd-6cbd-acfb";//52.33
//            deviceToken = "dd6fff07c502-4d4fa2c9-778f-db92-d48a";//http://203.67.6.200:8091
//            deviceToken = "4a842d96d2e0-406caf56-3e84-20d1-8222";//151
//            deviceToken = "2b6716ab245e-49deb9fa-abae-784f-02a8";//151 English


            mSharedPreference.edit().putString(Constants.PREF_KEY_DEVICE_TOKEN, deviceToken).apply();

        }

        if (rtspIp == null) {
            rtspIp = "192.168.4.31";
            mSharedPreference.edit().putString(Constants.PREF_KEY_RTSP_IP, rtspIp).apply();
        }

        if (rtspUrl == null) {
            rtspUrl = "rtsp://192.168.4.31/Streaming/Channels/2";
            mSharedPreference.edit().putString(Constants.PREF_KEY_RTSP_URL, rtspUrl).apply();
        }

        if (rtspAccount == null) {
            rtspAccount = "admin";
            mSharedPreference.edit().putString(Constants.PREF_KEY_RTSP_ACCOUNT, rtspAccount).apply();
        }

        if (rtspPassword == null) {
            rtspPassword = "12345";
            mSharedPreference.edit().putString(Constants.PREF_KEY_RTSP_PASSWORD, rtspPassword).apply();
        }

        if (videoIdleSeconds == 0) {
            videoIdleSeconds = 30;
            mSharedPreference.edit().putInt(Constants.PREF_KEY_VIDEO_IDLE_SECONDS, videoIdleSeconds).apply();
        }


        //Test
//        rtspUrl = "rtsp://192.168.10.21/LV/ch1";
//        rtspIp = "192.168.10.21";

        ApiAccessor.SERVER_IP = serverIp;
        ApiAccessor.FTP_IP = ftpIp;
        DeviceUtils.mWsIp = wsIp;
        DeviceUtils.mDeviceName = deviceToken;
//        DeviceUtils.mDeviceShowName = deviceName;
        DeviceUtils.mFtpAccount = ftpAccount;
        DeviceUtils.mFtpPassword = ftpPassword;

        DeviceUtils.mIsEmployeeOpenDoor = isEmployeeOpenDoor;
        DeviceUtils.mIsVisitorOpenDoor = isVisitorOpenDoor;

        DeviceUtils.mIsLivenessOn = isLiveness;
        DeviceUtils.mIsRtspSetting = isRtspSetting;

        DeviceUtils.mIsClockAuto = isClockAuto;
        DeviceUtils.mIsRadioClockIn = isRadioClockIn;
        DeviceUtils.mIsRadioClockOut = isRadioClockOut;
        DeviceUtils.mIsOnlineMode = isOnlineMode;


//        DeviceUtils.mIsInnoLux = isInnoLux;

        DeviceUtils.mRtspIp = rtspIp;
        DeviceUtils.mRtspUrl = rtspUrl;
        DeviceUtils.mRtspAccount = rtspAccount;
        DeviceUtils.mRtspPassword = rtspPassword;

        DeviceUtils.mFdrRange = fdrRange;

        LOG.D(TAG, "loadAppSettingFromSharedPreference fdrRange = " + fdrRange);

        DeviceUtils.VIDEO_DELAYED_TIME = (videoIdleSeconds * 1000);
        DeviceUtils.SYNC_TIME_OUT = (syncTimeOutSecond * 1000);
        DeviceUtils.mResultMessageDelayTime = identifyResultIdleMilliSeconds;

        try {
            DeviceUtils.mWsUri = new URI("ws://" + DeviceUtils.mWsIp + ApiAccessor.WEB_SOCKET_PATH);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

    }

    public void setHomeSettingWord(String settingWord) {
        mTxtBackToIndex.setText(settingWord);

    }

    public void setHomeSettingBackGround(int homeSettingBackGround) {
        mTxtBackToIndex.setBackgroundResource(homeSettingBackGround);
    }

    public void setIdentifyResult(String identifyResult) {
        mTxtIdentifyResult.setText(Html.fromHtml(identifyResult));
    }

    public void setMarqueeRequestFocus() {
        mTxtMarquee.requestFocus();
    }

    public TextView getMarquee() {
        return mTxtMarquee;
    }

    private IDeviceLoginListener deviceLoginListener = new IDeviceLoginListener() {
        @Override
        public void onDeviceLogin(LoginModel model) {
            //GG Test
//            model = null;
            LOG.D(TAG, "onLogin  model = " + model);
            mProgress.setVisibility(View.GONE);

            if (model != null) {
                LOG.D(TAG, "onLogin model.getStatus()  = " + model.getStatus());
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    //Success
                    LOG.D(TAG, "onLogin model.getLoginData().getModuleModes().size() = " + model.getLoginData().getModuleModes().size());

                    EnterpriseUtils.addDeviceLoginToDb(mContext, model);

                    //Init FDR and WebSocket
//                    FDRControlManager.getInstance(mContext).initFdr();
                    FDRControlManager.getInstance(mContext, MainActivity.this).initFdr();
                    WebSocketManager.getInstance(mContext).connect(DeviceUtils.mWsUri, DeviceUtils.WEB_SOCKET_TIME_OUT, mContext, mHandler);

                    DeviceUtils.mLoginStatus = Constants.LOGIN_STATUS_LOGIN;
                    DeviceUtils.mDeviceShowName = model.getLoginData().getDeviceName();

                    mSharedPreference.edit().putString(Constants.PREF_KEY_DEVICE_SHOW_NAME, model.getLoginData().getDeviceName()).apply();

                    //Set Device Info
                    DeviceUtils.setDeviceInfo(mContext, model.getLoginData().getLocale(), model.getLoginData().getBgImage(), model.getLoginData().getTitleImage());

                    //0. get Identify Image for offline mode
                    ApiUtils.getDeviceVerifiedIdAndImage(TAG, mContext, DeviceUtils.mDeviceName, CallbackUtils.getDeviceVerifiedIdAndImageListener);

                    //1. get Video
                    ApiUtils.getDeviceVideos(TAG, mContext, DeviceUtils.mDeviceName, false, CallbackUtils.getDeviceVideosListener);

                    //2. get Marquee
                    ApiUtils.getDeviceMarquees(TAG, mContext, DeviceUtils.mDeviceName, CallbackUtils.getDeviceMarqueesListener);

                    //3. record module and mode
                    for (int i = 0; i < model.getLoginData().getModuleModes().size(); i++) {
                        ClockUtils.mModule = model.getLoginData().getModuleModes().get(i).getModule();
                        if (model.getLoginData().getModuleModes().get(i).getModule() == Constants.MODULES_VISITORS) {
                            //Visitors
                            DeviceUtils.mVisitorModel = new VisitorModel();
                            DeviceUtils.mVisitorModel.setModules(model.getLoginData().getModuleModes().get(i).getModule());
                            DeviceUtils.mVisitorModel.setModes(model.getLoginData().getModuleModes().get(i).getMode());

                            ApiUtils.getDeviceVisitors(TAG, mContext, DeviceUtils.mDeviceName, CallbackUtils.getDeviceVisitorsListener);

                        } else {
                            //Employees
                            DeviceUtils.mEmployeeModel = new EmployeeModel();
                            DeviceUtils.mEmployeeModel.setModules(model.getLoginData().getModuleModes().get(i).getModule());
                            DeviceUtils.mEmployeeModel.setModes(model.getLoginData().getModuleModes().get(i).getMode());

                            ApiUtils.getDeviceEmployees(TAG, mContext, DeviceUtils.mDeviceName, CallbackUtils.getDeviceEmployeesListener);
                        }
                    }

                    registerDayPassAlarmManager();


                    LOG.D(TAG, "onLogin Constants.SET_ACTIVITY_UI");

                    mHandler.sendEmptyMessageDelayed(Constants.SET_ACTIVITY_UI, DeviceUtils.LIVING_LEARNING_TIME);

                } else {
                    // check DB Data, error first

                    LOG.D(TAG, "onLogin model.getError().getMessage()  = " + model.getError().getMessage());

                    initMessageDialog(getString(R.string.txt_login_Error), 0, true, mHandler);
                    mMessageDialog.show();
                }


            } else {
                //check DB Data

                LOG.D(TAG, "DeviceUtils.mEmployeeModel = " + DeviceUtils.mEmployeeModel);
                LOG.D(TAG, "DeviceUtils.mVisitorModel = " + DeviceUtils.mVisitorModel);

                ArrayList<DeviceLoginBean> deviceLoginBeanArrayList = EnterpriseUtils.getAllDeviceLogin(mContext);
                LOG.D(TAG, "deviceLoginBeanArrayList = " + deviceLoginBeanArrayList);

                if (DeviceUtils.mEmployeeModel == null && DeviceUtils.mVisitorModel == null) {

                } else {
                    mHandler.sendEmptyMessageDelayed(Constants.CLOSE_MESSAGE_DIALOG, DeviceUtils.LIVING_LEARNING_TIME);
                    return;
                }

                if (deviceLoginBeanArrayList != null) {
                    LOG.D(TAG, "deviceLoginBeanArrayList.size() = " + deviceLoginBeanArrayList.size());

                    //Set Device Info
                    DeviceUtils.setDeviceInfo(mContext, deviceLoginBeanArrayList.get(0).getLocale(), null, null);

                    //Init FDR and WebSocket
                    FDRControlManager.getInstance(mContext, MainActivity.this).initFdr();

                    IdentifyEmployeeManager.getInstance(mContext).addModelFromDb();
                    IdentifyVisitorManager.getInstance(mContext).addModelFromDb();


                    for (int i = 0; i < deviceLoginBeanArrayList.size(); i++) {
                        ClockUtils.mModule = Integer.parseInt(deviceLoginBeanArrayList.get(i).getModule());
                        if (Integer.parseInt(deviceLoginBeanArrayList.get(i).getModule()) == Constants.MODULES_VISITORS) {
                            //Visitors
                            DeviceUtils.mVisitorModel = new VisitorModel();


                            DeviceUtils.mVisitorModel.setModules(Integer.parseInt(deviceLoginBeanArrayList.get(i).getModule()));

                            String arr = deviceLoginBeanArrayList.get(i).getModes();

                            LOG.D(TAG, "mVisitorModel arr =  " + arr);
                            String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");

                            LOG.D(TAG, "mVisitorModel items =  " + items);
                            LOG.D(TAG, "mVisitorModel items.length =  " + items.length);
                            int[] modes = new int[items.length];
                            LOG.D(TAG, "mVisitorModel modes.length =  " + modes.length);
                            for (int j = 0; j < items.length; j++) {
                                try {
                                    modes[j] = Integer.parseInt(items[j]);
                                } catch (NumberFormatException nfe) {
                                    //NOTE: write something here if you need to recover from formatting errors
                                }
                            }


                            DeviceUtils.mVisitorModel.setModes(modes);


                            EnterpriseUtils.addVisitorsAcceptancesDbToModel(mContext);

                        } else {
                            //Employees
                            DeviceUtils.mEmployeeModel = new EmployeeModel();
                            DeviceUtils.mEmployeeModel.setModules(Integer.parseInt(deviceLoginBeanArrayList.get(i).getModule()));


                            String arr = deviceLoginBeanArrayList.get(i).getModes();
                            String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
                            int[] modes = new int[items.length];
                            for (int j = 0; j < items.length; j++) {
                                try {
                                    modes[j] = Integer.parseInt(items[j]);
                                } catch (NumberFormatException nfe) {
                                    //NOTE: write something here if you need to recover from formatting errors
                                }
                            }

                            LOG.D(TAG, "mEmployeeModel items.length =  " + items.length);
                            LOG.D(TAG, "mEmployeeModel modes.length =  " + modes.length);

                            DeviceUtils.mEmployeeModel.setModes(modes);

                            EnterpriseUtils.addEmployeesAcceptancesDbToModel(mContext);
                        }


                    }

                    registerDayPassAlarmManager();

                    DeviceUtils.mDeviceShowName = mSharedPreference.getString(Constants.PREF_KEY_DEVICE_SHOW_NAME, "");
                    mHandler.sendEmptyMessageDelayed(Constants.SET_ACTIVITY_UI, DeviceUtils.LIVING_LEARNING_TIME);


                } else {
                    //Error show Message login Fail, no deviceName and DB data
                    initMessageDialog(getString(R.string.txt_login_Error), 0, true, mHandler);
                    mMessageDialog.show();
                }


            }
        }
    };

    private IDeviceAttendanceRecordsListener deviceAttendanceRecordsListener = new IDeviceAttendanceRecordsListener() {
        @Override
        public void onDeviceAttendanceRecords(RecordsReplyModel model) {
            LOG.D(TAG, "onDeviceAttendanceRecords  model = " + model);
            LOG.D(TAG, "onDeviceAttendanceRecords ClockUtils.mType = " + ClockUtils.mType);
            LOG.D(TAG, "onDeviceAttendanceRecords ClockUtils.mLoginAccount = " + ClockUtils.mLoginAccount);
            mProgress.setVisibility(View.GONE);

            //show result message

            mTxtIdentifyResult.setTextColor(getResources().getColor(R.color.success));
//            mTxtIdentifyResult.setText(ClockUtils.mLoginAccount);
            mTxtIdentifyResult.setText(ClockUtils.mLoginName);
            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);
            LOG.D(TAG, "mIsFromMotpmIsFromMotpmIsFromMotpmIsFromMotp = " + mIsFromMotp);
            if (mIsFromMotp) {
                //launch Motp
//                Uri webPage = Uri.parse("otpauth://totp/gorillaqad?secret=4DKT7AB2L4Y43N6CV4BEPZFIU7URCWSE&digits=6&period=30&issuer=multiOTP");
                Uri webPage = Uri.parse("otpauth://totp/gorillaqad?");
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webPage);

                Bundle bag = new Bundle();
                bag.putBoolean("IsBackFromAttendance", true);
                webIntent.putExtras(bag);

                startActivity(webIntent);

                mTxtIdentifyResult.setText("");


            } else {
                //GGGG Test
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("org.connectbot");

                        LOG.D(TAG, "launchIntent = " + launchIntent);
                        if (launchIntent != null) {
//                            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Bundle bag = new Bundle();
                            bag.putBoolean("GGGG", true);
                            launchIntent.putExtras(bag);
                            startActivity(launchIntent);//null pointer check in case package name was not found
                        }
                    }
                }, 2000);
            }


            if (model != null) {
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    //back to index fragment or show success dialog
                    LOG.D(TAG, "onDeviceAttendanceRecords model.getStatus() = " + model.getStatus());

                    if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
                        return;
                    }

                    //check is clockAuto on
                    if (DeviceUtils.mIsClockAuto) {
                        if (DeviceUtils.mIsRadioClockIn == true || DeviceUtils.mIsRadioClockOut == true) {

                        } else {
                            initImageDialog(MESSAGE_DIALOG_DELAYED_TIME, R.mipmap.icon_dialog_success, getString(R.string.txt_clock_complete), ClockUtils.mLoginTime, mHandler);
                            mImageDialog.show();
                        }
                    } else {
                        //show Dialog
                        initImageDialog(MESSAGE_DIALOG_DELAYED_TIME, R.mipmap.icon_dialog_success, getString(R.string.txt_clock_complete), ClockUtils.mLoginTime, mHandler);
                        mImageDialog.show();
                    }
                } else {
                    //Server fail , add to DB

                    if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
//                        EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED);
                        EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_ATTENDANCE);
                        return;
                    }
//                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED);
                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_ATTENDANCE);
                    //check is clockAuto on
                    if (DeviceUtils.mIsClockAuto) {
                        if (DeviceUtils.mIsRadioClockIn == true || DeviceUtils.mIsRadioClockOut == true) {

                        } else {
                            initImageDialog(MESSAGE_DIALOG_DELAYED_TIME, R.mipmap.icon_dialog_success, getString(R.string.txt_clock_complete), ClockUtils.mLoginTime, mHandler);
                            mImageDialog.show();
                        }
                    } else {
                        //show Dialog
                        initImageDialog(MESSAGE_DIALOG_DELAYED_TIME, R.mipmap.icon_dialog_success, getString(R.string.txt_clock_complete), ClockUtils.mLoginTime, mHandler);
                        mImageDialog.show();
                    }
                }
            } else {
                //Fail

                if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
//                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED);
                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_ATTENDANCE);
                    return;
                }
//                EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED);
                EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_ATTENDANCE);
                //check is clockAuto on
                if (DeviceUtils.mIsClockAuto) {
                    if (DeviceUtils.mIsRadioClockIn == true || DeviceUtils.mIsRadioClockOut == true) {

                    } else {
                        initImageDialog(MESSAGE_DIALOG_DELAYED_TIME, R.mipmap.icon_dialog_success, getString(R.string.txt_clock_complete), ClockUtils.mLoginTime, mHandler);
                        mImageDialog.show();
                    }
                } else {
                    //show Dialog
                    initImageDialog(MESSAGE_DIALOG_DELAYED_TIME, R.mipmap.icon_dialog_success, getString(R.string.txt_clock_complete), ClockUtils.mLoginTime, mHandler);
                    mImageDialog.show();
                }
            }
        }
    };

    private IDeviceAttendanceRecordsListener deviceAttendanceRecordsErrorListener = new IDeviceAttendanceRecordsListener() {
        @Override
        public void onDeviceAttendanceRecords(RecordsReplyModel model) {
            LOG.D(TAG, "deviceAttendanceRecordsErrorListener onDeviceAttendanceRecords  model = " + model);
            LOG.D(TAG, "deviceAttendanceRecordsErrorListener onDeviceAttendanceRecords ClockUtils.mType = " + ClockUtils.mType);
            LOG.D(TAG, "deviceAttendanceRecordsErrorListener onDeviceAttendanceRecords ClockUtils.mLoginAccount = " + ClockUtils.mLoginAccount);
            mProgress.setVisibility(View.GONE);

            //show result message
            mTxtIdentifyResult.setText(Html.fromHtml(getString(R.string.face_identify_fail)));
            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);

            if (model != null) {
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    //back to index fragment or show success dialog
                    LOG.D(TAG, "deviceAttendanceRecordsErrorListener onDeviceAttendanceRecords model.getStatus() = " + model.getStatus());


                } else {
                    //Server fail , add to DB

                    if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
//                        EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED);
                        EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_ATTENDANCE);
                        return;
                    }
//                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED);
                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_ATTENDANCE);
                    //check is clockAuto on
                    if (DeviceUtils.mIsClockAuto) {
                        if (DeviceUtils.mIsRadioClockIn == true || DeviceUtils.mIsRadioClockOut == true) {

                        } else {
                            initImageDialog(MESSAGE_DIALOG_DELAYED_TIME, R.mipmap.icon_dialog_success, getString(R.string.txt_clock_complete), ClockUtils.mLoginTime, mHandler);
                            mImageDialog.show();
                        }
                    } else {
                        //show Dialog
                        initImageDialog(MESSAGE_DIALOG_DELAYED_TIME, R.mipmap.icon_dialog_success, getString(R.string.txt_clock_complete), ClockUtils.mLoginTime, mHandler);
                        mImageDialog.show();
                    }
                }
            } else {
                //Fail

                if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
//                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED);
                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_ATTENDANCE);
                    return;
                }
//                EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED);
                EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_ATTENDANCE);
                //check is clockAuto on
                if (DeviceUtils.mIsClockAuto) {
                    if (DeviceUtils.mIsRadioClockIn == true || DeviceUtils.mIsRadioClockOut == true) {

                    } else {
                        initImageDialog(MESSAGE_DIALOG_DELAYED_TIME, R.mipmap.icon_dialog_success, getString(R.string.txt_clock_complete), ClockUtils.mLoginTime, mHandler);
                        mImageDialog.show();
                    }
                } else {
                    //show Dialog
                    initImageDialog(MESSAGE_DIALOG_DELAYED_TIME, R.mipmap.icon_dialog_success, getString(R.string.txt_clock_complete), ClockUtils.mLoginTime, mHandler);
                    mImageDialog.show();
                }
            }
        }
    };

    private IDeviceAccessRecordsListener deviceAccessRecordsListener = new IDeviceAccessRecordsListener() {
        @Override
        public void onDeviceAccessRecords(RecordsReplyModel model) {
            LOG.D(TAG, "onDeviceAccessRecords model =  " + model);
            mProgress.setVisibility(View.GONE);

            //show result message
            mTxtIdentifyResult.setTextColor(getResources().getColor(R.color.success));
//            mTxtIdentifyResult.setText(ClockUtils.mLoginAccount);
            mTxtIdentifyResult.setText(ClockUtils.mLoginName);
            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);
            if (mIsFromMotp) {
//                launch Motp
//                Uri webPage = Uri.parse("otpauth://totp/gorillaqad?secret=4DKT7AB2L4Y43N6CV4BEPZFIU7URCWSE&digits=6&period=30&issuer=multiOTP");
                Uri webPage = Uri.parse("otpauth://totp/gorillaqad?");
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webPage);

                Bundle bag = new Bundle();
                bag.putBoolean("IsBackFromAttendance", true);
                webIntent.putExtras(bag);

                startActivity(webIntent);

                mTxtIdentifyResult.setText("");


            }
            if (model != null) {
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    //back to index fragment or show success dialog
                    LOG.D(TAG, "onDeviceAccessRecords model.getStatus() = " + model.getStatus());
                    if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
                        return;
                    }

                } else {
                    //Error
                    if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
//                        EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED);
                        EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_ACCESS);
                        return;
                    }

//                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED);
                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_ACCESS);

                }
            } else {
                //Error
                if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
//                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED);
                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_ACCESS);
                    return;
                }

//                EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED);
                EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_ACCESS);


            }
        }
    };

    private IDeviceAccessRecordsListener deviceAccessRecordsErrorListener = new IDeviceAccessRecordsListener() {
        @Override
        public void onDeviceAccessRecords(RecordsReplyModel model) {
            LOG.D(TAG, "deviceAccessRecordsErrorListener onDeviceAccessRecords model =  " + model);
            mProgress.setVisibility(View.GONE);

            //show result message
            mTxtIdentifyResult.setText(Html.fromHtml(getString(R.string.face_identify_fail)));
            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);

            if (model != null) {
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    //back to index fragment or show success dialog
                    LOG.D(TAG, "deviceAccessRecordsErrorListener onDeviceAccessRecords model.getStatus() = " + model.getStatus());
                    if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
                        return;
                    }

                } else {
                    //Error
                    if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
//                        EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED);
                        EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_ACCESS);
                        return;
                    }

                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_ACCESS);
                }
            } else {
                //Error
                if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
//                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED);
                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_ACCESS);
                    return;
                }

//                EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED);
                EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_ACCESS);

            }
        }
    };

    private IDeviceVisitorAccessRecordsListener deviceVisitorAccessRecordsListener = new IDeviceVisitorAccessRecordsListener() {
        @Override
        public void onDeviceVisitorAccessRecords(RecordsReplyModel model) {
            LOG.D(TAG, "onDeviceVisitorAccessRecords model =  " + model);
            mProgress.setVisibility(View.GONE);

            //show result message
            mTxtIdentifyResult.setTextColor(getResources().getColor(R.color.success));
//            mTxtIdentifyResult.setText(ClockUtils.mLoginAccount);
            mTxtIdentifyResult.setText(ClockUtils.mLoginName);
            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);
            if (mIsFromMotp) {
//                launch Motp
//                Uri webPage = Uri.parse("otpauth://totp/gorillaqad?secret=4DKT7AB2L4Y43N6CV4BEPZFIU7URCWSE&digits=6&period=30&issuer=multiOTP");
                Uri webPage = Uri.parse("otpauth://totp/gorillaqad?");
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webPage);

                Bundle bag = new Bundle();
                bag.putBoolean("IsBackFromAttendance", true);
                webIntent.putExtras(bag);

                startActivity(webIntent);

                mTxtIdentifyResult.setText("");


            }
            //GG test
//            model = null;

            if (model != null) {
                LOG.D(TAG, "onDeviceVisitorAccessRecords model.getStatus() =  " + model.getStatus());

                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    //back to index fragment or show success dialog
                    LOG.D(TAG, "onDeviceAccessRecords model.getStatus() = " + model.getStatus());
                    if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
                        return;
                    }


                } else {
                    //Error
//                    if(ClockUtils.mType == Constants.CLOCK_UNKNOWN){
////                        EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED);
//                        EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_VISITORS);
//                        return;
//                    }
//
////                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED);
//                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_VISITORS);
                }
            } else {
                //Error
//                if(ClockUtils.mType == Constants.CLOCK_UNKNOWN){
////                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED);
//                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_VISITORS);
//                    return;
//                }
//
////                EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED);
//                EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_VISITORS);
            }
        }
    };

    private IDeviceVisitorAccessRecordsListener deviceVisitorAccessRecordsErrorListener = new IDeviceVisitorAccessRecordsListener() {
        @Override
        public void onDeviceVisitorAccessRecords(RecordsReplyModel model) {
            LOG.D(TAG, "deviceVisitorAccessRecordsErrorListener onDeviceVisitorAccessRecords model =  " + model);
            mProgress.setVisibility(View.GONE);

            //show result message
            mTxtIdentifyResult.setText(Html.fromHtml(getString(R.string.face_identify_fail)));
            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);

            //GG test
//            model = null;

            if (model != null) {
                LOG.D(TAG, "deviceVisitorAccessRecordsErrorListener onDeviceVisitorAccessRecords model.getStatus() =  " + model.getStatus());

                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    //back to index fragment or show success dialog
                    LOG.D(TAG, "onDeviceAccessRecords model.getStatus() = " + model.getStatus());
                    if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
                        return;
                    }


                } else {
                    //Error
//                    if(ClockUtils.mType == Constants.CLOCK_UNKNOWN){
////                        EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED);
//                        EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_VISITORS);
//                        return;
//                    }
//
////                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED);
//                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_VISITORS);
                }
            } else {
                //Error
//                if(ClockUtils.mType == Constants.CLOCK_UNKNOWN){
////                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED);
//                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_VISITORS);
//                    return;
//                }
//
////                EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED);
//                EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_VISITORS);
            }
        }
    };

    private IDeviceVisitorRecordsListener deviceVisitorRecordsListener = new IDeviceVisitorRecordsListener() {
        @Override
        public void onDeviceVisitorRecords(RecordsReplyModel model) {
            LOG.D(TAG, "onDeviceVisitorRecords model = " + model);
            mProgress.setVisibility(View.GONE);

            //show result message
            mTxtIdentifyResult.setTextColor(getResources().getColor(R.color.success));
//            mTxtIdentifyResult.setText(ClockUtils.mLoginAccount);
            mTxtIdentifyResult.setText(ClockUtils.mLoginName);
            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);
            if (mIsFromMotp) {
//                launch Motp
//                Uri webPage = Uri.parse("otpauth://totp/gorillaqad?secret=4DKT7AB2L4Y43N6CV4BEPZFIU7URCWSE&digits=6&period=30&issuer=multiOTP");
                Uri webPage = Uri.parse("otpauth://totp/gorillaqad?");
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webPage);

                Bundle bag = new Bundle();
                bag.putBoolean("IsBackFromAttendance", true);
                webIntent.putExtras(bag);

                startActivity(webIntent);

                mTxtIdentifyResult.setText("");


            }
            //GG test
//            model = null;
            if (model != null) {
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    //back to index fragment or show success dialog
                    LOG.D(TAG, "onDeviceVisitorRecords model.getStatus() = " + model.getStatus());

                    if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
                        return;
                    }


                } else {
                    //Error

                    if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
                        EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_VISITORS);
                        return;
                    }

                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_VISITORS);

                }
            } else {
                if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_VISITORS);
                    return;
                }
                EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_VISITORS);

            }
        }
    };


    private Button.OnClickListener mTxtBackToIndexClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            //invisible marquee
            mLayoutMarqeuu.setVisibility(View.INVISIBLE);
            mLayoutIdentifyResult.setVisibility(View.VISIBLE);

            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                mHandler.sendEmptyMessage(Constants.BACK_TO_INDEX_PAGE);

            } else {

                //stop FDR
                FDRControlManager.getInstance(mContext).stopFdr();

                mTxtIdentifyResult.setText("");
                mHandler.removeMessages(Constants.GET_FACE_TOO_LONG);

                if (getSupportFragmentManager().findFragmentByTag(FaceIdentificationFragment.TAG) != null) {
                    FaceIdentificationFragment faceIdentificationFragment = (FaceIdentificationFragment) getSupportFragmentManager().findFragmentByTag(FaceIdentificationFragment.TAG);
                    faceIdentificationFragment.removeFdrView();
                }

                launchFragment(Constants.FRAGMENT_TAG_CONF_LOGIN);
            }


        }
    };


    private void registerDayPassAlarmManager() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 2);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, BootBroadcastReceiver.class);
        intent.setAction(Constants.BROADCAST_RECEIVER_DAY_PASS);
        PendingIntent pi = PendingIntent.getBroadcast(mContext, (int) calendar.getTimeInMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);

        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);
    }

    private void saveImages(int num, List<byte[]> pngList, String name) {

        Time t = new Time();
        t.setToNow();
        int year = t.year;
        int month = t.month + 1;
        int date = t.monthDay;
        int hour = t.hour;
        int minute = t.minute;
        int second = t.second;

        for (int i = 0; i < num; i++) {

            File dir = new File(EnterpriseUtils.SD_CARD_APP_FACE_IMAGE);
            if (!dir.exists()) {
                dir.mkdirs();
            }
//            File dir = new File(root + String.format("FDRControlSample/%s-%d-%d-%d %d:%d:%d", name, year, month, date, hour, minute, second));
//            dir.mkdirs();

            try {
                File file = new File(dir + String.format("/%s-%d-%d-%d %d:%d:%d.png", name + i, year, month, date, hour, minute, second));
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(pngList.get(i));
                fos.flush();
                fos.close();

            } catch (Exception e) {

            }
        }

    }

    private void restartApp() {
        LOG.D(TAG, "restartApp");
        AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(mContext, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra("crash", true);
        PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);//1000 ms 後重新啟動

        android.os.Process.killProcess(android.os.Process.myPid());
        finish();
        System.exit(0);
        System.gc();


    }

    private void checkFragmentBackToHome() {

        LOG.D(TAG, "checkFragmentBackToHome getSupportFragmentManager().findFragmentByTag(EnglishPinCodeFragment.TAG) = " +
                getSupportFragmentManager().findFragmentByTag(EnglishPinCodeFragment.TAG));
        LOG.D(TAG, "checkFragmentBackToHome getSupportFragmentManager().findFragmentByTag(FaceIconFragment.TAG) = " +
                getSupportFragmentManager().findFragmentByTag(FaceIconFragment.TAG));
        LOG.D(TAG, "checkFragmentBackToHome getSupportFragmentManager().findFragmentByTag(FaceIdentificationFragment.TAG) = " +
                getSupportFragmentManager().findFragmentByTag(FaceIdentificationFragment.TAG));

        LOG.D(TAG, "checkFragmentBackToHome getSupportFragmentManager().findFragmentByTag(ChooseModuleFragment.TAG) = " +
                getSupportFragmentManager().findFragmentByTag(ChooseModuleFragment.TAG));

        LOG.D(TAG, "checkFragmentBackToHome getSupportFragmentManager().findFragmentByTag(ChooseModeFragment.TAG) = " +
                getSupportFragmentManager().findFragmentByTag(ChooseModeFragment.TAG));

        LOG.D(TAG, "checkFragmentBackToHome getSupportFragmentManager().findFragmentByTag(QrCodeFragment.TAG) = " +
                getSupportFragmentManager().findFragmentByTag(QrCodeFragment.TAG));


        if (getSupportFragmentManager().findFragmentByTag(EnglishPinCodeFragment.TAG) != null) {
            LOG.D(TAG, "englishPinCodeFragment.backToHome");
            EnglishPinCodeFragment englishPinCodeFragment = (EnglishPinCodeFragment) getSupportFragmentManager().findFragmentByTag(EnglishPinCodeFragment.TAG);
            englishPinCodeFragment.backToHome();

        } else if (getSupportFragmentManager().findFragmentByTag(FaceIconFragment.TAG) != null) {

            FaceIconFragment faceIconFragment = (FaceIconFragment) getSupportFragmentManager().findFragmentByTag(FaceIconFragment.TAG);
            faceIconFragment.backToHome();

        } else if (getSupportFragmentManager().findFragmentByTag(FaceIdentificationFragment.TAG) != null) {
            FaceIdentificationFragment faceIdentificationFragment = (FaceIdentificationFragment) getSupportFragmentManager().findFragmentByTag(FaceIdentificationFragment.TAG);
            faceIdentificationFragment.startFdr();
        } else if (getSupportFragmentManager().findFragmentByTag(QrCodeFragment.TAG) != null) {
            LOG.D(TAG, "QrCodeFragment.backToHome");
            if (getSupportFragmentManager().findFragmentByTag(ChooseModuleFragment.TAG) != null ||
                    getSupportFragmentManager().findFragmentByTag(ChooseModeFragment.TAG) != null) {
                return;
            }

            QrCodeFragment qrCodeFragment = (QrCodeFragment) getSupportFragmentManager().findFragmentByTag(QrCodeFragment.TAG);

            qrCodeFragment.setOnlyQRcode(true);
            qrCodeFragment.initBarcodeView();
        } else if (getSupportFragmentManager().findFragmentByTag(RFIDFragment.TAG) != null) {
            LOG.D(TAG, "RFIDFragment.backToHome");
            RFIDFragment rfidFragment = (RFIDFragment) getSupportFragmentManager().findFragmentByTag(RFIDFragment.TAG);
            rfidFragment.launchRfidScan();
        }
        retry = mSharedPreference.getInt(Constants.PREF_KEY_RETRY, 3);
    }

    private void startFDR() {
        if (getSupportFragmentManager().findFragmentByTag(EnglishPinCodeFragment.TAG) != null) {
            LOG.D(TAG, "englishPinCodeFragment.startFdr");
            EnglishPinCodeFragment englishPinCodeFragment = (EnglishPinCodeFragment) getSupportFragmentManager().findFragmentByTag(EnglishPinCodeFragment.TAG);
            englishPinCodeFragment.startFdr();

        } else if (getSupportFragmentManager().findFragmentByTag(FaceIconFragment.TAG) != null) {
            FaceIconFragment faceIconFragment = (FaceIconFragment) getSupportFragmentManager().findFragmentByTag(FaceIconFragment.TAG);
            faceIconFragment.startFdr();

        } else if (getSupportFragmentManager().findFragmentByTag(FaceIdentificationFragment.TAG) != null) {
            FaceIdentificationFragment faceIdentificationFragment = (FaceIdentificationFragment) getSupportFragmentManager().findFragmentByTag(FaceIdentificationFragment.TAG);
            faceIdentificationFragment.startFdr();

        } else if (getSupportFragmentManager().findFragmentByTag(QrCodeFragment.TAG) != null) {
            LOG.D(TAG, "QrCodeFragment.startFdr");
            QrCodeFragment qrCodeFragment = (QrCodeFragment) getSupportFragmentManager().findFragmentByTag(QrCodeFragment.TAG);
            qrCodeFragment.startFdr();

        } else if (getSupportFragmentManager().findFragmentByTag(RFIDFragment.TAG) != null) {
            LOG.D(TAG, "RFIDFragment.startFdr");
            RFIDFragment rfidFragment = (RFIDFragment) getSupportFragmentManager().findFragmentByTag(RFIDFragment.TAG);
            rfidFragment.startFdr();
        }
    }

    private void sendLiveDetectionFailApi(boolean isErrorLog, int modules, String type, int serialNumber) {
        LOG.D(TAG, "sendLiveDetectionFailApi modules = " + modules);

        if (isErrorLog) {
            ClockUtils.mRecordMode = Constants.RECORD_MODE_UNRECOGNIZED;
            switch (modules) {
                case Constants.MODULES_ATTENDANCE:
                    ApiUtils.attendanceUnrecognizedLog(TAG, mContext, DeviceUtils.mDeviceName, false, attendanceUnrecognizedLogLDFListener);
                    break;
                case Constants.MODULES_ACCESS:
                    ApiUtils.accessUnrecognizedLog(TAG, mContext, DeviceUtils.mDeviceName, false, accessUnrecognizedLogLDFListener);
                    setFaceIdentifyResultToClient(false, "", "");
                    break;
                case Constants.MODULES_VISITORS:
                    ApiUtils.visitorsUnrecognizedLog(TAG, mContext, DeviceUtils.mDeviceName, false, visitorsUnrecognizedLogLDFListener);
                    if (DeviceUtils.mIsVisitorOpenDoor == true) {
                        ApiUtils.accessVisitorUnrecognizedLog(TAG, mContext, DeviceUtils.mDeviceName, false, accessVisitorUnrecognizedLogLDFListener);
                        setFaceIdentifyResultToClient(false, "", "");
                    }

                    break;
                case Constants.MODULES_ATTENDANCE_ACCESS:

                    ApiUtils.attendanceUnrecognizedLog(TAG, mContext, DeviceUtils.mDeviceName, false, attendanceUnrecognizedLogLDFListener);
                    ApiUtils.accessUnrecognizedLog(TAG, mContext, DeviceUtils.mDeviceName, false, accessUnrecognizedLogLDFListener);
                    setFaceIdentifyResultToClient(false, "", "");

                    break;
                default:
                    break;
            }
        } else {
            ClockUtils.mRecordMode = Constants.RECORD_MODE_RECORD;
            switch (modules) {
                case Constants.MODULES_ATTENDANCE:
                    ApiUtils.deviceAttendanceRecords(TAG, mContext, DeviceUtils.mDeviceName, false, type, serialNumber, deviceAttendanceRecordsLDFListener);
                    break;
                case Constants.MODULES_ACCESS:
                    ApiUtils.deviceAccessRecords(TAG, mContext, DeviceUtils.mDeviceName, false, type, serialNumber, deviceAccessRecordsLDFListener);
                    setFaceIdentifyResultToClient(false, "", "");
                    break;
                case Constants.MODULES_VISITORS:
                    ApiUtils.deviceVisitorRecords(TAG, mContext, DeviceUtils.mDeviceName, false, deviceVisitorRecordsLDFListener);
                    if (DeviceUtils.mIsVisitorOpenDoor == true) {
                        ApiUtils.deviceVisitorAccessRecords(TAG, mContext, DeviceUtils.mDeviceName, false, type, serialNumber, deviceVisitorAccessRecordsLDFListener);
                        setFaceIdentifyResultToClient(true, ClockUtils.mLoginName, ClockUtils.mLoginAccount);
//                        EnterpriseUtils.openDoorOne(mContext);
                    }
                    break;
                case Constants.MODULES_ATTENDANCE_ACCESS:
                    ApiUtils.deviceAttendanceRecords(TAG, mContext, DeviceUtils.mDeviceName, false, type, serialNumber, deviceAttendanceRecordsLDFListener);
                    ApiUtils.deviceAccessRecords(TAG, mContext, DeviceUtils.mDeviceName, false, type, serialNumber, deviceAccessRecordsLDFListener);
                    setFaceIdentifyResultToClient(false, "", "");

                    break;
                default:
                    break;
            }

        }
    }

    private void sendClockInfoToServer(boolean isErrorLog, int modules, String type, int serialNumber) {

        LOG.D(TAG, "sendClockInfoToServer modules = " + modules);

        if (isErrorLog) {
            ClockUtils.mRecordMode = Constants.RECORD_MODE_UNRECOGNIZED;
            switch (modules) {
                case Constants.MODULES_ATTENDANCE:
                    ApiUtils.attendanceUnrecognizedLog(TAG, mContext, DeviceUtils.mDeviceName, false, CallbackUtils.attendanceUnrecognizedLogListener);
                    break;
                case Constants.MODULES_ACCESS:
                    ApiUtils.accessUnrecognizedLog(TAG, mContext, DeviceUtils.mDeviceName, false, CallbackUtils.accessUnrecognizedLogListener);
                    setFaceIdentifyResultToClient(false, "", "");
                    break;
                case Constants.MODULES_VISITORS:
                    ApiUtils.visitorsUnrecognizedLog(TAG, mContext, DeviceUtils.mDeviceName, false, CallbackUtils.visitorsUnrecognizedLogListener);
                    if (DeviceUtils.mIsVisitorOpenDoor == true) {
                        ApiUtils.accessVisitorUnrecognizedLog(TAG, mContext, DeviceUtils.mDeviceName, false, CallbackUtils.accessVisitorUnrecognizedLogListener);
                        setFaceIdentifyResultToClient(false, "", "");
                    }

                    break;
                case Constants.MODULES_ATTENDANCE_ACCESS:

                    ApiUtils.attendanceUnrecognizedLog(TAG, mContext, DeviceUtils.mDeviceName, false, CallbackUtils.attendanceUnrecognizedLogListener);
                    ApiUtils.accessUnrecognizedLog(TAG, mContext, DeviceUtils.mDeviceName, false, CallbackUtils.accessUnrecognizedLogListener);
                    setFaceIdentifyResultToClient(false, "", "");

                    break;
                default:
                    break;
            }
        } else {
            ClockUtils.mRecordMode = Constants.RECORD_MODE_RECORD;
            switch (modules) {
                case Constants.MODULES_ATTENDANCE:
                    ApiUtils.deviceAttendanceRecords(TAG, mContext, DeviceUtils.mDeviceName, false, type, serialNumber, deviceAttendanceRecordsListener);
                    break;
                case Constants.MODULES_ACCESS:
                    ApiUtils.deviceAccessRecords(TAG, mContext, DeviceUtils.mDeviceName, false, type, serialNumber, deviceAccessRecordsListener);
                    setFaceIdentifyResultToClient(false, "", "");
                    break;
                case Constants.MODULES_VISITORS:
                    ApiUtils.deviceVisitorRecords(TAG, mContext, DeviceUtils.mDeviceName, false, deviceVisitorRecordsListener);
                    if (DeviceUtils.mIsVisitorOpenDoor == true) {
                        ApiUtils.deviceVisitorAccessRecords(TAG, mContext, DeviceUtils.mDeviceName, false, type, serialNumber, deviceVisitorAccessRecordsListener);
                        setFaceIdentifyResultToClient(true, ClockUtils.mLoginName, ClockUtils.mLoginAccount);
//                        EnterpriseUtils.openDoorOne(mContext);
                    }
                    break;
                case Constants.MODULES_ATTENDANCE_ACCESS:
                    ApiUtils.deviceAttendanceRecords(TAG, mContext, DeviceUtils.mDeviceName, false, type, serialNumber, deviceAttendanceRecordsListener);
                    ApiUtils.deviceAccessRecords(TAG, mContext, DeviceUtils.mDeviceName, false, type, serialNumber, deviceAccessRecordsListener);
                    setFaceIdentifyResultToClient(false, "", "");

                    break;
                default:
                    break;
            }

        }

    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = null;
            if (DeviceUtils.mLocale != null) {
                if (DeviceUtils.mLocale.equals(Constants.LOCALE_EN)) {
                    df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                } else if (DeviceUtils.mLocale.equals(Constants.LOCALE_TW)) {
                    df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                } else if (DeviceUtils.mLocale.equals(Constants.LOCALE_CN)) {
                    df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                } else {
                    df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                }
            } else {
                df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }
//
            df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = df.format(c.getTime());
            mTxtActionCurrentTime.setText(formattedDate);
//
//                    mTimerHandler.removeMessages(Constants.SET_TIMER);
            mCurrentTimeHandler.postDelayed(this, DeviceUtils.TIMER_DELAYED_TIME);
        }
    };


    private Handler mTimerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LOG.D(TAG, "mTimerHandler msg.what = " + msg.what);
            switch (msg.what) {
                case Constants.SET_TIMER:

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = null;
//
                    df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = df.format(c.getTime());
                    mTxtActionCurrentTime.setText(formattedDate);
//
                    mTimerHandler.sendEmptyMessageDelayed(Constants.SET_TIMER, DeviceUtils.TIMER_DELAYED_TIME);
                    break;
            }
        }
    };

    ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            LOG.D(TAG, "onServiceDisconnected ");
            mBounded = false;
            mMessenger = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            LOG.D(TAG, "onServiceConnected ");
            mMessenger = new Messenger(service);
            mBounded = true;
        }
    };

    private void setFaceIdentifyResultToClient(boolean isPass, String name, String id) {
        LOG.D(TAG, "setFaceIdentifyResultToClient isPass = " + isPass + " name = " + name + " id = " + id);
        Message message = Message.obtain(null, RemoteService.MSG_SET_IDENTIFY_RESULTS, null);
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(RemoteService.KEY_FACE_IDENTIFY_IS_PASS, isPass);
            bundle.putString(RemoteService.KEY_FACE_IDENTIFY_NAME, name);
            bundle.putString(RemoteService.KEY_FACE_IDENTIFY_ID, id);
            message.setData(bundle);

            mMessenger.send(message);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private BroadcastReceiver mStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            LOG.D(TAG, "mStatusListener onReceive action = " + action);

            if (Constants.BROADCAST_RECEIVER_DAY_PASS.equals(action)) {
                LOG.D(TAG, "BROADCAST_RECEIVER_DAY_PASS");
                EnterpriseUtils.deleteFacePhoto();
                EnterpriseUtils.deleteLogFile();
                EnterpriseUtils.startRecordLog();
//                EnterpriseUtils.uploadLog();


            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LOG.D(TAG, "mHandler msg.what = " + msg.what);

            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            // The first in the list of RunningTasks is always the foreground task.
            ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
            String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();


            if (foregroundTaskPackageName != null && foregroundTaskPackageName.equals(mContext.getPackageName())) {

            } else {
                if (msg.what == Constants.MSG_CHECK_USER_CLOCK_DB) {

                } else {
                    return;
                }
            }

            switch (msg.what) {
                case Constants.GET_FACE_SUCCESS:


                    FDRControlManager.getInstance(mContext).getFdrCtrl().stopFaceDetection();
//
                    saveImages(EnterpriseUtils.mFacePngList.size(), EnterpriseUtils.mFacePngList, "face-success");
                    LOG.D(TAG, "GET_FACE_SUCCESS ClockUtils.mRoleModel = " + mRoleModel);
                    LOG.D(TAG, "GET_FACE_SUCCESS ClockUtils.mMode = " + ClockUtils.mMode);
                    LOG.D(TAG, "GET_FACE_SUCCESS ClockUtils.mLoginUuid = " + ClockUtils.mLoginUuid);


                    if (DeviceUtils.mIsOnlineMode) {
                        mProgress.setVisibility(View.VISIBLE);
                        if (ClockUtils.mMode == Constants.MODES_FACE_IDENTIFICATION) {

                            LOG.D(TAG, "GET_FACE_SUCCESS  Constants.MODES_FACE_IDENTIFICATION");
                            if (mRoleModel instanceof EmployeeModel) {
                                bapIdentifyTask = ApiUtils.bapIdentify(TAG, mContext, "EMPLOYEE", "png", EnterpriseUtils.mFacePngList.get(0), bapIdentifyListener);
                            } else if (mRoleModel instanceof VisitorModel) {
                                //never here
                                bapIdentifyTask = ApiUtils.bapIdentify(TAG, mContext, "VISITOR", "png", EnterpriseUtils.mFacePngList.get(0), bapIdentifyListener);
                            }
                        } else {
                            LOG.D(TAG, "CGET_FACE_SUCCESS Constants.MODES_FACE_Verify");
                            if (mRoleModel instanceof EmployeeModel) {
                                bapVerifyTask = ApiUtils.bapVerify(TAG, mContext, ClockUtils.mLoginUuid, "EMPLOYEE", "png", EnterpriseUtils.mFacePngList.get(0), bapVerifyListener);
                            } else if (mRoleModel instanceof VisitorModel) {
                                bapVerifyTask = ApiUtils.bapVerify(TAG, mContext, ClockUtils.mLoginUuid, "VISITOR", "png", EnterpriseUtils.mFacePngList.get(0), bapVerifyListener);
                            }
                        }

                    } else {
                        //do faceVerify or faceIdentify
                        if (getSupportFragmentManager().findFragmentByTag(FaceIdentificationFragment.TAG) != null) {
                            //do face identification online or offline
                            if (DeviceUtils.mIsOffLineMode) {
                                LOG.D(TAG, "EnterpriseUtils.mPngList.size()11 = " + EnterpriseUtils.mFacePngList.size() + " mRoleModel = " + mRoleModel);
                                if (EnterpriseUtils.mFacePngList.size() > 0) {
                                    mProgress.setVisibility(View.VISIBLE);

                                    //do faceVerify offline check employee or visitor
                                    if (mRoleModel instanceof EmployeeModel) {
                                        IdentifyEmployeeManager.getInstance(mContext).search(mHandler);
                                    } else if (mRoleModel instanceof VisitorModel) {
                                        IdentifyVisitorManager.getInstance(mContext).search(mHandler);
                                    } else {
                                        mHandler.sendEmptyMessage(Constants.OFFLINE_FACE_VERIFY_FAIL);
                                    }

                                } else {
                                    //error, no face in FDR
                                    mHandler.sendEmptyMessage(Constants.OFFLINE_FACE_VERIFY_FAIL);
                                }
                            } else {
                                //do face identification online
                            }


                        } else {
                            //do face verify
                            if (DeviceUtils.mIsOffLineMode) {
                                LOG.D(TAG, "EnterpriseUtils.mPngList.size()22 = " + EnterpriseUtils.mFacePngList.size() + " mRoleModel = " + mRoleModel);
                                if (EnterpriseUtils.mFacePngList.size() > 0) {
                                    mProgress.setVisibility(View.VISIBLE);

                                    //check liveness

                                    //do faceVerify offline check employee or visitor
//                                mRoleModel = new RoleModel();
                                    if (mRoleModel instanceof EmployeeModel) {
                                        IdentifyEmployeeManager.getInstance(mContext).search(mHandler);
                                    } else if (mRoleModel instanceof VisitorModel) {
                                        IdentifyVisitorManager.getInstance(mContext).search(mHandler);
                                    } else {
                                        mHandler.sendEmptyMessage(Constants.OFFLINE_FACE_VERIFY_FAIL);
                                    }

                                } else {
                                    //error, no face in FDR
                                    mHandler.sendEmptyMessage(Constants.OFFLINE_FACE_VERIFY_FAIL);
                                }
                            } else {

                            }
                        }
                    }

                    break;
                case Constants.GET_FACE_FAIL:
                    FDRControlManager.getInstance(mContext).getFdrCtrl().stopFaceDetection();
//                    FDRControlManager.getInstance(mContext).getFdrCtrl().stopCamera();
                    //liveness fail
                    LOG.D(TAG, "GET_FACE_FAIL");
                    saveImages(EnterpriseUtils.mFacePngList.size(), EnterpriseUtils.mFacePngList, "face-fail");
                    ClockUtils.mFaceVerify = Constants.FACE_VERIFY_FAILED;

                    if (ClockUtils.mMode == Constants.MODES_SECURITY_CODE || ClockUtils.mMode == Constants.MODES_FACE_ICON) {
                        ClockUtils.mType = Constants.CLOCK_UNKNOWN;
                        ++ClockUtils.mSerialNumber;
                        mSharedPreference.edit().putInt(Constants.PREF_KEY_SERIAL_NUMBER, ClockUtils.mSerialNumber).commit();
//                        sendClockInfoToServer(false, ClockUtils.mRoleModel.getModules(), ClockUtils.mType, ClockUtils.mSerialNumber);

                        sendLiveDetectionFailApi(false, ClockUtils.mRoleModel.getModules(), ClockUtils.mType, ClockUtils.mSerialNumber);

                    } else if (ClockUtils.mMode == Constants.MODES_FACE_IDENTIFICATION) {
                        //
//                        sendClockInfoToServer(true, ClockUtils.mRoleModel.getModules(), ClockUtils.mType, ClockUtils.mSerialNumber);

                        sendLiveDetectionFailApi(true, ClockUtils.mRoleModel.getModules(), ClockUtils.mType, ClockUtils.mSerialNumber);
                    }

//                    mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
//                    mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, PAGE_DELAYED_TIME);
//                    mProgress.setVisibility(View.GONE);

//                    initMessageDialog(getString(R.string.txt_not_liveness), MESSAGE_DIALOG_DELAYED_TIME, false, mHandler);
//                    mMessageDialog.show();

                    //show live detection fail message


                    break;
                case Constants.GET_FACE_TOO_LONG:

                    //back to index page
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);


                    if (getSupportFragmentManager().findFragmentByTag(ChooseModuleFragment.TAG) != null ||
                            getSupportFragmentManager().findFragmentByTag(ChooseModeFragment.TAG) != null) {
                        if (FDRControlManager.getInstance(mContext).getFdrCtrl() != null) {
                            FDRControlManager.getInstance(mContext).getFdrCtrl().stopFaceDetection();
                        }

//                        FDRControlManager.getInstance(mContext).getFdrCtrl().stopCamera();
                    }

                    mHandler.removeMessages(Constants.LAUNCH_VIDEO);
                    mHandler.sendEmptyMessageDelayed(Constants.LAUNCH_VIDEO, DeviceUtils.VIDEO_DELAYED_TIME);
                    break;

                case Constants.OFFLINE_FACE_VERIFY_SUCCESS:
                    LOG.D(TAG, "OFFLINE_FACE_VERIFY_SUCCESS getSupportFragmentManager().getBackStackEntryCount() = " + getSupportFragmentManager().getBackStackEntryCount());

                    //show result and save data to db
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

                    } else {
                        mHandler.removeMessages(Constants.LAUNCH_VIDEO);
                        mHandler.sendEmptyMessageDelayed(Constants.LAUNCH_VIDEO, DeviceUtils.VIDEO_DELAYED_TIME);
                    }

                    mProgress.setVisibility(View.GONE);

                    mTxtIdentifyResult.setTextColor(getResources().getColor(R.color.success));
//                    mTxtIdentifyResult.setText(ClockUtils.mLoginAccount);
                    mTxtIdentifyResult.setText(ClockUtils.mLoginName);

                    mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                    mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);
                    if (mIsFromMotp) {
//                launch Motp
//                Uri webPage = Uri.parse("otpauth://totp/gorillaqad?secret=4DKT7AB2L4Y43N6CV4BEPZFIU7URCWSE&digits=6&period=30&issuer=multiOTP");
                        Uri webPage = Uri.parse("otpauth://totp/gorillaqad?");
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, webPage);

                        Bundle bag = new Bundle();
                        bag.putBoolean("IsBackFromAttendance", true);
                        webIntent.putExtras(bag);

                        startActivity(webIntent);

                        mTxtIdentifyResult.setText("");


                    }
                    LOG.D(TAG, "ClockUtils.mModule = " + ClockUtils.mModule);

                    ClockUtils.mFaceVerify = Constants.FACE_VERIFY_SUCCEED;
                    if (mRoleModel instanceof EmployeeModel) {
                        LOG.D(TAG, "OFFLINE_FACE_VERIFY_SUCCESS EmployeeModel");

                        //Set data
                        if (DeviceUtils.mIsRadioClockIn) {
                            ClockUtils.mType = Constants.CLOCK_IN;
                        } else {
                            ClockUtils.mType = Constants.CLOCK_OUT;
                        }

                        ClockUtils.mRecordMode = Constants.RECORD_MODE_RECORD;

                        if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE_ACCESS) {
                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
                            EnterpriseUtils.openDoorOne(mContext);
                        } else if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE) {
                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
                        } else {
                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
                            EnterpriseUtils.openDoorOne(mContext);
                        }


                    } else if (mRoleModel instanceof VisitorModel) {
                        LOG.D(TAG, "OFFLINE_FACE_VERIFY_SUCCESS VisitorModel");
//                        doVisitorClockApi(DeviceUtils.mIsRadioClockIn, DeviceUtils.mIsRadioClockOut, DeviceUtils.mIsVisitorOpenDoor);
                        //Set data
                        if (DeviceUtils.mIsRadioClockIn) {
                            ClockUtils.mType = Constants.VISITOR_VISIT;
                        } else {
                            ClockUtils.mType = Constants.VISITOR_LEAVE;
                        }

                        ClockUtils.mRecordMode = Constants.RECORD_MODE_RECORD;
                        EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_VISITORS);
                        EnterpriseUtils.openDoorOne(mContext);
                    }
                    break;

                case Constants.OFFLINE_FACE_VERIFY_FAIL:
                    LOG.D(TAG, "OFFLINE_FACE_VERIFY_FAIL ClockUtils.mLoginAccount = " + ClockUtils.mLoginAccount);
                    retryFDR();
//                    if(getSupportFragmentManager().getBackStackEntryCount() > 0){
//
//                    }else{
//                        mHandler.removeMessages(Constants.LAUNCH_VIDEO);
//                        mHandler.sendEmptyMessageDelayed(Constants.LAUNCH_VIDEO, DeviceUtils.VIDEO_DELAYED_TIME);
//                    }
//
//                    ClockUtils.mFaceVerify = Constants.FACE_VERIFY_FAILED;
//
//                    //1.check fragment
//                    LOG.D(TAG,"ClockUtils.mMode = " + ClockUtils.mMode);
//                    if(ClockUtils.mMode == Constants.MODES_SECURITY_CODE || ClockUtils.mMode == Constants.MODES_FACE_ICON
//                            || ClockUtils.mMode == Constants.MODES_QR_CODE || ClockUtils.mMode == Constants.MODES_RFID){
//                        ClockUtils.mType = Constants.CLOCK_UNKNOWN;
//                        ++ClockUtils.mSerialNumber;
//                        mSharedPreference.edit().putInt(Constants.PREF_KEY_SERIAL_NUMBER, ClockUtils.mSerialNumber).commit();
//                        sendErrorLogToServer(false, ClockUtils.mRoleModel.getModules(), ClockUtils.mType, ClockUtils.mSerialNumber);
//                    }else{
//                        sendErrorLogToServer(true, ClockUtils.mRoleModel.getModules(), ClockUtils.mType, ClockUtils.mSerialNumber);
//                    }

//
//                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//
//                    } else {
//                        mHandler.removeMessages(Constants.LAUNCH_VIDEO);
//                        mHandler.sendEmptyMessageDelayed(Constants.LAUNCH_VIDEO, DeviceUtils.VIDEO_DELAYED_TIME);
//                    }
//
//                    mProgress.setVisibility(View.GONE);
//                    mTxtIdentifyResult.setText(Html.fromHtml(getString(R.string.face_identify_fail)));
//                    mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
//                    mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);
//                    ClockUtils.mFaceVerify = Constants.FACE_VERIFY_FAILED;
//
//                    //1.check fragment
//                    LOG.D(TAG, "ClockUtils.mMode = " + ClockUtils.mMode);
//                    if (ClockUtils.mMode == Constants.MODES_SECURITY_CODE || ClockUtils.mMode == Constants.MODES_FACE_ICON
//                            || ClockUtils.mMode == Constants.MODES_QR_CODE || ClockUtils.mMode == Constants.MODES_RFID) {
//                        ClockUtils.mType = Constants.CLOCK_UNKNOWN;
//                        ++ClockUtils.mSerialNumber;
//                        mSharedPreference.edit().putInt(Constants.PREF_KEY_SERIAL_NUMBER, ClockUtils.mSerialNumber).commit();
////                        sendErrorLogToServer(false, ClockUtils.mRoleModel.getModules(), ClockUtils.mType, ClockUtils.mSerialNumber);
//
//                        //not unrecognized
//                        ClockUtils.mRecordMode = Constants.RECORD_MODE_RECORD;
//                        if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE_ACCESS) {
//                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
//                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
//                        } else if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE) {
//                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
//                        } else if (ClockUtils.mModule == Constants.MODULES_ACCESS) {
//                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
//                        } else if (ClockUtils.mModule == Constants.MODULES_VISITORS) {
//                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_VISITORS);
//                        }
//
//
//                    } else {
////                        sendErrorLogToServer(true, ClockUtils.mRoleModel.getModules(), ClockUtils.mType, ClockUtils.mSerialNumber);
//
//                        //visitor never here
//                        ClockUtils.mRecordMode = Constants.RECORD_MODE_UNRECOGNIZED;
//                        if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE_ACCESS) {
//                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
//                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
//                        } else if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE) {
//                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
//                        } else if (ClockUtils.mModule == Constants.MODULES_ACCESS) {
//                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
//                        } else if (ClockUtils.mModule == Constants.MODULES_VISITORS) {
//                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_VISITORS);
//                        }
//
//                    }
                    break;

                case Constants.CLOSE_CLOCK_DIALOG:
                    LOG.D(TAG, "CLOSE_CLOCK_DIALOG mClockDialog.isShowing() = " + mClockDialog.isShowing());
                    if (mClockDialog.isShowing()) {
                        mClockDialog.dismiss();
                        //back to index page
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        ClockUtils.clearClockData();
                    } else {
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        ClockUtils.clearClockData();
                    }

                    checkFragmentBackToHome();
                    break;

                case Constants.SEND_ATTENDANCE_RECOGNIZED_LOG:
                    LOG.D(TAG, "SEND_ATTENDANCE_RECOGNIZED_LOG");

                    mHandler.removeMessages(Constants.LAUNCH_VIDEO);
                    mHandler.sendEmptyMessageDelayed(Constants.LAUNCH_VIDEO, DeviceUtils.VIDEO_DELAYED_TIME);

                    //Pin code Error
                    sendClockInfoToServer(true, ClockUtils.mRoleModel.getModules(), ClockUtils.mType, ClockUtils.mSerialNumber);

//                    ApiUtils.attendanceUnrecognizedLog(TAG, mContext, DeviceUtils.mDeviceName, false, attendanceUnrecognizedLogListener);


//                    if(mRoleModel instanceof EmployeeModel){
//                        ApiUtils.attendanceUnrecognizedLog(TAG, mContext, "0EABFCE5-2AD7-4A66-B829-95F4F84A8663", false, CallbackUtils.attendanceUnrecognizedLogListener);
//                    } else if(mRoleModel instanceof VisitorModel){
//                        ApiUtils.visitorsUnrecognizedLog(TAG, mContext, "0EABFCE5-2AD7-4A66-B829-95F4F84A8663", false, CallbackUtils.visitorsUnrecognizedLogListener);
//                    }


                    break;

                case Constants.LAUNCH_VIDEO:
                    LOG.D(TAG, "LAUNCH_VIDEO");


                    //check if launch Video
//                    File dirContent = new File(EnterpriseUtils.SD_CARD_APP_CONTENT);
//                    LOG.D(TAG, "LAUNCH_VIDEO dirContent.exists() = " + dirContent.exists());
//                    LOG.D(TAG, "LAUNCH_VIDEO DeviceUtils.mVideoList.size() = " + DeviceUtils.mVideoList.size());
//                    if (dirContent.exists()) {
//                        LOG.D(TAG, "LAUNCH_VIDEO dirContent.listFiles() = " + dirContent.listFiles());
//                        if (dirContent.listFiles().length > 0) {
//
//                        } else {
//                            mHandler.removeMessages(Constants.LAUNCH_VIDEO);
//                            mHandler.sendEmptyMessageDelayed(Constants.LAUNCH_VIDEO, DeviceUtils.VIDEO_DELAYED_TIME);
//                            return;
//                        }
//                    } else {
//
//                        return;
//                    }

                    if (false) {
                        if (DeviceUtils.mVideoList.size() == 0) {
                            mHandler.removeMessages(Constants.LAUNCH_VIDEO);
                            mHandler.sendEmptyMessageDelayed(Constants.LAUNCH_VIDEO, DeviceUtils.VIDEO_DELAYED_TIME);
                            return;
                        }


                        //check if downloading video
                        if (DeviceUtils.mCheckIsDownloadingVideo != null) {
                            for (int i = 0; i < DeviceUtils.mCheckIsDownloadingVideo.length; i++) {
                                LOG.D(TAG, "LAUNCH_VIDEO DeviceUtils.mCheckIsDownloadingVideo[i] = " + DeviceUtils.mCheckIsDownloadingVideo[i]);
                                if (DeviceUtils.mCheckIsDownloadingVideo[i] == true) {
                                    mHandler.removeMessages(Constants.LAUNCH_VIDEO);
                                    mHandler.sendEmptyMessageDelayed(Constants.LAUNCH_VIDEO, DeviceUtils.VIDEO_DELAYED_TIME);
                                    return;
                                }
                            }
                        }

                        //show marquee
                        mLayoutMarqeuu.setVisibility(View.VISIBLE);
                        mLayoutIdentifyResult.setVisibility(View.INVISIBLE);


//                    FDRControlManager.getInstance(mContext).releaseFdr();
                        mTxtActionBarTitle.setVisibility(View.INVISIBLE);
                        mTxtActionBarSubTitle.setVisibility(View.INVISIBLE);
                        mLayoutFooterBarLeft.setVisibility(View.INVISIBLE);

//                    FDRControlManager.getInstance(mContext).stopFdr(mActivityHandler, mFragmentHandler);

                        //stop FDR
                        FDRControlManager.getInstance(mContext).stopFdr();

                        launchFragment(Constants.FRAGMENT_TAG_VIDEO);
//
                    }
                    break;

                case Constants.SET_ACTIVITY_UI:
                    //Decide load which Fragment by device utils
                    LOG.D(TAG, "SET_ACTIVITY_UI DeviceUtils.mEmployeeModel = " + DeviceUtils.mEmployeeModel);
                    LOG.D(TAG, "SET_ACTIVITY_UI DeviceUtils.mVisitorModel = " + DeviceUtils.mVisitorModel);


//                    mTxtBackToIndex.setText(getString(R.string.txt_home_page));

                    mTxtBackToIndex.setText(getString(R.string.txt_home_setting));
                    mTxtBackToIndex.setBackgroundResource(R.mipmap.icon_back_to_home_setting);

                    mTxtDeviceName.setText(DeviceUtils.mDeviceShowName);


                    if (DeviceUtils.mEmployeeModel != null && DeviceUtils.mVisitorModel != null) {
                        //load chooseModule
                        launchFragment(Constants.FRAGMENT_TAG_CHOOSE_MODULE);
                        //check is blueDoor


                    } else {
                        //check modes
                        if (DeviceUtils.mEmployeeModel != null) {
                            LOG.D(TAG, "SET_ACTIVITY_UI    DeviceUtils.mEmployeeModel.getModes().length = " + DeviceUtils.mEmployeeModel.getModes().length);
                            mRoleModel = DeviceUtils.mEmployeeModel;
                            if (DeviceUtils.mEmployeeModel.getModes().length > 1) {
                                launchFragment(Constants.FRAGMENT_TAG_CHOOSE_MODE);
                            } else {
                                //check which mode fragment to load
                                switch (DeviceUtils.mEmployeeModel.getModes()[0]) {
                                    case Constants.MODES_SECURITY_CODE:
                                        ClockUtils.mMode = Constants.MODES_SECURITY_CODE;
//                                        launchFragment(Constants.FRAGMENT_TAG_PIN_CODE);
                                        launchFragment(Constants.FRAGMENT_TAG_ENGLISH_PIN_CODE);
                                        break;
                                    case Constants.MODES_FACE_ICON:
//                                        ClockUtils.mMode = Constants.MODES_FACE_ICON;
//                                        launchFragment(Constants.FRAGMENT_TAG_FACE_ICON);
                                        break;
                                    case Constants.MODES_FACE_IDENTIFICATION:
                                        ClockUtils.mMode = Constants.MODES_FACE_IDENTIFICATION;
                                        launchFragment(Constants.FRAGMENT_TAG_FACE_IDENTIFICATION);
                                        break;
                                    case Constants.MODES_QR_CODE:
                                        ClockUtils.mMode = Constants.MODES_QR_CODE;
                                        launchFragment(Constants.FRAGMENT_TAG_QR_CODE);
                                        break;
                                    case Constants.MODES_RFID:
                                        ClockUtils.mMode = Constants.MODES_RFID;
                                        launchFragment(Constants.FRAGMENT_TAG_RFID);
                                        break;

                                }

                            }

                        } else if (DeviceUtils.mVisitorModel != null) {
                            LOG.D(TAG, "SET_ACTIVITY_UI DeviceUtils.mVisitorModel.getModes().length = " + DeviceUtils.mVisitorModel.getModes().length);

                            ClockUtils.mRoleModel = DeviceUtils.mVisitorModel;

                            if (DeviceUtils.mVisitorModel.getModes().length > 1) {
                                launchFragment(Constants.FRAGMENT_TAG_CHOOSE_MODE);
                            } else {
                                //check which mode fragment to load
                                switch (DeviceUtils.mVisitorModel.getModes()[0]) {
                                    case Constants.MODES_SECURITY_CODE:
                                        ClockUtils.mMode = Constants.MODES_SECURITY_CODE;
//                                        launchFragment(Constants.FRAGMENT_TAG_PIN_CODE);
                                        launchFragment(Constants.FRAGMENT_TAG_ENGLISH_PIN_CODE);
                                        break;
                                    case Constants.MODES_FACE_ICON:
//                                        ClockUtils.mMode = Constants.MODES_FACE_ICON;
//                                        launchFragment(Constants.FRAGMENT_TAG_FACE_ICON);
                                        break;
                                    case Constants.MODES_FACE_IDENTIFICATION:
                                        ClockUtils.mMode = Constants.MODES_FACE_IDENTIFICATION;
                                        launchFragment(Constants.FRAGMENT_TAG_FACE_IDENTIFICATION);
                                        break;
                                    case Constants.MODES_QR_CODE:
                                        ClockUtils.mMode = Constants.MODES_QR_CODE;
                                        launchFragment(Constants.FRAGMENT_TAG_QR_CODE);
                                        break;
                                    case Constants.MODES_RFID:
                                        ClockUtils.mMode = Constants.MODES_RFID;
                                        launchFragment(Constants.FRAGMENT_TAG_RFID);
                                        break;
                                }

                            }
                        } else {
                            //not come here   show error dialog


                        }
                    }

                    mHandler.sendEmptyMessage(Constants.CLOSE_MESSAGE_DIALOG);


                    if (DeviceUtils.mEmployeeModel != null) {
                        if (DeviceUtils.mEmployeeModel.getModules() == Constants.MODULES_ATTENDANCE_ACCESS ||
                                DeviceUtils.mEmployeeModel.getModules() == Constants.MODULES_ACCESS) {
                            turnBlueToothOn();
                            turnGpsOn();
                        }
                    }


                    break;
                case Constants.BACK_TO_INDEX_PAGE:
                    LOG.D(TAG, "BACK_TO_INDEX_PAGE Handle");

                    //invisible marquee
                    mLayoutMarqeuu.setVisibility(View.INVISIBLE);
                    mLayoutIdentifyResult.setVisibility(View.VISIBLE);
                    mTxtIdentifyResult.setText("");

                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    mHandler.removeMessages(Constants.LAUNCH_VIDEO);
                    mHandler.sendEmptyMessageDelayed(Constants.LAUNCH_VIDEO, DeviceUtils.VIDEO_DELAYED_TIME);

                    mHandler.removeMessages(Constants.MSG_CHECK_USER_CLOCK_DB);
                    mHandler.sendEmptyMessageDelayed(Constants.MSG_CHECK_USER_CLOCK_DB, DeviceUtils.CHECK_USER_CLOCK_DB_DELAYED_TIME);

                    mHandler.removeMessages(Constants.MSG_CHECK_WEB_SOCKET_ALIVE);
                    mHandler.sendEmptyMessageDelayed(Constants.MSG_CHECK_WEB_SOCKET_ALIVE, DeviceUtils.CHECK_WEB_SOCKET_TIME);

                    ClockUtils.clearClockData();

                    checkFragmentBackToHome();

                    mTxtBackToIndex.setText(getString(R.string.txt_home_setting));
                    mTxtBackToIndex.setBackgroundResource(R.mipmap.icon_back_to_home_setting);

                    EnterpriseUtils.mFacePngList.clear();

                    break;

                case Constants.CLOSE_MESSAGE_DIALOG:
                    LOG.D(TAG, "CLOSE_MESSAGE_DIALOG");
//                    if(mMessageDialog != null){
//                        if(mMessageDialog.isShowing()){
//                            mMessageDialog.dismiss();
//                        }
//                    }
                    if (mMessageDialog != null) {
                        if (mMessageDialog.isShowing()) {
                            mMessageDialog.dismiss();
                            //back to index page
                        } else {

                        }
                    }


//                    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//                    // The first in the list of RunningTasks is always the foreground task.
//                    ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
//                    String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
//                    LOG.D(TAG,"CLOSE_MESSAGE_DIALOG foregroundTaskPackageName = " + foregroundTaskPackageName);
//                    LOG.D(TAG,"CLOSE_MESSAGE_DIALOG mContext.getPackageName() = " + mContext.getPackageName());

                    if (foregroundTaskPackageName != null && foregroundTaskPackageName.equals(mContext.getPackageName())) {
                        //foreGround
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        ClockUtils.clearClockData();

                        checkFragmentBackToHome();

                        LOG.D(TAG, "CLOSE_MESSAGE_DIALOG DeviceUtils.VIDEO_DELAYED_TIME = " + DeviceUtils.VIDEO_DELAYED_TIME);
                        mHandler.removeMessages(Constants.LAUNCH_VIDEO);
                        mHandler.sendEmptyMessageDelayed(Constants.LAUNCH_VIDEO, DeviceUtils.VIDEO_DELAYED_TIME);
                        mHandler.removeMessages(Constants.MSG_CHECK_USER_CLOCK_DB);
                        mHandler.sendEmptyMessageDelayed(Constants.MSG_CHECK_USER_CLOCK_DB, DeviceUtils.CHECK_USER_CLOCK_DB_DELAYED_TIME);
                        mHandler.removeMessages(Constants.MSG_CHECK_WEB_SOCKET_ALIVE);
                        mHandler.sendEmptyMessageDelayed(Constants.MSG_CHECK_WEB_SOCKET_ALIVE, DeviceUtils.CHECK_WEB_SOCKET_TIME);

                    }


                    break;

                case Constants.CLOSE_IMAGE_DIALOG:
                    LOG.D(TAG, "CLOSE_IMAGE_DIALOG");
                    if (mImageDialog != null) {
                        if (mImageDialog.isShowing()) {
                            mImageDialog.dismiss();
                        }
                    }
                    break;


                case Constants.CLOSE_VIDEO:
                    LOG.D(TAG, "CLOSE_VIDEO");
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    //invisible marquee
                    mLayoutMarqeuu.setVisibility(View.INVISIBLE);
                    mLayoutIdentifyResult.setVisibility(View.VISIBLE);

                    mHandler.removeMessages(Constants.LAUNCH_VIDEO);
                    mHandler.sendEmptyMessageDelayed(Constants.LAUNCH_VIDEO, DeviceUtils.VIDEO_DELAYED_TIME);

                    break;

                case Constants.CLOSE_APP:
                    LOG.D(TAG, "CLOSE_APP");
                    finish();
                    break;

                case Constants.MSG_WEB_SOCKET_CONNECT:
                    LOG.D(TAG, "MSG_WEB_SOCKET_CONNECT");
                    mImgSocketState.setBackgroundResource(R.mipmap.icon_socket_connected);
                    break;

                case Constants.MSG_WEB_SOCKET_DISCONNECT:
                    LOG.D(TAG, "MSG_WEB_SOCKET_DISCONNECT");

                    LOG.D(TAG, "MSG_WEB_SOCKET_DISCONNECT foregroundTaskPackageName = " + foregroundTaskPackageName);
                    LOG.D(TAG, "MSG_WEB_SOCKET_DISCONNECT mContext.getPackageName() = " + mContext.getPackageName());

                    if (foregroundTaskPackageName != null && foregroundTaskPackageName.equals(mContext.getPackageName())) {
                        //foreGround
                        mImgSocketState.setBackgroundResource(R.mipmap.icon_socket_disconnect);
                        WebSocketManager.getInstance(mContext).reconnect(DeviceUtils.mWsUri, DeviceUtils.WEB_SOCKET_TIME_OUT, mContext, mHandler);
                    } else {

                    }

                    break;

                case Constants.MSG_CHECK_USER_CLOCK_DB:
                    LOG.D(TAG, "MSG_CHECK_USER_CLOCK_DB");
                    //upload DB Info
                    EnterpriseUtils.uploadUserClockDb(mContext);

                    mHandler.removeMessages(Constants.MSG_CHECK_USER_CLOCK_DB);
                    mHandler.sendEmptyMessageDelayed(Constants.MSG_CHECK_USER_CLOCK_DB, DeviceUtils.CHECK_USER_CLOCK_DB_DELAYED_TIME);

                    break;

                case Constants.MSG_UPDATE_MARQUEE:
                    LOG.D(TAG, "MSG_UPDATE_MARQUEE");
                    mTxtMarquee.setText(DeviceUtils.mMarqueeString);
                    break;

                case Constants.MSG_RESTART:
                    LOG.D(TAG, "MSG_RESTART");
                    restartApp();
                    break;

                case Constants.MSG_CHECK_WEB_SOCKET_ALIVE:
                    LOG.D(TAG, "MSG_CHECK_WEB_SOCKET_ALIVE");

                    mHandler.removeMessages(Constants.MSG_CHECK_WEB_SOCKET_ALIVE);
                    mHandler.sendEmptyMessageDelayed(Constants.MSG_CHECK_WEB_SOCKET_ALIVE, DeviceUtils.CHECK_WEB_SOCKET_TIME);
                    if (foregroundTaskPackageName != null && foregroundTaskPackageName.equals(mContext.getPackageName())) {
                        //foreGround
                        WebSocketManager.getInstance(mContext).reconnect(DeviceUtils.mWsUri, DeviceUtils.WEB_SOCKET_TIME_OUT, mContext, mHandler);
                    } else {

                    }


                    break;

                case Constants.MSG_SHOW_REGISTER_DIALOG:
                    LOG.D(TAG, "MSG_SHOW_REGISTER_DIALOG");

                    Bundle bundle = msg.getData();
                    String message = bundle.getString(Constants.KEY_REGISTER_MESSAGE);
                    String registerIdTitle = bundle.getString(Constants.KEY_REGISTER_ID_TITLE);
                    String registerId = bundle.getString(Constants.KEY_REGISTER_ID);
                    String registerNameTitle = bundle.getString(Constants.KEY_REGISTER_NAME_TITLE);
                    String registerName = bundle.getString(Constants.KEY_REGISTER_NAME);
                    String delayTime = bundle.getString(Constants.KEY_REGISTER_DELAY_TIME);


                    LOG.D(TAG, "MSG_SHOW_REGISTER_DIALOG message = " + message);
                    LOG.D(TAG, "MSG_SHOW_REGISTER_DIALOG registerIdTitle = " + registerIdTitle);
                    LOG.D(TAG, "MSG_SHOW_REGISTER_DIALOG registerId = " + registerId);
                    LOG.D(TAG, "MSG_SHOW_REGISTER_DIALOG registerNameTitle = " + registerNameTitle);
                    LOG.D(TAG, "MSG_SHOW_REGISTER_DIALOG registerName = " + registerName);
                    LOG.D(TAG, "MSG_SHOW_REGISTER_DIALOG delayTime = " + delayTime);

                    if (delayTime != null) {
                        initRegisterDialog(0, message, registerIdTitle, registerId, registerNameTitle, registerName, mHandler);
                    } else {
                        initRegisterDialog(MESSAGE_DIALOG_DELAYED_TIME, message, registerIdTitle, registerId, registerNameTitle, registerName, mHandler);
                    }

                    mRegisterDialog.show();

                    break;

                case Constants.MSG_CLOSE_REGISTER_DIALOG:
                    LOG.D(TAG, "MSG_CLOSE_REGISTER_DIALOG");

                    if (mRegisterDialog != null) {
                        if (mRegisterDialog.isShowing()) {
                            mRegisterDialog.dismiss();
                        }
                    }

                    break;

                case Constants.MSG_VISITOR_EXPIRE:
                    LOG.D(TAG, "MSG_VISITOR_EXPIRE");

                    initMessageDialog(getString(R.string.txt_visitor_expire), MESSAGE_DIALOG_DELAYED_TIME, true, mHandler);
                    mMessageDialog.show();

                    break;

                case Constants.MSG_SHOW_EMPLOYEE_THREE_TIMES_ERROR:
                    initImageDialog(MESSAGE_DIALOG_DELAYED_TIME, R.mipmap.icon_dialog_failed, getString(R.string.txt_employee_three_times_fail), ClockUtils.mLoginTime, mHandler);
                    mImageDialog.show();
                    break;

                case Constants.MSG_CLOSE_VIDEO:
                    LOG.D(TAG, "MSG_CLOSE_VIDEO getSupportFragmentManager().findFragmentByTag(VideoFragment.TAG) = " + getSupportFragmentManager().findFragmentByTag(VideoFragment.TAG));
                    if (getSupportFragmentManager().findFragmentByTag(VideoFragment.TAG) != null) {
                        VideoFragment videoFragment = (VideoFragment) getSupportFragmentManager().findFragmentByTag(VideoFragment.TAG);
                        videoFragment.closeVideo();
                    }
                    break;

                case Constants.MSG_DO_API:
                    LOG.D(TAG, "MSG_DO_API DeviceUtils.mEmployeeModel = " + DeviceUtils.mEmployeeModel);


                    if (DeviceUtils.mEmployeeModel != null) {
                        int module = DeviceUtils.mEmployeeModel.getModules();
                        LOG.D(TAG, "MSG_DO_API module   = " + module);

                        String type = "";
                        int serialNumber = -1;

                        switch (module) {
                            case Constants.MODULES_ATTENDANCE:
                                ClockUtils.mType = Constants.CLOCK_IN;
                                mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                                ++ClockUtils.mSerialNumber;
                                mSharedPreference.edit().putInt(Constants.PREF_KEY_SERIAL_NUMBER, ClockUtils.mSerialNumber).commit();
                                mHandler.removeMessages(Constants.CLOSE_CLOCK_DIALOG);
                                mProgress.setVisibility(View.VISIBLE);

                                type = Constants.CLOCK_IN;
                                serialNumber = ClockUtils.mSerialNumber;


                                ClockUtils.mRecordMode = Constants.RECORD_MODE_RECORD;
                                ApiUtils.deviceAttendanceRecords(TAG, mContext, DeviceUtils.mDeviceName, false, type, serialNumber, deviceAttendanceRecordsListener);
                                break;
                            case Constants.MODULES_ACCESS:
                                if (DeviceUtils.mIsEmployeeOpenDoor == true) {
                                    mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                                    mHandler.removeMessages(Constants.CLOSE_CLOCK_DIALOG);

                                    type = Constants.ACCESS_IN;
                                    ++ClockUtils.mSerialNumber;
                                    serialNumber = ClockUtils.mSerialNumber;

                                    mProgress.setVisibility(View.VISIBLE);

                                    setFaceIdentifyResultToClient(true, ClockUtils.mLoginName, ClockUtils.mLoginAccount);
                                    EnterpriseUtils.openDoorOne(mContext);

                                    //show Dialog
                                    initImageDialog(MESSAGE_DIALOG_DELAYED_TIME, R.mipmap.icon_dialog_success, getString(R.string.txt_clock_complete), ClockUtils.mLoginTime, mHandler);
                                    mImageDialog.show();

                                    if (getSupportFragmentManager().findFragmentByTag(EnglishPinCodeFragment.TAG) != null) {
                                        EnglishPinCodeFragment englishPinCodeFragment = (EnglishPinCodeFragment) getSupportFragmentManager().findFragmentByTag(EnglishPinCodeFragment.TAG);
                                        englishPinCodeFragment.clearLoginAccount();

                                    }

                                    ApiUtils.deviceAccessRecords(TAG, mContext, DeviceUtils.mDeviceName, false, type, serialNumber, deviceAccessRecordsListener);
                                } else {

                                }

                                break;
                            case Constants.MODULES_ATTENDANCE_ACCESS:
                                ClockUtils.mType = Constants.CLOCK_IN;
                                mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                                ++ClockUtils.mSerialNumber;
                                mSharedPreference.edit().putInt(Constants.PREF_KEY_SERIAL_NUMBER, ClockUtils.mSerialNumber).commit();
                                mHandler.removeMessages(Constants.CLOSE_CLOCK_DIALOG);
                                mProgress.setVisibility(View.VISIBLE);

                                type = Constants.CLOCK_IN;
                                serialNumber = ClockUtils.mSerialNumber;


                                ClockUtils.mRecordMode = Constants.RECORD_MODE_RECORD;
                                ApiUtils.deviceAttendanceRecords(TAG, mContext, DeviceUtils.mDeviceName, false, type, serialNumber, deviceAttendanceRecordsListener);

                                if (DeviceUtils.mIsEmployeeOpenDoor == true) {
                                    type = Constants.ACCESS_IN;
                                    ++ClockUtils.mSerialNumber;
                                    serialNumber = ClockUtils.mSerialNumber;

                                    setFaceIdentifyResultToClient(true, ClockUtils.mLoginName, ClockUtils.mLoginAccount);
                                    EnterpriseUtils.openDoorOne(mContext);
                                    ApiUtils.deviceAccessRecords(TAG, mContext, DeviceUtils.mDeviceName, false, type, serialNumber, deviceAccessRecordsListener);
                                } else {

                                }


                                break;
                            default:
                                break;
                        }

                    }


                    //check module

                    break;


            }
        }
    };

    public void onLaunchHomeFragment() {
        mTxtActionBarTitle.setVisibility(View.VISIBLE);
        mTxtActionBarSubTitle.setVisibility(View.VISIBLE);
        mLayoutFooterBarLeft.setVisibility(View.VISIBLE);

        mHandler.removeMessages(Constants.LAUNCH_VIDEO);
        mHandler.sendEmptyMessageDelayed(Constants.LAUNCH_VIDEO, DeviceUtils.VIDEO_DELAYED_TIME);
        FDRControlManager.getInstance(mContext, MainActivity.this).initFdr();
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LOG.D(TAG, "onActivityResult resultCode = " + resultCode + " requestCode = " + requestCode);

        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    LOG.D(TAG, "bluetooth enable");

                    String bluetoothDoorAddress = mSharedPreference.getString(Constants.PREF_KEY_BLUETOOTH_DOOR_ADDRESS, null);
                    LOG.D(TAG, "onActivityResult bluetoothDoorAddress = " + bluetoothDoorAddress);
                    if (bluetoothDoorAddress != null) {
                        Intent bluetoothLeService = new Intent(this, BluetoothLeService.class);
                        startService(bluetoothLeService);
                        bindService(bluetoothLeService, mBluetoothServiceConnection, BIND_AUTO_CREATE);

                    }

                    registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

                } else {
                    LOG.D(TAG, "bluetooth disable");
                }
                break;
        }
    }


    //****************************************************************** bluetooth start ********************************************************************

    public void bluetoothDoorConnect(String address) {
        LOG.D(TAG, "bluetoothDoorConnect address = " + address);
        if (address != null) {
            mSharedPreference.edit().putString(Constants.PREF_KEY_BLUETOOTH_DOOR_ADDRESS, address).apply();
        } else {

        }

        Intent bluetoothLeService = new Intent(this, BluetoothLeService.class);
        startService(bluetoothLeService);
        bindService(bluetoothLeService, mBluetoothServiceConnection, BIND_AUTO_CREATE);
    }

    public void bluetoothDoorDisConnect() {
        LOG.D(TAG, "bluetoothDoorConnect mIsBluetoothServiceBinded = " + mIsBluetoothServiceBinded);
        if (mIsBluetoothServiceBinded) {
            unbindService(mBluetoothServiceConnection);
            mIsBluetoothServiceBinded = false;
        }

    }

    // Code to manage Service lifecycle.
    private IBinder mBluetoothService;
    private final ServiceConnection mBluetoothServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            LOG.D(TAG, "onServiceConnected service = " + service);
            mBluetoothService = service;
            if (mBluetoothService != null)
                mIsBluetoothServiceBinded = true;
            else {
                LOG.D(TAG, "[mBluetoothServiceConnection] mService is null");
                return;
            }

            String bluetoothDoorAddress = mSharedPreference.getString(Constants.PREF_KEY_BLUETOOTH_DOOR_ADDRESS, null);

            DeviceUtils.mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            LOG.D(TAG, "mBluetoothLeService is okay bluetoothDoorAddress: " + bluetoothDoorAddress);
            DeviceUtils.mBluetoothLeService.connect(bluetoothDoorAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LOG.D(TAG, "onServiceDisconnected mBluetoothService: " + mBluetoothService + " componentName = " + componentName);
            if (mBluetoothService != null) {
                String bluetoothDoorAddress = mSharedPreference.getString(Constants.PREF_KEY_BLUETOOTH_DOOR_ADDRESS, null);
                DeviceUtils.mBluetoothLeService = ((BluetoothLeService.LocalBinder) mBluetoothService).getService();
                LOG.D(TAG, "mBluetoothLeService is okay 22 mDeviceAddress: " + bluetoothDoorAddress);
                DeviceUtils.mBluetoothLeService.connect(bluetoothDoorAddress);
            } else {
//                DeviceUtils.mBluetoothLeService = ((BluetoothLeService.LocalBinder) mBluetoothService).getService();
//                DeviceUtils.mBluetoothLeService.disconnect();
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_FAIL);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        return intentFilter;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            LOG.D(TAG, "action = " + action);
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                LOG.D(TAG, "Only gatt, just wait");
//                System.out.println("控制页面连接了");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
//                mConnected = false;// => for control light
//                System.out.println("控制页面未连接");
//                myTV.setText("");
//                LOG.D(TAG, "控制页面未连接 mDeviceAddress: " +mDeviceAddress);
//                mBluetoothLeService.connect(mDeviceAddress);

                DeviceUtils.mIsBTConnected = false;
                //connect disconnect, reconnect
                if (getSupportFragmentManager().findFragmentByTag(ConfigureSettingBluetoothFragment.TAG) != null) {
                    ConfigureSettingBluetoothFragment configureSettingBluetoothFragment = (ConfigureSettingBluetoothFragment) getSupportFragmentManager().findFragmentByTag(ConfigureSettingBluetoothFragment.TAG);
                    configureSettingBluetoothFragment.setBluetoothState(getString(R.string.txt_bluetooth_state_reconnect));

                    bluetoothDoorDisConnect();
                    bluetoothDoorConnect(null);

                    Toast.makeText(getApplicationContext(), getString(R.string.txt_bluetooth_state_reconnect), Toast.LENGTH_SHORT).show();

//                    DeviceUtils.mBluetoothLeService

                }

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

                DeviceUtils.mIsBTConnected = true;

                if (getSupportFragmentManager().findFragmentByTag(ConfigureSettingBluetoothFragment.TAG) != null) {
                    ConfigureSettingBluetoothFragment configureSettingBluetoothFragment = (ConfigureSettingBluetoothFragment) getSupportFragmentManager().findFragmentByTag(ConfigureSettingBluetoothFragment.TAG);
                    configureSettingBluetoothFragment.setBluetoothState(getString(R.string.txt_bluetooth_state_success));
                }

                Toast.makeText(getApplicationContext(), getString(R.string.txt_bluetooth_state_success), Toast.LENGTH_SHORT).show();

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                LOG.D(TAG, "RECV DATA");
//            	myTV.setText("蓝牙设备连接成功");
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
//            	byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
//            	String returnMsg = bytesToHexString(data);
//            	if (returnMsg != null) {
//            		System.out.println("传输过来的是"+returnMsg.substring(5, 6));
//            		if ("E".equals(returnMsg.substring(5, 6))) {
//            			Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
//					}
//                }
                if (data != null) {

                }

            } else if (BluetoothLeService.ACTION_GATT_FAIL.equals(action)) {
                //connect fail
                DeviceUtils.mIsBTConnected = false;
                if (getSupportFragmentManager().findFragmentByTag(ConfigureSettingBluetoothFragment.TAG) != null) {
                    ConfigureSettingBluetoothFragment configureSettingBluetoothFragment = (ConfigureSettingBluetoothFragment) getSupportFragmentManager().findFragmentByTag(ConfigureSettingBluetoothFragment.TAG);
                    configureSettingBluetoothFragment.setBluetoothState(getString(R.string.txt_bluetooth_state_fail_choose_again));
                }

                Toast.makeText(getApplicationContext(), getString(R.string.txt_bluetooth_state_fail_choose_again), Toast.LENGTH_SHORT).show();
            }
        }
    };


    /*******************************************Error to server callback*****************************************************/

    //Live Detection Fail callback
    private IDeviceAttendanceRecordsListener deviceAttendanceRecordsLDFListener = new IDeviceAttendanceRecordsListener() {
        @Override
        public void onDeviceAttendanceRecords(RecordsReplyModel model) {
            LOG.D(TAG, "deviceAttendanceRecordsLDFListener onDeviceAttendanceRecords  model = " + model);
            mProgress.setVisibility(View.GONE);
            //show result message

            mTxtIdentifyResult.setText(Html.fromHtml(getString(R.string.live_detection_fail)));
            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);

            if (model != null) {
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    //back to index fragment or show success dialog
                    LOG.D(TAG, "onDeviceAttendanceRecords model.getStatus() = " + model.getStatus());
                } else {
                    //Server fail , add to DB
                    if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
//                        EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED);
                        EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_ATTENDANCE);
                        return;
                    }
                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_ATTENDANCE);
                }
            } else {
                //Fail
                if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
//                        EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED);
                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_ATTENDANCE);
                    return;
                }
                EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_ATTENDANCE);

            }
        }
    };

    private IDeviceAccessRecordsListener deviceAccessRecordsLDFListener = new IDeviceAccessRecordsListener() {
        @Override
        public void onDeviceAccessRecords(RecordsReplyModel model) {
            LOG.D(TAG, "deviceAccessRecordsLDFListener onDeviceAccessRecords model =  " + model);
            mProgress.setVisibility(View.GONE);

            //show result message
            mTxtIdentifyResult.setText(Html.fromHtml(getString(R.string.live_detection_fail)));
            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);

            if (model != null) {
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    //back to index fragment or show success dialog
                    LOG.D(TAG, "onDeviceAccessRecords model.getStatus() = " + model.getStatus());
                } else {
                    //Error
                    if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
                        EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_ACCESS);
                        return;
                    }

                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_ACCESS);

                }
            } else {
                //Error
                if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_ACCESS);
                    return;
                }

                EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_ACCESS);

            }
        }
    };

    private IDeviceVisitorRecordsListener deviceVisitorRecordsLDFListener = new IDeviceVisitorRecordsListener() {
        @Override
        public void onDeviceVisitorRecords(RecordsReplyModel model) {
            LOG.D(TAG, "deviceVisitorRecordsLDFListener onDeviceVisitorRecords model = " + model);
            mProgress.setVisibility(View.GONE);

            //show result message
            mTxtIdentifyResult.setText(Html.fromHtml(getString(R.string.live_detection_fail)));
            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);


            if (model != null) {
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    //back to index fragment or show success dialog
                    LOG.D(TAG, "onDeviceVisitorRecords model.getStatus() = " + model.getStatus());

                } else {
                    //Error

                    if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
                        EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_VISITORS);
                        return;
                    }

                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_VISITORS);

                }
            } else {
                if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_VISITORS);
                    return;
                }
                EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_VISITORS);

            }
        }
    };

    private IDeviceVisitorAccessRecordsListener deviceVisitorAccessRecordsLDFListener = new IDeviceVisitorAccessRecordsListener() {
        @Override
        public void onDeviceVisitorAccessRecords(RecordsReplyModel model) {
            LOG.D(TAG, "deviceVisitorAccessRecordsLDFListener onDeviceVisitorAccessRecords model =  " + model);
            mProgress.setVisibility(View.GONE);

            //show result message
            mTxtIdentifyResult.setText(Html.fromHtml(getString(R.string.live_detection_fail)));
            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);

            if (model != null) {
                LOG.D(TAG, "onDeviceVisitorAccessRecords model.getStatus() =  " + model.getStatus());

                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    //back to index fragment or show success dialog
                    LOG.D(TAG, "onDeviceAccessRecords model.getStatus() = " + model.getStatus());
                    if (ClockUtils.mType == Constants.CLOCK_UNKNOWN) {
                        return;
                    }

                } else {
                    //Error
//                    if(ClockUtils.mType == Constants.CLOCK_UNKNOWN){
////                        EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED);
//                        EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_VISITORS);
//                        return;
//                    }
//
////                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED);
//                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_VISITORS);
                }
            } else {
                //Error
//                if(ClockUtils.mType == Constants.CLOCK_UNKNOWN){
////                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED);
//                    EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_FAILED, Constants.MODULES_VISITORS);
//                    return;
//                }
//
////                EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED);
//                EnterpriseUtils.addUserClockToDb(mContext, Constants.FACE_VERIFY_SUCCEED, Constants.MODULES_VISITORS);
            }
        }
    };


    private IAttendanceUnrecognizedLogListener attendanceUnrecognizedLogLDFListener = new IAttendanceUnrecognizedLogListener() {
        @Override
        public void onAttendanceUnrecognizedLog(RecordsReplyModel model) {
            LOG.D(TAG, "attendanceUnrecognizedLogLDFListener onAttendanceUnrecognizedLog model = " + model);
            mProgress.setVisibility(View.GONE);

            mTxtIdentifyResult.setText(Html.fromHtml(getString(R.string.live_detection_fail)));

            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);

            if (model != null) {
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {
                } else {
                    //Server fail , add to DB
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);

                }
            } else {
                //Fail
                EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
            }

        }
    };

    private IAccessUnrecognizedLogListener accessUnrecognizedLogLDFListener = new IAccessUnrecognizedLogListener() {
        @Override
        public void onAccessUnrecognizedLog(RecordsReplyModel model) {
            LOG.D(TAG, "accessUnrecognizedLogLDFListener onAccessUnrecognizedLog model = " + model);
            mProgress.setVisibility(View.GONE);

            mTxtIdentifyResult.setText(Html.fromHtml(getString(R.string.live_detection_fail)));

            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);

            if (model != null) {
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {


                } else {
                    //Server fail , add to DB
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);

                }
            } else {
                //Error save to DB
                LOG.D(TAG, "onAccessUnrecognizedLog model == null");
                EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
            }
        }
    };

    private IAccessVisitorUnrecognizedLogListener accessVisitorUnrecognizedLogLDFListener = new IAccessVisitorUnrecognizedLogListener() {
        @Override
        public void onAccessVisitorUnrecognizedLog(RecordsReplyModel model) {
            LOG.D(TAG, "accessVisitorUnrecognizedLogLDFListener onAccessVisitorUnrecognizedLog model = " + model);
            mProgress.setVisibility(View.GONE);

            mTxtIdentifyResult.setText(Html.fromHtml(getString(R.string.live_detection_fail)));

            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);

            if (model != null) {
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {


                } else {
                    //Server fail , add to DB
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_VISITORS);

                }
            } else {
                //Error save to DB
                LOG.D(TAG, "onAccessUnrecognizedLog model == null");
                EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_VISITORS);
            }
        }
    };


    private IVisitorsUnrecognizedLogListener visitorsUnrecognizedLogLDFListener = new IVisitorsUnrecognizedLogListener() {
        @Override
        public void onVisitorsUnrecognizedLog(RecordsReplyModel model) {
            LOG.D(TAG, "visitorsUnrecognizedLogLDFListener onVisitorsUnrecognizedLog model = " + model);
            mProgress.setVisibility(View.GONE);

            mTxtIdentifyResult.setText(Html.fromHtml(getString(R.string.live_detection_fail)));

            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);

            if (model != null) {
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {

                } else {
                    //Server fail , add to DB
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_VISITORS);

                }
            } else {
                //Error save to DB
                LOG.D(TAG, "onVisitorsUnrecognizedLog model == null");

                EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_VISITORS);
            }
        }
    };

    private IBapIdentifyListener bapIdentifyListener = new IBapIdentifyListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onBapIdentify(BapIdentifyModel model) {
            LOG.D(TAG, "onBapIdentify model = " + model);
//            mProgress.setVisiableWithAnimate(View.GONE);

            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

            } else {
                mHandler.removeMessages(Constants.LAUNCH_VIDEO);
                mHandler.sendEmptyMessageDelayed(Constants.LAUNCH_VIDEO, DeviceUtils.VIDEO_DELAYED_TIME);
            }

            if (model != null) {

                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {
                    //check if securityCode can clock here
                    boolean isLoginAccountCanClock = EnterpriseUtils.checkIfSecurityCodeLegal(model.getBapIdentifyData().getSecurityCode(), ClockUtils.mRoleModel);
                    if (!isLoginAccountCanClock) {
                        //fail

                        mProgress.setVisibility(View.GONE);
                        mTxtIdentifyResult.setText(Html.fromHtml(getString(R.string.face_identify_fail)));
                        mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                        mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);
                        ClockUtils.mFaceVerify = Constants.FACE_VERIFY_FAILED;

                        //1.check fragment
                        LOG.D(TAG, "ClockUtils.mMode = " + ClockUtils.mMode);
                        if (ClockUtils.mMode == Constants.MODES_SECURITY_CODE || ClockUtils.mMode == Constants.MODES_FACE_ICON
                                || ClockUtils.mMode == Constants.MODES_QR_CODE || ClockUtils.mMode == Constants.MODES_RFID) {
                            ClockUtils.mType = Constants.CLOCK_UNKNOWN;
                            ++ClockUtils.mSerialNumber;
                            mSharedPreference.edit().putInt(Constants.PREF_KEY_SERIAL_NUMBER, ClockUtils.mSerialNumber).commit();

                            //not unrecognized
                            ClockUtils.mRecordMode = Constants.RECORD_MODE_RECORD;
                            if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE_ACCESS) {
                                EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
                                EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
                            } else if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE) {
                                EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
                            } else {
                                EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
                            }

                        } else {

                            ClockUtils.mRecordMode = Constants.RECORD_MODE_UNRECOGNIZED;
                            if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE_ACCESS) {
                                EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
                                EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
                            } else if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE) {
                                EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
                            } else {
                                EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
                            }

                        }
                        return;
                    }

                    ClockUtils.mLoginName = model.getBapIdentifyData().getFirstName();
                    ClockUtils.mLoginAccount = model.getBapIdentifyData().getSecurityCode();
                    ClockUtils.mLoginUuid = model.getBapIdentifyData().getUuid();
                    ClockUtils.mLoginVerifyStatus = "SUCCEED";

                    //show result and save data to db
                    mProgress.setVisibility(View.GONE);

                    mTxtIdentifyResult.setTextColor(getResources().getColor(R.color.success));
//                    mTxtIdentifyResult.setText(ClockUtils.mLoginAccount);
                    mTxtIdentifyResult.setText(ClockUtils.mLoginName);
                    mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                    mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);

                    if (mIsFromMotp) {
//                launch Motp
//                Uri webPage = Uri.parse("otpauth://totp/gorillaqad?secret=4DKT7AB2L4Y43N6CV4BEPZFIU7URCWSE&digits=6&period=30&issuer=multiOTP");
                        Uri webPage = Uri.parse("otpauth://totp/gorillaqad?");
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, webPage);

                        Bundle bag = new Bundle();
                        bag.putBoolean("IsBackFromAttendance", true);
                        webIntent.putExtras(bag);

                        startActivity(webIntent);

                        mTxtIdentifyResult.setText("");


                    }
                    LOG.D(TAG, "ClockUtils.mModule = " + ClockUtils.mModule);

                    ClockUtils.mFaceVerify = Constants.FACE_VERIFY_SUCCEED;
                    if (mRoleModel instanceof EmployeeModel) {
                        LOG.D(TAG, "ONLINE_FACE_VERIFY_SUCCESS EmployeeModel");

                        //Set data
                        if (DeviceUtils.mIsRadioClockIn) {
                            ClockUtils.mType = Constants.CLOCK_IN;
                        } else {
                            ClockUtils.mType = Constants.CLOCK_OUT;
                        }

                        ClockUtils.mRecordMode = Constants.RECORD_MODE_RECORD;


                        if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE_ACCESS) {
                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
                        } else if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE) {
                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
                        } else {
                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
                        }


                    } else if (mRoleModel instanceof VisitorModel) {
                        LOG.D(TAG, "ONLINE_FACE_VERIFY_SUCCESS VisitorModel");
//                        doVisitorClockApi(DeviceUtils.mIsRadioClockIn, DeviceUtils.mIsRadioClockOut, DeviceUtils.mIsVisitorOpenDoor);
                        //Set data
                        if (DeviceUtils.mIsRadioClockIn) {
                            ClockUtils.mType = Constants.VISITOR_VISIT;
                        } else {
                            ClockUtils.mType = Constants.VISITOR_LEAVE;
                        }

                        ClockUtils.mRecordMode = Constants.RECORD_MODE_RECORD;
                        EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_VISITORS);
                    }

                } else {
                    retryFDR();
                }
            } else {
                retryFDR();
            }
        }
    };

    private void retryFDR() {
        LOG.D(TAG, "retryFDR-retry -" + retry);
        //fail
        if (retry - 1 == 0) {

            mProgress.setVisibility(View.GONE);
            mTxtIdentifyResult.setText(Html.fromHtml(getString(R.string.face_identify_fail)));
            mTxtIdentifyResult.setTextColor(getResources().getColor(R.color.error));
            mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);
            ClockUtils.mFaceVerify = Constants.FACE_VERIFY_FAILED;

            //1.check fragment
            LOG.D(TAG, "ClockUtils.mMode = " + ClockUtils.mMode);
            if (ClockUtils.mMode == Constants.MODES_SECURITY_CODE || ClockUtils.mMode == Constants.MODES_FACE_ICON
                    || ClockUtils.mMode == Constants.MODES_QR_CODE || ClockUtils.mMode == Constants.MODES_RFID) {
                ClockUtils.mType = Constants.CLOCK_UNKNOWN;
                ++ClockUtils.mSerialNumber;
                mSharedPreference.edit().putInt(Constants.PREF_KEY_SERIAL_NUMBER, ClockUtils.mSerialNumber).commit();
//                        sendErrorLogToServer(false, ClockUtils.mRoleModel.getModules(), ClockUtils.mType, ClockUtils.mSerialNumber);

                //not unrecognized
                ClockUtils.mRecordMode = Constants.RECORD_MODE_RECORD;
                if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE_ACCESS) {
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
                } else if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE) {
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
                } else {
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
                }


            } else {
//                        sendErrorLogToServer(true, ClockUtils.mRoleModel.getModules(), ClockUtils.mType, ClockUtils.mSerialNumber);

                ClockUtils.mRecordMode = Constants.RECORD_MODE_UNRECOGNIZED;
                if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE_ACCESS) {
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
                } else if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE) {
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
                } else {
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
                }

            }
        } else {
            retry--;
            mProgress.setVisibility(View.GONE);
            mTxtIdentifyResult.setText(Html.fromHtml(getString(R.string.face_identify_fail)));
            ClockUtils.mFaceVerify = Constants.FACE_VERIFY_FAILED;

            //1.check fragment
            LOG.D(TAG, "ClockUtils.mMode = " + ClockUtils.mMode);
            if (ClockUtils.mMode == Constants.MODES_SECURITY_CODE || ClockUtils.mMode == Constants.MODES_FACE_ICON
                    || ClockUtils.mMode == Constants.MODES_QR_CODE || ClockUtils.mMode == Constants.MODES_RFID) {
                ClockUtils.mType = Constants.CLOCK_UNKNOWN;
                ++ClockUtils.mSerialNumber;
                mSharedPreference.edit().putInt(Constants.PREF_KEY_SERIAL_NUMBER, ClockUtils.mSerialNumber).commit();
//                        sendErrorLogToServer(false, ClockUtils.mRoleModel.getModules(), ClockUtils.mType, ClockUtils.mSerialNumber);

                //not unrecognized
                ClockUtils.mRecordMode = Constants.RECORD_MODE_RECORD;
                if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE_ACCESS) {
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
                } else if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE) {
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
                } else {
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
                }


            } else {
//                        sendErrorLogToServer(true, ClockUtils.mRoleModel.getModules(), ClockUtils.mType, ClockUtils.mSerialNumber);

                ClockUtils.mRecordMode = Constants.RECORD_MODE_UNRECOGNIZED;
                if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE_ACCESS) {
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
                } else if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE) {
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
                } else {
                    EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
                }

            }
            startFDR();
        }
    }

    private IBapVerifyListener bapVerifyListener = new IBapVerifyListener() {
        @Override
        public void onBapVerify(BapVerifyModel model) {
            LOG.D(TAG, "onBapVerify model = " + model);

            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

            } else {
                mHandler.removeMessages(Constants.LAUNCH_VIDEO);
                mHandler.sendEmptyMessageDelayed(Constants.LAUNCH_VIDEO, DeviceUtils.VIDEO_DELAYED_TIME);
            }

            if (model != null) {
                if (model.getStatus().equals(Constants.STATUS_SUCCESS)) {

                    //show result and save data to db
                    mProgress.setVisibility(View.GONE);

                    mTxtIdentifyResult.setTextColor(getResources().getColor(R.color.success));
//                    mTxtIdentifyResult.setText(ClockUtils.mLoginAccount);
                    mTxtIdentifyResult.setText(ClockUtils.mLoginName);
                    mHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                    mHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);
                    if (mIsFromMotp) {
//                launch Motp
//                Uri webPage = Uri.parse("otpauth://totp/gorillaqad?secret=4DKT7AB2L4Y43N6CV4BEPZFIU7URCWSE&digits=6&period=30&issuer=multiOTP");
                        Uri webPage = Uri.parse("otpauth://totp/gorillaqad?");
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, webPage);

                        Bundle bag = new Bundle();
                        bag.putBoolean("IsBackFromAttendance", true);
                        webIntent.putExtras(bag);

                        startActivity(webIntent);

                        mTxtIdentifyResult.setText("");


                    }
                    LOG.D(TAG, "ClockUtils.mModule = " + ClockUtils.mModule);

                    ClockUtils.mFaceVerify = Constants.FACE_VERIFY_SUCCEED;
                    if (mRoleModel instanceof EmployeeModel) {
                        LOG.D(TAG, "ONLINE_FACE_VERIFY_SUCCESS EmployeeModel");

                        //Set data
                        if (DeviceUtils.mIsRadioClockIn) {
                            ClockUtils.mType = Constants.CLOCK_IN;
                        } else {
                            ClockUtils.mType = Constants.CLOCK_OUT;
                        }

                        ClockUtils.mRecordMode = Constants.RECORD_MODE_RECORD;


                        if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE_ACCESS) {
                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
                            EnterpriseUtils.openDoorOne(mContext);
                        } else if (ClockUtils.mModule == Constants.MODULES_ATTENDANCE) {
                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ATTENDANCE);
                        } else {
                            EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_ACCESS);
                            EnterpriseUtils.openDoorOne(mContext);
                        }


                    } else if (mRoleModel instanceof VisitorModel) {
                        LOG.D(TAG, "ONLINE_FACE_VERIFY_SUCCESS VisitorModel");
//                        doVisitorClockApi(DeviceUtils.mIsRadioClockIn, DeviceUtils.mIsRadioClockOut, DeviceUtils.mIsVisitorOpenDoor);
                        //Set data
                        if (DeviceUtils.mIsRadioClockIn) {
                            ClockUtils.mType = Constants.VISITOR_VISIT;
                        } else {
                            ClockUtils.mType = Constants.VISITOR_LEAVE;
                        }

                        ClockUtils.mRecordMode = Constants.RECORD_MODE_RECORD;
                        EnterpriseUtils.addUserClockToDb(mContext, ClockUtils.mFaceVerify, Constants.MODULES_VISITORS);
                        EnterpriseUtils.openDoorOne(mContext);
                    }
                } else {
                    retryFDR();
                }
            } else {
                retryFDR();
            }
        }
    };
}
