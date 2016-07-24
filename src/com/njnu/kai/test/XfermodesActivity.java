package com.njnu.kai.test;

import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.view.View;
import com.njnu.kai.test.support.BaseActivity;
import com.njnu.kai.test.support.DisplayUtils;

public class XfermodesActivity extends BaseActivity {

    // create a bitmap with a circle, used for the "dst" image
    static Bitmap makeDst(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        p.setColor(0xFF00FF00);
        c.drawOval(new RectF(0, 0, w * 3 / 4, h * 3 / 4), p);
        return bm;
    }

    // create a bitmap with a rect, used for the "src" image
    static Bitmap makeSrc(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        LinearGradient linearGradient = new LinearGradient(0, 0, 0, DisplayUtils.dp2px(60), 0xFF000000, 0x00000000, Shader.TileMode.CLAMP);
        p.setShader(linearGradient);
        c.drawRect(w / 3, h / 3, w * 19 / 20, h * 19 / 20, p);
        return bm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new SampleView(this));
    }

    private static class SampleView extends View {
        private static final int W = DisplayUtils.dp2px(64);
        private static final int H = DisplayUtils.dp2px(64);
        private static final int ROW_MAX = 4;   // number of samples per row

        private Bitmap mSrcB;
        private Bitmap mDstB;
        private Shader mBG;     // background checker-board pattern

        private static final Xfermode[] sModes = {
                new PorterDuffXfermode(PorterDuff.Mode.CLEAR),
                new PorterDuffXfermode(PorterDuff.Mode.SRC),
                new PorterDuffXfermode(PorterDuff.Mode.DST),
                new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER),
                new PorterDuffXfermode(PorterDuff.Mode.DST_OVER),
                new PorterDuffXfermode(PorterDuff.Mode.SRC_IN),
                new PorterDuffXfermode(PorterDuff.Mode.DST_IN),
                new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT),
                new PorterDuffXfermode(PorterDuff.Mode.DST_OUT),
                new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP),
                new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP),
                new PorterDuffXfermode(PorterDuff.Mode.XOR),
                new PorterDuffXfermode(PorterDuff.Mode.DARKEN),
                new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN),
                new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY),
                new PorterDuffXfermode(PorterDuff.Mode.SCREEN)
        };

        private static final String[] sLabels = {
                "Clear", "Src", "Dst", "SrcOver",
                "DstOver", "SrcIn", "DstIn", "SrcOut",
                "DstOut", "SrcATop", "DstATop", "Xor",
                "Darken", "Lighten", "Multiply", "Screen"
        };

        public SampleView(Context context) {
            super(context);

            mSrcB = makeSrc(W, H);
            mDstB = makeDst(W, H);

            // make a ckeckerboard pattern
            Bitmap bm = Bitmap.createBitmap(new int[]{0xFFFFFFFF, 0xFFCCCCCC,
                            0xFFCCCCCC, 0xFFFFFFFF}, 2, 2,
                    Bitmap.Config.RGB_565
            );
            mBG = new BitmapShader(bm,
                    Shader.TileMode.REPEAT,
                    Shader.TileMode.REPEAT);
            Matrix m = new Matrix();
            m.setScale(6, 6);
            mBG.setLocalMatrix(m);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);

            Paint labelP = new Paint(Paint.ANTI_ALIAS_FLAG);
            labelP.setTextSize(DisplayUtils.dp2px(14));
            labelP.setTextAlign(Paint.Align.CENTER);

            Paint paint = new Paint();
            paint.setFilterBitmap(false);

            canvas.translate(15, 35);

            int x = 0;
            int y = 0;
            for (int i = 0; i < sModes.length; i++) {
                // draw the border
                paint.setStyle(Paint.Style.STROKE);
                paint.setShader(null);
                canvas.drawRect(x - 0.5f, y - 0.5f,
                        x + W + 0.5f, y + H + 0.5f, paint);

                // draw the checker-board pattern
                paint.setStyle(Paint.Style.FILL);
                paint.setShader(mBG);
                canvas.drawRect(x, y, x + W, y + H, paint);

                // draw the src/dst example into our offscreen bitmap
                int sc = canvas.saveLayer(x, y, x + W, y + H, null,
                        Canvas.MATRIX_SAVE_FLAG |
                                Canvas.CLIP_SAVE_FLAG |
                                Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                                Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                                Canvas.CLIP_TO_LAYER_SAVE_FLAG
                );
                canvas.translate(x, y);
                canvas.drawBitmap(mDstB, 0, 0, paint);
                paint.setXfermode(sModes[i]);

//                LinearGradient linearGradient = new LinearGradient(0, 0, 0, DisplayUtils.dp2px(60), 0xFF000000, 0x00000000, Shader.TileMode.CLAMP);
//                paint.setShader(linearGradient);
//                paint.setColor(Color.BLUE);
//                canvas.drawRect(W / 3, H / 3, W * 19 / 20, H * 19 / 20, paint);

                canvas.drawBitmap(mSrcB, 0, 0, paint);

                paint.setXfermode(null);
                canvas.restoreToCount(sc);

                // draw the label
                canvas.drawText(sLabels[i],
                        x + W / 2, y - labelP.getTextSize() / 2, labelP);

                x += W + 10;

                // wrap around when we've drawn enough for one row
                if ((i % ROW_MAX) == ROW_MAX - 1) {
                    x = 0;
                    y += H + DisplayUtils.dp2px(30);
                }
            }
        }
    }
}

