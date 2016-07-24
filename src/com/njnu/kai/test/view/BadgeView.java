package com.njnu.kai.test.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabWidget;
import android.widget.TextView;

/**
 * A simple text label view that can be applied as a "badge" to any given {@link android.view.View}.
 * This class is intended to be instantiated at runtime rather than included in XML layouts.
 *
 * @author jeff.gilfelt
 * @version 1.0.0
 */
public class BadgeView extends TextView {

    /** */
    public static final int POSITION_TOP_LEFT = 1;
    /** */
    public static final int POSITION_TOP_RIGHT = 2;
    /** */
    public static final int POSITION_BOTTOM_LEFT = 3;
    /** */
    public static final int POSITION_BOTTOM_RIGHT = 4;
    /** */
    public static final int POSITION_CENTER = 5;
    /** */
    public static final int POSITION_RIGHT = 6;

    private static final int DEFAULT_ANIMATION_DURATION = 200;
    private static final int DEFAULT_MARGIN_DIP = 6;
    private static final int DEFAULT_TEXT_SIZE = 12;
    private static final int DEFAULT_LR_PADDING_DIP = 5;
    private static final int DEFAULT_CORNER_RADIUS_DIP = 10;
    private static final int DEFAULT_POSITION = POSITION_TOP_RIGHT;
    private static final int DEFAULT_BADGE_COLOR = Color.parseColor("#CCFF0000"); //Color.RED;
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;

    private static Animation fadeIn;
    private static Animation fadeOut;

    private Context mContext;
    private View mTarget;

    private int mBadgePosition;
    private int mBadgeMarginH;
    private int mBadgeMarginV;
    private int mBadgeColor;

    private boolean mIsShown;

    private ShapeDrawable mShapeDrawable;

    private int mTargetTabIndex;

    /**
     *
     * @param context Context
     */
    public BadgeView(Context context) {
        this(context, (AttributeSet) null, android.R.attr.textViewStyle);
    }

    /**
     *
     * @param context Context
     * @param attrs AttributeSet
     */
    public BadgeView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    /**
     * Constructor -
     * <p/>
     * create a new BadgeView instance attached to a mTarget {@link android.view.View}.
     *
     * @param context mContext for this view.
     * @param target  the View to attach the badge to.
     */
    public BadgeView(Context context, View target) {
        this(context, null, android.R.attr.textViewStyle, target, 0);
    }

    /**
     * Constructor -
     * <p/>
     * create a new BadgeView instance attached to a mTarget {@link android.view.View}.
     *
     * @param context mContext for this view.
     * @param target  the View to attach the badge to.
     * @param index  the position of the tab within the mTarget.
     */
    public BadgeView(Context context, View target, int index) {
        this(context, null, android.R.attr.textViewStyle, target, index);
    }

    /**
     * Constructor -
     * <p/>
     * create a new BadgeView instance attached to a mTarget {@link android.widget.TabWidget}
     * tab at a given index.
     *
     * @param context mContext for this view.
     * @param target  the TabWidget to attach the badge to.
     * @param index   the position of the tab within the mTarget.
     */
    public BadgeView(Context context, TabWidget target, int index) {
        this(context, null, android.R.attr.textViewStyle, target, index);
    }

    /**
     *
     * @param context Context
     * @param attrs AttributeSet
     * @param defStyle defStyle
     */
    public BadgeView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, null, 0);
    }

    /**
     *
     * @param context Context
     * @param attrs AttributeSet
     * @param defStyle defStyle
     * @param target mTarget
     * @param tabIndex tabIndex
     */
    public BadgeView(Context context, AttributeSet attrs, int defStyle, View target, int tabIndex) {
        super(context, attrs, defStyle);
        init(context, target, tabIndex);
    }

    private void init(Context context, View target, int tabIndex) {
        this.mContext = context;
        this.mTarget = target;
        this.mTargetTabIndex = tabIndex;

        // apply defaults
        mBadgePosition = DEFAULT_POSITION;
        mBadgeMarginH = dipToPixels(DEFAULT_MARGIN_DIP);
        mBadgeMarginV = mBadgeMarginH;
        mBadgeColor = DEFAULT_BADGE_COLOR;

        int paddingPixels = dipToPixels(DEFAULT_LR_PADDING_DIP);
        setPadding(paddingPixels, 0, paddingPixels, 0);
        setTextColor(DEFAULT_TEXT_COLOR);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE);

        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(DEFAULT_ANIMATION_DURATION);

        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(DEFAULT_ANIMATION_DURATION);

        mIsShown = false;

        if (this.mTarget != null) {
            applyTo(this.mTarget);
        } else {
            show();
        }
    }

    private void applyTo(View target) {
        LayoutParams lp = target.getLayoutParams();
        ViewParent parent = target.getParent();
        FrameLayout container = new FrameLayout(mContext);

        if (target instanceof TabWidget) {
            // set mTarget to the relevant tab child container
            target = ((TabWidget) target).getChildTabViewAt(mTargetTabIndex);
            this.mTarget = target;

            ((ViewGroup) target).addView(container,
                    new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            this.setVisibility(View.GONE);
            container.addView(this);
        } else {
            ViewGroup group = (ViewGroup) parent;
            int index = group.indexOfChild(target);

            if (lp instanceof LinearLayout.LayoutParams) {
                ((LinearLayout.LayoutParams) lp).weight = 1;
                lp.width = group.getMeasuredWidth() / group.getChildCount();
                lp.height = group.getMeasuredHeight();
            }

            group.removeView(target);
            group.addView(container, index, lp);
            container.addView(target);

            this.setVisibility(View.INVISIBLE);
            container.addView(this);
            group.invalidate();
        }
    }

    /**
     * Make the badge visible in the UI.
     */
    public void show() {
        show(false, null);
    }

    /**
     * Make the badge visible in the UI.
     *
     * @param animate flag to apply the default fade-in animation.
     */
    public void show(boolean animate) {
        show(animate, fadeIn);
    }

    /**
     * Make the badge visible in the UI.
     *
     * @param anim Animation to apply to the view when made visible.
     */
    public void show(Animation anim) {
        show(true, anim);
    }

    /**
     * Make the badge non-visible in the UI.
     */
    public void hide() {
        hide(false, null);
    }

    /**
     * Make the badge non-visible in the UI.
     *
     * @param animate flag to apply the default fade-out animation.
     */
    public void hide(boolean animate) {
        hide(animate, fadeOut);
    }

    /**
     * Make the badge non-visible in the UI.
     *
     * @param anim Animation to apply to the view when made non-visible.
     */
    public void hide(Animation anim) {
        hide(true, anim);
    }

    /**
     * Toggle the badge visibility in the UI.
     */
    public void toggle() {
        toggle(false, null, null);
    }

    /**
     * Toggle the badge visibility in the UI.
     *
     * @param animate flag to apply the default fade-in/out animation.
     */
    public void toggle(boolean animate) {
        toggle(animate, fadeIn, fadeOut);
    }

    private void show(boolean animate, Animation anim) {
        if (getBackground() == null) {
            if (mShapeDrawable == null) {
                mShapeDrawable = getDefaultBackground();
            }
            setBackgroundDrawable(mShapeDrawable);
        }
        applyLayoutParams();

        if (animate) {
            this.startAnimation(anim);
        }
        this.setVisibility(View.VISIBLE);
        mIsShown = true;
    }

    private void hide(boolean animate, Animation anim) {
        this.setVisibility(View.GONE);
        if (animate) {
            this.startAnimation(anim);
        }
        mIsShown = false;
    }

    private void toggle(boolean animate, Animation animIn, Animation animOut) {
        if (mIsShown) {
            hide(animate && (animOut != null), animOut);
        } else {
            show(animate && (animIn != null), animIn);
        }
    }

    private ShapeDrawable getDefaultBackground() {
        int r = dipToPixels(DEFAULT_CORNER_RADIUS_DIP);
        float[] outerR = new float[]{r, r, r, r, r, r, r, r};

        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(mBadgeColor);
        return drawable;
    }

    private void applyLayoutParams() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        switch (mBadgePosition) {
            case POSITION_TOP_LEFT:
                lp.gravity = Gravity.LEFT | Gravity.TOP;
                lp.setMargins(mBadgeMarginH, mBadgeMarginV, 0, 0);
                break;
            case POSITION_TOP_RIGHT:
                lp.gravity = Gravity.RIGHT | Gravity.TOP;
                lp.setMargins(0, mBadgeMarginV, mBadgeMarginH, 0);
                break;
            case POSITION_BOTTOM_LEFT:
                lp.gravity = Gravity.LEFT | Gravity.BOTTOM;
                lp.setMargins(mBadgeMarginH, 0, 0, mBadgeMarginV);
                break;
            case POSITION_BOTTOM_RIGHT:
                lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                lp.setMargins(0, 0, mBadgeMarginH, mBadgeMarginV);
                break;
            case POSITION_RIGHT:
                lp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                lp.setMargins(0, 0, mBadgeMarginH, 0);
                break;
            case POSITION_CENTER:
                lp.gravity = Gravity.CENTER;
                lp.setMargins(0, 0, 0, 0);
                break;
            default:
                break;
        }

        setLayoutParams(lp);
    }

    /**
     * Returns the mTarget View this badge has been attached to.
     * @return target
     */
    public View getTarget() {
        return mTarget;
    }

    /**
     * Is this badge currently visible in the UI?
     * @return isShown
     */
    @Override
    public boolean isShown() {
        return mIsShown;
    }

    /**
     * Set the positioning of this badge.
     *
     * @param layoutPosition one of POSITION_TOP_LEFT, POSITION_TOP_RIGHT, POSITION_BOTTOM_LEFT, POSITION_BOTTOM_RIGHT, POSTION_CENTER.
     */
    public void setBadgePosition(int layoutPosition) {
        this.mBadgePosition = layoutPosition;
    }

    /**
     * Set the horizontal/vertical margin from the mTarget View that is applied to this badge.
     *
     * @param horizontal margin in pixels.
     * @param vertical   margin in pixels.
     */
    public void setBadgeMargin(int horizontal, int vertical) {
        this.mBadgeMarginH = horizontal;
        this.mBadgeMarginV = vertical;
    }

    /**
     * Set the color value of the badge background.
     *
     * @param badgeColor the badge background color.
     */
    public void setBadgeBackgroundColor(int badgeColor) {
        this.mBadgeColor = badgeColor;
        mShapeDrawable = getDefaultBackground();
    }

    private int dipToPixels(int dip) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        return (int) px;
    }
}
