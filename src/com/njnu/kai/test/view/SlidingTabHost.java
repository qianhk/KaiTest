package com.njnu.kai.test.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.SDKVersionUtils;

import java.util.Locale;


/**
 * SlidingTabHost
 *
 * @author andreas.stuetz
 * @version 1.0.0
 */
public class SlidingTabHost extends HorizontalScrollView {

    private final static int SPANNALE_SIZE = 12;

    /**
     * interface
     */
    public interface DataProvider {
        /**
         * Get page icon
         *
         * @param position position
         * @return icon in the position
         */
        public int getPageIconResId(int position);

        /**
         * get page title
         *
         * @param position position
         * @return text in the position
         */
        public int getPageTitleResId(int position);
    }

    // @formatter:off
    private static final int[] ATTRS = new int[]{
            android.R.attr.textSize,
            android.R.attr.textColor
    };
    // @formatter:on

    private final LinearLayout.LayoutParams mDefaultTabLayoutParams;
    private final LinearLayout.LayoutParams mExpandedTabLayoutParams;

    private final PageListener mPageListener = new PageListener();
    /**
     *
     */
    protected ViewPager.OnPageChangeListener mDelegatePageListener;

    private LinearLayout mTabsContainer;
    private ViewPager mViewPager;

    private int mTabCount;
    /**
     *
     */
    protected int mCurrentPosition = 0;
    private float mCurrentPositionOffset = 0f;

    private Paint mRectPaint;
    private Paint mDividerPaint;

    private boolean mCheckedTabWidths = false;

    private int mIndicatorColor = 0xFF666666;
    private Drawable mIndicatorDrawable = null;
    private int mUnderlineColor = 0x00000000;
    private int mDividerColor = 0x1A000000;

    private boolean mShouldExpand = false;
    private boolean mTextAllCaps = true;

    private int mScrollOffset = -1;
    private int mIndicatorHeight = 8;
    private int mTabHeight = LayoutParams.MATCH_PARENT;
    private int mIndicatorPaddingBottom = 0;
    private int mUnderlineHeight = 2;
    private int mDividerPadding = 0;
    private int mTabPadding = 5;
    private int mDividerWidth = 0;
    private boolean mTextSpannable = false;

    private int mTabTextSize = 12;
    private ColorStateList mTabTextColor = ColorStateList.valueOf(0xFF666666);
    private Typeface mTabTypeface = null;
    private int mTabTypefaceStyle = Typeface.NORMAL;

    private int mLastScrollX = 0;

    private int mTabBackgroundResId = R.drawable.xml_background_tab_item;

    private Locale mLocale;

    private boolean mTabLayoutAverageSpace;
    private Rect mTouchArea = new Rect();
    private boolean mIgnoreHitEvents = false;

    /**
     * Default constructor
     *
     * @param context context
     */
    public SlidingTabHost(Context context) {
        this(context, null);
    }

    /**
     * contstructor
     *
     * @param context context
     * @param attrs   attrs
     */
    public SlidingTabHost(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * constructor
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public SlidingTabHost(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFillViewport(true);
        setWillNotDraw(false);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        if (mScrollOffset > 0) {
            mScrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mScrollOffset, dm);
        }
        mIndicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mIndicatorHeight, dm);
        mIndicatorPaddingBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mIndicatorPaddingBottom, dm);
        mUnderlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mUnderlineHeight, dm);
        mDividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mDividerPadding, dm);
        mTabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mTabPadding, dm);
        mDividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mDividerWidth, dm);
        mTabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTabTextSize, dm);

        // get system attrs (android:textSize and android:textColor)
        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        mTabTextSize = a.getDimensionPixelSize(0, mTabTextSize);
        ColorStateList colorStateList = a.getColorStateList(1);
        if (colorStateList != null) {
            mTabTextColor = colorStateList;
        }
        a.recycle();

        // get custom attrs

        a = context.obtainStyledAttributes(attrs, R.styleable.SlidingTabHost);
        mIndicatorColor = a.getColor(R.styleable.SlidingTabHost_indicatorColor, mIndicatorColor);
        mUnderlineColor = a.getColor(R.styleable.SlidingTabHost_underlineColor, mUnderlineColor);
        mDividerColor = a.getColor(R.styleable.SlidingTabHost_dividerColor, mDividerColor);
        mIndicatorHeight = a.getDimensionPixelSize(R.styleable.SlidingTabHost_indicatorHeight, mIndicatorHeight);
        mIndicatorPaddingBottom = a.getDimensionPixelSize(R.styleable.SlidingTabHost_indicatorPaddingBottom, mIndicatorPaddingBottom);
        mUnderlineHeight = a.getDimensionPixelSize(R.styleable.SlidingTabHost_underlineHeight, mUnderlineHeight);
        mDividerPadding = a.getDimensionPixelSize(R.styleable.SlidingTabHost_dividerPadding, mDividerPadding);
        mTabPadding = a.getDimensionPixelSize(R.styleable.SlidingTabHost_tabPaddingLeftRight, mTabPadding);
        mTabBackgroundResId = a.getResourceId(R.styleable.SlidingTabHost_tabBackground, mTabBackgroundResId);
        mShouldExpand = a.getBoolean(R.styleable.SlidingTabHost_shouldExpand, mShouldExpand);
        mScrollOffset = a.getDimensionPixelSize(R.styleable.SlidingTabHost_scrollOffset, mScrollOffset);
        mTextAllCaps = a.getBoolean(R.styleable.SlidingTabHost_textAllCaps, mTextAllCaps);
        mTextSpannable = a.getBoolean(R.styleable.SlidingTabHost_textSpannable, mTextSpannable);
        mTabHeight = a.getDimensionPixelSize(R.styleable.SlidingTabHost_tabHeight, mTabHeight);
        a.recycle();

        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);
        mRectPaint.setStyle(Paint.Style.FILL);
        mDividerPaint = new Paint();
        mDividerPaint.setAntiAlias(true);
        mDividerPaint.setStrokeWidth(mDividerWidth);
        mDefaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mExpandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
        if (mLocale == null) {
            mLocale = getResources().getConfiguration().locale;
        }
        //add container view.
        mTabsContainer = new LinearLayout(context);
        mTabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        mTabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mTabHeight));
        addView(mTabsContainer);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mTabsContainer.getHitRect(mTouchArea);
            }
        }, 200);
    }

    /**
     * 设置标签是否等间距填满父窗口
     *
     * @param enable 是否等间距
     */
    public void setTabLayoutAverageSpace(boolean enable) {
        mTabLayoutAverageSpace = enable;
    }

    /**
     * set view pager
     *
     * @param pager viewpager class instance
     */
    public void setViewPager(ViewPager pager) {
        mViewPager = pager;
        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
//        if (mViewPager.getAdapter() instanceof SlidingTabFragmentPagerAdapter) {
//            ((SlidingTabFragmentPagerAdapter) mViewPager.getAdapter()).updatePage(mViewPager.getCurrentItem());
//        }
        pager.setOnPageChangeListener(mPageListener);

        notifyDataSetChanged();
    }

    /**
     * 设置页面切换监听器
     * @param listener 监听器
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.mDelegatePageListener = listener;
    }

    /**
     * 返回页面监听器
     * @return listener
     */
    public ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return mDelegatePageListener;
    }

    /**
     * 通知数据已更新，刷新视图
     */
    public void notifyDataSetChanged() {
        notifyDataSetChanged(mViewPager.getAdapter());
    }

    protected void notifyDataSetChanged(PagerAdapter pagerAdapter) {
        mTabsContainer.removeAllViews();
        mTabCount = pagerAdapter.getCount();

        int currentItem = getCurrentItem();
        if (currentItem >= mTabCount) {
            setCurrentItem(mTabCount - 1);
        }

        if (pagerAdapter instanceof DataProvider) {
            for (int i = 0; i < mTabCount; i++) {
                int iconResId = ((DataProvider) pagerAdapter).getPageIconResId(i);
                int titleResId = ((DataProvider) pagerAdapter).getPageTitleResId(i);
                CharSequence titleString = pagerAdapter.getPageTitle(i);
                addPagerTable(i, titleResId, iconResId, titleString);
            }
        } else {
            for (int i = 0; i < mTabCount; i++) {
                addTextTab(i, pagerAdapter.getPageTitle(i).toString(), 0);
            }
        }

        updateTabStyles();

        mCheckedTabWidths = false;

        mCurrentPosition = getCurrentItem();
        scrollToChild(mCurrentPosition, 0);
        checkTab(mCurrentPosition, true);
    }

    protected int getCurrentItem() {
        return mViewPager.getCurrentItem();
    }

    protected void setCurrentItem(int currentItem) {
        mViewPager.setCurrentItem(currentItem);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mTouchArea.contains((int) ev.getX(), (int) ev.getY())) {
            mIgnoreHitEvents = false;
            return super.onInterceptTouchEvent(ev);
        } else {
            mIgnoreHitEvents = true;
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return !mIgnoreHitEvents && super.onTouchEvent(ev);
    }

    /**
     * 设置标签的标题
     * @param tabIndex 标签索引
     * @param title 标签内容
     */
    public void setTabText(int tabIndex, CharSequence title) {
        View view = mTabsContainer.getChildAt(tabIndex);
        if (view instanceof TextView) {
            ((TextView) view).setText(title);
        } else {
            throw new IllegalArgumentException("Tab at index:" + tabIndex + "is not a TextTab");
        }
    }

    /**
     * 设置标签选中项的字体颜色,其它项恢复成默认设置的字体颜色
     * @param tabIndex 选中项序号
     * @param colorValue 字体颜色值
     */
    public void setSelectTabTextColor(int tabIndex, int colorValue) {
        updateTabStyles();
        View view = mTabsContainer.getChildAt(tabIndex);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(colorValue);
        } else {
            throw new IllegalArgumentException("Tab at index:" + tabIndex + "is not a TextTab");
        }
    }

    private void addPagerTable(int position, int titleResId, int iconResId, CharSequence title) {
        if (titleResId != 0) {
            addTextTab(position, getContext().getText(titleResId), iconResId);
        } else if (title == null) {
            addIconTab(position, iconResId);
        } else {
            addTextTab(position, title, iconResId);
        }
    }

    private SpannableStringBuilder buildSpanableString(String name) {
        SpannableStringBuilder builder = new SpannableStringBuilder(name);
        int start = name.indexOf("(");
        if (start != -1) {
            builder.setSpan(new AbsoluteSizeSpan(SPANNALE_SIZE, true),
                    start, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    private void addTextTab(final int position, CharSequence title, int icon) {
        TextView tab = new TextView(getContext());
        if (mTextSpannable) {
            tab.setText(buildSpanableString(title.toString()));
        } else {
            tab.setText(title);
        }
        tab.setFocusable(true);
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();
        tab.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onTabClick(position);
            }
        });
        mTabsContainer.addView(tab);
    }

    private void addIconTab(final int position, int resId) {

        ImageButton tab = new ImageButton(getContext());
        tab.setFocusable(true);
        tab.setImageResource(resId);

        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onTabClick(position);
            }
        });

        mTabsContainer.addView(tab);

    }

    protected void onTabClick(final int position) {
        setCurrentItem(position);
    }

    private void updateTabStyles() {

        for (int i = 0; i < mTabCount; i++) {

            View v = mTabsContainer.getChildAt(i);
            v.setLayoutParams(mTabLayoutAverageSpace ? mExpandedTabLayoutParams : mDefaultTabLayoutParams);
//            v.setBackgroundResource(mTabBackgroundResId); //歌手页tab不需要设置每个tab的背景
            if (mShouldExpand) {
                v.setPadding(0, 0, 0, 0);
            } else {
                v.setPadding(mTabPadding, 0, mTabPadding, 0);
            }

            if (v instanceof TextView) {

                TextView tab = (TextView) v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabTextSize);
                tab.setTypeface(mTabTypeface, mTabTypefaceStyle);
                tab.setTextColor(mTabTextColor);

                // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                // pre-ICS-build
                if (mTextAllCaps) {
                    if (SDKVersionUtils.hasIceCreamSandwich()) {
                        tab.setAllCaps(true);
                    } else {
                        tab.setText(tab.getText().toString().toUpperCase(mLocale));
                    }
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!mShouldExpand || MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            return;
        }

        int myWidth = getMeasuredWidth();
        int childWidth = 0;
        for (int i = 0; i < mTabCount; i++) {
            childWidth += mTabsContainer.getChildAt(i).getMeasuredWidth();
        }

        if (!mCheckedTabWidths && childWidth > 0 && myWidth > 0) {

            if (childWidth <= myWidth) {
                for (int i = 0; i < mTabCount; i++) {
                    mTabsContainer.getChildAt(i).setLayoutParams(mExpandedTabLayoutParams);
                }
            }

            mCheckedTabWidths = true;
        }
    }

    protected void scrollToChild(int position, int offset) {

        if (mTabCount == 0) {
            return;
        }

        if (mScrollOffset < 0) {
            mScrollOffset = mTabsContainer.getChildAt(0).getWidth();
        }

        int newScrollX = mTabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= mScrollOffset;
        }

        if (newScrollX != mLastScrollX) {
            mLastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || mTabCount == 0) {
            return;
        }

        final int height = getHeight();
        // draw indicator line, 如果使用图片背景，此处就不需要绘制指示条的颜色了
        if (null == mIndicatorDrawable || mIndicatorDrawable instanceof ColorDrawable) {
            mIndicatorColor = mIndicatorColor;
            mRectPaint.setColor(mIndicatorColor);
            // default: line below current tab
            View currentTab = mTabsContainer.getChildAt(mCurrentPosition);
            float lineLeft = currentTab.getLeft();
            float lineRight = currentTab.getRight();

            // if there is an offset, start interpolating left and right coordinates between current and next tab
            if (mCurrentPositionOffset > 0f && mCurrentPosition < mTabCount - 1) {

                View nextTab = mTabsContainer.getChildAt(mCurrentPosition + 1);
                final float nextTabLeft = nextTab.getLeft();
                final float nextTabRight = nextTab.getRight();

                lineLeft = (mCurrentPositionOffset * nextTabLeft + (1f - mCurrentPositionOffset) * lineLeft);
                lineRight = (mCurrentPositionOffset * nextTabRight + (1f - mCurrentPositionOffset) * lineRight);
            }
            canvas.drawRect(lineLeft, height - mIndicatorHeight - mIndicatorPaddingBottom, lineRight, height - mIndicatorPaddingBottom, mRectPaint);

            // draw underline
            mRectPaint.setColor(mUnderlineColor);
            canvas.drawRect(0, height - mUnderlineHeight, mTabsContainer.getWidth(), height, mRectPaint);
        } else {
            //绘制图片背景
            View currentTab = mTabsContainer.getChildAt(mCurrentPosition);
            float lineLeft = currentTab.getLeft();

            // if there is an offset, start interpolating left and right coordinates between current and next tab
            if (mCurrentPositionOffset > 0f && mCurrentPosition < mTabCount - 1) {
                View nextTab = mTabsContainer.getChildAt(mCurrentPosition + 1);
                final float nextTabLeft = nextTab.getLeft();
                lineLeft = (mCurrentPositionOffset * nextTabLeft + (1f - mCurrentPositionOffset) * lineLeft);
            }

            if (mIndicatorDrawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) mIndicatorDrawable).getBitmap();
                if (bitmap != null) {
                    canvas.drawBitmap(bitmap, null, new RectF(lineLeft, 0, lineLeft + currentTab.getWidth(), currentTab.getHeight()), null);
                }
            }
        }

        // draw divider
        mDividerPaint.setColor(mDividerColor);
        for (int i = 0; i < mTabCount - 1; i++) {
            View tab = mTabsContainer.getChildAt(i);
            if (mDividerWidth > 0) {
                canvas.drawLine(tab.getRight(), mDividerPadding, tab.getRight(), height - mDividerPadding, mDividerPaint);
            }
        }
    }

    private void changeCurrentPosition(int newPosition) {
        if (mCurrentPosition != newPosition) {
            checkTab(mCurrentPosition, false);
            mCurrentPosition = newPosition;
            checkTab(mCurrentPosition, true);
        }
    }

    /**
     * 以view的enable为标志，指示是否选中。若选中，则enable为false，否则为true
     *
     * @param position position
     * @param checked  true if checked
     */
    private void checkTab(int position, boolean checked) {
        if (position >= 0 && position < mTabsContainer.getChildCount()) {
            View tab = mTabsContainer.getChildAt(position);
            tab.setSelected(checked);
//            if (null != mIndicatorDrawable && checked) {
//                tab.setBackgroundDrawable(mIndicatorDrawable);
//            } else {
//                tab.setBackgroundColor(Color.TRANSPARENT);
//            }
        }
    }

    private class PageListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            changeCurrentPosition(position);
            mCurrentPositionOffset = positionOffset;
            View view = mTabsContainer.getChildAt(position);
            if (null == view) {
                return;
            }
            scrollToChild(position, (int) (positionOffset * view.getWidth()));
            invalidate();

            if (mDelegatePageListener != null) {
                mDelegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToChild(getCurrentItem(), 0);
            }

            if (mDelegatePageListener != null) {
                mDelegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mDelegatePageListener != null) {
                mDelegatePageListener.onPageSelected(position);
//                if (mViewPager.getAdapter() instanceof SlidingTabFragmentPagerAdapter) {
//                    ((SlidingTabFragmentPagerAdapter) mViewPager.getAdapter()).updatePage(position);
//                }
            }
        }

    }

    /**
     *
     * @param indicatorColor indicatorColor
     */
    public void setIndicatorColor(int indicatorColor) {
        this.mIndicatorColor = indicatorColor;
        invalidate();
    }

    /**
     *
     * @param resId resId
     */
    public void setIndicatorColorResource(int resId) {
        this.mIndicatorColor = getResources().getColor(resId);
        invalidate();
    }

    /**
     * set indicator drawable
     *
     * @param drawable indicator drawable
     */
    public void setIndicatorDrawable(Drawable drawable) {
        mIndicatorDrawable = drawable;
    }

    /**
     * 返回指示器颜色
     * @return color
     */
    public int getIndicatorColor() {
        return this.mIndicatorColor;
    }

    /**
     * 设置指示器高度
     * @param indicatorLineHeightPx indicatorLineHeightPx
     */
    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.mIndicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    /**
     * 返回指示器高度
     * @return height
     */
    public int getIndicatorHeight() {
        return mIndicatorHeight;
    }

    /**
     * 设置下划线颜色
     * @param underlineColor underlineColor
     */
    public void setUnderlineColor(int underlineColor) {
        this.mUnderlineColor = underlineColor;
        invalidate();
    }

    /**
     * 通过资源ID设置下划线颜色
     * @param resId resId
     */
    public void setUnderlineColorResource(int resId) {
        this.mUnderlineColor = getResources().getColor(resId);
        invalidate();
    }

    /**
     * 返回下划线颜色
     * @return color
     */
    public int getUnderlineColor() {
        return mUnderlineColor;
    }

    /**
     * 设置分割线颜色
     * @param dividerColor dividerColor
     */
    public void setDividerColor(int dividerColor) {
        this.mDividerColor = dividerColor;
        invalidate();
    }

    /**
     * 通过资源id设置下划线颜色
     * @param resId resId
     */
    public void setDividerColorResource(int resId) {
        this.mDividerColor = getResources().getColor(resId);
        invalidate();
    }

    /**
     * 返回下划线颜色
     * @return color
     */
    public int getDividerColor() {
        return mDividerColor;
    }

    /**
     * 返回下划线颜色
     * @param  underlineHeightPx underlineHeightPx
     */
    public void setUnderlineHeight(int underlineHeightPx) {
        this.mUnderlineHeight = underlineHeightPx;
        invalidate();
    }

    /**
     * @return mUnderlineHeight
     */
    public int getUnderlineHeight() {
        return mUnderlineHeight;
    }

    /**
     * @param  dividerPaddingPx dividerPaddingPx
     */
    public void setDividerPadding(int dividerPaddingPx) {
        this.mDividerPadding = dividerPaddingPx;
        invalidate();
    }

    /**
     * @return mDividerPadding
     */
    public int getDividerPadding() {
        return mDividerPadding;
    }

    /**
     * @param  scrollOffsetPx scrollOffsetPx
     */
    public void setScrollOffset(int scrollOffsetPx) {
        this.mScrollOffset = scrollOffsetPx;
        invalidate();
    }

    /**
     * @return mScrollOffset
     */
    public int getScrollOffset() {
        return mScrollOffset;
    }

    /**
     * @param  shouldExpand shouldExpand
     */
    public void setShouldExpand(boolean shouldExpand) {
        this.mShouldExpand = shouldExpand;
        requestLayout();
    }

    /**
     * @return mShouldExpand
     */
    public boolean getShouldExpand() {
        return mShouldExpand;
    }

    /**
     * @return mTextAllCaps
     */
    public boolean isTextAllCaps() {
        return mTextAllCaps;
    }

    /**
     * @param textAllCaps textAllCaps
     */
    public void setAllCaps(boolean textAllCaps) {
        this.mTextAllCaps = textAllCaps;
    }

    /**
     * @param  textSizePx textSizePx
     */
    public void setTextSize(int textSizePx) {
        this.mTabTextSize = textSizePx;
        updateTabStyles();
    }

    /**
     * @return mTabTextSize
     */
    public int getTextSize() {
        return mTabTextSize;
    }

    /**
     * @param  textColor textColor
     */
    public void setTextColor(int textColor) {
        mTabTextColor = ColorStateList.valueOf(textColor);
        updateTabStyles();
    }

    /**
     * @param  textColor textColor
     */
    public void setTextColor(ColorStateList textColor) {
        if (null != textColor) {
            mTabTextColor = textColor;
            updateTabStyles();
        }
    }

    /**
     * @param  resId resId
     */
    public void setTextColorResource(int resId) {
        this.mTabTextColor = getResources().getColorStateList(resId);
        updateTabStyles();
    }

    /**
     * @return color
     */
    public int getTextColor() {
        return mTabTextColor.getDefaultColor();
    }

    /**
     * @param typeface typeface
     * @param style style
     */
    public void setTypeface(Typeface typeface, int style) {
        this.mTabTypeface = typeface;
        this.mTabTypefaceStyle = style;
        updateTabStyles();
    }

    /**
     * @param  resId resId
     */
    public void setTabBackground(int resId) {
        this.mTabBackgroundResId = resId;
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
    }

    /**
     * @return mTabBackgroundResId
     */
    public int getTabBackground() {
        return mTabBackgroundResId;
    }

    /**
     * @param  paddingPx paddingPx
     */
    public void setTabPaddingLeftRight(int paddingPx) {
        this.mTabPadding = paddingPx;
        updateTabStyles();
    }

    /**
     * @return mTabPadding
     */
    public int getTabPaddingLeftRight() {
        return mTabPadding;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mCurrentPosition = savedState.mCurrentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.mCurrentPosition = mCurrentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState {

        private int mCurrentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mCurrentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mCurrentPosition);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
