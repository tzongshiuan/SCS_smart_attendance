package com.gorilla.attendance.enterprise.fragment;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.gorilla.attendance.enterprise.MainActivity;
import com.gorilla.attendance.enterprise.R;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.DeviceUtils;
import com.gorilla.attendance.enterprise.util.EnterpriseUtils;
import com.gorilla.attendance.enterprise.util.FDRControlManager;
import com.gorilla.attendance.enterprise.util.LOG;
import com.gorilla.attendance.enterprise.util.NetworkManager;

import java.io.File;

/**
 * Created by ggshao on 2017/2/13.
 */

public class VideoFragment extends BaseFragment {
    public static final String TAG = "VideoFragment";

    private View mView = null;

    private MainActivity mMainActivity;
    private FragmentActivity mActivity = null;
    private Context mContext = null;

    private NetworkManager mNetworkManager = null;
    private Handler mActivityHandler = null;


    private MediaPlayer mMediaPlayer = null;
    private SurfaceView surfaceView = null;
    private SurfaceHolder surfaceHolder = null;

    private ViewGroup mSurfaceLayout = null;

    private int mVideoWidth = 0;
    private int mVideoHeight = 0;
    private int mVideo = -1;

    private int mDuration = 0;
    private static String mVideoFilePath = null;

    private RelativeLayout mLayoutFdr = null;
    private FrameLayout mFdrFrame = null;
    private boolean mIsStartFdr = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mMainActivity = (MainActivity) getActivity();
        mContext = getActivity();
        mActivityHandler = mMainActivity.getHandler();

        setRetainInstance(true);//set to true, orientation change will not call here


        new Thread(new Runnable() {
            @Override
            public void run() {
                getVideoFilePath(false);
            }
        }).start();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LOG.V(TAG, "[onCreateView] mView = " + mView);

        if (mView == null) {
            mView = inflater.inflate(R.layout.video_fragment, null);
            mView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {

                    LOG.D(TAG, "onTouch event.getAction() = " + event.getAction());
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (mIsStartFdr == true) {
                            FDRControlManager.getInstance(mContext).stopFdr();
                            mIsStartFdr = false;
                            mFdrFrame.removeAllViews();
                        }
                        mActivityHandler.sendEmptyMessage(Constants.CLOSE_VIDEO);
                    }
                    return true;
                }
            });
        } else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
        }

//        initView(mView);

        initView();

//        if(DeviceUtils.mIsClockAuto == true){
//            Handler handler=new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                    startFdr();
//                }
//            }, 10000);
//        }


        return mView;
    }

    @Override
    public void onPause() {
        LOG.D(TAG, "onPause");
        super.onPause();
        destroyMediaPlayer();

        //face detection out use
//        if(DeviceUtils.mIsClockAuto){
//            FDRControlManager.getInstance(mContext).stopFdr();
//            mFdrFrame.removeAllViews();
//            mIsStartFdr = false;
//        }


    }

    @Override
    public void onResume() {
        LOG.D(TAG, "onResume getFragmentManager().getBackStackEntryCount() = " + getFragmentManager().getBackStackEntryCount());
        super.onResume();
//        mMainActivity.setContentViewLayout(false);
//        mMainActivity.setActionBarTitle(getString(R.string.title_recommend));

        if (FDRControlManager.getInstance(mContext).getFdrCtrl() != null) {
            FDRControlManager.getInstance(mContext).getFdrCtrl().hideFaceView(true);
        }

    }

    @Override
    public void onStop() {
        LOG.D(TAG, "onStop");
        super.onStop();

        if (FDRControlManager.getInstance(mContext).getFdrCtrl() != null) {
            FDRControlManager.getInstance(mContext).getFdrCtrl().hideFaceView(false);
        }

    }

    @Override
    public void onDestroy() {
        LOG.D(TAG, "onDestroy");
        super.onDestroy();
        ((MainActivity) mActivity).onLaunchHomeFragment();
    }

    private void initView() {
        LOG.D(TAG, "initView");

        mLayoutFdr = (RelativeLayout) mView.findViewById(R.id.layout_fdr);
        mFdrFrame = (FrameLayout) mView.findViewById(R.id.fdr_frame);

    }

    private void getVideoFilePath(boolean isComplete) {

        boolean allVideoExist = true;
        LOG.D(TAG, "mVideoList.size() = " + DeviceUtils.mVideoList.size());
        if(DeviceUtils.mVideoList.size()==0){
            mActivityHandler.removeMessages(Constants.CLOSE_VIDEO);
            mActivityHandler.sendEmptyMessage(Constants.CLOSE_VIDEO);
            return;
        }

        for (int i = 0; i <  DeviceUtils.mVideoList.size(); i++) {
            LOG.D(TAG, "DeviceUtils.mVideoList.get(i).getFileName() - " + DeviceUtils.mVideoList.get(i).getFilename());
            File file = new File(EnterpriseUtils.SD_CARD_APP_CONTENT + "/" + DeviceUtils.mVideoList.get(i).getFilename());
            if (!file.exists()) {
                allVideoExist = false;
            }
        }
        if (allVideoExist) {
            //if there's no video, will be black screen
            mVideo++;
            int i = mVideo % DeviceUtils.mVideoList.size();
            mVideoFilePath = EnterpriseUtils.SD_CARD_APP_CONTENT + "/" + DeviceUtils.mVideoList.get(i).getFilename();

            if (isComplete == false) {
                //VideoPath done
                if (mInnerHandler != null) {
                    Message msg = mInnerHandler.obtainMessage(Constants.VIDEO_PATH_DONE);
                    //mHandler.removeMessages(Constants.SHOW_MEDIAPLAYER);
                    mInnerHandler.sendMessage(msg);
                } else {
                    LOG.V(TAG, "not queue refresh due to mInnerHandler:" + mInnerHandler);
                }
            }
        } else {
            //black screen
            mActivityHandler.removeMessages(Constants.CLOSE_VIDEO);
            mActivityHandler.sendEmptyMessage(Constants.CLOSE_VIDEO);
        }


    }


    private void createMediaPlayer() {
        LOG.D(TAG, "createMediaPlayer mVideoFilePath = " + mVideoFilePath);
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mVideoFilePath);
            mMediaPlayer.prepareAsync();
//        		mMediaPlayer.start();

            mMediaPlayer.setOnPreparedListener(onPreparedListener);

            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    LOG.E(TAG, "ErrorCode : " + what);
                    mActivityHandler.sendEmptyMessage(Constants.CLOSE_VIDEO);

                    return false;
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    try {
                        LOG.D(TAG, "onCompletion");
                        if (mMediaPlayer != null) {
                            //play next
                            mMediaPlayer.stop();
                            mMediaPlayer.reset();

                            getVideoFilePath(true);
//                            getVideoFilePathComplete();

                            LOG.D(TAG, "onCompletion mVideoFilePath = " + mVideoFilePath);
                            mMediaPlayer.setDataSource(mVideoFilePath);
                            mMediaPlayer.prepareAsync();
                        }

                    } catch (Exception e) {
                        LOG.E(TAG, e.getMessage(), e);
                        mActivityHandler.sendEmptyMessage(Constants.CLOSE_VIDEO);

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void destroyMediaPlayer() {
        LOG.I(TAG, "destroyMediaPlayer");
//            mSeekBarPlayer.removeCallbacks(onCheckPptEverySecond);

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

    }

    private void initViewer() {

        mSurfaceLayout = (ViewGroup) mView.findViewById(R.id.surface_layout);
        mSurfaceLayout.removeAllViews();

        surfaceView = new SurfaceView(getActivity());

        //surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        LOG.D(TAG, "mMediaPlayer =  " + mMediaPlayer);

        surfaceView.setLayoutParams(layoutParams);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setFixedSize(100, 100);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    LOG.D(TAG, "surfaceCreated ");
                    mMediaPlayer.setDisplay(holder);

                } catch (Exception e) {
                    LOG.E(TAG, e.getMessage(), e);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {

                LOG.D(TAG, "surfaceChanged holder = " + holder);
                LOG.D(TAG, "surfaceChanged format = " + format);
                LOG.D(TAG, "surfaceChanged width = " + width);
                LOG.D(TAG, "surfaceChanged height = " + height);
                LOG.D(TAG, "surfaceChanged  mSurfaceLayout.getMeasuredHeight() =  " + mSurfaceLayout.getMeasuredHeight());
                LOG.D(TAG, "surfaceChanged  mSurfaceLayout.getMeasuredWidth() =  " + mSurfaceLayout.getMeasuredWidth());
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                LOG.D(TAG, "surfaceDestroyed");
                if (mMediaPlayer != null) {
                    mMediaPlayer.setDisplay(null);
                }
            }

        });

        mSurfaceLayout.addView(surfaceView);

    }


    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {

            LOG.D(TAG, "onPreparedListener");
            if (mMediaPlayer != null) {
                mDuration = mMediaPlayer.getDuration();
                mVideoWidth = mMediaPlayer.getVideoWidth();
                mVideoHeight = mMediaPlayer.getVideoHeight();

                LOG.D(TAG, "onPreparedListener mVideoWidth = " + mVideoWidth);
                LOG.D(TAG, "onPreparedListener mVideoHeight = " + mVideoHeight);

                if (mMediaPlayer != null) {
                    mMediaPlayer.start();
                }

            }

        }
    };

    private Handler mInnerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LOG.D(TAG, "msg.what = " + msg.what);
            switch (msg.what) {
                case Constants.VIDEO_PATH_DONE:
                    initViewer();
                    createMediaPlayer();

                    break;
            }
        }
    };

    private void startFdr() {
        LOG.D(TAG, "startFdr");

        mFdrFrame.removeAllViews();

//        FDRControlManager.getInstance(mContext).getFdrCtrl().setLayoutParams(new ViewGroup.LayoutParams(
//                1, 1));
//        FDRControlManager.getInstance(mContext).getFdrCtrl().getLayoutParams().height = 1;
//        mFdrFrame.getLayoutParams().width = 1;
//        mFdrFrame.getLayoutParams().height = 1;

//        mFdrFrame.setVisibility(View.INVISIBLE);
//        mLayoutFdr.setVisibility(FrameLayout.INVISIBLE);
//        FDRControlManager.getInstance(mContext).getFdrCtrl().setCameraVisibility(View.INVISIBLE);
//        FDRControlManager.getInstance(mContext).getFdrCtrl().setFaceVisibility(View.INVISIBLE);
        mFdrFrame.addView(FDRControlManager.getInstance(mContext).getFdrCtrl());
//        FDRControlManager.getInstance(mContext).getFdrCtrl().setVisibility(View.INVISIBLE);

        FDRControlManager.getInstance(mContext).startFdrForVideoFragment();
        mIsStartFdr = true;

    }


    public void closeVideo() {
        LOG.D(TAG, "closeVideo");
        FDRControlManager.getInstance(mContext).stopFdr();
        mIsStartFdr = false;
        mFdrFrame.removeAllViews();
        mActivityHandler.sendEmptyMessage(Constants.CLOSE_VIDEO);
    }


}
