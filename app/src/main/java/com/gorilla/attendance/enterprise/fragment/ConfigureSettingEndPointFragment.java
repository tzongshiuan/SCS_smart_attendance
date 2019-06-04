package com.gorilla.attendance.enterprise.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.gorilla.attendance.enterprise.MainActivity;
import com.gorilla.attendance.enterprise.R;
import com.gorilla.attendance.enterprise.util.ClockUtils;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.DeviceUtils;
import com.gorilla.attendance.enterprise.util.FDRControlManager;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.NetworkManager;

/**
 * Created by ggshao on 2017/4/6.
 */

public class ConfigureSettingEndPointFragment extends BaseFragment {
    public static final String TAG = "ConfigureSettingEndPointFragment";

    private View mView = null;

    private MainActivity mMainActivity;
    private FragmentActivity mActivity = null;
    private Context mContext = null;

    private NetworkManager mNetworkManager = null;
    private Handler mActivityHandler = null;

    private EditText mEdtServerIp = null;
    private EditText mEdtFtpIp = null;
    private EditText mEdtWsIp = null;
    private EditText mEdtFtpAccount = null;
    private EditText mEdtFtpPassword = null;
    private EditText mEdtDeviceToken = null;
    private EditText mEdtDeviceName = null;
    private EditText mEdtVideoIdleSeconds = null;
    private EditText mEdtSyncTimeOutSeconds = null;
    private EditText mEdtIdentifyResultIdleSeconds = null;
    private EditText mEdtIdentifyThreshold = null;
    private EditText mEdtRetry = null;
    private Switch mSwitchLiveness = null;

    private Switch mSwitchEmployeeOpenDoor = null;
    private Switch mSwitchVisitorOpenDoor = null;
    private Switch mSwitchRtspSetting = null;

    private Switch mSwitchClockAuto = null;
    private Switch mSwitchOnlineMode = null;
//    private Switch mSwitchInnoLux = null;

    private EditText mEdtRtspIp = null;
    private EditText mEdtRtspUrl = null;
    private EditText mEdtRtspAccount = null;
    private EditText mEdtRtspPassword = null;

    private EditText mEdtFdrRange = null;

    private TextView mTxtDeviceToken = null;

    private SharedPreferences mSharedPreference = null;

    private Button mBtnDone = null;
    private Button mBtnCancel = null;
    private Button mBtnBluetoothSetting = null;
    private Button mBtnEmployeeRegister = null;
    private Button mBtnVisitorRegister = null;

    private ConfigureSettingBluetoothFragment mConfigureSettingBluetoothFragment = null;
    private RegisterChooseModeFragment mRegisterChooseModeFragment = null;


    private static int mTxtDeviceTokenClickCount = 0;
    private static final int DEVICE_TOKEN_CLICK_INTERVAL = 5000;
    private static final int MAX_TXT_TOKEN_CLICK_COUNT = 5;
    private long mTxtDeviceTokenPressTime = -1;

    private ImageView mImgQrCode = null;

    private LinearLayout mLayoutClockRadio = null;
    private RadioGroup mRadioGroupClock = null;
    private RadioButton mRadioButtonClockIn = null;
    private RadioButton mRadioButtonClockOut = null;

    private boolean mIsRadioClockInChange = false;
    private boolean mIsRadioClockOutChange = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LOG.D(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mMainActivity = (MainActivity) getActivity();
        mContext = getActivity();

        mActivityHandler = mMainActivity.getHandler();
        mSharedPreference = mContext.getSharedPreferences(Constants.PREF_NAME_ATTENDANCE_ENTERPRISE, Context.MODE_PRIVATE);

        if (getFragmentManager().getBackStackEntryCount() > 0) {
            mActivityHandler.removeMessages(Constants.LAUNCH_VIDEO);
            mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.SETTING_DETAIL_DELAYED_TIME);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LOG.V(TAG, "[onCreateView] ");

        if (mView == null) {
            mView = inflater.inflate(R.layout.configure_setting_end_point_fragment, null);
        } else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
        }

//        initView(mView);

        initView();
        return mView;
    }

    @Override
    public void onPause() {
        LOG.D(TAG, "onPause");
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        LOG.D(TAG, "onResume getFragmentManager().getBackStackEntryCount()  = " + getFragmentManager().getBackStackEntryCount());

        //RTSP Setting temp close
//        mSwitchRtspSetting.setEnabled(false);
//        mEdtRtspIp.setEnabled(false);
//        mEdtRtspUrl.setEnabled(false);
//        mEdtRtspAccount.setEnabled(false);
//        mEdtRtspPassword.setEnabled(false);
        mSwitchEmployeeOpenDoor.setEnabled(false);
//        mSwitchInnoLux.setEnabled(false);

        //set setting value
//        String serverIp = mSharedPreference.getString(Constants.PREF_KEY_SERVER_IP, null);
//        String ftpIp = mSharedPreference.getString(Constants.PREF_KEY_FTP_IP, null);
//        String wsIp = mSharedPreference.getString(Constants.PREF_KEY_WS_IP, null);
        String ftpAccount = mSharedPreference.getString(Constants.PREF_KEY_FTP_ACCOUNT, null);
        String ftpPassword = mSharedPreference.getString(Constants.PREF_KEY_FTP_PASSWORD, null);
        String deviceToken = mSharedPreference.getString(Constants.PREF_KEY_DEVICE_TOKEN, null);
        String deviceShowName = mSharedPreference.getString(Constants.PREF_KEY_DEVICE_SHOW_NAME, "");

        int videoIdleSeconds = mSharedPreference.getInt(Constants.PREF_KEY_VIDEO_IDLE_SECONDS, 0);
        int syncTimeOutSeconds = mSharedPreference.getInt(Constants.PREF_KEY_SYNC_TIME_OUT_SECONDS, 60);
        int identifyResultIdleSeconds = mSharedPreference.getInt(Constants.PREF_KEY_IDENTIFY_RESULT_IDLE_MILLI_SECONDS, DeviceUtils.mResultMessageDelayTime);
        float identifyThreshold = mSharedPreference.getFloat(Constants.PREF_KEY_IDENTIFY_THRESHOLD, (float) 0.6);
        int retry = mSharedPreference.getInt(Constants.PREF_KEY_RETRY, 3);
        boolean isLiveness = mSharedPreference.getBoolean(Constants.PREF_KEY_LIVENESS, true);
        boolean isRtspSetting = mSharedPreference.getBoolean(Constants.PREF_KEY_RTSP_SETTING, false);

        boolean isEmployeeOpenDoor = mSharedPreference.getBoolean(Constants.PREF_KEY_EMPLOYEE_OPEN_DOOR, true);
        boolean isVisitorOpenDoor = mSharedPreference.getBoolean(Constants.PREF_KEY_VISITOR_OPEN_DOOR, true);
//        boolean isInnoLux = mSharedPreference.getBoolean(Constants.PREF_KEY_INNO_LUX, false);

        boolean isClockAuto = mSharedPreference.getBoolean(Constants.PREF_KEY_CLOCK_AUTO, true);
        boolean isRadioClockIn = mSharedPreference.getBoolean(Constants.PREF_KEY_RADIO_CLOCK_IN, true);
        boolean isRadioClockOut = mSharedPreference.getBoolean(Constants.PREF_KEY_RADIO_CLOCK_OUT, false);
        boolean isOnlineMode = mSharedPreference.getBoolean(Constants.PREF_KEY_ONLINE_MODE, false);

        String rtspIp = mSharedPreference.getString(Constants.PREF_KEY_RTSP_IP, null);
        String rtspUrl = mSharedPreference.getString(Constants.PREF_KEY_RTSP_URL, null);
        String rtspAccount = mSharedPreference.getString(Constants.PREF_KEY_RTSP_ACCOUNT, null);
        String rtspPassword = mSharedPreference.getString(Constants.PREF_KEY_RTSP_PASSWORD, null);

        int getRange = mSharedPreference.getInt(Constants.PREF_KEY_FDR_RANGE, Constants.FDR_DEFAULT_RANGE);

//        mEdtServerIp.setText(serverIp);
//        mEdtFtpIp.setText(ftpIp);
//        mEdtWsIp.setText(wsIp);
        mEdtFtpAccount.setText(ftpAccount);
        mEdtFtpPassword.setText(ftpPassword);
//        mEdtDeviceToken.setText(deviceToken);
        mEdtDeviceName.setText(deviceShowName);

        mEdtVideoIdleSeconds.setText(String.valueOf(videoIdleSeconds));
        mEdtSyncTimeOutSeconds.setText(String.valueOf(syncTimeOutSeconds));
        mEdtIdentifyResultIdleSeconds.setText(String.valueOf(identifyResultIdleSeconds));
        mEdtIdentifyThreshold.setText(String.valueOf(identifyThreshold));
        mEdtRetry.setText(String.valueOf(retry));
        mSwitchEmployeeOpenDoor.setChecked(isEmployeeOpenDoor);
        mSwitchVisitorOpenDoor.setChecked(isVisitorOpenDoor);

        mSwitchLiveness.setChecked(isLiveness);
        mSwitchRtspSetting.setChecked(isRtspSetting);
//        mSwitchInnoLux.setChecked(isInnoLux);
        mSwitchClockAuto.setChecked(isClockAuto);
        mSwitchOnlineMode.setChecked(isOnlineMode);
        mRadioButtonClockIn.setChecked(isRadioClockIn);
        mRadioButtonClockOut.setChecked(isRadioClockOut);


        mEdtRtspIp.setText(rtspIp);
        mEdtRtspUrl.setText(rtspUrl);
        mEdtRtspAccount.setText(rtspAccount);
        mEdtRtspPassword.setText(rtspPassword);

        mEdtFdrRange.setText(String.valueOf(getRange));


        mEdtFtpAccount.setSelection(ftpAccount.length());
        mEdtFtpPassword.setSelection(ftpPassword.length());
        mEdtDeviceName.setSelection(deviceShowName.length());

        mEdtVideoIdleSeconds.setSelection((String.valueOf(videoIdleSeconds)).length());
        mEdtSyncTimeOutSeconds.setSelection((String.valueOf(syncTimeOutSeconds)).length());
        mEdtIdentifyResultIdleSeconds.setSelection((String.valueOf(identifyResultIdleSeconds)).length());
        mEdtIdentifyThreshold.setSelection((String.valueOf(identifyThreshold)).length());
        mEdtRetry.setSelection((String.valueOf(retry)).length());
        mEdtRtspIp.setSelection(rtspIp.length());
        mEdtRtspUrl.setSelection(rtspUrl.length());
        mEdtRtspAccount.setSelection(rtspAccount.length());
        mEdtRtspPassword.setSelection(rtspPassword.length());

        mEdtFdrRange.setSelection((String.valueOf(getRange)).length());

        if (deviceToken == null) {
            mEdtDeviceToken.setEnabled(true);
            mImgQrCode.setEnabled(true);
        } else if (deviceToken.isEmpty()) {
            mEdtDeviceToken.setEnabled(true);
            mImgQrCode.setEnabled(true);
        } else {
            mEdtDeviceToken.setEnabled(false);
            mImgQrCode.setEnabled(false);
        }

        if (isClockAuto) {
            mLayoutClockRadio.setVisibility(View.VISIBLE);
        } else {
            mLayoutClockRadio.setVisibility(View.GONE);
        }


    }

    @Override
    public void onStop() {
        LOG.D(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        LOG.D(TAG, "onDestroy");
        super.onDestroy();
    }


    private void initView() {
        mEdtServerIp = (EditText) mView.findViewById(R.id.edt_server_ip);
        mEdtFtpIp = (EditText) mView.findViewById(R.id.edt_ftp_ip);
        mEdtWsIp = (EditText) mView.findViewById(R.id.edt_ws_ip);

        mEdtFtpAccount = (EditText) mView.findViewById(R.id.edt_ftp_account);
        mEdtFtpPassword = (EditText) mView.findViewById(R.id.edt_ftp_password);
        mEdtDeviceToken = (EditText) mView.findViewById(R.id.edt_device_token);
        mEdtDeviceName = (EditText) mView.findViewById(R.id.edt_device_name);
        mEdtVideoIdleSeconds = (EditText) mView.findViewById(R.id.edt_video_idle_seconds);
        mEdtSyncTimeOutSeconds = (EditText) mView.findViewById(R.id.edt_sync_time_out_seconds);
        mEdtIdentifyResultIdleSeconds = (EditText) mView.findViewById(R.id.edt_identify_result_idle_seconds);
        mEdtIdentifyThreshold = (EditText) mView.findViewById(R.id.edt_identify_threshold);
        mEdtRetry = (EditText) mView.findViewById(R.id.edt_retry);
        mEdtRtspIp = (EditText) mView.findViewById(R.id.edt_rtsp_ip);
        mEdtRtspUrl = (EditText) mView.findViewById(R.id.edt_rtsp_url);
        mEdtRtspAccount = (EditText) mView.findViewById(R.id.edt_rtsp_account);
        mEdtRtspPassword = (EditText) mView.findViewById(R.id.edt_rtsp_password);

        mEdtFdrRange = (EditText) mView.findViewById(R.id.edt_fdr_range);


        mTxtDeviceToken = (TextView) mView.findViewById(R.id.txt_device_token);

        mBtnDone = (Button) mView.findViewById(R.id.btn_conf_done);
        mBtnCancel = (Button) mView.findViewById(R.id.btn_conf_cancel);
        mBtnBluetoothSetting = (Button) mView.findViewById(R.id.btn_conf_bluetooth_setting);
        mBtnEmployeeRegister = (Button) mView.findViewById(R.id.btn_conf_employee_register);
        mBtnVisitorRegister = (Button) mView.findViewById(R.id.btn_conf_visitor_register);


        mSwitchEmployeeOpenDoor = (Switch) mView.findViewById(R.id.switch_employee_open_door);
        mSwitchVisitorOpenDoor = (Switch) mView.findViewById(R.id.switch_visitor_open_door);
        mSwitchRtspSetting = (Switch) mView.findViewById(R.id.switch_rtsp_setting);

        mSwitchLiveness = (Switch) mView.findViewById(R.id.switch_liveness);

        mImgQrCode = (ImageView) mView.findViewById(R.id.img_qrcode);

        mBtnDone.setOnClickListener(mBtnDoneClickListener);
        mBtnCancel.setOnClickListener(mBtnCancelClickListener);
        mBtnBluetoothSetting.setOnClickListener(mBtnBluetoothSettingClickListener);
        mBtnEmployeeRegister.setOnClickListener(mBtnEmployeeRegisterClickListener);
        mBtnVisitorRegister.setOnClickListener(mBtnVisitorRegisterClickListener);

        mTxtDeviceToken.setOnClickListener(mTxtDeviceTokenClickListener);

        mImgQrCode.setOnClickListener(mImgQrCodeClickListener);

        String serverIp = mSharedPreference.getString(Constants.PREF_KEY_SERVER_IP, null);
        String ftpIp = mSharedPreference.getString(Constants.PREF_KEY_FTP_IP, null);
        String wsIp = mSharedPreference.getString(Constants.PREF_KEY_WS_IP, null);
        String deviceToken = mSharedPreference.getString(Constants.PREF_KEY_DEVICE_TOKEN, null);
        mEdtServerIp.setText(serverIp);
        mEdtFtpIp.setText(ftpIp);
        mEdtWsIp.setText(wsIp);
        mEdtDeviceToken.setText(deviceToken);

        mEdtFtpIp.setSelection(ftpIp.length());
        mEdtWsIp.setSelection(wsIp.length());
//        mEdtDeviceToken.setSelection(deviceToken.length());

        mSwitchClockAuto = (Switch) mView.findViewById(R.id.switch_clock_auto);
        mSwitchOnlineMode = (Switch) mView.findViewById(R.id.switch_online_mode);
        mSwitchClockAuto.setOnCheckedChangeListener(mSwitchClockAutoCheckedChangeListener);
        mSwitchOnlineMode.setOnCheckedChangeListener(mSwitchOnlineModeAutoCheckedChangeListener);

        mLayoutClockRadio = (LinearLayout) mView.findViewById(R.id.layout_clock_radio);
        mRadioGroupClock = (RadioGroup) mView.findViewById(R.id.radio_group_clock);
        mRadioButtonClockIn = (RadioButton) mView.findViewById(R.id.radio_button_clock_in);
        mRadioButtonClockOut = (RadioButton) mView.findViewById(R.id.radio_button_clock_out);

//        mRadioGroupClock.setOnCheckedChangeListener(mRadioGroupClockCheckedChangeListener);
        mRadioButtonClockIn.setOnCheckedChangeListener(mRadioButtonClockInCheckedChangeListener);
        mRadioButtonClockOut.setOnCheckedChangeListener(mRadioButtonClockOutCheckedChangeListener);

        mRadioButtonClockIn.setOnClickListener(mRadioButtonClockInClickListener);
        mRadioButtonClockOut.setOnClickListener(mRadioButtonClockOutClickListener);


    }

    private Button.OnClickListener mBtnDoneClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
//            launchFragment(Constants.FRAGMENT_TAG_CONF_END_POINT_SETTING, false);

            String originalServerIp = mSharedPreference.getString(Constants.PREF_KEY_SERVER_IP, "");
            String originalFtpIp = mSharedPreference.getString(Constants.PREF_KEY_FTP_IP, "");
            String originalWsIp = mSharedPreference.getString(Constants.PREF_KEY_WS_IP, "");
            String originalDeviceToken = mSharedPreference.getString(Constants.PREF_KEY_DEVICE_TOKEN, "");

            boolean originalRtspSetting = mSharedPreference.getBoolean(Constants.PREF_KEY_RTSP_SETTING, false);

            //check if change

            //Toast.makeText(mContext, getString(R.string.txt_save_success), Toast.LENGTH_SHORT).show();

            LOG.D(TAG, "");

            if (!originalServerIp.equals(mEdtServerIp.getText().toString()) ||
                    !originalFtpIp.equals(mEdtFtpIp.getText().toString()) ||
                    !originalWsIp.equals(mEdtWsIp.getText().toString()) ||
                    !originalDeviceToken.equals(mEdtDeviceToken.getText().toString()) ||
                    !originalDeviceToken.equals(mEdtDeviceToken.getText().toString()) ||
                    mSwitchRtspSetting.isChecked() != originalRtspSetting
//                    || mSwitchInnoLux.isChecked() != originalInnoLuxSetting

                    ) {
                //need to restart app

                new AlertDialog.Builder(mContext)
                        .setMessage(R.string.txt_dialog_restart_app)
                        .setPositiveButton(R.string.txt_log_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSharedPreference.edit().putString(Constants.PREF_KEY_SERVER_IP, mEdtServerIp.getText().toString()).apply();
                                mSharedPreference.edit().putString(Constants.PREF_KEY_FTP_IP, mEdtFtpIp.getText().toString()).apply();
                                mSharedPreference.edit().putString(Constants.PREF_KEY_WS_IP, mEdtWsIp.getText().toString()).apply();
                                mSharedPreference.edit().putString(Constants.PREF_KEY_FTP_ACCOUNT, mEdtFtpAccount.getText().toString()).apply();
                                mSharedPreference.edit().putString(Constants.PREF_KEY_FTP_PASSWORD, mEdtFtpPassword.getText().toString()).apply();
                                mSharedPreference.edit().putString(Constants.PREF_KEY_DEVICE_TOKEN, mEdtDeviceToken.getText().toString()).apply();
//                            mSharedPreference.edit().putString(Constants.PREF_KEY_DEVICE_NAME, mEdtDeviceName.getText().toString()).apply();


                                mSharedPreference.edit().putInt(Constants.PREF_KEY_VIDEO_IDLE_SECONDS, Integer.valueOf(mEdtVideoIdleSeconds.getText().toString())).apply();
                                mSharedPreference.edit().putInt(Constants.PREF_KEY_SYNC_TIME_OUT_SECONDS, Integer.valueOf(mEdtSyncTimeOutSeconds.getText().toString())).apply();
                                mSharedPreference.edit().putInt(Constants.PREF_KEY_IDENTIFY_RESULT_IDLE_MILLI_SECONDS, Integer.valueOf(mEdtIdentifyResultIdleSeconds.getText().toString())).apply();
                                mSharedPreference.edit().putFloat(Constants.PREF_KEY_IDENTIFY_THRESHOLD, Float.valueOf(mEdtIdentifyThreshold.getText().toString())).apply();
                                mSharedPreference.edit().putInt(Constants.PREF_KEY_RETRY, Integer.valueOf(mEdtRetry.getText().toString())).apply();

                                mSharedPreference.edit().putBoolean(Constants.PREF_KEY_EMPLOYEE_OPEN_DOOR, mSwitchEmployeeOpenDoor.isChecked()).apply();
                                mSharedPreference.edit().putBoolean(Constants.PREF_KEY_VISITOR_OPEN_DOOR, mSwitchVisitorOpenDoor.isChecked()).apply();

                                mSharedPreference.edit().putBoolean(Constants.PREF_KEY_LIVENESS, mSwitchLiveness.isChecked()).apply();

                                mSharedPreference.edit().putBoolean(Constants.PREF_KEY_RTSP_SETTING, mSwitchRtspSetting.isChecked()).apply();

                                mSharedPreference.edit().putString(Constants.PREF_KEY_RTSP_IP, mEdtRtspIp.getText().toString()).apply();
                                mSharedPreference.edit().putString(Constants.PREF_KEY_RTSP_URL, mEdtRtspUrl.getText().toString()).apply();
                                mSharedPreference.edit().putString(Constants.PREF_KEY_RTSP_ACCOUNT, mEdtRtspAccount.getText().toString()).apply();
                                mSharedPreference.edit().putString(Constants.PREF_KEY_RTSP_PASSWORD, mEdtRtspPassword.getText().toString()).apply();

                                mSharedPreference.edit().putInt(Constants.PREF_KEY_FDR_RANGE, Integer.parseInt(mEdtFdrRange.getText().toString())).apply();

                                if (FDRControlManager.getInstance(mContext).getFdrCtrl() != null) {
                                    FDRControlManager.getInstance(mContext).setFdrRange(Integer.parseInt(mEdtFdrRange.getText().toString()));
                                }

                                mSharedPreference.edit().putBoolean(Constants.PREF_KEY_CLOCK_AUTO, mSwitchClockAuto.isChecked()).apply();
                                mSharedPreference.edit().putBoolean(Constants.PREF_KEY_RADIO_CLOCK_IN, mRadioButtonClockIn.isChecked()).apply();
                                mSharedPreference.edit().putBoolean(Constants.PREF_KEY_RADIO_CLOCK_OUT, mRadioButtonClockOut.isChecked()).apply();
                                mSharedPreference.edit().putBoolean(Constants.PREF_KEY_ONLINE_MODE, mSwitchOnlineMode.isChecked()).apply();

                                LOG.D(TAG, "mSwitchLiveness.isChecked() = " + mSwitchLiveness.isChecked());
                                LOG.D(TAG, "mEdtDeviceToken.getText().toString() = " + mEdtDeviceToken.getText().toString());

                                mActivityHandler.sendEmptyMessageDelayed(Constants.MSG_RESTART, 1000);
                            }
                        })
                        .setNegativeButton(R.string.txt_log_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

            } else {
                mSharedPreference.edit().putString(Constants.PREF_KEY_FTP_ACCOUNT, mEdtFtpAccount.getText().toString()).apply();
                mSharedPreference.edit().putString(Constants.PREF_KEY_FTP_PASSWORD, mEdtFtpPassword.getText().toString()).apply();
//                mSharedPreference.edit().putString(Constants.PREF_KEY_DEVICE_NAME, mEdtDeviceName.getText().toString()).apply();

                mSharedPreference.edit().putInt(Constants.PREF_KEY_VIDEO_IDLE_SECONDS, Integer.valueOf(mEdtVideoIdleSeconds.getText().toString())).apply();
                mSharedPreference.edit().putInt(Constants.PREF_KEY_SYNC_TIME_OUT_SECONDS, Integer.valueOf(mEdtSyncTimeOutSeconds.getText().toString())).apply();
                mSharedPreference.edit().putInt(Constants.PREF_KEY_IDENTIFY_RESULT_IDLE_MILLI_SECONDS, Integer.valueOf(mEdtIdentifyResultIdleSeconds.getText().toString())).apply();
                mSharedPreference.edit().putFloat(Constants.PREF_KEY_IDENTIFY_THRESHOLD, Float.valueOf(mEdtIdentifyThreshold.getText().toString())).apply();
                mSharedPreference.edit().putInt(Constants.PREF_KEY_RETRY, Integer.valueOf(mEdtRetry.getText().toString())).apply();

                mSharedPreference.edit().putBoolean(Constants.PREF_KEY_EMPLOYEE_OPEN_DOOR, mSwitchEmployeeOpenDoor.isChecked()).apply();
                mSharedPreference.edit().putBoolean(Constants.PREF_KEY_VISITOR_OPEN_DOOR, mSwitchVisitorOpenDoor.isChecked()).apply();
                mSharedPreference.edit().putBoolean(Constants.PREF_KEY_LIVENESS, mSwitchLiveness.isChecked()).apply();


                mSharedPreference.edit().putBoolean(Constants.PREF_KEY_RTSP_SETTING, mSwitchRtspSetting.isChecked()).apply();

                mSharedPreference.edit().putString(Constants.PREF_KEY_RTSP_IP, mEdtRtspIp.getText().toString()).apply();
                mSharedPreference.edit().putString(Constants.PREF_KEY_RTSP_URL, mEdtRtspUrl.getText().toString()).apply();
                mSharedPreference.edit().putString(Constants.PREF_KEY_RTSP_ACCOUNT, mEdtRtspAccount.getText().toString()).apply();
                mSharedPreference.edit().putString(Constants.PREF_KEY_RTSP_PASSWORD, mEdtRtspPassword.getText().toString()).apply();

                mSharedPreference.edit().putInt(Constants.PREF_KEY_FDR_RANGE, Integer.parseInt(mEdtFdrRange.getText().toString())).apply();

                if (FDRControlManager.getInstance(mContext).getFdrCtrl() != null) {
                    FDRControlManager.getInstance(mContext).setFdrRange(Integer.parseInt(mEdtFdrRange.getText().toString()));
                }

                mSharedPreference.edit().putBoolean(Constants.PREF_KEY_CLOCK_AUTO, mSwitchClockAuto.isChecked()).apply();
                mSharedPreference.edit().putBoolean(Constants.PREF_KEY_RADIO_CLOCK_IN, mRadioButtonClockIn.isChecked()).apply();
                mSharedPreference.edit().putBoolean(Constants.PREF_KEY_RADIO_CLOCK_OUT, mRadioButtonClockOut.isChecked()).apply();
                mSharedPreference.edit().putBoolean(Constants.PREF_KEY_ONLINE_MODE, mSwitchOnlineMode.isChecked()).apply();

                LOG.D(TAG, "mSwitchLiveness.isChecked() = " + mSwitchLiveness.isChecked());


                DeviceUtils.mDeviceShowName = mEdtDeviceName.getText().toString();
                DeviceUtils.mFtpAccount = mEdtFtpAccount.getText().toString();
                DeviceUtils.mFtpPassword = mEdtFtpPassword.getText().toString();

                DeviceUtils.mIsEmployeeOpenDoor = mSwitchEmployeeOpenDoor.isChecked();
                DeviceUtils.mIsVisitorOpenDoor = mSwitchVisitorOpenDoor.isChecked();

//                DeviceUtils.mIsInnoLux = mSwitchInnoLux.isChecked();

                DeviceUtils.mIsLivenessOn = mSwitchLiveness.isChecked();

                DeviceUtils.mIsRtspSetting = mSwitchRtspSetting.isChecked();

                DeviceUtils.mIsClockAuto = mSwitchClockAuto.isChecked();
                DeviceUtils.mIsRadioClockIn = mRadioButtonClockIn.isChecked();
                DeviceUtils.mIsRadioClockOut = mRadioButtonClockOut.isChecked();
                DeviceUtils.mIsOnlineMode = mSwitchOnlineMode.isChecked();

                DeviceUtils.mRtspIp = mEdtRtspIp.getText().toString();
                DeviceUtils.mRtspUrl = mEdtRtspUrl.getText().toString();
                DeviceUtils.mRtspAccount = mEdtRtspAccount.getText().toString();
                DeviceUtils.mRtspPassword = mEdtRtspPassword.getText().toString();

                DeviceUtils.mFdrRange = Integer.parseInt(mEdtFdrRange.getText().toString());

                DeviceUtils.VIDEO_DELAYED_TIME = Integer.valueOf(mEdtVideoIdleSeconds.getText().toString()) * 1000;
                DeviceUtils.SYNC_TIME_OUT = Integer.valueOf(mEdtSyncTimeOutSeconds.getText().toString()) * 1000;
                DeviceUtils.mResultMessageDelayTime = Integer.valueOf(mEdtIdentifyResultIdleSeconds.getText().toString());

                Toast.makeText(mContext, getString(R.string.txt_save_success), Toast.LENGTH_SHORT).show();
            }

        }
    };

    private Button.OnClickListener mBtnCancelClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mActivityHandler.sendEmptyMessage(Constants.BACK_TO_INDEX_PAGE);
        }
    };

    private Button.OnClickListener mBtnBluetoothSettingClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            launchFragment(Constants.FRAGMENT_TAG_CONF_BLUETOOTH_SETTING, false, -1);
        }
    };

    private Button.OnClickListener mBtnEmployeeRegisterClickListener = new Button.OnClickListener() {
        public void onClick(View v) {

            if(DeviceUtils.mEmployeeModel != null){
                if(DeviceUtils.mEmployeeModel.getModes() != null){
                    if(DeviceUtils.mEmployeeModel.getModes().length == 1 && DeviceUtils.mEmployeeModel.getModes()[0] == Constants.MODES_FACE_IDENTIFICATION){
                        Toast.makeText(mContext, getString(R.string.not_support_regist), Toast.LENGTH_SHORT).show();
                    }else{
                        ClockUtils.mModule = DeviceUtils.mEmployeeModel.getModules();
                        ClockUtils.mRoleModel = DeviceUtils.mEmployeeModel;
                        launchFragment(Constants.FRAGMENT_TAG_REGISTER_CHOOSE_MODE, false, Constants.REGISTER_TYPE_EMPLOYEE);
                    }
                }else{
                    Toast.makeText(mContext, getString(R.string.not_support_regist), Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(mContext, getString(R.string.not_support_regist), Toast.LENGTH_SHORT).show();
            }

        }
    };

    private Button.OnClickListener mBtnVisitorRegisterClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            if(DeviceUtils.mVisitorModel != null){

                if(DeviceUtils.mVisitorModel.getModes() != null){
                    if(DeviceUtils.mVisitorModel.getModes().length == 1 && DeviceUtils.mVisitorModel.getModes()[0] == Constants.MODES_FACE_SCANNER){
                        Toast.makeText(mContext, getString(R.string.not_support_regist), Toast.LENGTH_SHORT).show();
                    }else{
                        ClockUtils.mModule = DeviceUtils.mVisitorModel.getModules();
                        ClockUtils.mRoleModel = DeviceUtils.mVisitorModel;
                        launchFragment(Constants.FRAGMENT_TAG_REGISTER_CHOOSE_MODE, false, Constants.REGISTER_TYPE_VISITOR);
                    }
                }else{
                    Toast.makeText(mContext, getString(R.string.not_support_regist), Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(mContext, getString(R.string.not_support_regist), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private TextView.OnClickListener mTxtDeviceTokenClickListener = new TextView.OnClickListener() {
        public void onClick(View v) {
            long currentSystemTime = System.currentTimeMillis();

            LOG.D(TAG, "mLayoutSocketStateClickListener mTxtDeviceTokenClickCount = " + mTxtDeviceTokenClickCount);
            if (mTxtDeviceTokenPressTime < currentSystemTime) {
                //first time click
                mTxtDeviceTokenPressTime = currentSystemTime + DEVICE_TOKEN_CLICK_INTERVAL;
                mTxtDeviceTokenClickCount = 1;

            } else {
                if (mTxtDeviceTokenPressTime - currentSystemTime <= DEVICE_TOKEN_CLICK_INTERVAL) {
                    mTxtDeviceTokenClickCount++;
                    if (mTxtDeviceTokenClickCount == MAX_TXT_TOKEN_CLICK_COUNT) {
                        LOG.D(TAG, "Press Socket 5 times");
                        mEdtDeviceToken.setEnabled(true);
                        mImgQrCode.setEnabled(true);


                    }
                }
            }
        }
    };

    private ImageView.OnClickListener mImgQrCodeClickListener = new ImageView.OnClickListener() {
        public void onClick(View v) {
            LOG.D(TAG, "mImgQrCodeClickListener");
//            new IntentIntegrator(mMainActivity).initiateScan();

            IntentIntegrator.forSupportFragment(ConfigureSettingEndPointFragment.this).initiateScan();
        }
    };

    private RadioGroup.OnCheckedChangeListener mRadioGroupClockCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            LOG.D(TAG, "checkedId = " + checkedId);
            LOG.D(TAG, "R.id.radio_button_clock_in = " + R.id.radio_button_clock_in);
            LOG.D(TAG, "R.id.radio_button_clock_out = " + R.id.radio_button_clock_out);

            switch (checkedId) {
                case R.id.radio_button_clock_in:
                    if (mRadioButtonClockIn.isSelected() == true) {
                        mRadioButtonClockIn.setChecked(false);
                    }
                    break;
                case R.id.radio_button_clock_out:
                    if (mRadioButtonClockOut.isSelected() == true) {
                        mRadioButtonClockOut.setChecked(false);
                    }
                    break;
            }

            mRadioButtonClockIn.setClickable(true);
            mRadioButtonClockOut.setClickable(true);


        }
    };

    private RadioButton.OnCheckedChangeListener mRadioButtonClockInCheckedChangeListener = new RadioButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            LOG.D(TAG, "mRadioButtonClockInCheckedChangeListener isChecked = " + isChecked);
//            mRadioButtonClockIn.setClickable(true);
//            mRadioButtonClockOut.setClickable(true);

//            if(isChecked == true){
//                mIsRadioClockInChange = true;
//            }

        }

    };

    private RadioButton.OnCheckedChangeListener mRadioButtonClockOutCheckedChangeListener = new RadioButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            LOG.D(TAG, "mRadioButtonClockOutCheckedChangeListener isChecked = " + isChecked);
//            mRadioButtonClockIn.setClickable(true);
//            mRadioButtonClockOut.setClickable(true);

//            if(isChecked == true){
//                mIsRadioClockOutChange = true;
//            }

        }
    };

    private RadioButton.OnClickListener mRadioButtonClockInClickListener = new RadioButton.OnClickListener() {

        @Override
        public void onClick(View v) {
            LOG.D(TAG, "mRadioButtonClockInClickListener onClick mRadioButtonClockIn.isChecked() = " + mRadioButtonClockIn.isChecked());
            LOG.D(TAG, "mRadioButtonClockInClickListener onClick mRadioButtonClockIn.isSelected() = " + mRadioButtonClockIn.isSelected());
            LOG.D(TAG, "mRadioButtonClockInClickListener onClick mRadioButtonClockOut.isChecked() = " + mRadioButtonClockOut.isChecked());
            LOG.D(TAG, "mRadioButtonClockInClickListener onClick mRadioButtonClockOut.isSelected() = " + mRadioButtonClockOut.isSelected());

            LOG.D(TAG, "mRadioButtonClockOutClickListener onClick mIsRadioClockInChange = " + mIsRadioClockInChange);

//            if(mIsRadioClockInChange){
//                mIsRadioClockInChange = false;
//            }else{
//                mRadioGroupClock.clearCheck();
//            }
        }
    };

    private RadioButton.OnClickListener mRadioButtonClockOutClickListener = new RadioButton.OnClickListener() {

        @Override
        public void onClick(View v) {
            LOG.D(TAG, "mRadioButtonClockOutClickListener onClick mRadioButtonClockIn.isChecked() = " + mRadioButtonClockIn.isChecked());
            LOG.D(TAG, "mRadioButtonClockOutClickListener onClick mRadioButtonClockIn.isSelected() = " + mRadioButtonClockIn.isSelected());
            LOG.D(TAG, "mRadioButtonClockOutClickListener onClick mRadioButtonClockOut.isChecked() = " + mRadioButtonClockOut.isChecked());
            LOG.D(TAG, "mRadioButtonClockOutClickListener onClick mRadioButtonClockOut.isSelected() = " + mRadioButtonClockOut.isSelected());

            LOG.D(TAG, "mRadioButtonClockOutClickListener onClick mIsRadioClockOutChange = " + mIsRadioClockOutChange);

//            mRadioButtonClockOut.toggle();
//
//            if(mIsRadioClockOutChange){
//                mIsRadioClockOutChange = false;
//            }else{
//                mRadioGroupClock.clearCheck();
//            }
        }
    };

    private Switch.OnCheckedChangeListener mSwitchClockAutoCheckedChangeListener = new Switch.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            LOG.D(TAG, "isChecked = " + isChecked);
            if (isChecked) {
                mLayoutClockRadio.setVisibility(View.VISIBLE);
            } else {
                mLayoutClockRadio.setVisibility(View.GONE);
            }
        }
    };

    private Switch.OnCheckedChangeListener mSwitchOnlineModeAutoCheckedChangeListener = new Switch.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            LOG.D(TAG, "mSwitchOnlineModeAutoCheckedChangeListener isChecked = " + isChecked);

        }
    };


    private void launchFragment(int tag, boolean removeChooseList, int registerType) {
        LOG.V(TAG, "[launchFragment] tag = " + tag);


        FragmentManager fm = getFragmentManager();
        //FragmentManager fm = getChildFragmentManager();
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

            case Constants.FRAGMENT_TAG_CONF_BLUETOOTH_SETTING:

                mConfigureSettingBluetoothFragment = new ConfigureSettingBluetoothFragment();

                ft.replace(Constants.CONTENT_FRAME_ID, mConfigureSettingBluetoothFragment, ConfigureSettingBluetoothFragment.TAG).addToBackStack(ConfigureSettingEndPointFragment.TAG).commitAllowingStateLoss();
                break;

            case Constants.FRAGMENT_TAG_REGISTER_CHOOSE_MODE:

                mRegisterChooseModeFragment = new RegisterChooseModeFragment();
                arguments.putInt(RegisterChooseModeFragment.KEY_REGISTER_TYPE, registerType);
                mRegisterChooseModeFragment.setArguments(arguments);

                ft.replace(Constants.CONTENT_FRAME_ID, mRegisterChooseModeFragment, RegisterChooseModeFragment.TAG).addToBackStack(RegisterChooseModeFragment.TAG).commitAllowingStateLoss();

                break;

        }

    }


//    private void launchFragment(int tag, boolean removeChooseList) {
//        LOG.V(TAG, "[launchFragment] tag = " + tag);
//
//
//        FragmentManager fm = getFragmentManager();
//        //FragmentManager fm = getChildFragmentManager();
//        if(fm == null) {
//            LOG.W(TAG, "[launchFragment] FragmentManager is null.");
//            return;
//        }
//
//        FragmentTransaction ft = fm.beginTransaction();
//        if(ft == null) {
//            LOG.W(TAG, "[launchFragment] FragmentTransaction is null.");
//            return;
//        }
//
////        Bundle arguments= new Bundle();
//
//        switch (tag) {
//
//            case Constants.FRAGMENT_TAG_CONF_BLUETOOTH_SETTING:
//
//                mConfigureSettingBluetoothFragment = new ConfigureSettingBluetoothFragment();
//
//                ft.replace(Constants.CONTENT_FRAME_ID, mConfigureSettingBluetoothFragment, ConfigureSettingBluetoothFragment.TAG).addToBackStack(ConfigureSettingEndPointFragment.TAG).commitAllowingStateLoss();
//                break;
//
//
//        }
//
//    }

    // Get the results:
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        LOG.D(TAG,"onActivityResult result = " + result);
        if (result != null) {
            if (result.getContents() == null) {
                LOG.D(TAG, "Cancelled");
            } else {
                LOG.D(TAG, "Scanned " + result.getContents());
                mEdtDeviceToken.setText(result.getContents());

//                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
