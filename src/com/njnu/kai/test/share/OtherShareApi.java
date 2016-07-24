package com.njnu.kai.test.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.ShareConstant;
import com.njnu.kai.test.support.StringUtils;

import java.io.File;

/**
 */
public class OtherShareApi {
    private Context mContext;

    /**
     * @param context Context
     */
    public OtherShareApi(Context context) {
        mContext = context;
    }

//    @Override
//    public ShareResult doShare(ShareInfo shareInfo, ApiCallback apiCallback) {
//        String title = shareInfo.isFromMusicCircle() ? mContext.getString(R.string.forward_to) : mContext.getString(R.string.share_to);
//        startShareActivity(mContext, "image/*", shareInfo.getMessage(), shareInfo.getLocalImagePath(), title);
//        return null;
//    }

    /**
     * 启动分享Activity
     *
     * @param context 上下文对象
     * @param type    分享类型
     * @param body    分享文本
     * @param path    分享的图片(或者音频)路径
     * @param title   分享对话框标题
     */
    public void startShareActivity(Context context, String type, String body, String path, String title) {
        Intent intent = new Intent(Intent.ACTION_SEND).setType(type);
        String subject = mContext.getString(R.string.share);
        if (!StringUtils.isEmpty(path)) {
            Uri uri = Uri.fromFile(new File(path));
            intent.putExtra(Intent.EXTRA_STREAM, uri);
        }
        intent.putExtra(ShareConstant.PARAMETER_SMS_BODY, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ((Activity) context).startActivityForResult(Intent.createChooser(intent, title), 1);
    }
}
