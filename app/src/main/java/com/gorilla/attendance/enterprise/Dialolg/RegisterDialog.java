package com.gorilla.attendance.enterprise.Dialolg;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.gorilla.attendance.enterprise.R;
import com.gorilla.attendance.enterprise.util.LOG;

/**
 * Created by ggshao on 2017/5/11.
 */

public class RegisterDialog extends PopupDialog {
    public static final String TAG = "RegisterDialog";
    private Context mContext;
    private TextView mTxtDialogMessage = null;
    private TextView mTxtDialogIdTitle = null;
    private TextView mTxtDialogId = null;
    private TextView mTxtDialogNameTitle = null;
    private TextView mTxtDialogName = null;

    private Handler mActivityHandler = null;

    public RegisterDialog(Context context, String message, String idTitle, String id, String nameTitle, String name, Handler callback) {
        super(context);

        LOG.D(TAG, "message = " + message);
        this.mContext = context;
        mActivityHandler = callback;
        setContentView(R.layout.register_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        mTxtDialogMessage = (TextView) this.findViewById(R.id.txt_message);
        mTxtDialogIdTitle = (TextView) this.findViewById(R.id.txt_id_title);
        mTxtDialogId = (TextView) this.findViewById(R.id.txt_id);
        mTxtDialogNameTitle = (TextView) this.findViewById(R.id.txt_name_title);
        mTxtDialogName = (TextView) this.findViewById(R.id.txt_name);

        mTxtDialogMessage.setText(message);


        if(id != null){
            mTxtDialogIdTitle.setText(idTitle);
            mTxtDialogId.setText(id);
            mTxtDialogNameTitle.setText(nameTitle);
            mTxtDialogName.setText(name);
        }else{
            mTxtDialogIdTitle.setVisibility(View.GONE);
            mTxtDialogId.setVisibility(View.GONE);
            mTxtDialogNameTitle.setVisibility(View.GONE);
            mTxtDialogName.setVisibility(View.GONE);
        }




    }


    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        LOG.D(TAG,"setOnDismissListener ");
        super.setOnDismissListener(listener);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LOG.D(TAG,"onTouchEvent");
        this.dismiss();
        return super.onTouchEvent(event);



    }
}
