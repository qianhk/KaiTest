package com.njnu.kai.test.dragupdatelist;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

/**
 * @author guo.chen
 * @version 5.5.0
 */
public class DragUpdateScrollView extends ScrollView {
    private DragUpdateHelper mDragUpdateHelper = null;
    private int[] mLocation = new int[2];

    /**
     * 构造函数
     * @param context 上下文对象
     */
    public DragUpdateScrollView(Context context) { this(context, null); }

    /**
     * 构造函数
     * @param context 上下文对象
     * @param attrs 属性
     */
	public DragUpdateScrollView(Context context, AttributeSet attrs) { this(context, attrs, 0); }

    /**
     * 构造函数
     * @param context 上下文对象
     * @param attrs 属性
     * @param defStyle 风格
     */
    public DragUpdateScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDragUpdateHelper = new DragUpdateHelper();
        mDragUpdateHelper.initUpdateView(context, mOnDragUpdateListener);
        setWillNotDraw(true);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        getLocationOnScreen(mLocation);
    }

    /**
     * 设置刷新时回调接口
     * @param listener 刷新时回调接口
     */
    public void setOnStartRefreshListener(DragUpdateHelper.OnStartRefreshListener listener) {
        mDragUpdateHelper.setOnStartRefreshListener(listener);
    }

    /**
     * 主动刷新
     */
    public void startRefresh() {
        mDragUpdateHelper.startRefresh();
    }

    /**
     * 设置加载提示文字颜色
     * @param color color
     */
    public void setLoadingTitleColor(int color) {
        mDragUpdateHelper.setLoadingTitleColor(color);
    }

    /**
     * 设置加载提示文字颜色
     * @param color color
     */
    public void setLoadingTitleColor(ColorStateList color) {
        mDragUpdateHelper.setLoadingTitleColor(color);
    }

    /**
     * 停止刷新
     */
    public void stopRefresh() {
        mDragUpdateHelper.stopRefresh();
    }

    @Override
         protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDragUpdateHelper.attachHeaderView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mDragUpdateHelper.handleTouchEvent(ev);
        return super.onTouchEvent(ev);
    }

    private DragUpdateHelper.OnDragUpdateListener mOnDragUpdateListener = new DragUpdateHelper.OnDragUpdateListener() {
        @Override
        public boolean shouldStartUpdate() {
            View child = getChildAt(0);
            int[] childPosition = new int[2];
            child.getLocationOnScreen(childPosition);
            return (childPosition[1] + mDragUpdateHelper.getRefreshViewHeight()) > mLocation[1];
        }

        private static final int SCROLL_FACTOR = 2;
        @Override
        public void onRefreshViewSizeChanged() {
            View child = getChildAt(0);
            int[] childPosition = new int[2];
            child.getLocationOnScreen(childPosition);
            if (getScrollY() != 0) {
                scrollTo(0, (mLocation[1] - childPosition[1]) / SCROLL_FACTOR);
            }
        }

        @Override
        public void attachHeaderRefreshViewToScrollableView(View refreshView) {
            if (getChildCount() > 0) {
                View view = getChildAt(0);
                if (view instanceof ViewGroup) {
                    ((ViewGroup)view).addView(refreshView, 0, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
            }
        }

        @Override
        public void onDragRelease() {
            if (getScrollY() != 0) {
                scrollTo(0, 0);
            }
        }
    };
}
