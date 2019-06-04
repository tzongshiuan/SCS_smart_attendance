package com.gorilla.attendance.enterprise.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gorilla.attendance.enterprise.MainActivity;
import com.gorilla.attendance.enterprise.R;
import com.gorilla.attendance.enterprise.database.bean.RegisterBean;
import com.gorilla.attendance.enterprise.datamodel.SearchUserModel;
import com.gorilla.attendance.enterprise.util.ApiAccessor;
import com.gorilla.attendance.enterprise.util.ApiUtils;
import com.gorilla.attendance.enterprise.util.CallbackUtils;
import com.gorilla.attendance.enterprise.util.ClockUtils;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.DeviceUtils;
import com.gorilla.attendance.enterprise.util.FDRControlManager;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.NetworkManager;
import com.gorilla.attendance.enterprise.util.RFIDScannerManager;
import com.gorilla.attendance.enterprise.util.apitask.listener.ISearchUserListener;
import com.gorilla.enroll.lib.util.EnrollConfiguration;
import com.gorilla.enroll.lib.util.EnrollUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.gorilla.attendance.enterprise.util.Constants.FRAGMENT_TAG_REGISTER_VISITOR;

/**
 * Created by ggshao on 2018/1/25.
 */

public class RegisterInputRfidFragment extends BaseFragment {
    public static final String TAG = "RegisterInputRfidFragment";

    public static final String KEY_REGISTER_TYPE = "key-register-type";

    private View mView = null;

    private MainActivity mMainActivity;
    private FragmentActivity mActivity = null;
    private Context mContext = null;

    private NetworkManager mNetworkManager = null;
    private Handler mActivityHandler = null;

    private SharedPreferences mSharedPreference = null;

    private Button mBtnDone = null;

    private int mRegisterType = -1;
    private String mRfidNumber = null;
    private boolean mIsFirstLaunch = true;

    private RegisterEmployeeFragment mRegisterEmployeeFragment = null;
    private RegisterVisitorFragment mRegisterVisitorFragment = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LOG.D(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mMainActivity = (MainActivity) getActivity();
        mContext = getActivity();

        mActivityHandler = mMainActivity.getHandler();
        mSharedPreference = mContext.getSharedPreferences(Constants.PREF_NAME_ATTENDANCE_ENTERPRISE, Context.MODE_PRIVATE);

        if(getFragmentManager().getBackStackEntryCount() > 0){
            mActivityHandler.removeMessages(Constants.LAUNCH_VIDEO);
            mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.SETTING_DETAIL_DELAYED_TIME);
        }

        Bundle bundle = getArguments();
        if(bundle != null) {
            mRegisterType = bundle.getInt(KEY_REGISTER_TYPE);
            bundle.clear();
        }

        LOG.D(TAG, "mRegisterType = " + mRegisterType);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LOG.V(TAG, "[onCreateView] ");

        if (mView == null) {
            mView = inflater.inflate(R.layout.register_input_rfid_fragment, null);
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
        LOG.D(TAG,"onPause");
        super.onPause();

    }

    @Override
    public void onResume(){
        super.onResume();
        LOG.D(TAG,"onResume");

        if(mIsFirstLaunch){
            mIsFirstLaunch = false;
        }else{
            mIsFirstLaunch = true;
            mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mActivityHandler.sendEmptyMessage(Constants.BACK_TO_INDEX_PAGE);
        }

        RFIDScannerManager.getInstance(mContext).start();
        RFIDScannerManager.getInstance(mContext).setEnableReadCard(true);


    }

    @Override
    public void onStop() {
        LOG.D(TAG,"onStop");
        super.onStop();

        RFIDScannerManager.getInstance(mContext).stop();
    }

    @Override
    public void onDestroy() {
        LOG.D(TAG,"onDestroy");
        super.onDestroy();

        mIsFirstLaunch = true;
    }


    private void initView(){
//        mEdtServerIp = (EditText) mView.findViewById(R.id.edt_server_ip);
//        mEdtFtpIp = (EditText) mView.findViewById(R.id.edt_ftp_ip);


//        mRadioGroupClock.setOnCheckedChangeListener(mRadioGroupClockCheckedChangeListener);
//        mRadioButtonClockIn.setOnCheckedChangeListener(mRadioButtonClockInCheckedChangeListener);


    }

//    private Button.OnClickListener mBtnCancelClickListener = new Button.OnClickListener() {
//        public void onClick(View v) {
//            mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
//            mActivityHandler.sendEmptyMessage(Constants.BACK_TO_INDEX_PAGE);
//        }
//    };
//

    @SuppressLint("HandlerLeak")
    private Handler mFragmentHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LOG.D(TAG,"mHandler msg.what = " + msg.what);

            Bundle bundle = msg.getData();

            switch(msg.what) {
//                case Constants.GET_FACE_SUCCESS:
//
//                    mFragmentHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
//                    mLayoutFdr.setVisibility(FrameLayout.GONE);
//                    mLayoutPinCode.setVisibility(View.VISIBLE);
//
//
//                    //show clock dialog
//                    break;
//                case Constants.GET_FACE_FAIL:
//
//                    mFragmentHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
//                    mLayoutFdr.setVisibility(FrameLayout.GONE);
//                    mLayoutPinCode.setVisibility(View.VISIBLE);
//                    //show fail dialog
//
//                    break;
//                case Constants.GET_FACE_TOO_LONG:
//                    mFdrFrame.removeAllViews();
//                    FDRControlManager.getInstance(mContext).stopFdr(mActivityHandler, mFragmentHandler);
//                    mFragmentHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
//                    mLayoutFdr.setVisibility(FrameLayout.GONE);
//                    mLayoutPinCode.setVisibility(View.VISIBLE);
//                    mTxtLoginAccount.setText("");
//                    mLoginAccount = "";
//
////                    FDRControlManager.getInstance(mContext).stopFdr(mHandler);
////                    mFragmentHandler.setVisibility(FrameLayout.GONE);
////                    mLayoutPinCode.setVisibility(View.VISIBLE);
//                    break;


                case Constants.MSG_RFID_CARD_READ:

                    mRfidNumber = bundle.getString(Constants.KEY_RFID_CARD_NUMBER);
                    LOG.D(TAG,"mRfidNumber = " + mRfidNumber);

                    if(mRfidNumber != null){

                        if(mRegisterType == Constants.REGISTER_TYPE_EMPLOYEE){
                            ApiUtils.searchUser(TAG, mContext, DeviceUtils.mDeviceName, "EMPLOYEE", null, mRfidNumber, searchUserListener);
                        }else{
                            ApiUtils.searchUser(TAG, mContext, DeviceUtils.mDeviceName, "VISITOR", null, mRfidNumber, searchUserListener);
                        }


                    }

                    break;


            }
        }
    };

    private void launchFragment(int tag, boolean removeChooseList, String rfidNumber) {
        LOG.V(TAG, "[launchFragment] tag = " + tag);


        FragmentManager fm = getFragmentManager();
        //FragmentManager fm = getChildFragmentManager();
        if(fm == null) {
            LOG.W(TAG, "[launchFragment] FragmentManager is null.");
            return;
        }

        FragmentTransaction ft = fm.beginTransaction();
        if(ft == null) {
            LOG.W(TAG, "[launchFragment] FragmentTransaction is null.");
            return;
        }

        Bundle arguments= new Bundle();

        switch (tag) {

            case Constants.FRAGMENT_TAG_REGISTER_EMPLOYEE:

                mRegisterEmployeeFragment = new RegisterEmployeeFragment();
                arguments.putString(RegisterEmployeeFragment.KEY_RFID_NUMBER, rfidNumber);
                mRegisterEmployeeFragment.setArguments(arguments);

                ft.replace(Constants.CONTENT_FRAME_ID, mRegisterEmployeeFragment, RegisterEmployeeFragment.TAG)
                        .addToBackStack(RegisterEmployeeFragment.TAG).commitAllowingStateLoss();
                break;

            case Constants.FRAGMENT_TAG_REGISTER_VISITOR:

                mRegisterVisitorFragment = new RegisterVisitorFragment();
                arguments.putString(RegisterVisitorFragment.KEY_RFID_NUMBER, rfidNumber);
                mRegisterVisitorFragment.setArguments(arguments);

                ft.replace(Constants.CONTENT_FRAME_ID, mRegisterVisitorFragment, RegisterVisitorFragment.TAG)
                        .addToBackStack(RegisterVisitorFragment.TAG).commitAllowingStateLoss();
                break;



        }

    }

    private ISearchUserListener searchUserListener = new ISearchUserListener() {
        @Override
        public void onSearchUser(SearchUserModel model) {
            LOG.D(TAG, "onSearchUser model = " + model);
            if(model != null){
                if(model.getStatus().equals(Constants.STATUS_SUCCESS)){



                    //find people, only do faceEnroll
                    if(mRegisterType == Constants.REGISTER_TYPE_EMPLOYEE){

                        boolean isClockInThisDeviceEmployee = false;

                        if(DeviceUtils.mEmployeeModel != null){
                            if(DeviceUtils.mEmployeeModel.getEmployeeData() != null){
                                for(int i = 0 ; i < DeviceUtils.mEmployeeModel.getEmployeeData().getAcceptances().size() ; i++){
                                    if(model.getMemberData().getEmployeeId().equals(DeviceUtils.mEmployeeModel.getEmployeeData().getAcceptances().get(i).getEmployeeId())){
                                        isClockInThisDeviceEmployee = true;
                                        break;
                                    }
                                }
                            }

                        }else{

                        }


                        LOG.D(TAG, "isClockInThisDeviceEmployee = " + isClockInThisDeviceEmployee);

                        if(isClockInThisDeviceEmployee){

                        }else{
                            //show can't clock here
                            Message message = new Message();
                            Bundle bundle = new Bundle();

                            bundle.putString(Constants.KEY_REGISTER_MESSAGE, getString(R.string.can_not_clock_here_employee));
                            bundle.putString(Constants.KEY_REGISTER_ID_TITLE, null);
                            bundle.putString(Constants.KEY_REGISTER_ID, null);
                            bundle.putString(Constants.KEY_REGISTER_NAME_TITLE, null);
                            bundle.putString(Constants.KEY_REGISTER_NAME, null);
                            bundle.putString(Constants.KEY_REGISTER_DELAY_TIME, "0");

                            message.setData(bundle);
                            message.what = Constants.MSG_SHOW_REGISTER_DIALOG;

                            mActivityHandler.sendMessage(message);
                            return;
                        }


                        EnrollConfiguration enrollConfiguration = new EnrollConfiguration.Builder(mContext)
                                .setFDRControl(FDRControlManager.getInstance(mContext).getFdrCtrl())
                                .setIP(ApiAccessor.SERVER_IP)
                                .setDeviceToken(DeviceUtils.mDeviceName)
                                .setOnEnrollListener(CallbackUtils.onEnrollListener)
                                .setOnVerifyListener(CallbackUtils.onVerifyListener)
                                .set800PoundGorillaVisible(true)
                                .build();


                        ClockUtils.mRegisterBean = new RegisterBean();
                        ClockUtils.mRegisterBean.setDeviceToken(DeviceUtils.mDeviceName);
//                    ClockUtils.mRegisterBean.setEmployeeId(model.getMemberData().ge());
                        ClockUtils.mRegisterBean.setEmployeeId(model.getMemberData().getEmployeeId());
                        ClockUtils.mRegisterBean.setName(model.getMemberData().getFirstName() + " " + model.getMemberData().getLastName());

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                        String formattedDate = sdf.format(c.getTime());


                        ClockUtils.mRegisterBean.setCreateTime(formattedDate);
                        ClockUtils.mRegisterBean.setFormat("png");
                        ClockUtils.mRegisterBean.setMobileNo(model.getMemberData().getMobileNo());

                        if(model.getMemberData().getEmployeeId() != null){
                            ClockUtils.mRegisterBean.setRegisterType(Constants.REGISTER_TYPE_EMPLOYEE);
                        }else if(model.getMemberData().getMobileNo() != null){
                            ClockUtils.mRegisterBean.setRegisterType(Constants.REGISTER_TYPE_VISITOR);
                        }
                        ClockUtils.mRegisterBean.setIsSearchUserSuccess(true);

                        int registerModelId = mSharedPreference.getInt(Constants.PREF_KEY_REGISTER_MODEL_ID, Constants.REGISTER_MODEL_ID_INIT);
                        ClockUtils.mRegisterBean.setModelId(registerModelId);

                        //callback no security code info, use employeeId
                        ClockUtils.mRegisterBean.setSecurityCode(model.getMemberData().getEmployeeId());

                        //SAVE RFID in callback
//                        ClockUtils.mRegisterBean.setRfid(model.getMemberData().get);

                        ++registerModelId;
                        mSharedPreference.edit().putInt(Constants.PREF_KEY_REGISTER_MODEL_ID, registerModelId).apply();

                        LOG.D(TAG,"onSearchUser model.getMemberData().getId() = " + model.getMemberData().getId());
//                    EnrollUtil.startEnrollActivity(mContext, enrollConfiguration);
//                    EnrollUtil.startEnrollActivityDirectly(mContext, model.getUsers().getMemberData().getId(), true, enrollConfiguration);

                        if(model.getMemberData().getEmployeeId() != null){
                            EnrollUtil.startEnrollActivityDirectly(mContext, model.getMemberData().getId(), false, enrollConfiguration);
                        }else if(model.getMemberData().getMobileNo() != null){
                            EnrollUtil.startEnrollActivityDirectly(mContext, model.getMemberData().getId(), true, enrollConfiguration);
                        }


                    }else if(mRegisterType == Constants.REGISTER_TYPE_VISITOR){

                        boolean isClockInThisDeviceVisitor = false;
                        if(DeviceUtils.mVisitorModel != null){
                            if(DeviceUtils.mVisitorModel.getVisitorData() != null){
                                for(int i = 0 ; i < DeviceUtils.mVisitorModel.getVisitorData().getAcceptances().size() ; i++){
                                    if(model.getMemberData().getMobileNo().equals(DeviceUtils.mVisitorModel.getVisitorData().getAcceptances().get(i).getSecurityCode())){
                                        isClockInThisDeviceVisitor = true;
                                        break;
                                    }
                                }
                            }

                        }else{

                        }

                        if(isClockInThisDeviceVisitor){

                        }else{
                            Message message = new Message();
                            Bundle bundle = new Bundle();

                            bundle.putString(Constants.KEY_REGISTER_MESSAGE, getString(R.string.can_not_clock_here_visitor));
                            bundle.putString(Constants.KEY_REGISTER_ID_TITLE, null);
                            bundle.putString(Constants.KEY_REGISTER_ID, null);
                            bundle.putString(Constants.KEY_REGISTER_NAME_TITLE, null);
                            bundle.putString(Constants.KEY_REGISTER_NAME, null);
                            bundle.putString(Constants.KEY_REGISTER_DELAY_TIME, "0");

                            message.setData(bundle);
                            message.what = Constants.MSG_SHOW_REGISTER_DIALOG;

                            mActivityHandler.sendMessage(message);
                            return;
                        }



                        EnrollConfiguration enrollConfiguration = new EnrollConfiguration.Builder(mContext)
                                .setFDRControl(FDRControlManager.getInstance(mContext).getFdrCtrl())
                                .setIP(ApiAccessor.SERVER_IP)
                                .setDeviceToken(DeviceUtils.mDeviceName)
                                .setOnEnrollListener(CallbackUtils.onEnrollListener)
                                .setOnVerifyListener(CallbackUtils.onVerifyListener)
                                .set800PoundGorillaVisible(true)
                                .build();


                        ClockUtils.mRegisterBean = new RegisterBean();
                        ClockUtils.mRegisterBean.setDeviceToken(DeviceUtils.mDeviceName);
//                    ClockUtils.mRegisterBean.setEmployeeId(model.getMemberData().ge());
                        ClockUtils.mRegisterBean.setEmployeeId(model.getMemberData().getEmployeeId());
                        ClockUtils.mRegisterBean.setName(model.getMemberData().getFirstName() + " " + model.getMemberData().getLastName());

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                        String formattedDate = sdf.format(c.getTime());


                        ClockUtils.mRegisterBean.setCreateTime(formattedDate);
                        ClockUtils.mRegisterBean.setFormat("png");
                        ClockUtils.mRegisterBean.setMobileNo(model.getMemberData().getMobileNo());

                        if(model.getMemberData().getEmployeeId() != null){
                            ClockUtils.mRegisterBean.setRegisterType(Constants.REGISTER_TYPE_EMPLOYEE);
                        }else if(model.getMemberData().getMobileNo() != null){
                            ClockUtils.mRegisterBean.setRegisterType(Constants.REGISTER_TYPE_VISITOR);
                        }
                        ClockUtils.mRegisterBean.setIsSearchUserSuccess(true);

                        int registerModelId = mSharedPreference.getInt(Constants.PREF_KEY_REGISTER_MODEL_ID, Constants.REGISTER_MODEL_ID_INIT);
                        ClockUtils.mRegisterBean.setModelId(registerModelId);
                        //no callback securityCode GGGGGGGGGGGGG, use mobileNo
                        ClockUtils.mRegisterBean.setSecurityCode(model.getMemberData().getMobileNo());


                        ++registerModelId;
                        mSharedPreference.edit().putInt(Constants.PREF_KEY_REGISTER_MODEL_ID, registerModelId).apply();

                        LOG.D(TAG,"onSearchUser model.getMemberData().getId() = " + model.getMemberData().getId());
//                    EnrollUtil.startEnrollActivity(mContext, enrollConfiguration);
//                    EnrollUtil.startEnrollActivityDirectly(mContext, model.getUsers().getMemberData().getId(), true, enrollConfiguration);

                        if(model.getMemberData().getEmployeeId() != null){
                            EnrollUtil.startEnrollActivityDirectly(mContext, model.getMemberData().getId(), false, enrollConfiguration);
                        }else if(model.getMemberData().getMobileNo() != null){
                            EnrollUtil.startEnrollActivityDirectly(mContext, model.getMemberData().getId(), true, enrollConfiguration);
                        }


                    }


                }else{
                    if(mRegisterType == Constants.REGISTER_TYPE_EMPLOYEE){
                        launchFragment(Constants.FRAGMENT_TAG_REGISTER_EMPLOYEE, false, mRfidNumber);
                    }else if(mRegisterType == Constants.REGISTER_TYPE_VISITOR){
                        launchFragment(FRAGMENT_TAG_REGISTER_VISITOR, false, mRfidNumber);
                    }
                }

            }else{

            }
        }
    };



}
