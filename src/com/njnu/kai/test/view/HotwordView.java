package com.njnu.kai.test.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.util.StateSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.DisplayUtils;
import com.njnu.kai.test.support.SDKVersionUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * @version 1.0.0
 * @since 14-8-12
 */
public class HotwordView extends ViewGroup {

    private static final int DEFAULT_MARGIN = 8;
    private static final int DEFAULT_WORD_COUNT = 10;
    private static final int CORNER_RADIUS = DisplayUtils.dp2px(4);

    private int mMargin;

    private OnHotwordClickListener mListener;

    private Drawable mBackgroundDrawable;

    /**
     * @param context context
     */
    public HotwordView(Context context) {
        super(context);
        init(context);
    }

    /**
     * @param context context
     * @param attrs attrs
     */
    public HotwordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * @param context context
     * @param attrs attrs
     * @param defStyle default style
     */
    public HotwordView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mMargin = DisplayUtils.dp2px(DEFAULT_MARGIN);
    }

    /**
     * @param margin 设置元素间间距
     */
    public void setMargin(int margin) {
        mMargin = margin;
        requestLayout();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }
        int needWidth = 0;
        int idx = 0;
        int row = 0;
        int parentWidth = right - left;
        int childViewHeight = getChildAt(0).getMeasuredHeight();
        int currentTop = 0;
        for (; idx < childCount && row <= 1;) {
            View view = getChildAt(idx);
            int childWidth = view.getMeasuredWidth();
            if (needWidth > 0) {
                needWidth += mMargin;
            }
            if (needWidth == 0 || (needWidth + childWidth) <= parentWidth) {
                view.layout(needWidth, currentTop, needWidth + childWidth, currentTop + childViewHeight);
                needWidth += childWidth;
                ++idx;
            } else {
                ++row;
                currentTop += (childViewHeight + mMargin);
                needWidth = 0;
            }
        }
        for (; idx < childCount; ++idx) {
            View view = getChildAt(idx);
            view.layout(right, bottom, right, bottom);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        int useHeight = 0;
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int childMeasure = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.AT_MOST);
        if (childCount > 0) {
            for (int idx = 0; idx < childCount; ++idx) {
                getChildAt(idx).measure(childMeasure, childMeasure);
            }
            View view = getChildAt(0);
            int measuredHeight = view.getMeasuredHeight();
            useHeight = measuredHeight * 2 + mMargin;
//            int useMeasuredHeight = MeasureSpec.makeMeasureSpec(useHeight, MeasureSpec.EXACTLY);
            setMeasuredDimension(widthSize, useHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        LogUtils.d("HotwordView", String.format("HotwordView onMeasure %08X %d %08X %d count=%d useM=%08X h=%d"
//                , widthMode, widthSize, heightMode, heightSize, childCount, MeasureSpec.getMode(getMeasuredHeightAndState()), useHeight));
    }

    private void prepareView(int totalCount) {
        int childCount = getChildCount();
        if (childCount > totalCount) {
            for (int idx = childCount - 1; idx >= totalCount; --idx) {
                removeViewAt(idx);
            }
        } else if (childCount < totalCount) {
            for (int idx = childCount; idx < totalCount; ++idx) {
                addView(makeView(idx));
            }
        }
    }

    private View makeView(int index) {
        View view = View.inflate(getContext(), R.layout.fragment_search_result_hotword, null);
        view.setOnClickListener(mOnClickListener);
        setTheme(view);
//        view.setPadding(30, 30, 30, 30);
        return view;
    }

    /**
     * @param billboardsList 设置具体内容
     */
    public void setContent(List<String> billboardsList) {
        int count = billboardsList != null ? billboardsList.size() : 0;
        if (count > DEFAULT_WORD_COUNT) {
            Collections.shuffle(billboardsList);
            billboardsList = billboardsList.subList(0, DEFAULT_WORD_COUNT);
            count = billboardsList.size();
        }
        prepareView(count);
        if (count > 0) {
            Collections.sort(billboardsList);
            for (int idx = 0; idx < count; ++idx) {
                TextView textView = (TextView)getChildAt(idx);
                textView.setText(billboardsList.get(idx));
            }
        }
    }

    /**
     * @param listener listener
     */
    public void setListener(OnHotwordClickListener listener) {
        mListener = listener;
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onClickHotword(((TextView)v).getText().toString());
            }
        }
    };

    private void setTheme(View view) {

        ShapeDrawable normalShapeDrawable = null;
        ShapeDrawable selectedShapeDrawable = null;
        if (mBackgroundDrawable == null) {
            float[] outerRadius = new float[] {CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS
                    , CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS };
            normalShapeDrawable = new ShapeDrawable(new RoundRectShape(outerRadius, null, null));
            normalShapeDrawable.getPaint().setColor(Color.CYAN);
            selectedShapeDrawable = new ShapeDrawable(new RoundRectShape(outerRadius, null, null));
            selectedShapeDrawable.getPaint().setColor(Color.MAGENTA);
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, selectedShapeDrawable);
            stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, selectedShapeDrawable);
            stateListDrawable.addState(StateSet.WILD_CARD, normalShapeDrawable);
            mBackgroundDrawable = stateListDrawable;
        }
        if (mBackgroundDrawable != null) {
            Drawable background = mBackgroundDrawable;//mBackgroundDrawable.getConstantState().newDrawable();
            mBackgroundDrawable = null;
            if (normalShapeDrawable != null) {
                normalShapeDrawable.setPadding(null);
                selectedShapeDrawable.setPadding(null);
            }
            Rect padding = new Rect();
            boolean successGotPadding = background.getPadding(padding);
            int paddingLeft = view.getPaddingLeft();
            int paddingRight = view.getPaddingRight();
            int paddingBottom = view.getPaddingBottom();
            int paddingTop = view.getPaddingTop();
            view.setBackgroundDrawable(background);
//            view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
//        } else {
//            ThemeManager.setThemeResource(view, ThemeElement.TILE_MASK);
        }
    }
//
//    /**
//     * 获取显示状态的Drawable
//     *
//     * @return 返回一个临时的包括了正常和选中状态的StateListDrawable
//     */
//    public static Drawable getBackgroundDrawable() {
//        ThemeFramework.ThemeDrawable rawThemeDrawable = ThemeManager.getRawThemeDrawable(ThemeElement.TILE_MASK);
//        if (rawThemeDrawable instanceof ThemeFramework.ThemeColorDrawable) {
//            ColorDrawable normalDrawable = (ColorDrawable)rawThemeDrawable.getNormalDrawable();
//            ColorDrawable selectedDrawable = (ColorDrawable)rawThemeDrawable.getSelectedDrawable();
//            float[] outerRadius = new float[] {CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS
//                    , CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS };
//            ShapeDrawable normalShapeDrawable = null;
//            if (normalDrawable != null) {
//                normalShapeDrawable = new ShapeDrawable(new RoundRectShape(outerRadius, null, null));
//                normalShapeDrawable.getPaint().setColor(getColorDrawableColor(normalDrawable));
//            }
//            ShapeDrawable selectedShapeDrawable = null;
//            if (selectedDrawable != null) {
//                selectedShapeDrawable = new ShapeDrawable(new RoundRectShape(outerRadius, null, null));
//                selectedShapeDrawable.getPaint().setColor(getColorDrawableColor(selectedDrawable));
//            }
//            if (normalShapeDrawable != null && selectedShapeDrawable != null) {
//                StateListDrawable stateListDrawable = new StateListDrawable();
//                stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, selectedShapeDrawable);
//                stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, selectedShapeDrawable);
//                stateListDrawable.addState(StateSet.WILD_CARD, normalShapeDrawable);
//                return stateListDrawable;
//            } else {
//                return normalShapeDrawable != null ? normalShapeDrawable : selectedShapeDrawable;
//            }
//        }
//
//        return null;
//    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static int getColorDrawableColor(ColorDrawable colorDrawable) {
        if (SDKVersionUtils.hasHoneycomb()) {
            return colorDrawable.getColor();
        } else {
            try {
                Field stateField = ColorDrawable.class.getDeclaredField("mState");
                stateField.setAccessible(true);
                Object object = stateField.get(colorDrawable);
                Field colorField = object.getClass().getDeclaredField("mUseColor");
                colorField.setAccessible(true);
                Number colorObj = (Number)colorField.get(object);
                return colorObj.intValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Color.TRANSPARENT;
        }
    }

    /**
     * 加载主题
     */
    public void onThemeLoaded() {
        mBackgroundDrawable = null;
        for (int idx = getChildCount() - 1; idx >= 0; --idx) {
            setTheme(getChildAt(idx));
        }
    }

    /**
     * 热搜词被点击接口
     */
    public interface OnHotwordClickListener {
        /**
         * @param word 被点击的热搜词
         */
        public void onClickHotword(String word);
    }

    public class NoPaddingStateListDrawable extends StateListDrawable {
        @Override
        public boolean getPadding(Rect padding) {
            return false;
        }


    }
}
