package com.njnu.kai.test;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import com.njnu.kai.test.support.SDKVersionUtils;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-1-13
 */
public class WallPaperActivity extends Activity {

    private View mRootView;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            if (viewId == R.id.btn_drawable) {
                setDrawable();
            } else if (viewId == R.id.btn_fast_drawable) {
                setFastDrawable();
            } else if (viewId == R.id.btn_peek_drawable) {
                setPeekDrawable();
            } else if (viewId == R.id.btn_peek_fast_drawable) {
                setPeekFastDrawable();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockscreen);
        mRootView = findViewById(R.id.layoutLockScreen);
        findViewById(R.id.btn_drawable).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_peek_drawable).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_fast_drawable).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_peek_fast_drawable).setOnClickListener(mOnClickListener);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void doSetDrawable(Drawable drawable) {
        if (SDKVersionUtils.hasJellyBean()) {
            mRootView.setBackground(drawable);
        } else {
            mRootView.setBackgroundDrawable(drawable);
        }
    }

    private void setDrawable() {
        try {
            doSetDrawable(WallpaperManager.getInstance(this).getDrawable());
        } catch (Throwable t) {
            t.printStackTrace();
            doSetDrawable(null);
        }
    }

    private void setFastDrawable() {
        try {
            doSetDrawable(WallpaperManager.getInstance(this).getFastDrawable());
        } catch (Throwable t) {
            t.printStackTrace();
            doSetDrawable(null);
        }
    }

    private void setPeekDrawable() {
        try {
            doSetDrawable(WallpaperManager.getInstance(this).peekDrawable());
        } catch (Throwable t) {
            t.printStackTrace();
            doSetDrawable(null);
        }
    }

    private void setPeekFastDrawable() {
        try {
            doSetDrawable(WallpaperManager.getInstance(this).peekFastDrawable());
        } catch (Throwable t) {
            t.printStackTrace();
            doSetDrawable(null);
        }
    }

}
