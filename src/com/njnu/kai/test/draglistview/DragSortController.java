package com.njnu.kai.test.draglistview;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

/**
 * @author chao.fan
 * @version 7.1.0
 */
public class DragSortController extends SimpleFloatViewController
        implements View.OnTouchListener, GestureDetector.OnGestureListener {
    /**
     *
     */
    public static final int ON_DOWN = 0;
    private int mDragInitMode = ON_DOWN;
    private boolean mSortEnabled = true;
    private GestureDetector mDetector;

    private static final int MISS = -1;
    private int mHitPos = MISS;

    private int[] mTempLoc = new int[2];

    private int mItemX;
    private int mItemY;

    private boolean mDragging = false;
    private int mDragHandleId;
    private DraggableListView mDslv;

    /**
     * @param dslv         DraggableListView
     * @param dragHandleId int
     * @param dragInitMode int
     */
    public DragSortController(DraggableListView dslv, int dragHandleId,
                              int dragInitMode) {
        super(dslv);
        mDslv = dslv;
        mDetector = new GestureDetector(dslv.getContext(), this);
        mDragHandleId = dragHandleId;
        setDragInitMode(dragInitMode);
    }

    /**
     * @param dragHandleId int
     */
    public void setDragHandleId(int dragHandleId) {
        mDragHandleId = dragHandleId;
    }

    /**
     * @param mode int
     */
    public void setDragInitMode(int mode) {
        mDragInitMode = mode;
    }

    /**
     * @param enabled boolean
     */
    public void setSortEnabled(boolean enabled) {
        mSortEnabled = enabled;
    }

    /**
     * @param position int
     * @param deltaX   int
     * @param deltaY   int
     * @return boolean
     */
    public boolean startDrag(int position, int deltaX, int deltaY) {
        int dragFlags = 0;
        if (mSortEnabled) {
            dragFlags |= DraggableListView.DRAG_POS_Y | DraggableListView.DRAG_NEG_Y;
        }
        mDragging = mDslv.startDrag(position - mDslv.getHeaderViewsCount(),
                dragFlags, deltaX, deltaY);

        return mDragging;
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        if (!mDslv.isDragEnabled() || mDslv.listViewIntercepted()) {
            return false;
        }
        mDetector.onTouchEvent(ev);

        return false;
    }

    /**
     * @param ev MotionEvent
     * @return position
     */
    public int startDragPosition(MotionEvent ev) {
        return dragHandleHitPosition(ev);
    }

    private int dragHandleHitPosition(MotionEvent ev) {
        return viewIdHitPosition(ev, mDragHandleId);
    }

    private int viewIdHitPosition(MotionEvent ev, int id) {
        final int x = (int) ev.getX();
        final int y = (int) ev.getY();

        int touchPos = mDslv.pointToPosition(x, y); // includes headers/footers

        final int numHeaders = mDslv.getHeaderViewsCount();
        final int numFooters = mDslv.getFooterViewsCount();
        final int count = mDslv.getCount();

        if (touchPos != AdapterView.INVALID_POSITION && touchPos >= numHeaders
                && touchPos < (count - numFooters)) {
            final View item = mDslv.getChildAt(touchPos - mDslv.getFirstVisiblePosition());
            final int rawX = (int) ev.getRawX();
            final int rawY = (int) ev.getRawY();

            View dragBox = id == 0 ? item : (View) item.findViewById(id);
            if (dragBox != null) {
                dragBox.getLocationOnScreen(mTempLoc);
                if (rawX > mTempLoc[0] && rawY > mTempLoc[1]
                        && rawX < mTempLoc[0] + dragBox.getWidth()
                        && rawY < mTempLoc[1] + dragBox.getHeight()) {
                    mItemX = item.getLeft();
                    mItemY = item.getTop();
                    return touchPos;
                }
            } //if(dragBox != null)
        } //if
        return MISS;
    }

    @Override
    public boolean onDown(MotionEvent ev) {
        mHitPos = startDragPosition(ev);
        if (mHitPos != MISS && mDragInitMode == ON_DOWN) {
            startDrag(mHitPos, (int) ev.getX() - mItemX, (int) ev.getY() - mItemY);
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                            float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public final boolean onFling(MotionEvent e1, MotionEvent e2,
                                 float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent ev) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent ev) {
    }

}
