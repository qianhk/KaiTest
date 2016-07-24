package com.njnu.kai.test.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.*;
import com.umeng.socialize.bean.*;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.*;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.utils.OauthHelper;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import java.util.List;
import java.util.Map;

/**
 * 感慨，与其用友盟的框架，还不如直接用这么几个第三方提供的api，反正小平台不用就是了。友盟用相似、相同的接口表示类似的含义，提供的方法虽多但各平台起作用的方法不一致，一堆坑
 *
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-1-28
 */
public class ShareEntryActivity extends BaseActivity implements ShareSelectDialog.ShareSelectListener {

    private static final String TAG = "ShareEntryActivity";

    private UMSocialService mController;

    private TextView mTvResult;

    private UMImage mUmImage;
    private UMusic mUmMusic;
    private UMVideo mUmVideo;
    private String mTitleText = "凯测试第三方分享";
    private String mContentText = "凯测试第三方分享功能，http://www.weibo.com/qianhk";
    private String mTargetUrl = "http://www.weibo.com/qianhk";

    private long mShareId;

    public void onApiButtonClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_api_get_platform_key) {
            new ShareSelectDialog(this, this, ShareSelectDialog.TYPE_PlatformInfo).show();
        } else if (viewId == R.id.btn_check_token_expired) {
            //android.os.NetworkOnMainThreadException
            SHARE_MEDIA[] medias = {SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA};
            mController.checkTokenExpired(this, medias, new SocializeListeners.UMDataListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onComplete(int code, Map<String, Object> map) {
                    mTvResult.setText("getPlatformInfo code=" + code + "\n" + map);
                }
            });
        } else if (viewId == R.id.btn_get_user_info) {
            getUserInfo();
        } else if (viewId == R.id.btn_getFriends) {
            new ShareSelectDialog(this, this, ShareSelectDialog.TYPE_getFriends).show();
        }
    }

    @Override
    public void onGetFriends(ShareType shareType) {
        mController.getFriends(this, new SocializeListeners.FetchFriendsListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(int status,
                                   List<UMFriend> friends) {
                StringBuilder sb = new StringBuilder();
                if (status == 200 && friends != null) {
                    for (UMFriend friend : friends) {
                        sb.append(friend.getName() + "   ");
                    }
                } else {
                    sb.append("status_code=" + status);
                }
                mTvResult.setText(sb.toString());
            }
        }, shareTypeToMedia(shareType));
    }

    private void getUserInfo() {
        mController.getUserInfo(this, new SocializeListeners.FetchUserListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(int code, SocializeUser socializeUser) {
                String str = "";
                if (socializeUser != null) {
                    str = "defaultPlatform = "
                            + (socializeUser.mDefaultPlatform != null ? socializeUser.mDefaultPlatform.name() : "null")
                            + " account=" + socializeUser.mLoginAccount + "\n";
                    if (socializeUser.mAccounts != null) {
                        for (SnsAccount account : socializeUser.mAccounts) {
                            str += "account" + account + "\n";
                        }
                    }
                }
                mTvResult.setText("code=" + code + "\n user=" + str);
            }
        });
    }

    @Override
    public void onPlatformInfo(ShareType shareType) {
        mController.getPlatformInfo(this, shareTypeToMedia(shareType), new SocializeListeners.UMDataListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(int code, Map<String, Object> map) {
                mTvResult.setText("getPlatformInfo code=" + code + "\n" + map);
            }
        });
    }

    public void onUserButtonClick(View view) {
        int viewId = view.getId();
        Activity activity = ShareEntryActivity.this;
        if (viewId == R.id.btn_user_flush_oauth) {
            flushOauthStatus();
        } else if (viewId == R.id.btn_user_do_oauth) {
            ShareSelectDialog dialog = new ShareSelectDialog(this, this, ShareSelectDialog.TYPE_DO_OAUTH);
            dialog.show();
        }
    }

    @Override
    public void onDoOauth(ShareType shareType) {
        mController.doOauthVerify(ShareEntryActivity.this, shareTypeToMedia(shareType), new SocializeListeners.UMAuthListener() {
            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                ToastUtils.showToast(ShareEntryActivity.this, platform.name() + " 授权错误, 线程id=" + Thread.currentThread().getId()
                + " errCode=" + e.getErrorCode() + " msg=" + e.getMessage());
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                if (value != null && !StringUtils.isEmpty(value.getString("uid"))) {
                    ToastUtils.showToast(ShareEntryActivity.this, platform.name() + " 授权成功, 线程id=" + Thread.currentThread().getId()
                    + " bundle=" + value);
                    LogUtils.d(TAG, "doOauthVerify complete bundle=%s", value.toString());
                    mTvResult.setText(value.toString());
                } else {
                    ToastUtils.showToast(ShareEntryActivity.this, platform.name() + " 授权失败，线程id=" + Thread.currentThread().getId());
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                ToastUtils.showToast(ShareEntryActivity.this, platform.name() + " 授权取消, 线程id=" + Thread.currentThread().getId());
            }

            @Override
            public void onStart(SHARE_MEDIA platform) {
                ToastUtils.showToast(ShareEntryActivity.this, platform.name() + " 授权开始, 线程id=" + Thread.currentThread().getId());
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_entry);
        mTvResult = (TextView)findViewById(R.id.tv_result);

        mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        mController.getConfig().closeToast();
        Log.LOG = EnvironmentUtils.Config.isLogEnable();

        mController.getConfig().addFollow(SHARE_MEDIA.SINA, "1485085381");

        SinaSsoHandler sinaSsoHandler = new SinaSsoHandler();
        sinaSsoHandler.addToSocialSDK();

        UMWXHandler wxHandler = new UMWXHandler(this, ShareConstant.WX_APP_ID, ShareConstant.WX_APP_SECRET);
        wxHandler.showCompressToast(false);
        wxHandler.addToSocialSDK();
        UMWXHandler wxCircleHandler = new UMWXHandler(this, ShareConstant.WX_APP_ID, ShareConstant.WX_APP_SECRET);
        wxCircleHandler.showCompressToast(false);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();

        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, ShareConstant.QQ_APP_ID, ShareConstant.QQ_APP_KEY);
        qqSsoHandler.addToSocialSDK();

        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, ShareConstant.QQ_APP_ID, ShareConstant.QQ_APP_KEY);
        qZoneSsoHandler.addToSocialSDK();

        mUmImage = new UMImage(this, R.drawable.img_guide_background);

        mUmMusic = new UMusic("http://nie.dfe.yymommy.com/m4a_96_0/af/f2/af89331cceb6ac99da0df4cd4adff0f2.m4a?k=ae0e9241a591ea5f&t=1422921493");
        mUmMusic.setAuthor("梁静茹");
        mUmMusic.setTitle("燕尾蝶");
        mUmMusic.setTargetUrl("http://music.163.com/song/254270/");
        mUmMusic.setThumb(new UMImage(this, "http://tp2.sinaimg.cn/1485085381/180/5613134409/1")); //不要用直接写string的方法，qq分享不从string中取值

        mUmVideo = new UMVideo("http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html");
        mUmVideo.setThumb(new UMImage(this, "http://static.cnbetacdn.com/topics/nokiapure.gif"));
        mUmVideo.setTitle("视频分享Title");
        //视频的target url不起作用，可能因为没有哪家客户端支持直接再分享里看视频，需要直接用构造参数里的url。
        //QQ、QZone有作用，如果controller里没有setTargetUrl则用这边的
        mUmVideo.setTargetUrl("http://www.dongting.com");

        flushOauthStatus();
    }

    private void flushOauthStatus() {
        StringBuilder builder = new StringBuilder();
        builder.append("QQ:" + OauthHelper.isAuthenticated(this, SHARE_MEDIA.QQ) + " " + OauthHelper.isAuthenticatedAndTokenNotExpired(this, SHARE_MEDIA.QQ));
        builder.append("\nQZone:" + OauthHelper.isAuthenticated(this, SHARE_MEDIA.QZONE) + " " + OauthHelper.isAuthenticatedAndTokenNotExpired(this, SHARE_MEDIA.QZONE));
        builder.append("\nWeiXin:" + OauthHelper.isAuthenticated(this, SHARE_MEDIA.WEIXIN) + " " + OauthHelper.isAuthenticatedAndTokenNotExpired(this, SHARE_MEDIA.WEIXIN));
        builder.append("\nCircle:" + OauthHelper.isAuthenticated(this, SHARE_MEDIA.WEIXIN_CIRCLE) + " " + OauthHelper.isAuthenticatedAndTokenNotExpired(this, SHARE_MEDIA.WEIXIN_CIRCLE));
        builder.append("\nSina:" + OauthHelper.isAuthenticated(this, SHARE_MEDIA.SINA) + " " + OauthHelper.isAuthenticatedAndTokenNotExpired(this, SHARE_MEDIA.SINA));
        mTvResult.setText(builder);
    }

    private void updateUMSocialService(String id) {
    }

    private void prepareWeiXinShareContent() {
        WeiXinShareContent shareContent = new WeiXinShareContent();
        if (mShareId == R.id.btn_share_text) {
            updateUMSocialService("com.njnu.kai.train.text");
            shareContent.setShareContent("纯文本:" + mContentText);
        } else if (mShareId == R.id.btn_share_pic) {
            updateUMSocialService("com.njnu.kai.train.pic");
            shareContent.setShareImage(mUmImage);
        } else if (mShareId == R.id.btn_share_text_pic) {
            updateUMSocialService("com.njnu.kai.train.text_pic");
            shareContent.setShareContent("图文:" + mContentText);
            shareContent.setShareImage(mUmImage);
            shareContent.setTargetUrl(mTargetUrl);
        } else if (mShareId == R.id.btn_share_song) {
            updateUMSocialService("com.njnu.kai.train.song");
            shareContent.setShareContent("梁静茹");
            shareContent.setShareMedia(mUmMusic);
        } else if (mShareId == R.id.btn_share_video) {
            updateUMSocialService("com.njnu.kai.train.video");
            shareContent.setShareContent("视频作者");
            shareContent.setShareMedia(mUmVideo);
        }
        mController.setShareMedia(shareContent);
    }

    private void prepareCircleShareContent() {
        CircleShareContent shareContent = new CircleShareContent();
        if (mShareId == R.id.btn_share_text) {
            updateUMSocialService("com.njnu.kai.train.text");
            shareContent.setShareContent("纯文本:" + mContentText);
        } else if (mShareId == R.id.btn_share_pic) {
            updateUMSocialService("com.njnu.kai.train.pic");
            shareContent.setShareImage(mUmImage);
        } else if (mShareId == R.id.btn_share_text_pic) {
            updateUMSocialService("com.njnu.kai.train.text_pic");
            shareContent.setShareImage(mUmImage);
            shareContent.setTargetUrl("http://www.chaohaohe.com");
            shareContent.setTitle("图文分享测试标题");
            shareContent.setShareContent(mContentText); //虽然此处的context不会显示出来，但如果没有内容，会变成仅图片的形式。
        } else if (mShareId == R.id.btn_share_song) {
            updateUMSocialService("com.njnu.kai.train.song");
            shareContent.setShareContent("梁静茹_");
            shareContent.setShareMedia(mUmMusic);
        } else if (mShareId == R.id.btn_share_video) {
            updateUMSocialService("com.njnu.kai.train.video");
            shareContent.setShareMedia(mUmVideo);
        }
        mController.setShareMedia(shareContent);
    }

    private void prepareSinaShareContent() {
        SinaShareContent shareContent = new SinaShareContent();
        if (mShareId == R.id.btn_share_text) {
            updateUMSocialService("com.njnu.kai.train.text");
            shareContent.setShareContent("纯文本:" + mContentText);
        } else if (mShareId == R.id.btn_share_pic) {
            updateUMSocialService("com.njnu.kai.train.pic");
            shareContent.setShareImage(mUmImage);
        } else if (mShareId == R.id.btn_share_text_pic) {
            updateUMSocialService("com.njnu.kai.train.text_pic");
            shareContent.setShareContent("图文:" + mContentText);
            shareContent.setShareImage(mUmImage);
            shareContent.setTargetUrl(mTargetUrl);
        } else if (mShareId == R.id.btn_share_song) {
            updateUMSocialService("com.njnu.kai.train.song");
            shareContent.setShareContent("梁静茹");
            shareContent.setShareMedia(mUmMusic);
        } else if (mShareId == R.id.btn_share_video) {
            updateUMSocialService("com.njnu.kai.train.video");
            shareContent.setShareContent("视频作者");
            shareContent.setShareMedia(mUmVideo);
//            shareContent.setTargetUrl(mTargetUrl);  //视频的target url似乎不起作用，再这儿也不行
        }
        mController.setShareMedia(shareContent);
    }

    private void prepareQQShareContent() {
        QQShareContent shareContent = new QQShareContent();
        if (mShareId == R.id.btn_share_text) {
            updateUMSocialService("com.njnu.kai.train.text");
            shareContent.setTitle(mTitleText); //做不到纯文字，会自动附加上一次的图，除非上次还没用过带图的分享
            shareContent.setShareContent("纯文本:" + mContentText);
            shareContent.setTargetUrl(mTargetUrl);
        } else if (mShareId == R.id.btn_share_pic) {
            updateUMSocialService("com.njnu.kai.train.pic");
            shareContent.setShareImage(mUmImage);
        } else if (mShareId == R.id.btn_share_text_pic) {
            updateUMSocialService("com.njnu.kai.train.text_pic");
            shareContent.setShareContent("图文:" + mContentText);
            shareContent.setShareImage(mUmImage);
            shareContent.setTitle(mTitleText);
            shareContent.setTargetUrl(mTargetUrl);
        } else if (mShareId == R.id.btn_share_song) {
            updateUMSocialService("com.njnu.kai.train.song");
            shareContent.setShareContent("梁静茹_");
            shareContent.setShareMedia(mUmMusic);
        } else if (mShareId == R.id.btn_share_video) {
            updateUMSocialService("com.njnu.kai.train.video");
            shareContent.setShareContent("视频作者");
            shareContent.setShareMedia(mUmVideo);
//            shareContent.setTargetUrl("http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html"); //可不用，用mUmVideo里的
        }
        mController.setShareMedia(shareContent);
    }

    private void prepareQZoneShareContent() {
        QZoneShareContent shareContent = new QZoneShareContent(); //其实QZone分享本质上是个图片、标题、内容的分享
        if (mShareId == R.id.btn_share_text) {
            updateUMSocialService("com.njnu.kai.train.text");
            shareContent.setTitle(mTitleText);
            shareContent.setShareContent("纯文本:" + mContentText);
            shareContent.setTargetUrl(mTargetUrl);
        } else if (mShareId == R.id.btn_share_pic) {
            updateUMSocialService("com.njnu.kai.train.pic"); //QZone貌似不支持纯图片分享，得有shareContent，有title也不行
            shareContent.setShareImage(mUmImage);
            shareContent.setShareContent("带上图片");
            shareContent.setTargetUrl(mTargetUrl);
        } else if (mShareId == R.id.btn_share_text_pic) {
            updateUMSocialService("com.njnu.kai.train.text_pic");
            shareContent.setShareContent("图文:" + mContentText);
            shareContent.setShareImage(mUmImage);
            shareContent.setTitle(mTitleText);
            shareContent.setTargetUrl(mTargetUrl);
        } else if (mShareId == R.id.btn_share_song) {
            updateUMSocialService("com.njnu.kai.train.song");
            shareContent.setShareContent("梁静茹");
            shareContent.setShareMedia(mUmMusic);
            shareContent.setTargetUrl("http://music.163.com/song/254270/");
        } else if (mShareId == R.id.btn_share_video) {
            updateUMSocialService("com.njnu.kai.train.video");
            shareContent.setShareContent("视频作者");
            shareContent.setShareMedia(mUmVideo);
            shareContent.setTargetUrl("http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html"); //得有，否则乱入
        }
        mController.setShareMedia(shareContent);
    }

    public void onShareButtonClick(View view) {
        mShareId = view.getId();
        ShareSelectDialog dialog = new ShareSelectDialog(this, this
                , mShareId != R.id.btn_delete_auth ? ShareSelectDialog.TYPE_SHARE : ShareSelectDialog.TYPE_DELETE_OAUTH);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**使用SSO授权必须添加如下代码 */
        if (mController != null) {
            UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
            if (ssoHandler != null) {
                ssoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onShareSelect(ShareType shareType) {
//        ToastUtils.showToast(this, "ShareType= " + shareType.name());
        SHARE_MEDIA shareMedia = null;
        switch (shareType) {
            case SINA_WEIBO:
                shareMedia = SHARE_MEDIA.SINA;
                prepareSinaShareContent();
                break;

            case QQ_WEIBO:
                shareMedia = SHARE_MEDIA.TENCENT;
                break;

            case QZONE:
                shareMedia = SHARE_MEDIA.QZONE;
                prepareQZoneShareContent();
                break;

            case QQ:
                shareMedia = SHARE_MEDIA.QQ;
                prepareQQShareContent();
                break;

            case WECHAT:
                shareMedia = SHARE_MEDIA.WEIXIN;
                prepareWeiXinShareContent();
                break;

            case WECHAT_FRIENDS:
                shareMedia = SHARE_MEDIA.WEIXIN_CIRCLE;
                prepareCircleShareContent();
                break;
        }
        if (shareMedia == null) {
            OtherShareApi shareApi = new OtherShareApi(this);
            shareApi.startShareActivity(ShareEntryActivity.this, "image/*", "凯凯-掌柜 测试第三方分享功能Other", null, "掌柜测试分享");
        } else {
            mController.postShare(ShareEntryActivity.this, shareMedia, new SocializeListeners.SnsPostListener() {
                @Override
                public void onStart() {
                    ShareEntryActivity activity = ShareEntryActivity.this;
                    ToastUtils.showToast(activity, "开始分享，线程id=" + Thread.currentThread().getId());
                }

                @Override
                public void onComplete(SHARE_MEDIA share_media, int code, SocializeEntity socializeEntity) {
                    ShareEntryActivity activity = ShareEntryActivity.this;
                    String message = "分享成功";
                    if (code != 200) {
                        message = "分享失败[" + code + "]";
                        if (code == -101) {
                            message += " 没有授权";
                        }
                    }
                    message += "线程id=" + Thread.currentThread().getId();
                    ToastUtils.showToast(activity, message);
                }
            });
        }
    }

    @Override
    public void onDeleteOauth(ShareType shareType) {
        final SHARE_MEDIA shareMedia = shareTypeToMedia(shareType);
        mController.deleteOauth(this, shareMedia, new SocializeListeners.SocializeClientListener() {
            @Override
            public void onStart() {
                ToastUtils.showToast(ShareEntryActivity.this, "delete oauth start " + shareMedia.name());
            }

            @Override
            public void onComplete(int status, SocializeEntity socializeEntity) {
                String message = "delete oauth complete [";
                if (status == 200) {
                    message += "success";
                } else {
                    message += ("failed=" + status);
                }
                ToastUtils.showToast(ShareEntryActivity.this, message + "] " + shareMedia.name());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e(TAG, "onDestroy");
    }

    private SHARE_MEDIA shareTypeToMedia(ShareType shareType) {
        SHARE_MEDIA shareMedia = null;
        switch (shareType) {
            case SINA_WEIBO:
                shareMedia = SHARE_MEDIA.SINA;
                break;

            case QQ_WEIBO:
                shareMedia = SHARE_MEDIA.TENCENT;
                break;

            case QZONE:
                shareMedia = SHARE_MEDIA.QZONE;
                break;

            case QQ:
                shareMedia = SHARE_MEDIA.QQ;
                break;

            case WECHAT:
                shareMedia = SHARE_MEDIA.WEIXIN;
                break;

            case WECHAT_FRIENDS:
                shareMedia = SHARE_MEDIA.WEIXIN_CIRCLE;
                break;
        }
        return shareMedia;
    }

}
