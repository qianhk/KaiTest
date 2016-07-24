package com.njnu.kai.test.grid;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * View for AZ navigation side bar
 *
 * @author hongkai.qian
 * @version 1.0.0
 * @since 2013-01-31
 */

public class AZSideBar extends View {
    /**
     * count的个数，count > 20 则显示，否则不显示
     */
    public static final int MIN_ITEMS_COUNT_IF_SHOW = 20;
    private static final float TEXT_SIZE = 12.0f;
    private static final int DEGREE_HALF_CIRCLE = 180;
    private static final int COLOR_BACKGROUND_NORMAL = 0x7f3f7785;
    private static final int COLOR_BACKGROUND_PRESSED = 0xe53f7785;
    private static final int COLOR_TEXT_NORMAL = 0x7fffffff;
    private OnLetterChangedListener mOnLetterChangedListener;
    private static String[] mSegmentChar = {"#", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z"};
    private int mChoose = -1;
    private Paint mPaint = new Paint();
    private boolean mShowBkg = false;

    /**
     * construct AZSideBar
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public AZSideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        Resources resource = context.getResources();
        float textSize = TEXT_SIZE;
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, resource.getDisplayMetrics());
        mPaint.setTextSize(textSize);
//		mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setAntiAlias(true);
    }

    /**
     * construct AZSideBar
     *
     * @param context context
     * @param attrs   attrs
     */
    public AZSideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * construct AZSideBar
     *
     * @param context context
     */
    public AZSideBar(Context context) {
        super(context);
        init(context);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);

        float height = getHeight();
        int width = getWidth();
        float singleHeight = (height - width) / mSegmentChar.length;
        int dy = width >> 1;
        for (int idx = 0; idx < mSegmentChar.length; ++idx) {
            mPaint.setColor(idx == mChoose ? Color.WHITE : COLOR_TEXT_NORMAL);
            mPaint.setFakeBoldText(idx == mChoose);

            float yPos = singleHeight * idx + singleHeight + dy;
            canvas.drawText(mSegmentChar[idx], dy, yPos, mPaint);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = mChoose;
        final OnLetterChangedListener listener = mOnLetterChangedListener;
        int width = getWidth();
        int height = getHeight();
        final int c = (int) ((y - (width >> 1)) / (height - width) * mSegmentChar.length);
//		Log.d("xx", "s:" + y + " c=" + c + " getHeight()" + getHeight() + " y/height=" + (y / getHeight()));

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mShowBkg = true;
                if (oldChoose != c && listener != null) {
                    if (c >= 0 && c < mSegmentChar.length) {
                        listener.onLetterChanged(mSegmentChar[c]);
                        mChoose = c;
                    }
                }
                invalidate();

                break;

            case MotionEvent.ACTION_MOVE:
                if (oldChoose != c && listener != null) {
                    if (c >= 0 && c < mSegmentChar.length) {
                        listener.onLetterChanged(mSegmentChar[c]);
                        mChoose = c;
                        invalidate();
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mShowBkg = false;
                mChoose = -1;
                invalidate();
                break;

            default:
                break;
        }
        return true;
    }

    private void drawBackground(Canvas canvas) {
        int color = mShowBkg ? COLOR_BACKGROUND_PRESSED : COLOR_BACKGROUND_NORMAL;
        int w = getWidth();
        int h = getHeight();
        int dx = w >> 1;
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(0, dx, w, h - dx, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        RectF rect = new RectF(0, 0, w, w);
        canvas.drawArc(rect, 0, -DEGREE_HALF_CIRCLE, true, paint);

        rect.set(0, h - w, w, h);
        canvas.drawArc(rect, 0, DEGREE_HALF_CIRCLE, true, paint);
    }

    /**
     * @param onLetterChangedListener listener
     */
    public void setOnLetterChangedListener(OnLetterChangedListener onLetterChangedListener) {
        mOnLetterChangedListener = onLetterChangedListener;
    }

    /**
     * letter changed listenen
     */
    public interface OnLetterChangedListener {
        /**
         * notify letter changed
         *
         * @param letter letter
         */
        public void onLetterChanged(String letter);
    }

}
