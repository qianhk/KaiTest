package com.njnu.kai.test.menu.draglayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-04-12
 */
public class MenuMainFrameLayout extends FrameLayout {

    private SlidingMenu mMenu;

    /**
     * @param context context
     */
    public MenuMainFrameLayout(Context context) {
        super(context);
    }

    /**
     * @param context context
     * @param attrs attrs
     */
    public MenuMainFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param context context
     * @param attrs attrs
     * @param defStyle def style
     */
    public MenuMainFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * @param menu menu
     */
    public void setDragLayout(SlidingMenu menu) {
        this.mMenu = menu;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mMenu.getStatus() != SlidingMenu.Status.Close) {
            return true;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mMenu.getStatus() != SlidingMenu.Status.Close) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mMenu.close();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

}
