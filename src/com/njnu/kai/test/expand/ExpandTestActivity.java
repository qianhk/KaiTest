package com.njnu.kai.test.expand;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.BaseActivity;
import com.njnu.kai.test.support.DisplayUtils;
import com.njnu.kai.test.support.ToastUtils;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-9-25
 */
public class ExpandTestActivity extends BaseActivity implements View.OnClickListener {

    private ExpandContentLayout mExpandContentLayout;

    private ArrayList<ProgressView> mPvList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expand_main_layout);
        mExpandContentLayout = (ExpandContentLayout) findViewById(R.id.layout_expand_content_layout);
        mExpandContentLayout.expand();
        mPvList.add((ProgressView) mExpandContentLayout.findViewById(R.id.pv_0));
        mPvList.add((ProgressView) mExpandContentLayout.findViewById(R.id.pv_1));
        mPvList.add((ProgressView) mExpandContentLayout.findViewById(R.id.pv_2));
        mPvList.add((ProgressView) mExpandContentLayout.findViewById(R.id.pv_3));
        mPvList.add((ProgressView) mExpandContentLayout.findViewById(R.id.pv_4));
        mPvList.add((ProgressView) mExpandContentLayout.findViewById(R.id.pv_5));
        ProgressView progressView = mPvList.get(5);
        progressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExpandTestActivity.this, ExpandGridTestActivity.class));
            }
        });
        findViewById(R.id.tv_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mExpandContentLayout.isExpanded()) {
                    mExpandContentLayout.collapse();
                } else {
                    mExpandContentLayout.expand();
                }
            }
        });

        Random random = new Random(System.currentTimeMillis());
        for (int idx = 0; idx < mPvList.size(); ++idx) {
            ProgressView childProgressView = mPvList.get(idx);
            childProgressView.setProgressColor(0xFF000000 | random.nextInt());
            childProgressView.setButtonColor(0xFF000000 | random.nextInt());
            childProgressView.setTextColor(Color.WHITE);
            childProgressView.setBkgColor(0x1AFFFFFF);
            childProgressView.setMaxValue(100);
            childProgressView.setCornorRadius(DisplayUtils.dp2px(12));
            childProgressView.setTextHorizontalPadding(random.nextInt(DisplayUtils.dp2px(20)));
//            childProgressView.setStrokeWidth(idx * 5);
            childProgressView.setSelected(true);
            childProgressView.setOnClickListener(this);
            childProgressView.setTag("pos=" + idx);
        }

        progressView = mPvList.get(0);
        progressView.setLeftText("Left");
        progressView.setValue(99);
        progressView.setButtonText("投票");
        progressView.setButtonWidth(ViewGroup.LayoutParams.WRAP_CONTENT);

        progressView = mPvList.get(1);
        progressView.setCenterText("Center");
        progressView.setValue(98);
        progressView.setSelected(true);
        progressView.setButtonText("Hello Kai");
        progressView.setButtonWidth(ViewGroup.LayoutParams.MATCH_PARENT);

        progressView = mPvList.get(2);
        progressView.setRightText("Right");
        progressView.setValue(6);
        progressView.setButtonText("Vote");
        progressView.setButtonWidth(DisplayUtils.dp2px(80));

        progressView = mPvList.get(3);
        progressView.setLeftText("Left");
        progressView.setCenterText("Center");
        progressView.setRightText("Right");
        progressView.setValue(4);

        progressView = mPvList.get(4);
        progressView.setValue(2);
        progressView.setLeftText("Left");

        progressView = mPvList.get(5);
        progressView.setValue(0);
        progressView.setLeftText("Left");
    }

    @Override
    public void onClick(View v) {
        ToastUtils.showToast(v.getContext(), "You Click " + v.getTag());
        ProgressView progressView = (ProgressView)v;
        progressView.setButtonWidth(0);
        progressView.setRightText("哈哈， vote完成");
    }
}
