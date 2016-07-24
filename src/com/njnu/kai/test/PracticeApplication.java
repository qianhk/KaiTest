package com.njnu.kai.test;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.njnu.kai.test.support.BaseApplication;
import com.njnu.kai.test.support.LogUtils;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-4-29
 */
public class PracticeApplication extends BaseApplication {

    private static final String TAG = "TrainApplication";
    private static boolean mSplashHasFinished = false;

    public static boolean isSplashHasFinished() {
        return mSplashHasFinished;
    }

    public static void setSplashAlreadyFinished() {
        mSplashHasFinished = true;
    }

    @Override
    protected void onAppCreated(Context context) {
        LogUtils.d(TAG, "lookMetaData build config debug = %b", BuildConfig.DEBUG);
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String umengAppkey = appInfo.metaData.getString("UMENG_APPKEY");
            LogUtils.d(TAG, "lookMetaData umeng app key = %s", umengAppkey);
//            appInfo.metaData.putString("UMENG_APPKEY", "testKey");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
