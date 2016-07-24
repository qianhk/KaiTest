package com.njnu.kai.test.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.njnu.kai.test.R;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-5-14
 */
public class WaveActivity extends Activity {

    private final int MAX_VALUE = 100;

    private int mCurValue = 50;

    private TitanicTextView mTitanicTextView;
    private TextView mTvValue;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_add) {
                if (mCurValue < MAX_VALUE) {
                    mCurValue = Math.min(mCurValue + 10, MAX_VALUE);
                    doFlushProgress();
                }
            } else if (id == R.id.btn_minus) {
                if (mCurValue > 0) {
                    mCurValue = Math.max(mCurValue - 10, 0);
                    doFlushProgress();
                }
            }
        }
    };

    private void doFlushProgress() {
        mTitanicTextView.setMaskY(mCurValue);
        mTitanicTextView.setMaskX(0);
        mTvValue.setText(Long.toString(mCurValue));
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_wave);
        findViewById(R.id.btn_add).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_minus).setOnClickListener(mOnClickListener);
        mTitanicTextView = (TitanicTextView)findViewById(R.id.tv_loading);
        mTitanicTextView.setSinking(true);
        mTvValue = (TextView)findViewById(R.id.tv_value);
//        new Titanic().start(mTitanicTextView);
        doFlushProgress();
    }

}
