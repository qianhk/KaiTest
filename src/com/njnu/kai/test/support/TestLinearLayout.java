package com.njnu.kai.test.support;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-8-28
 */
public class TestLinearLayout extends LinearLayout {

    private static final long DRAW_INTERVAL = 100 * 1000 * 1000;

    private long mNanoTime;

    public TestLinearLayout(Context context) {
        super(context);
    }

    public TestLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public TestLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        long curNanoTime = System.nanoTime();
        boolean draw = curNanoTime - mNanoTime > DRAW_INTERVAL;
//        LogUtils.d("TestLinearLayout", "lookAnimation TestLinearLayout dispatchDraw draw=%b", draw);
//        if (draw) {
            mNanoTime = curNanoTime;
            super.dispatchDraw(canvas);
//        }
    }
}
