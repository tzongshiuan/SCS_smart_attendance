package com.gorilla.attendance.enterprise.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.gorilla.attendance.enterprise.MainActivity;
import com.gorilla.attendance.enterprise.R;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.NetworkManager;

/**
 * Created by ggshao on 2017/2/7.
 */

public class HomeFragment extends BaseFragment {
    public static final String TAG = "HomeFragment";

    private View mView = null;

    private MainActivity mMainActivity;
    private FragmentActivity mActivity = null;
    private Context mContext = null;

    private NetworkManager mNetworkManager = null;

    private RelativeLayout mLayoutPinCode = null;
    private RelativeLayout mLayoutFaceIcon = null;
    private RelativeLayout mLayoutFaceIdentification = null;

    private PinCodeFragment mPinCodeFragment = null;
    private FaceIconFragment mFaceIconFragment = null;
    private FaceIdentificationFragment mFaceIdentificationFragment = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mMainActivity = (MainActivity) getActivity();
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LOG.V(TAG, "[onCreateView] ");

        if (mView == null) {
            mView = inflater.inflate(R.layout.home_fragment, null);
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
        LOG.D(TAG, "initView");

        mLayoutPinCode = (RelativeLayout) mView.findViewById(R.id.layout_pin_code);
        mLayoutFaceIcon = (RelativeLayout) mView.findViewById(R.id.layout_face_icon);
        mLayoutFaceIdentification = (RelativeLayout) mView.findViewById(R.id.layout_face_identification);

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

                mPinCodeFragment = new PinCodeFragment();

//                arguments.putString(WebViewFragment.KEY_WEB_VIEW_URL, webViewUrl);
//                mPinCodeFragment.setArguments(arguments);

                ft.replace(Constants.CONTENT_FRAME_ID, mPinCodeFragment, PinCodeFragment.TAG).addToBackStack(PinCodeFragment.TAG).commitAllowingStateLoss();
                break;

            case Constants.FRAGMENT_TAG_FACE_ICON:

                mFaceIconFragment = new FaceIconFragment();

                ft.replace(Constants.CONTENT_FRAME_ID, mFaceIconFragment, FaceIconFragment.TAG).addToBackStack(FaceIconFragment.TAG).commitAllowingStateLoss();
                break;

            case Constants.FRAGMENT_TAG_FACE_IDENTIFICATION://banner and article

                mFaceIdentificationFragment = new FaceIdentificationFragment();


                ft.replace(Constants.CONTENT_FRAME_ID, mFaceIdentificationFragment, FaceIdentificationFragment.TAG).addToBackStack(FaceIdentificationFragment.TAG).commitAllowingStateLoss();
                break;

        }

    }


    private Button.OnClickListener mLayoutPinCodeClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            LOG.D(TAG,"mLayoutPinCodeClickListener");
            launchFragment(Constants.FRAGMENT_TAG_PIN_CODE, false);

        }
    };

    private Button.OnClickListener mLayoutFaceIconClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            LOG.D(TAG,"mLayoutFaceIconClickListener");
            launchFragment(Constants.FRAGMENT_TAG_FACE_ICON, false);

        }
    };

    private Button.OnClickListener mLayoutFaceIdentificationClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            LOG.D(TAG,"mLayoutFaceIdentificationClickListener");
            launchFragment(Constants.FRAGMENT_TAG_FACE_IDENTIFICATION, false);
        }
    };


}
