package com.njnu.kai.test.dragupdatelist;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @version 5.5.0
 */
public class DragUpdateListView extends ListView {

    private DragUpdateHelper mDragUpdateHelper = null;

    private boolean mEnableDragUpdate = true;
    /**
     * 构造函数
     * @param context 上下文对象
     */
    public DragUpdateListView(Context context) { this(context, null); }

    /**
     * 构造函数
     * @param context 上下文对象
     * @param attrs 属性
     */
	public DragUpdateListView(Context context, AttributeSet attrs) { this(context, attrs, 0); }

    /**
     * 构造函数
     * @param context 上下文对象
     * @param attrs 属性
     * @param defStyle 风格
     */
	public DragUpdateListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        mDragUpdateHelper = new DragUpdateHelper();
        mDragUpdateHelper.initUpdateView(context, mOnDragUpdateListener);
        mDragUpdateHelper.attachHeaderView();
    }

    /**
     * 设置是否允许下拉刷新
     * @param enableDragUpdate true表示允许下拉刷新，否则为不允许
     */
    public void setEnableDragUpdate(boolean enableDragUpdate) {
        mEnableDragUpdate = enableDragUpdate;
    }

    /**
     * 下拉刷新头部View
     * @return null 如果没有初始化头部；否则返回TextView
     */
    public TextView getTitleTextView() {
        return mDragUpdateHelper.getTitleTextView();
    }

    /**
     * 下拉刷新头部刷新时间View
     * @return null 如果没有初始化头部；否则返回TextView
     */
    public TextView getContentTextView() {
        return mDragUpdateHelper.getContentTextView();
    }

    /**
     * 主动刷新
     */
    public void startRefresh() {
        mDragUpdateHelper.startRefresh();
    }

    /**
     * 设置刷新时回调接口
     * @param listener 刷新时回调接口
     */
    public void setOnStartRefreshListener(DragUpdateHelper.OnStartRefreshListener listener) {
        mDragUpdateHelper.setOnStartRefreshListener(listener);
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
	public boolean onTouchEvent(MotionEvent ev) {
        if (mEnableDragUpdate) {
            mDragUpdateHelper.handleTouchEvent(ev);
        }
		return super.onTouchEvent(ev);
	}

    private DragUpdateHelper.OnDragUpdateListener mOnDragUpdateListener = new DragUpdateHelper.OnDragUpdateListener() {
        @Override
        public boolean shouldStartUpdate() {
            return 0 == getFirstVisiblePosition();
        }

        @Override
        public void onRefreshViewSizeChanged() {
            setSelection(0);
        }

        @Override
        public void attachHeaderRefreshViewToScrollableView(View refreshView) {
            addHeaderView(refreshView);
        }

        @Override
        public void onDragRelease() {

        }
    };
}
