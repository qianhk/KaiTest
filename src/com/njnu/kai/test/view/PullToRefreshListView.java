package com.njnu.kai.test.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * @author h.z
 * @version 1.0.0
 */
public class PullToRefreshListView extends ListView implements PullToRefreshHelper.PullToRefreshHelperListener {

    private PullToRefreshHelper mPullToRefreshHelper;
    private View mActionHeaderView;

    /**
     * 构造方法
     * @param context context
     */
    public PullToRefreshListView(Context context) {
        super(context);
        init(context);
    }

    /**
     * 构造方法
     * @param context context
     * @param attrs 属性
     */
    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 构造方法
     * @param context context
     * @param attrs 属性
     * @param defStyle 默认风格
     */
    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mPullToRefreshHelper = new PullToRefreshHelper(this, this);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPullToRefreshHelper.setMaxHeaderHeight(w);
    }

    @Override
    public void addHeaderView(View v, Object data, boolean isSelectable) {
        super.addHeaderView(v, data, isSelectable);
        if (v != null && mActionHeaderView == null) {
            registerActionHeaderView(v);
        }
    }

    /**
     * 注册拉伸的header view，必须是listview 自身的view
     * @param headerView header view，用于拉伸显示效果
     */
    public void registerActionHeaderView(View headerView) {
        mActionHeaderView = headerView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isEnabled() && getChildCount() > 0 && mPullToRefreshHelper.onInterceptTouchEvent(ev) || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isEnabled() && getChildCount() > 0 && mPullToRefreshHelper.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    @Override
    public boolean shouldStartPull() {
        if (getFirstVisiblePosition() == 0) {
            if (getChildCount() > 0) {
                View view = getChildAt(0);
                return view != null && view.getTop() == getPaddingTop();
            }
        }
        return false;
    }

    @Override
    public View getActionView() {
        return mActionHeaderView;
    }

    @Override
    public void setOnPullRefreshListener(PullToRefreshHelper.OnPullToRefreshListener listener) {
        mPullToRefreshHelper.setOnPullToRefreshListener(listener);
    }

    /**
     * 设置图片的最大拉伸高度
     * @param h 高度
     */
    public void setMaxHeaderHeight(int h) {
        mPullToRefreshHelper.setMaxHeaderHeight(h);
    }
}
