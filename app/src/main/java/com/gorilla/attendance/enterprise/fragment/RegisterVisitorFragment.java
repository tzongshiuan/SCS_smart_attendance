package com.gorilla.attendance.enterprise.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gorilla.attendance.enterprise.MainActivity;
import com.gorilla.attendance.enterprise.R;
import com.gorilla.attendance.enterprise.database.bean.RegisterBean;
import com.gorilla.attendance.enterprise.datamodel.RegisterReplyModel;
import com.gorilla.attendance.enterprise.util.ApiAccessor;
import com.gorilla.attendance.enterprise.util.ApiUtils;
import com.gorilla.attendance.enterprise.util.ClockUtils;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.DeviceUtils;
import com.gorilla.attendance.enterprise.util.EnrollmentManager;
import com.gorilla.attendance.enterprise.util.EnterpriseUtils;
import com.gorilla.attendance.enterprise.util.FDRControlManager;
import com.gorilla.attendance.enterprise.util.IdentifyVisitorManager;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.NetworkManager;
import com.gorilla.attendance.enterprise.util.apitask.RegisterVisitorV2RfidTask;
import com.gorilla.attendance.enterprise.util.apitask.listener.IRegisterVisitorV2Listener;
import com.gorilla.enroll.lib.listener.OnEnrollListener;
import com.gorilla.enroll.lib.listener.OnVerifyListener;
import com.gorilla.enroll.lib.util.EnrollConfiguration;
import com.gorilla.enroll.lib.util.EnrollUtil;
import com.gorilla.enroll.lib.widget.WaitingProgressView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ggshao on 2017/5/3.
 */

public class RegisterVisitorFragment extends BaseFragment {
    public static final String TAG = "RegisterVisitorFragment";

    public static final String KEY_VISITOR_ID = "key-visitor-id";
    public static final String KEY_RFID_NUMBER = "key-rfid-number";

    private View mView = null;

    private MainActivity mMainActivity;
    private FragmentActivity mActivity = null;
    private Context mContext = null;

    private NetworkManager mNetworkManager = null;
    private Handler mActivityHandler = null;

    private SharedPreferences mSharedPreference = null;

    private String mVisitorId = null;
    private String mVisitorRfidNumber = null;

    private EditText mEdtVisitorId = null;
    private EditText mEdtVisitorName = null;
    private EditText mEdtVisitorEmail = null;
    private EditText mEdtVisitorRfid = null;

    private LinearLayout mLayoutVisitorRfid = null;


    private Button mBtnDone = null;
    private Button mBtnCancel = null;

    private WaitingProgressView mProgress = null;

    private Handler mHandler = null;
    private Activity mEnrollActivity = null;
    private ArrayList<byte[]> mArrayList = null;
    private  boolean mIsEnrollSuccess = false;
    private byte[] mModel = null;

    private static final int ENROLL_DONE = 1;

    private RegisterVisitorV2RfidTask mRegisterVisitorV2Task = null;


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
            mVisitorId = bundle.getString(KEY_VISITOR_ID);
            mVisitorRfidNumber = bundle.getString(KEY_RFID_NUMBER);
            bundle.clear();
        }

        LOG.D(TAG,"mVisitorRfidNumber = " + mVisitorRfidNumber);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LOG.V(TAG, "[onCreateView] ");

        if (mView == null) {
            mView = inflater.inflate(R.layout.register_visitor_fragment, null);
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

        if(mVisitorId != null){
            mEdtVisitorId.setText(mVisitorId);
        }

        //pin code mode
        if(mVisitorId != null){
            mEdtVisitorId.setText(mVisitorId);
            mEdtVisitorId.setEnabled(false);
            mLayoutVisitorRfid.setVisibility(View.GONE);
//            mEdtVisitorRfid.setEnabled(true);
        }

        //rfid mode
        if(mVisitorRfidNumber != null){
            mEdtVisitorRfid.setText(mVisitorRfidNumber);
            mEdtVisitorId.setEnabled(true);
            mLayoutVisitorRfid.setVisibility(View.VISIBLE);
            mEdtVisitorRfid.setEnabled(false);

        }

    }

    @Override
    public void onStop() {
        LOG.D(TAG,"onStop");
        super.onStop();

        if(mRegisterVisitorV2Task != null){
            mRegisterVisitorV2Task.cancel(true);
        }
    }

    @Override
    public void onDestroy() {
        LOG.D(TAG,"onDestroy");
        super.onDestroy();
    }


    private void initView(){

        mEdtVisitorId = (EditText) mView.findViewById(R.id.edt_visitor_phone_number);
        mEdtVisitorName = (EditText) mView.findViewById(R.id.edt_visitor_name);
        mEdtVisitorEmail = (EditText) mView.findViewById(R.id.edt_employee_email);
        mEdtVisitorRfid = (EditText) mView.findViewById(R.id.edt_visitor_rfid);

        mLayoutVisitorRfid = (LinearLayout) mView.findViewById(R.id.layout_visitor_rfid);

        mBtnDone = (Button) mView.findViewById(R.id.btn_conf_done);
        mBtnCancel = (Button) mView.findViewById(R.id.btn_conf_cancel);

        mProgress = (WaitingProgressView) mView.findViewById(R.id.progress);

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
                            LOG.D(TAG,"mVisitorId = " + mVisitorId);
                            //must here

                            String visitorId = mEdtVisitorId.getText().toString();
                            String visitorName = mEdtVisitorName.getText().toString();
                            String visitorEmail = mEdtVisitorEmail.getText().toString();
                            String visitorRfid = mEdtVisitorRfid.getText().toString();

//                            mRegisterVisitorV2Task = ApiUtils.registerVisitorV2(TAG, mContext, ClockUtils.mRegisterBean.getDeviceToken(), visitorId, visitorName, "", "",
//                                    ClockUtils.mRegisterBean.getCreateTime(), ClockUtils.mRegisterBean.getFormat(), ClockUtils.mRegisterBean.getDataInBase64(), visitorEmail, visitorId, registerVisitorV2Listener);

                            LOG.D(TAG,"visitorRfid = " + visitorRfid);

                            mRegisterVisitorV2Task = ApiUtils.registerVisitorV2Rfid(TAG, mContext, ClockUtils.mRegisterBean.getDeviceToken(), visitorId, visitorName, "", "",
                                    ClockUtils.mRegisterBean.getCreateTime(), ClockUtils.mRegisterBean.getFormat(),
                                    ClockUtils.mRegisterBean.getDataInBase64(), visitorEmail, visitorId, visitorRfid, registerVisitorV2Listener);

                        }

                        break;
                }
                super.handleMessage(msg);
            }
        };


    }

    private boolean isEmailValid(String email) {
//        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
//        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(email);
//        return matcher.matches();

        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private Button.OnClickListener mBtnDoneClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            //Call visitor register api

            String visitorId = mEdtVisitorId.getText().toString();
            String visitorName = mEdtVisitorName.getText().toString();
            String visitorEmail = mEdtVisitorEmail.getText().toString();
            String visitorRfid = mEdtVisitorRfid.getText().toString();

            //check is Empty
            if(visitorName.isEmpty() || visitorEmail.isEmpty()){
                Toast.makeText(mContext, getString(R.string.register_empty), Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isEmailValid = isEmailValid(visitorEmail);
            if(isEmailValid == false){
                //show toast
                Toast.makeText(mContext, getString(R.string.register_email_invalid), Toast.LENGTH_SHORT).show();
                return;
            }

            mProgress.setVisiableWithAnimate(View.VISIBLE);

            EnrollUtil.ENROLL_POLLING_TIME = 2000;

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

            ClockUtils.mRegisterBean.setRegisterType(Constants.REGISTER_TYPE_VISITOR);
            ClockUtils.mRegisterBean.setIsSearchUserSuccess(false);

            int registerModelId = mSharedPreference.getInt(Constants.PREF_KEY_REGISTER_MODEL_ID, Constants.REGISTER_MODEL_ID_INIT);
            ClockUtils.mRegisterBean.setModelId(registerModelId);

//                if(mRegisterType)

            ClockUtils.mRegisterBean.setSecurityCode(mVisitorId);
//                ClockUtils.mRegisterBean.setMobileNo();


            ++registerModelId;
            mSharedPreference.edit().putInt(Constants.PREF_KEY_REGISTER_MODEL_ID, registerModelId).apply();

            ClockUtils.mRegisterBean.setEmployeeId("");
            ClockUtils.mRegisterBean.setName(visitorName);
            ClockUtils.mRegisterBean.setMobileNo(visitorId);
            ClockUtils.mRegisterBean.setPassword("");
            ClockUtils.mRegisterBean.setEmail(visitorEmail);
            ClockUtils.mRegisterBean.setDepartment("");
            ClockUtils.mRegisterBean.setTitle("");
            ClockUtils.mRegisterBean.setSecurityCode(visitorId);
            ClockUtils.mRegisterBean.setRfid(visitorRfid);

//            ApiUtils.registerVisitor(TAG, mContext, ClockUtils.mRegisterBean.getDeviceToken(), visitorId, visitorName, "", "",
//                    ClockUtils.mRegisterBean.getCreateTime(), ClockUtils.mRegisterBean.getFormat(), ClockUtils.mRegisterBean.getDataInBase64(), registerVisitorListener);

//            ApiUtils.registerVisitorEmail(TAG, mContext, ClockUtils.mRegisterBean.getDeviceToken(), visitorId, visitorName, "", "",
//                    ClockUtils.mRegisterBean.getCreateTime(), ClockUtils.mRegisterBean.getFormat(), ClockUtils.mRegisterBean.getDataInBase64(), visitorEmail, registerVisitorEmailListener);

            EnrollUtil.startEnrollActivityDirectly(mContext, null, true, enrollConfiguration);

//            mProgress.setVisiableWithAnimate(View.GONE);



        }
    };

    private Button.OnClickListener mBtnCancelClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            mMainActivity.onBackPressed();
        }
    };

    private IRegisterVisitorV2Listener registerVisitorV2Listener = new IRegisterVisitorV2Listener() {
        @Override
        public void onRegisterVisitorV2(RegisterReplyModel model) {
            mProgress.setVisiableWithAnimate(View.GONE);
            LOG.D(TAG,"onRegisterVisitorV2 model  = " + model);

            //Test BY GGGG
//            model = null;

            if(model != null){
                if(model.getStatus().equals(Constants.STATUS_SUCCESS)){
                    //Register success, Got a new uuid

                    //back to home page
                    Message message = new Message();
                    Bundle bundle = new Bundle();

                    bundle.putString(Constants.KEY_REGISTER_MESSAGE, getString(R.string.register_success));
                    bundle.putString(Constants.KEY_REGISTER_ID_TITLE, getString(R.string.txt_phone_number));
                    bundle.putString(Constants.KEY_REGISTER_ID, ClockUtils.mRegisterBean.getSecurityCode());
                    bundle.putString(Constants.KEY_REGISTER_NAME_TITLE, getString(R.string.txt_visitor_name));
                    bundle.putString(Constants.KEY_REGISTER_NAME, ClockUtils.mRegisterBean.getName());
                    bundle.putString(Constants.KEY_REGISTER_RFID, ClockUtils.mRegisterBean.getRfid());


                    LOG.D(TAG,"ClockUtils.mRegisterBean.getRfid() = " + ClockUtils.mRegisterBean.getRfid());
//                    LOG.D(TAG,"ClockUtils.mRegisterBean.getRfid() = " + ClockUtils.mRegisterBean.get());
//                    LOG.D(TAG,"ClockUtils.mRegisterBean.getRfid() = " + ClockUtils.mRegisterBean.getRfid());
                    message.setData(bundle);
                    message.what = Constants.MSG_SHOW_REGISTER_DIALOG;

                    mActivityHandler.sendMessage(message);

                    EnterpriseUtils.addRegisterToVisitorModel(mContext, ClockUtils.mRegisterBean);
                    EnterpriseUtils.addRegisterToVisitorAcceptanceDb(mContext, ClockUtils.mRegisterBean);
                    EnterpriseUtils.addRegisterToVisitorIdentityDb(mContext, ClockUtils.mRegisterBean);


//            EnterpriseUtils.addRegisterToVisitorRegisterDb(mContext, ClockUtils.mRegisterBean);
//                IdentifyVisitorManager.getInstance(mContext).addSingleModel(ClockUtils.mRegisterBean.getModelId(), ClockUtils.mRegisterBean.getModel());
                    IdentifyVisitorManager.getInstance(mContext).addModelFromDb();

                }else{
//                    EnterpriseUtils.addRegisterToVisitorRegisterDb(mContext, ClockUtils.mRegisterBean);

                    Message message = new Message();
                    Bundle bundle = new Bundle();

                    if(model.getError().getMessage() != null){
                        if(model.getError().getCode().equals(Constants.CODE_ERROR_12)){
                            bundle.putString(Constants.KEY_REGISTER_MESSAGE, getString(R.string.visitor_register_data_repeat));
                        }else{
                            bundle.putString(Constants.KEY_REGISTER_MESSAGE, model.getError().getMessage());
                        }

                    }else{
                        bundle.putString(Constants.KEY_REGISTER_MESSAGE, getString(R.string.register_fail_again));
                    }

                    bundle.putString(Constants.KEY_REGISTER_ID_TITLE, null);
                    bundle.putString(Constants.KEY_REGISTER_ID, null);
                    bundle.putString(Constants.KEY_REGISTER_NAME_TITLE, null);
                    bundle.putString(Constants.KEY_REGISTER_NAME, null);
                    bundle.putString(Constants.KEY_REGISTER_RFID, null);
                    bundle.putString(Constants.KEY_REGISTER_DELAY_TIME, "0");

                    message.setData(bundle);
                    message.what = Constants.MSG_SHOW_REGISTER_DIALOG;

                    mActivityHandler.sendMessage(message);
                }
            }else{
//                EnterpriseUtils.addRegisterToVisitorRegisterDb(mContext, ClockUtils.mRegisterBean);

                Message message = new Message();
                Bundle bundle = new Bundle();

                bundle.putString(Constants.KEY_REGISTER_MESSAGE, getString(R.string.register_fail_again));
                bundle.putString(Constants.KEY_REGISTER_ID_TITLE, null);
                bundle.putString(Constants.KEY_REGISTER_ID, null);
                bundle.putString(Constants.KEY_REGISTER_NAME_TITLE, null);
                bundle.putString(Constants.KEY_REGISTER_NAME, null);
                bundle.putString(Constants.KEY_REGISTER_RFID, null);
                bundle.putString(Constants.KEY_REGISTER_DELAY_TIME, "0");

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
