package com.njnu.kai.test.support;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

/**
 * @version 7.0.0
 */
abstract public class BaseApplication extends Application {
    private final static String TAG = "BaseApplication";
    private static final int KILL_SELF_DELAY = 500; //ms
    private static BaseApplication sApp = null;

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public final void onCreate() {
        super.onCreate();

        sApp = this;

        DisplayUtils.init(this);
        EnvironmentUtils.init(this);
        LogUtils.setEnable(EnvironmentUtils.Config.isLogEnable());
        onAppCreated(this);
    }

    abstract protected void onAppCreated(Context context);

    /**
     * 获取Application实例
     *
     * @return Application 实例
     */
    public static BaseApplication getApp() {
        return sApp;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DisplayUtils.onConfigurationChanged(this, newConfig);
    }


}
