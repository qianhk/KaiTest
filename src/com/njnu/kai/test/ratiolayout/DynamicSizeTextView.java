package com.njnu.kai.test.ratiolayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-3-3
 */
public class DynamicSizeTextView extends android.widget.TextView implements DynamicSize {

    private float mOriginTextSize;

    public DynamicSizeTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mOriginTextSize = getTextSize();
    }

    public DynamicSizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DynamicSizeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @Override
    public void scaleRatio(float ratio) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, mOriginTextSize * ratio);
    }
}
