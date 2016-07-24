package com.njnu.kai.test.guideanimator;

import android.animation.*;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;

import java.util.ArrayList;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-8-13
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GuideAnimatorView extends View implements ValueAnimator.AnimatorUpdateListener {

    private Bitmap mBitmap;
    private Bitmap[] mBitmaps;

    private final ArrayList<GuideHolder> mGuideHolderList = new ArrayList<GuideHolder>();

    private static final int VERTICAL_TRANSLATE_ANIMATION_DURATION = 500;
    private static final int HORIZONTAL_TRANSLATE_ANIMATION_DURATION = 1500;
    private static final int FADE_ANIMATION_DURATION = 300;

    private static final long DRAW_INTERVAL = 50 * 1000 * 1000;

    private long mNanoTime;

    /**
     * @param context context
     */
    public GuideAnimatorView(Context context) {
        super(context);
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    /**
     * @param resIds resource ids
     */
    public void setDrawableResourceId(int... resIds) {
        mBitmap = null;
        int length = resIds != null ? resIds.length : 0;
        mBitmaps = new Bitmap[length];
        for (int idx = 0; idx < length; ++idx) {
            BitmapDrawable drawable = (BitmapDrawable)getResources().getDrawable(resIds[idx]);
            if (mBitmap == null) {
                mBitmap = drawable.getBitmap();
            }
            mBitmaps[idx] = drawable.getBitmap();
        }
    }

    private GuideHolder makeChicken(int x, int y) {
        GuideHolder shapeHolder = new GuideHolder(mBitmaps);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shapeHolder.setPaint(paint);
        shapeHolder.setX(x);
        shapeHolder.setY(y);
        return shapeHolder;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (GuideHolder guideHolder : mGuideHolderList) {
            canvas.save();
            canvas.translate(guideHolder.getX(), guideHolder.getY());
            guideHolder.drawBitmap(canvas);
            canvas.restore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return false;
        }
        makeChickenAnimation((int)event.getX(), (int)event.getY());
        return false;
    }

    /**
     * @param x x position
     * @param y y position
     * @return 是否成功添加了一个动画
     */
    public boolean makeChickenAnimation(int x, int y) {
        GuideHolder chicken = makeChicken(x, y);

        int startY = chicken.getY();
        int bitmapHeight = mBitmap.getHeight();
        int endY = getHeight() - (bitmapHeight >> 1);
        int runHeight = endY - startY;
        if (runHeight < bitmapHeight) {
            return false;
        }
        mGuideHolderList.add(chicken);

        ValueAnimator stretchWidthAnimator = ObjectAnimator.ofInt(chicken, "width", 0, mBitmap.getWidth());
        stretchWidthAnimator.setDuration(FADE_ANIMATION_DURATION);
        stretchWidthAnimator.addUpdateListener(this);

        ValueAnimator stretchHeightAnimator = ObjectAnimator.ofInt(chicken, "height", 0, mBitmap.getHeight());
        stretchHeightAnimator.setDuration(FADE_ANIMATION_DURATION);
        stretchHeightAnimator.addUpdateListener(this);

        ValueAnimator fadeInAnimator = ObjectAnimator.ofFloat(chicken, "alpha", 0.0f, 1.0f);
        fadeInAnimator.setDuration(FADE_ANIMATION_DURATION);
        fadeInAnimator.addUpdateListener(this);

//        int verticalTranslateDuration = (int)(VERTICAL_TRANSLATE_ANIMATION_DURATION * (1.0f * runHeight / (getHeight() - bitmapHeight)));
        ValueAnimator verticalTranslateAnim = ObjectAnimator.ofInt(chicken, "y", startY, endY);
        verticalTranslateAnim.setDuration(VERTICAL_TRANSLATE_ANIMATION_DURATION);
        verticalTranslateAnim.setInterpolator(new AccelerateInterpolator());
        verticalTranslateAnim.addUpdateListener(this);

        ValueAnimator horizontalTranslateAnim = ObjectAnimator.ofInt(chicken, "x", chicken.getX(), -mBitmap.getWidth() / 2);
        horizontalTranslateAnim.setDuration(HORIZONTAL_TRANSLATE_ANIMATION_DURATION);
        horizontalTranslateAnim.setInterpolator(new AnticipateInterpolator());
        horizontalTranslateAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                GuideHolder guideHolder = (GuideHolder)((ObjectAnimator)animation).getTarget();
                guideHolder.setAnimationEnabled(true);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Object target = ((ObjectAnimator)animation).getTarget();
                mGuideHolderList.remove(target);
                invalidate();
            }
        });
        horizontalTranslateAnim.addUpdateListener(this);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(verticalTranslateAnim).after(fadeInAnimator);
        animatorSet.play(fadeInAnimator).with(stretchWidthAnimator);
        animatorSet.play(fadeInAnimator).with(stretchHeightAnimator);
        animatorSet.play(horizontalTranslateAnim).after(verticalTranslateAnim);
        animatorSet.start();
        return true;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        long curNanoTime = System.nanoTime();
        boolean draw = curNanoTime - mNanoTime > DRAW_INTERVAL;
        if (draw) {
            mNanoTime = curNanoTime;
            invalidate();
        }
    }

    /**
     * @author hongkai.qian
     * @version 1.0.0
     * @since 14-8-13
     */
    private final class GuideHolder {

        private int mX;
        private int mY;
        private int mWidth;
        private int mHeight;
        private Bitmap[] mBitmaps;
        private Paint mPaint;
        private Rect mBitmapRect;
        private int mCurIndex;
        private boolean mAnimationEnabled;

        private static final float MAX_COLOR_VALUE = 255.0f;
        private static final float FLOAT_AMEND_VALUE = 0.5f;

        /**
         * @param paint paint
         */
        public void setPaint(Paint paint) {
            mPaint = paint;
        }

        /**
         * @return paint
         */
        public Paint getPaint() {
            return mPaint;
        }

        /**
         * @param value x
         */
        public void setX(int value) {
            mX = value;
        }

        /**
         * @return x
         */
        public int getX() {
            return mX;
        }

        /**
         * @param value y
         */
        public void setY(int value) {
            mY = value;
        }

        /**
         * @return y
         */
        public int getY() {
            return mY;
        }

        /**
         * @param alpha alpha
         */
        public void setAlpha(float alpha) {
            mPaint.setAlpha((int)((alpha * MAX_COLOR_VALUE) + FLOAT_AMEND_VALUE));
        }

        /**
         * @return width
         */
        public int getWidth() {
            return mWidth;
        }

        /**
         * @param width width
         */
        public void setWidth(int width) {
            mWidth = width;
        }

        /**
         * @return height
         */
        public int getHeight() {
            return mHeight;
        }

        /**
         * @param height height
         */
        public void setHeight(int height) {
            mHeight = height;
        }

        /**
         * @param bitmaps bitmaps
         */
        public GuideHolder(Bitmap... bitmaps) {
            mBitmaps = bitmaps;
            mBitmapRect = new Rect(0, 0, mBitmaps[0].getWidth(), mBitmaps[0].getHeight());
        }

        /**
         * @param canvas canvas
         */
        public void drawBitmap(Canvas canvas) {
            int halfWidth = mWidth >> 1;
            int halfHeight = mHeight >> 1;
            if (mWidth >= mBitmapRect.width()) {
                canvas.drawBitmap(mBitmaps[mCurIndex], -halfWidth, -halfHeight, mPaint);
            } else {
                Rect drawRect = new Rect(-halfWidth, -halfHeight, halfWidth, halfHeight);
                canvas.drawBitmap(mBitmaps[mCurIndex], mBitmapRect, drawRect, mPaint);
            }
            if (mAnimationEnabled) {
                ++mCurIndex;
                if (mCurIndex >= mBitmaps.length) {
                    mCurIndex = 0;
                }
            }
        }

        /**
         * @param enabled enabled
         */
        public void setAnimationEnabled(boolean enabled) {
            mAnimationEnabled = enabled;
            if (!enabled) {
                mCurIndex = 0;
            }
        }
    }
}
