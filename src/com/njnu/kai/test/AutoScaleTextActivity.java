package com.njnu.kai.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.njnu.kai.test.view.AutoScaleTextView;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-12-22
 */
public class AutoScaleTextActivity extends Activity {

    private Button mBtnSmall;
    private Button mBtnLarge;
    private AutoScaleTextView mAutoScaleTextView;
    private StringBuilder mBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_autoscale_textview);
        mBtnSmall = (Button)findViewById(R.id.btn_small);
        mBtnLarge = (Button)findViewById(R.id.btn_large);
        mAutoScaleTextView = (AutoScaleTextView)findViewById(R.id.atv_first);

        mBtnLarge.setOnClickListener(mOnClickListener);
        mBtnSmall.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mBtnLarge) {
                mBuilder.append("凯");
            } else {
                if (mBuilder.length() > 0) {
                    mBuilder.setLength(mBuilder.length() - 1);
                }
            }
            mAutoScaleTextView.setText(mBuilder);
        }
    };
}
