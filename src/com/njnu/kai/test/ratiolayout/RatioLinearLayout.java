package com.njnu.kai.test.ratiolayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.njnu.kai.test.R;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 13-8-16
 */
public class RatioLinearLayout extends LinearLayout {

    private int mRatioWidth = 1;
    private int mRatioHeight = 1;

    /**
     * construct
     * @param context context
     */
    public RatioLinearLayout(Context context) {
        super(context);
        initAttributeSet(context, null, 0);
    }

    /**
     * construct
     * @param context context
     * @param attrs   attrs
     */
    public RatioLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs, 0);
    }

    /**
     * construct
     * @param context context
     * @param attrs       attrs
     * @param defStyle default style
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public RatioLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
            }
        }
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int childWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (mode != MeasureSpec.UNSPECIFIED) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize * mRatioHeight / mRatioWidth, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
