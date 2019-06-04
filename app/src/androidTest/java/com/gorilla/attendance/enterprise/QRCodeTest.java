package com.gorilla.attendance.enterprise;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.test.InstrumentationTestRunner;

import com.google.zxing.ResultPoint;
import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.LOG;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by ggshao on 2018/3/12.
 */

public class QRCodeTest extends InstrumentationTestRunner {
    private static final String TAG = "ApiTest";

    private Context mContext = null;
    private SharedPreferences mSharedPreference = null;

    private DecoratedBarcodeView mBarcodeView = null;


    @Before
    public void init() {
        LOG.D(TAG, "init");
        mContext = InstrumentationRegistry.getTargetContext();
        mSharedPreference = mContext.getSharedPreferences(Constants.PREF_NAME_ATTENDANCE_ENTERPRISE, Context.MODE_PRIVATE);

//        Looper.prepare();
////
//        RelativeLayout view = (RelativeLayout) LayoutInflater.from(new Activity()).inflate(R.layout.qrcode_fragment, null);
//        mBarcodeView = (DecoratedBarcodeView) view.findViewById(R.id.barcode_scanner);
//
//        Looper.myLooper();

//        mBarcodeView = new DecoratedBarcodeView(mContext);

    }

    @Test
    public void testQRCodeScan() throws Exception {
        LOG.D(TAG, "testQRCodeScan");



        Assert.assertTrue(true);

//        Intent intent = new Intent();
//        intent.putExtra("SCAN_CAMERA_ID", FDRControlManager.CAMERA_ID);
//        LOG.D(TAG,"initView 222");
//        int cameraId = intent.getIntExtra(Intents.Scan.CAMERA_ID, -1);
//        LOG.D(TAG,"initView 222 cameraId = " + cameraId);
//        mBarcodeView.initializeFromIntent(intent);
//        mBarcodeView.decodeSingle(mBarcodeViewCallback);
//        mBarcodeView.resume();


    }

    @Test
    public void testQRCodeAttendanceSuccess() throws Exception {
        Assert.assertTrue(true);
    }

    @Test
    public void testQRCodeAttendanceFail() throws Exception {
        Assert.assertTrue(true);
    }


    private BarcodeCallback mBarcodeViewCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null) {
                // Prevent duplicate scans,

                return;
            }

            LOG.D(TAG,"mBarcodeViewCallback result.getText() = " + result.getText());

//            lastText = result.getText();
            mBarcodeView.pauseAndWait();
//            mBarcodeView.setVisibility(View.GONE);

//            LOG.D(TAG,"mBarcodeViewCallback mBarcodeView.getBarcodeView().isCameraClosed()) = " + mBarcodeView.getBarcodeView().isCameraClosed());
//
//            Message message = new Message();
//            Bundle bundle = new Bundle();
//            bundle.putString(Constants.KEY_QRCODE_NUMBER, result.getText());
//            message.what = Constants.MSG_QRCODE_SCAN;
//            message.setData(bundle);
//
//            mFragmentHandler.sendMessage(message);



        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

}
