package com.njnu.kai.test.support;

import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

/**
 * 图片处理工具.
 *
 * @version 1.0.0
 */
public class BitmapUtils {  //FIXME 与SDK的BitmapUtils合并
    /**
     * bitmap with alpha channel
     */
    public static final String LOG_TAG = "BitmapUtils";

    /**
     * 转换drawable到bitmap
     * @param drawable drawable
     * @param width width
     * @param height height
     * @return bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        options.inInputShareable = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        try {
            return BitmapFactory.decodeFile(filePath, options);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if ((reqWidth > 0 && reqHeight > 0) && (height > reqHeight || width > reqWidth)) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }
        return 0 == inSampleSize ? 1 : inSampleSize;
    }

    public static int getAverageColor(Drawable drawable, float offset, Matrix matrix) {
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            int height = bitmap.getHeight();
            int yPos = (int)(height * offset);
            long color = 0;
            int width = bitmap.getWidth();
            int size = 0;
            if (yPos < height && yPos >= 0) {
            for (int idx = 0; idx < width; ++idx) {
                int pixel = bitmap.getPixel(idx, yPos);
                if ((pixel & 0xFF000000) == 0xFF000000) {
                    ++size;
                    color += pixel;
                }
            }
            }
            if (size > 0) {
                color /= size;
            }
            return (int)color;
        } else {
            int color = (int)(255.0f * offset);
            return Color.rgb(color, color, color);
        }
    }
}
