package com.gorilla.attendance.enterprise.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.gorilla.attendance.enterprise.util.EnrollmentManager;
import com.gorilla.attendance.enterprise.util.FDRControlManager;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.NetworkManager;
import com.gorilla.attendance.enterprise.util.apitask.listener.ISearchEmployeeListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.ISearchUserListener;
import com.gorilla.attendance.enterprise.util.apitask.listener.ISearchVisitorListener;
import com.gorilla.enroll.lib.listener.OnEnrollListener;
import com.gorilla.enroll.lib.listener.OnVerifyListener;
import com.gorilla.enroll.lib.util.EnrollConfiguration;
import com.gorilla.enroll.lib.util.EnrollUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

//import com.gorilla.enroll.lib.util.EnrollConfiguration;
//import com.gorilla.enroll.lib.util.EnrollUtil;

//import com.gorilla.enroll.lib.util.EnrollConfiguration;
//import com.gorilla.enroll.lib.util.EnrollUtil;

/**
 * Created by ggshao on 2017/5/3.
 */

public class RegisterInputIdFragment extends BaseFragment {
    public static final String TAG = "RegisterInputIdFragment";

    public static final String KEY_REGISTER_TYPE = "key-register-type";

    private static final int ENROLL_DONE = 1;
    private static final int ENROLL_FAIL = 2;



    private View mView = null;

    private MainActivity mMainActivity;
    private FragmentActivity mActivity = null;
    private Context mContext = null;

    private NetworkManager mNetworkManager = null;
    private Handler mActivityHandler = null;

    private SharedPreferences mSharedPreference = null;

    private int mRegisterType = -1;

    private TextView mTxtRegisterInputIdTitle = null;
    private TextView mTxtInputId = null;
    private EditText mEdtInputId = null;

    private Button mBtnDone = null;
    private Button mBtnCancel = null;

    private RegisterEmployeeFragment mRegisterEmployeeFragment = null;
    private RegisterVisitorFragment mRegisterVisitorFragment = null;

    private String mRegisterInputId = null;

    private ProgressBar mProgress = null;

    private boolean mIsFirstLaunch = true;

    private Handler mHandler = null;
    private Activity mEnrollActivity = null;
    private byte[] mModel = null;
    private ArrayList<byte[]> mArrayList = null;
    private  boolean mIsEnrollSuccess = false;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LOG.V(TAG, "[onCreateView] ");

        if (mView == null) {
            mView = inflater.inflate(R.layout.register_input_id_fragment, null);
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
        LOG.D(TAG,"onPause");
        super.onPause();

    }

    @Override
    public void onResume(){
        super.onResume();
        LOG.D(TAG,"onResume mRegisterType = " + mRegisterType);


        if(mIsFirstLaunch){
            mIsFirstLaunch = false;
        }else{
            mIsFirstLaunch = true;
            mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mActivityHandler.sendEmptyMessage(Constants.BACK_TO_INDEX_PAGE);
        }

        if(mRegisterType == Constants.REGISTER_TYPE_EMPLOYEE){
            mTxtRegisterInputIdTitle.setText(getString(R.string.txt_register_input_id));
            mTxtInputId.setText(getString(R.string.txt_security_code));
            mEdtInputId.setHint(getString(R.string.txt_register_input_id));

            mEdtInputId.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        }else if(mRegisterType == Constants.REGISTER_TYPE_VISITOR){
            mTxtRegisterInputIdTitle.setText(getString(R.string.txt_register_input_phone_number));
            mTxtInputId.setText(getString(R.string.txt_phone_number));
            mEdtInputId.setHint(getString(R.string.txt_register_input_phone_number));
            mEdtInputId.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

        }

    }

    @Override
    public void onStop() {
        LOG.D(TAG,"onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        LOG.D(TAG,"onDestroy");
        super.onDestroy();

        mIsFirstLaunch = true;
        mModel = null;
        mArrayList = null;


    }


    @SuppressLint("HandlerLeak")
    private void initView(){
        mTxtRegisterInputIdTitle = (TextView) mView.findViewById(R.id.txt_register_input_id_title);
        mTxtInputId = (TextView) mView.findViewById(R.id.txt_input_id);
        mEdtInputId = (EditText) mView.findViewById(R.id.edt_input_id);
//        mEdtFtpIp = (EditText) mView.findViewById(R.id.edt_ftp_ip);
//        mEdtWsIp = (EditText) mView.findViewById(R.id.edt_ws_ip);

        mBtnDone = (Button) mView.findViewById(R.id.btn_conf_done);
        mBtnCancel = (Button) mView.findViewById(R.id.btn_conf_cancel);

        mProgress = (ProgressBar) mView.findViewById(R.id.marker_progress);

        mBtnDone.setOnClickListener(mBtnDoneClickListener);
        mBtnCancel.setOnClickListener(mBtnCancelClickListener);


        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                LOG.D(TAG,"msg.what = " + msg.what);
                switch(msg.what){
                    case ENROLL_DONE:
                        LOG.D(TAG,"msg.arg1 = " + msg.arg1);
                        LOG.D(TAG,"mIsEnrollSuccess = " + mIsEnrollSuccess);

                        //not in this page
                        if(mArrayList == null){
                            return;
                        }


                        if(mModel != null){ // != null
                            ClockUtils.mRegisterBean.setModel(mModel);
                            ClockUtils.mRegisterBean.setDataInBase64(mArrayList.get(0));
                        }else{// == null
                            Message message = new Message();
                            Bundle bundle = new Bundle();

                            bundle.putString(Constants.KEY_REGISTER_MESSAGE, getString(R.string.register_fail_again));
                            bundle.putString(Constants.KEY_REGISTER_ID_TITLE, null);
                            bundle.putString(Constants.KEY_REGISTER_ID, null);
                            bundle.putString(Constants.KEY_REGISTER_NAME_TITLE, null);
                            bundle.putString(Constants.KEY_REGISTER_NAME, null);

                            message.setData(bundle);
                            message.what = Constants.MSG_SHOW_REGISTER_DIALOG;

                            mActivityHandler.sendMessage(message);

                            mEnrollActivity.finish();
                            return;
                        }

                        mEnrollActivity.finish();

                        if(mIsEnrollSuccess) {
                            //never here
                        }else{
                            LOG.D(TAG,"mRegisterInputId = " + mRegisterInputId);
                            //must here
                            //Enroll fail, get face and jump to register page
                            if(ClockUtils.mRegisterBean.getRegisterType() == Constants.REGISTER_TYPE_EMPLOYEE){
                                launchFragment(Constants.FRAGMENT_TAG_REGISTER_EMPLOYEE, false, mRegisterInputId);
                            } else if(ClockUtils.mRegisterBean.getRegisterType() == Constants.REGISTER_TYPE_VISITOR){
                                launchFragment(Constants.FRAGMENT_TAG_REGISTER_VISITOR, false, mRegisterInputId);
                            }


                        }

                        break;
                }
                super.handleMessage(msg);
            }
        };


    }

    private Button.OnClickListener mBtnDoneClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
//            launchFragment(Constants.FRAGMENT_TAG_CONF_END_POINT_SETTING, false);

            //check input employeeId is in list

            boolean isInputIdInList = false;
            String uuId = null;


            mRegisterInputId = mEdtInputId.getText().toString();

            if(mRegisterInputId.isEmpty()){
                //toast
                Toast.makeText(mContext, getString(R.string.register_empty), Toast.LENGTH_SHORT).show();
                return;
            }


            mProgress.setVisibility(View.VISIBLE);
            if(mRegisterType == Constants.REGISTER_TYPE_EMPLOYEE){

                //check id from server api
//                ApiUtils.searchUser(TAG, mContext, DeviceUtils.mDeviceName, "EMPLOYEE", mEdtInputId.getText().toString(), null, searchUserListener);
                ApiUtils.searchEmployee(TAG, mContext, DeviceUtils.mDeviceName, mEdtInputId.getText().toString(), searchEmployeeListener);



            }else if(mRegisterType == Constants.REGISTER_TYPE_VISITOR){


//                ApiUtils.searchUser(TAG, mContext, DeviceUtils.mDeviceName, "VISITOR", mEdtInputId.getText().toString(), null, searchUserListener);

                ApiUtils.searchVisitor(TAG, mContext, DeviceUtils.mDeviceName, mEdtInputId.getText().toString(), searchVisitorListener);

//                launchFragment(Constants.FRAGMENT_TAG_REGISTER_VISITOR, false);

            }

        }
    };

    private Button.OnClickListener mBtnCancelClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            mMainActivity.onBackPressed();
        }
    };


    private void launchFragment(int tag, boolean removeChooseList, String inputId) {
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

                arguments.putString(RegisterEmployeeFragment.KEY_SECURITY_CODE, inputId);
                mRegisterEmployeeFragment.setArguments(arguments);

                ft.replace(Constants.CONTENT_FRAME_ID, mRegisterEmployeeFragment, RegisterEmployeeFragment.TAG).addToBackStack(RegisterEmployeeFragment.TAG).commitAllowingStateLoss();
                break;

            case Constants.FRAGMENT_TAG_REGISTER_VISITOR:

                mRegisterVisitorFragment = new RegisterVisitorFragment();

                arguments.putString(RegisterVisitorFragment.KEY_VISITOR_ID, inputId);
                mRegisterVisitorFragment.setArguments(arguments);

                ft.replace(Constants.CONTENT_FRAME_ID, mRegisterVisitorFragment, RegisterVisitorFragment.TAG).addToBackStack(RegisterVisitorFragment.TAG).commitAllowingStateLoss();
                break;


        }

    }

    private ISearchEmployeeListener searchEmployeeListener = new ISearchEmployeeListener() {
        @Override
        public void onSearchEmployee(SearchUserModel model) {
            LOG.D(TAG,"onSearchEmployee model = " + model);

            mProgress.setVisibility(View.GONE);
            if(model != null){
                if(model.getStatus().equals(Constants.STATUS_SUCCESS)){

                    //check if exist in this device
                    boolean isClockInThisDeviceEmployee = false;

                    if(DeviceUtils.mEmployeeModel != null){
                        if(DeviceUtils.mEmployeeModel.getEmployeeData() != null){
                            for(int i = 0 ; i < DeviceUtils.mEmployeeModel.getEmployeeData().getAcceptances().size() ; i++){
                                if(mRegisterInputId.equals(DeviceUtils.mEmployeeModel.getEmployeeData().getAcceptances().get(i).getEmployeeId())){
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
                    ClockUtils.mRegisterBean.setSecurityCode(mRegisterInputId);


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

//                    EnrollUtil.startEnrollActivityDirectly(mContext, model.getMemberData().getId(), true, enrollConfiguration);


                }else{
                    //change to input page first

                    launchFragment(Constants.FRAGMENT_TAG_REGISTER_EMPLOYEE, false, mRegisterInputId);






//                    EnrollConfiguration enrollConfiguration = new EnrollConfiguration.Builder(mContext)
//                            .setFDRControl(FDRControlManager.getInstance(mContext).getFdrCtrl())
//                            .setIP(ApiAccessor.SERVER_IP)
//                            .setDeviceToken(DeviceUtils.mDeviceName)
//                            .setOnEnrollListener(onEnrollListener)
//                            .setOnVerifyListener(onVerifyListener)
//                            .set800PoundGorillaVisible(true)
//                            .build();
//
//                    //do Register, enroll first
//                    ClockUtils.mRegisterBean = new RegisterBean();
//                    ClockUtils.mRegisterBean.setDeviceToken(DeviceUtils.mDeviceName);
//
//                    Calendar c = Calendar.getInstance();
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//                    String formattedDate = sdf.format(c.getTime());
//
//
//                    ClockUtils.mRegisterBean.setCreateTime(formattedDate);
//                    ClockUtils.mRegisterBean.setFormat("png");
//
//                    LOG.D(TAG,"mRegisterType = " + mRegisterType);
//                    ClockUtils.mRegisterBean.setRegisterType(mRegisterType);
//                    ClockUtils.mRegisterBean.setIsSearchUserSuccess(false);
//
//                    int registerModelId = mSharedPreference.getInt(Constants.PREF_KEY_REGISTER_MODEL_ID, Constants.REGISTER_MODEL_ID_INIT);
//                    ClockUtils.mRegisterBean.setModelId(registerModelId);
//
////                if(mRegisterType)
//
//                    ClockUtils.mRegisterBean.setSecurityCode(mRegisterInputId);
////                ClockUtils.mRegisterBean.setMobileNo();
//
//
//                    ++registerModelId;
//                    mSharedPreference.edit().putInt(Constants.PREF_KEY_REGISTER_MODEL_ID, registerModelId).apply();
//
//                    if(mRegisterType == Constants.REGISTER_TYPE_EMPLOYEE){
//                        EnrollUtil.startEnrollActivityDirectly(mContext, null, false, enrollConfiguration);
//                    }else if(mRegisterType == Constants.REGISTER_TYPE_VISITOR){
//                        EnrollUtil.startEnrollActivityDirectly(mContext, null, true, enrollConfiguration);
//                    }


                }
            }else{
                //API FAILm show dialog

                Message message = new Message();
                Bundle bundle = new Bundle();

                bundle.putString(Constants.KEY_REGISTER_MESSAGE, getString(R.string.register_fail_again));
                bundle.putString(Constants.KEY_REGISTER_ID_TITLE, null);
                bundle.putString(Constants.KEY_REGISTER_ID, null);
                bundle.putString(Constants.KEY_REGISTER_NAME_TITLE, null);
                bundle.putString(Constants.KEY_REGISTER_NAME, null);

                message.setData(bundle);
                message.what = Constants.MSG_SHOW_REGISTER_DIALOG;

                mActivityHandler.sendMessage(message);

            }
        }
    };

    private ISearchVisitorListener searchVisitorListener = new ISearchVisitorListener() {
        @Override
        public void onSearchVisitor(SearchUserModel model) {
            LOG.D(TAG,"onSearchVisitor model = " + model);
            mProgress.setVisibility(View.GONE);


            if(model != null){
                if(model.getStatus().equals(Constants.STATUS_SUCCESS)){


                    boolean isClockInThisDeviceVisitor = false;
                    if(DeviceUtils.mVisitorModel != null){
                        if(DeviceUtils.mVisitorModel.getVisitorData() != null){
                            for(int i = 0 ; i < DeviceUtils.mVisitorModel.getVisitorData().getAcceptances().size() ; i++){
                                if(mRegisterInputId.equals(DeviceUtils.mVisitorModel.getVisitorData().getAcceptances().get(i).getSecurityCode())){
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
                    ClockUtils.mRegisterBean.setSecurityCode(mRegisterInputId);


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

//                    EnrollUtil.startEnrollActivityDirectly(mContext, model.getMemberData().getId(), true, enrollConfiguration);


                }else{
//do Register

                    //launch first

                    launchFragment(Constants.FRAGMENT_TAG_REGISTER_VISITOR, false, mRegisterInputId);


                }
            }else{
                //API FAILm show dialog

                Message message = new Message();
                Bundle bundle = new Bundle();

                bundle.putString(Constants.KEY_REGISTER_MESSAGE, getString(R.string.register_fail_again));
                bundle.putString(Constants.KEY_REGISTER_ID_TITLE, null);
                bundle.putString(Constants.KEY_REGISTER_ID, null);
                bundle.putString(Constants.KEY_REGISTER_NAME_TITLE, null);
                bundle.putString(Constants.KEY_REGISTER_NAME, null);

                message.setData(bundle);
                message.what = Constants.MSG_SHOW_REGISTER_DIALOG;

                mActivityHandler.sendMessage(message);

            }
        }
    };


    private ISearchUserListener searchUserListener = new ISearchUserListener() {
        @Override
        public void onSearchUser(SearchUserModel model) {
            LOG.D(TAG,"onSearchUser model = " + model);
            mProgress.setVisibility(View.GONE);
            //GGGG Test
//            model = null;

            if(model != null){
                if(model.getStatus().equals(Constants.STATUS_SUCCESS)){

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
                    ClockUtils.mRegisterBean.setSecurityCode(mRegisterInputId);


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

//                    EnrollUtil.startEnrollActivityDirectly(mContext, model.getMemberData().getId(), true, enrollConfiguration);


                }else{
//do Register
                    EnrollConfiguration enrollConfiguration = new EnrollConfiguration.Builder(mContext)
                            .setFDRControl(FDRControlManager.getInstance(mContext).getFdrCtrl())
                            .setIP(ApiAccessor.SERVER_IP)
                            .setDeviceToken(DeviceUtils.mDeviceName)
                            .setOnEnrollListener(onEnrollListener)
                            .setOnVerifyListener(onVerifyListener)
                            .set800PoundGorillaVisible(true)
                            .build();

                    //do Register, enroll first
                    ClockUtils.mRegisterBean = new RegisterBean();
                    ClockUtils.mRegisterBean.setDeviceToken(DeviceUtils.mDeviceName);

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                    String formattedDate = sdf.format(c.getTime());


                    ClockUtils.mRegisterBean.setCreateTime(formattedDate);
                    ClockUtils.mRegisterBean.setFormat("png");

//                if(model.getMemberData().getEmployeeId() != null){
//                    ClockUtils.mRegisterBean.setRegisterType(mRegisterType);
//                }else if(model.getMemberData().getMobileNo() != null){
//                    ClockUtils.mRegisterBean.setRegisterType(Constants.REGISTER_TYPE_VISITOR);
//                }

                    LOG.D(TAG,"mRegisterType = " + mRegisterType);
                    ClockUtils.mRegisterBean.setRegisterType(mRegisterType);
                    ClockUtils.mRegisterBean.setIsSearchUserSuccess(false);

                    int registerModelId = mSharedPreference.getInt(Constants.PREF_KEY_REGISTER_MODEL_ID, Constants.REGISTER_MODEL_ID_INIT);
                    ClockUtils.mRegisterBean.setModelId(registerModelId);

//                if(mRegisterType)

                    ClockUtils.mRegisterBean.setSecurityCode(mRegisterInputId);
//                ClockUtils.mRegisterBean.setMobileNo();


                    ++registerModelId;
                    mSharedPreference.edit().putInt(Constants.PREF_KEY_REGISTER_MODEL_ID, registerModelId).apply();

                    if(mRegisterType == Constants.REGISTER_TYPE_EMPLOYEE){
                        EnrollUtil.startEnrollActivityDirectly(mContext, null, false, enrollConfiguration);
                    }else if(mRegisterType == Constants.REGISTER_TYPE_VISITOR){
                        EnrollUtil.startEnrollActivityDirectly(mContext, null, true, enrollConfiguration);
                    }


                }
            }else{
                //API FAILm show dialog

                Message message = new Message();
                Bundle bundle = new Bundle();

                bundle.putString(Constants.KEY_REGISTER_MESSAGE, getString(R.string.register_fail_again));
                bundle.putString(Constants.KEY_REGISTER_ID_TITLE, null);
                bundle.putString(Constants.KEY_REGISTER_ID, null);
                bundle.putString(Constants.KEY_REGISTER_NAME_TITLE, null);
                bundle.putString(Constants.KEY_REGISTER_NAME, null);

                message.setData(bundle);
                message.what = Constants.MSG_SHOW_REGISTER_DIALOG;

                mActivityHandler.sendMessage(message);

            }
        }
    };


    private OnEnrollListener onEnrollListener = new OnEnrollListener() {
        @Override
        public boolean onEnroll(Activity activity, boolean isEnrollSuccess, String imageFormat, ArrayList<byte[]> arrayList) {
            if(arrayList != null){
                LOG.D(TAG,"onEnroll arrayList.size() = " + arrayList.size());
                mEnrollActivity = activity;
                mArrayList = arrayList;
                mIsEnrollSuccess = isEnrollSuccess;

                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        mModel = EnrollmentManager.getInstance(mContext).trainModel(mArrayList);
                        LOG.D(TAG,"onEnroll mModel = " + mModel);


                        Message msg = new Message();
                        msg.what = ENROLL_DONE;
                        mHandler.sendMessage(msg);
                    }
                }).start();


            }else{
                //return train fail. try again, Enroll no data
                Message message = new Message();
                Bundle bundle = new Bundle();

                bundle.putString(Constants.KEY_REGISTER_MESSAGE, getString(R.string.register_fail_again));
                bundle.putString(Constants.KEY_REGISTER_ID_TITLE, null);
                bundle.putString(Constants.KEY_REGISTER_ID, null);
                bundle.putString(Constants.KEY_REGISTER_NAME_TITLE, null);
                bundle.putString(Constants.KEY_REGISTER_NAME, null);

                message.setData(bundle);
                message.what = Constants.MSG_SHOW_REGISTER_DIALOG;

                mActivityHandler.sendMessage(message);

                activity.finish();
                return true;
            }

            return true;
        }
    };

    private OnVerifyListener onVerifyListener = new OnVerifyListener() {
        @Override
        public boolean onVerify(Activity activity, boolean b, String s, byte[] bytes) {
            return false;
        }
    };


}
