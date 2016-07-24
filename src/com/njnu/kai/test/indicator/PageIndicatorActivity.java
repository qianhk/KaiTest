package com.njnu.kai.test.indicator;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import com.njnu.kai.test.GlobalMenuDialog;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.BaseActivity;
import com.njnu.kai.test.view.GlobalMenuThumbImageView;


public class PageIndicatorActivity extends BaseActivity {

    private PageIndicator mPageIndicator;
    private int mCount = 5;
    private int mCurrentIndex = 1;

    private GlobalMenuThumbImageView mThumbImageView;
    private TextView mTextView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page_indicator);
        mPageIndicator = (IconPageIndicator)findViewById(R.id.indicator);
        ((IconPageIndicator)mPageIndicator).setIconResource(R.drawable.social_page_indicator, R.drawable.social_page_indicator_selected);
        mPageIndicator.onPageCountChanged(mCount);
        mPageIndicator.onPageSelected(mCurrentIndex);
        findViewById(R.id.btn_add).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_remove).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_left).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_right).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_mode_one).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_mode_two).setOnClickListener(mOnClickListener);

        SeekBar seekBar = (SeekBar)findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mThumbImageView.setThumbOffset(1.0f * progress / 100.0f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mThumbImageView = (GlobalMenuThumbImageView)findViewById(R.id.thumbImageView);
        mThumbImageView.setThumbDrawable(getResources().getDrawable(R.drawable.img_menu_indicator_thumb2));

        findViewById(R.id.btn_pop_menu_dialog).setOnClickListener(mOnClickListener);

        mTextView = (TextView)findViewById(R.id.tv_text);
//        mTextView.setText("abcdkefalsjkfjalsdffklasdfjlkasjdflkajsldfjaksldfw1w2w3w4w5w6");
//        mTextView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mTextView.setText("");
//            }
//        }, 5000);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_add:
                    ++mCount;
                    mPageIndicator.onPageCountChanged(mCount);
                    break;
                case R.id.btn_remove:
                    if (mCount > 0) {
                        --mCount;
                        mPageIndicator.onPageCountChanged(mCount);
                    }
                    break;
                case R.id.btn_left:
                    --mCurrentIndex;
                    mPageIndicator.onPageSelected(mCurrentIndex);
                    break;
                case R.id.btn_right:
                    ++mCurrentIndex;
                    if (mCurrentIndex >= mCount) {
                        mCurrentIndex = 0;
                    }
                    mPageIndicator.onPageSelected(mCurrentIndex);
                    break;

                case R.id.btn_mode_one:
                    break;

                case R.id.btn_mode_two:
                    break;

                case R.id.btn_pop_menu_dialog:
                    new GlobalMenuDialog(PageIndicatorActivity.this).show();
                    break;
            }
        }
    };

}