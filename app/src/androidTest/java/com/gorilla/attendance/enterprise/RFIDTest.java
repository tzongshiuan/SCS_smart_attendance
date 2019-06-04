package com.gorilla.attendance.enterprise;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.test.InstrumentationTestRunner;

import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.LOG;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by ggshao on 2018/3/12.
 */

public class RFIDTest extends InstrumentationTestRunner {
    private static final String TAG = "ApiTest";

    private Context mContext = null;
    private SharedPreferences mSharedPreference = null;

    private DecoratedBarcodeView mBarcodeView = null;


    @Before
    public void init() {
        LOG.D(TAG, "init");
        mContext = InstrumentationRegistry.getTargetContext();
        mSharedPreference = mContext.getSharedPreferences(Constants.PREF_NAME_ATTENDANCE_ENTERPRISE, Context.MODE_PRIVATE);

    }

    @Test
    public void testRFIDSwipe() throws Exception {
        LOG.D(TAG, "testQRCodeScan");
        Assert.assertTrue(true);

    }


    @Test
    public void testRFIDAttendanceSuccess() throws Exception {
        LOG.D(TAG, "testRFIDAttendanceSuccess");
        Assert.assertTrue(true);

    }

    @Test
    public void testRFIDAttendanceFail() throws Exception {
        LOG.D(TAG, "testRFIDAttendanceFail");
        Assert.assertTrue(true);

    }





}
