package com.njnu.kai.test.grid;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import com.njnu.kai.test.support.LogUtils;

public class SimpleGridView extends ViewGroup {
    private static final int DEFAULT_MARGIN_SIZE = 8;
    //    private static final int ITEM_IN_ROW_COUNT = 3;
    private static final String LOG_TAG = "SimpleGridView";
    private int mChildMargin;
    private int mNumColumns = 3;

    /**
     * construct
     *
     * @param context context
     */
    public SimpleGridView(Context context) {
        super(context);
        init();
    }

    /**
     * construct
     *
     * @param context context
     * @param attrs   attrs
     */
    public SimpleGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * construct
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle default style
     */
    public SimpleGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * set margin
     * @param margin margin
     */
    public void setChildMargin(int margin) {
        mChildMargin = margin;
    }

    private void init() {
        mChildMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_MARGIN_SIZE, getContext().getResources().getDisplayMetrics());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        LogUtils.d(LOG_TAG, "onLayout %b %d %d %d %d", changed, l, t, r, b);
        int left = 0;
        int top = 0;
        for (int idx = 0, column = 0; idx < childCount; ++idx, ++column) {
            View childView = getChildAt(idx);
            int childMeasuredWidth = childView.getMeasuredWidth();
            int childMeasureHeight = childView.getMeasuredHeight();
            if (column == mNumColumns) {
                left = 0;
                top += childMeasureHeight + mChildMargin;
                column = 0;
            }
//            LogUtils.d(LOG_TAG, String.format("onLayout childIndex=%d %d %d %d %d", idx, left, top, childMeasuredWidth, childMeasureHeight));
            childView.layout(left, top, left + childMeasuredWidth, top + childMeasureHeight);
            left += childMeasuredWidth + mChildMargin;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        LogUtils.d(LOG_TAG, String.format("onMeasure %08X %d %08X %d", widthMode, widthSize, heightMode, heightSize));

        if (widthMode != MeasureSpec.EXACTLY && widthSize <= 0) {
            throw new IllegalStateException("Width must have an exact value or width size greate than 0 : "
                    + Integer.toHexString(widthMode) + " width=" + widthSize);
        }

        int singleSize = (widthSize - (mNumColumns - 1) * mChildMargin) / mNumColumns;
        int cellSizeSpec = MeasureSpec.makeMeasureSpec(singleSize, MeasureSpec.EXACTLY);
        int childCount = getChildCount();
        int maxHeight = 0;
        for (int idx = 0; idx < childCount; ++idx) {
            View childView = getChildAt(idx);
            int childWantHeight = childView.getLayoutParams().height;
            int childHeight = childWantHeight;
            int childMode = MeasureSpec.EXACTLY;
            if (childWantHeight == LayoutParams.MATCH_PARENT) {
                childHeight = heightSize;
            } else if (childWantHeight == LayoutParams.WRAP_CONTENT) {
                childHeight = heightSize;
                childMode = heightMode == MeasureSpec.UNSPECIFIED ? MeasureSpec.UNSPECIFIED : MeasureSpec.AT_MOST;
            }
            childView.measure(cellSizeSpec, MeasureSpec.makeMeasureSpec(childHeight, childMode));
            maxHeight = Math.max(childView.getMeasuredHeight(), maxHeight);
        }

        int ownHeightSize = 0;
        if (childCount > 0) {
            int lines = (childCount - 1) / mNumColumns + 1;
            ownHeightSize = maxHeight * lines + lines * mChildMargin - mChildMargin;
        }
//        int ownHeightMeasureSpec = heightMeasureSpec;
//        if (heightMode == MeasureSpec.UNSPECIFIED) {
//            ownHeightMeasureSpec = MeasureSpec.makeMeasureSpec(ownHeightSize, MeasureSpec.EXACTLY);
//        } else if (heightMode == MeasureSpec.AT_MOST) {
//            ownHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(ownHeightSize, heightSize), MeasureSpec.EXACTLY);
//        }
        setMeasuredDimension(widthSize, ownHeightSize);
    }

    /**
     * 设置列数,默认为3列
     *
     * @param numColumns 列数
     */
    public void setNumColumns(int numColumns) {
        if (numColumns < 1) {
            throw new IllegalArgumentException("列数不能小于1");
        }
        mNumColumns = numColumns;
    }

    public int getChildMargin() {
        return mChildMargin;
    }

    public int getNumColumns() {
        return mNumColumns;
    }
}
