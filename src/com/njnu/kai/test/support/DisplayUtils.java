package com.njnu.kai.test.support;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.util.DisplayMetrics;

/**
 * @author hu.cao
 * @version 1.0.0
 * 显示相关总汇
 */
public class DisplayUtils {
    private static DisplayMetrics sDisplayMetrics;
    private static Configuration sConfiguration;
    private static Typeface sIconTypeFace;

    /**
     * ldpi
     */
    public static final String EX_DENSITY_LOW = "_ldpi";
    /**
     * mdpi
     */
    public static final String EX_DENSITY_MEDIUM = "_mdpi";
    /**
     * hdpi
     */
    public static final String EX_DENSITY_HIGH = "_hdpi";
    /**
     * xhdpi
     */
    public static final String EX_DENSITY_XHIGH = "_xhdpi";

    private static final float ROUND_DIFFERENCE = 0.5f;

    /**
     * 初始化操作
     * @param context context
     */
    public static void init(Context context) {
        sDisplayMetrics = context.getResources().getDisplayMetrics();
        sConfiguration = context.getResources().getConfiguration();
        sIconTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/IconFont.ttf");
    }

    /**
     * @return 图形字体
     */
    public static Typeface getIconTypeFace() {
        return sIconTypeFace;
    }

    /**
     * 配置发生变化
     * @param context context
     * @param newConfiguration newConfiguration
     */
    public static void onConfigurationChanged(Context context, Configuration newConfiguration) {
        sDisplayMetrics = context.getResources().getDisplayMetrics();
        sConfiguration = newConfiguration;
    }

    /**
     * 获取屏幕宽度 单位：像素
     * @return 屏幕宽度
     */
    public static int getWidthPixels() {
        return sDisplayMetrics.widthPixels;
    }

    /**
     * 获取屏幕高度 单位：像素
     * @return 屏幕高度
     */
    public static int getHeightPixels() {
        return sDisplayMetrics.heightPixels;
    }

    /**
     * 获取Density
     * @return Density
     */
    public static float getDensity() {
        return sDisplayMetrics.density;
    }

    /**
     * 获取DensityDpi
     * @return DensityDpi
     */
    public static int getDensityDpi() {
        return sDisplayMetrics.densityDpi;
    }

    /**
     * dp 转 px
     * 注意正负数的四舍五入规则
     * @param dp dp值
     * @return 转换后的像素值
     */
    public static int dp2px(int dp) {
        return (int)(dp * sDisplayMetrics.density + (dp > 0 ? ROUND_DIFFERENCE : -ROUND_DIFFERENCE));
    }

    /**
     * px 转 dp
     * 注意正负数的四舍五入规则
     * @param px px值
     * @return 转换后的dp值
     */
    public static int px2dp(int px) {
        return (int)(px / sDisplayMetrics.density + (px > 0 ? ROUND_DIFFERENCE : -ROUND_DIFFERENCE));
    }

    /**
     * get bitmap density
     * @return String
     */
    public static String getBitmapDensityStr() {
        switch (getBitmapDensity()) {
            case DisplayMetrics.DENSITY_LOW:
                return EX_DENSITY_LOW;
            case DisplayMetrics.DENSITY_MEDIUM:
                return EX_DENSITY_MEDIUM;
            case DisplayMetrics.DENSITY_HIGH:
                return EX_DENSITY_HIGH;
            case DisplayMetrics.DENSITY_XHIGH:
            case DisplayMetrics.DENSITY_XXHIGH:
                return EX_DENSITY_XHIGH;
            default:
                return "";
        }
    }

    /**
     * 获取bitmapDensity
     * @return bitmapDensity
     */
    public static int getBitmapDensity() {
        int densityDpi = sDisplayMetrics.densityDpi;
        if (densityDpi <= DisplayMetrics.DENSITY_LOW) {
            return DisplayMetrics.DENSITY_LOW;
        } else if (densityDpi <= DisplayMetrics.DENSITY_MEDIUM) {
            return DisplayMetrics.DENSITY_MEDIUM;
        } else if (densityDpi <= DisplayMetrics.DENSITY_HIGH) {
            return DisplayMetrics.DENSITY_HIGH;
        } else if (densityDpi <= DisplayMetrics.DENSITY_XHIGH) {
            return DisplayMetrics.DENSITY_XHIGH;
        } else {
            return DisplayMetrics.DENSITY_XXHIGH;
        }
    }

    /**
     * 是否为竖屏
     * @return true/false
     */
    public static boolean isPortrait() {
        return sConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT
                || (sConfiguration.orientation == Configuration.ORIENTATION_UNDEFINED && getHeightPixels() > getWidthPixels());
    }
}
