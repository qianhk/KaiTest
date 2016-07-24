package com.njnu.kai.test.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.View;
import com.njnu.kai.test.support.DisplayUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-4-15
 */
public class SignalView extends View {

    private SurfaceHolder mSurfaceHolder;
    private boolean mLoop;
    private final int mStrokeWidth = 2;
    private int mColor = Color.parseColor("#7ccded");
    private int mBkgColor = Color.parseColor("#F5FDFD");
    private final int mCircleCount = 4;
    private final static int MIN_RADIUS = 60;
    private int mMinRadius;
    private final static int FLUSH_INTERVAL = 50;
    private final static int SECTION_TIME = 1000;
    private int []mSteps;
    private final static float MAX_ALPHA = 255.0f;
    private int mTotalStep;
    private int mCx;
    private int mCy;
    private final static boolean USE_NORMAL_VIEW = true;
    private TimerTask mTimerTask;
    private Timer mTimer;

    /**
     * @param loop loop
     */
    public void setLoop(boolean loop) {
        mLoop = loop;
    }

    /**
     * @param context context
     */
    public SignalView(Context context) {
        super(context);
        init(context);
    }

    /**
     * @param context context
     * @param attrs attrs
     */
    public SignalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * @param context context
     * @param attrs attrs
     * @param defStyle defStyle
     */
    public SignalView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void initSteps() {
        int itemStep = mTotalStep / (mCircleCount - 1);
        for (int idx = 0; idx < mCircleCount; ++idx) {
            mSteps[idx] = -(itemStep * idx);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (USE_NORMAL_VIEW && mTimer != null) {
            mTimer.cancel();
        }
        super.onDetachedFromWindow();
    }

    private void init(Context context) {
        mMinRadius = DisplayUtils.dp2px(MIN_RADIUS);
        mTotalStep = SECTION_TIME / FLUSH_INTERVAL;
        mSteps = new int[mCircleCount];
        initSteps();
        mLoop = true;
        if (USE_NORMAL_VIEW) {
            initTimerSchedule();
        } else {
//            mSurfaceHolder = getHolder();
            mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
                }

                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    new Thread(new SignalThread()).start();
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    mLoop = false;
                }
            });
        }
    }

    private void initTimerSchedule() {
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (mLoop) {
                    postInvalidate();
                }
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, FLUSH_INTERVAL, FLUSH_INTERVAL);
    }

    private class SignalThread implements Runnable {
        @Override
        public void run() {
            while (mLoop) {
                doDrawLoop();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (USE_NORMAL_VIEW) {
            int height = getHeight();
            int width = getWidth();
            int maxRadius = Math.min(width, height) >> 1;
            mCy = height >> 1;
            mCx = width >> 1;
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(mStrokeWidth);
            paint.setColor(mColor);
            int stepDistance = (maxRadius - mMinRadius) / mTotalStep;
            for (int idx = 0; idx < mCircleCount; ++idx) {
                drawCircle(canvas, paint, idx, stepDistance);
            }
            if (mSteps[mCircleCount - 1] > mTotalStep) {
                initSteps();
                mTimer.cancel();
                initTimerSchedule();
            }
        }
    }

    private void doDrawLoop() {
        Canvas canvas = mSurfaceHolder.lockCanvas(null);
        int width = getWidth();
        int height = getHeight();
        int maxRadius = Math.min(width, height) >> 1;
        mCx = width >> 1;
        mCy = height >> 1;
        Paint paint = new Paint();
        Xfermode xfermode = paint.getXfermode();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(xfermode);
        canvas.drawColor(mBkgColor);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(mStrokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(mColor);
        int stepDistance = (maxRadius - mMinRadius) / mTotalStep;
        for (int idx = 0; idx < mCircleCount; ++idx) {
            drawCircle(canvas, paint, idx, stepDistance);
        }
        mSurfaceHolder.unlockCanvasAndPost(canvas);
        if (mSteps[mCircleCount - 1] > mTotalStep) {
            initSteps();
            sleep(SECTION_TIME >> 1);
        } else {
            sleep(FLUSH_INTERVAL);
        }
    }

    private void drawCircle(Canvas canvas, Paint paint, int position, int stepDistance) {
        int step = mSteps[position];
        if (step > mTotalStep) {
            return;
        }

        if (step > 0) {
            int curRadius = stepDistance * step + mMinRadius;
            int alpha;
            int halfStep = mTotalStep >> 1;
            if (step <= halfStep) {
                alpha = (int)(MAX_ALPHA * step / halfStep);
            } else {
                alpha = (int)(MAX_ALPHA * (mTotalStep - step) / halfStep);
            }
            paint.setAlpha(alpha);
            canvas.drawCircle(mCx, mCy, curRadius, paint);
        }
        ++mSteps[position];
    }

    private void sleep(int timeMs) {
        try {
            Thread.sleep(timeMs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
