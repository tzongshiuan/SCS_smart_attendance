package com.gorilla.attendance.enterprise.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gorilla.attendance.enterprise.MainActivity;
import com.gorilla.attendance.enterprise.R;
import com.gorilla.attendance.enterprise.Widget.EnglishKeyboardView;
import com.gorilla.attendance.enterprise.Widget.KeyboardView;
import com.gorilla.attendance.enterprise.datamodel.EmployeeModel;
import com.gorilla.attendance.enterprise.datamodel.RecordsReplyModel;
import com.gorilla.attendance.enterprise.datamodel.VisitorModel;
import com.gorilla.attendance.enterprise.util.ApiUtils;
import com.gorilla.attendance.enterprise.util.ClockUtils;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.DeviceUtils;
import com.gorilla.attendance.enterprise.util.EnterpriseUtils;
import com.gorilla.attendance.enterprise.util.FDRControlManager;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.NetworkManager;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceVisitorAccessRecordsListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.IDeviceVisitorRecordsListener;

import java.util.Calendar;

/**
 * Created by ggshao on 2017/5/11.
 */

public class EnglishPinCodeFragment extends BaseFragment {
    public static final String TAG = "EnglishPinCodeFragment";

    public static final String KEY_RENEW_SECURITY_CODE = "key-renew-security-code";
    public static final String KEY_PIN_CODE_TYPE = "key-pin-code-type";

    private View mView = null;

    private MainActivity mMainActivity;
    private FragmentActivity mActivity = null;
    private Context mContext = null;

    private NetworkManager mNetworkManager = null;
    private Handler mActivityHandler = null;

    private RelativeLayout mLayoutPinCode = null;
    private TextView mTxtPinCodeHint = null;
    private RelativeLayout mLayoutFdr = null;
    private FrameLayout mFdrFrame = null;


    private TextView mTxtLoginAccount = null;

    private String mLoginAccount = "";

    private KeyboardView mKeyboardView;

    private SharedPreferences mSharedPreference = null;

    private String mVisitorRenewSecurityCode = null;
    private int mPinCodeType = -1;

    private ProgressBar mProgress = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LOG.D(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mMainActivity = (MainActivity) getActivity();
        mContext = getActivity();

        DeviceUtils.checkSettingLanguage(mContext);
        mActivityHandler = mMainActivity.getHandler();


        mSharedPreference = mContext.getSharedPreferences(Constants.PREF_NAME_ATTENDANCE_ENTERPRISE, Context.MODE_PRIVATE);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mVisitorRenewSecurityCode = bundle.getString(KEY_RENEW_SECURITY_CODE);
            mPinCodeType = bundle.getInt(KEY_PIN_CODE_TYPE);
            bundle.clear();
        } else {
            mVisitorRenewSecurityCode = null;
            mPinCodeType = -1;
        }

        if (getFragmentManager().getBackStackEntryCount() > 0) {
            mActivityHandler.removeMessages(Constants.LAUNCH_VIDEO);
            if (mVisitorRenewSecurityCode == null) {
                mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.PAGE_DELAYED_TIME);
            } else {
                mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.RENEW_VISITOR_SECURITY_CODE_DELAYED_TIME);
            }


        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LOG.V(TAG, "[onCreateView] ");

        if (mView == null) {
            mView = inflater.inflate(R.layout.english_pin_code_fragment, null);
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
        mMainActivity.getMarquee().setOnKeyListener(null);
    }

    @Override
    public void onResume() {
        LOG.D(TAG, "onResume getFragmentManager().getBackStackEntryCount() = " + getFragmentManager().getBackStackEntryCount());
        super.onResume();

        if (ClockUtils.mRoleModel.getModules() == Constants.MODULES_VISITORS) {
            mTxtLoginAccount.setHint(getString(R.string.txt_visitor_pin_code_hint));
        } else {
            mTxtLoginAccount.setHint(getString(R.string.txt_please_input_password));
        }

        if (getFragmentManager().getBackStackEntryCount() > 0) {
            mMainActivity.setHomeSettingWord(getString(R.string.txt_home_page));
            mMainActivity.setHomeSettingBackGround(R.mipmap.icon_back_to_home);
        } else {
            mMainActivity.setHomeSettingWord(getString(R.string.txt_home_setting));
            mMainActivity.setHomeSettingBackGround(R.mipmap.icon_back_to_home_setting);
        }

        mMainActivity.setMarqueeRequestFocus();
        InputMethodManager imm = (InputMethodManager) mMainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mMainActivity.getMarquee().getWindowToken(), 0);

        //for  手動鍵盤輸入
        mMainActivity.getMarquee().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.PAGE_DELAYED_TIME);

                if (event.getAction() == KeyEvent.ACTION_UP) {
                    LOG.D(TAG, "keyCode = " + keyCode);
                    if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {

                        enterFunction();

                    }

                } else if (keyCode == KeyEvent.KEYCODE_DEL) {
//                        Toast.makeText(mContext, " KEYCODE_BACK = ", Toast.LENGTH_SHORT).show();
                    if (mLoginAccount.length() > 0) {
                        StringBuffer buf = new StringBuffer(mLoginAccount.length() - 1);
                        buf.append(mLoginAccount.substring(0, mLoginAccount.length() - 1)).append(mLoginAccount.substring(mLoginAccount.length() - 1 + 1));
                        mLoginAccount = buf.toString();

                        mTxtLoginAccount.setText(mLoginAccount);
                    }

                } else {
                    char pressedKey = (char) event.getUnicodeChar();
//                        Toast.makeText(mContext, " pressedKey = " + pressedKey, Toast.LENGTH_SHORT).show();
//                        Toast.makeText(mContext, " Character.toString(pressedKey) = " + Character.toString(pressedKey), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(mContext, " event.getUnicodeChar() = " + event.getUnicodeChar(), Toast.LENGTH_SHORT).show();


                    mLoginAccount += pressedKey;
                    mTxtLoginAccount.setText(mLoginAccount);
                }

                return false;
            }
        });

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

        FDRControlManager.getInstance(mContext).stopFdr(mActivityHandler, mFragmentHandler);
        mFdrFrame.removeAllViews();


    }

    private void initView() {
        LOG.D(TAG, "initView");

        mLayoutPinCode = (RelativeLayout) mView.findViewById(R.id.layout_pin_code);
        mTxtPinCodeHint = (TextView) mView.findViewById(R.id.txt_pin_code_hint);
        mLayoutFdr = (RelativeLayout) mView.findViewById(R.id.layout_fdr);
        mFdrFrame = (FrameLayout) mView.findViewById(R.id.fdr_frame);
//        mFdrFrame.removeAllViews();

        mTxtLoginAccount = (TextView) mView.findViewById(R.id.txt_login_account);

//        mTxtLoginAccount.setText("");
//        mLoginAccount = "";

        mProgress = (ProgressBar) mView.findViewById(R.id.marker_progress);

        if (mVisitorRenewSecurityCode != null) {
            mTxtPinCodeHint.setText(getString(R.string.txt_visitor_one_password_title));
        }

        mKeyboardView = (KeyboardView) mView.findViewById(R.id.keyboard);
        mKeyboardView.setOnKeyClickListener(new EnglishKeyboardView.OnKeyClickListener() {
            @Override
            public void onKeyClick(String key) {
                LOG.D(TAG, "onKeyClick key = " + key);

                mLoginAccount += key;
                mTxtLoginAccount.setText(mLoginAccount);

            }

            @Override
            public void onDeleteClick() {
                LOG.D(TAG, "onDeleteClick key = ");
                if (mLoginAccount.length() > 0) {
                    StringBuffer buf = new StringBuffer(mLoginAccount.length() - 1);
                    buf.append(mLoginAccount.substring(0, mLoginAccount.length() - 1)).append(mLoginAccount.substring(mLoginAccount.length() - 1 + 1));
                    mLoginAccount = buf.toString();

                    mTxtLoginAccount.setText(mLoginAccount);
                }
            }

            @Override
            public void onEnterClick() {
                enterFunction();
            }
        });
    }


    private void enterFunction() {
        LOG.D(TAG, "onEnterClick key = ");

        LOG.D(TAG, "mBtnNextClickListener mLoginAccount = " + mLoginAccount);
        LOG.D(TAG, "mBtnNextClickListener mVisitorRenewSecurityCode = " + mVisitorRenewSecurityCode);
        LOG.D(TAG, "mBtnNextClickListener mPinCodeType = " + mPinCodeType);


        boolean isLoginAccountCanClock = false;


        //Check mVisitorRenewSecurityCode, never here, china requirement
        if (mVisitorRenewSecurityCode != null) {
            //go to check renew Password mod
            if (mVisitorRenewSecurityCode.equals(mLoginAccount)) {
                //CLOCK AND OPEN DOOR
                ClockUtils.mType = Constants.VISITOR_VISIT;

                mProgress.setVisibility(View.VISIBLE);

                ++ClockUtils.mSerialNumber;
                ClockUtils.mLoginAccount = mLoginAccount;
                mSharedPreference.edit().putInt(Constants.PREF_KEY_SERIAL_NUMBER, ClockUtils.mSerialNumber).commit();
//                        ApiUtils.visitorRecords(TAG, mContext, DeviceUtils.mDeviceName, false, visitorRecordsListener);
                ClockUtils.mRecordMode = Constants.RECORD_MODE_RECORD;

                LOG.D(TAG, "onEnterClick ClockUtils.mLoginAccount = " + ClockUtils.mLoginAccount);
                ApiUtils.deviceVisitorRecords(TAG, mContext, DeviceUtils.mDeviceName, false, deviceVisitorRecordsListener);

                //check if do access door
                LOG.D(TAG, "onEnterClick DeviceUtils.mIsVisitorOpenDoor = " + DeviceUtils.mIsVisitorOpenDoor);
                if (DeviceUtils.mIsVisitorOpenDoor == true) {

                    LOG.D(TAG, "onEnterClick ClockUtils.mLoginName = " + ClockUtils.mLoginName);
                    LOG.D(TAG, "onEnterClick ClockUtils.mLoginAccount = " + ClockUtils.mLoginAccount);

//                            ApiUtils.deviceAccessRecords(TAG, mContext, DeviceUtils.mDeviceName, false, type, serialNumber, deviceAccessRecordsListener);
//                            setFaceIdentifyResultToClient(true, ClockUtils.mLoginName, ClockUtils.mLoginAccount);
//
//                            EnterpriseUtils.openDoorOne(mContext);

                    ApiUtils.deviceVisitorAccessRecords(TAG, mContext, DeviceUtils.mDeviceName, false, ClockUtils.mType, ++ClockUtils.mSerialNumber, deviceVisitorAccessRecordsListener);
//                            setFaceIdentifyResultToClient(true, ClockUtils.mLoginName, ClockUtils.mLoginAccount);

                    EnterpriseUtils.openDoorOne(mContext);

                }


            } else {
                //input fail
                Toast.makeText(mContext, getString(R.string.txt_visitor_one_password_wrong_input), Toast.LENGTH_SHORT).show();
                mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                mActivityHandler.sendEmptyMessage(Constants.BACK_TO_INDEX_PAGE);

            }

            return;
        } else {

        }

        //  1. check password if in list
        if (ClockUtils.mRoleModel instanceof EmployeeModel) {
            LOG.D(TAG, "I am Employee");

            EmployeeModel employeeModel = ((EmployeeModel) ClockUtils.mRoleModel);

            if (employeeModel.getEmployeeData() != null) {
                for (int i = 0; i < employeeModel.getEmployeeData().getAcceptances().size(); i++) {
                    if (mLoginAccount.equals(employeeModel.getEmployeeData().getAcceptances().get(i).getSecurityCode())) {
                        isLoginAccountCanClock = true;

                        ClockUtils.mLoginAccount = mLoginAccount;
                        ClockUtils.mLoginUuid = employeeModel.getEmployeeData().getAcceptances().get(i).getUuid();
                        ClockUtils.mLoginIntId = employeeModel.getEmployeeData().getAcceptances().get(i).getIntId();
                        ClockUtils.mLoginName = employeeModel.getEmployeeData().getAcceptances().get(i).getFirstName();
                        break;
                    }
                }


            } else {

            }

        } else if (ClockUtils.mRoleModel instanceof VisitorModel) {
            LOG.D(TAG, "I am visitor");

            VisitorModel visitorModel = ((VisitorModel) ClockUtils.mRoleModel);
            if (visitorModel.getVisitorData() != null) {
                for (int i = 0; i < visitorModel.getVisitorData().getAcceptances().size(); i++) {
                    if (mLoginAccount.equals(visitorModel.getVisitorData().getAcceptances().get(i).getSecurityCode())) {
                        isLoginAccountCanClock = true;

                        ClockUtils.mLoginAccount = mLoginAccount;
                        ClockUtils.mLoginUuid = visitorModel.getVisitorData().getAcceptances().get(i).getUuid();
                        ClockUtils.mLoginIntId = visitorModel.getVisitorData().getAcceptances().get(i).getIntId();
                        ClockUtils.mLoginName = visitorModel.getVisitorData().getAcceptances().get(i).getFirstName();


                        LOG.D(TAG, "visitorModel.getVisitorData().getAcceptances().get(i).getStartTime() = " +
                                visitorModel.getVisitorData().getAcceptances().get(i).getStartTime());

                        LOG.D(TAG, "visitorModel.getVisitorData().getAcceptances().get(i).getEndTime() = " +
                                visitorModel.getVisitorData().getAcceptances().get(i).getEndTime());


                        Calendar c = Calendar.getInstance();


                        if (visitorModel.getVisitorData().getAcceptances().get(i).getStartTime() == 0 ||
                                visitorModel.getVisitorData().getAcceptances().get(i).getEndTime() == 0) {
                            //means register from local pad
                            ClockUtils.mStartTime = c.getTimeInMillis() - 900000;
                            ClockUtils.mEndTime = c.getTimeInMillis() + 900000;
                            break;
                        } else {
                            if (visitorModel.getVisitorData().getAcceptances().get(i).getStartTime() <= c.getTimeInMillis() &&
                                    visitorModel.getVisitorData().getAcceptances().get(i).getEndTime() >= c.getTimeInMillis()) {
                                ClockUtils.mStartTime = visitorModel.getVisitorData().getAcceptances().get(i).getStartTime();
                                ClockUtils.mEndTime = visitorModel.getVisitorData().getAcceptances().get(i).getEndTime();
                                break;
                            } else {
                                //expire time
                                ClockUtils.mStartTime = -1;
                                ClockUtils.mEndTime = -1;
                            }
                        }


                    }
                }
            } else {

            }


        }

        if (isLoginAccountCanClock) {
            //check if expire
            Calendar c = Calendar.getInstance();
            LOG.D(TAG, "c.getTimeInMillis() = " + c.getTimeInMillis());
            LOG.D(TAG, "ClockUtils.mStartTime = " + ClockUtils.mStartTime);
            LOG.D(TAG, "ClockUtils.mEndTime = " + ClockUtils.mEndTime);
            if (ClockUtils.mStartTime != 0 && ClockUtils.mEndTime != 0) {
                if (ClockUtils.mStartTime <= c.getTimeInMillis() && ClockUtils.mEndTime >= c.getTimeInMillis()) {
//                            mActivityHandler.sendEmptyMessage(Constants.MSG_VISITOR_EXPIRE);
//                            return;
                } else {
                    mActivityHandler.sendEmptyMessage(Constants.MSG_VISITOR_EXPIRE);
                    return;
                }
            }

        } else {
            //show password wrong
            mTxtPinCodeHint.setText(getString(R.string.txt_wrong_password));
            mTxtLoginAccount.setText("");

            //send wrong Api
            ClockUtils.mLoginAccount = mLoginAccount;
            ClockUtils.mLiveness = "FAILED";
            ClockUtils.mLoginTime = System.currentTimeMillis();
            ++ClockUtils.mSerialNumber;
            mActivityHandler.sendEmptyMessage(Constants.SEND_ATTENDANCE_RECOGNIZED_LOG);

            mLoginAccount = "";

            return;
        }

        mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
        mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.FDR_DELAYED_TIME);

        LOG.D(TAG, "mBtnNextClickListener ClockUtils.mLoginAccount = " + ClockUtils.mLoginAccount);
        //2. in list , do face check
        startFdr();
    }

    public void backToHome() {
        LOG.D(TAG, "backToHome ");
        mTxtLoginAccount.setText("");
        mLoginAccount = "";
        mTxtPinCodeHint.setText(getString(R.string.txt_please_input_password));

        if (mFdrFrame != null) {
            mFdrFrame.removeAllViews();
        }

        FDRControlManager.getInstance(mContext).stopFdr(mActivityHandler, mFragmentHandler);

        if (mLayoutFdr != null) {
            mLayoutFdr.setVisibility(FrameLayout.GONE);
        }

        if (mLayoutPinCode != null) {
            mLayoutPinCode.setVisibility(View.VISIBLE);
        }

    }

    public void clearLoginAccount() {
        mTxtLoginAccount.setText("");
        mLoginAccount = "";

    }

    public void setPinCodeType(int pinCodeType) {
        mPinCodeType = pinCodeType;
        mTxtLoginAccount.setText("");
        mLoginAccount = "";
    }

    public void startFdr() {
        LOG.D(TAG, "startFdr");
        LOG.D(TAG, "startFdr mFdrFrame.getChildCount() = " + mFdrFrame.getChildCount());

//        Calendar calendar = Calendar.getInstance();
//        ClockUtils.mLoginTime = calendar.get(Calendar.SECOND);
        ClockUtils.mLoginTime = System.currentTimeMillis();

        mLayoutPinCode.setVisibility(View.GONE);
        mLayoutFdr.setVisibility(View.VISIBLE);

        mFdrFrame.removeAllViews();
        mFdrFrame.addView(FDRControlManager.getInstance(mContext).getFdrCtrl());
        FDRControlManager.getInstance(mContext).startFdr(mActivityHandler, mFragmentHandler);


    }

    private Handler mFragmentHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LOG.D(TAG, "mHandler msg.what = " + msg.what);
            switch (msg.what) {
                case Constants.GET_FACE_SUCCESS:

                    mFragmentHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
                    mActivityHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
//                    mLayoutFdr.setVisibility(FrameLayout.GONE);
//                    mLayoutPinCode.setVisibility(View.VISIBLE);

                    //show clock dialog
                    break;
                case Constants.GET_FACE_FAIL:

                    mFragmentHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
                    mActivityHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
//                    mLayoutFdr.setVisibility(FrameLayout.GONE);
//                    mLayoutPinCode.setVisibility(View.VISIBLE);
                    //show fail dialog

                    break;
                case Constants.GET_FACE_TOO_LONG:
                    mFdrFrame.removeAllViews();
                    FDRControlManager.getInstance(mContext).stopFdr(mActivityHandler, mFragmentHandler);
                    mFragmentHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
                    mLayoutFdr.setVisibility(FrameLayout.GONE);
                    mLayoutPinCode.setVisibility(View.VISIBLE);
                    mTxtLoginAccount.setText("");
                    mLoginAccount = "";

//                    FDRControlManager.getInstance(mContext).stopFdr(mHandler);
//                    mFragmentHandler.setVisibility(FrameLayout.GONE);
//                    mLayoutPinCode.setVisibility(View.VISIBLE);
                    break;


            }
        }
    };


    private IDeviceVisitorRecordsListener deviceVisitorRecordsListener = new IDeviceVisitorRecordsListener() {
        @Override
        public void onDeviceVisitorRecords(RecordsReplyModel model) {
            LOG.D(TAG, "onDeviceVisitorRecords model = " + model);


        }
    };

    private IDeviceVisitorAccessRecordsListener deviceVisitorAccessRecordsListener = new IDeviceVisitorAccessRecordsListener() {
        @Override
        public void onDeviceVisitorAccessRecords(RecordsReplyModel model) {
            LOG.D(TAG, "onDeviceVisitorAccessRecords model =  " + model);
            mProgress.setVisibility(View.GONE);


            mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mActivityHandler.sendEmptyMessage(Constants.BACK_TO_INDEX_PAGE);

        }
    };


}
