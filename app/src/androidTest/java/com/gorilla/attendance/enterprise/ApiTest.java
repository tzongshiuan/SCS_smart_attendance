package com.gorilla.attendance.enterprise;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.test.InstrumentationTestRunner;

import com.gorilla.attendance.enterprise.util.Constants;
import com.gorilla.attendance.enterprise.util.LOG;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by ggshao on 2018/3/12.
 */

public class ApiTest extends InstrumentationTestRunner {
    private static final String TAG = "ApiTest";

    private Context mContext = null;
    private SharedPreferences mSharedPreference = null;

    @Before
    public void init() {
        LOG.D(TAG, "init");
        mContext = InstrumentationRegistry.getTargetContext();
        mSharedPreference = mContext.getSharedPreferences(Constants.PREF_NAME_ATTENDANCE_ENTERPRISE, Context.MODE_PRIVATE);

    }

    @Test
    public void testApiRFIDEmployeeRegister() throws Exception {
        LOG.D(TAG, "testApiRFIDEmployeeRegister");
        Assert.assertTrue(true);

    }

    @Test
    public void testApiRFIDVisitorRegister() throws Exception {
        LOG.D(TAG, "testApiRFIDVisitorRegister");
        Assert.assertTrue(true);

    }

    @Test
    public void testApiSearchEmployee() throws Exception {
        LOG.D(TAG, "testApiSearchEmployee");
        Assert.assertTrue(true);

    }

    @Test
    public void testApiSearchVisitor() throws Exception {
        LOG.D(TAG, "testApiSearchVisitor");
        Assert.assertTrue(true);

    }



}
