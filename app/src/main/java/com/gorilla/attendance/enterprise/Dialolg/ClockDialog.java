package com.gorilla.attendance.enterprise.Dialolg;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gorilla.attendance.enterprise.R;
import com.gorilla.attendance.enterprise.util.ClockUtils;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.EnterpriseUtils;
import com.gorilla.attendance.enterprise.util.LOG;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import static android.view.View.VISIBLE;

/**
 * Created by ggshao on 2017/2/12.
 */

public class ClockDialog extends PopupDialog implements DialogInterface.OnDismissListener{

    /*
     * ======================================================================
     * Constant Fields
     * =======================================================================
     */
    public static final String TAG = "ClockDialog";
    public static final int CLOCK_IN_BUTTON_ID = 101;
    public static final int CLOCK_OUT_BUTTON_ID = 102;
    public static final int OPEN_DOOR_BUTTON_ID = 103;
    public static final int ARRIVAL_BUTTON_ID = 104;
    public static final int LEAVE_BUTTON_ID = 105;
    public static final int CLOCK_IN_DOOR_BUTTON_ID = 106;
    public static final int CLOCK_OUT_DOOR_BUTTON_ID = 107;
    public static final int BACK_TO_INDEX_BUTTON_ID = 108;
    public static final int RETRY_BUTTON_ID = 109;
    public static final int TOUCH_CLOSE_BUTTON_ID = 110;
    public static final int VISITOR_ONE_PASSWORD_BUTTON_ID = 111;

    private Context mContext;
    private Button mClockInButton;
    private Button mClockOutButton;
    private Button mOpenDoorButton;
    private ImageView mImgPhoto;

    private TextView mTxtLoginName = null;
    private TextView mTxtLoginId = null;
    private TextView mTxtLoginTime = null;
    private TextView mTxtDescription = null;


    private LinearLayout mLayoutLoginName = null;
    private LinearLayout mLayoutLoginId = null;
    private LinearLayout mLayoutLoginFail = null;


    private int mClickButtonId = 0;

    private SharedPreferences mSharedPreference = null;

    public ClockDialog(Context context, int module, int mode, boolean isRadioClockIn, boolean isRadioClockOut, boolean isShowDailDescription){
        super(context);
        LOG.D(TAG,"auto clock module = " + module);
        this.mContext = context;
        setContentView(R.layout.clock_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mSharedPreference = mContext.getSharedPreferences(Constants.PREF_NAME_ATTENDANCE_ENTERPRISE, Context.MODE_PRIVATE);

        mImgPhoto = (ImageView) this.findViewById(R.id.img_login_photo);

        mClockInButton = (Button) this.findViewById(R.id.btn_clock_in);
        mClockOutButton = (Button) this.findViewById(R.id.btn_clock_out);
        mOpenDoorButton = (Button) this.findViewById(R.id.btn_open_door);

        mTxtLoginName = (TextView) this.findViewById(R.id.txt_login_name);
        mTxtLoginId = (TextView) this.findViewById(R.id.txt_login_id);
        mTxtLoginTime = (TextView) this.findViewById(R.id.txt_login_time);
        mTxtDescription = (TextView) this.findViewById(R.id.txt_description);

        mLayoutLoginName = (LinearLayout) this.findViewById(R.id.layout_login_name);
        mLayoutLoginId = (LinearLayout) this.findViewById(R.id.layout_login_id);
        mLayoutLoginFail = (LinearLayout) this.findViewById(R.id.layout_login_fail);


        Bitmap bMap = BitmapFactory.decodeByteArray(EnterpriseUtils.mFacePngList.get(0), 0, EnterpriseUtils.mFacePngList.get(0).length);
        mImgPhoto.setImageBitmap(bMap);
        mImgPhoto.setVisibility(VISIBLE);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String dateString = sdf.format(new Date(ClockUtils.mLoginTime));
        mTxtLoginTime.setText(dateString);

        setOnDismissListener(this);

        if(isShowDailDescription) {
            mTxtDescription.setVisibility(View.VISIBLE);
        }else{
            mTxtDescription.setVisibility(View.GONE);
        }


        mClockInButton.setVisibility(View.GONE);
        mClockOutButton.setVisibility(View.GONE);
        mOpenDoorButton.setVisibility(View.GONE);


        mTxtLoginName.setText(ClockUtils.mLoginName);
        mTxtLoginId.setText(ClockUtils.mLoginAccount);


        mLayoutLoginName.setVisibility(VISIBLE);
        mLayoutLoginId.setVisibility(VISIBLE);
        mLayoutLoginFail.setVisibility(View.GONE);


    }



    public ClockDialog(Context context, final int module, boolean isFaceVerifySuccess, boolean isShowDailDescription) {
        super(context);

        LOG.D(TAG,"module = " + module);
        this.mContext = context;
        setContentView(R.layout.clock_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mSharedPreference = mContext.getSharedPreferences(Constants.PREF_NAME_ATTENDANCE_ENTERPRISE, Context.MODE_PRIVATE);

        mImgPhoto = (ImageView) this.findViewById(R.id.img_login_photo);

        mClockInButton = (Button) this.findViewById(R.id.btn_clock_in);
        mClockOutButton = (Button) this.findViewById(R.id.btn_clock_out);
        mOpenDoorButton = (Button) this.findViewById(R.id.btn_open_door);

        mTxtLoginName = (TextView) this.findViewById(R.id.txt_login_name);
        mTxtLoginId = (TextView) this.findViewById(R.id.txt_login_id);
        mTxtLoginTime = (TextView) this.findViewById(R.id.txt_login_time);
        mTxtDescription = (TextView) this.findViewById(R.id.txt_description);

        mLayoutLoginName = (LinearLayout) this.findViewById(R.id.layout_login_name);
        mLayoutLoginId = (LinearLayout) this.findViewById(R.id.layout_login_id);
        mLayoutLoginFail = (LinearLayout) this.findViewById(R.id.layout_login_fail);


        Bitmap bMap = BitmapFactory.decodeByteArray(EnterpriseUtils.mFacePngList.get(0), 0, EnterpriseUtils.mFacePngList.get(0).length);
        mImgPhoto.setImageBitmap(bMap);
        mImgPhoto.setVisibility(VISIBLE);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String dateString = sdf.format(new Date(ClockUtils.mLoginTime));
        mTxtLoginTime.setText(dateString);

        setOnDismissListener(this);

        if(isShowDailDescription) {
            mTxtDescription.setVisibility(View.VISIBLE);
        }else{
            mTxtDescription.setVisibility(View.GONE);
        }

        if(isFaceVerifySuccess){
            mTxtLoginName.setText(ClockUtils.mLoginName);
            mTxtLoginId.setText(ClockUtils.mLoginAccount);


            mLayoutLoginName.setVisibility(VISIBLE);
            mLayoutLoginId.setVisibility(VISIBLE);
            mLayoutLoginFail.setVisibility(View.GONE);

            switch(module){
                case (Constants.MODULES_ATTENDANCE):
                    mClockInButton.setVisibility(VISIBLE);
                    mClockOutButton.setVisibility(VISIBLE);
                    mOpenDoorButton.setVisibility(View.GONE);

                    mClockInButton.setText(mContext.getString(R.string.txt_clock_in));
                    mClockOutButton.setText(mContext.getString(R.string.txt_clock_out));

                    break;
                case (Constants.MODULES_ACCESS):
                    mClockInButton.setVisibility(View.GONE);
                    mClockOutButton.setVisibility(View.GONE);
                    mOpenDoorButton.setVisibility(View.GONE);

                    break;
                case (Constants.MODULES_ATTENDANCE_ACCESS):
                    mClockInButton.setVisibility(VISIBLE);
                    mClockOutButton.setVisibility(VISIBLE);
                    mOpenDoorButton.setVisibility(VISIBLE);

                    mClockInButton.setText(mContext.getString(R.string.txt_clock_in));
                    mClockOutButton.setText(mContext.getString(R.string.txt_clock_out));
                    mOpenDoorButton.setText(mContext.getString(R.string.txt_open_door));

                    break;
                case (Constants.MODULES_VISITORS):
                    mClockInButton.setVisibility(VISIBLE);
                    mClockOutButton.setVisibility(VISIBLE);
                    mOpenDoorButton.setVisibility(View.GONE);

                    mClockInButton.setText(mContext.getString(R.string.txt_arrival));
                    mClockOutButton.setText(mContext.getString(R.string.txt_leave));

                    break;
            }


            mClockInButton.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent mv) {
                    if(mv.getX() < 0 || mv.getX() > view.getWidth() || mv.getY() < 0 || mv.getY() > view.getHeight())
                    {
//                                mClockInButton.setBackgroundResource(R.mipmap.btn_dialog_normal);
                        return false;
                    }

                    switch(mv.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
//                                    mClockInButton.setBackgroundResource(R.mipmap.btn_dialog_active);

                            return true;
                        case MotionEvent.ACTION_UP:
//                                    mClockInButton.setBackgroundResource(R.mipmap.btn_dialog_normal);


                            switch(module){
                                case (Constants.MODULES_ATTENDANCE):
                                    mClickButtonId = CLOCK_IN_BUTTON_ID;
                                    break;
                                case (Constants.MODULES_ACCESS):

                                    break;
                                case (Constants.MODULES_ATTENDANCE_ACCESS):
                                    mClickButtonId = CLOCK_IN_DOOR_BUTTON_ID;
                                    break;
                                case (Constants.MODULES_VISITORS):
                                    mClickButtonId = ARRIVAL_BUTTON_ID;

                                    break;
                            }

                            mClockButtonClickListener.onClick(view);
                            return true;
                    }
                    return false;
                }

            });



            mClockOutButton.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent mv) {
                    if(mv.getX() < 0 || mv.getX() > view.getWidth() || mv.getY() < 0 || mv.getY() > view.getHeight())
                    {
//                                mClockOutButton.setBackgroundResource(R.mipmap.btn_dialog_normal);
                        return false;
                    }

                    switch(mv.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
//                                    mClockOutButton.setBackgroundResource(R.mipmap.btn_dialog_active);

                            return true;
                        case MotionEvent.ACTION_UP:
//                                    mClockOutButton.setBackgroundResource(R.mipmap.btn_dialog_normal);


                            switch(module){
                                case (Constants.MODULES_ATTENDANCE):
                                    mClickButtonId = CLOCK_OUT_BUTTON_ID;
                                    break;
                                case (Constants.MODULES_ACCESS):

                                    break;
                                case (Constants.MODULES_ATTENDANCE_ACCESS):
                                    mClickButtonId = CLOCK_OUT_DOOR_BUTTON_ID;
                                    break;
                                case (Constants.MODULES_VISITORS):
                                    mClickButtonId = LEAVE_BUTTON_ID;

                                    break;
                            }

                            mClockButtonClickListener.onClick(view);
                            return true;
                    }
                    return false;
                }

            });


            mOpenDoorButton.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent mv) {
                    if(mv.getX() < 0 || mv.getX() > view.getWidth() || mv.getY() < 0 || mv.getY() > view.getHeight())
                    {
//                                mOpenDoorButton.setBackgroundResource(R.mipmap.btn_dialog_normal);
                        return false;
                    }

                    switch(mv.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
//                                    mOpenDoorButton.setBackgroundResource(R.mipmap.btn_dialog_active);

                            return true;
                        case MotionEvent.ACTION_UP:
//                                    mOpenDoorButton.setBackgroundResource(R.mipmap.btn_dialog_normal);


                            LOG.D(TAG,"mOpenDoorButton module = " + module);
                            switch(module){
                                case (Constants.MODULES_ATTENDANCE):
//                                    mClickButtonId = CLOCK_OUT_BUTTON_ID;
                                    break;
                                case (Constants.MODULES_ACCESS):

                                    break;
                                case (Constants.MODULES_ATTENDANCE_ACCESS):
                                    mClickButtonId = OPEN_DOOR_BUTTON_ID;
                                    break;
                                case (Constants.MODULES_VISITORS):
                                    mClickButtonId = VISITOR_ONE_PASSWORD_BUTTON_ID;

                                    break;
                            }


                            mClockButtonClickListener.onClick(view);
                            return true;
                    }
                    return false;
                }

            });




        }else{
            //Fail
            mLayoutLoginName.setVisibility(View.GONE);
            mLayoutLoginId.setVisibility(View.GONE);
            mLayoutLoginFail.setVisibility(VISIBLE);

            switch(module){
                case (Constants.MODULES_ATTENDANCE):
                    mClockInButton.setVisibility(View.VISIBLE);
                    mClockOutButton.setVisibility(View.VISIBLE);
                    mOpenDoorButton.setVisibility(View.GONE);

                    mClockInButton.setText(mContext.getString(R.string.txt_back_to_index));
                    mClockOutButton.setText(mContext.getString(R.string.txt_retry));

                    break;
                case (Constants.MODULES_ACCESS):
                    mClockInButton.setVisibility(VISIBLE);
                    mClockOutButton.setVisibility(VISIBLE);
                    mOpenDoorButton.setVisibility(View.GONE);

                    mClockInButton.setText(mContext.getString(R.string.txt_back_to_index));
                    mClockOutButton.setText(mContext.getString(R.string.txt_retry));

                    break;
                case (Constants.MODULES_ATTENDANCE_ACCESS):
                    mClockInButton.setVisibility(VISIBLE);
                    mClockOutButton.setVisibility(VISIBLE);
                    mOpenDoorButton.setVisibility(View.GONE);

                    mClockInButton.setText(mContext.getString(R.string.txt_back_to_index));
                    mClockOutButton.setText(mContext.getString(R.string.txt_retry));

                    break;
                case (Constants.MODULES_VISITORS):
                    mClockInButton.setVisibility(VISIBLE);
                    mClockOutButton.setVisibility(VISIBLE);
                    mOpenDoorButton.setVisibility(View.GONE);

                    //check if open one password

                    mClockInButton.setText(mContext.getString(R.string.txt_back_to_index));
                    mClockOutButton.setText(mContext.getString(R.string.txt_retry));

                    break;
            }


            mClockInButton.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent mv) {
                    if(mv.getX() < 0 || mv.getX() > view.getWidth() || mv.getY() < 0 || mv.getY() > view.getHeight())
                    {
//                                mClockInButton.setBackgroundResource(R.mipmap.btn_dialog_normal);
                        return false;
                    }

                    switch(mv.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
//                                    mClockInButton.setBackgroundResource(R.mipmap.btn_dialog_active);

                            return true;
                        case MotionEvent.ACTION_UP:
//                                    mClockInButton.setBackgroundResource(R.mipmap.btn_dialog_normal);


                            switch(module){
                                case (Constants.MODULES_ATTENDANCE):
                                    mClickButtonId = BACK_TO_INDEX_BUTTON_ID;
                                    break;
                                case (Constants.MODULES_ACCESS):
                                    mClickButtonId = BACK_TO_INDEX_BUTTON_ID;
                                    break;
                                case (Constants.MODULES_ATTENDANCE_ACCESS):
                                    mClickButtonId = BACK_TO_INDEX_BUTTON_ID;
                                    break;
                                case (Constants.MODULES_VISITORS):
                                    mClickButtonId = BACK_TO_INDEX_BUTTON_ID;

                                    break;
                            }

                            mClockButtonClickListener.onClick(view);
                            return true;
                    }
                    return false;
                }

            });



            mClockOutButton.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent mv) {
                    if(mv.getX() < 0 || mv.getX() > view.getWidth() || mv.getY() < 0 || mv.getY() > view.getHeight())
                    {
//                                mClockOutButton.setBackgroundResource(R.mipmap.btn_dialog_normal);
                        return false;
                    }

                    switch(mv.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
//                                    mClockOutButton.setBackgroundResource(R.mipmap.btn_dialog_active);

                            return true;
                        case MotionEvent.ACTION_UP:
//                                    mClockOutButton.setBackgroundResource(R.mipmap.btn_dialog_normal);


                            switch(module){
                                case (Constants.MODULES_ATTENDANCE):
                                    mClickButtonId = RETRY_BUTTON_ID;
                                    break;
                                case (Constants.MODULES_ACCESS):
                                    mClickButtonId = RETRY_BUTTON_ID;
                                    break;
                                case (Constants.MODULES_ATTENDANCE_ACCESS):
                                    mClickButtonId = RETRY_BUTTON_ID;
                                    break;
                                case (Constants.MODULES_VISITORS):
                                    mClickButtonId = RETRY_BUTTON_ID;

                                    break;
                            }

                            mClockButtonClickListener.onClick(view);
                            return true;
                    }
                    return false;
                }

            });

            mOpenDoorButton.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent mv) {
                    if(mv.getX() < 0 || mv.getX() > view.getWidth() || mv.getY() < 0 || mv.getY() > view.getHeight())
                    {
//                                mOpenDoorButton.setBackgroundResource(R.mipmap.btn_dialog_normal);
                        return false;
                    }

                    switch(mv.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
//                                    mOpenDoorButton.setBackgroundResource(R.mipmap.btn_dialog_active);

                            return true;
                        case MotionEvent.ACTION_UP:
//                                    mOpenDoorButton.setBackgroundResource(R.mipmap.btn_dialog_normal);


                            LOG.D(TAG,"mOpenDoorButton module = " + module);
                            switch(module){
                                case (Constants.MODULES_ATTENDANCE):
//                                    mClickButtonId = CLOCK_OUT_BUTTON_ID;
                                    break;
                                case (Constants.MODULES_ACCESS):

                                    break;
                                case (Constants.MODULES_ATTENDANCE_ACCESS):
                                    mClickButtonId = OPEN_DOOR_BUTTON_ID;
                                    break;
                                case (Constants.MODULES_VISITORS):
                                    mClickButtonId = VISITOR_ONE_PASSWORD_BUTTON_ID;

                                    break;
                            }


                            mClockButtonClickListener.onClick(view);
                            return true;
                    }
                    return false;
                }

            });

        }


    }


    private View.OnClickListener mClockButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            LOG.D(TAG,"mClockInButtonClickListener onClick mClickButtonId = " + mClickButtonId);
            //check callBack
            notifyButtonListeners(mClickButtonId, TAG, null);

        }
    };


    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        LOG.D(TAG,"onDismiss");
//        notifyButtonListeners(BACK_TO_INDEX_BUTTON_ID, TAG, null);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LOG.D(TAG,"onTouchEvent event.getAction() = " + event.getAction());
        LOG.D(TAG,"onTouchEvent MotionEvent.ACTION_UP = " + MotionEvent.ACTION_UP);

//        Object objects[] = {"CloseDialog"};
//        notifyButtonListeners(BACK_TO_INDEX_BUTTON_ID, TAG, objects);
        notifyButtonListeners(TOUCH_CLOSE_BUTTON_ID, TAG, null);
        return true;
    }

    // Special area for practicing LeetCode
    class Solution {
        // Expand Around Center
        public String longestPalindrome(String s) {
            if (s == null || s.length() == 0) {
                return "";
            }

            int start = 0, end = 0;
            for (int i = 0; i < s.length(); i++) {
                int len1 = expandAroundCenter(s, i, i);
                int len2 = expandAroundCenter(s, i, i + 1);
                int len = Math.max(len1, len2);

                if (len > (end - start + 1)) {
                    start = i - (len-1)/2;
                    end = i + (len/2);
                }
            }

            return s.substring(start, end + 1);
        }

        private int expandAroundCenter(String s, int left, int right) {
            int L = left;
            int R = right;

            while ((left - 1 > 0) && (right + 1 < s.length())
                    && (s.charAt(left - 1) == s.charAt(right + 1))) {
                L--;
                R++;
            }

            return (R - L - 1);
        }
    }
}
