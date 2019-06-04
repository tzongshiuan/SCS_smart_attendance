package com.gorilla.attendance.enterprise.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gorilla.attendance.enterprise.MainActivity;
import com.gorilla.attendance.enterprise.R;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.DeviceUtils;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.NetworkManager;

/**
 * Created by ggshao on 2017/4/5.
 */

public class ConfigureSettingLoginFragment extends BaseFragment {
    public static final String TAG = "ConfigureSettingLoginFragment";

    private View mView = null;

    private MainActivity mMainActivity;
    private FragmentActivity mActivity = null;
    private Context mContext = null;

    private NetworkManager mNetworkManager = null;
    private Handler mActivityHandler = null;

    private EditText mEdtConfigureAccount = null;
    private EditText mEdtConfigurePassword = null;

    private Button mBtnConfirm = null;
    private Button mBtnCancel = null;

    private ConfigureSettingEndPointFragment mConfigureSettingEndPointFragment = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LOG.D(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mMainActivity = (MainActivity) getActivity();
        mContext = getActivity();

        DeviceUtils.checkSettingLanguage(mContext);


        mActivityHandler = mMainActivity.getHandler();

        mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
        mActivityHandler.removeMessages(Constants.LAUNCH_VIDEO);

        if(getFragmentManager().getBackStackEntryCount() > 0){
            mActivityHandler.removeMessages(Constants.LAUNCH_VIDEO);
            mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.SETTING_DELAYED_TIME);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LOG.V(TAG, "[onCreateView] ");

        if (mView == null) {
            mView = inflater.inflate(R.layout.configure_setting_login_fragment, null);
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
        super.onDestroy();

        mEdtConfigureAccount.setText("");
        mEdtConfigurePassword.setText("");
    }


    private void initView(){
        mEdtConfigureAccount = (EditText) mView.findViewById(R.id.edt_conf_login_account);
        mEdtConfigurePassword = (EditText) mView.findViewById(R.id.edt_conf_login_password);
        mBtnConfirm = (Button) mView.findViewById(R.id.btn_conf_login);
        mBtnCancel = (Button) mView.findViewById(R.id.btn_conf_cancel);

//        mEdtConfigureAccount.setCompoundDrawablesWithIntrinsicBounds(
//                R.mipmap.icon_account, 0, 0, 0);

//        mEdtConfigurePassword.setCompoundDrawablesWithIntrinsicBounds(
//                R.mipmap.icon_password, 0, 0, 0);



        mBtnConfirm.setOnClickListener(mBtnConfirmClickListener);
        mBtnCancel.setOnClickListener(mBtnCancelClickListener);

    }


    private Button.OnClickListener mBtnConfirmClickListener = new Button.OnClickListener() {
        public void onClick(View v) {

            if(mEdtConfigureAccount.getText().toString().equals(DeviceUtils.CONF_SETTING_ACCOUNT_1) &&
                    mEdtConfigurePassword.getText().toString().equals(DeviceUtils.CONF_SETTING_PASSWORD_1)){
                launchFragment(Constants.FRAGMENT_TAG_CONF_END_POINT_SETTING, false);
            }else if(mEdtConfigureAccount.getText().toString().equals(DeviceUtils.CONF_SETTING_ACCOUNT_2) &&
                    mEdtConfigurePassword.getText().toString().equals(DeviceUtils.CONF_SETTING_PASSWORD_2)){
                launchFragment(Constants.FRAGMENT_TAG_CONF_END_POINT_SETTING, false);
            }else if(mEdtConfigureAccount.getText().toString().equals("a") &&
                    mEdtConfigurePassword.getText().toString().equals("a")){
                launchFragment(Constants.FRAGMENT_TAG_CONF_END_POINT_SETTING, false);
            }else{
                Toast.makeText(mContext, getString(R.string.txt_conf_set_wrong_password), Toast.LENGTH_SHORT).show();
            }

//            launchFragment(Constants.FRAGMENT_TAG_CONF_END_POINT_SETTING, false);

        }
    };

    private Button.OnClickListener mBtnCancelClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mActivityHandler.sendEmptyMessage(Constants.BACK_TO_INDEX_PAGE);
        }
    };

    private void launchFragment(int tag, boolean removeChooseList) {
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

//        Bundle arguments= new Bundle();

        switch (tag) {

            case Constants.FRAGMENT_TAG_CONF_END_POINT_SETTING:

                mConfigureSettingEndPointFragment = new ConfigureSettingEndPointFragment();

                ft.replace(Constants.CONTENT_FRAME_ID, mConfigureSettingEndPointFragment, ConfigureSettingEndPointFragment.TAG).addToBackStack(ConfigureSettingEndPointFragment.TAG).commitAllowingStateLoss();
                break;


        }

    }


}
