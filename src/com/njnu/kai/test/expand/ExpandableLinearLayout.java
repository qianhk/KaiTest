package com.njnu.kai.test.expand;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15/11/7
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public class ExpandableLinearLayout extends LinearLayout {

    private static final long DEFAULT_ANIMATION_DURATION = 280;

    private boolean mAnimating;
    private long mAnimationDuration = DEFAULT_ANIMATION_DURATION;

    private int mMinHeight;

    private boolean mExpanded = false;

    public ExpandableLinearLayout(Context context) {
        super(context);
        initExpandView(context);
    }

    public ExpandableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initExpandView(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ExpandableLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initExpandView(context);
    }

    private void initExpandView(Context context) {
        setOrientation(VERTICAL);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mAnimating || mExpanded) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(mMinHeight, MeasureSpec.AT_MOST));
        }
    }

    public void collapse() {
        if (mExpanded && !mAnimating) {
            mExpanded = false;
            clearAnimation();
            mAnimating = true;
            startAnimation(new ExpandAnimation(false));
        }
    }

    public void expand() {
        if (!mExpanded && !mAnimating) {
            mExpanded = true;
            clearAnimation();
            mAnimating = true;
            startAnimation(new ExpandAnimation(true));
        }
    }

    public boolean isExpand() {
        return mExpanded;
    }

    private class ExpandAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
            mAnimating = true;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mAnimating = false;
//            if (!mExpanded) {
//                toggleOnCollapseListener();
//            } else {
//                toggleOnExpandListener();
//            }
        }

    }

    private void changeExpanderHeight(int height) {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = height;
        setLayoutParams(params);
    }

    private class ExpandAnimation extends Animation {

        private final int mStartHeight;
        private final int mDistance;

        public ExpandAnimation(boolean expand) {
            super();

            int maxHeight = 0;
            int childCount = getChildCount();
            for (int idx = 0; idx < childCount; ++idx) {
                View view = getChildAt(idx);
                int viewMeasuredWidth = view.getMeasuredWidth();
                if (viewMeasuredWidth > 0) {
                    view.measure(MeasureSpec.makeMeasureSpec(viewMeasuredWidth, MeasureSpec.EXACTLY), ViewGroup.LayoutParams.WRAP_CONTENT);
                } else {
                    view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                maxHeight += view.getMeasuredHeight();
                MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();
                maxHeight += layoutParams.topMargin;
                maxHeight += layoutParams.bottomMargin;
            }
            maxHeight += getPaddingTop();
            maxHeight += getPaddingBottom();

            int endHeight;
            if (expand) {
                mStartHeight = mMinHeight;
                endHeight = maxHeight;
            } else {
                mStartHeight = maxHeight;
                endHeight = mMinHeight;
            }
            mDistance = endHeight - mStartHeight;
            setDuration(mAnimationDuration);
            setAnimationListener(new ExpandAnimationListener());
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            if (interpolatedTime > 0.9999f && mDistance >= 0) {
                changeExpanderHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                changeExpanderHeight(mStartHeight + Math.round(mDistance * interpolatedTime));
            }
        }

    }

}