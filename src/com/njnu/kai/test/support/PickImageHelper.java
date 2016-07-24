package com.njnu.kai.test.support;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import java.io.File;

/**
 * @version 1.0.0
 */
public final class PickImageHelper {

    private static final String LOG_TAG = "PickImageHelper";

    private static final String KEY_WIDTH = "width";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_PATH = "path";
    private static final String KEY_PICK_ID = "pick_id";

    /**
     * 从剪切中返回
     */
    public static final int CROP_SELECTED_PHOTO = 3;
    private static final int FROM_GALLERY = 1;
    private static final int FROM_CAMERA = 2;
    private static String sTempCameraFilePath;

    private Activity mAttachedActivity;
    private int mCachedWidth, mCachedHeight;
    private String mCachedCroppedPath;
    private int mCachedPickId = CROP_SELECTED_PHOTO;

    public static void setTempCameraFolder(String tempCameraFolder) {
        sTempCameraFilePath = tempCameraFolder + File.separator + ".tmp.jpg";
    }

    public static String getTempCameraFilePath() {
        return sTempCameraFilePath;
    }

    /**
     * 构造方法
     *
     * @param activity 调用的activity
     */
    public PickImageHelper(Activity activity) {
        mAttachedActivity = activity;
    }




    /**
     * 从本地媒体库选取图片
     *
     * @param title       标题
     * @param requestCode 请求代码
     * @param width       宽
     * @param height      高
     * @return 如果成功返回true
     */
    public boolean pickImageFromGallery(CharSequence title, int requestCode, int width, int height) {
        try {
            pickImage(new Intent(Intent.ACTION_PICK)
                    .setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                    , title, requestCode, width, height);
            mCachedPickId = FROM_GALLERY;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从本地媒体库选取图片
     *
     * @param fragment    所在的fragment界面
     * @param title       标题
     * @param requestCode 请求代码
     * @param width       宽
     * @param height      高
     * @return 如果成功返回true
     */
    public boolean pickImageFromGallery(Fragment fragment, CharSequence title, int requestCode, int width, int height) {
        try {
            pickImage(fragment, new Intent(Intent.ACTION_PICK)
                    .setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                    , title, requestCode, width, height);
            mCachedPickId = FROM_GALLERY;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 使用照相机选取图片
     *
     * @param title       标题
     * @param requestCode 请求代码
     * @param width       宽
     * @param height      高
     * @return 如果成功返回true
     */
    public boolean pickImageFromCamera(CharSequence title, int requestCode, int width, int height) {
        try {
            pickImage(new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(sTempCameraFilePath)))
                    , title, requestCode, width, height);
            mCachedPickId = FROM_CAMERA;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 使用照相机选取图片
     *
     * @param fragment    所在的fragment界面
     * @param title       标题
     * @param requestCode 请求代码
     * @param width       宽
     * @param height      高
     * @return 如果成功返回true
     */
    public boolean pickImageFromCamera(Fragment fragment, CharSequence title, int requestCode, int width, int height) {
        try {
            pickImage(fragment, new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(sTempCameraFilePath)))
                    , title, requestCode, width, height);
            mCachedPickId = FROM_CAMERA;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void pickImage(Intent actionIntent, CharSequence title, int requestCode, int width, int height) {
        mAttachedActivity.startActivityForResult(Intent.createChooser(
                actionIntent.putExtra("return-data", false)
                , title)
                , requestCode);
        mCachedWidth = width;
        mCachedHeight = height;
    }

    private void pickImage(Fragment fragment, Intent actionIntent, CharSequence title, int requestCode, int width, int height) {
        fragment.startActivityForResult(Intent.createChooser(
                actionIntent.putExtra("return-data", false)
                , title)
                , requestCode);
        mCachedWidth = width;
        mCachedHeight = height;
    }


    /**
     * get image path
     *
     * @return path
     */
    public String getCroppedImagePath() {
        return mCachedCroppedPath;
    }

    /**
     * Save instance
     *
     * @param outState outState
     */
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putInt(KEY_WIDTH, mCachedWidth);
            outState.putInt(KEY_HEIGHT, mCachedHeight);
            outState.putString(KEY_PATH, mCachedCroppedPath);
            outState.putInt(KEY_PICK_ID, mCachedPickId);
        }
    }

    /**
     * Restore instance
     *
     * @param inState inState
     */
    public void onRestoreInstanceState(Bundle inState) {
        if (inState != null) {
            mCachedWidth = inState.getInt(KEY_WIDTH, mCachedWidth);
            mCachedHeight = inState.getInt(KEY_HEIGHT, mCachedHeight);
            mCachedPickId = inState.getInt(KEY_PICK_ID, mCachedPickId);
            mCachedCroppedPath = inState.getString(KEY_PATH);
        }
    }
}
