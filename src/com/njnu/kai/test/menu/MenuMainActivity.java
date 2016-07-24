package com.njnu.kai.test.menu;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import com.njnu.kai.test.R;
import com.njnu.kai.test.menu.draglayout.DragLayoutMenuActivity;
import com.njnu.kai.test.menu.draglayout.MenuWithViewPagerActivity;
import com.njnu.kai.test.support.LogUtils;
import com.njnu.kai.test.support.SecurityUtils;
import com.njnu.kai.test.support.ToastUtils;

import java.io.File;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-4-9
 */
public class MenuMainActivity extends Activity {

    private static final String TAG = "MenuMainActivity";

    private static final String IMAGE_FILE_PATH = "/sdcard/test.jpg";

    public static final String BROADCAST_DA_MESSAGE = "com.autonavi.cvc.da.code";
    public static final String MESSAGE_CODE = "code";
    public static final int CODE_MUSIC_PREV = 1025;
    public static final int CODE_MUSIC_NEXT = 1026;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            if (viewId == R.id.btn_test1) {
                final Intent intent = new Intent(BROADCAST_DA_MESSAGE);
                intent.putExtra(MESSAGE_CODE, CODE_MUSIC_PREV);
                sendBroadcast(intent);
                ToastUtils.showToast(MenuMainActivity.this, "send prev");
                test1();
            } else if (viewId == R.id.btn_test2) {
                final Intent intent = new Intent(BROADCAST_DA_MESSAGE);
                intent.putExtra(MESSAGE_CODE, CODE_MUSIC_NEXT);
                sendBroadcast(intent);
                ToastUtils.showToast(MenuMainActivity.this, "send next");
                test2();
            } else if (viewId == R.id.btn_share_circle) {
                shareToCircle(v.getContext(), "测试Circle分享文字", IMAGE_FILE_PATH);
            } else if (viewId == R.id.btn_share_wechat) {
                shareToWeChat(v.getContext(), "测试Wechat分享文字", IMAGE_FILE_PATH);
            } else if (viewId == R.id.btn_share_wechat_no_pic) {
                shareToWeChat(v.getContext(), "测试Wechat分享文字_no_pic", null);
            } else if (viewId == R.id.btn_share_to_qq) {
                shareToQQ(v.getContext(), "测试分享文字share_to_qq", IMAGE_FILE_PATH);
            } else if (viewId == R.id.btn_share_to_qzone) {
                shareToQZone(v.getContext(), "测试分享文字share_to_qzone", IMAGE_FILE_PATH);
            } else {
                testFunction(2);
            }
        }
    };

    private static final String WECHAT_PKG = "com.tencent.mm";
    private static final String WECHAT_CLASS = "com.tencent.mm.ui.tools.ShareImgUI";
    private static final String CIRCLE_CLASS = "com.tencent.mm.ui.tools.ShareToTimeLineUI";


    private static final String QQ_PKG = "com.tencent.mobileqq";
    private static final String QQ_CLASS = "com.tencent.mobileqq.activity.JumpActivity";

    private static final String QZONE_PKG = "com.qzone";
    private static final String QZONE_CLASS = "com.qzonex.module.operation.ui.QZonePublishMoodActivity";

    private void shareToQQ(Context context, String text, String filePath) {
        try {
            Intent localIntent = new Intent();
            localIntent.setComponent(new ComponentName(QQ_PKG, QQ_CLASS));
            localIntent.setAction(Intent.ACTION_SEND);
            localIntent.putExtra(Intent.EXTRA_TEXT, text);
            localIntent.setType("text/*");
//            localIntent.putExtra(Intent.EXTRA_TITLE, "extra_title");
//            localIntent.putExtra(Intent.EXTRA_SUBJECT, "extra_subject");
//            if (filePath != null) {
//                File file = new File(filePath);
//                if (file.exists()) {
//                    localIntent.setType("image/*");
//                    localIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
////                    localIntent.setDataAndType(Uri.fromFile(file), "image/*");
//                }
//            }
            context.startActivity(localIntent);
        } catch (ActivityNotFoundException e) {
            ToastUtils.showToast(context, "Share Failed To Friend");
        }
    }

    private void shareToQZone(Context context, String text, String filePath) {
        boolean shareToQZoneSuccess = false;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(QZONE_PKG, PackageManager.GET_RESOLVED_FILTER);
            if (packageInfo != null) {
                Intent localIntent = new Intent();
                localIntent.setComponent(new ComponentName(QZONE_PKG, QZONE_CLASS));
                localIntent.setAction(Intent.ACTION_SEND);
                localIntent.putExtra(Intent.EXTRA_TEXT, text);
                if (filePath != null) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        localIntent.setType("image/*");
                        localIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    }
                }
                context.startActivity(localIntent);
                shareToQZoneSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!shareToQZoneSuccess) {
            try {
                Intent localIntent = getPackageManager().getLaunchIntentForPackage(QQ_PKG);
                context.startActivity(localIntent);
            } catch (Exception e) {
                ToastUtils.showToast(context, "Share Failed To Friend");
            }
        }
    }

    private void shareToWeChat(Context context, String text, String filePath) {
        try {
            Intent localIntent = new Intent();
            localIntent.setComponent(new ComponentName(WECHAT_PKG, WECHAT_CLASS));
            localIntent.setAction(Intent.ACTION_SEND);
            localIntent.putExtra(Intent.EXTRA_TEXT, text);
            if (filePath != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    localIntent.setType("image/*");
                    localIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                }
            }
            context.startActivity(localIntent);
        } catch (ActivityNotFoundException e) {
            ToastUtils.showToast(context, "Share Failed To WeChat");
        }
    }

    private void shareToCircle(Context context, String text, String filePath) {
        try {
            Intent localIntent = new Intent();
            localIntent.setComponent(new ComponentName(WECHAT_PKG, CIRCLE_CLASS));
            localIntent.setAction(Intent.ACTION_SEND);
            localIntent.putExtra(Intent.EXTRA_TEXT, text);
            localIntent.putExtra("Kdescription", text);
            if (filePath != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    localIntent.setType("image/*");
                    localIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//                    localIntent.setDataAndType(Uri.fromFile(file), "image/*");
                }
            }
            context.startActivity(localIntent);
        } catch (ActivityNotFoundException e) {
            ToastUtils.showToast(context, "Share Failed To Friend");
        }
    }

    private void test1() {
        String pkg = a.auu.a.c("JgEOXA0VGiYLDQZXHRk=");
        String cls = a.auu.a.c("JgEOXA0VGiYLDQZXHRlrGwpcDR8bKR1NIRERBiA6DCYQHREJBw0XLDk=");
        String action = a.auu.a.c("JAAHABYZEGsHDQYcHgBrDwAGEB8aaz0mPD0=");
        String strKey = a.auu.a.c("LAMCFRxfXg==");
        String type = a.auu.a.c("JAAHABYZEGsHDQYcHgBrCxsGCxFaESs7Jg==");
        String fileKey = a.auu.a.c("JAAHABYZEGsHDQYcHgBrCxsGCxFaFjoxNzg9");

        LogUtils.d(TAG, "pkg=%s cls=%s action=%s strkey=%s type=%s filekey=%s", pkg, cls, action, strKey, type, fileKey);
    }

    private void test2() {
        String pkg = a.auu.a.c("JgEOXA0VGiYLDQZXHRk=");
        String cls = a.auu.a.c("JgEOXA0VGiYLDQZXHRlrGwpcDR8bKR1NIRERBiAnDhUsOQ==");
        String action = a.auu.a.c("JAAHABYZEGsHDQYcHgBrDwAGEB8aaz0mPD0=");
        String type = a.auu.a.c("JAAHABYZEGsHDQYcHgBrCxsGCxFaESs7Jg==");
        String strKey = a.auu.a.c("LAMCFRxfXg==");
        String fileKey = a.auu.a.c("JAAHABYZEGsHDQYcHgBrCxsGCxFaFjoxNzg9");
        LogUtils.d(TAG, "pkg=%s cls=%s action=%s strkey=%s type=%s filekey=%s", pkg, cls, action, strKey, type, fileKey);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_main);
        findViewById(R.id.btn_drag_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testFunction(1);
            }
        });
        findViewById(R.id.btn_viewpager).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_test1).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_test2).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_share_circle).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_share_wechat).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_share_wechat_no_pic).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_share_to_qq).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_share_to_qzone).setOnClickListener(mOnClickListener);

        LogUtils.d(TAG, "encrypt Kdescription=%s", SecurityUtils.RC4.encrypt("Kdescription"));
        LogUtils.d(TAG, "encrypt wechat_pg=%s", SecurityUtils.RC4.encrypt(WECHAT_PKG));
        LogUtils.d(TAG, "encrypt wechat_class=%s", SecurityUtils.RC4.encrypt(WECHAT_CLASS));
        String circleEncrypt = SecurityUtils.RC4.encrypt(CIRCLE_CLASS);
        LogUtils.d(TAG, "encrypt circle_class=%s", circleEncrypt);

        LogUtils.d(TAG, "decrypt circle_class=%s", SecurityUtils.RC4.decrypt(circleEncrypt));

        LogUtils.d(TAG, "encrypt qq_pkg=%s", SecurityUtils.RC4.encrypt(QQ_PKG));
        LogUtils.d(TAG, "encrypt qq_class=%s", SecurityUtils.RC4.encrypt(QQ_CLASS));
        LogUtils.d(TAG, "encrypt qzone_pkg=%s", SecurityUtils.RC4.encrypt(QZONE_PKG));
        LogUtils.d(TAG, "encrypt qzone_class=%s", SecurityUtils.RC4.encrypt(QZONE_CLASS));
    }

    private void testFunction(int type) {
        if (type == 1) {
            startActivity(new Intent(MenuMainActivity.this, DragLayoutMenuActivity.class));
        } else {
            startActivity(new Intent(MenuMainActivity.this, MenuWithViewPagerActivity.class));
        }
    }
}
