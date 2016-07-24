package com.njnu.kai.test.view;

import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.BaseActivity;


public class KeepTopImageActivity extends BaseActivity implements ImageView.OnClickListener {

    private static final String LOG_TAG = "FitCenterImageViewActivity";
    private ImageView mImageView1;
    private ImageView mImageView2;
    private ImageView mImageView3;
    private ImageView mImageView4;
    private ImageView mImageView5;
    private SharedPreferences.Editor mEditor;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_keep_top_image);

        mImageView1 = (ImageView)findViewById(R.id.iv_1);
        mImageView1.setTag("imageview_1");

        mImageView2 = (ImageView)findViewById(R.id.iv_2);
        mImageView2.setTag("imageview_2");

        mImageView3 = (ImageView)findViewById(R.id.iv_3);
        mImageView3.setTag("imageview_3");

        mImageView4 = (ImageView)findViewById(R.id.iv_4);
        mImageView4.setTag("imageview_4");

        mImageView5 = (ImageView)findViewById(R.id.iv_5);
        mImageView5.setTag("imageview_5");

//        amendAllImageView();

    }

    private void onViewGlobalLayout(final ImageView imageView) {
        ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Log.d(LOG_TAG, "onGlobalLayout tag=" + imageView.getTag());
                    imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    amendMatrixForCenterCrop(imageView);
                }
            });
        }
        imageView.setOnClickListener(this);
    }

    private void amendAllImageView() {
        onViewGlobalLayout(mImageView1);
        onViewGlobalLayout(mImageView2);
        onViewGlobalLayout(mImageView3);
        onViewGlobalLayout(mImageView4);
        onViewGlobalLayout(mImageView5);
    }

    public static void amendMatrixForCenterCrop(ImageView imageView) {
        if (imageView == null) {
            return;
        }

        Drawable drawable = imageView.getDrawable();
        int drawableHeight = drawable != null ? drawable.getIntrinsicHeight() : 0;
        int drawableWidth = drawable != null ? drawable.getIntrinsicWidth() : 0;
        int viewWidth = imageView.getWidth();
        int viewHeight = imageView.getHeight();
        if (drawableHeight <= 0 || drawableWidth <= 0 || viewWidth <= 0 || viewHeight <= 0) {
            return;
        }
        Log.d(LOG_TAG, String.format("amendMatrixForCenterCrop tag=%s view=%d,%d drawable=%d,%d", imageView.getTag(), viewWidth, viewHeight, drawableWidth, drawableHeight));
        float horizontalScaleRatio = 1.0f * viewWidth / drawableWidth;
        float verticalScaleRatio = 1.0f * viewHeight / drawableHeight;
        if (verticalScaleRatio >= horizontalScaleRatio) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Log.d(LOG_TAG, String.format("use system center_crop %f %f", horizontalScaleRatio, verticalScaleRatio));
        } else {
            imageView.setScaleType(ImageView.ScaleType.MATRIX);
            float scaleRatio = Math.max(horizontalScaleRatio, verticalScaleRatio);
            Matrix matrix = new Matrix();
            matrix.postScale(scaleRatio, scaleRatio);
            imageView.setImageMatrix(matrix);
            Log.d(LOG_TAG, String.format("use my matrix %f %f scale=%f", horizontalScaleRatio, verticalScaleRatio, scaleRatio));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mImageView1) {
            Log.d(LOG_TAG, "open Editor");
            mEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        } else if (v == mImageView2) {
            Log.d(LOG_TAG, "put iv 2");
            PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("imageview2", 1).commit();
        } else if (v == mImageView3) {
            Log.d(LOG_TAG, "put iv 1");
            mEditor.putInt("imageview1", 1).commit();
        } else if (v == mImageView4) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            Log.d(LOG_TAG, String.format("i1=%d i2=%d", preferences.getInt("imageview1", 0), preferences.getInt("imageview2", 0)));
        } else if (v == mImageView5) {
//            SharedPreferences preferences = getSharedPreferences("testp", Context.MODE_MULTI_PROCESS);
//            SharedPreferences.Editor edit = preferences.edit();
        }

    }
}