package com.njnu.kai.test.owndan;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import com.njnu.kai.test.support.DisplayUtils;
import com.njnu.kai.test.support.LogUtils;
import com.njnu.kai.test.support.StringUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-2-10
 */
public class DanmakuView extends View {

    private static final String TAG = "BarrageView";

    private static final int DEFAULT_FONT_SIZE = 18; // sp
    private static final int TEXT_INTEVAL = 30; //dp
    private static final float CEIL_OF_FLOAT_OFFSET = 0.96f;
    private static final int ROWS = 3;
    private static final int DEFAULT_SPEED = 5;
    private static final int MAX_SPEED = 10;
    private static final int MAX_PURE_COLOR = 255;
    private static final int FLUSH_INTERVAL = 50;

    private float mTextSize = DisplayUtils.dp2px(DEFAULT_FONT_SIZE);
    private Typeface mTypeface;
    private int mSpeed = DEFAULT_SPEED;
    private int mTextInterval = DisplayUtils.dp2px(TEXT_INTEVAL);

    private int mDisplayWidth;
    private TextPaint mTextPaint;
    private int mLineHeight;
    private int mFontOffset;

    private Random mRandom;

    private ArrayList<String> mStringList = new ArrayList<String>();
    private Section mSection = new Section(ROWS);

    private static final int WHAT_FLUSH_SENTENCE = 1;
    private InnerHandler mHandler = new InnerHandler(this);

    /**
     * @param speed 显示速度，最低1，最高10
     */
    public void setSpeed(int speed) {
        mSpeed = speed >= 1 ? (speed <= MAX_SPEED ? speed : MAX_SPEED) : 1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mSection.onDraw(this, canvas);
    }

    private int getSpeedByPx() {
        return DisplayUtils.dp2px(mSpeed);
    }

    private static final class Sentence {
        public Sentence(DanmakuView view, String string) {
            mString = string;
            mPosition = view.mDisplayWidth + view.mTextInterval;
            mLength = view.measureText(mString);
            mColor = Color.rgb(view.mRandom.nextInt(MAX_PURE_COLOR), view.mRandom.nextInt(MAX_PURE_COLOR), view.mRandom.nextInt(MAX_PURE_COLOR));
        }

        private String mString;
        private int mPosition;
        private int mColor;
        private int mLength;

        private void onDraw(DanmakuView view, Canvas canvas, int bottom) {
            view.mTextPaint.setColor(mColor);
            canvas.drawText(mString, mPosition, bottom - view.mFontOffset, view.mTextPaint);
        }

        private boolean step(int speed) {
            mPosition -= speed;
            return (mPosition + mLength) < 0;
        }
    }

    private static final class Row {
        public Row(int row) {
            mRow = row;
        }

        private ArrayList<Sentence> mSentenceList;
        private int mRow;

        private boolean updateData(DanmakuView view, int speed) {
            if (mSentenceList == null) {
                mSentenceList = new ArrayList<Sentence>();
            }

            Sentence sentence;
            if (speed > 0) {
                for (int idx = mSentenceList.size() - 1; idx >= 0; --idx) {
                    sentence = mSentenceList.get(idx);
                    if (sentence.step(speed)) {
                        mSentenceList.remove(idx);
                    }
                }
            }

            int rowTail = 0;
            int size = mSentenceList.size();
            if (size > 0) {
                sentence = mSentenceList.get(size - 1);
                rowTail = sentence.mPosition + sentence.mLength;
            }
            if (rowTail <= view.mDisplayWidth && !view.mStringList.isEmpty()) {
                mSentenceList.add(new Sentence(view, view.mStringList.remove(0)));
            }
            return !mSentenceList.isEmpty();
        }

        private void onDraw(DanmakuView view, Canvas canvas) {
            int bottom = (mRow + 1) * view.mLineHeight;
            for (Sentence sentence : mSentenceList) {
                sentence.onDraw(view, canvas, bottom);
            }
        }
    }

    private static final class Section extends ArrayList<Row> {
        private Section(int rows) {
            for (int idx = 0; idx < rows; ++idx) {
                add(new Row(idx));
            }
        }

        private boolean updateData(DanmakuView view, boolean step) {
            int speed = view.getSpeedByPx();
            boolean hasData = false;
            for (int idx = size() - 1; idx >= 0; --idx) {
                Row row = get(idx);
                if (row.updateData(view, speed)) {
                    hasData = true;
                }
            }
            return hasData;
        }

        private void onDraw(DanmakuView barrageView, Canvas canvas) {
            for (int idx = size() - 1; idx >= 0; --idx) {
                Row row = get(idx);
                row.onDraw(barrageView, canvas);
            }
        }
    }

    /**
     * @param context context
     */
    public DanmakuView(Context context) {
        super(context);
        init(context);
    }

    /**
     * @param context context
     * @param attrs   attrs
     */
    public DanmakuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * @param context      context
     * @param attrs        attrs
     * @param defStyleAttr default style attribute
     */
    public DanmakuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * @param context      context
     * @param attrs        attrs
     * @param defStyleAttr default style attribute
     * @param defStyleRes  default style resource
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DanmakuView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mRandom = new Random(System.currentTimeMillis());
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setTypeface(mTypeface);
        mTextPaint.setStrokeCap(Paint.Cap.ROUND);
        mTextPaint.setStrokeJoin(Paint.Join.ROUND);
        mTextPaint.setTextSize(mTextSize);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        int fontHeight = (int)(fontMetrics.bottom - fontMetrics.top);
        mLineHeight = fontHeight + (fontHeight >> 2);
        if (mLineHeight < 2) {
            mLineHeight = 2;
        }
        mFontOffset = ((mLineHeight - fontHeight) >> 1) + (int)fontMetrics.bottom;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDisplayWidth = w;
        mSection.updateData(this, false);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        LogUtils.d(TAG, "onWindowVisibilityChanged visibility=%08X", visibility);
        if (visibility == View.VISIBLE) {
            if (!mStringList.isEmpty()) {
                mHandler.sendEmptyMessageDelayed(WHAT_FLUSH_SENTENCE, FLUSH_INTERVAL);
            }
        } else {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private int measureText(String text) {
        int width = 1;
        if (!TextUtils.isEmpty(text)) {
            width = (int)(mTextPaint.measureText(text) + CEIL_OF_FLOAT_OFFSET);
        }
        return width;
    }

    /**
     * 增加语句
     *
     * @param sentenceList 一些语句
     */
    public void append(List<String> sentenceList) {
        if (sentenceList != null) {
            for (String sentence : sentenceList) {
                if (!StringUtils.isEmpty(sentence)) {
                    mStringList.add(sentence);
                }
            }
            updateData(false);
        }
    }

    private void updateData(boolean step) {
        boolean hasData = mSection.updateData(this, step);
        if (step) {
            invalidate();
        }
        if (!mHandler.hasMessages(WHAT_FLUSH_SENTENCE) && hasData) {
            mHandler.sendEmptyMessageDelayed(WHAT_FLUSH_SENTENCE, FLUSH_INTERVAL);
        }
    }

    private static final class InnerHandler extends Handler {
        private WeakReference<DanmakuView> mWeakRefView;

        public InnerHandler(DanmakuView view) {
            mWeakRefView = new WeakReference<DanmakuView>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            DanmakuView view = mWeakRefView.get();
            if (view == null) {
                return;
            }
            view.updateData(true);
        }
    }
}
