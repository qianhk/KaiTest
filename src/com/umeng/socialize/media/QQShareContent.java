//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.umeng.socialize.media;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.BaseShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.net.utils.SocializeNetUtils;
import com.umeng.socialize.utils.Log;

public class QQShareContent extends BaseShareContent {
    public static final Creator<QQShareContent> CREATOR = new Creator() {
        public QQShareContent createFromParcel(Parcel in) {
            return new QQShareContent(in);
        }

        public QQShareContent[] newArray(int size) {
            return new QQShareContent[size];
        }
    };

    public QQShareContent() {
    }

    public QQShareContent(String text) {
        super(text);
    }

    public QQShareContent(UMImage image) {
        super(image);
    }

    public QQShareContent(UMusic music) {
        super(music);
    }

    public QQShareContent(UMVideo video) {
        super(video);
    }

    protected QQShareContent(Parcel in) {
        super(in);
    }

    public void setTargetUrl(String targetUrl) {
        if(!TextUtils.isEmpty(targetUrl) && SocializeNetUtils.startWithHttp(targetUrl)) {
            this.mTargetUrl = targetUrl;
        } else {
            Log.e(this.TAG, "### QQ的targetUrl必须以http://或者https://开头");
        }

    }

    public String toString() {
        return super.toString() + "QQShareContent [mTitle=" + this.mTitle + ", mTargetUrl =" + this.mTargetUrl + "]";
    }

    public SHARE_MEDIA getTargetPlatform() {
        return SHARE_MEDIA.QQ;
    }
}
