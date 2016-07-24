package com.njnu.kai.test;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.njnu.kai.test.groupheader.ExpandableStickyGroupHeaderListView;
import com.njnu.kai.test.groupheader.StickyGroupHeaderListView;

import java.util.WeakHashMap;

/**
 */
public class ExpandableStickyGroupHeaderListActivity extends Activity {

    private ExpandableStickyGroupHeaderListView mListView;
    StickyGroupHeaderBaseTestAdapter mStickyGroupHeaderBaseTestAdapter;
    WeakHashMap<View,Integer> mOriginalViewHeightPool = new WeakHashMap<View, Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_expandable_group_header_list);
        mListView = (ExpandableStickyGroupHeaderListView) findViewById(R.id.list);
        //custom expand/collapse animation
//        mListView.setAnimExecutor(new AnimationExecutor());
        mStickyGroupHeaderBaseTestAdapter = new StickyGroupHeaderBaseTestAdapter(this);
        mListView.setAdapter(mStickyGroupHeaderBaseTestAdapter, 0, 0);
        mListView.setOnHeaderClickListener(new StickyGroupHeaderListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyGroupHeaderListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if(mListView.isHeaderCollapsed(headerId)){
                    mListView.expand(headerId);
                }else {
                    mListView.collapse(headerId);
                }
            }
        });
        mListView.setDivider(new ColorDrawable(Color.RED));
        mListView.setDividerHeight(10);
    }
    //animation executor
    class AnimationExecutor implements ExpandableStickyGroupHeaderListView.IAnimationExecutor {

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void executeAnim(final View target, final int animType) {
            if(ExpandableStickyGroupHeaderListView.ANIMATION_EXPAND==animType&&target.getVisibility()==View.VISIBLE){
                return;
            }
            if(ExpandableStickyGroupHeaderListView.ANIMATION_COLLAPSE==animType&&target.getVisibility()!=View.VISIBLE){
                return;
            }
            if(mOriginalViewHeightPool.get(target)==null){
                mOriginalViewHeightPool.put(target,target.getHeight());
            }
            final int viewHeight = mOriginalViewHeightPool.get(target);
            float animStartY = animType == ExpandableStickyGroupHeaderListView.ANIMATION_EXPAND ? 0f : viewHeight;
            float animEndY = animType == ExpandableStickyGroupHeaderListView.ANIMATION_EXPAND ? viewHeight : 0f;
            final ViewGroup.LayoutParams lp = target.getLayoutParams();
            ValueAnimator animator = ValueAnimator.ofFloat(animStartY, animEndY);
            animator.setDuration(200);
            target.setVisibility(View.VISIBLE);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (animType == ExpandableStickyGroupHeaderListView.ANIMATION_EXPAND) {
                        target.setVisibility(View.VISIBLE);
                    } else {
                        target.setVisibility(View.GONE);
                    }
                    target.getLayoutParams().height = viewHeight;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    lp.height = ((Float) valueAnimator.getAnimatedValue()).intValue();
                    target.setLayoutParams(lp);
                    target.requestLayout();
                }
            });
            animator.start();

        }
    }
}
