package com.njnu.kai.test.view;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.njnu.kai.test.support.BitmapUtils;
import com.njnu.kai.test.support.LogUtils;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-7-22
 */
public class GlobalMenuThumbImageView extends ImageView {

    private Bitmap mThumbBitmap;
    private float mThumbOffset;
    private PorterDuffXfermode mMode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    private Paint mThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Drawable mThumbDrawable;

    /**
     * @param context context
     */
    public GlobalMenuThumbImageView(Context context) {
        super(context);
    }

    /**
     * @param context context
     * @param attrs attrs
     */
    public GlobalMenuThumbImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param context context
     * @param attrs attrs
     * @param defStyle default style
     */
    public GlobalMenuThumbImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mThumbBitmap = null;
    }

    /**
     * @param thumbDrawable 设置thumb drawable
     */
    public void setThumbDrawable(Drawable thumbDrawable) {
        mThumbDrawable = thumbDrawable;
        mThumbBitmap = null;
        invalidate();
    }

    private void prepareThumbPaint() {
        int thumbHeight = mThumbDrawable.getIntrinsicHeight();
        int height = getHeight();
        if (thumbHeight == height && mThumbDrawable instanceof BitmapDrawable) {
            mThumbBitmap = ((BitmapDrawable)mThumbDrawable).getBitmap();
        } else {
            float scaleRatio = 1.0f * height / thumbHeight;
            mThumbBitmap = BitmapUtils.drawableToBitmap(mThumbDrawable
                    , (int)(mThumbDrawable.getIntrinsicWidth() * scaleRatio), (int)(thumbHeight * scaleRatio));
        }
        mThumbPaint.setShader(new BitmapShader(mThumbBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mThumbDrawable == null) {
            super.onDraw(canvas);
            return;
        }

        if (mThumbBitmap == null) {
            prepareThumbPaint();
        }

        int height = getHeight();
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getWidth() - getPaddingRight();
        int bottom = height - getPaddingBottom();
        int sc = canvas.saveLayer(left, top, right, bottom, null, Canvas.ALL_SAVE_FLAG);
        super.onDraw(canvas);

        int thumbWidth = mThumbBitmap.getWidth();
        int thumbArea = getWidth() >> 1;
        int leftPosition = (thumbArea >> 1) + (int)(thumbArea * (1 - mThumbOffset)) - (thumbWidth >> 1);
        LogUtils.d("GlobalMenuThumbImageView", "GlobalMenuThumbImageView2 width=%d height=%d thumbWidth=%d height=%d"
                , getWidth(), height, thumbWidth, mThumbBitmap.getHeight());

        mThumbPaint.setXfermode(mMode);
        canvas.translate(leftPosition, 0);
        canvas.drawRect(0, 0, thumbWidth, height, mThumbPaint);
        mThumbPaint.setXfermode(null);

        canvas.restoreToCount(sc);
    }

    /**
     * @param offset 滑块偏移值
     */
    public void setThumbOffset(float offset) {
        mThumbOffset = offset;
        invalidate();
    }
}
