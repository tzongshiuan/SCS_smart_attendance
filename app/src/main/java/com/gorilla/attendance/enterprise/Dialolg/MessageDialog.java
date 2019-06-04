package com.gorilla.attendance.enterprise.Dialolg;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.TextView;

import com.gorilla.attendance.enterprise.R;
import com.gorilla.attendance.enterprise.util.LOG;

/**
 * Created by ggshao on 2017/2/13.
 */

public class MessageDialog extends PopupDialog {
    public static final String TAG = "MessageDialog";
    private Context mContext;
    private TextView mTxtDialogMessage = null;

    private boolean mIsNeedToFinishApp = false;
    private Handler mActivityHandler = null;

    public MessageDialog(Context context, String message, boolean isNeedToFinishApp, Handler callback) {
        super(context);

        LOG.D(TAG, "message = " + message);
        this.mContext = context;
        mIsNeedToFinishApp = isNeedToFinishApp;
        mActivityHandler = callback;
        setContentView(R.layout.message_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        mTxtDialogMessage = (TextView) this.findViewById(R.id.txt_dialog_message);
        if(message != null){
            mTxtDialogMessage.setText(message);
        }


    }


    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        LOG.D(TAG,"setOnDismissListener ");
        super.setOnDismissListener(listener);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LOG.D(TAG,"onTouchEvent mIsNeedToFinishApp = " + mIsNeedToFinishApp);

        if(mIsNeedToFinishApp){
//            mActivityHandler.sendEmptyMessage(Constants.CLOSE_APP);
            this.dismiss();
        }

        return super.onTouchEvent(event);


    }
}
