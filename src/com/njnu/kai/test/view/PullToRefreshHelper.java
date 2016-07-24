package com.njnu.kai.test.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

/**
 * @version 1.0.0
 */
public class PullToRefreshHelper {

    private static final String LOG_TAG = "PullToRefreshHelper";
    private int mMaxHeaderHeight;
    private float mLastMotionY;
    private Scroller mHeaderScroller;
    private int mHeaderHeight;
    private Runnable mHeaderScrollRunable;
    private boolean mHasCancelled;
    private View mParentView;
    private PullToRefreshHelperListener mPullToRefreshHelperListener;
    private OnPullToRefreshListener mOnPullToRefreshListener;
    /**
     * 用于记录上次触发的事件，因此其记录的值并非为当前状态，而是上次切换的状态
     */
    private int mCurrentPullState = OnPullToRefreshListener.STATE_IDLE;

    PullToRefreshHelper(View parentView, PullToRefreshHelperListener listener) {
        Context context = parentView.getContext();
        mParentView = parentView;
        mHeaderScroller = new Scroller(context, new AccelerateDecelerateInterpolator());
        mPullToRefreshHelperListener = listener;
    }

    void setOnPullToRefreshListener(OnPullToRefreshListener onPullToRefreshListener) {
        mOnPullToRefreshListener = onPullToRefreshListener;
    }

    void setMaxHeaderHeight(int height) {
        mMaxHeaderHeight = height;
    }

    boolean onInterceptTouchEvent(MotionEvent ev) {
        if ((ev.getAction() & MotionEventCompat.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            View actionView = mPullToRefreshHelperListener.getActionView();
            if (actionView != null) {
                mHasCancelled = false;
                mLastMotionY = ev.getY();
                if (!mHeaderScroller.isFinished()) {
                    mHeaderScroller.abortAnimation();
                    return true;
                } else {
                    ViewGroup.LayoutParams layoutParams = actionView.getLayoutParams();
                    mHeaderHeight = layoutParams == null ? actionView.getHeight() : layoutParams.height;
                }
            }
        }
        return false;
    }

    private void changePullState(int state) {
        mCurrentPullState = state;
         if (mOnPullToRefreshListener != null) {
            mOnPullToRefreshListener.onPullStateChanged(mParentView, mCurrentPullState);
        }
    }

    boolean onTouchEvent(MotionEvent ev) {
        final View actionView = mPullToRefreshHelperListener.getActionView();
        if (actionView == null) {
            return false;
        }
        final ViewGroup.LayoutParams actionViewLayoutParams = actionView.getLayoutParams();
        if (actionViewLayoutParams == null) {
            return false;
        }
        switch (ev.getAction() & MotionEventCompat.ACTION_MASK) {
            default:
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = ev.getY() - mLastMotionY;
                mLastMotionY = ev.getY();
                int layoutHeight = actionViewLayoutParams.height;
                if (layoutHeight > mHeaderHeight || mPullToRefreshHelperListener.shouldStartPull()) {
                    float height = Math.max(mHeaderHeight, Math.min(layoutHeight + dy, mMaxHeaderHeight));
                    if (height != layoutHeight) {
                        actionViewLayoutParams.height = (int) height;
                        actionView.requestLayout();
                    }
                    if (!mHasCancelled) {
                        if (height != mHeaderHeight) {
                            ev.setAction((ev.getAction() & MotionEventCompat.ACTION_POINTER_INDEX_MASK)
                                    | MotionEvent.ACTION_CANCEL);
                            mHasCancelled = true;
                            changePullState(OnPullToRefreshListener.STATE_START_PULL);
                        }
                    } else {
                        if (height == mMaxHeaderHeight && mCurrentPullState != OnPullToRefreshListener.STATE_PULL_TO_MAX) {
                            changePullState(OnPullToRefreshListener.STATE_PULL_TO_MAX);
                        } else if (height == mHeaderHeight && mCurrentPullState != OnPullToRefreshListener.STATE_PULL_TO_MIN) {
                            changePullState(OnPullToRefreshListener.STATE_PULL_TO_MIN);
                        }
                        return true;
                    }
                }
                break;
            case MotionEventCompat.ACTION_POINTER_DOWN:
            case MotionEventCompat.ACTION_POINTER_UP:
                if (actionViewLayoutParams.height != mHeaderHeight || mHasCancelled) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (actionViewLayoutParams.height != mHeaderHeight) {
                    if (mCurrentPullState == OnPullToRefreshListener.STATE_PULL_TO_MAX && mOnPullToRefreshListener != null) {
                        mOnPullToRefreshListener.onPullToRefresh(mParentView);
                    }
                    mHeaderScroller.startScroll(0, actionViewLayoutParams.height, 0, mHeaderHeight - actionViewLayoutParams.height);
                    if (mHeaderScrollRunable != null) {
                        mParentView.removeCallbacks(mHeaderScrollRunable);
                        mHeaderScrollRunable = null;
                    }
                    mHeaderScrollRunable = new Runnable() {
                        @Override
                        public void run() {
                            if (mHeaderScroller.isFinished()) {
                                mHeaderScrollRunable = null;
                                changePullState(OnPullToRefreshListener.STATE_IDLE);
                                return;
                            }
                            if (mHeaderScroller.computeScrollOffset()) {
                                actionViewLayoutParams.height = mHeaderScroller.getCurrY();
                                actionView.requestLayout();
                            }
                            ViewCompat.postOnAnimation(mParentView, this);
                        }
                    };
                    ViewCompat.postOnAnimation(mParentView, mHeaderScrollRunable);
                    return true;
                } else if (mCurrentPullState != OnPullToRefreshListener.STATE_IDLE) {
                    changePullState(OnPullToRefreshListener.STATE_IDLE);
                }
                break;
        }
        return false;
    }

    /**
     * 提供下拉刷新的必要检测参数
     * @author h.z
     * @
        /**
         * 当触发刷新时调用
     version 1.0.0
     */
    public interface PullToRefreshHelperListener {
        /**
         * 应该开始下拉动作
         * @return true if yes
         */
        public boolean shouldStartPull();

        /**
        /**
         * 当触发刷新时调用

         * 获取下拉动作需要拉伸变形的视图
         * @return 拉伸视图
         */
        public View getActionView();

        /**
         * 设置下拉触发器
         * @param listener listener
         */
        public void setOnPullRefreshListener(OnPullToRefreshListener listener);
    }

    /**
     * 提供触发下拉刷新的侦听器
     */
    public interface OnPullToRefreshListener {

        /**
         * 空闲状态
         */
        public static final int STATE_IDLE = 0;
        /**
         * 开始下拉
         */
        public static final int STATE_START_PULL = 1;
        /**
         * 下拉到最大位置
         */
        public static final int STATE_PULL_TO_MAX = 2;
        /**
         * 下拉到最小位置
         */
        public static final int STATE_PULL_TO_MIN = 3;


        /**
         * 当触发刷新时调用
         * @param view 触发刷新的view
         */
        public void onPullToRefresh(View view);

        /**
         * 当下拉状态改变
         * @param view 触发的view
         * @param state 状态
         */
        public void onPullStateChanged(View view, int state);
    }

}
