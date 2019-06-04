package com.gorilla.attendance.enterprise.Dialolg;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.gorilla.attendance.enterprise.R;
import com.gorilla.attendance.enterprise.util.LOG;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ggshao on 2017/2/15.
 */

public class ImageDialog extends PopupDialog implements DialogInterface.OnDismissListener{

    /*
     * ======================================================================
     * Constant Fields
     * =======================================================================
     */
    public static final String TAG = "ImageDialog";
    private Context mContext;

    private ImageView mImgPhoto;
    private TextView mTxtMessage = null;
    private TextView mTxtTime = null;

    private Handler mCallback = null;

    public ImageDialog(Context context, int imageId, String message, long time, Handler callback) {
        super(context);

        this.mContext = context;
        mCallback = callback;
        setContentView(R.layout.image_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        mImgPhoto = (ImageView) this.findViewById(R.id.img_photo);
        mTxtMessage = (TextView) this.findViewById(R.id.txt_message);
        mTxtTime = (TextView) this.findViewById(R.id.txt_time);

        mImgPhoto.setBackgroundResource(imageId);
        mTxtMessage.setText(message);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String dateString = sdf.format(new Date(time));

        mTxtTime.setText(dateString);

    }


    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        LOG.D(TAG,"onDismiss");



    }


}
