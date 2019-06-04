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
import android.widget.LinearLayout;

import com.gorilla.attendance.enterprise.MainActivity;
import com.gorilla.attendance.enterprise.R;
import com.gorilla.attendance.enterprise.util.ClockUtils;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.DeviceUtils;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.NetworkManager;

/**
 * Created by ggshao on 2017/2/9.
 */

public class ChooseModeFragment extends BaseFragment {
    public static final String TAG = "ChooseModeFragment";
    private View mView = null;

    private MainActivity mMainActivity;
    private FragmentActivity mActivity = null;
    private Context mContext = null;

    private NetworkManager mNetworkManager = null;
    private Handler mActivityHandler = null;

    private LinearLayout mLayoutRfid = null;
    private LinearLayout mLayoutQrCode = null;
    private LinearLayout mLayoutPinCode = null;
    private LinearLayout mLayoutFaceIcon = null;
    private LinearLayout mLayoutFaceIdentification = null;

//    private PinCodeFragment mPinCodeFragment = null;
    private FaceIconFragment mFaceIconFragment = null;
    private FaceIdentificationFragment mFaceIdentificationFragment = null;

    private EnglishPinCodeFragment mEnglishPinCodeFragment = null;
    private RFIDFragment mRFIDFragment = null;
    private QrCodeFragment mQrCodeFragment = null;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        LOG.D(TAG,"onCreate");
        super.onCreate(savedInstanceState);

        mActivity = getActivity();
        mMainActivity = (MainActivity) getActivity();
        mContext = getActivity();

        DeviceUtils.checkSettingLanguage(mContext);

        mActivityHandler = mMainActivity.getHandler();
        if(getFragmentManager().getBackStackEntryCount() > 0){
            mActivityHandler.removeMessages(Constants.LAUNCH_VIDEO);
            mActivityHandler.removeMessages(Constants.BACK_TO_INDEX_PAGE);
            mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.PAGE_DELAYED_TIME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LOG.V(TAG, "[onCreateView]");

        if (mView == null) {
            mView = inflater.inflate(R.layout.choose_mode_fragment, null);
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
        LOG.D(TAG,"onPause");
        super.onPause();

    }

    @Override
    public void onResume(){
        LOG.D(TAG,"onResume");
        super.onResume();
//        mMainActivity.setContentViewLayout(false);
//        mMainActivity.setActionBarTitle(getString(R.string.title_recommend));

        //decide show which mode
        for(int i = 0 ; i < ClockUtils.mRoleModel.getModes().length ; i++){
            switch (ClockUtils.mRoleModel.getModes()[i]){
                case Constants.MODES_SECURITY_CODE :
                    mLayoutPinCode.setVisibility(View.VISIBLE);
                    break;
                case Constants.MODES_RFID :
                    mLayoutRfid.setVisibility(View.VISIBLE);
                    break;
                case Constants.MODES_QR_CODE :
                    mLayoutQrCode.setVisibility(View.VISIBLE);
                    break;
                case Constants.MODES_FACE_ICON :
                    //no use
                    mLayoutFaceIcon.setVisibility(View.GONE);
                    break;
                case Constants.MODES_FACE_IDENTIFICATION :
                    mLayoutFaceIdentification.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }
        }

        if(getFragmentManager().getBackStackEntryCount() > 0){
            mMainActivity.setHomeSettingWord(getString(R.string.txt_home_page));
            mMainActivity.setHomeSettingBackGround(R.mipmap.icon_back_to_home);
        }else{
            mMainActivity.setHomeSettingWord(getString(R.string.txt_home_setting));
            mMainActivity.setHomeSettingBackGround(R.mipmap.icon_back_to_home_setting);
        }

        mMainActivity.setIdentifyResult("");

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
    }

    private void initView(){
        mLayoutRfid = (LinearLayout) mView.findViewById(R.id.layout_rfid);
        mLayoutQrCode = (LinearLayout) mView.findViewById(R.id.layout_qr_code);
        mLayoutPinCode = (LinearLayout) mView.findViewById(R.id.layout_pin_code);
        mLayoutFaceIcon = (LinearLayout) mView.findViewById(R.id.layout_face_icon);
        mLayoutFaceIdentification = (LinearLayout) mView.findViewById(R.id.layout_face_identification);

        mLayoutRfid.setVisibility(View.GONE);
        mLayoutQrCode.setVisibility(View.GONE);
        mLayoutPinCode.setVisibility(View.GONE);
        mLayoutFaceIcon.setVisibility(View.GONE);
        mLayoutFaceIdentification.setVisibility(View.GONE);

        mLayoutRfid.setOnClickListener(mLayoutRfidClickListener);
        mLayoutQrCode.setOnClickListener(mLayoutQrCodeClickListener);
        mLayoutPinCode.setOnClickListener(mLayoutPinCodeClickListener);
        mLayoutFaceIcon.setOnClickListener(mLayoutFaceIconClickListener);
        mLayoutFaceIdentification.setOnClickListener(mLayoutFaceIdentificationClickListener);


    }

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

            case Constants.FRAGMENT_TAG_PIN_CODE:

//                mPinCodeFragment = new PinCodeFragment();
//
////                arguments.putString(WebViewFragment.KEY_WEB_VIEW_URL, webViewUrl);
////                mPinCodeFragment.setArguments(arguments);
//
//                ft.replace(Constants.CONTENT_FRAME_ID, mPinCodeFragment, PinCodeFragment.TAG).addToBackStack(PinCodeFragment.TAG).commitAllowingStateLoss();
                break;

            case Constants.FRAGMENT_TAG_ENGLISH_PIN_CODE://banner and article

                mEnglishPinCodeFragment = new EnglishPinCodeFragment();


                ft.replace(Constants.CONTENT_FRAME_ID, mEnglishPinCodeFragment, EnglishPinCodeFragment.TAG).addToBackStack(EnglishPinCodeFragment.TAG).commitAllowingStateLoss();
                break;

            case Constants.FRAGMENT_TAG_FACE_ICON:

                mFaceIconFragment = new FaceIconFragment();

                ft.replace(Constants.CONTENT_FRAME_ID, mFaceIconFragment, FaceIconFragment.TAG).addToBackStack(FaceIconFragment.TAG).commitAllowingStateLoss();
                break;

            case Constants.FRAGMENT_TAG_FACE_IDENTIFICATION:

                mFaceIdentificationFragment = new FaceIdentificationFragment();
                ft.replace(Constants.CONTENT_FRAME_ID, mFaceIdentificationFragment, FaceIdentificationFragment.TAG).addToBackStack(FaceIdentificationFragment.TAG).commitAllowingStateLoss();
                break;

            case Constants.FRAGMENT_TAG_RFID:

                mRFIDFragment = new RFIDFragment();
                ft.replace(Constants.CONTENT_FRAME_ID, mRFIDFragment, RFIDFragment.TAG).addToBackStack(RFIDFragment.TAG).commitAllowingStateLoss();
                break;

            case Constants.FRAGMENT_TAG_QR_CODE:

                mQrCodeFragment = new QrCodeFragment();
                ft.replace(Constants.CONTENT_FRAME_ID, mQrCodeFragment, QrCodeFragment.TAG).addToBackStack(QrCodeFragment.TAG).commitAllowingStateLoss();
                break;


        }

    }

    private Button.OnClickListener mLayoutRfidClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            LOG.D(TAG,"mLayoutRfidClickListener");
            ClockUtils.mMode = Constants.MODES_RFID;
            launchFragment(Constants.FRAGMENT_TAG_RFID, false);

        }
    };

    private Button.OnClickListener mLayoutQrCodeClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            LOG.D(TAG,"mLayoutQrCodeClickListener");
            ClockUtils.mMode = Constants.MODES_QR_CODE;
            launchFragment(Constants.FRAGMENT_TAG_QR_CODE, false);

        }
    };

    private Button.OnClickListener mLayoutPinCodeClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            LOG.D(TAG,"mLayoutPinCodeClickListener");
//            ClockUtils.mMode = Constants.MODES_SECURITY_CODE;
//            launchFragment(Constants.FRAGMENT_TAG_PIN_CODE, false);


            ClockUtils.mMode = Constants.MODES_SECURITY_CODE;
            launchFragment(Constants.FRAGMENT_TAG_ENGLISH_PIN_CODE, false);

        }
    };

    private Button.OnClickListener mLayoutFaceIconClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            LOG.D(TAG,"mLayoutFaceIconClickListener");
            ClockUtils.mMode = Constants.MODES_FACE_ICON;
            launchFragment(Constants.FRAGMENT_TAG_FACE_ICON, false);

        }
    };

    private Button.OnClickListener mLayoutFaceIdentificationClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            LOG.D(TAG,"mLayoutFaceIdentificationClickListener");
            ClockUtils.mMode = Constants.MODES_FACE_IDENTIFICATION;
            launchFragment(Constants.FRAGMENT_TAG_FACE_IDENTIFICATION, false);
        }
    };

}
