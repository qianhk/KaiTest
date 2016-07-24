package com.njnu.kai.test.indicator;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 13-8-21
 */
public class IconPageIndicator extends View implements PageIndicator {

    private Drawable mUnSelectDrawable;
    private Drawable mSelectedDrawable;

    private int mCount;
    private int mCurrentPage;

    private static final int SPACE_OFFSET = 2;

    public IconPageIndicator(Context context) {
        super(context);
        init();
    }

    private void init() {
    }

    public IconPageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconPageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setIconResource(int unSelectResId, int selectedResId) {
        Resources resources = getContext().getResources();
        setIconResource(resources.getDrawable(unSelectResId), resources.getDrawable(selectedResId));
    }

    public void setIconResource(Drawable unSelectDrawable, Drawable selectedDrawable) {
        mUnSelectDrawable = unSelectDrawable;
        mSelectedDrawable = selectedDrawable;

        invalidate();
    }

    @Override
    public void onPageSelected(int page) {
        mCurrentPage = page;
        if (mCurrentPage >= mCount) {
            mCurrentPage = mCount - 1;
        }

        invalidate();
    }

    @Override
    public void onPageCountChanged(int count) {
        if (count < 0) {
            count = 0;
        }
        if (mCount != count) {
            mCount = count;
            requestLayout();
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCount == 0 || mSelectedDrawable == null || mUnSelectDrawable == null) {
            return;
        }

        int longPaddingBefore = getPaddingLeft();
        int shortPaddingBefore = getPaddingTop();
        int drawableSize = mSelectedDrawable.getIntrinsicWidth();
        int spaceSize = drawableSize >> SPACE_OFFSET;
        int needWidth = longPaddingBefore + getPaddingRight() + drawableSize * mCount + (mCount - 1) * spaceSize;
        int longOffset = ((getWidth() - needWidth) >> 1) + longPaddingBefore;

        Drawable drawable;
        for(int idx = 0; idx < mCount; ++idx) {
            drawable = idx == mCurrentPage ? mSelectedDrawable : mUnSelectDrawable;
            drawable.setBounds(longOffset, shortPaddingBefore, longOffset + drawableSize, shortPaddingBefore + drawableSize);
            drawable.draw(canvas);
            longOffset += drawableSize + spaceSize;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureLong(widthMeasureSpec), measureShort(heightMeasureSpec));
    }

    private int measureLong(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = specSize;

        if (specMode != MeasureSpec.EXACTLY) {
            int iconWidth = mSelectedDrawable != null ? mSelectedDrawable.getIntrinsicWidth() : 0;
            result = getPaddingLeft() + getPaddingRight() + iconWidth * mCount + (mCount - 1) * (iconWidth >> SPACE_OFFSET);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureShort(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = specSize;

        if (specMode != MeasureSpec.EXACTLY) {
            result = getPaddingTop() + getPaddingBottom() + (mSelectedDrawable != null ? mSelectedDrawable.getIntrinsicHeight() : 0);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
}
