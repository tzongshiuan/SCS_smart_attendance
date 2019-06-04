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

public class ApiParserTest extends InstrumentationTestRunner {
    private static final String TAG = "ApiParserTest";

    private Context mContext = null;
    private SharedPreferences mSharedPreference = null;

    @Before
    public void init() {
        LOG.D(TAG, "init");
        mContext = InstrumentationRegistry.getTargetContext();
        mSharedPreference = mContext.getSharedPreferences(Constants.PREF_NAME_ATTENDANCE_ENTERPRISE, Context.MODE_PRIVATE);

    }

    @Test
    public void testApiRFIDEmployeeRegisterParser() throws Exception {
        LOG.D(TAG, "testApiRFIDEmployeeRegisterParser");

        Assert.assertTrue(true);
    }

    @Test
    public void testApiRFIDVisitorRegisterParser() throws Exception {
        LOG.D(TAG, "testApiRFIDVisitorRegisterParser");

        Assert.assertTrue(true);
    }

    @Test
    public void testApiSearchEmployeeParser() throws Exception {
        LOG.D(TAG, "testApiSearchEmployeeParser");

        Assert.assertTrue(true);
    }

    @Test
    public void testApiSearchVisitorParser() throws Exception {
        LOG.D(TAG, "testApiSearchVisitorParser");

        Assert.assertTrue(true);
    }


}
