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

public class ChooseModuleFragment extends BaseFragment {
    public static final String TAG = "ChooseModuleFragment";
    private View mView = null;

    private MainActivity mMainActivity;
    private FragmentActivity mActivity = null;
    private Context mContext = null;

    private NetworkManager mNetworkManager = null;
    private Handler mActivityHandler = null;

    private LinearLayout mLayoutEmployee = null;
    private LinearLayout mLayoutVisitor = null;

    private ChooseModeFragment mChooseModeFragment = null;


//    private PinCodeFragment mPinCodeFragment = null;
    private EnglishPinCodeFragment mEnglishPinCodeFragment = null;
    private FaceIconFragment mFaceIconFragment = null;
    private FaceIdentificationFragment mFaceIdentificationFragment = null;
    private QrCodeFragment mQrCodeFragment = null;
    private RFIDFragment mRFIDFragment = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mMainActivity = (MainActivity) getActivity();
        mContext = getActivity();

        mActivityHandler = mMainActivity.getHandler();
        LOG.D(TAG,"getFragmentManager().getBackStackEntryCount() = " + getFragmentManager().getBackStackEntryCount());
        if(getFragmentManager().getBackStackEntryCount() > 0){
            mActivityHandler.sendEmptyMessageDelayed(Constants.BACK_TO_INDEX_PAGE, DeviceUtils.PAGE_DELAYED_TIME);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LOG.V(TAG, "[onCreateView] ");

        if (mView == null) {
            mView = inflater.inflate(R.layout.choose_module_fragment, null);
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
        mLayoutEmployee = (LinearLayout) mView.findViewById(R.id.layout_employee);
        mLayoutVisitor = (LinearLayout) mView.findViewById(R.id.layout_visitor);

        mLayoutEmployee.setOnClickListener(mLayoutEmployeeClickListener);
        mLayoutVisitor.setOnClickListener(mLayoutVisitorClickListener);

    }


    private Button.OnClickListener mLayoutEmployeeClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            LOG.D(TAG,"mLayoutEmployeeClickListener DeviceUtils.mEmployeeModel = " + DeviceUtils.mEmployeeModel);
            //check mode number
            if(DeviceUtils.mEmployeeModel != null){
                ClockUtils.mModule = DeviceUtils.mEmployeeModel.getModules();
                ClockUtils.mRoleModel = DeviceUtils.mEmployeeModel;
                if(DeviceUtils.mEmployeeModel.getModes().length > 1){
                    launchFragment(Constants.FRAGMENT_TAG_CHOOSE_MODE, false);
                }else{
                    //check which mode fragment to go
                    switch (DeviceUtils.mEmployeeModel.getModes()[0]){
                        case Constants.MODES_SECURITY_CODE :
                            ClockUtils.mMode = Constants.MODES_SECURITY_CODE;
//                        launchFragment(Constants.FRAGMENT_TAG_PIN_CODE, false);
                            launchFragment(Constants.FRAGMENT_TAG_ENGLISH_PIN_CODE, false);
                            break;
                        case Constants.MODES_FACE_ICON :
                            ClockUtils.mMode = Constants.MODES_FACE_ICON;
                            launchFragment(Constants.FRAGMENT_TAG_FACE_ICON, false);
                            break;
                        case Constants.MODES_FACE_IDENTIFICATION :
                            ClockUtils.mMode = Constants.MODES_FACE_IDENTIFICATION;
                            launchFragment(Constants.FRAGMENT_TAG_FACE_IDENTIFICATION, false);
                            break;
                        case Constants.MODES_QR_CODE :
                            ClockUtils.mMode = Constants.MODES_QR_CODE;
                            launchFragment(Constants.FRAGMENT_TAG_QR_CODE,false);
                            break;
                        case Constants.MODES_RFID :
                            ClockUtils.mMode = Constants.MODES_RFID;
                            launchFragment(Constants.FRAGMENT_TAG_RFID,false);
                            break;
                    }
                }
            }




        }
    };

    private Button.OnClickListener mLayoutVisitorClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            LOG.D(TAG,"mLayoutVisitorClickListener DeviceUtils.mVisitorModel = " + DeviceUtils.mVisitorModel);
            if(DeviceUtils.mVisitorModel != null){
                //check mode number
                ClockUtils.mModule = DeviceUtils.mVisitorModel.getModules();
                ClockUtils.mRoleModel = DeviceUtils.mVisitorModel;
                if(DeviceUtils.mVisitorModel.getModes().length > 1){

                    launchFragment(Constants.FRAGMENT_TAG_CHOOSE_MODE, false);
                }else{

                    //launchFragment(Constants.FRAGMENT_TAG_CHOOSE_MODE, false);
                    //check which mode fragment to go
                    switch (DeviceUtils.mVisitorModel.getModes()[0]){
                        case Constants.MODES_SECURITY_CODE :
                            ClockUtils.mMode = Constants.MODES_SECURITY_CODE;
//                        launchFragment(Constants.FRAGMENT_TAG_PIN_CODE, false);
                            launchFragment(Constants.FRAGMENT_TAG_ENGLISH_PIN_CODE, false);
                            break;
                        case Constants.MODES_FACE_ICON :
                            ClockUtils.mMode = Constants.MODES_FACE_ICON;
                            launchFragment(Constants.FRAGMENT_TAG_FACE_ICON, false);
                            break;
                        case Constants.MODES_FACE_IDENTIFICATION :
                            ClockUtils.mMode = Constants.MODES_FACE_IDENTIFICATION;
                            launchFragment(Constants.FRAGMENT_TAG_FACE_IDENTIFICATION, false);
                            break;
                    }
                }
            }

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

            case Constants.FRAGMENT_TAG_CHOOSE_MODE:
                mChooseModeFragment = new ChooseModeFragment();
                ft.replace(Constants.CONTENT_FRAME_ID, mChooseModeFragment, ChooseModeFragment.TAG).addToBackStack(ChooseModeFragment.TAG).commitAllowingStateLoss();
                break;

            case Constants.FRAGMENT_TAG_PIN_CODE:
//                mPinCodeFragment = new PinCodeFragment();
//
//                ft.replace(Constants.CONTENT_FRAME_ID, mPinCodeFragment, PinCodeFragment.TAG).addToBackStack(PinCodeFragment.TAG).commitAllowingStateLoss();
                break;

            case Constants.FRAGMENT_TAG_ENGLISH_PIN_CODE:

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

            case Constants.FRAGMENT_TAG_QR_CODE:
                mQrCodeFragment = new QrCodeFragment();

                ft.replace(Constants.CONTENT_FRAME_ID, mQrCodeFragment, QrCodeFragment.TAG).addToBackStack(QrCodeFragment.TAG).commitAllowingStateLoss();
                break;

            case Constants.FRAGMENT_TAG_RFID:
                mRFIDFragment = new RFIDFragment();

                ft.replace(Constants.CONTENT_FRAME_ID, mRFIDFragment, RFIDFragment.TAG).addToBackStack(RFIDFragment.TAG).commitAllowingStateLoss();
                break;

        }

    }


}
