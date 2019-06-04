package com.gorilla.attendance.enterprise.fragment;

import android.support.v4.app.Fragment;

import java.lang.reflect.Field;

/**
 * Created by ggshao on 2017/2/7.
 */

public class BaseFragment extends Fragment {
    private final String TAG = "BaseFragment";

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field fragmentManager = Fragment.class.getDeclaredField("mFragmentManager");
            fragmentManager.setAccessible(true);
            fragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
