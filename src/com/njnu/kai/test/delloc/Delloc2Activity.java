package com.njnu.kai.test.delloc;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import com.njnu.kai.test.support.DisplayUtils;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-11-27
 */
public class Delloc2Activity extends FragmentActivity {

    private static final String TAG = "Delloc2Activity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        setContentView(textView);
        textView.setBackgroundColor(Color.WHITE);
        textView.setTextColor(Color.BLUE);
        textView.setTextSize(DisplayUtils.dp2px(32));
        textView.setText("ok hongkai.qian");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        LogUtils.d(TAG, "onDestroy");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        LogUtils.d(TAG, "onDetachedFromWindow");
        AutoDelloc.autoDelloc(this);
    }
}