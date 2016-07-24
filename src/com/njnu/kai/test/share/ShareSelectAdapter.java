package com.njnu.kai.test.share;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.BaseApplication;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class ShareSelectAdapter extends BaseAdapter {

    private List<ShareItem> mShareItems = new ArrayList<ShareItem>();
    private Context mContext;

    /**
     *
     * @param context Context
     */
    public ShareSelectAdapter(Context context) {
        if (context == null) {
            context = BaseApplication.getApp();
        }
        this.mContext = context;
    }

    /**
     *
     * @param shareItemList List
     */
    public void setShareItemList(List<ShareItem> shareItemList) {
        this.mShareItems = shareItemList;
    }

    /**
     *
     * @return List<ShareItem>
     */
    public List<ShareItem> getShareItemList() {
        return mShareItems;
    }

    @Override
    public int getCount() {
        return mShareItems == null ? 0 : mShareItems.size();
    }

    @Override
    public ShareItem getItem(int position) {
        return mShareItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.share_select_item, parent, false);
        }
        TextView textView = (TextView) convertView;
        ShareItem item = getItem(position);
        textView.setText(item.mTitleId);
        textView.setCompoundDrawablesWithIntrinsicBounds(0, item.mIconId, 0, 0);
        return convertView;
    }

    /**
     * 分享Item
     */
    public static class ShareItem {
        private int mTitleId;
        private int mIconId;
        private ShareType mShareType;

        /**
         * ShareItem
         * @param titleId title id
         * @param iconId icon id
         * @param shareType share type
         */
        public ShareItem(int titleId, int iconId, ShareType shareType) {
            this.mTitleId = titleId;
            this.mIconId = iconId;
            this.mShareType = shareType;
        }

        /**
         * Title
         * @return Title
         */
        public int getTitleId() {
            return mTitleId;
        }

        /**
         * Icon
         * @return Icon
         */
        public int getIconId() {
            return mIconId;
        }

        /**
         * ShareType
         * @return ShareType
         */
        public ShareType getShareType() {
            return mShareType;
        }
    }
}
