package com.njnu.kai.test.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.text.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.DisplayUtils;
import com.njnu.kai.test.support.StringUtils;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-8-25
 */
public class IconTextView extends View {

    private static final float FLOAT_AMEND_VALUE = 0.5f;
    private static final float DEFAULT_TEXT_SIZE = 14.0f;

    private int mDrawableWidth;
    private int mDrawableHeight;
    private Matrix mDrawMatrix;
    private Matrix mMatrix;
    private ImageView.ScaleType mScaleType;
    private RectF mTempSrc = new RectF();
    private RectF mTempDst = new RectF();
    private Drawable mDrawable;
    private Drawable mCheckedDrawable;

    private TextPaint mTextPaint;
    private boolean mFixedTextSize;
    private int mFontOffset;
    private String mText;
    private ColorStateList mTextColor;

    private String mCheckedText;
    private ColorStateList mCheckedTextColor;

    private String mBkgText;
    private int mBkgTextColor;

    private OnCheckedChangeListener mOnCheckedChangeListener;
    private boolean mCheckable;
    private boolean mChecked;

    public boolean mLog = false;

    /**
     * @param context context
     */
    public IconTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    /**
     * @param context context
     * @param attrs   attrs
     */
    public IconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    /**
     * @param context  context
     * @param attrs    attrs
     * @param defStyle default style
     */
    public IconTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {

        CharSequence arrtText = null;
        ColorStateList attrTextColor = null;
        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE, context.getResources().getDisplayMetrics());
        int attrTextSize = (int)textSize;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconTextView, defStyle, 0);
            for (int i = (a != null ? (a.getIndexCount() - 1) : -1); i >= 0; i--) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.IconTextView_text) {
                    arrtText = a.getText(attr);
                } else if (attr == R.styleable.IconTextView_textColor) {
                    attrTextColor = a.getColorStateList(attr);
                } else if (attr == R.styleable.IconTextView_textSize) {
                    attrTextSize = a.getDimensionPixelSize(attr, attrTextSize);
                    mFixedTextSize = true;
                } else if (attr == R.styleable.IconTextView_bkgText) {
                    mBkgText = a.getText(attr).toString();
                } else if (attr == R.styleable.IconTextView_bkgTextColor) {
                    mBkgTextColor = a.getColorStateList(attr).getDefaultColor();
                }
            }
        }

        mMatrix = new Matrix();
        mScaleType = ScaleType.FIT_CENTER;
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(DisplayUtils.getIconTypeFace());
        setTextColor(attrTextColor != null ? attrTextColor : ColorStateList.valueOf(Color.MAGENTA));
        setTextSize(attrTextSize);
        setText(arrtText != null ? arrtText.toString() : null);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setChecked(!mChecked, true);
        }
    };

    /**
     * @param checkable 可选择状态
     */
    public void setCheckable(boolean checkable) {
        mCheckable = checkable;
        setClickable(checkable);
        setOnClickListener(checkable ? mOnClickListener : null);
    }

    /**
     * @param text text
     */
    public void setText(String text) {
        mText = text;
        mDrawable = null;
        mCheckedDrawable = null;
        requestLayout();
        invalidate();
    }

    /**
     * @param textResId text resource id
     */
    public void setText(int textResId) {
        setText(getContext().getString(textResId));
    }

    /**
     * @param text        text
     * @param checkedText checked text
     */
    public void setText(String text, String checkedText) {
        mText = text;
        mCheckedText = checkedText;
        mDrawable = null;
        mCheckedDrawable = null;
        requestLayout();
        invalidate();
    }

    /**
     * @param textResId        text resource id
     * @param checkedTextResId checked text resource id
     */
    public void setText(int textResId, int checkedTextResId) {
        Resources resources = getContext().getResources();
        setText(resources.getString(textResId), resources.getString(checkedTextResId));
    }

    /**
     * @param color 设置颜色
     */

    public void setTextColor(int color) {
        setTextColor(ColorStateList.valueOf(color));
    }

    /**
     * @param colors colors
     */
    public void setTextColor(ColorStateList colors) {
        if (colors == null) {
            throw new NullPointerException();
        }

        mTextColor = colors;
        invalidate();
    }

    /**
     * @param color        设置颜色
     * @param checkedColor 选中颜色
     */

    public void setTextColor(int color, int checkedColor) {
        setTextColor(ColorStateList.valueOf(color), ColorStateList.valueOf(checkedColor));
    }

    /**
     * @param colors        colors
     * @param checkedColors 选中颜色
     */
    public void setTextColor(ColorStateList colors, ColorStateList checkedColors) {
        if (colors == null || checkedColors == null) {
            throw new NullPointerException();
        }

        mTextColor = colors;
        mCheckedTextColor = checkedColors;
        invalidate();
    }

    /**
     * @param bkgTextColor background text color
     */
    public void setBkgTextColor(int bkgTextColor) {
        mBkgTextColor = bkgTextColor;
        invalidate();
    }

    /**
     * @param resId resource id
     */
    public void setImageResource(int resId) {
        if (resId != 0) {
            setImageDrawable(getContext().getResources().getDrawable(resId));
        } else {
            setImageDrawable(null);
        }
    }

    /**
     * Sets a drawable as the content of this ImageView.
     *
     * @param drawable The drawable to set
     */
    public void setImageDrawable(Drawable drawable) {
        if (mDrawable != drawable) {
            final int oldWidth = mDrawableWidth;
            final int oldHeight = mDrawableHeight;
            updateDrawable(drawable);
            if (oldWidth != mDrawableWidth || oldHeight != mDrawableHeight) {
                requestLayout();
            }
            invalidate();
        }
        mText = null;
    }

    /**
     * @param resId        resource id
     * @param checkedResId checked resource id
     */
    public void setImageResource(int resId, int checkedResId) {
        Resources resources = getContext().getResources();
        setImageDrawable(resId != 0 ? resources.getDrawable(resId) : null, checkedResId != 0 ? resources.getDrawable(checkedResId) : null);
    }

    /**
     * Sets a drawable as the content of this ImageView.
     *
     * @param drawable        The drawable to set
     * @param checkedDrawable checked drawable
     */
    public void setImageDrawable(Drawable drawable, Drawable checkedDrawable) {
        setImageDrawable(drawable);
        if (mCheckedDrawable != checkedDrawable) {
            if (mCheckedDrawable != null) {
                mCheckedDrawable.setCallback(null);
                unscheduleDrawable(mCheckedDrawable);
            }
            mCheckedDrawable = checkedDrawable;
            if (checkedDrawable != null) {
                checkedDrawable.setCallback(this);
                if (checkedDrawable.isStateful()) {
                    checkedDrawable.setState(getDrawableState());
                }
                checkedDrawable.setVisible(getVisibility() == VISIBLE, true);
                if (mDrawable != null) {
                    mCheckedDrawable.setBounds(mDrawable.getBounds());
                }
            }
        }
    }

    private void updateDrawable(Drawable d) {
        if (mDrawable != null) {
            mDrawable.setCallback(null);
            unscheduleDrawable(mDrawable);
        }
        mDrawable = d;
        if (d != null) {
            d.setCallback(this);
            if (d.isStateful()) {
                d.setState(getDrawableState());
            }
            d.setVisible(getVisibility() == VISIBLE, true);
            mDrawableWidth = d.getIntrinsicWidth();
            mDrawableHeight = d.getIntrinsicHeight();
            configureBounds();
        } else {
            mDrawableWidth = -1;
            mDrawableHeight = -1;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isUseDrawable()) {
            drawDrawable(canvas);
        } else {
            drawText(canvas);
        }
    }

    private boolean isUseDrawable() {
        return StringUtils.isEmpty(mText) && StringUtils.isEmpty(mCheckedText);
    }

    private void drawText(Canvas canvas) {
        int paddingLeft = getPaddingLeft();
        int width = getWidth() - paddingLeft - getPaddingRight();
        int height = getHeight() - getPaddingBottom();
        if (mFixedTextSize) {
//            mTextPaint.setStyle(Paint.Style.STROKE);
//            mTextPaint.setColor(Color.RED);
//            canvas.drawRect(paddingLeft, getPaddingTop(), paddingLeft + width, height, mTextPaint);
            canvas.save();
            canvas.clipRect(paddingLeft, getPaddingTop(), paddingLeft + width, height);
        }
        int baseY = height - mFontOffset;
        if (!StringUtils.isEmpty(mBkgText)) {
            mTextPaint.setColor(mBkgTextColor);
            canvas.drawText(mBkgText, paddingLeft + (width >> 1), baseY, mTextPaint);
        }
        ColorStateList colorStateList = mCheckable && mChecked && mCheckedTextColor != null ? mCheckedTextColor : mTextColor;
        int color = colorStateList.getColorForState(getDrawableState(), colorStateList.getDefaultColor());
        mTextPaint.setColor(color);
        String text = mCheckable && mChecked ? mCheckedText : mText;
        if (text == null) {
            text = "";
        }
        canvas.drawText(text, paddingLeft + (width >> 1), baseY, mTextPaint);
        if (mFixedTextSize) {
            canvas.restore();
        }
        if (!mLog) {
            return;
        }
        int posX1 = 0;
        int posX2 = posX1 + getWidth();
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float topY = baseY + fontMetrics.top;
        float ascentY = baseY + fontMetrics.ascent;
        float descentY = baseY + fontMetrics.descent;
        float bottomY = baseY + fontMetrics.bottom;
        mTextPaint.setColor(Color.BLUE);
//        canvas.drawLine(posX1, baseY, posX2, baseY, mTextPaint);
//
////        mTextPaint.setColor(Color.LTGRAY);
////        canvas.drawLine(posX1, topY, posX2, topY, mTextPaint);
//
//        canvas.drawLine(posX1, ascentY, posX2, ascentY, mTextPaint);
//
//        canvas.drawLine(posX1, descentY, posX2, descentY, mTextPaint);

//        mTextPaint.setColor(Color.MAGENTA);
//        mTextPaint.setColor(Color.RED);
//        canvas.drawLine(posX1, bottomY, posX2, bottomY, mTextPaint);
//        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setColor(Color.RED);
        RectF rect = new RectF(paddingLeft, getPaddingTop(), paddingLeft + width, height);
        canvas.drawCircle(rect.centerX(), rect.centerY(), 16, mTextPaint);
        rect.top = topY;
        rect.bottom = bottomY;

        mTextPaint.setColor(Color.CYAN);
        canvas.drawCircle(rect.centerX(), rect.centerY(), 16, mTextPaint);
    }

    private void drawDrawable(Canvas canvas) {
        Drawable drawable = mCheckable && mChecked ? mCheckedDrawable : mDrawable;
        if (drawable == null || mDrawableWidth == 0 || mDrawableHeight == 0) {
            return;
        }

        int paddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();
        if (mDrawMatrix == null && paddingTop == 0 && paddingLeft == 0) {
            drawable.draw(canvas);
        } else {
            int saveCount = canvas.getSaveCount();
            canvas.save();

            canvas.translate(paddingLeft, paddingTop);

            if (mDrawMatrix != null) {
                canvas.concat(mDrawMatrix);
            }
            drawable.draw(canvas);
            canvas.restoreToCount(saveCount);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isUseDrawable()) {
            onMeasureDrawable(widthMeasureSpec, heightMeasureSpec);
        } else {
            onMeasureText(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            w -= getPaddingLeft() + getPaddingRight();
            h -= getPaddingTop() + getPaddingBottom();
            configureBounds();
            if (!mFixedTextSize) {
                setTextSize(Math.min(w, h));
            }
            calcFontOffset();
        }
    }

    private void setTextSize(float textSize) {
        mTextPaint.setTextSize(textSize);
    }

    private void calcFontOffset() {
        int lineHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float fontHeightNormal = fontMetrics.bottom - fontMetrics.top;
        mFontOffset = (int)((lineHeight - fontHeightNormal) / 2 + fontMetrics.bottom + FLOAT_AMEND_VALUE);
    }

    private static final Matrix.ScaleToFit[] SCALE_TO_FIT_ARRAY = {
            Matrix.ScaleToFit.FILL,
            Matrix.ScaleToFit.START,
            Matrix.ScaleToFit.CENTER,
            Matrix.ScaleToFit.END
    };

    private static Matrix.ScaleToFit scaleTypeToScaleToFit(ScaleType st) {
        // ScaleToFit enum to their corresponding Matrix.ScaleToFit values
        return SCALE_TO_FIT_ARRAY[st.ordinal() - 1];
    }

    private void onMeasureDrawable(int widthMeasureSpec, int heightMeasureSpec) {
        int w;
        int h;

        if (mDrawable == null) {
            // If no drawable, its intrinsic size is 0.
            mDrawableWidth = -1;
            mDrawableHeight = -1;
            w = 0;
            h = 0;
        } else {
            w = mDrawableWidth;
            h = mDrawableHeight;
            if (w <= 0) {
                w = 1;
            }
            if (h <= 0) {
                h = 1;
            }
        }

        int pleft = getPaddingLeft();
        int pright = getPaddingRight();
        int ptop = getPaddingTop();
        int pbottom = getPaddingBottom();

        w += pleft + pright;
        h += ptop + pbottom;

        w = Math.max(w, getSuggestedMinimumWidth());
        h = Math.max(h, getSuggestedMinimumHeight());

        int widthSize = resolveSize(w, widthMeasureSpec);
        int heightSize = resolveSize(h, heightMeasureSpec);

        setMeasuredDimension(widthSize, heightSize);
    }

    private void configureBounds() {
        if (mDrawable == null) {
            return;
        }

        int dwidth = mDrawableWidth;
        int dheight = mDrawableHeight;

        int paddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();
        int vwidth = getWidth() - paddingLeft - getPaddingRight();
        int vheight = getHeight() - paddingTop - getPaddingBottom();

        boolean fits = (dwidth < 0 || vwidth == dwidth) && (dheight < 0 || vheight == dheight);

        if (dwidth <= 0 || dheight <= 0 || ScaleType.FIT_XY == mScaleType) {
            /* If the drawable has no intrinsic size, or we're told to
                scaletofit, then we just fill our entire view.
            */
            mDrawable.setBounds(0, 0, vwidth, vheight);
            mDrawMatrix = null;
        } else {
            // We need to do the scaling ourself, so have the drawable
            // use its native size.
            mDrawable.setBounds(0, 0, dwidth, dheight);

            if (ScaleType.MATRIX == mScaleType) {
                // Use the specified matrix as-is.
                if (mMatrix.isIdentity()) {
                    mDrawMatrix = null;
                } else {
                    mDrawMatrix = mMatrix;
                }
            } else if (fits) {
                // The bitmap fits exactly, no transform needed.
                mDrawMatrix = null;
            } else if (ScaleType.CENTER == mScaleType) {
                // Center bitmap in view, no scaling.
                mDrawMatrix = mMatrix;
                mDrawMatrix.setTranslate((int)((vwidth - dwidth) * FLOAT_AMEND_VALUE + FLOAT_AMEND_VALUE),
                        (int)((vheight - dheight) * FLOAT_AMEND_VALUE + FLOAT_AMEND_VALUE));
            } else if (ScaleType.CENTER_CROP == mScaleType) {
                mDrawMatrix = mMatrix;

                float scale;
                float dx = 0, dy = 0;

                if (dwidth * vheight > vwidth * dheight) {
                    scale = (float)vheight / (float)dheight;
                    dx = (vwidth - dwidth * scale) * FLOAT_AMEND_VALUE;
                } else {
                    scale = (float)vwidth / (float)dwidth;
                    dy = (vheight - dheight * scale) * FLOAT_AMEND_VALUE;
                }

                mDrawMatrix.setScale(scale, scale);
                mDrawMatrix.postTranslate((int)(dx + FLOAT_AMEND_VALUE), (int)(dy + FLOAT_AMEND_VALUE));
            } else if (ScaleType.CENTER_INSIDE == mScaleType) {
                mDrawMatrix = mMatrix;
                float scale;
                float dx;
                float dy;

                if (dwidth <= vwidth && dheight <= vheight) {
                    scale = 1.0f;
                } else {
                    scale = Math.min((float)vwidth / (float)dwidth,
                            (float)vheight / (float)dheight);
                }

                dx = (int)((vwidth - dwidth * scale) * FLOAT_AMEND_VALUE + FLOAT_AMEND_VALUE);
                dy = (int)((vheight - dheight * scale) * FLOAT_AMEND_VALUE + FLOAT_AMEND_VALUE);

                mDrawMatrix.setScale(scale, scale);
                mDrawMatrix.postTranslate(dx, dy);
            } else {
                // Generate the required transform.
                mTempSrc.set(0, 0, dwidth, dheight);
                mTempDst.set(0, 0, vwidth, vheight);

                mDrawMatrix = mMatrix;
                mDrawMatrix.setRectToRect(mTempSrc, mTempDst, scaleTypeToScaleToFit(mScaleType));
            }
        }
    }

    private void onMeasureText(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = paddingLeft + paddingRight;
            width += mTextPaint != null ? (int)(mTextPaint.measureText(mText) + FLOAT_AMEND_VALUE) : 0;
            width = Math.max(width, getSuggestedMinimumWidth());
            if (widthMode == MeasureSpec.AT_MOST) {
                width = Math.min(widthSize, width);
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = paddingTop + paddingBottom;
            height += mTextPaint != null ? (int)(mTextPaint.getTextSize() + FLOAT_AMEND_VALUE) : 0;
            height = Math.max(height, getSuggestedMinimumHeight());
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (isUseDrawable()) {
            if (mDrawable != null && mDrawable.isStateful()) {
                mDrawable.setState(getDrawableState());
                invalidate();
            }
            if (mCheckable && mChecked && mCheckedDrawable != null && mCheckedDrawable.isStateful()) {
                mCheckedDrawable.setState(getDrawableState());
                invalidate();
            }
        } else {
            if (mTextColor != null && mTextColor.isStateful()) {
                invalidate();
            }
            if (mCheckable && mChecked && mCheckedTextColor != null && mCheckedTextColor.isStateful()) {
                invalidate();
            }
        }
    }

    /**
     * @return is checked
     */
    public boolean isChecked() {
        return mChecked;
    }


    /**
     * @param checked checked
     */
    public void setChecked(boolean checked) {
        setChecked(checked, false);
    }

    private void setChecked(boolean checked, boolean fromUser) {
        if (mChecked != checked) {
            mChecked = checked;
            invalidate();
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, mChecked, fromUser);
            }
        }
    }

    @Override
    public void startAnimation(Animation animation) {
        if (DisplayUtils.getBitmapDensity() > DisplayMetrics.DENSITY_MEDIUM) {
            super.startAnimation(animation);
        }
    }

    @Override
    public void clearAnimation() {
        if (DisplayUtils.getBitmapDensity() > DisplayMetrics.DENSITY_MEDIUM) {
            super.clearAnimation();
        }
    }

    @Override
    public void setAnimation(Animation animation) {
        if (DisplayUtils.getBitmapDensity() > DisplayMetrics.DENSITY_MEDIUM) {
            super.setAnimation(animation);
        }
    }

    /**
     * Register a callback to be invoked when the checked state of this button
     * changes.
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    /**
     * Interface definition for a callback to be invoked when the checked state
     * of a compound button changed.
     */
    public static interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param buttonView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         * @param fromUser   is user changed
         */
        void onCheckedChanged(IconTextView buttonView, boolean isChecked, boolean fromUser);
    }
}
