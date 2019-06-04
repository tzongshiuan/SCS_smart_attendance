package com.gorilla.attendance.enterprise.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

/**
 * Created by ggshao on 2017/2/7.
 */

public class PinCodeFragment extends BaseFragment {
    public static final String TAG = "PinCodeFragment";

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
    private Button mBtnNumber0 = null;
    private Button mBtnNumber1 = null;
    private Button mBtnNumber2 = null;
    private Button mBtnNumber3 = null;
    private Button mBtnNumber4 = null;
    private Button mBtnNumber5 = null;
    private Button mBtnNumber6 = null;
    private Button mBtnNumber7 = null;
    private Button mBtnNumber8 = null;
    private Button mBtnNumber9 = null;
    private Button mBtnDash = null;
    private ImageButton mBtnBack = null;
    private Button mBtnNext = null;

    private String mLoginAccount = "";


    private SharedPreferences mSharedPreference = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LOG.D(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mMainActivity = (MainActivity) getActivity();
        mContext = getActivity();

        mActivityHandler = mMainActivity.getHandler();
        if(getFragmentManager().getBackStackEntryCount() > 0){
            mActivityHandler.removeMessages(Constants.LAUNCH_VIDEO);
            mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.PAGE_DELAYED_TIME);
        }

        mSharedPreference = mContext.getSharedPreferences(Constants.PREF_NAME_ATTENDANCE_ENTERPRISE, Context.MODE_PRIVATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LOG.V(TAG, "[onCreateView] ");

        if (mView == null) {
            mView = inflater.inflate(R.layout.pin_code_fragment, null);
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
        LOG.D(TAG,"onResume getFragmentManager().getBackStackEntryCount() = " + getFragmentManager().getBackStackEntryCount());
        super.onResume();

        if(ClockUtils.mRoleModel.getModules() == Constants.MODULES_VISITORS){
            mTxtLoginAccount.setHint(getString(R.string.txt_visitor_pin_code_hint));
        }else{
            mTxtLoginAccount.setHint(getString(R.string.txt_please_input_password));
        }

        if(getFragmentManager().getBackStackEntryCount() > 0){
            mMainActivity.setHomeSettingWord(getString(R.string.txt_home_page));
            mMainActivity.setHomeSettingBackGround(R.mipmap.icon_back_to_home);
        }else{
            mMainActivity.setHomeSettingWord(getString(R.string.txt_home_setting));
            mMainActivity.setHomeSettingBackGround(R.mipmap.icon_back_to_home_setting);
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
        mFdrFrame.removeAllViews();
        FDRControlManager.getInstance(mContext).stopFdr(mActivityHandler, mFragmentHandler);


        super.onDestroy();
    }

    private void initView(){
        LOG.D(TAG, "initView");

        mLayoutPinCode = (RelativeLayout) mView.findViewById(R.id.layout_pin_code);
        mTxtPinCodeHint = (TextView) mView.findViewById(R.id.txt_pin_code_hint);
        mLayoutFdr = (RelativeLayout) mView.findViewById(R.id.layout_fdr);
        mFdrFrame = (FrameLayout) mView.findViewById(R.id.fdr_frame);
//        mFdrFrame.removeAllViews();

        mTxtLoginAccount = (TextView) mView.findViewById(R.id.txt_login_account);
        mBtnNext = (Button) mView.findViewById(R.id.btn_next);
        mBtnNumber0 = (Button) mView.findViewById(R.id.btn_number_0);
        mBtnNumber1 = (Button) mView.findViewById(R.id.btn_number_1);
        mBtnNumber2 = (Button) mView.findViewById(R.id.btn_number_2);
        mBtnNumber3 = (Button) mView.findViewById(R.id.btn_number_3);
        mBtnNumber4 = (Button) mView.findViewById(R.id.btn_number_4);
        mBtnNumber5 = (Button) mView.findViewById(R.id.btn_number_5);
        mBtnNumber6 = (Button) mView.findViewById(R.id.btn_number_6);
        mBtnNumber7 = (Button) mView.findViewById(R.id.btn_number_7);
        mBtnNumber8 = (Button) mView.findViewById(R.id.btn_number_8);
        mBtnNumber9 = (Button) mView.findViewById(R.id.btn_number_9);

        mBtnDash = (Button) mView.findViewById(R.id.btn_number_dash);
        mBtnBack = (ImageButton) mView.findViewById(R.id.btn_number_back);

        mBtnNumber0.setOnTouchListener(buttonTouchListener);
        mBtnNumber1.setOnTouchListener(buttonTouchListener);
        mBtnNumber2.setOnTouchListener(buttonTouchListener);
        mBtnNumber3.setOnTouchListener(buttonTouchListener);
        mBtnNumber4.setOnTouchListener(buttonTouchListener);
        mBtnNumber5.setOnTouchListener(buttonTouchListener);
        mBtnNumber6.setOnTouchListener(buttonTouchListener);
        mBtnNumber7.setOnTouchListener(buttonTouchListener);
        mBtnNumber8.setOnTouchListener(buttonTouchListener);
        mBtnNumber9.setOnTouchListener(buttonTouchListener);
        mBtnDash.setOnTouchListener(buttonTouchListener);
        mBtnBack.setOnTouchListener(buttonTouchListener);


        mBtnBack.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent mv) {
                if(mv.getX() < 0 || mv.getX() > view.getWidth() || mv.getY() < 0 || mv.getY() > view.getHeight())
                {
                    mBtnBack.setImageResource(R.mipmap.icon_delete_normal);
                    return false;
                }

                switch(mv.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        mBtnBack.setImageResource(R.mipmap.icon_delete_pressed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        mBtnBack.setImageResource(R.mipmap.icon_delete_normal);
                        btnBackClickListener.onClick(view);
                        return true;
                }
                return false;
            }

        });

        mBtnNext.setOnTouchListener(buttonTouchListener);
    }


    public void backToHome(){
        mFdrFrame.removeAllViews();
        FDRControlManager.getInstance(mContext).stopFdr(mActivityHandler, mFragmentHandler);
        mLayoutFdr.setVisibility(FrameLayout.GONE);
        mLayoutPinCode.setVisibility(View.VISIBLE);
        mTxtLoginAccount.setText("");
        mLoginAccount = "";


    }

    private void startFdr(){
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

    private View.OnTouchListener buttonTouchListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View view, MotionEvent mv) {
//            v.findViewById()

            Button button = (Button)view.findViewById(view.getId());
            if(mv.getX() < 0 || mv.getX() > view.getWidth() || mv.getY() < 0 || mv.getY() > view.getHeight())
            {
                button.setBackgroundResource(R.drawable.button_not_pressed);
                button.setTextColor(mContext.getResources().getColor(R.color.light_brown));
                return false;
            }

            mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.PAGE_DELAYED_TIME);


            switch(mv.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    button.setBackgroundResource(R.drawable.button_pressed);
                    button.setTextColor(mContext.getResources().getColor(R.color.white));
                    return true;
                case MotionEvent.ACTION_UP:
                    button.setBackgroundResource(R.drawable.button_not_pressed);
                    button.setTextColor(mContext.getResources().getColor(R.color.light_brown));

                    switch(view.getId()){
                        case R.id.btn_number_0:
                            btn0ClickListener.onClick(view);
                            break;
                        case R.id.btn_number_1:
                            btn1ClickListener.onClick(view);
                            break;
                        case R.id.btn_number_2:
                            btn2ClickListener.onClick(view);
                            break;
                        case R.id.btn_number_3:
                            btn3ClickListener.onClick(view);
                            break;
                        case R.id.btn_number_4:
                            btn4ClickListener.onClick(view);
                            break;
                        case R.id.btn_number_5:
                            btn5ClickListener.onClick(view);
                            break;
                        case R.id.btn_number_6:
                            btn6ClickListener.onClick(view);
                            break;
                        case R.id.btn_number_7:
                            btn7ClickListener.onClick(view);
                            break;
                        case R.id.btn_number_8:
                            btn8ClickListener.onClick(view);
                            break;
                        case R.id.btn_number_9:
                            btn9ClickListener.onClick(view);
                            break;
                        case R.id.btn_number_dash:
                            btnDashClickListener.onClick(view);
                            break;
                        case R.id.btn_next:
                            mBtnNextClickListener.onClick(view);
                            break;
                    }

                    return true;
            }
            return false;
        }
    };


    private Button.OnClickListener btn0ClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            mLoginAccount += "0";
            mTxtLoginAccount.setText(mLoginAccount);
        }
    };

    private Button.OnClickListener btn1ClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            mLoginAccount += "1";
            mTxtLoginAccount.setText(mLoginAccount);
        }
    };

    private Button.OnClickListener btn2ClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            mLoginAccount += "2";
            mTxtLoginAccount.setText(mLoginAccount);
        }
    };

    private Button.OnClickListener btn3ClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            mLoginAccount += "3";
            mTxtLoginAccount.setText(mLoginAccount);
        }
    };

    private Button.OnClickListener btn4ClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            mLoginAccount += "4";
            mTxtLoginAccount.setText(mLoginAccount);
        }
    };

    private Button.OnClickListener btn5ClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            mLoginAccount += "5";
            mTxtLoginAccount.setText(mLoginAccount);
        }
    };

    private Button.OnClickListener btn6ClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            mLoginAccount += "6";
            mTxtLoginAccount.setText(mLoginAccount);
        }
    };

    private Button.OnClickListener btn7ClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            mLoginAccount += "7";
            mTxtLoginAccount.setText(mLoginAccount);
        }
    };

    private Button.OnClickListener btn8ClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            mLoginAccount += "8";
            mTxtLoginAccount.setText(mLoginAccount);

            //back to index page
//            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        }
    };

    private Button.OnClickListener btn9ClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            mLoginAccount += "9";
            mTxtLoginAccount.setText(mLoginAccount);
        }
    };

    private Button.OnClickListener btnDashClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            mLoginAccount += "-";
            mTxtLoginAccount.setText(mLoginAccount);
        }
    };

    private Button.OnClickListener btnBackClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            if(mLoginAccount.length() > 0){
                StringBuffer buf = new StringBuffer(mLoginAccount.length() -1);
                buf.append(mLoginAccount.substring(0, mLoginAccount.length() -1)).append(mLoginAccount.substring(mLoginAccount.length() -1+1));
                mLoginAccount =  buf.toString();

                mTxtLoginAccount.setText(mLoginAccount);
            }
        }
    };



    private Button.OnClickListener mBtnNextClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            LOG.D(TAG, "mBtnNextClickListener mLoginAccount = " + mLoginAccount);

            boolean isLoginAccountCanClock = false;

            //  1. check password if in list
            if(ClockUtils.mRoleModel instanceof EmployeeModel){
                LOG.D(TAG,"I am Employee");

                EmployeeModel employeeModel = ((EmployeeModel) ClockUtils.mRoleModel);

                if(employeeModel.getEmployeeData() != null){
                    for(int i = 0 ; i < employeeModel.getEmployeeData().getAcceptances().size() ; i++){
                        if(mLoginAccount.equals(employeeModel.getEmployeeData().getAcceptances().get(i).getSecurityCode())){
                            isLoginAccountCanClock = true;

                            ClockUtils.mLoginAccount = mLoginAccount;
                            ClockUtils.mLoginUuid = employeeModel.getEmployeeData().getAcceptances().get(i).getUuid();

                            break;
                        }
                    }

                    //test get DB
//                DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance(mContext);
//                databaseAdapter.open();
//                AcceptancesBean acceptancesBean = databaseAdapter.getEmployeesAcceptancesBySecurityId(mLoginAccount);
//                LOG.D(TAG,"acceptancesBean = " + acceptancesBean);
//                if(acceptancesBean != null){
//                    isLoginAccountCanClock = true;
//                }else{
//                    isLoginAccountCanClock = false;
//                }
//
//                databaseAdapter.close();
                }else{

                }




            }else if(ClockUtils.mRoleModel instanceof VisitorModel){
                LOG.D(TAG,"I am visitor");


                VisitorModel visitorModel = ((VisitorModel) ClockUtils.mRoleModel);
                if(visitorModel.getVisitorData() != null){
                    for(int i = 0 ; i < visitorModel.getVisitorData().getAcceptances().size() ; i++){
                        if(mLoginAccount.equals(visitorModel.getVisitorData().getAcceptances().get(i).getSecurityCode())){
                            isLoginAccountCanClock = true;

                            ClockUtils.mLoginAccount = mLoginAccount;
                            ClockUtils.mLoginUuid = visitorModel.getVisitorData().getAcceptances().get(i).getUuid();

                            break;
                        }
                    }
                }else{

                }



            }

            if(isLoginAccountCanClock){

            }else{
                //show password wrong
                mTxtPinCodeHint.setText(getString(R.string.txt_wrong_password));
                //send wrong Api
                ClockUtils.mLoginAccount = mLoginAccount;
                ClockUtils.mLiveness = "FAILED";
                ClockUtils.mLoginTime = System.currentTimeMillis();
                ++ClockUtils.mSerialNumber;
                mActivityHandler.sendEmptyMessage(Constants.SEND_ATTENDANCE_RECOGNIZED_LOG);


                return;
            }

            mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.FDR_DELAYED_TIME);

            LOG.D(TAG,"mBtnNextClickListener ClockUtils.mLoginAccount = " + ClockUtils.mLoginAccount);
            //2. in list , do face check
            startFdr();


        }
    };


    private Handler mFragmentHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LOG.D(TAG,"mHandler msg.what = " + msg.what);
            switch(msg.what) {
                case Constants.GET_FACE_SUCCESS:

                    mFragmentHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
                    mLayoutFdr.setVisibility(FrameLayout.GONE);
                    mLayoutPinCode.setVisibility(View.VISIBLE);


                    //show clock dialog
                    break;
                case Constants.GET_FACE_FAIL:

                    mFragmentHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
                    mLayoutFdr.setVisibility(FrameLayout.GONE);
                    mLayoutPinCode.setVisibility(View.VISIBLE);
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







}
