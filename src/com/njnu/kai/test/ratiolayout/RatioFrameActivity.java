package com.njnu.kai.test.ratiolayout;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.BaseActivity;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-3-3
 */
public class RatioFrameActivity extends BaseActivity {

    private FrameLayout mLayoutPt;
    private View mNewFlag;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_size_small:
                    setTestWidth(300);
                    ViewCompat.setScaleX(mViewOri, 0.5f);
                    ViewCompat.setScaleY(mViewOri, 0.5f);
                    break;

                case R.id.btn_size_middle:
                    setTestWidth(500);
                    ViewCompat.setScaleX(mViewOri, 1.2f);
                    ViewCompat.setScaleY(mViewOri, 1.2f);
                    break;

                case R.id.btn_size_large:
                    setTestWidth(700);
                    ViewCompat.setScaleX(mViewOri, 1.0f);
                    ViewCompat.setScaleY(mViewOri, 1.0f);
                    break;

                case R.id.btn_ori:
                    setTestWidth(192);

                    break;

                case R.id.btn_hide:
                    mNewFlag.setVisibility(View.INVISIBLE);
                    break;

                case R.id.btn_display:
                    mNewFlag.setVisibility(View.VISIBLE);
                    break;

                default: break;
            }
        }
    };
    private View mViewOri;


    private void setTestWidth(int width) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)mLayoutPt.getLayoutParams();
        layoutParams.width = width;
        mLayoutPt.setLayoutParams(layoutParams);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_ratio_frame);
        findViewById(R.id.btn_size_small).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_size_middle).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_size_large).setOnClickListener(mOnClickListener);
        mViewOri = findViewById(R.id.btn_ori);
        mViewOri.setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_hide).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_display).setOnClickListener(mOnClickListener);
        mLayoutPt = (FrameLayout)findViewById(R.id.layout_pt);
        mNewFlag = mLayoutPt.findViewById(R.id.iv_new_flag);
    }

}