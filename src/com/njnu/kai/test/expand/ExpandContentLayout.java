package com.njnu.kai.test.expand;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-9-25
 */
public class ExpandContentLayout extends LinearLayout implements IExpandAbleLayout {

    private int mMaxShowCount;
    private boolean mExpanded;

    public ExpandContentLayout(Context context) {
        super(context);
        init(context);
    }

    public ExpandContentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ExpandContentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExpandContentLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        mMaxShowCount = 3;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int childCount = getChildCount();
        if (mExpanded || childCount <= mMaxShowCount) {
            return;
        }
        int needHeight = 0;
        LinearLayout.LayoutParams layoutParams;
        for (int idx = 0; idx < mMaxShowCount; ++idx) {
            final View childView = getChildAt(idx);
            needHeight += childView.getMeasuredHeight();
            layoutParams = (LayoutParams) childView.getLayoutParams();
            needHeight += layoutParams.topMargin + layoutParams.bottomMargin;
        }
        setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(needHeight, MeasureSpec.EXACTLY));
    }

    @Override
    public int getMaxShowCount() {
        return mMaxShowCount;
    }

    @Override
    public void setMaxShowCount(int maxShowCount) {
        mMaxShowCount = maxShowCount;
    }

    @Override
    public boolean isExpanded() {
        return mExpanded;
    }

    @Override
    public void collapse() {
        if (mExpanded) {
            mExpanded = false;
            requestLayout();
        }
    }

    @Override
    public void expand() {
        if (!mExpanded) {
            mExpanded = true;
            requestLayout();
        }
    }
}
