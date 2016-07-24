package com.njnu.kai.test.groupheader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * the view that wrapps a divider listview_group_header_list_group_header and a normal list item. The listview sees this as 1 item
 *
 */
public class WrapperView extends ViewGroup {


    View mItem;
    Drawable mDivider;
    Drawable mDivider2;
    Drawable mGroupDivider;
    int mDividerHeight;
    View mHeader;
    int mItemTop;

    public static boolean USE_SYSTEM_DIVIDER = true;

    WrapperView(Context c) {
        super(c);
    }

    public boolean hasHeader() {
        return mHeader != null;
    }

    public View getItem() {
        return mItem;
    }

    public View getHeader() {
        return mHeader;
    }

    void update(View item, View header, int dividerHeight, Drawable divider, Drawable divider2, Drawable groupDivider) {

        //every wrapperview must have a list item
        if (item == null) {
            throw new NullPointerException("List view item must not be null.");
        }

        //only remove the current item if it is not the same as the new item. this can happen if wrapping a recycled view
        if (this.mItem != item) {
            removeView(this.mItem);
            this.mItem = item;
            final ViewParent parent = item.getParent();
            if (parent != null && parent != this) {
                if (parent instanceof ViewGroup) {
                    ((ViewGroup)parent).removeView(item);
                }
            }
            addView(item);
        }

        //same logik as above but for the listview_group_header_list_group_header
        if (this.mHeader != header) {
            if (this.mHeader != null) {
                removeView(this.mHeader);
            }
            this.mHeader = header;
            if (header != null) {
                addView(header);
            }
//            header.setBackgroundColor(Color.GREEN);
        }

        if (USE_SYSTEM_DIVIDER) {
            if (mGroupDivider != groupDivider) {
                mGroupDivider = groupDivider;
                mDividerHeight = dividerHeight;
                invalidate();
                if (mHeader != null) {
                    requestLayout();
                }
            }
        } else {
            if (this.mDivider != divider) {
                this.mDivider = divider;
                this.mDividerHeight = dividerHeight;
                invalidate();
            }

            if (mGroupDivider != groupDivider) {
                mGroupDivider = groupDivider;
                mDividerHeight = dividerHeight;
                invalidate();
            }

            if (this.mDivider2 != divider2) {
                this.mDivider2 = divider2;
                this.mDividerHeight = dividerHeight;
                invalidate();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(measuredWidth,
                MeasureSpec.EXACTLY);
        int measuredHeight = 0;

        //measure listview_group_header_list_group_header or divider. when there is a listview_group_header_list_group_header visible it acts as the divider
        if (mHeader != null) {
            LayoutParams params = mHeader.getLayoutParams();
            if (params != null && params.height > 0) {
                mHeader.measure(childWidthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY));
            } else {
                mHeader.measure(childWidthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            }
            measuredHeight += mHeader.getMeasuredHeight();

            if (mGroupDivider != null && mItem.getVisibility() != View.GONE) {
                measuredHeight += mDividerHeight;
            }

        } else if (!USE_SYSTEM_DIVIDER && mDivider != null && mItem.getVisibility() != View.GONE) {
            measuredHeight += mDividerHeight;
        }

        //measure item
        LayoutParams params = mItem.getLayoutParams();
        //enable hiding listview item,ex. toggle off items in group
        if (mItem.getVisibility() == View.GONE) {
            mItem.measure(childWidthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY));
        } else if (params != null && params.height >= 0) {
            mItem.measure(childWidthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY));
            measuredHeight += mItem.getMeasuredHeight();
        } else {
            mItem.measure(childWidthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            measuredHeight += mItem.getMeasuredHeight();
        }


        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        l = 0;
        t = 0;
        r = getWidth();
        b = getHeight();

        if (mHeader != null) {
            int headerHeight = mHeader.getMeasuredHeight();
            mHeader.layout(l, t, r, headerHeight);
            if (mGroupDivider != null) {
                mGroupDivider.setBounds(l, headerHeight, r, headerHeight + mDividerHeight);
                headerHeight += mDividerHeight;
            }
            mItemTop = headerHeight;
            mItem.layout(l, headerHeight, r, b);
        } else if (mDivider != null) {
            if (USE_SYSTEM_DIVIDER) {
                mItemTop = t;
                mItem.layout(l, t, r, b);
            } else {
                mDivider.setBounds(l, t, r, mDividerHeight);
                mItemTop = mDividerHeight;
                mItem.layout(l, mDividerHeight, r, b);
            }
        } else {
            mItemTop = t;
            mItem.layout(l, t, r, b);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (!USE_SYSTEM_DIVIDER && mHeader == null && mDivider != null && mItem.getVisibility() != View.GONE) {
            // Drawable.setBounds() does not seem to work pre-honeycomb. So have
            // to do this instead
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                canvas.clipRect(0, 0, getWidth(), mDividerHeight);
            }
            mDivider.draw(canvas);
        }
        if (mHeader != null && mGroupDivider != null && mItem.getVisibility() != View.GONE) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                canvas.clipRect(mGroupDivider.getBounds());
            }
            mGroupDivider.draw(canvas);
        }
    }
}
