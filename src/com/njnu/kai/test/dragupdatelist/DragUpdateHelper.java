package com.njnu.kai.test.dragupdatelist;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.BaseApplication;

/**
 * @author yingchang.zhang
 * @version 7.0.0
 */
public class DragUpdateHelper  implements ModifySizeNotifyLayout.OnShowStateChangedListener {
    private float mStartScaleY;
    private boolean mEnableAdjustHeight;
    private ModifySizeNotifyLayout mTargetLayout;
    private DragUpdateViewControl mUpdateControl = null;
    private int mPreviousState = -1;
    private OnStartRefreshListener mListener;
    private OnDragUpdateListener mOnDragUpdateListener = null;

    /**
     * construct method
     * @param context 上下文环境
     * @param listener 拖动刷新监听接口
     */
    public void initUpdateView(Context context, OnDragUpdateListener listener) {
        if (null == listener) {
            throw new IllegalArgumentException("OnDragUpdateListener can not be null");
        }

        mTargetLayout = (ModifySizeNotifyLayout) View.inflate(context, R.layout.drag_update_list_header, null);
        mTargetLayout.setOnShowStateChangedListener(this);
        mOnDragUpdateListener = listener;
    }

    /**
     * 设置刷新时回调接口
     * @param listener 刷新时回调接口
     */
    public void setOnStartRefreshListener(OnStartRefreshListener listener) {
        mListener = listener;
    }

    /**
     * 主动刷新
     */
    public void startRefresh() {
        mTargetLayout.setChildHeight();
        mUpdateControl.startRefreshingAnimation();
    }

    /**
     * 设置加载提示文字颜色
     * @param color color
     */
    public void setLoadingTitleColor(int color) {
        if (mUpdateControl != null) {
            mUpdateControl.setTitleColor(color);
        }
    }

    /**
     * 设置加载提示文字颜色
     * @param color color
     */
    public void setLoadingTitleColor(ColorStateList color) {
        if (mUpdateControl != null) {
            mUpdateControl.setTitleColor(color);
        }
    }

    /**
     * 停止刷新
     */
    public void stopRefresh() {
        mTargetLayout.back();
    }


    /**
     * 绑定顶部刷新view到布局
     */
    public void attachHeaderView() {
        if (null == mUpdateControl) {
            mUpdateControl = new DragUpdateViewControl(mTargetLayout.findViewById(R.id.drag_update_layout));
            mOnDragUpdateListener.attachHeaderRefreshViewToScrollableView(mTargetLayout);
        }
    }

    /**
     * 处理触屏事件
     * @param ev 触屏事件
     */
    public void handleTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mEnableAdjustHeight = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = ev.getY();
                if (mEnableAdjustHeight) {
                    if (!mOnDragUpdateListener.shouldStartUpdate()) {
                        mEnableAdjustHeight = false;
                    }
                } else {
                    if (mOnDragUpdateListener.shouldStartUpdate()) {
                        mStartScaleY = moveY;
                        mEnableAdjustHeight = true;
                    }
                }
                if (mEnableAdjustHeight) {
                    float diffY = moveY - mStartScaleY;
                    int height = getRefreshViewHeight(diffY);
                    mTargetLayout.setHeight(height);
                    if (diffY > 1.0f) {
                        mOnDragUpdateListener.onRefreshViewSizeChanged();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                int currentState = mTargetLayout.getShowState();
                boolean enableBackState = currentState == ModifySizeNotifyLayout.OnShowStateChangedListener.SHOW_STATE_SCALE_FAR
                        || currentState == ModifySizeNotifyLayout.OnShowStateChangedListener.SHOW_STATE_SCALE_NEAR;
                if (mEnableAdjustHeight && enableBackState) {
                    mEnableAdjustHeight = false;
                    mOnDragUpdateListener.onDragRelease();
                    mTargetLayout.back();
                }
                break;
            default:
                break;
        }
    }

    private int getRefreshViewHeight(float height) {
        ViewGroup viewGroup = mTargetLayout;
        int refreshViewHeight = viewGroup.getChildAt(0).getMeasuredHeight()
                + viewGroup.getPaddingTop() + viewGroup.getPaddingBottom();
        final float scale = 0.2f;
        if (height > refreshViewHeight) {
            height = (height - refreshViewHeight) * scale + refreshViewHeight;
        }
        return (int) height;
    }

    /**
     * getRefreshViewHeight
     * @return refresh view height
     */
    public int getRefreshViewHeight() {
        return mTargetLayout.getHeight();
    }

    @Override
    public void onShowStateChangedEvent(int state) {
        switch (state) {
            case SHOW_STATE_SCALE_NEAR:
                if (mPreviousState == SHOW_STATE_NORMAL) {
                    mUpdateControl.setPreparingIcon();
                } else {
                    mUpdateControl.startPreparingAnimation();
                }
                break;
            case SHOW_STATE_SCALE_FAR:
                mUpdateControl.startPreparedAnimation();
                break;
            case SHOW_STATE_BACK_FAR:
                mUpdateControl.startRefreshingAnimation();
                break;
            case SHOW_STATE_CHILD_HEIGHT:
                if (mListener != null) {
                    mListener.onStartRefreshEvent();
                }
                break;
            case SHOW_STATE_NORMAL:
                mUpdateControl.setPreparingIcon();
                break;
            default:
                break;
        }
        mPreviousState = state;
    }

    /**
     * 下拉刷新头部View
     * @return null 如果没有初始化头部；否则返回TextView
     */
    public TextView getTitleTextView() {
        return mUpdateControl == null ? null : mUpdateControl.mTitleView;
    }

    /**
     * 下拉刷新头部刷新时间View
     * @return null 如果没有初始化头部；否则返回TextView
     */
    public TextView getContentTextView() {
        if (mUpdateControl != null) {
            TextView v = mUpdateControl.mTitleContent;
            if (v.getVisibility() != View.VISIBLE) {
                v.setVisibility(View.VISIBLE);
            }
            return v;
        }
        return null;
    }

    /**
     * 拖动刷新接口
     */
    public static interface OnDragUpdateListener {
        /**
         * 是否开始刷新
         * @return true 开始刷新
         */
        public boolean shouldStartUpdate();

        /**
         * 当刷新view被拖动高度改变时回调方法
         */
        public void onRefreshViewSizeChanged();

        /**
         * 绑定刷新的view
         * @param refreshView 刷新view
         */
        public void attachHeaderRefreshViewToScrollableView(View refreshView);

        /**
         *
         */
        public void onDragRelease();
    }

    /**
     * 刷新时回调接口
     */
    public static interface OnStartRefreshListener {
        /**
         * 刷新时回调
         */
        public void onStartRefreshEvent();
    }

    /**
     * @author guo.chen
     * @version 5.5.0
     */
    public static class DragUpdateViewControl {

        private static final long MINUTE = 1000 * 60;
        private long mLastUpdateTime;

        private ImageView mIconView;
        private TextView mTitleView;
        private TextView mTitleContent;

        private Animation mRefreshingAnimation;
        private Animation mPreparingAnimation;
        private Animation mPreparedAnimation;

        private String mRefreshPreparingPrompt;
        private String mRefreshPreparedPrompt;
        private String mRefreshingPrompt;
        private String mLastUpdateTimeStr;
        private String mUpdateMinutesStr;

        /**
         * 构造函数
         * @param view view
         */
        public DragUpdateViewControl(View view) {
            mIconView = (ImageView)view.findViewById(R.id.online_refresh_icon);
            mTitleView = (TextView)view.findViewById(R.id.online_refresh_title);
            mTitleContent = (TextView)view.findViewById(R.id.online_refresh_content);

            mRefreshingAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate);

            final float startAngle = -180f;
            final float center = 0.5f;
            final int duration = 500;
            mPreparingAnimation = new RotateAnimation(startAngle, 0, Animation.RELATIVE_TO_SELF, center
                    , Animation.RELATIVE_TO_SELF, center);
            mPreparingAnimation.setDuration(duration);
            mPreparingAnimation.setFillEnabled(true);
            mPreparingAnimation.setFillAfter(true);

            mPreparedAnimation = new RotateAnimation(-startAngle, 0, Animation.RELATIVE_TO_SELF, center
                    , Animation.RELATIVE_TO_SELF, center);
            mPreparedAnimation.setDuration(duration);
            mPreparedAnimation.setFillEnabled(true);
            mPreparedAnimation.setFillAfter(true);

            mLastUpdateTime = System.currentTimeMillis();

            //refreshingInitialize string
            Context ctx = BaseApplication.getApp();
            mRefreshPreparingPrompt = ctx.getString(R.string.pull_down_refresh);
            mRefreshPreparedPrompt = ctx.getString(R.string.release_refresh);
            mLastUpdateTimeStr = ctx.getString(R.string.last_updated);
            mUpdateMinutesStr = ctx.getString(R.string.updated_minutes);
            mRefreshingPrompt = ctx.getString(R.string.refreshing_prompt);
        }

        /**
         * 设置加载提示文字颜色
         * @param color color
         */
        public void setTitleColor(int color) {
            mTitleView.setTextColor(color);
            mTitleView.setShadowLayer(0, 0, 0, 0);
        }

        /**
         * 设置加载提示文字颜色
         * @param color color
         */
        public void setTitleColor(ColorStateList color) {
            if (null != color) {
                mTitleView.setTextColor(color);
                mTitleView.setShadowLayer(0, 0, 0, 0);
            }
        }

        /**
         * 设置加载提示文字背景颜色
         * @param color color
         */
        public void setTitleBackgroundColor(int color) {
            mTitleView.setTextColor(color);
            mTitleView.setShadowLayer(0, 0, 0, 0);
        }

        /**
         * 准备图标
         */
        public void setPreparingIcon() {
            mIconView.clearAnimation();
            mIconView.setImageResource(R.drawable.img_refresh);
            mTitleView.setText(mRefreshPreparingPrompt);
            mTitleContent.setText(mLastUpdateTimeStr + getLastUpdateTime() + " " + mUpdateMinutesStr);
        }

        /**
         * 执行准备刷新的动画
         */
        public void startPreparingAnimation() {
            mIconView.clearAnimation();
            mIconView.setImageResource(R.drawable.img_refresh);
            mIconView.startAnimation(mPreparingAnimation);
            mTitleView.setText(mRefreshPreparingPrompt);
            mTitleContent.setText(mLastUpdateTimeStr + getLastUpdateTime() + " " + mUpdateMinutesStr);
        }

        /**
         * 执行准备好的动画
         */
        public void startPreparedAnimation() {
            mIconView.clearAnimation();
            mIconView.setImageResource(R.drawable.img_refresh);
            mIconView.startAnimation(mPreparedAnimation);
            mTitleView.setText(mRefreshPreparedPrompt);
            mTitleContent.setText(mLastUpdateTimeStr + getLastUpdateTime() + " " + mUpdateMinutesStr);
        }

        /**
         * 执行刷新的动画
         */
        public void startRefreshingAnimation() {
            mIconView.clearAnimation();
            mIconView.setImageResource(R.drawable.img_refresh);
            mIconView.startAnimation(mRefreshingAnimation);
            mTitleView.setText(mRefreshingPrompt);
            mTitleContent.setText(mLastUpdateTimeStr + getLastUpdateTime() + " " + mUpdateMinutesStr);
        }

        private long getLastUpdateTime() {
            if (mLastUpdateTime == 0) {
                return 0;
            }
            return (System.currentTimeMillis() - mLastUpdateTime) / MINUTE;
        }
    }
}
