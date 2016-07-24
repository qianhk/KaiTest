//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.umeng.socialize.sso;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.socialize.bean.CustomPlatform;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.ShareType;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.UMShareMsg;
import com.umeng.socialize.common.ResContainer;
import com.umeng.socialize.common.UMAsyncTask;
import com.umeng.socialize.common.ResContainer.ResType;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.sso.UMTencentSsoHandler;
import com.umeng.socialize.sso.UMTencentSsoHandler.ObtainAppIdListener;
import com.umeng.socialize.sso.UMTencentSsoHandler.ObtainImageUrlListener;
import com.umeng.socialize.utils.BitmapUtils;
import com.umeng.socialize.utils.OauthHelper;
import com.umeng.socialize.utils.SocializeUtils;
import com.umeng.socialize.utils.StatisticsDataUtils;
import com.umeng.socialize.view.ShareActivity;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class UMQQSsoHandler extends UMTencentSsoHandler {
    private boolean GOTO_SHARE_ACTIVITY = false;
    private static final String TAG = "UMQQSsoHandler";
    private int mShareType = 1;
    private Bundle mParams;

    public UMQQSsoHandler(Activity activity, String appId, String appKey) {
        super(activity, appId, appKey);
    }

    protected void initResource() {
        this.mKeyWord = "qq";
        this.mShowWord = ResContainer.getString(this.mActivity, "umeng_socialize_text_qq_key");
        this.mIcon = ResContainer.getResourceId(this.mActivity, ResType.DRAWABLE, "umeng_socialize_qq_on");
        this.mGrayIcon = ResContainer.getResourceId(this.mActivity, ResType.DRAWABLE, "umeng_socialize_qq_off");
    }

    protected void handleOnClick(CustomPlatform customPlatform, SocializeEntity entity, SnsPostListener listener) {
        if("deault_id".equals(this.mAppID)) {
            this.showDialog();
        } else {
            this.mSocializeConfig.registerListener(listener);
            this.isShareAfterAuthorize = true;
            SocializeConfig.setSelectedPlatfrom(SHARE_MEDIA.QQ);
            this.mShareType = 1;
            if(entity != null) {
                mEntity = entity;
                UMShareMsg authData = mEntity.getShareMsg();
                if(authData != null && mEntity.getShareType() == ShareType.SHAKE) {
                    this.mShareContent = authData.mText;
                    this.mShareMedia = authData.getMedia();
                } else {
                    this.mShareContent = entity.getShareContent();
                    this.mShareMedia = entity.getMedia();
                }
            }

            this.setShareContent();
            String[] authData1 = OauthHelper.getAccessTokenForQQ(this.mActivity);
            ObtainAppIdListener obtainAppIdListener = new ObtainAppIdListener() {
                public void onComplete() {
                    if(UMQQSsoHandler.this.initTencent()) {
                        UMQQSsoHandler.this.gotoShare();
                    }
                }
            };
            if(authData1 != null) {
                if(TextUtils.isEmpty(this.mAppID)) {
                    this.mAppID = (String)OauthHelper.getAppIdAndAppkey(this.mActivity).get("appid");
                    this.mAppKey = (String)OauthHelper.getAppIdAndAppkey(this.mActivity).get("appkey");
                }

                if(!TextUtils.isEmpty(this.mAppID)) {
                    this.mTencent = Tencent.createInstance(this.mAppID, this.mActivity);
                    this.mTencent.setOpenId(authData1[1]);
                    this.mTencent.setAccessToken(authData1[0], authData1[2]);
                    this.gotoShare();
                } else {
                    this.getAppIdFromServer(obtainAppIdListener);
                }
            } else if(!TextUtils.isEmpty(this.mAppID)) {
                if(!this.initTencent()) {
                    return;
                }

                this.gotoShare();
            } else {
                this.getAppIdFromServer(obtainAppIdListener);
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
        String label = "请添加QQ平台到SDK \n添加方式：\nUMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(), \"你的APP ID\",\"你的APP KEY\");\nqqSsoHandler.addToSocialSDK(); \n参考文档：\nhttp://dev.umeng.com/social/android/share/quick-integration#social_qq_sso";
        TextView textView = new TextView(this.mContext);
        textView.setText(label);
        textView.setTextColor(-1);
        textView.setTextSize(16.0F);
        textView.setAutoLinkMask(1);
        builder.setView(textView);
        AlertDialog dialog = builder.show();
        dialog.show();
    }

    public boolean shareTo() {
        this.shareToQQ();
        return true;
    }

    public void authorize(Activity act, UMAuthListener listener) {
        this.mAuthListener = listener;
        this.setActivity(act);
        if(TextUtils.isEmpty(this.mAppID)) {
            this.mAppID = (String)OauthHelper.getAppIdAndAppkey(this.mActivity).get("appid");
            this.mAppKey = (String)OauthHelper.getAppIdAndAppkey(this.mActivity).get("appkey");
        }

        if(TextUtils.isEmpty(this.mAppID)) {
            ObtainAppIdListener obtainAppIdListener = new ObtainAppIdListener() {
                public void onComplete() {
                    UMQQSsoHandler.this.loginDeal();
                }
            };
            this.getAppIdFromServer(obtainAppIdListener);
        } else {
            this.loginDeal();
        }

    }

    private void loginDeal() {
        if(this.validTencent()) {
            this.mTencent.logout(this.mActivity);
        } else if((this.mTencent == null || TextUtils.isEmpty(this.mTencent.getAppId())) && !this.initTencent()) {
            return;
        }

        if(mEntity != null) {
            mEntity.addStatisticsData(this.mActivity, SHARE_MEDIA.QQ, 3);
        }

        Log.i("UMQQSsoHandler", "QQ oauth login...");
        this.createDialog("");
        SocializeUtils.safeShowDialog(this.mProgressDialog);
        this.mTencent.login(this.mActivity, sQQScope, new IUiListener() {
            public void onError(UiError e) {
                if(e != null) {
                    Log.d("UMQQSsoHandler", "授权失败! ==> errorCode = " + e.errorCode + ", errorMsg = " + e.errorMessage + ", detail = " + e.errorDetail);
                }

                SocializeUtils.safeCloseDialog(UMQQSsoHandler.this.mProgressDialog);
                UMQQSsoHandler.this.mAuthListener.onError(new SocializeException(e.errorCode, e.errorDetail), SHARE_MEDIA.QQ);
                if(UMQQSsoHandler.mEntity != null) {
                    UMQQSsoHandler.mEntity.addOauthData(UMQQSsoHandler.this.mActivity, SHARE_MEDIA.QQ, 0);
                }

            }

            public void onCancel() {
                com.umeng.socialize.utils.Log.d("UMQQSsoHandler", "cancel");
                SocializeUtils.safeCloseDialog(UMQQSsoHandler.this.mProgressDialog);
                UMQQSsoHandler.this.mAuthListener.onCancel(SHARE_MEDIA.QQ);
                if(UMQQSsoHandler.mEntity != null) {
                    UMQQSsoHandler.mEntity.addOauthData(UMQQSsoHandler.this.mActivity, SHARE_MEDIA.QQ, 0);
                }

            }

            public void onComplete(Object response) {
                SocializeUtils.safeCloseDialog(UMQQSsoHandler.this.mProgressDialog);
                Bundle values = UMQQSsoHandler.this.parseOauthData(response);
                if(values != null) {
                    int status = values.getInt("ret");
                    if(status == 0) {
                        if(UMQQSsoHandler.mEntity != null) {
                            UMQQSsoHandler.mEntity.addOauthData(UMQQSsoHandler.this.mActivity, SHARE_MEDIA.QQ, 1);
                        }

                        UMQQSsoHandler.this.uploadToken(UMQQSsoHandler.this.mActivity, response, UMQQSsoHandler.this.mAuthListener);
                        return;
                    }
                }

                if(UMQQSsoHandler.mEntity != null) {
                    UMQQSsoHandler.mEntity.addOauthData(UMQQSsoHandler.this.mActivity, SHARE_MEDIA.QQ, 0);
                }

                UMQQSsoHandler.this.mAuthListener.onComplete((Bundle)null, SHARE_MEDIA.QQ);
            }
        });
    }

    private void setShareContent() {
        if(this.mShareMedia instanceof QQShareContent) {
            QQShareContent qqShareContent = (QQShareContent)this.mShareMedia;
            this.mShareContent = qqShareContent.getShareContent();
            this.mTargetUrl = qqShareContent.getTargetUrl();
            this.mTitle = qqShareContent.getTitle();
            this.mShareMedia = qqShareContent.getShareMedia();
        }

    }

    public void canOpenShareActivity(boolean val) {
        this.GOTO_SHARE_ACTIVITY = val;
    }

    private void gotoShare() {
        if(this.GOTO_SHARE_ACTIVITY) {
            SocializeUtils.safeCloseDialog(this.mProgressDialog);
            Intent intent = new Intent(this.mActivity, ShareActivity.class);
            intent.putExtra("QQ-SSO", true);
            intent.putExtra("sns", SHARE_MEDIA.QQ.toString());
            if(mEntity != null && !TextUtils.isEmpty(mEntity.mEntityKey)) {
                intent.putExtra("dc", mEntity.mEntityKey);
            }

            this.mActivity.startActivity(intent);
        } else {
            this.shareTo();
        }

    }

    public void shareToQQ() {
        if(this.validTencent()) {
            this.mSocializeConfig.fireAllListenersOnStart(SnsPostListener.class);
            String path = (String)this.mExtraData.get("image_path_local");
            if(this.isLoadImageAsync()) {
                String image1 = (String)this.mExtraData.get("image_path_url");
                this.loadImage(image1);
                return;
            }

            if(this.isUploadImageAsync(path, this.mShareType)) {
                UMImage image = new UMImage(this.mActivity, new File(path));
                Log.w("UMQQSsoHandler", "未安装QQ客户端的情况下，QQ不支持音频，图文是为本地图片的分享。此时将上传本地图片到相册，请确保在QQ互联申请了upload_pic权限.");
                UMAuthListener authListener = this.createUploadAuthListener(image);
                this.authorize(this.mActivity, authListener);
                return;
            }

            this.defaultShareToQQ();
        } else {
            Log.d("UMQQSsoHandler", "QQ平台还没有授权");
            this.createAuthListener();
            this.authorize(this.mActivity, this.mAuthListener);
        }

    }

    private void defaultShareToQQ() {
        SocializeUtils.safeCloseDialog(this.mProgressDialog);
        this.buildParams();
        com.umeng.socialize.utils.Log.d("UMQQSsoHandler", "invoke Tencent.shareToQQ method...");
        this.mTencent.shareToQQ(this.mActivity, this.mParams, new IUiListener() {
            public void onError(UiError e) {
                Log.e("UMQQSsoHandler", "分享失败! ==> errorCode = " + e.errorCode + ", errorMsg = " + e.errorMessage + ", detail = " + e.errorDetail);
                UMQQSsoHandler.this.mSocializeConfig.fireAllListenersOnComplete(SnsPostListener.class, SHARE_MEDIA.QQ, '鱂', UMQQSsoHandler.mEntity);
            }

            public void onCancel() {
                UMQQSsoHandler.this.mSocializeConfig.fireAllListenersOnComplete(SnsPostListener.class, SHARE_MEDIA.QQ, '鱀', UMQQSsoHandler.mEntity);
            }

            public void onComplete(Object response) {
                char status = '鱂';
                int code = UMQQSsoHandler.this.getResponseCode(response);
                if(code == 0) {
                    status = 200;
                }

                UMQQSsoHandler.this.mSocializeConfig.fireAllListenersOnComplete(SnsPostListener.class, SHARE_MEDIA.QQ, status, UMQQSsoHandler.mEntity);
                UMQQSsoHandler.this.sendReport(true);
            }
        });
        this.mParams = null;
        mEntity.setShareType(ShareType.NORMAL);
    }

    private boolean isLoadImageAsync() {
        String urlPath = (String)this.mExtraData.get("image_path_url");
        String localPath = (String)this.mExtraData.get("image_path_local");
        return this.mShareType == 5 && this.isClientInstalled() && !TextUtils.isEmpty(urlPath) && TextUtils.isEmpty(localPath);
    }

    protected void sendReport(boolean result) {
        SocializeUtils.sendAnalytic(this.mActivity, mEntity.mDescriptor, this.mShareContent, this.mShareMedia, "qq");

        try {
            StatisticsDataUtils.addStatisticsData(this.mActivity, SHARE_MEDIA.QQ, 16);
        } catch (Exception var3) {
            ;
        }

    }

    public void shareToQQ(String summary) {
        this.mShareContent = summary;
        this.shareTo();
    }

    private void buildParams() {
        this.mParams = new Bundle();
        this.mParams.putString("summary", this.mShareContent);
        if(this.mShareMedia instanceof UMImage && TextUtils.isEmpty(this.mShareContent)) {
            this.mShareType = 5;
            this.buildImageParams(this.mParams);
        } else if(!(this.mShareMedia instanceof UMusic) && !(this.mShareMedia instanceof UMVideo)) {
            this.buildTextImageParams(this.mParams);
        } else {
            this.mShareType = 2;
            this.buildAudioParams(this.mParams);
        }

        this.mParams.putInt("req_type", this.mShareType);
        if(TextUtils.isEmpty(this.mTitle)) {
            this.mTitle = "分享到QQ";
        }

        if(TextUtils.isEmpty(this.mTargetUrl)) {
            this.mTargetUrl = "http://www.umeng.com/social";
        }

        this.mParams.putString("targetUrl", this.mTargetUrl);
        this.mParams.putString("title", this.mTitle);
        this.mParams.putString("appName", this.getAppName());
    }

    private void buildImageParams(Bundle bundle) {
        this.parseImage(this.mShareMedia);
        String path = (String)this.mExtraData.get("image_path_local");
        String urlPath = (String)this.mExtraData.get("image_path_url");
        if(!TextUtils.isEmpty(path) && BitmapUtils.isFileExist(path)) {
            bundle.putString("imageLocalUrl", path);
        } else if(!TextUtils.isEmpty(urlPath)) {
            bundle.putString("imageUrl", urlPath);
        }

        if(!this.isClientInstalled()) {
            com.umeng.socialize.utils.Log.w("UMQQSsoHandler", "QQ不支持无客户端情况下纯图片分享...");
        }

    }

    private void buildTextImageParams(Bundle bundle) {
        this.buildImageParams(bundle);
    }

    private void buildAudioParams(Bundle bundle) {
        if(this.mShareMedia instanceof UMusic) {
            this.parseMusic(this.mShareMedia);
        } else if(this.mShareMedia instanceof UMVideo) {
            this.parseVideo(this.mShareMedia);
        }

        String path = (String)this.mExtraData.get("image_path_local");
        String urlPath = (String)this.mExtraData.get("image_path_url");
        if(!TextUtils.isEmpty(path) && BitmapUtils.isFileExist(path)) {
            bundle.putString("imageLocalUrl", path);
        } else if(!TextUtils.isEmpty(urlPath)) {
            bundle.putString("imageUrl", urlPath);
        }

        bundle.putString("audio_url", this.mShareMedia.toUrl());
    }

    public int getRequstCode() {
        return 5658;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void createAuthListener() {
        this.mAuthListener = new UMAuthListener() {
            public void onStart(SHARE_MEDIA platform) {
            }

            public void onError(SocializeException e, SHARE_MEDIA platform) {
            }

            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                if(!TextUtils.isEmpty(value.getString("uid"))) {
                    UMQQSsoHandler.this.gotoShare();
                } else {
                    Toast.makeText(UMQQSsoHandler.this.mActivity, "授权失败", 0).show();
                }

            }

            public void onCancel(SHARE_MEDIA platform) {
            }
        };
    }

    private void loadImage(final String imageUrlPath) {
        (new UMAsyncTask<Void>() {
            protected void onPreExecute() {
                super.onPreExecute();
                UMQQSsoHandler.this.createDialog("");
                SocializeUtils.safeShowDialog(UMQQSsoHandler.this.mProgressDialog);
            }

            protected Void doInBackground() {
                BitmapUtils.getBitmapFromFile(imageUrlPath);
                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                SocializeUtils.safeCloseDialog(UMQQSsoHandler.this.mProgressDialog);
                String localPath = BitmapUtils.getFileName(imageUrlPath);
                UMQQSsoHandler.this.mParams.putString("imageLocalUrl", localPath);
                UMQQSsoHandler.this.mParams.remove("imageUrl");
                UMQQSsoHandler.this.defaultShareToQQ();
            }
        }).execute();
    }

    private UMAuthListener createUploadAuthListener(final UMImage image) {
        return new UMAuthListener() {
            public void onStart(SHARE_MEDIA platform) {
            }

            public void onError(SocializeException e, SHARE_MEDIA platform) {
            }

            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                if(value != null && value.containsKey("uid")) {
                    String uid = value.getString("uid");
                    UMQQSsoHandler.this.getBitmapUrl(image, uid, new ObtainImageUrlListener() {
                        public void onComplete(String path) {
                            UMQQSsoHandler.this.mParams.putString("imageUrl", path);
                            UMQQSsoHandler.this.mParams.remove("imageLocalUrl");
                            UMQQSsoHandler.this.defaultShareToQQ();
                        }
                    });
                }

            }

            public void onCancel(SHARE_MEDIA platform) {
            }
        };
    }

    private class ReAuthListener implements IUiListener {
        public UMDataListener listener;
        @Override
        public void onComplete(Object o) {

        }

        @Override
        public void onError(UiError uiError) {

        }

        @Override
        public void onCancel() {

        }
    }

    private ReAuthListener mReAuthListener = new ReAuthListener();

    public void getUserInfo(final UMDataListener listener) {
        if(this.mTencent == null) {
            listener.onComplete(-101, (Map)null);
        } else {
            UserInfo userInfo = new UserInfo(this.mActivity, this.mTencent.getQQToken());
            userInfo.getUserInfo(new IUiListener() {
                public void onCancel() {
                    listener.onComplete('鱀', (Map)null);
                }

                public void onComplete(Object arg) {
                    if(arg == null) {
                        listener.onComplete('鱂', (Map)null);
                    } else {
                        try {
//                            if (arg instanceof JSONObject) {
//                                JSONObject jsonObject = (JSONObject)arg;
//                                int ret = jsonObject.getInt("ret");
//                                if (ret == 100030) {
//                                    mReAuthListener.listener = listener;
//                                    mTencent.reAuth((Activity)mContext, sQQScope, mReAuthListener);
//                                    return;
//                                }
//                            }
                            JSONObject e = new JSONObject(arg.toString());
                            HashMap infos = new HashMap();
                            infos.put("screen_name", e.optString("nickname"));
                            infos.put("gender", e.optString("gender"));
                            infos.put("profile_image_url", e.optString("figureurl_qq_2"));
                            infos.put("is_yellow_year_vip", e.optString("is_yellow_year_vip"));
                            infos.put("yellow_vip_level", e.optString("yellow_vip_level"));
                            infos.put("msg", e.optString("msg"));
                            infos.put("city", e.optString("city"));
                            infos.put("vip", e.optString("vip"));
                            infos.put("level", e.optString("level"));
                            infos.put("province", e.optString("province"));
                            infos.put("is_yellow_vip", e.optString("is_yellow_vip"));
                            listener.onComplete(200, infos);
                        } catch (JSONException var4) {
                            listener.onComplete('鱂', (Map)null);
                        }

                    }
                }

                public void onError(UiError arg0) {
                    listener.onComplete('鱂', (Map)null);
                }
            });
        }
    }
}
