package com.njnu.kai.test.support;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-8-8
 */
public class LineImageView extends ImageView {

    private float mLineOffsetPosition = 0.5f;
    private Paint mPaint;

    public LineImageView(Context context) {
        super(context);
    }

    public LineImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LineImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setColor(0xFFFF00FF);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(1.0f);
        }
        int height = (int)(getHeight() * mLineOffsetPosition);
        Rect rect = new Rect(8, height - 4, getRight() - 8, height + 4);
        canvas.drawRect(rect, mPaint);
    }

    public void setLineOffsetPosition(float lineOffsetPosition) {
        mLineOffsetPosition = lineOffsetPosition;
        invalidate();
    }
}
