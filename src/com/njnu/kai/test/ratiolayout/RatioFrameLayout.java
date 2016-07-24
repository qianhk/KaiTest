package com.njnu.kai.test.ratiolayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.njnu.kai.test.R;

import java.util.ArrayList;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 13-11-13
 */
public class RatioFrameLayout extends FrameLayout {

    private static final String LOG_TAG = "RatioFrameLayout";

    private int mRatioWidth = 1;
    private int mRatioHeight = 1;

    private float mDesignWidth = 1.0f;
    private boolean mScaleChild;

    private ArrayList<Rect> mChildMarginList;

    /**
     * construct
     * @param context context
     */
    public RatioFrameLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    /**
     * construct
     * @param context context
     * @param attrs   attrs
     */
    public RatioFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    /**
     * construct
     * @param context context
     * @param attrs   attrs
     * @param defStyle def style
     */
    public RatioFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        initAttributeSet(context, attrs, defStyle);
    }


    private void initAttributeSet(Context context, AttributeSet attrs, int defStyle) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioView, defStyle, 0);
        if (typedArray == null) {
            return;
        }
        for (int idx = typedArray.getIndexCount() - 1; idx >= 0; idx--) {
            int attr = typedArray.getIndex(idx);
            if (attr == R.styleable.RatioView_ratio_height) {
                mRatioHeight = typedArray.getInteger(attr, mRatioHeight);
            } else if (attr == R.styleable.RatioView_ratio_width) {
                mRatioWidth = typedArray.getInteger(attr, mRatioWidth);
            } else if (attr == R.styleable.RatioView_design_width) {
                mDesignWidth = typedArray.getDimension(attr, mDesignWidth);
            } else if (attr == R.styleable.RatioView_scale_child) {
                mScaleChild = typedArray.getBoolean(attr, mScaleChild);
            }
        }
        typedArray.recycle();
    }

//    @Override
//    public void addView(View child, int index, ViewGroup.LayoutParams params) {
//        super.addView(child, index, params);
//        int pos = index < 0 ? (getChildCount() - 1) : index;
//        MarginLayoutParams lp = (MarginLayoutParams)getChildAt(pos).getLayoutParams();
//        Rect rect = new Rect(lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin);
//        if (index < 0) {
//            mChildMarginList.add(rect);
//        } else {
//            mChildMarginList.add(index, rect);
//        }
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        if (mScaleChild && mChildMarginList == null) {
            mChildMarginList = new ArrayList<Rect>(childCount);
            for (int idx = 0; idx < childCount; ++idx) {
                MarginLayoutParams lp = (MarginLayoutParams)getChildAt(idx).getLayoutParams();
                mChildMarginList.add(new Rect(lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin));
            }
        }

//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        LogUtils.d(LOG_TAG, "onMeasure, widthMode=%08X width=%d heightMode=%08X height=%d", widthMode, widthSize
//                , MeasureSpec.getMode(heightMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));

        if (mScaleChild) {
            resetChildViewMargin(widthSize);
        }

        int height = mRatioHeight * widthSize / mRatioWidth;
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void resetChildViewMargin(int parentWidth) {
        if (mChildMarginList == null || mChildMarginList.isEmpty()) {
            return;
        }
        float ratio = 1.0f + (parentWidth - mDesignWidth) / mDesignWidth;
        if (ratio < 0) {
            return;
        }
        for (int idx = getChildCount() - 1; idx >= 0; --idx) {
            View childView = getChildAt(idx);
            MarginLayoutParams lp = (MarginLayoutParams)childView.getLayoutParams();
            Rect rect = mChildMarginList.get(idx);
            lp.leftMargin = (int)(rect.left * ratio);
            lp.topMargin = (int)(rect.top * ratio);
            lp.rightMargin = (int)(rect.right * ratio);
            lp.bottomMargin = (int)(rect.bottom * ratio);
            if (childView instanceof DynamicSize) {
                DynamicSize dynamicSize = (DynamicSize)childView;
                dynamicSize.scaleRatio(ratio);
            }
        }
    }
}
