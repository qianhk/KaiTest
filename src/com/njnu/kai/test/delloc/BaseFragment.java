package com.njnu.kai.test.delloc;

import android.support.v4.app.Fragment;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-11-27
 */
public class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment delloc";

    @Override
    public void onDestroy() {
        super.onDestroy();
//        LogUtils.d(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        LogUtils.d(TAG, "onDetach");
        AutoDelloc.autoDelloc(this);
    }
}
