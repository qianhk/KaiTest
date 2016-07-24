package com.njnu.kai.test.groupheader;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * add expand/collapse functions like ExpandableListView
 *
 */
public class ExpandableStickyGroupHeaderListView extends StickyGroupHeaderListView {
    public interface IAnimationExecutor {
        public void executeAnim(View target, int animType);
    }

    public final static int ANIMATION_COLLAPSE = 1;
    public final static int ANIMATION_EXPAND = 0;

    ExpandableStickyGroupHeaderAdapter mExpandableStickyGroupHeaderAdapter;


    IAnimationExecutor mDefaultAnimExecutor = new IAnimationExecutor() {
        @Override
        public void executeAnim(View target, int animType) {
            if (animType == ANIMATION_EXPAND) {
                target.setVisibility(VISIBLE);
            } else if (animType == ANIMATION_COLLAPSE) {
                target.setVisibility(GONE);
            }
        }
    };


    public ExpandableStickyGroupHeaderListView(Context context) {
        super(context);
    }

    public ExpandableStickyGroupHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableStickyGroupHeaderListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ExpandableStickyGroupHeaderAdapter getAdapter() {
        return mExpandableStickyGroupHeaderAdapter;
    }

    @Override
    public void setAdapter(StickyGroupHeaderAdapter adapter, int toggleButtonId, int expandableViewId) {
        mExpandableStickyGroupHeaderAdapter = new ExpandableStickyGroupHeaderAdapter(adapter);
        super.setAdapter(mExpandableStickyGroupHeaderAdapter, toggleButtonId, expandableViewId);
    }

    public View findViewByItemId(long itemId) {
        return mExpandableStickyGroupHeaderAdapter.findViewByItemId(itemId);
    }

    public long findItemIdByView(View view) {
        return mExpandableStickyGroupHeaderAdapter.findItemIdByView(view);
    }

    public void expand(long headerId) {
        if (!mExpandableStickyGroupHeaderAdapter.isHeaderCollapsed(headerId)) {
            return;
        }
        mExpandableStickyGroupHeaderAdapter.expand(headerId);
        //find and expand views in group
        List<View> itemViews = mExpandableStickyGroupHeaderAdapter.getItemViewsByHeaderId(headerId);
        if (itemViews == null) {
            return;
        }
        for (View view : itemViews) {
            animateView(view, ANIMATION_EXPAND);
        }
    }

    public void collapse(long headerId) {
        if (mExpandableStickyGroupHeaderAdapter.isHeaderCollapsed(headerId)) {
            return;
        }
        mExpandableStickyGroupHeaderAdapter.collapse(headerId);
        //find and hide views with the same listview_group_header_list_group_header
        List<View> itemViews = mExpandableStickyGroupHeaderAdapter.getItemViewsByHeaderId(headerId);
        if (itemViews == null) {
            return;
        }
        for (View view : itemViews) {
            animateView(view, ANIMATION_COLLAPSE);
        }
    }

    public boolean isHeaderCollapsed(long headerId) {
        return mExpandableStickyGroupHeaderAdapter.isHeaderCollapsed(headerId);
    }

    public void setAnimExecutor(IAnimationExecutor animExecutor) {
        this.mDefaultAnimExecutor = animExecutor;
    }

    /**
     * Performs either COLLAPSE or EXPAND animation on the target view
     *
     * @param target the view to animate
     * @param type   the animation type, either ExpandCollapseAnimation.COLLAPSE
     *               or ExpandCollapseAnimation.EXPAND
     */
    private void animateView(final View target, final int type) {
        if (ANIMATION_EXPAND == type && target.getVisibility() == VISIBLE) {
            return;
        }
        if (ANIMATION_COLLAPSE == type && target.getVisibility() != VISIBLE) {
            return;
        }
        if (mDefaultAnimExecutor != null) {
            mDefaultAnimExecutor.executeAnim(target, type);
        }

    }

}
