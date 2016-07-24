//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.umeng.socialize.sso;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import com.njnu.kai.test.support.LogUtils;
import com.tencent.tauth.Tencent;
import com.umeng.socialize.bean.CustomPlatform;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SNSPair;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.UMToken;
import com.umeng.socialize.common.ResContainer;
import com.umeng.socialize.common.UMAsyncTask;
import com.umeng.socialize.common.ResContainer.ResType;
import com.umeng.socialize.controller.impl.InitializeController;
import com.umeng.socialize.controller.listener.SocializeListeners.OnSnsPlatformClickListener;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMediaObject;
import com.umeng.socialize.utils.DeviceConfig;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.utils.OauthHelper;
import com.umeng.socialize.utils.SocializeUtils;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class UMTencentSsoHandler extends UMSsoHandler {
    private static final String TAG = UMTencentSsoHandler.class.getName();
    protected SocializeConfig mSocializeConfig = SocializeConfig.getSocializeConfig();
    protected ProgressDialog mProgressDialog = null;
    protected Activity mActivity = null;
    protected String mAppID;
    protected String mAppKey;
    protected Tencent mTencent;
    protected UMAuthListener mAuthListener;
    private static final String PUBLIC_ACCOUNT = "100424468";
    protected static Map<String, String> mImageCache = new HashMap();
    protected String mImageUrl = null;
    protected String mShowWord;
    protected String mKeyWord;
    protected int mIcon;
    protected int mGrayIcon;

    //http://wiki.connect.qq.com/api%E5%88%97%E8%A1%A8
    //QQ_SCOPE ="get_user_info,add_t" '授权项 例如：QQ_SCOPE=get_user_info,list_album,upload_pic,do_like,add_t
    //所有权限用“all”
    protected static String sQQScope = "all";

    public UMTencentSsoHandler(Activity activity, String appId, String appKey) {
        super(activity);
        if(activity == null) {
            Log.e(TAG, "传入的activity为null，请传递一个非空Activity对象");
        } else {
            if(TextUtils.isEmpty(appKey)) {
                Log.e(TAG, "传递的APP KEY无效，请传一个有效的APP KEY");
            }

            if(TextUtils.isEmpty(appId)) {
                Log.e(TAG, "传递的APP ID无效，请传一个有效的APP ID");
            }

            this.mActivity = activity;
            this.mAppID = appId;
            this.mAppKey = appKey;
            this.saveAppIDAndAppKey();
            this.mExtraData.put("qzone_id", appId);
            this.mExtraData.put("qzone_secret", appKey);
        }
    }

    private void saveAppIDAndAppKey() {
        if(!TextUtils.isEmpty(this.mAppID) && !TextUtils.isEmpty(this.mAppKey)) {
            OauthHelper.saveAppidAndAppkey(this.mActivity, this.mAppID, this.mAppKey);
        }

    }

    protected CustomPlatform createNewPlatform() {
        this.initResource();
        this.mCustomPlatform = new CustomPlatform(this.mKeyWord, this.mShowWord, this.mIcon);
        this.mCustomPlatform.mGrayIcon = this.mGrayIcon;
        this.mCustomPlatform.mClickListener = new OnSnsPlatformClickListener() {
            public void onClick(Context context, SocializeEntity entity, SnsPostListener listener) {
                UMTencentSsoHandler.this.mContext = context;
                UMTencentSsoHandler.this.handleOnClick(UMTencentSsoHandler.this.mCustomPlatform, entity, listener);
            }
        };
        return this.mCustomPlatform;
    }

    protected abstract void initResource();

    public void setAppId(String appid) {
        if(TextUtils.isEmpty(appid)) {
            Log.w(TAG, "your appid is null...");
        } else {
            this.mAppID = appid;
        }
    }

    protected void createDialog(String message) {
        if(this.mProgressDialog != null && this.mProgressDialog.isShowing()) {
            Activity platform = this.mProgressDialog.getOwnerActivity();
            if(platform == this.mActivity) {
                return;
            }
        }

        this.mProgressDialog = new ProgressDialog(this.mActivity, ResContainer.getResourceId(this.mActivity, ResType.STYLE, "Theme.UMDialog"));
        SHARE_MEDIA platform1 = SocializeConfig.getSelectedPlatfrom();
        String showPlatformWord = "umeng_socialize_text_waitting_qq";
        if(platform1 == SHARE_MEDIA.QZONE) {
            showPlatformWord = "umeng_socialize_text_waitting_qzone";
        }

        if(TextUtils.isEmpty(message)) {
            message = this.mActivity.getString(ResContainer.getResourceId(this.mActivity, ResType.STRING, showPlatformWord));
        }

        this.mProgressDialog.setOwnerActivity(this.mActivity);
        this.mProgressDialog.setMessage(message);
    }

    protected void getAppIdFromServer(UMTencentSsoHandler.ObtainAppIdListener listener) {
        String message = "获取AppID中...";
        this.createDialog(message);
        SocializeUtils.safeShowDialog(this.mProgressDialog);
        this.getPlatformKey(listener);
    }

    private void getPlatformKey(final UMTencentSsoHandler.ObtainAppIdListener listener) {
        if(!DeviceConfig.isNetworkAvailable(this.mActivity)) {
            Toast.makeText(this.mActivity, "您的网络不可用,请检查网络连接...", Toast.LENGTH_SHORT).show();
            SocializeUtils.safeCloseDialog(this.mProgressDialog);
        } else {
            LogUtils.e(TAG, "getPlatformKey kao， gao sha");
            throw new Error("getPlatformKey kao， gao sha");
//            (new UMAsyncTask() {
//                protected GetPlatformKeyResponse doInBackground() {
//                    GetPlatformKeyResponse response = (new BaseController(new SocializeEntity("com.umeng.qq.sso", RequestType.SOCIAL))).getPlatformKeys(UMTencentSsoHandler.this.mActivity);
//                    return response;
//                }
//
//                protected void onPostExecute(GetPlatformKeyResponse response) {
//                    super.onPostExecute(response);
//                    SocializeUtils.safeCloseDialog(UMTencentSsoHandler.this.mProgressDialog);
//                    if(response != null && response.mData != null) {
//                        UMTencentSsoHandler.this.mAppID = (String)response.mData.get("qzone");
//                        if(response.mSecrets != null) {
//                            UMTencentSsoHandler.this.mAppKey = (String)response.mSecrets.get("qzone");
//                        }
//
//                        SocializeUtils.savePlatformKey(UMTencentSsoHandler.this.mActivity, response.mData);
//                        OauthHelper.saveAppidAndAppkey(UMTencentSsoHandler.this.mActivity, UMTencentSsoHandler.this.mAppID, UMTencentSsoHandler.this.mAppKey);
//                        if(listener != null) {
//                            listener.onComplete();
//                        }
//
//                    } else {
//                        Log.e(UMTencentSsoHandler.TAG, "obtain appId failed,public account share...");
//                        UMTencentSsoHandler.this.mAppID = "100424468";
//                        listener.onComplete();
//                    }
//                }
//            }).execute();
        }
    }

    protected Bundle parseOauthData(Object response) {
        Bundle bundle = new Bundle();
        if(response == null) {
            return bundle;
        } else {
            String jsonStr = response.toString().trim();
            if(TextUtils.isEmpty(jsonStr)) {
                return bundle;
            } else {
                JSONObject json = null;

                try {
                    json = new JSONObject(jsonStr);
                } catch (JSONException var6) {
                    var6.printStackTrace();
                }

                if(json == null) {
                    return bundle;
                } else {
                    bundle.putString("auth_time", json.optString("auth_time", ""));
                    bundle.putString("pay_token", json.optString("pay_token", ""));
                    bundle.putString("pf", json.optString("pf", ""));
                    bundle.putInt("ret", json.optInt("ret", -1));
                    bundle.putString("sendinstall", json.optString("sendinstall", ""));
                    bundle.putString("page_type", json.optString("page_type", ""));
                    bundle.putString("appid", json.optString("appid", ""));
                    bundle.putString("openid", json.optString("openid", ""));
                    bundle.putString("uid", json.optString("openid", ""));
                    String expiresStr = json.optString("expires_in", "");
                    bundle.putString("expires_in", expiresStr);
                    bundle.putString("pfkey", json.optString("pfkey", ""));
                    bundle.putString("access_token", json.optString("access_token", ""));
                    return bundle;
                }
            }
        }
    }

    protected UMToken buildUmToken(Object response) {
        Bundle bundle = this.parseOauthData(response);
        if(bundle == null) {
            return null;
        } else {
            String token = bundle.getString("access_token");
            String openid = bundle.getString("openid");
            String expire_in = bundle.getString("expires_in");
            SHARE_MEDIA platform = SocializeConfig.getSelectedPlatfrom();
            UMToken mToken = UMToken.buildToken(new SNSPair(platform.toString(), openid), token, openid);
            mToken.setAppKey(this.mAppKey);
            mToken.setAppId(this.mAppID);
            mToken.setExpireIn(expire_in);
            return mToken;
        }
    }

    protected boolean initTencent() {
        Log.d("", "#### qzone app id  = " + this.mAppID);
        this.mTencent = Tencent.createInstance(this.mAppID, this.mActivity);
        if(this.mTencent == null) {
            Log.e(TAG, "Tencent变量初始化失败，请检查你的app id跟AndroidManifest.xml文件中AuthActivity的scheme是否填写正确");
            return false;
        } else {
            return true;
        }
    }

    protected boolean validTencent() {
        return this.mTencent != null && this.mTencent.getAppId().equals(this.mAppID);
    }

    protected void uploadToken(final Context context, Object response, final UMAuthListener listener) {
        final Bundle value = this.parseOauthData(response);
        final UMToken token = this.buildUmToken(response);
        if(token != null) {
            if(!DeviceConfig.isNetworkAvailable(this.mActivity)) {
                Toast.makeText(context, "您的网络不可用,请检查网络连接...", Toast.LENGTH_SHORT).show();
            }

            (new UMAsyncTask<Integer>() {
                protected void onPreExecute() {
                    super.onPreExecute();
                    if(listener != null) {
                        listener.onStart(SocializeConfig.getSelectedPlatfrom());
                    }

                }

                protected Integer doInBackground() {
                    InitializeController controller = new InitializeController(new SocializeEntity("qq", RequestType.SOCIAL));
                    return Integer.valueOf(controller.uploadPlatformToken(context, token));
                }

                protected void onPostExecute(Integer result) {
                    super.onPostExecute(result);
                    if(200 != result.intValue()) {
                        Log.d(UMTencentSsoHandler.TAG, "##### Token 授权失败");
                    } else {
                        Log.d(UMTencentSsoHandler.TAG, "##### Token 授权成功");
                        String mtk = token.getToken();
                        SHARE_MEDIA platform = SHARE_MEDIA.convertToEmun(token.mPaltform);
                        if(platform != null && !TextUtils.isEmpty(mtk)) {
                            OauthHelper.saveAccessToken(context, platform, mtk, "null");
                            OauthHelper.setUsid(context, platform, token.mUsid);
                        }
                    }

                    if(listener != null) {
                        listener.onComplete(value, SocializeConfig.getSelectedPlatfrom());
                    }

                    Log.d(UMTencentSsoHandler.TAG, "RESULT : CODE = " + result);
                }
            }).execute();
        }
    }

    public int getResponseCode(Object response) {
        if(response == null) {
            return -1;
        } else {
            String jsonStr = response.toString().trim();
            if(TextUtils.isEmpty(jsonStr)) {
                return -1;
            } else {
                JSONObject json = null;

                try {
                    json = new JSONObject(jsonStr);
                } catch (JSONException var5) {
                    var5.printStackTrace();
                }

                return json == null?-1:(json.has("ret")?json.optInt("ret"):-1);
            }
        }
    }

    public void setActivity(Activity activity) {
        if(this.mActivity == null || this.mActivity.isFinishing()) {
            this.mActivity = activity;
        }

    }

    public void authorizeCallBack(int requestCode, int resultCode, Intent data) {
    }

    public boolean isClientInstalled() {
        return DeviceConfig.isAppInstalled("com.tencent.mobileqq", this.mActivity);
    }

    protected String getAppName() {
        String appName = "";
        if(TextUtils.isEmpty(SocializeEntity.mAppName) && this.mActivity != null) {
            CharSequence sequence = this.mActivity.getApplicationInfo().loadLabel(this.mActivity.getPackageManager());
            if(!TextUtils.isEmpty(sequence)) {
                appName = sequence.toString();
                SocializeEntity.mAppName = appName;
            }
        } else {
            appName = SocializeEntity.mAppName;
        }

        return appName;
    }

    public void getBitmapUrl(final UMediaObject uMediaObjects, final String usid, final UMTencentSsoHandler.ObtainImageUrlListener listener) {
        final InitializeController controller = new InitializeController(new SocializeEntity("com.umeng.share.uploadImage", RequestType.SOCIAL));
        final long startTime = System.currentTimeMillis();
        (new UMAsyncTask<String>() {
            protected void onPreExecute() {
                super.onPreExecute();
                UMTencentSsoHandler.this.createDialog("");
                SocializeUtils.safeShowDialog(UMTencentSsoHandler.this.mProgressDialog);
            }

            protected String doInBackground() {
                UMImage image = null;
                if(uMediaObjects instanceof UMImage) {
                    image = (UMImage)uMediaObjects;
                }

                if(!image.isSerialized()) {
                    image.waitImageToSerialize();
                }

                if(image != null) {
                    String imageLocalPath = image.getImageCachePath();
                    String imageCachePath = (String)UMTencentSsoHandler.mImageCache.get(imageLocalPath);
                    if(!TextUtils.isEmpty(imageCachePath)) {
                        UMTencentSsoHandler.this.mImageUrl = imageCachePath;
                        Log.i(UMTencentSsoHandler.TAG, "obtain image url form cache..." + UMTencentSsoHandler.this.mImageUrl);
                    } else {
                        Log.i(UMTencentSsoHandler.TAG, "obtain image url form server...");
                        String imageUrl = controller.uploadImage(UMTencentSsoHandler.this.mActivity, image, usid);
                        UMTencentSsoHandler.this.setImageUrl(imageLocalPath, imageUrl);
                        if(UMTencentSsoHandler.this.mActivity != null && TextUtils.isEmpty(imageUrl)) {
                            Toast.makeText(UMTencentSsoHandler.this.mActivity, "上传图片失败", Toast.LENGTH_SHORT).show();
                        }

                        Log.i(UMTencentSsoHandler.TAG, "obtain image url form server..." + UMTencentSsoHandler.this.mImageUrl);
                    }
                }

                Log.i(UMTencentSsoHandler.TAG, "doInBackground end...");
                return "";
            }

            protected void onPostExecute(String imageUrl) {
                super.onPostExecute(imageUrl);
                Log.i(UMTencentSsoHandler.TAG, "upload image kill time: " + (System.currentTimeMillis() - startTime));
                SocializeUtils.safeCloseDialog(UMTencentSsoHandler.this.mProgressDialog);
                listener.onComplete(UMTencentSsoHandler.this.mImageUrl);
            }
        }).execute();
    }

    private void setImageUrl(String localPath, String urlPath) {
        if(!TextUtils.isEmpty(urlPath)) {
            mImageCache.put(localPath, urlPath);
            this.mImageUrl = urlPath;
        }

    }

    protected boolean isUploadImageAsync(String imagePath, int type) {
        if(TextUtils.isEmpty(imagePath)) {
            return false;
        } else {
            SHARE_MEDIA platform = SocializeConfig.getSelectedPlatfrom();
            boolean hasClient = this.isClientInstalled();
            boolean isLocalImage = !imagePath.startsWith("http://") && !imagePath.startsWith("https://");
            if(!hasClient && isLocalImage) {
                if(platform == SHARE_MEDIA.QQ && (type == 2 || type == 1)) {
                    return true;
                }

                if(platform == SHARE_MEDIA.QZONE && (type == 1 || type == 2)) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean shareTo() {
        return true;
    }

    public void cleanQQCache() {
    }

    protected interface ObtainAppIdListener {
        void onComplete();
    }

    public interface ObtainImageUrlListener {
        void onComplete(String var1);
    }
}
