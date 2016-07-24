package com.njnu.kai.test.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-4-29
 */
public class KeepTopImageView extends ImageView {

    private static final int MATRIX_SIZE = 9;
    private static final int MATRIX_TRANS_Y = 5;

    /**
     * @param context context
     */
    public KeepTopImageView(Context context) {
        super(context);
    }

    /**
     * @param context context
     * @param attrs attrs
     */
    public KeepTopImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param context context
     * @param attrs attrs
     * @param defStyle default style
     */
    public KeepTopImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Matrix imageMatrix = getImageMatrix();
        float[] values = new float[MATRIX_SIZE];
        imageMatrix.getValues(values);
        if (values[MATRIX_TRANS_Y] != 0) {
            values[MATRIX_TRANS_Y] = 0;
            imageMatrix.setValues(values);
        }
        super.onDraw(canvas);
    }
}
