package com.njnu.kai.test.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @version 1.0.0
 * @since 2014-10-13
 */
public class WrapFixedLinearLayout extends LinearLayout {

    /**
     * construct
     * @param context context
     */
    public WrapFixedLinearLayout(Context context) {
        super(context);
        initAttributeSet(context, null, 0);
    }

    /**
     * construct
     * @param context context
     * @param attrs   attrs
     */
    public WrapFixedLinearLayout(Context context, AttributeSet attrs) {
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
    public WrapFixedLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttributeSet(context, attrs, defStyle);
    }

    private void initAttributeSet(Context context, AttributeSet attrs, int defStyle) {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int actualWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (getOrientation() == HORIZONTAL && getChildCount() == 2 && actualWidth > 0) {
            View leftView = getChildAt(0);
            View rightView = getChildAt(1);
            LayoutParams leftLayoutParams = (LayoutParams)leftView.getLayoutParams();
            LayoutParams rightLayoutParams = (LayoutParams)rightView.getLayoutParams();
            int rightWidth = rightView.getMeasuredWidth();
            if (rightView instanceof ViewGroup) {
                rightView.measure(actualWidth, heightMeasureSpec);
                rightWidth = rightView.getMeasuredWidth();
            }
            int leftWidth = leftView.getMeasuredWidth();
            int needWholeWidth = getPaddingLeft() + getPaddingRight() + leftWidth + rightWidth
                    + leftLayoutParams.leftMargin + leftLayoutParams.rightMargin + rightLayoutParams.leftMargin + rightLayoutParams.rightMargin;
            if (needWholeWidth > actualWidth) {
                leftWidth -= needWholeWidth - actualWidth;
                leftView.measure(MeasureSpec.makeMeasureSpec(leftWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        if (getOrientation() == HORIZONTAL && getChildCount() == 2) {
//            View leftView = getChildAt(0);
//            View rightView = getChildAt(1);
//            int leftWidth = leftView.getMeasuredWidth();
//            int rightWidth = rightView.getMeasuredWidth();
//            LayoutParams leftLayoutParams = (LayoutParams)leftView.getLayoutParams();
//            LayoutParams rightLayoutParams = (LayoutParams)rightView.getLayoutParams();
//            int needWholeWidth = getPaddingLeft() + getPaddingRight() + leftWidth + rightWidth
//                    + leftLayoutParams.leftMargin + leftLayoutParams.rightMargin + rightLayoutParams.leftMargin + rightLayoutParams.rightMargin;
//            int actualWidth = r - l;
//            if (needWholeWidth > actualWidth) {
//                int leftActualWidth = leftWidth - needWholeWidth + actualWidth;
//                int xPos = getPaddingLeft();
//                int yPos = getPaddingTop() + leftLayoutParams.topMargin;
//                leftView.layout(xPos, yPos, xPos + leftActualWidth, yPos + leftView.getMeasuredHeight());
//                xPos += (leftActualWidth + leftLayoutParams.rightMargin + rightLayoutParams.leftMargin);
//                yPos = getPaddingTop() + rightLayoutParams.topMargin;
//                rightView.layout(xPos, yPos, xPos + rightWidth, yPos + rightView.getMeasuredHeight());
//                return;
//            }
//        }
        super.onLayout(changed, l, t, r, b);
    }
}
