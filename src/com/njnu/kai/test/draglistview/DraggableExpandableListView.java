package com.njnu.kai.test.draglistview;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;

import java.util.ArrayList;

/**
 * 可拖动的列表
 *
 * @author chao.fan
 * @version 7.1.0
 */
public class DraggableExpandableListView extends ExpandableListView {
    private static final int DRAGGER_BACKGROUND_COLOR = 0x4F000000;
    private static final float AEFAULT_ALPHA = 0.8f;
    private static final float SCROLL_START_FRACTION = 0.2f;
    private static final float DEFAULT_SMOOTHNESS = 0.5f;
    private static final int DURATION = 150;
    private static final float DEFAULT_HEIGHT_FRACTION = 0.5f;
    private static final float HALF_FLOAT = 0.5f;
    private static final float DEFAULT_REGION_FRACTION = 0.7f;
    private static final String TAG = "DELV";
    private View mFloatView;

    private Point mFloatLoc = new Point();
    private Point mTouchLoc = new Point();

    private int mFloatViewMid;

    private boolean mFloatViewOnMeasured = false;

    private DataSetObserver mObserver;

    private float mCurrFloatAlpha = AEFAULT_ALPHA;

    private int mFloatPos;
    private int mFirstExpPos;
    private int mSecondExpPos;

    private boolean mAnimate = true; //false - true

    private int mSrcPos;

    private int mDragDeltaX;
    private int mDragDeltaY;

    private DragListener mDragListener;
    private DropListener mDropListener;

    private boolean mDragEnabled = true;

    private final static int IDLE = 0;
    private final static int DROPPING = 1;
    private final static int STOPPED = 2;
    private final static int DRAGGING = 3;

    private int mDragState = IDLE;
    private int mItemHeightCollapsed = 2; //1 to 2
    private int mFloatViewHeight;
    private int mFloatViewHeightHalf;

    private int mWidthMeasureSpec = 0;

    private View[] mSampleViewTypes = new View[1];

    private DragScroller mDragScroller;

    private float mDragUpScrollStartFrac = 1.0f / 3.0f;
    private float mDragDownScrollStartFrac = 1.0f / 3.0f;

    private int mUpScrollStartY;
    private int mDownScrollStartY;
    private float mDownScrollStartYF;
    private float mUpScrollStartYF;

    private float mDragUpScrollHeight;
    private float mDragDownScrollHeight;
    private float mMaxScrollSpeed = 1.0f;

    private DragScrollProfile mScrollProfile = new DragScrollProfile() {
        @Override
        public float getSpeed(float w, long t) {
            return mMaxScrollSpeed * w;
        }
    };

    private int mX;
    private int mY;
    private int mLastY;
    /**
     *
     */
    public final static int DRAG_POS_X = 0x1;
    /**
     *
     */
    public final static int DRAG_NEG_X = 0x2;
    /**
     *
     */
    public final static int DRAG_POS_Y = 0x4;
    /**
     *
     */
    public final static int DRAG_NEG_Y = 0x8;

    private int mDragFlags = 0;

    private boolean mLastCallWasIntercept = false;

    private boolean mInTouchEvent = false;

    private FloatViewController mFloatViewController = null;

    private MotionEvent mCancelEvent;

    private static final int NO_CANCEL = 0;
    private static final int ON_TOUCH_EVENT = 1;
    private static final int ON_INTERCEPT_TOUCH_EVENT = 2;

    private int mCancelMethod = NO_CANCEL;

    private float mSlideRegionFrac = DEFAULT_REGION_FRACTION;
    private float mSlideFrac = 0.0f;

    private DraggableAdapterWrapper mAdapterWrapper;

    private boolean mBlockLayoutRequests = false;
    private boolean mIgnoreTouchEvent = false;

    private static final int CACHE_SIZE = 3;
    private HeightCache mChildHeightCache = new HeightCache(CACHE_SIZE);

    private DropAnimator mDropAnimator;

    /**
     * @param context Context
     * @param attrs   AttributeSet
     */
    public DraggableExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);

//        if (attrs != null) {
//            mCurrFloatAlpha = mFloatAlpha;
        setDragScrollStart(DEFAULT_HEIGHT_FRACTION);

        int dragInitMode = DragSortController.ON_DOWN;
        int dragHandleId = android.R.id.empty;

        DragExpandableSortController controller = new DragExpandableSortController(
                this, dragHandleId, dragInitMode);
        controller.setSortEnabled(true);
        controller.setBackgroundColor(DRAGGER_BACKGROUND_COLOR);

        mFloatViewController = controller;
        setOnTouchListener(controller);
//        }

        mDragScroller = new DragScroller();

        float smoothness = DEFAULT_SMOOTHNESS;
        int dropAnimDuration = DURATION; // ms
        if (dropAnimDuration > 0) {
            mDropAnimator = new DropAnimator(smoothness, dropAnimDuration);
        }

        mCancelEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL,
                0f, 0f, 0f, 0f, 0, 0f, 0f, 0, 0);

        mObserver = new DataSetObserver() {
            private void cancel() {
                if (mDragState == DRAGGING) {
                    cancelDrag();
                }
            }

            @Override
            public void onChanged() {
                cancel();
            }

            @Override
            public void onInvalidated() {
                cancel();
            }
        };
    }

    @Override
    public void setAdapter(ExpandableListAdapter adapter) {
        if (adapter != null) {
            mAdapterWrapper = new DraggableAdapterWrapper(adapter);
            adapter.registerDataSetObserver(mObserver);

            if (adapter instanceof DropListener) {
                setDropListener((DropListener) adapter);
            }
            if (adapter instanceof DragListener) {
                setDragListener((DragListener) adapter);
            }
        } else {
            mAdapterWrapper = null;
        }
        super.setAdapter(mAdapterWrapper);
    }

    private class DraggableAdapterWrapper extends BaseExpandableListAdapter {
        private ExpandableListAdapter mAdapter;

        public DraggableAdapterWrapper(ExpandableListAdapter adapter) {
            super();
            mAdapter = adapter;

            mAdapter.registerDataSetObserver(new DataSetObserver() {
                public void onChanged() {
                    notifyDataSetChanged();
                }

                public void onInvalidated() {
                    notifyDataSetInvalidated();
                }
            });
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            DragSortItemView v;
            View child;

            if (convertView != null) {
                v = (DragSortItemView) convertView;
                View oldChild = v.getChildAt(0);

                child = mAdapter.getChildView(groupPosition, childPosition, isLastChild, oldChild, DraggableExpandableListView.this);
                if (child != oldChild) {
                    if (oldChild != null) {
                        v.removeViewAt(0);
                    }
                    v.addView(child);
                }
            } else {
                child = mAdapter.getChildView(groupPosition, childPosition, isLastChild, null, DraggableExpandableListView.this);
                v = new DragSortItemView(getContext());
                v.setLayoutParams(new LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                v.addView(child);
            }
            adjustItem(childPosition + getHeaderViewsCount(), v, true);

            return v;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//            return mAdapter.getGroupView(groupPosition, isExpanded, convertView, parent);

            DragSortItemView v;
            View child;

            if (convertView != null) {
                v = (DragSortItemView) convertView;
                View oldChild = v.getChildAt(0);

                child = mAdapter.getGroupView(groupPosition, isExpanded, oldChild, DraggableExpandableListView.this);
                if (child != oldChild) {
                    if (oldChild != null) {
                        v.removeViewAt(0);
                    }
                    v.addView(child);
                }
            } else {
                child = mAdapter.getGroupView(groupPosition, isExpanded, null, DraggableExpandableListView.this);
                v = new DragSortItemView(getContext());
                v.setLayoutParams(new LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                v.addView(child);
            }
            adjustItem(groupPosition + getHeaderViewsCount(), v, true);
            return v;
        }

        @Override
        public int getGroupCount() {
            return mAdapter.getGroupCount();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mAdapter.getChildrenCount(groupPosition);
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mAdapter.getGroup(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mAdapter.getChild(groupPosition, childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return mAdapter.getGroupId(groupPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return mAdapter.getChildId(groupPosition, childPosition);
        }

        @Override
        public boolean hasStableIds() {
            return mAdapter.hasStableIds();
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return mAdapter.isChildSelectable(groupPosition, childPosition);
        }
    }

    private void drawDivider(int expPosition, Canvas canvas) {
        //to draw divider between two items
        final Drawable divider = getDivider();
        final int dividerHeight = getDividerHeight();

        if (divider != null && dividerHeight != 0) {
            final View childAt = getChildAt(expPosition
                    - getFirstVisiblePosition());
            if (childAt != null) {
                final int l = getPaddingLeft();
                final int r = getWidth() - getPaddingRight();
                final int t;
                final int b;

                final int childHeight = childAt instanceof ViewGroup ? ((ViewGroup) childAt).getChildAt(0).getHeight() : childAt.getHeight();

                if (expPosition > mSrcPos) {
                    t = childAt.getTop() + childHeight;
                    b = t + dividerHeight;
                } else {
                    b = childAt.getBottom() - childHeight;
                    t = b - dividerHeight;
                }

                canvas.save();
                canvas.clipRect(l, t, r, b);
                divider.setBounds(l, t, r, b);
                divider.draw(canvas);
                canvas.restore();
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (mDragState != IDLE) {
            // draw the divider over the expanded item
            if (mFirstExpPos != mSrcPos) {
                drawDivider(mFirstExpPos, canvas);
            }
            if (mSecondExpPos != mFirstExpPos && mSecondExpPos != mSrcPos) {
                drawDivider(mSecondExpPos, canvas);
            }
        }

        if (mFloatView != null) {
            // draw the float view over everything
            final int w = mFloatView.getWidth();
            final int h = mFloatView.getHeight();

            int x = mFloatLoc.x;
            int width = getWidth();
            if (x < 0) {
                x = -x;
            }
            float alphaMod;
            if (x < width) {
                alphaMod = ((float) (width - x)) / ((float) width);
                alphaMod *= alphaMod;
            } else {
                alphaMod = 0;
            }

            final int alpha = (int) (255f * mCurrFloatAlpha * alphaMod);
            canvas.save();
            canvas.translate(mFloatLoc.x, mFloatLoc.y);
            canvas.clipRect(0, 0, w, h);

            canvas.saveLayerAlpha(0, 0, w, h, alpha, Canvas.ALL_SAVE_FLAG);
            mFloatView.draw(canvas);
            canvas.restore();
            canvas.restore();
        }
    }

    private int getItemHeight(int position) {
        View v = getChildAt(position - getFirstVisiblePosition());
        if (v != null) {
            return v.getHeight();
        } else {
            return calcItemHeight(position, getChildHeight(position));
        }
    }

    private class HeightCache {
        private SparseIntArray mMap;
        private ArrayList<Integer> mOrder;
        private int mMaxSize;

        public HeightCache(int size) {
            mMap = new SparseIntArray(size);
            mOrder = new ArrayList<Integer>(size);
            mMaxSize = size;
        }

        public void add(int position, int height) {
            int currHeight = mMap.get(position, -1);
            if (currHeight != height) {
                if (currHeight == -1) {
                    if (mMap.size() == mMaxSize) {
                        mMap.delete(mOrder.remove(0));
                    }
                } else {
                    mOrder.remove((Integer) position);
                }
                mMap.put(position, height);
                mOrder.add(position);
            }
        }

        public int get(int position) {
            return mMap.get(position, -1);
        }

        public void clear() {
            mMap.clear();
            mOrder.clear();
        }
    }

    private int getShuffleEdge(int position, int top) {
        final int numHeaders = getHeaderViewsCount();
        final int numFooters = getFooterViewsCount();

        if (position <= numHeaders || (position >= getCount() - numFooters)) {
            return top;
        }

        int divHeight = getDividerHeight();
        int edge;
//        LogUtils.v("testitemheight", "item: " + mItemHeightCollapsed);
        int maxBlankHeight = mFloatViewHeight - mItemHeightCollapsed;
        int childHeight = getChildHeight(position);
        int itemHeight = getItemHeight(position);

        int otop = top;
        if (mSecondExpPos <= mSrcPos) {   //drag to up
            if (position == mSecondExpPos && mFirstExpPos != mSecondExpPos) {
                if (position == mSrcPos) {
                    otop = top + itemHeight - mFloatViewHeight;
                } else {
                    int blankHeight = itemHeight - childHeight;
                    otop = top + blankHeight - maxBlankHeight;
                }
            } else if (position > mSecondExpPos && position <= mSrcPos) {
                otop = top - maxBlankHeight;
            }
        } else {   //drag to down
            if (position > mSrcPos && position <= mFirstExpPos) {
                otop = top + maxBlankHeight;
            } else if (position == mSecondExpPos && mFirstExpPos != mSecondExpPos) {
                int blankHeight = itemHeight - childHeight;
                otop = top + blankHeight;
            }
        }

        if (position <= mSrcPos) {
            edge = otop + (mFloatViewHeight - divHeight - getChildHeight(position - 1)) / 2;
        } else {
            edge = otop + (childHeight - divHeight - mFloatViewHeight) / 2;
        }
        return edge;
    }

    private boolean updatePositions() {
        final int first = getFirstVisiblePosition();
        int startPos = mFirstExpPos;
        View startView = getChildAt(startPos - first);

        if (startView == null) {
            startPos = first + getChildCount() / 2;
            startView = getChildAt(startPos - first);
        }
        int startTop = startView.getTop();
        int itemHeight = startView.getHeight();
        int edge = getShuffleEdge(startPos, startTop);
        int lastEdge = edge;

        int divHeight = getDividerHeight();
        int itemPos = startPos;
        int itemTop = startTop;
        if (mFloatViewMid < edge) {
            while (itemPos >= 0) {
                itemPos--;
                itemHeight = getItemHeight(itemPos);

                if (itemPos == 0) {
                    edge = itemTop - divHeight - itemHeight;
                    break;
                }

                itemTop -= itemHeight + divHeight;
                edge = getShuffleEdge(itemPos, itemTop);

                if (mFloatViewMid >= edge) {
                    break;
                }

                lastEdge = edge;
            } //while(itemPos >= 0)
        } else {
            final int count = getCount();
            while (itemPos < count) {
                if (itemPos == count - 1) {
                    edge = itemTop + divHeight + itemHeight;
                    break;
                }

                itemTop += divHeight + itemHeight;
                itemHeight = getItemHeight(itemPos + 1);
                edge = getShuffleEdge(itemPos + 1, itemTop);
                if (mFloatViewMid < edge) {
                    break;
                }

                lastEdge = edge;
                itemPos++;
            } //while(itemPos < count)
        } //else

        final int numHeaders = getHeaderViewsCount();
        final int numFooters = getFooterViewsCount();

        boolean updated = false;

        int oldFirstExpPos = mFirstExpPos;
        int oldSecondExpPos = mSecondExpPos;
        float oldSlideFrac = mSlideFrac;

        if (mAnimate) {
            int edgeToEdge = Math.abs(edge - lastEdge);

            int edgeTop, edgeBottom;
            if (mFloatViewMid < edge) {
                edgeBottom = edge;
                edgeTop = lastEdge;
            } else {
                edgeTop = edge;
                edgeBottom = lastEdge;
            }

            int slideRgnHeight = (int) (HALF_FLOAT * mSlideRegionFrac * edgeToEdge);
            float slideRgnHeightF = (float) slideRgnHeight;
            int slideEdgeTop = edgeTop + slideRgnHeight;
            int slideEdgeBottom = edgeBottom - slideRgnHeight;

            // Three regions
            if (mFloatViewMid < slideEdgeTop) {
                mFirstExpPos = itemPos - 1;
                mSecondExpPos = itemPos;
                mSlideFrac = HALF_FLOAT * ((float) (slideEdgeTop - mFloatViewMid)) / slideRgnHeightF;
            } else if (mFloatViewMid < slideEdgeBottom) {
                mFirstExpPos = itemPos;
                mSecondExpPos = itemPos;
            } else {
                mFirstExpPos = itemPos;
                mSecondExpPos = itemPos + 1;
                mSlideFrac = HALF_FLOAT * (1.0f + ((float) (edgeBottom - mFloatViewMid))
                        / slideRgnHeightF);
            }

        } else {
            mFirstExpPos = itemPos;
            mSecondExpPos = itemPos;
        }

        if (mFirstExpPos < numHeaders) {
            itemPos = numHeaders;
            mFirstExpPos = itemPos;
            mSecondExpPos = itemPos;
        } else if (mSecondExpPos >= getCount() - numFooters) {
            itemPos = getCount() - numFooters - 1;
            mFirstExpPos = itemPos;
            mSecondExpPos = itemPos;
        }

        if (mFirstExpPos != oldFirstExpPos || mSecondExpPos != oldSecondExpPos
                || mSlideFrac != oldSlideFrac) {
            updated = true;
        }

        if (itemPos != mFloatPos) {
            if (mDragListener != null) {
                final long fromPackedPostion = getExpandableListPosition(mFloatPos - numHeaders);
                final long toPackedPostion = getExpandableListPosition(itemPos - numHeaders);
                final int fromGroup = getPackedPositionGroup(fromPackedPostion);
                final int toGroup = getPackedPositionGroup(toPackedPostion);
                final int fromPos = getPackedPositionChild(fromPackedPostion) + 1;
                final int toPos = getPackedPositionChild(toPackedPostion) + 1;
                if (fromPos <= mAdapterWrapper.getChildrenCount(fromGroup) && toPos <= mAdapterWrapper.getChildrenCount(toGroup)) {
                    mDragListener.drag(fromGroup, fromPos, toGroup, toPos);
                }
            }
            mFloatPos = itemPos;
            updated = true;
        }

        return updated;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private class SmoothAnimator implements Runnable {
        protected long mStartTime;
        private float mDurationF;
        private float mAlpha;
        private float mA, mB, mC, mD;
        private boolean mCanceled;

        public SmoothAnimator(float smoothness, int duration) {
            mAlpha = smoothness;
            mDurationF = (float) duration;
            mA = 1f / (2f * mAlpha * (1f - mAlpha));
            mB = mAlpha / (2f * (mAlpha - 1f));
            mC = 1f / (1f - mAlpha);
            mD = mA;
        }

        public float transform(float frac) {
            if (frac < mAlpha) {
                return mA * frac * frac;
            } else if (frac < 1f - mAlpha) {
                return mB + mC * frac;
            } else {
                return 1f - mD * (frac - 1f) * (frac - 1f);
            }
        }

        public void start() {
            mStartTime = SystemClock.uptimeMillis();
            mCanceled = false;
            onStart();
            post(this);
        }

        public void cancel() {
            mCanceled = true;
        }

        public void onStart() {
        }

        public void onUpdate(float frac, float smoothFrac) {
        }

        public void onStop() {
        }

        @Override
        public void run() {
            if (mCanceled) {
                return;
            }

            float fraction = ((float) (SystemClock.uptimeMillis() - mStartTime)) / mDurationF;

            if (fraction >= 1f) {
                onUpdate(1f, 1f);
                onStop();
            } else {
                onUpdate(fraction, transform(fraction));
                post(this);
            }
        }
    }

    private class DropAnimator extends SmoothAnimator {
        private int mDropPos;
        private int mSrcPos;
        private float mInitDeltaY;
        private float mInitDeltaX;

        public DropAnimator(float smoothness, int duration) {
            super(smoothness, duration);
        }

        @Override
        public void onStart() {
            mDropPos = mFloatPos;
            mSrcPos = DraggableExpandableListView.this.mSrcPos;
            mDragState = DROPPING;
            mInitDeltaY = mFloatLoc.y - getTargetY();
            mInitDeltaX = mFloatLoc.x - getPaddingLeft();
        }

        private int getTargetY() {
            final int first = getFirstVisiblePosition();
            final int otherAdjust = (mItemHeightCollapsed + getDividerHeight()) / 2;
            View v = getChildAt(mDropPos - first);
            int targetY = -1;
            if (v != null) {
                if (mDropPos == mSrcPos) {
                    targetY = v.getTop();
                } else if (mDropPos < mSrcPos) {
                    targetY = v.getTop() - otherAdjust;
                } else {
                    targetY = v.getBottom() + otherAdjust - mFloatViewHeight;
                }
            } else {
                // drop position is not on screen, no animation
                cancel();
            }
            return targetY;
        }

        @Override
        public void onUpdate(float frac, float smoothFrac) {
            final int targetY = getTargetY();
            final int targetX = getPaddingLeft();
            final float deltaY = mFloatLoc.y - targetY;
            final float deltaX = mFloatLoc.x - targetX;
            final float f = 1f - smoothFrac;
            if (f < Math.abs(deltaY / mInitDeltaY) || f < Math.abs(deltaX / mInitDeltaX)) {
                mFloatLoc.y = targetY + (int) (mInitDeltaY * f);
                mFloatLoc.x = getPaddingLeft() + (int) (mInitDeltaX * f);
                doDragFloatView(true);
            }
        }

        @Override
        public void onStop() {
            dropFloatView();
        }
    } //DropAnimator

    /**
     * 取消Drag
     */
    public void cancelDrag() {
        if (mDragState == DRAGGING) {
            mDragScroller.stopScrolling(true);
            destroyFloatView();
            clearPositions();
            adjustAllItems();

            if (mInTouchEvent) {
                mDragState = STOPPED;
            } else {
                mDragState = IDLE;
            }
        }
    }

    private void clearPositions() {
        mSrcPos = -1;
        mFirstExpPos = -1;
        mSecondExpPos = -1;
        mFloatPos = -1;
    }

    private void dropFloatView() {   // must set to avoid cancelDrag being called from the
        // DataSetObserver
        mDragState = DROPPING;

        if (mDropListener != null && mFloatPos >= 0 && mFloatPos < getCount()) {
            final int numHeaders = getHeaderViewsCount();
            final long fromPackedPostion = getExpandableListPosition(mSrcPos - numHeaders);
            final long toPackedPostion = getExpandableListPosition(mFloatPos - numHeaders);
            final int fromGroup = getPackedPositionGroup(fromPackedPostion);
            final int toGroup = getPackedPositionGroup(toPackedPostion);
            final int fromPos = getPackedPositionChild(fromPackedPostion) + 1;
            final int toPos = getPackedPositionChild(toPackedPostion) + 1;
            if (fromPos <= mAdapterWrapper.getChildrenCount(fromGroup) && toPos <= mAdapterWrapper.getChildrenCount(toGroup)) {
                mDropListener.drop(fromGroup, fromPos, toGroup, toPos);
            }
        }

        destroyFloatView();

        adjustOnReorder();
        clearPositions();
        adjustAllItems();

        // now the drag is done
        if (mInTouchEvent) {
            mDragState = STOPPED;
        } else {
            mDragState = IDLE;
        }
    }

    private void adjustOnReorder() {
        final int firstPos = getFirstVisiblePosition();
        if (mSrcPos < firstPos) {
            // collapsed src item is off screen;
            // adjust the scroll after item heights have been fixed
            View v = getChildAt(0);
            int top = 0;
            if (v != null) {
                top = v.getTop();
            }
            setSelectionFromTop(firstPos - 1, top - getPaddingTop());
        }
    }

    /**
     * 停止Drag
     *
     * @return boolean
     */
    public boolean stopDrag() {
        if (mFloatView != null) {
            mDragScroller.stopScrolling(true);

            if (mDropAnimator != null) {
                mDropAnimator.start();
            } else {
                dropFloatView();
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIgnoreTouchEvent) {
            mIgnoreTouchEvent = false;
            return false;
        }

        if (!mDragEnabled) {
            return super.onTouchEvent(ev);
        }

        boolean more = false;

        boolean lastCallWasIntercept = mLastCallWasIntercept;
        mLastCallWasIntercept = false;

        if (!lastCallWasIntercept) {
            saveTouchCoords(ev);
        }

        if (mDragState == DRAGGING) {
            onDragTouchEvent(ev);
            more = true; // give us more!
        } else {
            if (mDragState == IDLE) {
                if (super.onTouchEvent(ev)) {
                    more = true;
                }
            }

            int action = ev.getAction() & MotionEvent.ACTION_MASK;

            switch (action) {
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    doActionUpOrCancel();
                    break;
                default:
                    if (more) {
                        mCancelMethod = ON_TOUCH_EVENT;
                    }
            }
        }

        return more;
    }

    private void doActionUpOrCancel() {
        mCancelMethod = NO_CANCEL;
        mInTouchEvent = false;
        if (mDragState == STOPPED) {
            mDragState = IDLE;
        }
//        mCurrFloatAlpha = mFloatAlpha;
        mListViewIntercepted = false;
        mChildHeightCache.clear();
    }

    private void saveTouchCoords(MotionEvent ev) {
        int action = ev.getAction() & MotionEvent.ACTION_MASK;
        if (action != MotionEvent.ACTION_DOWN) {
            mLastY = mY;
        }
        mX = (int) ev.getX();
        mY = (int) ev.getY();
        if (action == MotionEvent.ACTION_DOWN) {
            mLastY = mY;
        }
    }

    /**
     * @return boolean
     */
    public boolean listViewIntercepted() {
        return mListViewIntercepted;
    }

    private boolean mListViewIntercepted = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mDragEnabled) {
            return super.onInterceptTouchEvent(ev);
        }

        saveTouchCoords(ev);
        mLastCallWasIntercept = true;

        int action = ev.getAction() & MotionEvent.ACTION_MASK;
        if (action == MotionEvent.ACTION_DOWN) {
            if (mDragState != IDLE) {
                // intercept and ignore
                mIgnoreTouchEvent = true;
                return true;
            }
            mInTouchEvent = true;
        }

        boolean intercept = false;
        // the following deals with calls to super.onInterceptTouchEvent
        if (mFloatView != null) {
            // super's touch event canceled in startDrag
            intercept = true;
        } else {
            if (super.onInterceptTouchEvent(ev)) {
                mListViewIntercepted = true;
                intercept = true;
            }
            switch (action) {
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    doActionUpOrCancel();
                    break;
                default:
                    if (intercept) {
                        mCancelMethod = ON_TOUCH_EVENT;
                    } else {
                        mCancelMethod = ON_INTERCEPT_TOUCH_EVENT;
                    }
            }
        }

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            mInTouchEvent = false;
        }

        return intercept;
    }

    /**
     * @param heightFraction float
     */
    public void setDragScrollStart(float heightFraction) {
        setDragScrollStarts(heightFraction, heightFraction);
    }

    /**
     * @param upperFrac float
     * @param lowerFrac float
     */
    public void setDragScrollStarts(float upperFrac, float lowerFrac) {
        if (lowerFrac > SCROLL_START_FRACTION) {
            mDragDownScrollStartFrac = SCROLL_START_FRACTION;
        } else {
            mDragDownScrollStartFrac = lowerFrac;
        }

        if (upperFrac > SCROLL_START_FRACTION) {
            mDragUpScrollStartFrac = SCROLL_START_FRACTION;
        } else {
            mDragUpScrollStartFrac = upperFrac;
        }

        if (getHeight() != 0) {
            updateScrollStarts();
        }
    }

    private void continueDrag(int x, int y) {
        mFloatLoc.x = x - mDragDeltaX;
        mFloatLoc.y = y - mDragDeltaY;

        doDragFloatView(true);

        int minY = Math.min(y, mFloatViewMid + mFloatViewHeightHalf);
        int maxY = Math.max(y, mFloatViewMid - mFloatViewHeightHalf);

        int currentScrollDir = mDragScroller.getScrollDir();

        if (minY > mLastY && minY > mDownScrollStartY && currentScrollDir != DragScroller.DOWN) {

            if (currentScrollDir != DragScroller.STOP) {
                mDragScroller.stopScrolling(true);
            }

            mDragScroller.startScrolling(DragScroller.DOWN);
        } else if (maxY < mLastY && maxY < mUpScrollStartY
                && currentScrollDir != DragScroller.UP) {

            if (currentScrollDir != DragScroller.STOP) {
                // moved directly from down scroll to up scroll
                mDragScroller.stopScrolling(true);
            }

            // start scrolling up
            mDragScroller.startScrolling(DragScroller.UP);
        } else if (maxY >= mUpScrollStartY && minY <= mDownScrollStartY
                && mDragScroller.isScrolling()) {
            // not in the upper nor in the lower drag-scroll regions but it is
            // still scrolling
            mDragScroller.stopScrolling(true);
        }
    }

    private void updateScrollStarts() {
        final int padTop = getPaddingTop();
        final int listHeight = getHeight() - padTop - getPaddingBottom();
        float heightF = (float) listHeight;

        mUpScrollStartYF = padTop + mDragUpScrollStartFrac * heightF;
        mDownScrollStartYF = padTop + (1.0f - mDragDownScrollStartFrac) * heightF;

        mUpScrollStartY = (int) mUpScrollStartYF;
        mDownScrollStartY = (int) mDownScrollStartYF;

        mDragUpScrollHeight = mUpScrollStartYF - padTop;
        mDragDownScrollHeight = padTop + listHeight - mDownScrollStartYF;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateScrollStarts();
    }

    private void adjustAllItems() {
        final int first = getFirstVisiblePosition();
        final int last = getLastVisiblePosition();

        int begin = Math.max(0, getHeaderViewsCount() - first);
        int end = Math.min(last - first, getCount() - 1 - getFooterViewsCount() - first);

        for (int i = begin; i <= end; ++i) {
            View v = getChildAt(i);
            if (v != null) {
                adjustItem(first + i, v, false);
            }
        }
    }

    private void adjustItem(int position, View v, boolean invalidChildHeight) {
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        int height;
        if (position != mSrcPos && position != mFirstExpPos && position != mSecondExpPos) {
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            height = calcItemHeight(position, v, invalidChildHeight);
        }

        if (height != lp.height) {
            lp.height = height;
            v.setLayoutParams(lp);
        }

        if (v instanceof DragSortItemView) {
            if (position == mFirstExpPos || position == mSecondExpPos) {
                if (position < mSrcPos) {
                    ((DragSortItemView) v).setGravity(Gravity.BOTTOM);
                } else if (position > mSrcPos) {
                    ((DragSortItemView) v).setGravity(Gravity.TOP);
                }
            }
        }

        int oldVis = v.getVisibility();
        int vis = View.VISIBLE;

        if (position == mSrcPos && mFloatView != null) {
            vis = View.INVISIBLE;
        }

        if (vis != oldVis) {
            v.setVisibility(vis);
        }
    }

    private int getChildHeight(int position) {
        if (position == mSrcPos) {
            return 0;
        }

        View v = getChildAt(position - getFirstVisiblePosition());

        if (v != null) {
            return getChildHeight(position, v, false);
        } else {
            int childHeight = mChildHeightCache.get(position);
            if (childHeight != -1) {
                return childHeight;
            }

            final ListAdapter adapter = getAdapter();
            int type = adapter.getItemViewType(position);

            final int typeCount = adapter.getViewTypeCount();
            if (typeCount != mSampleViewTypes.length) {
                mSampleViewTypes = new View[typeCount];
            }

            if (type >= 0) {
                if (mSampleViewTypes[type] == null) {
                    v = adapter.getView(position, null, this);
                    mSampleViewTypes[type] = v;
                } else {
                    v = adapter.getView(position, mSampleViewTypes[type], this);
                }
            } else {
                v = adapter.getView(position, null, this);
            }

            childHeight = getChildHeight(position, v, true);
            mChildHeightCache.add(position, childHeight);

            return childHeight;
        }
    }

    private int getChildHeight(int position, View item, boolean invalidChildHeight) {
        if (position == mSrcPos) {
            return 0;
        }

        View child;
        if (position < getHeaderViewsCount() || position
                >= getCount() - getFooterViewsCount()) {
            child = item;
        } else {
            if (item instanceof ViewGroup) {
                child = ((ViewGroup) item).getChildAt(0);
            } else {
                child = item;
            }
        }

        ViewGroup.LayoutParams lp = child.getLayoutParams();

        if (lp != null) {
            if (lp.height > 0) {
                return lp.height;
            }
        }

        int childHeight = child.getHeight();

        if (childHeight == 0 || invalidChildHeight) {
            measureItem(child);
            childHeight = child.getMeasuredHeight();
        }

        return childHeight;
    }

    private int calcItemHeight(int position, View item, boolean invalidChildHeight) {
        return calcItemHeight(position, getChildHeight(position, item, invalidChildHeight));
    }

    private int calcItemHeight(int position, int childHeight) {
        boolean isSliding = mAnimate && mFirstExpPos != mSecondExpPos;
        int maxNonSrcBlankHeight = mFloatViewHeight - mItemHeightCollapsed;
        int slideHeight = (int) (mSlideFrac * maxNonSrcBlankHeight);

        int height;

        if (position == mSrcPos) {
            if (mSrcPos == mFirstExpPos) {
                if (isSliding) {
                    height = slideHeight + mItemHeightCollapsed;
                } else {
                    height = mFloatViewHeight;
                }
            } else if (mSrcPos == mSecondExpPos) {
                // if gets here, we know an item is sliding
                height = mFloatViewHeight - slideHeight;
            } else {
                height = mItemHeightCollapsed;
            }
        } else if (position == mFirstExpPos) {
            if (isSliding) {
                height = childHeight + slideHeight;
            } else {
                height = childHeight + maxNonSrcBlankHeight;
            }
        } else if (position == mSecondExpPos) {
            // we know an item is sliding (b/c 2ndPos != 1stPos)
            height = childHeight + maxNonSrcBlankHeight - slideHeight;
        } else {
            height = childHeight;
        }

        return height;
    }

    @Override
    public void requestLayout() {
        if (!mBlockLayoutRequests) {
            super.requestLayout();
        }
    }

    private int adjustScroll(int movePos, View moveItem,
                             int oldFirstExpPos, int oldSecondExpPos) {
        int adjust = 0;

        final int childHeight = getChildHeight(movePos);

        int moveHeightBefore = moveItem.getHeight();
        int moveHeightAfter = calcItemHeight(movePos, childHeight);

        int moveBlankBefore = moveHeightBefore;
        int moveBlankAfter = moveHeightAfter;
        if (movePos != mSrcPos) {
            moveBlankBefore -= childHeight;
            moveBlankAfter -= childHeight;
        }

        int maxBlank = mFloatViewHeight;
        if (mSrcPos != mFirstExpPos && mSrcPos != mSecondExpPos) {
            maxBlank -= mItemHeightCollapsed;
        }

        if (movePos <= oldFirstExpPos) {
            if (movePos > mFirstExpPos) {
                adjust += maxBlank - moveBlankAfter;
            }
        } else if (movePos == oldSecondExpPos) {
            if (movePos <= mFirstExpPos) {
                adjust += moveBlankBefore - maxBlank;
            } else if (movePos == mSecondExpPos) {
                adjust += moveHeightBefore - moveHeightAfter;
            } else {
                adjust += moveBlankBefore;
            }
        } else {
            if (movePos <= mFirstExpPos) {
                adjust -= maxBlank;
            } else if (movePos == mSecondExpPos) {
                adjust -= moveBlankAfter;
            }
        }

        return adjust;
    }

    private void measureItem(View item) {
        ViewGroup.LayoutParams lp = item.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            item.setLayoutParams(lp);
        }
        int wspec = ViewGroup.getChildMeasureSpec(mWidthMeasureSpec, getListPaddingLeft()
                + getListPaddingRight(), lp.width);
        int hspec;
        if (lp.height > 0) {
            hspec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
        } else {
            hspec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        item.measure(wspec, hspec);
    }

    private void measureFloatView() {
        if (mFloatView != null) {
            measureItem(mFloatView);
            mFloatViewHeight = mFloatView.getMeasuredHeight(); //floatitem height
            mFloatViewHeightHalf = mFloatViewHeight / 2;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mFloatView != null) {
            if (mFloatView.isLayoutRequested()) {
                measureFloatView();
            }
            mFloatViewOnMeasured = true; // set to false after layout
        }
        mWidthMeasureSpec = widthMeasureSpec;
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        if (mFloatView != null) {
            if (mFloatView.isLayoutRequested() && !mFloatViewOnMeasured) {
                measureFloatView();
            }
            mFloatView.layout(0, 0, mFloatView.getMeasuredWidth(),
                    mFloatView.getMeasuredHeight());
            mFloatViewOnMeasured = false;
        }
    }

    protected boolean onDragTouchEvent(MotionEvent ev) {

        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_CANCEL:
                if (mDragState == DRAGGING) {
                    cancelDrag();
                }
                doActionUpOrCancel();
                break;
            case MotionEvent.ACTION_UP:
                if (mDragState == DRAGGING) {
                    stopDrag();
                }
                doActionUpOrCancel();
                break;
            case MotionEvent.ACTION_MOVE:
                continueDrag((int) ev.getX(), (int) ev.getY());
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * @param position  int
     * @param dragFlags int
     * @param deltaX    int
     * @param deltaY    int
     * @return boolean
     */
    public boolean startDrag(int position, int dragFlags, int deltaX, int deltaY) {
        if (!mInTouchEvent || mFloatViewController == null) {
            return false;
        }

        View v = mFloatViewController.onCreateFloatView(position);

        if (v == null) {
            return false;
        } else {
            return startDrag(position, v, dragFlags, deltaX, deltaY);
        }
    }

    /**
     * @param position  int
     * @param floatView View
     * @param dragFlags int
     * @param deltaX    int
     * @param deltaY    int
     * @return boolean
     */
    public boolean startDrag(int position, View floatView, int dragFlags,
                             int deltaX, int deltaY) {
        if (mDragState != IDLE || !mInTouchEvent || mFloatView != null
                || floatView == null || !mDragEnabled) {
            return false;
        }

        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        int pos = position + getHeaderViewsCount();
        mFirstExpPos = pos;
        mSecondExpPos = pos;
        mSrcPos = pos;
        mFloatPos = pos;

        // mDragState = dragType;
        mDragState = DRAGGING;
        mDragFlags = 0;
        mDragFlags |= dragFlags;

        mFloatView = floatView;
        measureFloatView(); // sets mFloatViewHeight

        mDragDeltaX = deltaX;
        mDragDeltaY = deltaY;

        mFloatLoc.x = mX - mDragDeltaX;
        mFloatLoc.y = mY - mDragDeltaY;

        // set src item invisible
        final View srcItem = getChildAt(mSrcPos - getFirstVisiblePosition());

        if (srcItem != null) {
            srcItem.setVisibility(View.INVISIBLE);
        }

        switch (mCancelMethod) {
            case ON_TOUCH_EVENT:
                super.onTouchEvent(mCancelEvent);
                break;
            case ON_INTERCEPT_TOUCH_EVENT:
                super.onInterceptTouchEvent(mCancelEvent);
                break;
            default:
                break;
        }
        requestLayout();

        return true;
    }

    private void doDragFloatView(boolean forceInvalidate) {
        int movePos = getFirstVisiblePosition() + getChildCount() / 2;
        View moveItem = getChildAt(getChildCount() / 2);

        if (moveItem == null) {
            return;
        }
        doDragFloatView(movePos, moveItem, forceInvalidate);
    }

    private void doDragFloatView(int movePos, View moveItem, boolean forceInvalidate) {
        mBlockLayoutRequests = true;

        updateFloatView();

        int oldFirstExpPos = mFirstExpPos;
        int oldSecondExpPos = mSecondExpPos;

        boolean updated = updatePositions();

        if (updated) {
            adjustAllItems();
            int scroll = adjustScroll(movePos, moveItem, oldFirstExpPos, oldSecondExpPos);
            setSelectionFromTop(movePos, moveItem.getTop() + scroll - getPaddingTop());
            layoutChildren();
        }

        if (updated || forceInvalidate) {
            invalidate();
        }
        mBlockLayoutRequests = false;
    }

    private void updateFloatView() {
        if (mFloatViewController != null) {
            mTouchLoc.set(mX, mY);
            mFloatViewController.onDragFloatView(mFloatView, mFloatLoc, mTouchLoc);
        }

        final int floatX = mFloatLoc.x;
        final int floatY = mFloatLoc.y;

        // restrict x motion
        int padLeft = getPaddingLeft();
        if ((mDragFlags & DRAG_POS_X) == 0 && floatX > padLeft) {
            mFloatLoc.x = padLeft;
        } else if ((mDragFlags & DRAG_NEG_X) == 0 && floatX < padLeft) {
            mFloatLoc.x = padLeft;
        }

        // keep floating view from going past bottom of last header view
        final int numHeaders = getHeaderViewsCount();
        final int numFooters = getFooterViewsCount();
        final int firstPos = getFirstVisiblePosition();
        final int lastPos = getLastVisiblePosition();

        int topLimit = getPaddingTop();
        if (firstPos < numHeaders) {
            topLimit = getChildAt(numHeaders - firstPos - 1).getBottom();
        }
        final long expandableListPosition = getExpandableListPosition(mSrcPos);
        final int srcGroup = getPackedPositionGroup(expandableListPosition);
        final int srcPos = getPackedPositionChild(expandableListPosition);
//        LogUtils.d(TAG, "updateFloatView g=%d p=%d mSrcPos=%d fP=%d lP=%d", srcGroup, srcPos, mSrcPos, firstPos, lastPos);
        final int topIndex = mSrcPos - firstPos - srcPos;
        topLimit = Math.max(getChildAt(Math.max(topIndex, 0)).getTop(), topLimit);

//        if ((mDragFlags & DRAG_NEG_Y) == 0) {
//            if (firstPos <= mSrcPos) {
//                topLimit = Math.max(getChildAt(mSrcPos - srcPos).getTop(), topLimit);
//            }
//        }

        int bottomLimit = getHeight() - getPaddingBottom();
        if (lastPos >= getCount() - numFooters - 1) {
            bottomLimit = getChildAt(getCount() - numFooters - 1 - firstPos).getBottom();
        }
        int childGroupCount = mAdapterWrapper.getChildrenCount(srcGroup);
        int bottomIndex = mSrcPos + childGroupCount - srcPos - 1 - firstPos;
        bottomLimit = Math.min(getChildAt(Math.min(bottomIndex, lastPos - firstPos)).getBottom(), bottomLimit);

//        if ((mDragFlags & DRAG_POS_Y) == 0) {
//            if (lastPos >= mSrcPos) {
//                bottomLimit = Math.min(getChildAt(mSrcPos - firstPos).getBottom(), bottomLimit);
//            }
//        }

        if (floatY < topLimit) {
            mFloatLoc.y = topLimit;
        } else if (floatY + mFloatViewHeight > bottomLimit) {
            mFloatLoc.y = bottomLimit - mFloatViewHeight;
        }
        mFloatViewMid = mFloatLoc.y + mFloatViewHeightHalf;
    }

    private void destroyFloatView() {
        if (mFloatView != null) {
            mFloatView.setVisibility(GONE);
            if (mFloatViewController != null) {
                mFloatViewController.onDestroyFloatView(mFloatView);
            }
            mFloatView = null;
            invalidate();
        }
    }


    /**
     * @param l DragListener
     */
    public void setDragListener(DragListener l) {
        mDragListener = l;
    }

    /**
     * @return boolean
     */
    public boolean isDragEnabled() {
        return mDragEnabled;
    }

    /**
     * @param l DropListener
     */
    public void setDropListener(DropListener l) {
        mDropListener = l;
    }

    /**
     *
     */
    public interface DragListener {
        /**
         * @param from int
         * @param to   int
         */
        public void drag(int fromGroup, int from, int toGroup, int to);
    }

    /**
     *
     */
    public interface DropListener {
        /**
         * @param from int
         * @param to   int
         */
        public void drop(int fromGroup, int from, int toGroup, int to);
    }

    /**
     *
     */
    public interface DragScrollProfile {
        /**
         * @param w float
         * @param t long
         * @return float
         */
        float getSpeed(float w, long t);
    }

    private class DragScroller implements Runnable {
        private boolean mAbort;

        private long mPrevTime;
        private long mCurrTime;

        private int mDy;
        private float mDt;
        private long mStartTime;
        private int mScrollDir;

        public final static int STOP = -1;
        public final static int UP = 0;
        public final static int DOWN = 1;

        private float mScrollSpeed; // pixels per ms

        private boolean mScrolling = false;

        public boolean isScrolling() {
            return mScrolling;
        }

        public int getScrollDir() {
            return mScrolling ? mScrollDir : STOP;
        }

        public DragScroller() {
        }

        public void startScrolling(int dir) {
            if (!mScrolling) {
                mAbort = false;
                mScrolling = true;
                mStartTime = SystemClock.uptimeMillis();
                mPrevTime = mStartTime;
                mScrollDir = dir;
                post(this);
            }
        }

        public void stopScrolling(boolean now) {
            if (now) {
                DraggableExpandableListView.this.removeCallbacks(this);
                mScrolling = false;
            } else {
                mAbort = true;
            }
        }

        @Override
        public void run() {
            if (mAbort) {
                mScrolling = false;
                return;
            }

            final int first = getFirstVisiblePosition();
            final int last = getLastVisiblePosition();
            final int count = getCount();
            final int padTop = getPaddingTop();
            final int listHeight = getHeight() - padTop - getPaddingBottom();

            int minY = Math.min(mY, mFloatViewMid + mFloatViewHeightHalf);
            int maxY = Math.max(mY, mFloatViewMid - mFloatViewHeightHalf);

            if (mScrollDir == UP) {
                View v = getChildAt(0);
                if (v == null) {
                    mScrolling = false;
                    return;
                } else {
                    if (first == 0 && v.getTop() == padTop) {
                        mScrolling = false;
                        return;
                    }
                }
                mScrollSpeed = mScrollProfile.getSpeed((mUpScrollStartYF - maxY)
                        / mDragUpScrollHeight, mPrevTime);
            } else {
                View v = getChildAt(last - first);
                if (v == null) {
                    mScrolling = false;
                    return;
                } else {
                    if (last == count - 1 && v.getBottom() <= listHeight + padTop) {
                        mScrolling = false;
                        return;
                    }
                }
                mScrollSpeed = -mScrollProfile.getSpeed((minY - mDownScrollStartYF)
                        / mDragDownScrollHeight, mPrevTime);
            }

            mCurrTime = SystemClock.uptimeMillis();
            mDt = (float) (mCurrTime - mPrevTime);
            mDy = (int) Math.round(mScrollSpeed * mDt);

            int movePos;
            if (mDy >= 0) {
                mDy = Math.min(listHeight, mDy);
                movePos = first;
            } else {
                mDy = Math.max(-listHeight, mDy);
                movePos = last;
            }

            final View moveItem = getChildAt(movePos - first);
            int top = moveItem.getTop() + mDy;

            if (movePos == 0 && top > padTop) {
                top = padTop;
            }
            mBlockLayoutRequests = true;

            setSelectionFromTop(movePos, top - padTop);
            DraggableExpandableListView.this.layoutChildren();
            invalidate();

            mBlockLayoutRequests = false;
            doDragFloatView(movePos, moveItem, false);
            mPrevTime = mCurrTime;
            post(this);
        } //run()
    }

    /**
     * 设置触发拖拽的id
     *
     * @param dragViewId int
     */
    public void setDragStartViewId(int dragViewId) {
        if (mFloatViewController instanceof DragExpandableSortController) {
            ((DragExpandableSortController) mFloatViewController).setDragHandleId(dragViewId);
        }
    }

}
