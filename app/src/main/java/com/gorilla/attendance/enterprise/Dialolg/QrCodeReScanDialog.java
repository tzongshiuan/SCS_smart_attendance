package com.gorilla.attendance.enterprise.Dialolg;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gorilla.attendance.enterprise.R;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.LOG;

/**
 * Created by ggshao on 2018/3/5.
 */

public class QrCodeReScanDialog extends PopupDialog {
    public static final String TAG = "QrCodeReScanDialog";
    private Context mContext;
    private TextView mTxtDialogMessage = null;
    private Button mBtnCancel = null;
    private Button mBtnReScan = null;

    private Handler mActivityHandler = null;

    public QrCodeReScanDialog(Context context, String message, Handler callback) {
        super(context);

        LOG.D(TAG, "message = " + message);
        this.mContext = context;
        mActivityHandler = callback;
        setContentView(R.layout.qrcode_rescan_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        mTxtDialogMessage = (TextView) this.findViewById(R.id.txt_title);
        mBtnCancel =  (Button) this.findViewById(R.id.btn_cancel);
        mBtnReScan =  (Button) this.findViewById(R.id.btn_rescan);


        if(message != null){
            mTxtDialogMessage.setText(message);
        }

        mBtnCancel.setOnClickListener(mBtnCancelClickListener);
        mBtnReScan.setOnClickListener(mBtnReScanClickListener);


    }


    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        LOG.D(TAG,"setOnDismissListener ");
        super.setOnDismissListener(listener);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LOG.D(TAG,"onTouchEvent = ");

        return super.onTouchEvent(event);


    }

    private Button.OnClickListener mBtnCancelClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            LOG.D(TAG, "mBtnCancelClickListener");
            dismiss();
            mActivityHandler.sendEmptyMessage(Constants.MSG_QRCODE_CANCEL);
        }
    };

    private Button.OnClickListener mBtnReScanClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            LOG.D(TAG, "mBtnReScanClickListener");
            dismiss();
            mActivityHandler.sendEmptyMessage(Constants.MSG_QRCODE_RESCAN);
        }
    };
}
