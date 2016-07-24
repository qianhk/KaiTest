//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.umeng.socialize.sso;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.umeng.socialize.bean.CustomPlatform;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.ShareType;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.UMShareMsg;
import com.umeng.socialize.common.ResContainer;
import com.umeng.socialize.common.ResContainer.ResType;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMediaObject;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.media.UMediaObject.MediaType;
import com.umeng.socialize.sso.UMTencentSsoHandler;
import com.umeng.socialize.sso.UMTencentSsoHandler.ObtainAppIdListener;
import com.umeng.socialize.sso.UMTencentSsoHandler.ObtainImageUrlListener;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.utils.OauthHelper;
import com.umeng.socialize.utils.SocializeUtils;
import com.umeng.socialize.utils.StatisticsDataUtils;
import java.util.ArrayList;

public class QZoneSsoHandler extends UMTencentSsoHandler {
    private static final String TAG = QZoneSsoHandler.class.getName();
    private IUiListener mUiListener = new IUiListener() {
        public void onError(UiError e) {
            Log.d("Tencent SSo Authorize --> onError:", e.toString());
            QZoneSsoHandler.this.mAuthListener.onError(new SocializeException(e.errorCode, e.errorMessage), SHARE_MEDIA.QZONE);
        }

        public void onCancel() {
            Log.d("### Tencent Sso Authorize --> onCancel", "Authorize Cancel");
            QZoneSsoHandler.this.mAuthListener.onCancel(SHARE_MEDIA.QZONE);
        }

        public void onComplete(Object response) {
            Log.d(QZoneSsoHandler.TAG, "oauth complete...");
            QZoneSsoHandler.mEntity.addOauthData(QZoneSsoHandler.this.mActivity, SHARE_MEDIA.QZONE, 1);
            QZoneSsoHandler.this.uploadToken(QZoneSsoHandler.this.mActivity, response, QZoneSsoHandler.this.mAuthListener);
        }
    };

    public QZoneSsoHandler(Activity activity, String appId, String appKey) {
        super(activity, appId, appKey);
    }

    public void authorize(Activity activity, UMAuthListener authListener) {
        if(this.mTencent != null && this.mTencent.isSessionValid()) {
            this.mTencent.logout(this.mActivity);
        }

        this.mAuthListener = authListener;
        SocializeConfig.setSelectedPlatfrom(SHARE_MEDIA.QZONE);
        if(TextUtils.isEmpty(this.mAppID)) {
            this.mAppID = (String)OauthHelper.getAppIdAndAppkey(this.mActivity).get("appid");
            this.mAppKey = (String)OauthHelper.getAppIdAndAppkey(this.mActivity).get("appkey");
        }

        if(!TextUtils.isEmpty(this.mAppID)) {
            this.authorizeCheck();
        } else {
            this.getAppIdFromServer(new ObtainAppIdListener() {
                public void onComplete() {
                    QZoneSsoHandler.this.authorizeCheck();
                }
            });
        }

    }

    private void authorizeCheck() {
        if(this.initTencent()) {
            if(this.validTencent()) {
                this.mTencent.reAuth(this.mActivity, sQQScope, this.mUiListener);
            } else if(this.mTencent != null) {
                this.mTencent.login(this.mActivity, sQQScope, this.mUiListener);
            }

        }
    }

    public int getRequstCode() {
        return 5657;
    }

    private IUiListener getShareToQZoneListener() {
        IUiListener iUiListener = new IUiListener() {
            public void onError(UiError error) {
                Log.e("IUiListener", "error code : " + error.errorCode + "       error message:" + error.errorMessage);
                QZoneSsoHandler.this.mSocializeConfig.fireAllListenersOnComplete(SnsPostListener.class, SHARE_MEDIA.QZONE, '鱂', QZoneSsoHandler.mEntity);
            }

            public void onCancel() {
                QZoneSsoHandler.this.mSocializeConfig.fireAllListenersOnComplete(SnsPostListener.class, SHARE_MEDIA.QZONE, '鱀', QZoneSsoHandler.mEntity);
            }

            public void onComplete(Object response) {
                int statusCode = QZoneSsoHandler.this.getResponseCode(response);
                char code = 200;
                if(statusCode != 0) {
                    code = '鱂';
                }

                QZoneSsoHandler.this.mSocializeConfig.fireAllListenersOnComplete(SnsPostListener.class, SHARE_MEDIA.QZONE, code, QZoneSsoHandler.mEntity);
                QZoneSsoHandler.this.sendReport(true);
            }
        };
        return iUiListener;
    }

    private Bundle buildParams(UMShareMsg shareMsg) {
        Bundle bundle = new Bundle();
        String shareContent = shareMsg.mText;
        byte shareType = 1;
        UMediaObject uMediaObject = shareMsg.getMedia();
        if(uMediaObject instanceof QZoneShareContent) {
            QZoneShareContent paths = (QZoneShareContent)uMediaObject;
            this.mTitle = paths.getTitle();
            shareContent = paths.getShareContent();
            if(!TextUtils.isEmpty(paths.getTargetUrl())) {
                this.mTargetUrl = paths.getTargetUrl();
            }

            uMediaObject = paths.getShareMedia();
        }

        if(uMediaObject instanceof UMImage && TextUtils.isEmpty(shareContent)) {
            shareType = 5;
            this.setShareToImage(bundle, uMediaObject);
        } else if(!(uMediaObject instanceof UMVideo) && !(uMediaObject instanceof UMusic)) {
            this.setShareToTextAndImage(bundle, uMediaObject);
        } else {
            shareType = 2;
            this.setShareToAudio(bundle, uMediaObject);
        }

        bundle.putString("summary", shareContent);
        ArrayList paths1 = new ArrayList();
        String imagePath = bundle.getString("imageUrl");
        bundle.remove("imageUrl");
        if(!TextUtils.isEmpty(imagePath)) {
            paths1.add(imagePath);
        }

        bundle.putStringArrayList("imageUrl", paths1);
        bundle.putInt("req_type", shareType);
        if(TextUtils.isEmpty(bundle.getString("title"))) {
            bundle.putString("title", "分享到QQ空间");
        }

        if(TextUtils.isEmpty(bundle.getString("targetUrl"))) {
            bundle.putString("targetUrl", "http://www.umeng.com/social");
            Log.w(TAG, "没有设置QZone targetUrl，分享将采用友盟默认targetUrl");
        }

        bundle.putString("appName", this.getAppName());
        this.mExtraData.clear();
        this.mExtraData.put("qzone_id", this.mAppID);
        this.mExtraData.put("qzone_secret", this.mAppKey);
        return bundle;
    }

    private void setShareToTextAndImage(Bundle bundle, UMediaObject uMediaObject) {
        this.setShareToImage(bundle, uMediaObject);
    }

    private void setShareToAudio(Bundle bundle, UMediaObject uMediaObject) {
        if(uMediaObject != null && (uMediaObject instanceof UMusic || uMediaObject instanceof UMVideo)) {
            if(uMediaObject instanceof UMusic) {
                this.parseMusic(uMediaObject);
            } else if(uMediaObject instanceof UMVideo) {
                this.parseVideo(uMediaObject);
            }

            String path = (String)this.mExtraData.get("image_path_local");
            if(TextUtils.isEmpty(path)) {
                path = (String)this.mExtraData.get("image_path_url");
            }

            bundle.putString("imageUrl", path);
            bundle.putString("targetUrl", this.mTargetUrl);
            bundle.putString("audio_url", uMediaObject.toUrl());
            bundle.putString("title", this.mTitle);
        } else {
            Log.e(TAG, "请设置分享媒体...");
        }
    }

    private void setShareToImage(Bundle bundle, UMediaObject uMediaObject) {
        this.parseImage(uMediaObject);
        String path = (String)this.mExtraData.get("image_path_local");
        if(TextUtils.isEmpty(path)) {
            path = (String)this.mExtraData.get("image_path_url");
        }

        bundle.putString("imageUrl", path);
        if(TextUtils.isEmpty(this.mTargetUrl)) {
            this.mTargetUrl = (String)this.mExtraData.get("image_target_url");
        }

        if(TextUtils.isEmpty(this.mTargetUrl)) {
            this.mTargetUrl = "http://www.umeng.com/social";
            Log.w(TAG, "没有设置QZone targetUrl，分享将采用友盟默认targetUrl");
        }

        bundle.putString("targetUrl", this.mTargetUrl);
        bundle.putString("title", this.mTitle);
        Log.w(TAG, "QZone不支持纯图片分享");
    }

    private UMShareMsg getShareMsg() {
        UMShareMsg shareMsg = null;
        if(mEntity.getShareMsg() != null) {
            shareMsg = mEntity.getShareMsg();
            mEntity.setShareMsg((UMShareMsg)null);
        } else {
            shareMsg = new UMShareMsg();
            shareMsg.mText = mEntity.getShareContent();
            shareMsg.setMediaData(mEntity.getMedia());
        }

        return shareMsg;
    }

    private void shareToQZone() {
        if(this.initTencent()) {
            Bundle bundle = this.buildParams(this.getShareMsg());
            int type = bundle.getInt("req_type");
            ArrayList paths = bundle.getStringArrayList("imageUrl");
            String imagePath = null;
            if(paths != null && paths.size() > 0) {
                imagePath = (String)paths.get(0);
            }

            boolean flag = this.isUploadImageAsync(imagePath, type);
            if(flag) {
                UMImage image = new UMImage(this.mActivity, imagePath);
                UMAuthListener authListener = this.createAuthListener(bundle, image);
                this.authorize(this.mActivity, authListener);
            } else {
                this.defaultQZoneShare(bundle);
            }

            mEntity.setShareType(ShareType.NORMAL);
        }
    }

    private void defaultQZoneShare(Bundle bundle) {
        this.mSocializeConfig.fireAllListenersOnStart(SnsPostListener.class);
        Log.d(TAG, "invoke Tencent.shareToQzone method...");
        if(this.mTencent != null) {
            this.mTencent.shareToQzone(this.mActivity, bundle, this.getShareToQZoneListener());
        }

    }

    protected void initResource() {
        this.mKeyWord = "qzone";
        this.mShowWord = ResContainer.getString(this.mActivity, "umeng_socialize_text_qq_zone_key");
        this.mIcon = ResContainer.getResourceId(this.mActivity, ResType.DRAWABLE, "umeng_socialize_qzone_on");
        this.mGrayIcon = ResContainer.getResourceId(this.mActivity, ResType.DRAWABLE, "umeng_socialize_qzone_off");
    }

    protected void handleOnClick(CustomPlatform customPlatform, SocializeEntity entity, SnsPostListener listener) {
        if("deault_id".equals(this.mAppID)) {
            this.showDialog();
        } else {
            this.mSocializeConfig.registerListener(listener);
            mEntity = entity;
            SocializeConfig.setSelectedPlatfrom(SHARE_MEDIA.QZONE);
            if(TextUtils.isEmpty(this.mAppID)) {
                this.mAppID = (String)OauthHelper.getAppIdAndAppkey(this.mActivity).get("appid");
                this.mAppKey = (String)OauthHelper.getAppIdAndAppkey(this.mActivity).get("appkey");
            }

            if(!TextUtils.isEmpty(this.mAppID)) {
                this.shareToQZone();
            } else {
                this.getAppIdFromServer(new ObtainAppIdListener() {
                    public void onComplete() {
                        QZoneSsoHandler.this.shareToQZone();
                    }
                });
            }

        }
    }

    private void showDialog() {
        Builder builder = new Builder(this.mContext);
        TextView title = new TextView(this.mContext);
        title.setText("分享失败原因");
        title.setPadding(0, 20, 0, 20);
        title.setTextColor(-1);
        title.setGravity(17);
        title.setTextSize(16.0F);
        builder.setCustomTitle(title);
        String label = "请添加QZone平台到SDK \n添加方式：\nQZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(getActivity(), \"你的APP ID\",\"你的APP KEY\");\nqZoneSsoHandler.addToSocialSDK();\n参考文档：\nhttp://dev.umeng.com/social/android/share/quick-integration#social_qzone_sso";
        TextView textView = new TextView(this.mContext);
        textView.setText(label);
        textView.setTextColor(-1);
        textView.setTextSize(16.0F);
        textView.setAutoLinkMask(1);
        builder.setView(textView);
        AlertDialog dialog = builder.show();
        dialog.show();
    }

    protected void sendReport(boolean flag) {
        UMShareMsg shareMsg = this.getShareMsg();
        SocializeUtils.sendAnalytic(this.mActivity, mEntity.mDescriptor, shareMsg.mText, shareMsg.getMedia(), "qzone");

        try {
            StatisticsDataUtils.addStatisticsData(this.mActivity, SHARE_MEDIA.QZONE, 25);
        } catch (Exception var4) {
            ;
        }

    }

    private UMAuthListener createAuthListener(final Bundle bundle, final UMImage image) {
        return new UMAuthListener() {
            public void onStart(SHARE_MEDIA platform) {
            }

            public void onError(SocializeException e, SHARE_MEDIA platform) {
            }

            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                if(value != null && value.containsKey("uid")) {
                    String uid = value.getString("uid");
                    QZoneSsoHandler.this.getBitmapUrl(image, uid, new ObtainImageUrlListener() {
                        public void onComplete(String path) {
                            if(!TextUtils.isEmpty(path)) {
                                ArrayList mediaObject = new ArrayList();
                                bundle.remove("imageUrl");
                                mediaObject.add(path);
                                bundle.putStringArrayList("imageUrl", mediaObject);
                                QZoneSsoHandler.this.defaultQZoneShare(bundle);
                            } else {
                                QZoneSsoHandler.this.defaultQZoneShare(bundle);
                                UMediaObject mediaObject1 = QZoneSsoHandler.this.getShareMsg().getMedia();
                                int type = bundle.getInt("req_type");
                                if(!QZoneSsoHandler.this.isClientInstalled() && mediaObject1 != null && (mediaObject1.getMediaType() == MediaType.VEDIO || mediaObject1.getMediaType() == MediaType.MUSIC || type == 1)) {
                                    Log.e(QZoneSsoHandler.TAG, "QQ空间上传图片失败将导致无客户端分享失败，请设置缩略图为url类型或者较小的本地图片...");
                                }
                            }

                        }
                    });
                }

            }

            public void onCancel(SHARE_MEDIA platform) {
            }
        };
    }
}
