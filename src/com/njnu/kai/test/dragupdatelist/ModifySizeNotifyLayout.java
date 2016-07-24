package com.njnu.kai.test.dragupdatelist;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 状态
 * 正常状态
 * 拖拽-近
 * 拖拽-远
 * 返回-远
 * 返回-近
 * 子View高度
 *
 * 只用一个child view
 * @author guo.chen
 * @version 5.5.0
 */
public class ModifySizeNotifyLayout extends ViewGroup {

    /**
     * child显示状态改变的监听器
     */
	public static interface OnShowStateChangedListener {

        /** 正常状态 */
		public static final int SHOW_STATE_NORMAL = 0;
        /** 拖拽小于child的高度 */
		public static final int SHOW_STATE_SCALE_NEAR = 1;
        /** 拖拽大于child的高度 */
		public static final int SHOW_STATE_SCALE_FAR = 2;
        /** 超过child的高度返回 */
		public static final int SHOW_STATE_BACK_FAR = 3;
        /** 小于child的高度返回 */
		public static final int SHOW_STATE_BACK_NEAR = 4;
        /** 等于child的高度 */
		public static final int SHOW_STATE_CHILD_HEIGHT = 5;

        /**
         * child显示状态改变时调用
         * @param state 当前的状态
         */
		public void onShowStateChangedEvent(int state);
	}

    private static final String LOG_TAG = "ModifySizeNotifyLayout";

	private static final int BACK_IGNORE_MIN_VALUE = 4;
	private static final int MSG_DELAY = 17;
	private static final float BACK_HEIGHT_SCALE = 0.5f;

	private int mCurrentHeight;
	private int mState;
	private OnShowStateChangedListener mListener;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case OnShowStateChangedListener.SHOW_STATE_BACK_FAR:
					int childViewHeight = getChildAt(0).getMeasuredHeight() + getPaddingBottom() + getPaddingTop();
					backAnimation(childViewHeight, OnShowStateChangedListener.SHOW_STATE_CHILD_HEIGHT);
					break;
				case OnShowStateChangedListener.SHOW_STATE_BACK_NEAR:
					int normalHeight = getPaddingBottom();
					backAnimation(normalHeight, OnShowStateChangedListener.SHOW_STATE_NORMAL);
					break;
				default:
					break;
			}
		}
	};

	private void backAnimation(int endHeight, int endState) {
		int nextHeight = (int)((mCurrentHeight - endHeight) * BACK_HEIGHT_SCALE) + endHeight;
		if (nextHeight <= endHeight + BACK_IGNORE_MIN_VALUE) {
			nextHeight = endHeight;
			adjustHeight(nextHeight);
			mState = endState;
			notifyStateChanged();
		} else {
			adjustHeight(nextHeight);
			mHandler.sendEmptyMessageDelayed(mState, MSG_DELAY);
		}
	}

    /**
     * 构造函数
     * @param context 上下文
     */
	public ModifySizeNotifyLayout(Context context) { this(context, null); }

    /**
     * 构造函数
     * @param context 上下文
     * @param attrs 属性
     */
	public ModifySizeNotifyLayout(Context context, AttributeSet attrs) { this(context, attrs, 0); }

    /**
     * 构造函数
     * @param context 上下文
     * @param attrs 属性
     * @param defStyle 风格
     */
	public ModifySizeNotifyLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// 正常状态下只显示PaddingBottom的高度，若paddingBottom的高度不为零，则下拉刷新界面顶部会显示一条空白间隙
		if (getPaddingBottom() != 0) {
			setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), 0);
		}
		mCurrentHeight = getPaddingBottom();
		mState = OnShowStateChangedListener.SHOW_STATE_NORMAL;
	}

    /**
     * 设置child显示状态改变的监听器
     * @param listener child显示状态改变的监听器
     */
	public void setOnShowStateChangedListener(OnShowStateChangedListener listener) {
		mListener = listener;
	}

    /**
     * 设置为child的高度
     */
    public void setChildHeight() {
        mState = OnShowStateChangedListener.SHOW_STATE_CHILD_HEIGHT;
        requestLayout();
    }

    /**
     * 获取child的显示状态
     * @return child的显示状态
     */
	public int getShowState() {
		return mState;
	}

	/**
	 * 显示以后调用
	 * @param height view的高度
	 * @return true - 外界不要调整高度，false - 外界可以调整高度
	 */
	public boolean setHeight(int height) {
		if (mState == OnShowStateChangedListener.SHOW_STATE_BACK_FAR
				|| mState == OnShowStateChangedListener.SHOW_STATE_BACK_NEAR
				|| mState == OnShowStateChangedListener.SHOW_STATE_CHILD_HEIGHT) {
			return true;
		}
		if (height < getPaddingBottom()) {
			height = getPaddingBottom();
		}
		if (height == getPaddingBottom()) {
			if (mState != OnShowStateChangedListener.SHOW_STATE_NORMAL) {
				adjustHeight(height);
				mState = OnShowStateChangedListener.SHOW_STATE_NORMAL;
				notifyStateChanged();
			} else {
				return false;
			}
		} else if (height > getPaddingBottom()) {
			adjustHeight(height);
			View childView = getChildAt(0);
			if (height > childView.getMeasuredHeight() + getPaddingTop() + getPaddingBottom()) {
				if (mState != OnShowStateChangedListener.SHOW_STATE_SCALE_FAR) {
					mState = OnShowStateChangedListener.SHOW_STATE_SCALE_FAR;
					notifyStateChanged();
				}
			} else {
				if (mState != OnShowStateChangedListener.SHOW_STATE_SCALE_NEAR) {
					mState = OnShowStateChangedListener.SHOW_STATE_SCALE_NEAR;
					notifyStateChanged();
				}
			}
		}
		return true;
	}

	private void adjustHeight(int height) {
		if (mCurrentHeight != height) {
			mCurrentHeight = height;
			requestLayout();
		}
	}

	private void notifyStateChanged() {
		if (mListener != null) {
			mListener.onShowStateChangedEvent(mState);
		}
	}

    /**
     * 返回
     */
	public void back() {
		if (mState == OnShowStateChangedListener.SHOW_STATE_SCALE_FAR) {
			mState = OnShowStateChangedListener.SHOW_STATE_BACK_FAR;
			mHandler.sendEmptyMessage(mState);
			notifyStateChanged();
		} else if (mState == OnShowStateChangedListener.SHOW_STATE_SCALE_NEAR || mState == OnShowStateChangedListener.SHOW_STATE_CHILD_HEIGHT) {
			mState = OnShowStateChangedListener.SHOW_STATE_BACK_NEAR;
			mHandler.sendEmptyMessage(mState);
			notifyStateChanged();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		computeChildViewHeight(widthMeasureSpec, heightMeasureSpec);

		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mCurrentHeight);
	}

	private void computeChildViewHeight(int widthMeasureSpec, int heightMeasureSpec) {
		View childView = getChildAt(0);
		LayoutParams layoutParams = childView.getLayoutParams();
		if (layoutParams == null) {
			layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			childView.setLayoutParams(layoutParams);
		}
		measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        if (mState == OnShowStateChangedListener.SHOW_STATE_CHILD_HEIGHT) {
            mCurrentHeight = childView.getMeasuredHeight() + getPaddingBottom() + getPaddingTop();
        }
    }

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		View childView = getChildAt(0);
		int height = b - t;

		int left = getLeft();
		int bottom = height - getPaddingBottom();
		int right = left + childView.getMeasuredWidth();
		int top = bottom - childView.getMeasuredHeight();
		childView.layout(left, top, right, bottom);
	}
}
