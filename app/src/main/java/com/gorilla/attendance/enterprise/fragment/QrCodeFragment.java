package com.gorilla.attendance.enterprise.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.gorilla.attendance.enterprise.Dialolg.QrCodeReScanDialog;
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
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.Calendar;
import java.util.List;

import static com.gorilla.attendance.enterprise.util.ClockUtils.mLoginAccount;

/**
 * Created by ggshao on 2018/2/21.
 */

public class QrCodeFragment extends BaseFragment {
    public static final String TAG = "QrCodeFragment";

    private View mView = null;

    private MainActivity mMainActivity;
    private FragmentActivity mActivity = null;
    private Context mContext = null;

    private NetworkManager mNetworkManager = null;
    private Handler mActivityHandler = null;

    private RelativeLayout mLayoutPinCode = null;
    public RelativeLayout mLayoutFdr = null;
    private FrameLayout mFdrFrame = null;
    private TextView mTxtQrcodeTitle = null;

//    private TextView mTxtLoginAccount = null;

    private SharedPreferences mSharedPreference = null;

    private String mQrCode = null;

    private ProgressBar mProgress = null;
    private BeepManager mBeepManager;
    private DecoratedBarcodeView mBarcodeView = null;
    //    private RelativeLayout mLayoutBarcode = null;
    private QrCodeReScanDialog mQrCodeReScanDialog = null;
    private boolean onlyQRcode = false;
    private int retry;
//    private boolean testGG = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LOG.D(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mMainActivity = (MainActivity) getActivity();
        mContext = getActivity();
        mActivityHandler = mMainActivity.getHandler();
        FDRControlManager.getInstance(mContext).releaseCamera(FDRControlManager.CAMERA_ID);

        mSharedPreference = mContext.getSharedPreferences(Constants.PREF_NAME_ATTENDANCE_ENTERPRISE, Context.MODE_PRIVATE);
        retry = mSharedPreference.getInt(Constants.PREF_KEY_RETRY, 3);
//        Bundle bundle = getArguments();
//        if(bundle != null) {
//            bundle.clear();
//        }else{
//        }

        LOG.D(TAG, "getFragmentManager().getBackStackEntryCount() = " + getFragmentManager().getBackStackEntryCount());
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            mActivityHandler.removeMessages(Constants.LAUNCH_VIDEO);
            mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.PAGE_DELAYED_TIME);
//            mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, 2000000);
        }

        mBeepManager = new BeepManager(mMainActivity);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LOG.V(TAG, "[onCreateView] ");

        if (mView == null) {
            mView = inflater.inflate(R.layout.qrcode_fragment, null);
        } else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
        }


        initView();

        return mView;
    }

    @Override
    public void onPause() {
        LOG.D(TAG, "onPause");
        super.onPause();

        mBarcodeView.pauseAndWait();
//        FDRControlManager.getInstance(mContext).releaseCamera(FDRControlManager.CAMERA_ID);


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

//        FDRControlManager.getInstance(mContext).releaseCamera(FDRControlManager.CAMERA_ID);
        LOG.D(TAG, "mBarcodeView.isEnabled()11 = " + mBarcodeView.isEnabled());
        LOG.D(TAG, "mBarcodeView.isActivated()11 = " + mBarcodeView.isActivated());
//        LOG.D(TAG, "mBarcodeView.isEnabled() = " + mBarcodeView.get());
//        LOG.D(TAG, "mBarcodeView.isEnabled() = " + mBarcodeView.isEnabled());
//        LOG.D(TAG, "mBarcodeView.isEnabled() = " + mBarcodeView.isEnabled());

        mBarcodeView.resume();

        LOG.D(TAG, "mBarcodeView.isEnabled()22 = " + mBarcodeView.isEnabled());
        LOG.D(TAG, "mBarcodeView.isActivated()22 = " + mBarcodeView.isActivated());


//        if(mQrCode == null){
//
//            Handler handler=new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    IntentIntegrator.forSupportFragment(QrCodeFragment.this).initiateScan();
//                }
//            }, 3000);
//        }


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
        mFdrFrame.removeAllViews();
        mBarcodeView.pauseAndWait();
        mBarcodeView.setVisibility(View.GONE);
        mTxtQrcodeTitle.setVisibility(View.GONE);


//        FDRControlManager.getInstance(mContext).reInitCamera(FDRControlManager.CAMERA_ID);
        FDRControlManager.getInstance(mContext).stopFdr(mActivityHandler, mFragmentHandler);
        if (mQrCodeReScanDialog != null) {
            mQrCodeReScanDialog.dismiss();
        }


    }

    private void initView() {
        LOG.D(TAG, "initView");

//        mLayoutPinCode = (RelativeLayout) mView.findViewById(R.id.layout_pin_code);
        mLayoutFdr = (RelativeLayout) mView.findViewById(R.id.layout_fdr);
        mFdrFrame = (FrameLayout) mView.findViewById(R.id.fdr_frame);
        mProgress = (ProgressBar) mView.findViewById(R.id.marker_progress);
        mTxtQrcodeTitle = (TextView) mView.findViewById(R.id.txt_qrcode_title);
        mBarcodeView = (DecoratedBarcodeView) mView.findViewById(R.id.barcode_scanner);
//        mLayoutBarcode = (RelativeLayout) mView.findViewById(R.id.layout_barcode_scanner);
//        mBarcodeView.decodeContinuous(mBarcodeViewCallback);

        Intent intent = new Intent();
        intent.putExtra("SCAN_CAMERA_ID", FDRControlManager.CAMERA_ID);
//        intent.putExtra("SCAN_CAMERA_ID", -1);
        LOG.D(TAG, "initView 222 ");
        int cameraId = intent.getIntExtra(Intents.Scan.CAMERA_ID, -1);
        LOG.D(TAG, "initView 222 cameraId   = " + cameraId);
        mBarcodeView.initializeFromIntent(intent);
        mBarcodeView.decodeSingle(mBarcodeViewCallback);


//        mTxtQrcodeTitle.setOnClickListener(mTxtQrcodeTitleClickListener);

    }


    public void backToHome() {
        mFdrFrame.removeAllViews();
        FDRControlManager.getInstance(mContext).stopFdr(mActivityHandler, mFragmentHandler);
        mLayoutFdr.setVisibility(FrameLayout.GONE);
//        mLayoutPinCode.setVisibility(View.VISIBLE);
//        mTxtLoginAccount.setText("");
        mLoginAccount = "";

    }

    public void startFdr() {
        LOG.D(TAG, "startFdr");
        LOG.D(TAG, "startFdr mFdrFrame.getChildCount() = " + mFdrFrame.getChildCount());

//        Calendar calendar = Calendar.getInstance();
//        ClockUtils.mLoginTime = calendar.get(Calendar.SECOND);
        ClockUtils.mLoginTime = System.currentTimeMillis();

        mLayoutFdr.setVisibility(View.VISIBLE);

//        if(mFdrFrame.getChildCount() > 0){
//        }else{
//            mFdrFrame.addView(FDRControlManager.getInstance(mContext).getFdrCtrl());
//        }

        mFdrFrame.removeAllViews();
        mFdrFrame.addView(FDRControlManager.getInstance(mContext).getFdrCtrl());
        FDRControlManager.getInstance(mContext).startFdr(mActivityHandler, mFragmentHandler);

    }

    private TextView.OnClickListener mTxtQrcodeTitleClickListener = new TextView.OnClickListener() {
        public void onClick(View v) {
            LOG.D(TAG, "mImgQrCodeClickListener");
//            new IntentIntegrator(mMainActivity).initiateScan();

            IntentIntegrator.forSupportFragment(QrCodeFragment.this).initiateScan();
        }
    };

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
                    mLayoutFdr.setVisibility(FrameLayout.GONE);
                    mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                    mActivityHandler.sendEmptyMessage(Constants.BACK_TO_INDEX_PAGE);

                    break;

                case Constants.MSG_QRCODE_CANCEL:
                    mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                    mActivityHandler.sendEmptyMessage(Constants.BACK_TO_INDEX_PAGE);
                    break;

                case Constants.MSG_QRCODE_RESCAN:
                    mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                    mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.PAGE_DELAYED_TIME);
                    launchBarcodeScan();
                    mMainActivity.setIdentifyResult("");

                    break;

                case Constants.MSG_QRCODE_SCAN:
                    mQrCode = bundle.getString(Constants.KEY_QRCODE_NUMBER);
                    LOG.D(TAG, "mQrCode = " + mQrCode);

                    mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                    mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.PAGE_DELAYED_TIME);


                    if (mQrCode != null) {

                        boolean isQrCodeLegal = false;
                        //  1. check password if in list
                        if (ClockUtils.mRoleModel instanceof EmployeeModel) {
                            LOG.D(TAG, "I am Employee");

                            EmployeeModel employeeModel = ((EmployeeModel) ClockUtils.mRoleModel);

                            if (employeeModel.getEmployeeData() != null) {
                                for (int i = 0; i < employeeModel.getEmployeeData().getAcceptances().size(); i++) {
                                    if (mQrCode.equals(employeeModel.getEmployeeData().getAcceptances().get(i).getSecurityCode())) {
                                        isQrCodeLegal = true;

                                        ClockUtils.mLoginAccount = mQrCode;
                                        ClockUtils.mLoginIntId = employeeModel.getEmployeeData().getAcceptances().get(i).getIntId();
                                        ClockUtils.mLoginUuid = employeeModel.getEmployeeData().getAcceptances().get(i).getUuid();
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
                                    if (mQrCode.equals(visitorModel.getVisitorData().getAcceptances().get(i).getSecurityCode())) {
                                        isQrCodeLegal = true;

                                        ClockUtils.mLoginAccount = mQrCode;
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

                        LOG.D(TAG, "isQrCodeLegal = " + isQrCodeLegal);
                        if (isQrCodeLegal) {
                            mBarcodeView.pauseAndWait();
                            mBarcodeView.setVisibility(View.GONE);
//                            mLayoutBarcode.setVisibility(View.GONE);
                            mTxtQrcodeTitle.setVisibility(View.GONE);

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
                            //wrong message in footer bar
                            mMainActivity.setIdentifyResult(getString(R.string.invalid));
                            ClockUtils.mLoginAccount = mQrCode;
                            ClockUtils.mLiveness = "FAILED";
                            ClockUtils.mLoginTime = System.currentTimeMillis();
                            ++ClockUtils.mSerialNumber;
                            mActivityHandler.sendEmptyMessage(Constants.SEND_ATTENDANCE_RECOGNIZED_LOG);
                            mQrCode = null;
                            if (ClockUtils.mRoleModel.getModes().length == 1) {
                                mFragmentHandler.sendEmptyMessageDelayed(Constants.MSG_QRCODE_RESCAN, DeviceUtils.mResultMessageDelayTime);
                            }else {
                                retry--;
                                if (retry == 0) {
                                    mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                                    mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.mResultMessageDelayTime);
                                } else if (retry > 0) {
                                    mFragmentHandler.sendEmptyMessageDelayed(Constants.MSG_QRCODE_RESCAN, DeviceUtils.mResultMessageDelayTime);
                                } else {

                                }
                            }
                            return;
                        }

                        mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                        mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.FDR_DELAYED_TIME);


                        startFdr();


                    } else {
                        //ERROR
                        mMainActivity.setIdentifyResult(getString(R.string.invalid));

                        //send wrong Api
                        ClockUtils.mLoginAccount = mQrCode;
                        ClockUtils.mLiveness = "FAILED";
                        ClockUtils.mLoginTime = System.currentTimeMillis();
                        ++ClockUtils.mSerialNumber;
                        mActivityHandler.sendEmptyMessage(Constants.SEND_ATTENDANCE_RECOGNIZED_LOG);

                        mQrCode = null;

                        mFragmentHandler.sendEmptyMessageDelayed(Constants.MSG_QRCODE_RESCAN, DeviceUtils.mResultMessageDelayTime);
//                            launchBarcodeScan();
                    }

                    break;

            }
        }
    };

    private BarcodeCallback mBarcodeViewCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null) {
                // Prevent duplicate scans,

                return;
            }

            LOG.D(TAG, "mBarcodeViewCallback result.getText() = " + result.getText());

//            lastText = result.getText();
//            mBarcodeView.pauseAndWait();
//            mBarcodeView.setVisibility(View.GONE);

            LOG.D(TAG, "mBarcodeViewCallback mBarcodeView.getBarcodeView().isCameraClosed()) = " + mBarcodeView.getBarcodeView().isCameraClosed());


            Message message = new Message();
            Bundle bundle = new Bundle();
            //Test FAke data
//            if(testGG){
//                testGG = false;
//                bundle.putString(Constants.KEY_QRCODE_NUMBER, "0988");
//            }else{
//                testGG = true;
//                bundle.putString(Constants.KEY_QRCODE_NUMBER, result.getText());
//            }

            bundle.putString(Constants.KEY_QRCODE_NUMBER, result.getText());
            message.what = Constants.MSG_QRCODE_SCAN;
            message.setData(bundle);

            mFragmentHandler.sendMessage(message);


        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    public void launchBarcodeScan() {
        LOG.D(TAG, "launchBarcodeScan mBarcodeView = " + mBarcodeView);
        if (mBarcodeView != null) {
            mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.FDR_DELAYED_TIME);
            FDRControlManager.getInstance(mContext).releaseCamera(FDRControlManager.CAMERA_ID);
            mBarcodeView.decodeSingle(mBarcodeViewCallback);
            mBarcodeView.resume();
            mBarcodeView.setVisibility(View.VISIBLE);
        }
    }

    public void initBarcodeView() {
        LOG.D(TAG, "launchBarcodeScan mBarcodeView = " + mBarcodeView);
        if (mBarcodeView != null) {
            if (!onlyQRcode) {
                mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
                mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.FDR_DELAYED_TIME);
            }
            mLayoutFdr.setVisibility(View.GONE);
            FDRControlManager.getInstance(mContext).releaseCamera(FDRControlManager.CAMERA_ID);
            ((ViewGroup) mBarcodeView.getParent()).removeView(mBarcodeView);
            ((ViewGroup) mTxtQrcodeTitle.getParent()).addView(mBarcodeView);
            Intent intent = new Intent();
            intent.putExtra("SCAN_CAMERA_ID", FDRControlManager.CAMERA_ID);
            LOG.D(TAG, "initBarcodeView  ");
            int cameraId = intent.getIntExtra(Intents.Scan.CAMERA_ID, -1);
            LOG.D(TAG, "initBarcodeView  cameraId   = " + cameraId);
            mBarcodeView.initializeFromIntent(intent);
            mBarcodeView.decodeSingle(mBarcodeViewCallback);
            mBarcodeView.resume();
            mBarcodeView.setVisibility(View.VISIBLE);
            mTxtQrcodeTitle.setVisibility(View.VISIBLE);
        }
    }

    public void setOnlyQRcode(boolean b) {
        onlyQRcode = b;
    }
}
