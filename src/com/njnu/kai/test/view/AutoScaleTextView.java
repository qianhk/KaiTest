package com.njnu.kai.test.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.DisplayUtils;
import com.njnu.kai.test.support.LogUtils;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-12-25
 */
public class AutoScaleTextView extends TextView {
    private static final String TAG = "AutoScaleTextView";
    private static final float FLOAT_AMEND_VALUE = 0.5f;
    private Paint mTextPaint;
    private String mText;
    private float mMinTextSize;
    private int mFontOffset;

    /**
     * @param context context
     */
    public AutoScaleTextView(Context context) {
        super(context);
        init(context, null, 0);
    }


    /**
     * @param context context
     * @param attrs   attrs
     */
    public AutoScaleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    /**
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public AutoScaleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoScaleTextView, defStyle, 0);
        mMinTextSize = a.getDimension(R.styleable.AutoScaleTextView_minTextSize, DisplayUtils.dp2px(4 << 1));
        a.recycle();

    }

    /**
     * Set the minimum text size for view
     *
     * @param minTextSize The minimum text size
     */
    public void setMinTextSize(float minTextSize) {
        mMinTextSize = minTextSize;
    }

    private void refitText(String text, int textWidth, int textHeight) {
        if (textWidth <= 0 || textHeight <= 0 || text == null || text.length() == 0) {
            return;
        }

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null && layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            return;
        }

        int targetWidth = textWidth - getPaddingLeft() - getPaddingRight();
        int targetHeight = textHeight - getPaddingTop() - getPaddingBottom() - DisplayUtils.dp2px(2);

        mTextPaint.setTextSize(targetHeight);
        if (mTextPaint.measureText(text) <= targetWidth) {
            calcFontOffset();
            setTextSize(TypedValue.COMPLEX_UNIT_PX, targetHeight);
            LogUtils.d(TAG, "direct set text size to %d", targetHeight);
        } else {
            float textSize = targetHeight - FLOAT_AMEND_VALUE;
            for (; textSize >= mMinTextSize; textSize -= FLOAT_AMEND_VALUE) {
                mTextPaint.setTextSize(textSize);
                if (mTextPaint.measureText(text) <= targetWidth) {
                    break;
                }
            }
            LogUtils.d(TAG, "set text size to %.2f", textSize);
            calcFontOffset();
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
    }

    private void calcFontOffset() {
        int lineHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float fontHeightNormal = fontMetrics.bottom - fontMetrics.top;
        mFontOffset = (int) ((lineHeight - fontHeightNormal) / 2 + fontMetrics.bottom + FLOAT_AMEND_VALUE + DisplayUtils.dp2px(3));
    }

//    /**
//     * @param text text
//     */
//    public void setText(String text) {
//        mText = text;
//        refitText(mText, getWidth(), getHeight());
//    }

    @Override
    protected void onSizeChanged(int width, int height, int oldwidth, int oldheight) {
        if (width != oldwidth) {
            refitText(mText, width, height);
        }
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        mText = text.toString();
        refitText(mText, getWidth(), getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        int paddingLeft = getPaddingLeft();
        int width = getWidth() - paddingLeft - getPaddingRight();
        int height = getHeight() - getPaddingBottom();
        mTextPaint.setColor(getCurrentTextColor());
        if (mText != null) {
            int gravity = getGravity();
            if (gravity == Gravity.CENTER) {
                mTextPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(mText, paddingLeft + (width >> 1), height - mFontOffset, mTextPaint);
            } else {
                mTextPaint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(mText, paddingLeft, height - mFontOffset, mTextPaint);
            }
        }
    }

}
