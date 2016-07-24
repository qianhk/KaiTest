package com.njnu.kai.test.expand;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.njnu.kai.test.support.DisplayUtils;
import com.njnu.kai.test.support.StringUtils;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-9-26
 */
public class ProgressView extends View {

    private static final String TAG = "ProgressView";
    private int mValue = 20;
    private int mMaxValue = 100;

    private int mProgressColor = Color.MAGENTA;
    private int mBkgColor = Color.RED;

    private int mTextSize = DisplayUtils.dp2px(14);
    private int mTextColor = Color.CYAN;
//    private int mSelectColor = Color.WHITE;
//    private float mStrokeWidth = 1.0f;

    private int mButtonWidth;
    private int mButtonColor;
    private String mButtonText;

    private int mTextHorizontalPadding = DisplayUtils.dp2px(12);
    private int mCornorRadius = DisplayUtils.dp2px(12);

    private String mLeftText;
    private String mCenterText;
    private String mRightText;

    private RectF mRectF = new RectF();
    private TextPaint mTextPaint = new TextPaint();
    private int mFontOffset;
    private RectF mButtonRectF = new RectF();

    private OnClickListener mOnClickListener;

    private GestureDetector mGestureDetector;
    private GestureDetector.SimpleOnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
//            LogUtils.e(TAG, "onSingleTapConfirmed");
            if (mButtonRectF.contains(e.getX(), e.getY())) {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(ProgressView.this);
                }
                return true;
            }
            return false;
        }

    };

    @Override
    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;
    }

    public ProgressView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    public int getButtonWidth() {
        return mButtonWidth;
    }

    public void setButtonWidth(int buttonWidth) {
        mButtonWidth = buttonWidth;
        invalidate();
    }

    public int getButtonColor() {
        return mButtonColor;
    }

    public void setButtonColor(int buttonColor) {
        mButtonColor = buttonColor;
        invalidate();
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFakeBoldText(false);
        mGestureDetector = new GestureDetector(context, mOnGestureListener);

//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//        mStrokeWidth = 0.5f * displayMetrics.density;
//
//                , mCornorRadius, mCornorRadius, mCornorRadius, mCornorRadius };
//        RoundRectShape roundRectShape = new RoundRectShape(outerRadius, null, null);
//        mShapeDrawable.setShape(roundRectShape);
    }

    public void setProgress(int value, int maxValue) {
        mValue = value;
        mMaxValue = maxValue;
        invalidate();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
        invalidate();
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        mValue = value;
        invalidate();
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public void setMaxValue(int maxValue) {
        mMaxValue = maxValue;
        invalidate();
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    public void setProgressColor(int progressColor) {
        mProgressColor = progressColor;
        invalidate();
    }

    public void setLeftText(String leftText) {
        mLeftText = leftText;
        invalidate();
    }

    public void setCenterText(String centerText) {
        mCenterText = centerText;
        invalidate();
    }

    public void setRightText(String rightText) {
        mRightText = rightText;
        invalidate();
    }

    public String getButtonText() {
        return mButtonText;
    }

    public void setButtonText(String buttonText) {
        mButtonText = buttonText;
        invalidate();
    }

    public int getBkgColor() {
        return mBkgColor;
    }

    public void setBkgColor(int bkgColor) {
        mBkgColor = bkgColor;
        invalidate();
    }

    public int getTextHorizontalPadding() {
        return mTextHorizontalPadding;
    }

    public void setTextHorizontalPadding(int textHorizontalPadding) {
        mTextHorizontalPadding = textHorizontalPadding;
        invalidate();
    }

    public int getCornorRadius() {
        return mCornorRadius;
    }

    public void setCornorRadius(int cornorRadius) {
        mCornorRadius = cornorRadius;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//        mTextPaint.setStrokeWidth(0.0f);
        mButtonRectF.setEmpty();
        mTextPaint.setColor(mBkgColor);
        mRectF.set(0, 0, getWidth(), getHeight());
        if (mCornorRadius <= 0) {
            canvas.drawRect(mRectF, mTextPaint);
        } else {
            canvas.drawRoundRect(mRectF, mCornorRadius, mCornorRadius, mTextPaint);
        }

//        if (isSelected()) {
//            mTextPaint.setColor(mSelectColor);
//            mTextPaint.setStrokeWidth(mStrokeWidth);
//            mTextPaint.setStyle(Paint.Style.STROKE);
//
//            Paint paint = mShapeDrawable.getPaint();
//            paint.setColor(mSelectColor);
//            paint.setStyle(Paint.Style.FILL);
//            mShapeDrawable.draw(canvas);
//
//            mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//            mTextPaint.setStrokeWidth(0.0f);
//        }

        mTextPaint.setColor(mProgressColor);
        if (mValue < 0) {
            mValue = 0;
        } else if (mValue > mMaxValue) {
            mValue = mMaxValue;
        }
        float drawWidth = getWidth();
        if (mMaxValue <= 0) {
            drawWidth = 0;
        } else {
            drawWidth = drawWidth * mValue / mMaxValue;
        }

        mRectF.set(0, 0, drawWidth, getHeight());
        if (mCornorRadius <= 0) {
            canvas.drawRect(mRectF, mTextPaint);
        } else {
//          裁切形式
            canvas.save(Canvas.CLIP_SAVE_FLAG);
            canvas.clipRect(mRectF);
            mRectF.right = getWidth();
            canvas.drawRoundRect(mRectF, mCornorRadius, mCornorRadius, mTextPaint);
            canvas.restore();
        }

        int baseY = getHeight() - getPaddingBottom() - mFontOffset;
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        if (!StringUtils.isEmpty(mLeftText)) {
            mTextPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(mLeftText, mTextHorizontalPadding, baseY, mTextPaint);
        }
        if (!StringUtils.isEmpty(mCenterText)) {
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(mCenterText, getWidth() / 2, baseY, mTextPaint);
        }
        if (mButtonWidth == 0) {
            if (!StringUtils.isEmpty(mRightText)) {
                mTextPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(mRightText, getWidth() - mTextHorizontalPadding, baseY, mTextPaint);
            }
        } else {
            mTextPaint.setColor(mButtonColor);
            mButtonRectF.set(0, 0, getWidth(), getHeight());
            if (mButtonWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
                float textWidth = mRightText != null ? mTextPaint.measureText(mButtonText) : 10.0f;
                mButtonRectF.left = mButtonRectF.right - textWidth - mCornorRadius * 8;
            } else if (mButtonWidth > 0) {
                mButtonRectF.left = mButtonRectF.right - mButtonWidth;
            }
            if (mCornorRadius <= 0) {
                canvas.drawRect(mButtonRectF, mTextPaint);
            } else {
                canvas.drawRoundRect(mButtonRectF, mCornorRadius, mCornorRadius, mTextPaint);
            }
            if (mButtonText != null) {
                mTextPaint.setColor(mTextColor);
                mTextPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(mButtonText, mButtonRectF.centerX(), baseY, mTextPaint);
            }
        }
    }

    private void calcFontOffset() {
        int lineHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float fontHeightNormal = fontMetrics.bottom - fontMetrics.top;
        mFontOffset = (int)((lineHeight - fontHeightNormal) / 2 + fontMetrics.bottom + 0.5f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
//            w -= getPaddingLeft() + getPaddingRight();
//            h -= getPaddingTop() + getPaddingBottom();
            calcFontOffset();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

}
