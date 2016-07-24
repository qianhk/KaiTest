package com.njnu.kai.test.share;


import com.njnu.kai.test.R;

/**
 * 分享类型
 *
 * @author xinru.leng
 * @version 7.0.0
 */
public enum ShareType {
    /**
     * 初始值
     */
    NONE,
    /**
     * 身边好友
     */
    FRIEND,
    /**
     * 音乐圈
     */
    MUSIC_CYCLE,
    /**
     * 新浪微博
     */
    SINA_WEIBO,
    /**
     * qq微博
     */
    QQ_WEIBO,
    /**
     * QQ空间
     */
    QZONE,
    /**
     * QQ好友
     */
    QQ,
    /**
     * 微信好友
     */
    WECHAT,
    /**
     * 微信朋友圈
     */
    WECHAT_FRIENDS,
    /**
     * 其它
     */
    OTHER;

    /**
     * 返回分享Title 字段
     * @param type ShareType
     * @return Title
     */
    public static int getShareContentDialogTitle(ShareType type) {
        int titleId = R.string.share;
        switch (type) {
            case SINA_WEIBO:
                titleId = R.string.share_to_sina_weibo;
                break;
            case QQ_WEIBO:
                titleId = R.string.share_to_qq_weibo;
                break;
            case QZONE:
                titleId = R.string.share_to_qqzone;
                break;
            default:
                break;
        }
        return titleId;
    }
}
