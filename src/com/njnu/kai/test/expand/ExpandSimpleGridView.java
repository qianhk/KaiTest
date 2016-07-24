package com.njnu.kai.test.expand;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.njnu.kai.test.grid.SimpleGridView;
import com.njnu.kai.test.support.LogUtils;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-9-25
 */
public class ExpandSimpleGridView extends SimpleGridView implements IExpandAbleLayout {

    private int mMaxShowCount;
    private boolean mExpanded;

    public ExpandSimpleGridView(Context context) {
        super(context);
        init(context);
    }

    public ExpandSimpleGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ExpandSimpleGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int childCount = getChildCount();
        if (mExpanded || childCount <= mMaxShowCount) {
            return;
        }
        int needHeight = 0;
        final int numColumns = getNumColumns();
        int rowCount = (mMaxShowCount + numColumns - 1) / numColumns;
        if (rowCount > 0) {
            final View childView = getChildAt(0);
            ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) childView.getLayoutParams();
            int singleHeight = childView.getMeasuredHeight();
            LogUtils.e("ExpandSimpleGridView", "ExpandSimpleGridView layoutParams=" + layoutParams.getClass().getSimpleName());
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (MarginLayoutParams) layoutParams;
                singleHeight += marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;
            }
            final int childMargin = getChildMargin();
            needHeight = (childMargin + singleHeight) * rowCount - childMargin;
        }
        setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(needHeight, MeasureSpec.EXACTLY));
    }

    private void init(Context context) {
        mMaxShowCount = 3;
    }

    @Override
    public int getMaxShowCount() {
        return mMaxShowCount;
    }

    @Override
    public void setMaxShowCount(int maxShowCount) {
        mMaxShowCount = maxShowCount;
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public void collapse() {
        if (mExpanded) {
            mExpanded = false;
            requestLayout();
        }
    }

    public void expand() {
        if (!mExpanded) {
            mExpanded = true;
            requestLayout();
        }
    }
}
