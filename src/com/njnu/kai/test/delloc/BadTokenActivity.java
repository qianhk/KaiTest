package com.njnu.kai.test.delloc;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-12-22
 */
public class BadTokenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        setContentView(textView);
        textView.setText("BadToken test");
        textView.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 5000);
    }
}
