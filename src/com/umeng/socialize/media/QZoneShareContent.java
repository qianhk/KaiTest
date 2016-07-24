//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.umeng.socialize.media;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.BaseShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMusic;

public class QZoneShareContent extends BaseShareContent {
    public static final Creator<QZoneShareContent> CREATOR = new Creator() {
        public QZoneShareContent createFromParcel(Parcel in) {
            return new QZoneShareContent(in);
        }

        public QZoneShareContent[] newArray(int size) {
            return new QZoneShareContent[size];
        }
    };

    public QZoneShareContent() {
    }

    public QZoneShareContent(String text) {
        super(text);
    }

    public QZoneShareContent(UMImage image) {
        super(image);
    }

    public QZoneShareContent(UMusic music) {
        super(music);
    }

    public QZoneShareContent(UMVideo video) {
        super(video);
    }

    protected QZoneShareContent(Parcel in) {
        super(in);
    }

    public String toString() {
        return super.toString() + "[QZoneShareMedia]";
    }

    public SHARE_MEDIA getTargetPlatform() {
        return SHARE_MEDIA.QZONE;
    }
}
