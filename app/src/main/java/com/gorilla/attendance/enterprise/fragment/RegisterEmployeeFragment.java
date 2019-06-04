package com.gorilla.attendance.enterprise.fragment;

import android.annotation.SuppressLint;
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
import com.gorilla.attendance.enterprise.util.IdentifyEmployeeManager;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.NetworkManager;
import com.gorilla.attendance.enterprise.util.apitask.RegisterEmployeeV2RfidTask;
import com.gorilla.attendance.enterprise.util.apitask.listener.IRegisterEmployeeV2Listener;
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

public class RegisterEmployeeFragment extends BaseFragment {
    public static final String TAG = "RegisterEmployeeFragment";

//    public static final String KEY_EMPLOYEE_ID = "key-employee-id";
    public static final String KEY_SECURITY_CODE = "key-sequrity-code";
    public static final String KEY_RFID_NUMBER = "key-rfid-number";

    private View mView = null;

    private MainActivity mMainActivity;
    private FragmentActivity mActivity = null;
    private Context mContext = null;

    private NetworkManager mNetworkManager = null;
    private Handler mActivityHandler = null;

    private SharedPreferences mSharedPreference = null;

//    private String mEmployeeId = null;
    private String mSecurityCode = null;
    private String mEmployeeRfidNumber = null;

    private EditText mEdtEmployeeId = null;
    private EditText mEdtEmployeeName = null;
    private EditText mEdtEmployeeSecurityCode = null;
    private EditText mEdtEmployeeEmail = null;
    private EditText mEdtEmployeePassword = null;
    private EditText mEdtEmployeeRfid = null;

    private LinearLayout mLayoutEmployeeRfid = null;

    private Button mBtnDone = null;
    private Button mBtnCancel = null;

//    private ProgressBar mProgress = null;
    private WaitingProgressView mProgress = null;

    private Handler mHandler = null;
    private Activity mEnrollActivity = null;
    private ArrayList<byte[]> mArrayList = null;
    private  boolean mIsEnrollSuccess = false;
    private byte[] mModel = null;

    private static final int ENROLL_DONE = 1;

    RegisterEmployeeV2RfidTask mRegisterEmployeeV2Task = null;


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
//            mEmployeeId = bundle.getString(KEY_EMPLOYEE_ID);
            mSecurityCode = bundle.getString(KEY_SECURITY_CODE);

            mEmployeeRfidNumber = bundle.getString(KEY_RFID_NUMBER);

            bundle.clear();
        }

        LOG.D(TAG, "mEmployeeRfidNumber = " + mEmployeeRfidNumber);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LOG.V(TAG, "[onCreateView] ");

        if (mView == null) {
            mView = inflater.inflate(R.layout.register_employee_fragment, null);
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

        LOG.D(TAG,"onResume mSecurityCode = " + mSecurityCode);

        //pin code register
        if(mSecurityCode != null){
//            mEdtEmployeeId.setText(mEmployeeId);
            mEdtEmployeeSecurityCode.setText(mSecurityCode);
            mEdtEmployeeSecurityCode.setEnabled(false);
            mLayoutEmployeeRfid.setVisibility(View.GONE);
        }

        //rfid register
        if(mEmployeeRfidNumber != null){
            mEdtEmployeeRfid.setText(mEmployeeRfidNumber);
            mEdtEmployeeSecurityCode.setEnabled(true);
            mEdtEmployeeRfid.setEnabled(false);
            mLayoutEmployeeRfid.setVisibility(View.VISIBLE);
        }




    }

    @Override
    public void onStop() {
        LOG.D(TAG,"onStop");
        super.onStop();

        if(mRegisterEmployeeV2Task != null){
            mRegisterEmployeeV2Task.cancel(true);
        }
    }

    @Override
    public void onDestroy() {
        LOG.D(TAG,"onDestroy");
        super.onDestroy();
    }


    @SuppressLint("HandlerLeak")
    private void initView(){

        mEdtEmployeeId = (EditText) mView.findViewById(R.id.edt_employee_id);
        mEdtEmployeeName = (EditText) mView.findViewById(R.id.edt_employee_name);
        mEdtEmployeeSecurityCode = (EditText) mView.findViewById(R.id.edt_employee_security_code);
        mEdtEmployeeEmail = (EditText) mView.findViewById(R.id.edt_employee_email);
        mEdtEmployeePassword = (EditText) mView.findViewById(R.id.edt_employee_password);
        mEdtEmployeeRfid = (EditText) mView.findViewById(R.id.edt_employee_rfid);

        mLayoutEmployeeRfid = (LinearLayout) mView.findViewById(R.id.layout_employee_rfid);


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

                        LOG.D(TAG,"mEnrollActivity.isDestroyed() = " + mEnrollActivity.isDestroyed());
                        LOG.D(TAG,"mEnrollActivity.isFinishing() = " + mEnrollActivity.isFinishing());

                        if(mEnrollActivity.isDestroyed() == true || mEnrollActivity.isFinishing() == true){
                            return;
                        }

                        //not in this page
                        if(mArrayList == null){
                            return;
                        }

                        mMainActivity.setIdentifyResult("");

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
                            LOG.D(TAG,"mSecurityCode = " + mSecurityCode);
                            //must here

                            String employeeId = mEdtEmployeeId.getText().toString();
                            String employeeName = mEdtEmployeeName.getText().toString();
                            String employeeSecurityCode = mEdtEmployeeSecurityCode.getText().toString();
                            String employeeEmail = mEdtEmployeeEmail.getText().toString();
                            String employeePassword = mEdtEmployeePassword.getText().toString();
                            String employeeRfid = mEdtEmployeeRfid.getText().toString();


                            mProgress.setVisiableWithAnimate(View.VISIBLE);

//                            mRegisterEmployeeV2Task = ApiUtils.registerEmployeeV2(TAG, mContext, ClockUtils.mRegisterBean.getDeviceToken(), employeeId, employeeName, employeeEmail, employeePassword, employeeSecurityCode,
//                                    ClockUtils.mRegisterBean.getCreateTime(), ClockUtils.mRegisterBean.getFormat(), ClockUtils.mRegisterBean.getDataInBase64(), registerEmployeeV2Listener);

                            mRegisterEmployeeV2Task = ApiUtils.registerEmployeeV2Rfid(TAG, mContext, ClockUtils.mRegisterBean.getDeviceToken(), employeeId, employeeName, employeeEmail, employeePassword, employeeSecurityCode,
                                    ClockUtils.mRegisterBean.getCreateTime(), ClockUtils.mRegisterBean.getFormat(), ClockUtils.mRegisterBean.getDataInBase64(), employeeRfid, registerEmployeeV2Listener);

                        }

                        break;
                }
                super.handleMessage(msg);
            }
        };


    }

    private Button.OnClickListener mBtnDoneClickListener = new Button.OnClickListener() {
        public void onClick(View v) {

            //call register api
            String employeeId = mEdtEmployeeId.getText().toString();
            String employeeName = mEdtEmployeeName.getText().toString();
            String employeeSecurityCode = mEdtEmployeeSecurityCode.getText().toString();
            String employeeEmail = mEdtEmployeeEmail.getText().toString();
            String employeePassword = mEdtEmployeePassword.getText().toString();
            String employeeRfid = mEdtEmployeeRfid.getText().toString();

            //check is Empty
            boolean isRegisterDataValid = isRegisterDataValid(employeeId, employeeName, employeeEmail, employeePassword);

            if(isRegisterDataValid == false){
                //show toast
                Toast.makeText(mContext, getString(R.string.register_empty), Toast.LENGTH_SHORT).show();
                return;
            }


            boolean isEmailValid = isEmailValid(employeeEmail);
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

            ClockUtils.mRegisterBean.setRegisterType(Constants.REGISTER_TYPE_EMPLOYEE);
            ClockUtils.mRegisterBean.setIsSearchUserSuccess(false);

            int registerModelId = mSharedPreference.getInt(Constants.PREF_KEY_REGISTER_MODEL_ID, Constants.REGISTER_MODEL_ID_INIT);
            ClockUtils.mRegisterBean.setModelId(registerModelId);

//                if(mRegisterType)



            ++registerModelId;
            mSharedPreference.edit().putInt(Constants.PREF_KEY_REGISTER_MODEL_ID, registerModelId).apply();

            ClockUtils.mRegisterBean.setEmployeeId(employeeId);
            ClockUtils.mRegisterBean.setName(employeeName);
            ClockUtils.mRegisterBean.setMobileNo("");
            ClockUtils.mRegisterBean.setPassword(employeePassword);
            ClockUtils.mRegisterBean.setEmail(employeeEmail);
            ClockUtils.mRegisterBean.setSecurityCode(mSecurityCode);
            ClockUtils.mRegisterBean.setRfid(employeeRfid);



            EnrollUtil.startEnrollActivityDirectly(mContext, null, false, enrollConfiguration);

            mProgress.setVisiableWithAnimate(View.GONE);

            //take photo first




//            ApiUtils.registerEmployeeV2(TAG, mContext, ClockUtils.mRegisterBean.getDeviceToken(), employeeId, employeeName, employeeEmail, employeePassword, employeeSecurityCode,
//                    ClockUtils.mRegisterBean.getCreateTime(), ClockUtils.mRegisterBean.getFormat(), ClockUtils.mRegisterBean.getDataInBase64(), registerEmployeeV2Listener);



        }
    };

    private Button.OnClickListener mBtnCancelClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            mMainActivity.onBackPressed();
        }
    };

    private boolean isRegisterDataValid(String employeeId, String employeeName, String employeeEmail, String employeePassword){
        if(employeeId.isEmpty() || employeeName.isEmpty() || employeeEmail.isEmpty() || employeePassword.isEmpty()){
            return false;
        }
        return true;
    }

    private boolean isEmailValid(String email) {
//        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
//        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(email);
//        return matcher.matches();

        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private IRegisterEmployeeV2Listener registerEmployeeV2Listener = new IRegisterEmployeeV2Listener() {
        @Override
        public void onRegisterEmployee(RegisterReplyModel model) {
            LOG.D(TAG,"onRegisterEmployee model = " + model);

            LOG.D(TAG,"onRegisterEmployee ClockUtils.mRegisterBean.getEmployeeId() = " + ClockUtils.mRegisterBean.getEmployeeId());
            LOG.D(TAG,"onRegisterEmployee ClockUtils.mRegisterBean.getMobileNo() = " + ClockUtils.mRegisterBean.getMobileNo());
            LOG.D(TAG,"onRegisterEmployee ClockUtils.mRegisterBean.getCreateTime() = " + ClockUtils.mRegisterBean.getCreateTime());
            LOG.D(TAG,"onRegisterEmployee ClockUtils.mRegisterBean.getDepartment() = " + ClockUtils.mRegisterBean.getDepartment());
            LOG.D(TAG,"onRegisterEmployee ClockUtils.mRegisterBean.getDeviceToken() = " + ClockUtils.mRegisterBean.getDeviceToken());
            LOG.D(TAG,"onRegisterEmployee ClockUtils.mRegisterBean.getModelId() = " + ClockUtils.mRegisterBean.getModelId());
            LOG.D(TAG,"onRegisterEmployee ClockUtils.mRegisterBean.getFormat() = " + ClockUtils.mRegisterBean.getFormat());
            LOG.D(TAG,"onRegisterEmployee ClockUtils.mRegisterBean.getName() = " + ClockUtils.mRegisterBean.getName());
            LOG.D(TAG,"onRegisterEmployee ClockUtils.mRegisterBean.getPassword() = " + ClockUtils.mRegisterBean.getPassword());
            LOG.D(TAG,"onRegisterEmployee ClockUtils.mRegisterBean.getTitle() = " + ClockUtils.mRegisterBean.getTitle());
            LOG.D(TAG,"onRegisterEmployee ClockUtils.mRegisterBean.getDataInBase64() = " + ClockUtils.mRegisterBean.getDataInBase64());
            LOG.D(TAG,"onRegisterEmployee ClockUtils.mRegisterBean.getModel() = " + ClockUtils.mRegisterBean.getModel());
            LOG.D(TAG,"onRegisterEmployee ClockUtils.mRegisterBean.getRegisterType()  = " + ClockUtils.mRegisterBean.getRegisterType());
            LOG.D(TAG,"onRegisterEmployee ClockUtils.mRegisterBean.getSecurityCode()  = " + ClockUtils.mRegisterBean.getSecurityCode());
            LOG.D(TAG,"onRegisterEmployee ClockUtils.mRegisterBean.getRfid()  = " + ClockUtils.mRegisterBean.getRfid());

            mProgress.setVisiableWithAnimate(View.GONE);

            //Test BY GGGG
//            model = null;

            if(model != null){
                if(model.getStatus().equals(Constants.STATUS_SUCCESS)){
                    //Register success, Got a new uuid
//back to home page
                    Message message = new Message();
                    Bundle bundle = new Bundle();

                    bundle.putString(Constants.KEY_REGISTER_MESSAGE, getString(R.string.register_success));
                    bundle.putString(Constants.KEY_REGISTER_ID_TITLE, getString(R.string.txt_employee_id));
                    bundle.putString(Constants.KEY_REGISTER_ID, ClockUtils.mRegisterBean.getEmployeeId());
                    bundle.putString(Constants.KEY_REGISTER_NAME_TITLE, getString(R.string.txt_employee_name));
                    bundle.putString(Constants.KEY_REGISTER_NAME, ClockUtils.mRegisterBean.getName());
                    bundle.putString(Constants.KEY_REGISTER_RFID, ClockUtils.mRegisterBean.getRfid());

                    message.setData(bundle);
                    message.what = Constants.MSG_SHOW_REGISTER_DIALOG;

                    mActivityHandler.sendMessage(message);

                    EnterpriseUtils.addRegisterToEmployeeModel(mContext, ClockUtils.mRegisterBean);
                    EnterpriseUtils.addRegisterToEmployeeAcceptanceDb(mContext, ClockUtils.mRegisterBean);
                    //no rfid info
                    EnterpriseUtils.addRegisterToEmployeeIdentityDb(mContext, ClockUtils.mRegisterBean);

                    IdentifyEmployeeManager.getInstance(mContext).addModelFromDb();


                }else{
                    //show error message
                    //EnterpriseUtils.addRegisterToEmployeeRegisterDb(mContext, ClockUtils.mRegisterBean);
                    Message message = new Message();
                    Bundle bundle = new Bundle();

                    if(model.getError().getCode().equals(Constants.CODE_ERROR_10)){
                        bundle.putString(Constants.KEY_REGISTER_MESSAGE, getString(R.string.employee_register_data_repeat));
                    }else{
                        bundle.putString(Constants.KEY_REGISTER_MESSAGE, model.getError().getMessage());
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
                //show error message
                //EnterpriseUtils.addRegisterToEmployeeRegisterDb(mContext, ClockUtils.mRegisterBean);

                Message message = new Message();
                Bundle bundle = new Bundle();

                bundle.putString(Constants.KEY_REGISTER_MESSAGE, getString(R.string.register_fail_again));
                bundle.putString(Constants.KEY_REGISTER_ID_TITLE, null);
                bundle.putString(Constants.KEY_REGISTER_ID, null);
                bundle.putString(Constants.KEY_REGISTER_NAME_TITLE, null);
                bundle.putString(Constants.KEY_REGISTER_NAME, null);
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
