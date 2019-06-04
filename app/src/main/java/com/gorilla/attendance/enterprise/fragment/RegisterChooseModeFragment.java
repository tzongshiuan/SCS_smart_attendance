package com.gorilla.attendance.enterprise.fragment;

import android.content.Context;
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
import android.widget.LinearLayout;

import com.gorilla.attendance.enterprise.MainActivity;
import com.gorilla.attendance.enterprise.R;
import com.gorilla.attendance.enterprise.util.ClockUtils;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.DeviceUtils;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.NetworkManager;

/**
 * Created by ggshao on 2018/1/25.
 */

public class RegisterChooseModeFragment extends BaseFragment {
    public static final String TAG = "RegisterChooseModeFragment";

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

    private RegisterInputIdFragment mRegisterInputIdFragment = null;
    private RegisterInputRfidFragment mRegisterInputRfidFragment = null;
    private RegisterInputQRFragment mRegisterInputQRFragment = null;

    private LinearLayout mLayoutRfid = null;
    private LinearLayout mLayoutPinCode = null;
    private LinearLayout mLayoutQRcode = null;

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

        Bundle bundle = getArguments();
        if (bundle != null) {
            mRegisterType = bundle.getInt(KEY_REGISTER_TYPE);
            bundle.clear();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LOG.V(TAG, "[onCreateView]");

        if (mView == null) {
            mView = inflater.inflate(R.layout.register_choose_mode_fragment, null);
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
        LOG.D(TAG, "onResume");

        for (int i = 0; i < ClockUtils.mRoleModel.getModes().length; i++) {
            if (ClockUtils.mRoleModel.getModes()[i] == Constants.MODES_SECURITY_CODE) {
                mLayoutPinCode.setVisibility(View.VISIBLE);
            }
            if (ClockUtils.mRoleModel.getModes()[i] == Constants.MODES_RFID) {
                mLayoutRfid.setVisibility(View.VISIBLE);
            }
            if (ClockUtils.mRoleModel.getModes()[i] == Constants.MODES_QR_CODE) {
                mLayoutQRcode.setVisibility(View.VISIBLE);
            }
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
        mLayoutRfid = (LinearLayout) mView.findViewById(R.id.layout_rfid);
        mLayoutPinCode = (LinearLayout) mView.findViewById(R.id.layout_pin_code);
        mLayoutQRcode = (LinearLayout) mView.findViewById(R.id.layout_qr_code);
        mLayoutRfid.setVisibility(View.GONE);
        mLayoutPinCode.setVisibility(View.GONE);
        mLayoutQRcode.setVisibility(View.GONE);
        mLayoutRfid.setOnClickListener(mLayoutRfidClickListener);
        mLayoutPinCode.setOnClickListener(mLayoutPinCodeClickListener);
        mLayoutQRcode.setOnClickListener(mLayoutQRcodeClickListener);
//        mRadioButtonClockIn.setOnCheckedChangeListener(mRadioButtonClockInCheckedChangeListener);


    }

    private Button.OnClickListener mLayoutRfidClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            launchFragment(Constants.FRAGMENT_TAG_REGISTER_INPUT_RFID, false, mRegisterType);
        }
    };

    private Button.OnClickListener mLayoutPinCodeClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            launchFragment(Constants.FRAGMENT_TAG_REGISTER_INPUT_ID, false, mRegisterType);
        }
    };

    private Button.OnClickListener mLayoutQRcodeClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            launchFragment(Constants.FRAGMENT_TAG_REGISTER_INPUT_QR, false, mRegisterType);
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

            case Constants.FRAGMENT_TAG_REGISTER_INPUT_RFID:

                mRegisterInputRfidFragment = new RegisterInputRfidFragment();
                arguments.putInt(RegisterInputRfidFragment.KEY_REGISTER_TYPE, registerType);
                mRegisterInputRfidFragment.setArguments(arguments);

                ft.replace(Constants.CONTENT_FRAME_ID, mRegisterInputRfidFragment, RegisterInputRfidFragment.TAG)
                        .addToBackStack(RegisterInputRfidFragment.TAG).commitAllowingStateLoss();
                break;

            case Constants.FRAGMENT_TAG_REGISTER_INPUT_ID:

                mRegisterInputIdFragment = new RegisterInputIdFragment();
                arguments.putInt(RegisterInputIdFragment.KEY_REGISTER_TYPE, registerType);
                mRegisterInputIdFragment.setArguments(arguments);

                ft.replace(Constants.CONTENT_FRAME_ID, mRegisterInputIdFragment, RegisterInputIdFragment.TAG).addToBackStack(RegisterInputIdFragment.TAG).commitAllowingStateLoss();

                break;

            case Constants.FRAGMENT_TAG_REGISTER_INPUT_QR:

                mRegisterInputQRFragment = new RegisterInputQRFragment();
                arguments.putInt(RegisterInputQRFragment.KEY_REGISTER_TYPE, registerType);
                mRegisterInputQRFragment.setArguments(arguments);

                ft.replace(Constants.CONTENT_FRAME_ID, mRegisterInputQRFragment, RegisterInputQRFragment.TAG).addToBackStack(RegisterInputQRFragment.TAG).commitAllowingStateLoss();

                break;

        }

    }


}
