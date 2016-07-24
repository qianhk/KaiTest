package com.njnu.kai.test.menu.draglayout;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.DisplayUtils;
import com.njnu.kai.test.support.LogUtils;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-04-12
 */
public class SlidingMenu extends FrameLayout {

    private static final String TAG = "SlidingMenu";

    private GestureDetectorCompat mGestureDetector;
    private ViewDragHelper mDragHelper;
    private DragListener mDragListener;
    private int mRange;
    private int mWidth;
    private int mHeight;
    private int mMainLeft;
    private ImageView mIvShadow;
    private ViewGroup mVgLeft;
    private MenuMainFrameLayout mVgMain;
    private Status mStatus = Status.Close;

    private static final float LEFT_RANGE_PERCENT = 0.7f;
    private static final float MAIN_SCALE_PERCENT = 0.8f;
    private static final float LEFT_SCALE_PERCENT = 0.5f;

    private static final int MARGIN_THRESHOLD = 30;

    private int mOriLeftPaddingRight;
    private int mMarginThreshold;

    private float mCurMainPercent = 1.0f;

    private static final int DEFAULT_SHADOW_WIDTH = 10;

    private int mShadowWidth;

    private Drawable mLeftShadowDrawable;
    private Drawable mTopShadowDrawable;
//    private Drawable mRightShadowDrawable;
    private Drawable mBottomShadowDrawable;
    private Drawable mLeftTopShadowDrawable;
    private Drawable mLeftBottomShadowDrawable;

    /**
     * @param context context
     */
    public SlidingMenu(Context context) {
        super(context);
        init(context);
    }

    /**
     * @param context context
     * @param attrs   attrs
     */
    public SlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * @param context  context
     * @param attrs    attrs
     * @param defStyle def style
     */
    public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mGestureDetector = new GestureDetectorCompat(context, new YScrollDetector());
        mDragHelper = ViewDragHelper.create(this, mDragHelperCallback);
        mMarginThreshold = DisplayUtils.dp2px(MARGIN_THRESHOLD);
        mShadowWidth = DisplayUtils.dp2px(DEFAULT_SHADOW_WIDTH);
        Resources resources = context.getResources();
        mLeftShadowDrawable = resources.getDrawable(R.drawable.img_shadow_left);
        mTopShadowDrawable = resources.getDrawable(R.drawable.img_shadow_top);
//        mRightShadowDrawable = resources.getDrawable(R.drawable.xml_shadow_right);
        mBottomShadowDrawable = resources.getDrawable(R.drawable.img_shadow_bottom);
        mLeftTopShadowDrawable = resources.getDrawable(R.drawable.img_shadow_left_top);
        mLeftBottomShadowDrawable = resources.getDrawable(R.drawable.img_shadow_left_bottom);

    }

    private class YScrollDetector extends SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
            return Math.abs(dy) <= Math.abs(dx);
        }
    }

    private ViewDragHelper.Callback mDragHelperCallback = new ViewDragHelper.Callback() {

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (mMainLeft + dx < 0) {
                return 0;
            } else if (mMainLeft + dx > mRange) {
                return mRange;
            } else {
                return left;
            }
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mWidth;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (xvel > 0) {
                open();
            } else if (xvel < 0) {
                close();
            } else if (releasedChild == mVgMain && mMainLeft > mRange * 0.3) {
                open();
            } else if (releasedChild == mVgLeft && mMainLeft > mRange * 0.7) {
                open();
            } else {
                close();
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            if (changedView == mVgMain) {
                mMainLeft = left;
            } else {
                mMainLeft = mMainLeft + left;
            }
            if (mMainLeft < 0) {
                mMainLeft = 0;
            } else if (mMainLeft > mRange) {
                mMainLeft = mRange;
            }

            if (changedView == mVgLeft) {
                mVgLeft.layout(0, 0, mWidth, mHeight);
                mVgMain.layout(mMainLeft, 0, mMainLeft + mWidth, mHeight);
            }

            dispatchDragEvent(mMainLeft);
        }
    };

    /**
     * drag listener
     */
    public interface DragListener {
        /**
         * menu open
         */
        public void onOpen();

        /**
         * menu close
         */
        public void onClose();

        /**
         * @param percent percent
         */
        public void onDrag(float percent);
    }

    /**
     * @param dragListener drag listener
     */
    public void setDragListener(DragListener dragListener) {
        this.mDragListener = dragListener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mVgLeft = (ViewGroup) getChildAt(0);
        mVgMain = (MenuMainFrameLayout) getChildAt(1);
        mVgMain.setDragLayout(this);
        mVgLeft.setClickable(true);
        mVgMain.setClickable(true);
        mOriLeftPaddingRight = mVgLeft.getPaddingRight();
    }
//
//    public ViewGroup getVgMain() {
//        return mVgMain;
//    }
//
//    public ViewGroup getVgLeft() {
//        return mVgLeft;
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = mVgLeft.getMeasuredWidth();
        mHeight = mVgLeft.getMeasuredHeight();
        mRange = (int) (mWidth * LEFT_RANGE_PERCENT);
        mVgLeft.setPadding(mVgLeft.getPaddingLeft(), mVgLeft.getPaddingTop()
                , mOriLeftPaddingRight + mWidth - mRange - ((int) (mWidth - mWidth * MAIN_SCALE_PERCENT) >> 1), mVgLeft.getPaddingBottom());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mVgLeft.layout(0, 0, mWidth, mHeight);
        mVgMain.layout(mMainLeft, 0, mMainLeft + mWidth, mHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean marginTouch = getStatus() != Status.Close || ev.getX() < mMarginThreshold;
//        LogUtils.d(TAG, "onInterceptTouchEvent status=%s x=%.2f rawX=%.2f margin=%b", mStatus.name(), ev.getX(), ev.getRawX(), marginTouch);
        return marginTouch && mDragHelper.shouldInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        try {
            mDragHelper.processTouchEvent(e);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void dispatchDragEvent(int mainLeft) {
        float percent = mainLeft / (float) mRange;
        animateView(percent);

        if (mDragListener == null) {
            return;
        }
        mDragListener.onDrag(percent);
        Status lastStatus = mStatus;
        if (lastStatus != getStatus() && mStatus == Status.Close) {
            mDragListener.onClose();
        } else if (lastStatus != getStatus() && mStatus == Status.Open) {
            mDragListener.onOpen();
        }
    }

    private void animateView(float percent) {
        mCurMainPercent = 1.0f - percent * (1.0f - MAIN_SCALE_PERCENT);
        mVgMain.setScaleX(mCurMainPercent);
        mVgMain.setScaleY(mCurMainPercent);

        mVgLeft.setTranslationX(-mVgLeft.getWidth() / 2.3f + mVgLeft.getWidth() / 2.3f * percent);

        mVgLeft.setScaleX(LEFT_SCALE_PERCENT + (1.0f - LEFT_SCALE_PERCENT) * percent);
        mVgLeft.setScaleY(LEFT_SCALE_PERCENT + (1.0f - LEFT_SCALE_PERCENT) * percent);
        mVgLeft.setAlpha(percent);

        Drawable background = getBackground();
        if (background != null) {
            background.setColorFilter(evaluateColor(percent, Color.BLACK, Color.TRANSPARENT), Mode.SRC_OVER);
        }
    }

    public static int evaluateColor(float percent, int startColor, int endColor) {
        int startA = startColor >>> 24;
        int startR = (startColor >> 16) & 0xff;
        int startG = (startColor >> 8) & 0xff;
        int startB = startColor & 0xff;
        int endA = endColor >>> 24;
        int endR = (endColor >> 16) & 0xff;
        int endG = (endColor >> 8) & 0xff;
        int endB = endColor & 0xff;
        return ((startA + (int) (percent * (endA - startA))) << 24)
                | ((startR + (int) (percent * (endR - startR))) << 16)
                | ((startG + (int) (percent * (endG - startG))) << 8)
                | ((startB + (int) (percent * (endB - startB))));
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * menu mStatus
     */
    public enum Status {
        /**
         * drag
         */
        Drag
        /** open */
        , Open
        /** close */
        , Close
    }

    /**
     * @return menu status
     */
    public Status getStatus() {
        if (mMainLeft == 0) {
            mStatus = Status.Close;
        } else if (mMainLeft == mRange) {
            mStatus = Status.Open;
        } else {
            mStatus = Status.Drag;
        }
        return mStatus;
    }

    /**
     * open menu
     */
    public void open() {
        open(true);
    }

    /**
     * @param animate animate
     */
    public void open(boolean animate) {
        if (animate) {
            if (mDragHelper.smoothSlideViewTo(mVgMain, mRange, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            mVgMain.layout(mRange, 0, mRange * 2, mHeight);
            dispatchDragEvent(mRange);
        }
    }

    /**
     * close
     */
    public void close() {
        close(true);
    }

    /**
     * @param animate animate
     */
    public void close(boolean animate) {
        if (animate) {
            if (mDragHelper.smoothSlideViewTo(mVgMain, 0, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            mVgMain.layout(0, 0, mWidth, mHeight);
            dispatchDragEvent(0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawShadow(mVgMain, canvas);
    }

    private void drawShadow(View content, Canvas canvas) {
        int left = content.getLeft(), top = content.getTop(), right = content.getRight(), bottom = content.getBottom();
        int width = right - left;
        int height = bottom - top;
        float reducePercent = 1 - mCurMainPercent;

        int widthRecude = (int)(width * reducePercent / 2 + 0.5f);
        left += widthRecude;
        int shadowLeft = left - mShadowWidth;

        int heightReduce = (int)(height * reducePercent / 2 + 0.5f);
        top += heightReduce;
        bottom -= heightReduce;
        int shadowTop = top - mShadowWidth;
        int shadowBottom = bottom + mShadowWidth;

        LogUtils.d(TAG, "lookDraw onDraw left=%d top=%d percent=%.2f wR=%d tR=%d", left, top, mCurMainPercent, widthRecude, heightReduce);

        if (mLeftShadowDrawable != null) {
            mLeftShadowDrawable.setBounds(shadowLeft, top, left, bottom);
            mLeftShadowDrawable.draw(canvas);
        }
//        if (mRightShadowDrawable != null) {
//            mRightShadowDrawable.setBounds(right, top, shadowRight, bottom);
//            mRightShadowDrawable.draw(canvas);
//        }
        if (mTopShadowDrawable != null) {
            mTopShadowDrawable.setBounds(left, shadowTop, right, top);
            mTopShadowDrawable.draw(canvas);
        }

        if (mBottomShadowDrawable != null) {
            mBottomShadowDrawable.setBounds(left, bottom, right, shadowBottom);
            mBottomShadowDrawable.draw(canvas);
        }


        if (mLeftTopShadowDrawable != null) {
            mLeftTopShadowDrawable.setBounds(shadowLeft, shadowTop, left, top);
            mLeftTopShadowDrawable.draw(canvas);
        }


        if (mLeftBottomShadowDrawable != null) {
            mLeftBottomShadowDrawable.setBounds(shadowLeft, bottom, left, shadowBottom);
            mLeftBottomShadowDrawable.draw(canvas);
        }
    }
}
