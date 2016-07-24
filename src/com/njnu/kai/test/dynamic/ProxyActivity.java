package com.njnu.kai.test.dynamic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import com.njnu.kai.test.support.BaseActivity;
import com.njnu.kai.test.support.LogUtils;
import dalvik.system.DexClassLoader;

import java.io.File;
import java.lang.reflect.Method;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-11-16
 */

public class ProxyActivity extends BaseActivity {

    private static final String TAG = "ProxyActivity";

    public static final String FROM = "extra.from";
    public static final int FROM_EXTERNAL = 0;
    public static final int FROM_INTERNAL = 1;

    public static final String EXTRA_DEX_PATH = "extra.dex.path";
    public static final String EXTRA_CLASS = "extra.class";

    private String mClass;
    private String mDexPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("宿主HostActivity");
        mDexPath = getIntent().getStringExtra(EXTRA_DEX_PATH);
        mClass = getIntent().getStringExtra(EXTRA_CLASS);
        LogUtils.w(TAG, "mClass=" + mClass + " mDexPath=" + mDexPath);
        loadResources();

        if (mClass == null) {
            launchTargetActivity();
        } else {
            launchTargetActivity(mClass);
        }
    }

    @SuppressLint("NewApi")
    protected void launchTargetActivity() {
        PackageInfo packageInfo = getPackageManager().getPackageArchiveInfo(mDexPath, 1);
        if ((packageInfo.activities != null) && (packageInfo.activities.length > 0)) {
            String activityName = packageInfo.activities[0].name;
            mClass = activityName;
            launchTargetActivity(mClass);
        }
    }

    @SuppressLint("NewApi")
    protected void launchTargetActivity(final String className) {
        LogUtils.d(TAG, "start launchTargetActivity, className=" + className);
        File dexOutputDir = this.getDir("dex", 0);
        final String dexOutputPath = dexOutputDir.getAbsolutePath();
        ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
        DexClassLoader dexClassLoader = new DexClassLoader(mDexPath, dexOutputPath, null, localClassLoader);
        try {
            Class<?> localClass = dexClassLoader.loadClass(className);
//            Constructor<?> localConstructor = localClass.getConstructor(new Class[] {});
//            Object instance = localConstructor.newInstance(new Object[] {});
            Object instance = localClass.newInstance();
            LogUtils.d(TAG, "instance = " + instance);

            Method setProxy = localClass.getMethod("setProxy", Activity.class);
            setProxy.setAccessible(true);
            setProxy.invoke(instance, this);

            Method onCreate = localClass.getDeclaredMethod("onCreate", Bundle.class);
            onCreate.setAccessible(true);
            Bundle bundle = new Bundle();
            bundle.putInt(FROM, FROM_EXTERNAL);
            onCreate.invoke(instance, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private AssetManager mAssetManager;
    private Resources mResources;
    private Resources.Theme mTheme;

    protected void loadResources() {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, mDexPath);
            mAssetManager = assetManager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Resources superRes = super.getResources();
        mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
        mTheme = mResources.newTheme();
        mTheme.setTo(super.getTheme());
    }

    @Override
    public AssetManager getAssets() {
        return mAssetManager == null ? super.getAssets() : mAssetManager;
    }

    @Override
    public Resources getResources() {
        return mResources == null ? super.getResources() : mResources;
    }

}