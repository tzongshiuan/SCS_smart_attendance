package com.gorilla.attendance.enterprise.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gorilla.attendance.enterprise.MainActivity;
import com.gorilla.attendance.enterprise.R;
import com.gorilla.attendance.enterprise.datamodel.EmployeeModel;
import com.gorilla.attendance.enterprise.datamodel.VisitorModel;
import com.gorilla.attendance.enterprise.util.ClockUtils;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.DeviceUtils;
import com.gorilla.attendance.enterprise.util.FDRControlManager;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.NetworkManager;
import com.gorilla.attendance.enterprise.util.RFIDScannerManager;

import java.util.Calendar;

import static com.gorilla.attendance.enterprise.util.ClockUtils.mLoginAccount;

/**
 * Created by ggshao on 2017/7/12.
 */

public class RFIDFragment extends BaseFragment {
    public static final String TAG = "RFIDFragment";

    private View mView = null;

    private MainActivity mMainActivity;
    private FragmentActivity mActivity = null;
    private Context mContext = null;

    private NetworkManager mNetworkManager = null;
    private Handler mActivityHandler = null;

    private RelativeLayout mLayoutPinCode = null;
    private RelativeLayout mLayoutFdr = null;
    private FrameLayout mFdrFrame = null;
    private TextView mTxtRfidTitle = null;

//    private TextView mTxtLoginAccount = null;

    private SharedPreferences mSharedPreference = null;

    private int mPinCodeType = -1;

    private ProgressBar mProgress = null;

    private String mRfidNumber = null;
    private int retry;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LOG.D(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mMainActivity = (MainActivity) getActivity();
        mContext = getActivity();
        mActivityHandler = mMainActivity.getHandler();
        mSharedPreference = mContext.getSharedPreferences(Constants.PREF_NAME_ATTENDANCE_ENTERPRISE, Context.MODE_PRIVATE);
        retry = mSharedPreference.getInt(Constants.PREF_KEY_RETRY, 3);
//        Bundle bundle = getArguments();
//        if(bundle != null) {
//            bundle.clear();
//        }else{
//        }


        if (getFragmentManager().getBackStackEntryCount() > 0) {
            mActivityHandler.removeMessages(Constants.LAUNCH_VIDEO);
            mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
//            mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.PAGE_DELAYED_TIME);
            mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.PAGE_DELAYED_TIME);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LOG.V(TAG, "[onCreateView] ");

        if (mView == null) {
            mView = inflater.inflate(R.layout.rfid_fragment, null);
        } else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
        }

//        initView(mView);

        initView();

        RFIDScannerManager.getInstance(mContext).initRFIDManager(mActivityHandler, mFragmentHandler);

        return mView;
    }

    @Override
    public void onPause() {
        LOG.D(TAG, "onPause");
        super.onPause();

    }

    @Override
    public void onResume() {
        LOG.D(TAG, "onResume getFragmentManager().getBackStackEntryCount() = " + getFragmentManager().getBackStackEntryCount());
        super.onResume();

        if (getFragmentManager().getBackStackEntryCount() > 0) {
            mMainActivity.setHomeSettingWord(getString(R.string.txt_home_page));
            mMainActivity.setHomeSettingBackGround(R.mipmap.icon_back_to_home);
        } else {
            mMainActivity.setHomeSettingWord(getString(R.string.txt_home_setting));
            mMainActivity.setHomeSettingBackGround(R.mipmap.icon_back_to_home_setting);
        }

        if (mTxtRfidTitle != null) {
            mTxtRfidTitle.setText(getString(R.string.txt_please_swipe_the_card));
        }
        RFIDScannerManager.getInstance(mContext).start();
        RFIDScannerManager.getInstance(mContext).setEnableReadCard(true);

    }

    @Override
    public void onStop() {
        LOG.D(TAG, "onStop");
        super.onStop();

        RFIDScannerManager.getInstance(mContext).stop();

    }

    @Override
    public void onDestroy() {
        LOG.D(TAG, "onDestroy");
        mFdrFrame.removeAllViews();


        FDRControlManager.getInstance(mContext).stopFdr(mActivityHandler, mFragmentHandler);
        super.onDestroy();
    }

    private void initView() {
        LOG.D(TAG, "initView");

//        mLayoutPinCode = (RelativeLayout) mView.findViewById(R.id.layout_pin_code);
        mLayoutFdr = (RelativeLayout) mView.findViewById(R.id.layout_fdr);
        mFdrFrame = (FrameLayout) mView.findViewById(R.id.fdr_frame);
        mProgress = (ProgressBar) mView.findViewById(R.id.marker_progress);
        mTxtRfidTitle = (TextView) mView.findViewById(R.id.txt_rfid_title);

    }


    public void backToHome() {
        mFdrFrame.removeAllViews();
        FDRControlManager.getInstance(mContext).stopFdr(mActivityHandler, mFragmentHandler);
        mLayoutFdr.setVisibility(FrameLayout.GONE);
        mLayoutPinCode.setVisibility(View.VISIBLE);
//        mTxtLoginAccount.setText("");
        mLoginAccount = "";

    }

    public void launchRfidScan() {

        if (mTxtRfidTitle != null) {
            mTxtRfidTitle.setText(getString(R.string.txt_please_swipe_the_card));
        }

        if (RFIDScannerManager.getInstance(mContext) != null) {
            RFIDScannerManager.getInstance(mContext).start();
            RFIDScannerManager.getInstance(mContext).setEnableReadCard(true);
            mLayoutFdr.setVisibility(FrameLayout.GONE);

        }
    }

    public void startFdr() {
        LOG.D(TAG, "startFdr");
        LOG.D(TAG, "startFdr mFdrFrame.getChildCount() = " + mFdrFrame.getChildCount());

//        Calendar calendar = Calendar.getInstance();
//        ClockUtils.mLoginTime = calendar.get(Calendar.SECOND);
        ClockUtils.mLoginTime = System.currentTimeMillis();

        mLayoutFdr.setVisibility(View.VISIBLE);

        mFdrFrame.removeAllViews();
        mFdrFrame.addView(FDRControlManager.getInstance(mContext).getFdrCtrl());
        FDRControlManager.getInstance(mContext).startFdr(mActivityHandler, mFragmentHandler);

    }

    @SuppressLint("HandlerLeak")
    private Handler mFragmentHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LOG.D(TAG, "mHandler msg.what = " + msg.what);

            Bundle bundle = msg.getData();

            switch (msg.what) {
                case Constants.GET_FACE_SUCCESS:

                    mFragmentHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
                    mActivityHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
//                    mLayoutFdr.setVisibility(FrameLayout.GONE);


                    //show clock dialog
                    break;
                case Constants.GET_FACE_FAIL:

                    mFragmentHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
                    mActivityHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
//                    mLayoutFdr.setVisibility(FrameLayout.GONE);
                    //show fail dialog

                    break;
                case Constants.GET_FACE_TOO_LONG:
                    mFdrFrame.removeAllViews();
                    FDRControlManager.getInstance(mContext).stopFdr(mActivityHandler, mFragmentHandler);
                    mFragmentHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
//                    mLayoutFdr.setVisibility(FrameLayout.GONE);
                    launchRfidScan();
//                    mTxtLoginAccount.setText("");
                    mLoginAccount = "";

//                    FDRControlManager.getInstance(mContext).stopFdr(mHandler);
//                    mFragmentHandler.setVisibility(FrameLayout.GONE);
//                    mLayoutPinCode.setVisibility(View.VISIBLE);
                    break;


                case Constants.MSG_RFID_CARD_READ:

                    mRfidNumber = bundle.getString(Constants.KEY_RFID_CARD_NUMBER);
                    LOG.D(TAG, "mRfidNumber = " + mRfidNumber);

                    mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                    mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.PAGE_DELAYED_TIME);

                    if (mRfidNumber != null) {

                        boolean isRFIDLegal = false;
                        if (ClockUtils.mRoleModel instanceof EmployeeModel) {
                            LOG.D(TAG, "I am Employee");

                            EmployeeModel employeeModel = ((EmployeeModel) ClockUtils.mRoleModel);

                            if (employeeModel.getEmployeeData() != null) {

                                for (int i = 0; i < employeeModel.getEmployeeData().getAcceptances().size(); i++) {
                                    LOG.D(TAG, "employeeModel.getEmployeeData().getAcceptances().get(i).getRfid() = " +
                                            employeeModel.getEmployeeData().getAcceptances().get(i).getRfid());

                                    if (mRfidNumber.equals(employeeModel.getEmployeeData().getAcceptances().get(i).getRfid())) {
                                        isRFIDLegal = true;

                                        ClockUtils.mLoginAccount = employeeModel.getEmployeeData().getAcceptances().get(i).getSecurityCode();
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
                                    LOG.D(TAG, "visitorModel.getVisitorData().getAcceptances().get(i).getRfid() = " +
                                            visitorModel.getVisitorData().getAcceptances().get(i).getRfid());

                                    if (mRfidNumber.equals(visitorModel.getVisitorData().getAcceptances().get(i).getRfid())) {
                                        isRFIDLegal = true;

//                                        mLoginAccount = mLoginAccount;
                                        ClockUtils.mLoginAccount = visitorModel.getVisitorData().getAcceptances().get(i).getSecurityCode();
                                        ClockUtils.mLoginUuid = visitorModel.getVisitorData().getAcceptances().get(i).getUuid();
                                        ClockUtils.mLoginIntId = visitorModel.getVisitorData().getAcceptances().get(i).getIntId();
                                        ClockUtils.mLoginName = visitorModel.getVisitorData().getAcceptances().get(i).getFirstName();
                                        ClockUtils.mStartTime = visitorModel.getVisitorData().getAcceptances().get(i).getStartTime();
                                        ClockUtils.mEndTime = visitorModel.getVisitorData().getAcceptances().get(i).getEndTime();

                                        break;
                                    }
                                }
                            } else {

                            }

                        }

                        LOG.D(TAG, "isRFIDLegal = " + isRFIDLegal);
                        if (isRFIDLegal) {
                            //check if expire, only visitor mStartTime mEndTime != 0
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
                            //wrong message
                            retry--;
                            mMainActivity.setIdentifyResult(getString(R.string.invalid));
                            mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                            //send wrong Api
                            ClockUtils.mLoginAccount = mRfidNumber;
                            ClockUtils.mLiveness = "FAILED";
                            ClockUtils.mLoginTime = System.currentTimeMillis();
                            ++ClockUtils.mSerialNumber;
                            mActivityHandler.sendEmptyMessage(Constants.SEND_ATTENDANCE_RECOGNIZED_LOG);
                            mRfidNumber = null;

                            if (retry == 0) {
                                RFIDScannerManager.getInstance(mContext).setEnableReadCard(false);
                                mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                                mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);
                                return;
                            }

                            mFragmentHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    LOG.D(TAG, "mFragmentHandler.postDelayed");
                                    RFIDScannerManager.getInstance(mContext).start();
                                    RFIDScannerManager.getInstance(mContext).setEnableReadCard(true);
                                    mMainActivity.setIdentifyResult("");

                                }
                            }, DeviceUtils.mResultMessageDelayTime);

                            return;
                        }

                        mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                        mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.FDR_DELAYED_TIME);

                        LOG.D(TAG, "mBtnNextClickListener ClockUtils.mLoginAccount = " + ClockUtils.mLoginAccount);
                        startFdr();

                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.toast_no_result), Toast.LENGTH_SHORT).show();
                    }

                    break;


            }
        }
    };


}
