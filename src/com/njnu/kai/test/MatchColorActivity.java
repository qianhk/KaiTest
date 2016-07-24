package com.njnu.kai.test;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;
import com.njnu.kai.test.support.*;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-8-8
 */
public class MatchColorActivity extends Activity {

    private static final int REQUEST_FROM_GALLEY = 1;
    private static final String LOG_TAG = "MatchColorActivity";
    private static final float ROTATE_IMAGE_DEGREE = 90.0f;

    private PickImageHelper mPickImageHelper;
    private LineImageView mImageView;
    private View mView;
    private float mOffset = 0.5f;
    private int mColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_match_color);
        findViewById(R.id.btn_choose_pic).setOnClickListener(mOnClickListener);
        mImageView = (LineImageView)findViewById(R.id.iv_pic);
        mView = findViewById(R.id.view);

        SeekBar seekBar = (SeekBar)findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (SDKVersionUtils.hasHoneycomb()) {
                    changeOffset(1.0f * progress / 100.0f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBar.setProgress((int)(mOffset * seekBar.getMax()));
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void changeOffset(float offset) {
        mImageView.setLineOffsetPosition(offset);
        Matrix matrix = mImageView.getMatrix();
        Matrix imageMatrix = mImageView.getImageMatrix();
        Drawable drawable = mImageView.getDrawable();
        int averageColor = BitmapUtils.getAverageColor(drawable, offset, imageMatrix);
        LogUtils.e("Test", "matrix1 matrix2=%s m=%s drawableWidth=%d %d viewWidth=%d %d argb=%X %X %X %X", matrix.toShortString(), imageMatrix.toShortString()
                , drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), mImageView.getWidth(), mImageView.getHeight()
        , Color.alpha(averageColor) , Color.red(averageColor), Color.green(averageColor), Color.blue(averageColor));
        mView.setBackgroundColor(averageColor);
    }

    private PickImageHelper getPickImageHelper() {
        if (mPickImageHelper == null) {
            mPickImageHelper = new PickImageHelper(this);
        }
        return mPickImageHelper;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            if (viewId == R.id.btn_choose_pic) {
                getPickImageHelper().pickImageFromGallery(getString(R.string.image_from_galley)
                        , REQUEST_FROM_GALLEY, mImageView.getWidth(), mImageView.getHeight());
            }
        }
    };

    private void flushPhotoView(Uri fileUri) {
        try {
            Bitmap bitmap;
            if (fileUri.getScheme().equals("content")) {
                bitmap = getContentPic(fileUri);
            } else {
                bitmap = getFilePathPic(fileUri.getPath());
            }
            LogUtils.d(LOG_TAG, "flushPhotoView bitmap width=%d height=%d", bitmap.getWidth(), bitmap.getHeight());
            mImageView.setImageBitmap(bitmap);
        } catch (OutOfMemoryError memoryError) {
            LogUtils.e(LOG_TAG, "show flushPhotoView OutOfMemoryError: " + memoryError.toString());
            ToastUtils.showToast(this, "内存不够显示图片");
        } catch (Exception e) {
            LogUtils.e(LOG_TAG, "show flushPhotoView Exception e=" + e.toString());
            ToastUtils.showToast(this, "显示图片发生错误, " + e.toString());
        }
    }



    private Bitmap getFilePathPic(String path) throws Exception {
        Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromFile(path, mImageView.getWidth(), mImageView.getHeight());
        Bitmap bitmapRotated = bitmap;
        ExifInterface exifInterface = new ExifInterface(path);
        int tagOrientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        if (tagOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            Matrix matrix = new Matrix();
            matrix.postRotate(ROTATE_IMAGE_DEGREE);
            bitmapRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (bitmapRotated != bitmap) {
                bitmap.recycle();
            }
            LogUtils.d(LOG_TAG, "getFilePathPic Rotate_90 bitmap=%b bitmapRotated=%b", bitmap.isRecycled(), bitmapRotated.isRecycled());
        }
        return bitmapRotated;
    }

    private Bitmap getContentPic(Uri fileUri) throws Exception {
        Cursor query = this.getContentResolver().query(fileUri, null, null, null, null);
        query.moveToFirst();
        String picPath = query.getString(query.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
        query.close();
        return getFilePathPic(picPath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_FROM_GALLEY:
                    if (data != null) {
                        flushPhotoView(data.getData());
                    } else {
                        ToastUtils.showToast(MatchColorActivity.this, getString(R.string.can_not_open_image) + " result data==null");
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            toggleDialog();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void toggleDialog() {
        GlobalMenuDialog globalMenuDialog = new GlobalMenuDialog(this, R.style.Global_Menu_Dialog);
        globalMenuDialog.setBackgroundColor(mColor);
        globalMenuDialog.show();
    }
}