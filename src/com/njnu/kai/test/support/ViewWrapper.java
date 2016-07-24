/**
 * @(#) ViewWrapper.java     2011-8-3
 * Copyright (c) 2007-2011 Shanghai ShuiDuShi Co.Ltd. All right reserved.
 * 
 */
package com.njnu.kai.test.support;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * 提供对View中保护的State的外部访问，该类暴露set常数，不能被实例化
 * @version 2.0.0
 * @since 2011-8-17
 */
public final class ViewWrapper extends View {
	
	/**
	 * @param context
	 */
	private ViewWrapper(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	private ViewWrapper(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	private ViewWrapper(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	/**空状态.*/
	public static final int[] EMPTY_STATE_SET = View.EMPTY_STATE_SET;
	
    /**
     * Indicates the view is enabled. States are used with
     * {@link android.graphics.drawable.Drawable} to change the drawing of the
     * view depending on its state.
     *
     * @see android.graphics.drawable.Drawable
     * @see #getDrawableState()
     */
    public static final int[] ENABLED_STATE_SET = View.ENABLED_STATE_SET;
    /**
     * Indicates the view is focused. States are used with
     * {@link android.graphics.drawable.Drawable} to change the drawing of the
     * view depending on its state.
     *
     * @see android.graphics.drawable.Drawable
     * @see #getDrawableState()
     */
    public static final int[] FOCUSED_STATE_SET = View.FOCUSED_STATE_SET;
    /**
     * Indicates the view is selected. States are used with
     * {@link android.graphics.drawable.Drawable} to change the drawing of the
     * view depending on its state.
     *
     * @see android.graphics.drawable.Drawable
     * @see #getDrawableState()
     */
    public static final int[] SELECTED_STATE_SET = View.SELECTED_STATE_SET;

    /**
     * Indicates the view's window has focus. States are used with
     * {@link android.graphics.drawable.Drawable} to change the drawing of the
     * view depending on its state.
     *
     * @see android.graphics.drawable.Drawable
     * @see #getDrawableState()
     */
    public static final int[] WINDOW_FOCUSED_STATE_SET = View.WINDOW_FOCUSED_STATE_SET;
    // Doubles
    /**
     * Indicates the view is enabled and has the focus.
     *
     * @see #ENABLED_STATE_SET
     * @see #FOCUSED_STATE_SET
     */
    public static final int[] ENABLED_FOCUSED_STATE_SET = View.ENABLED_FOCUSED_STATE_SET;
    /**
     * Indicates the view is enabled and selected.
     *
     * @see #ENABLED_STATE_SET
     * @see #SELECTED_STATE_SET
     */
    public static final int[] ENABLED_SELECTED_STATE_SET = View.ENABLED_SELECTED_STATE_SET;
    /**
     * Indicates the view is enabled and that its window has focus.
     *
     * @see #ENABLED_STATE_SET
     * @see #WINDOW_FOCUSED_STATE_SET
     */
    public static final int[] ENABLED_WINDOW_FOCUSED_STATE_SET = View.ENABLED_WINDOW_FOCUSED_STATE_SET;
    
    /**
     * Indicates the view is focused and selected.
     *
     * @see #FOCUSED_STATE_SET
     * @see #SELECTED_STATE_SET
     */
    public static final int[] FOCUSED_SELECTED_STATE_SET = View.FOCUSED_SELECTED_STATE_SET;
    
    /**
     * Indicates the view has the focus and that its window has the focus.
     *
     * @see #FOCUSED_STATE_SET
     * @see #WINDOW_FOCUSED_STATE_SET
     */
    public static final int[] FOCUSED_WINDOW_FOCUSED_STATE_SET = View.FOCUSED_WINDOW_FOCUSED_STATE_SET;
    /**
     * Indicates the view is selected and that its window has the focus.
     *
     * @see #SELECTED_STATE_SET
     * @see #WINDOW_FOCUSED_STATE_SET
     */
    public static final int[] SELECTED_WINDOW_FOCUSED_STATE_SET = View.SELECTED_WINDOW_FOCUSED_STATE_SET;
    // Triples
    /**
     * Indicates the view is enabled, focused and selected.
     *
     * @see #ENABLED_STATE_SET
     * @see #FOCUSED_STATE_SET
     * @see #SELECTED_STATE_SET
     */
    public static final int[] ENABLED_FOCUSED_SELECTED_STATE_SET = View.ENABLED_FOCUSED_SELECTED_STATE_SET;
    
    /**
     * Indicates the view is enabled, focused and its window has the focus.
     *
     * @see #ENABLED_STATE_SET
     * @see #FOCUSED_STATE_SET
     * @see #WINDOW_FOCUSED_STATE_SET
     */
    public static final int[] ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = View.ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET;
    /**
     * Indicates the view is enabled, selected and its window has the focus.
     *
     * @see #ENABLED_STATE_SET
     * @see #SELECTED_STATE_SET
     * @see #WINDOW_FOCUSED_STATE_SET
     */
    public static final int[] ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = View.ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET;
    /**
     * Indicates the view is focused, selected and its window has the focus.
     *
     * @see #FOCUSED_STATE_SET
     * @see #SELECTED_STATE_SET
     * @see #WINDOW_FOCUSED_STATE_SET
     */
    public static final int[] FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = View.FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;

    /**
     * Indicates the view is enabled, focused, selected and its window
     * has the focus.
     *
     * @see #ENABLED_STATE_SET
     * @see #FOCUSED_STATE_SET
     * @see #SELECTED_STATE_SET
     * @see #WINDOW_FOCUSED_STATE_SET
     */
    public static final int[] ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = View.ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;

    /**
     * Indicates the view is pressed and its window has the focus.
     * @see #WINDOW_FOCUSED_STATE_SET
     */
    public static final int[] PRESSED_WINDOW_FOCUSED_STATE_SET = View.PRESSED_WINDOW_FOCUSED_STATE_SET;
    /**
     * Indicates the view is pressed and selected.
     * @see #SELECTED_STATE_SET
     */
    public static final int[] PRESSED_SELECTED_STATE_SET = View.PRESSED_SELECTED_STATE_SET;

    /**
     * Indicates the view is pressed, selected and its window has the focus.
     *
     * @see #SELECTED_STATE_SET
     * @see #WINDOW_FOCUSED_STATE_SET
     */
    public static final int[] PRESSED_SELECTED_WINDOW_FOCUSED_STATE_SET = View.PRESSED_SELECTED_WINDOW_FOCUSED_STATE_SET;

    /**
     * Indicates the view is pressed and focused.
     *
     * @see #FOCUSED_STATE_SET
     */
    public static final int[] PRESSED_FOCUSED_STATE_SET = View.PRESSED_FOCUSED_STATE_SET;

    /**
     * Indicates the view is pressed, focused and its window has the focus.
     *
     * @see #FOCUSED_STATE_SET
     * @see #WINDOW_FOCUSED_STATE_SET
     */
    public static final int[] PRESSED_FOCUSED_WINDOW_FOCUSED_STATE_SET = View.PRESSED_FOCUSED_WINDOW_FOCUSED_STATE_SET;

    /**
     * Indicates the view is pressed, focused and selected.
     *
     * @see #SELECTED_STATE_SET
     * @see #FOCUSED_STATE_SET
     */
    public static final int[] PRESSED_FOCUSED_SELECTED_STATE_SET = View.PRESSED_FOCUSED_SELECTED_STATE_SET;

    /**
     * Indicates the view is pressed, focused, selected and its window has the focus.
     *
     * @see #FOCUSED_STATE_SET
     * @see #SELECTED_STATE_SET
     * @see #WINDOW_FOCUSED_STATE_SET
     */
    public static final int[] PRESSED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = View.PRESSED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;

    /**
     * Indicates the view is pressed and enabled.
     *
     * @see #ENABLED_STATE_SET
     */
    public static final int[] PRESSED_ENABLED_STATE_SET = View.PRESSED_ENABLED_STATE_SET;

    /**
     * Indicates the view is pressed, enabled and its window has the focus.
     *
     * @see #ENABLED_STATE_SET
     * @see #WINDOW_FOCUSED_STATE_SET
     */
    public static final int[] PRESSED_ENABLED_WINDOW_FOCUSED_STATE_SET = View.PRESSED_ENABLED_WINDOW_FOCUSED_STATE_SET;

    /**
     * Indicates the view is pressed, enabled and selected.
     *
     * @see #ENABLED_STATE_SET
     * @see #SELECTED_STATE_SET
     */
    public static final int[] PRESSED_ENABLED_SELECTED_STATE_SET = View.PRESSED_ENABLED_SELECTED_STATE_SET;

    /**
     * Indicates the view is pressed, enabled, selected and its window has the
     * focus.
     *
     * @see #ENABLED_STATE_SET
     * @see #SELECTED_STATE_SET
     * @see #WINDOW_FOCUSED_STATE_SET
     */
    public static final int[] PRESSED_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = View.PRESSED_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET;

    /**
     * Indicates the view is pressed, enabled and focused.
     *
     * @see #ENABLED_STATE_SET
     * @see #FOCUSED_STATE_SET
     */
    public static final int[] PRESSED_ENABLED_FOCUSED_STATE_SET = View.PRESSED_ENABLED_FOCUSED_STATE_SET;

    /**
     * Indicates the view is pressed, enabled, focused and its window has the
     * focus.
     *
     * @see #ENABLED_STATE_SET
     * @see #FOCUSED_STATE_SET
     * @see #WINDOW_FOCUSED_STATE_SET
     */
    public static final int[] PRESSED_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = View.PRESSED_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET;

    /**
     * Indicates the view is pressed, enabled, focused and selected.
     *
     * @see #ENABLED_STATE_SET
     * @see #SELECTED_STATE_SET
     * @see #FOCUSED_STATE_SET
     */
    public static final int[] PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET = View.PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET;

    /**
     * Indicates the view is pressed, enabled, focused, selected and its window
     * has the focus.
     *
     * @see #ENABLED_STATE_SET
     * @see #SELECTED_STATE_SET
     * @see #FOCUSED_STATE_SET
     * @see #WINDOW_FOCUSED_STATE_SET
     */
    public static final int[] PRESSED_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = View.PRESSED_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET;

    /**
     * Used by views that contain lists of items. This state indicates that
     * the view is showing the last item.
     * @hide
     */
    protected static final int[] LAST_STATE_SET = {android.R.attr.state_last};
    /**
     * Used by views that contain lists of items. This state indicates that
     * the view is showing the first item.
     * @hide
     */
    protected static final int[] FIRST_STATE_SET = {android.R.attr.state_first};
    /**
     * Used by views that contain lists of items. This state indicates that
     * the view is showing the middle item.
     * @hide
     */
    protected static final int[] MIDDLE_STATE_SET = {android.R.attr.state_middle};
    /**
     * Used by views that contain lists of items. This state indicates that
     * the view is showing only one item.
     * @hide
     */
    protected static final int[] SINGLE_STATE_SET = {android.R.attr.state_single};
    /**
     * Used by views that contain lists of items. This state indicates that
     * the view is pressed and showing the last item.
     * @hide
     */
    protected static final int[] PRESSED_LAST_STATE_SET = {android.R.attr.state_last, android.R.attr.state_pressed};
    /**
     * Used by views that contain lists of items. This state indicates that
     * the view is pressed and showing the first item.
     * @hide
     */
    protected static final int[] PRESSED_FIRST_STATE_SET = {android.R.attr.state_first, android.R.attr.state_pressed};
    /**
     * Used by views that contain lists of items. This state indicates that
     * the view is pressed and showing the middle item.
     * @hide
     */
    protected static final int[] PRESSED_MIDDLE_STATE_SET = {android.R.attr.state_middle, android.R.attr.state_pressed};
    /**
     * Used by views that contain lists of items. This state indicates that
     * the view is pressed and showing only one item.
     * @hide
     */
    protected static final int[] PRESSED_SINGLE_STATE_SET = {android.R.attr.state_single, android.R.attr.state_pressed};

}
