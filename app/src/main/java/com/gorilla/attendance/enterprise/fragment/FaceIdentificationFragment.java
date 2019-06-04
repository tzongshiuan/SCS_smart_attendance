package com.gorilla.attendance.enterprise.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.gorilla.attendance.enterprise.MainActivity;
import com.gorilla.attendance.enterprise.R;
import com.gorilla.attendance.enterprise.util.ClockUtils;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.DeviceUtils;
import com.gorilla.attendance.enterprise.util.FDRControlManager;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.NetworkManager;

/**
 * Created by ggshao on 2017/2/7.
 */

public class FaceIdentificationFragment extends BaseFragment {
    public static final String TAG = "FaceIdentificationFragment";

    private View mView = null;

    private MainActivity mMainActivity;
    private FragmentActivity mActivity = null;
    private Context mContext = null;

    private NetworkManager mNetworkManager = null;
    private Handler mActivityHandler = null;

    private RelativeLayout mLayoutFdr = null;
    private FrameLayout mFdrFrame = null;

    private MediaPlayer mPlayer = null;
    private boolean mIsStartFdr = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
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

        ClockUtils.mLoginAccount = null;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LOG.V(TAG, "[onCreateView] ");

        if (mView == null) {
            mView = inflater.inflate(R.layout.face_identification_fragment, null);
        } else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
        }

        initView();
//        startFdr();
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
//        mMainActivity.setContentViewLayout(false);
//        mMainActivity.setActionBarTitle(getString(R.string.title_recommend));

//        startFdr();

        if(getFragmentManager().getBackStackEntryCount() > 0){
            mMainActivity.setHomeSettingWord(getString(R.string.txt_home_page));
            mMainActivity.setHomeSettingBackGround(R.mipmap.icon_back_to_home);
        }else{
            mMainActivity.setHomeSettingWord(getString(R.string.txt_home_setting));
            mMainActivity.setHomeSettingBackGround(R.mipmap.icon_back_to_home_setting);
        }

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startFdr();
            }
        }, 50);


    }

    @Override
    public void onStop() {
        LOG.D(TAG,"onStop");
        super.onStop();
        mIsStartFdr = false;

// remove face_too_long
        FDRControlManager.getInstance(mContext).stopFdr(mActivityHandler, mFragmentHandler);
        mFragmentHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
        removeFdrView();

    }

    @Override
    public void onDestroy() {
        LOG.D(TAG,"onDestroy");
        super.onDestroy();

        FDRControlManager.getInstance(mContext).stopFdr(mActivityHandler, mFragmentHandler);
    }

    private void initView(){
        LOG.D(TAG, "initView");

        mLayoutFdr = (RelativeLayout) mView.findViewById(R.id.layout_fdr);
        mFdrFrame = (FrameLayout) mView.findViewById(R.id.fdr_frame);
        mPlayer = MediaPlayer.create(mContext, R.raw.sound1);


    }

    //for RETRY_BUTTON_ID use
    public void startFdr(){
        LOG.D(TAG, "startFdr mIsStartFdr = " + mIsStartFdr );
        if (mIsStartFdr) {
            return;
        }

        mIsStartFdr = true;

        ClockUtils.mLoginAccount = null;
//        LOG.D(TAG, "startFdr mFdrFrame.getChildCount() = " + mFdrFrame.getChildCount());


        if(mLayoutFdr == null){
            return;
        }

//        Calendar calendar = Calendar.getInstance();
//        ClockUtils.mLoginTime = calendar.get(Calendar.SECOND);
        ClockUtils.mLoginTime = System.currentTimeMillis();


        mLayoutFdr.setVisibility(View.VISIBLE);

        LOG.D(TAG, "mFdrFrame.getChildCount() = " + mFdrFrame.getChildCount());

        if(mFdrFrame.getChildCount() > 0){
        }else{
            mFdrFrame.addView(FDRControlManager.getInstance(mContext).getFdrCtrl());
        }


        ClockUtils.mLoginIntId = -1;
        FDRControlManager.getInstance(mContext).startFdr(mActivityHandler, mFragmentHandler);


    }

    public void removeFdrView(){
        if(mFdrFrame != null){
            mFdrFrame.removeAllViews();
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler mFragmentHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LOG.D(TAG,"mHandler msg.what = " + msg.what);
            mIsStartFdr = false;
            switch(msg.what) {
                case Constants.GET_FACE_SUCCESS:

                    mFragmentHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
                    mActivityHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
//                    mLayoutFdr.setVisibility(FrameLayout.GONE);

                    break;
                case Constants.GET_FACE_FAIL:

                    mFragmentHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
                    mActivityHandler.removeMessages(Constants.GET_FACE_TOO_LONG);
//                    mLayoutFdr.setVisibility(FrameLayout.GONE);

                    break;
                case Constants.GET_FACE_TOO_LONG:
//                    FDRControlManager.getInstance(mContext).stopFdr(mHandler);
//                    mFragmentHandler.setVisibility(FrameLayout.GONE);
//                    mLayoutPinCode.setVisibility(View.VISIBLE);
                    break;


            }
        }
    };

}
