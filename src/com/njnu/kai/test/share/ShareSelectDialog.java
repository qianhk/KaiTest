package com.njnu.kai.test.share;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.njnu.kai.test.R;
import com.njnu.kai.test.share.ShareSelectAdapter.ShareItem;
import com.njnu.kai.test.support.EnvironmentUtils;
import com.njnu.kai.test.support.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
public class ShareSelectDialog extends Dialog {
    private static final String TAG = "ShareSelectDialog";
    private static final int DEFAULT_COUNT = 8;
    private static final int MSG_AUTH_SUCCESS = 0;
    private static final int MSG_AUTH_FAILED = MSG_AUTH_SUCCESS + 1;
    private static final int MSG_NO_NETWORK = MSG_AUTH_FAILED + 1;
    private static final int MSG_WECHAT_NOT_INSTALLED = MSG_NO_NETWORK + 1;
    private static final int MSG_WECHAT_NO_SUPPORT_SEND_FRIEND = MSG_WECHAT_NOT_INSTALLED + 1;

    private GridView mShareSelectGridView;
    private ShareSelectAdapter mAdapter;
    private List<ShareSelectAdapter.ShareItem> mShareItemList;
    private Activity mActivity;
    private String mCacheFolderPath;
    private ShareType mShareType = ShareType.NONE;
    private ShareSelectListener mShareSelectListener;

    private int mType;

    private static Map<ShareType, Integer> mAuthorityFailureMsg = new ArrayMap<ShareType, Integer>();
    private static Map<ShareType, Integer> mShareSuccessMsg = new ArrayMap<ShareType, Integer>();
    private static Map<ShareType, Integer> mShareFailureMsg = new ArrayMap<ShareType, Integer>();

    public static final int TYPE_SHARE = 0;
    public static final int TYPE_DELETE_OAUTH = 1;
    public static final int TYPE_DO_OAUTH = 2;
    public static final int TYPE_PlatformInfo = 3;
    public static final int TYPE_getFriends = 4;

    static {
        mAuthorityFailureMsg.put(ShareType.SINA_WEIBO, R.string.sina_weibo_accredit_fail);
        mAuthorityFailureMsg.put(ShareType.QQ_WEIBO, R.string.qq_weibo_accredit_fail);
        mAuthorityFailureMsg.put(ShareType.QZONE, R.string.qzone_accredit_fail);

        mShareSuccessMsg.put(ShareType.SINA_WEIBO, R.string.sina_weibo_share_success);
        mShareSuccessMsg.put(ShareType.QQ_WEIBO, R.string.qq_weibo_share_success);
        mShareSuccessMsg.put(ShareType.QZONE, R.string.qzone_share_success);


        mShareFailureMsg.put(ShareType.SINA_WEIBO, R.string.sina_weibo_share_fail);
        mShareFailureMsg.put(ShareType.QQ_WEIBO, R.string.qq_weibo_share_fail);
        mShareFailureMsg.put(ShareType.QZONE, R.string.qzone_share_fail);
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUTH_SUCCESS:
                    Bundle bundle = (Bundle)msg.obj;
                    break;
                case MSG_AUTH_FAILED:
                    ToastUtils.showToast(mActivity, mAuthorityFailureMsg.get(mShareType));
                    break;
                case MSG_NO_NETWORK:
                    ToastUtils.showToast(mActivity, R.string.share_no_network);
                    break;
                case MSG_WECHAT_NOT_INSTALLED:
                    ToastUtils.showToast(mActivity, R.string.no_wechat);
                    dismiss();
                    break;
                case MSG_WECHAT_NO_SUPPORT_SEND_FRIEND:
                    ToastUtils.showToast(mActivity, R.string.wechat_not_support_friend);
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * @param activity Activity
     */
    public ShareSelectDialog(Activity activity, ShareSelectListener listener, int type) {
        super(activity);
        if (activity == null || activity.isFinishing()) {
            return;
        }
        mType = type;
        setTitle("选择平台");
        mActivity = activity;
        mShareSelectListener = listener;
        setContentView(onCreateBodyView(mActivity));
        createShareSelectItemList();
        mAdapter.setShareItemList(mShareItemList);
        mAdapter.notifyDataSetChanged();

    }

    protected View onCreateBodyView(Context context) {
        View view = View.inflate(context, R.layout.dialog_share_select, null);
        mAdapter = new ShareSelectAdapter(getContext());
        mShareSelectGridView = (GridView)view.findViewById(R.id.gridview_share_select);
        mShareSelectGridView.setAdapter(mAdapter);
        mShareSelectGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                processOnItemClick(position);
            }
        });
        return view;
    }

    private void processCallback(int what, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        mHandler.sendMessage(msg);
    }

    private void processOnItemClick(int position) {
        mShareType = mShareItemList.get(position).getShareType();


        if (mShareType == ShareType.OTHER) {
//            String shareBody = mShareInfo.isThirdPartyShare() ? mShareInfo.getMessage() : ShareContentUtil.getShareBody(mShareInfo, mShareType);
//            final String message = shareBody + " " + getContext().getString(R.string.share_text_tail_info);
//            mShareInfo.setMessage(message);
//            if (mShareAction != null) {
//                mShareAction.doShareBegin(mShareType, mShareInfo);
//            }
//            mBaseApi.share(this, mShareInfo, mCallback);
//            dismiss();
//            return;
        }

        if (mShareType == ShareType.FRIEND) {
//            mShareSelectGridView.postDelayed(new ApShareRunnable(mActivity, mShareInfo.getMediaId()), ConstantUtils.THOUSAND >> 1);
            ToastUtils.showToast(mActivity, "暂不支持");
            dismiss();
            return;
        }

        if (!EnvironmentUtils.Network.isNetWorkAvailable()) {
            mHandler.sendEmptyMessage(MSG_NO_NETWORK);
            return;
        }

        if (mShareSelectListener != null) {
            if (mType == TYPE_SHARE) {
                mShareSelectListener.onShareSelect(mShareType);
            } else if (mType == TYPE_DELETE_OAUTH) {
                mShareSelectListener.onDeleteOauth(mShareType);
            } else if (mType == TYPE_DO_OAUTH) {
                mShareSelectListener.onDoOauth(mShareType);
            } else if (mType == TYPE_PlatformInfo) {
                mShareSelectListener.onPlatformInfo(mShareType);
            } else if (mType == TYPE_getFriends) {
                mShareSelectListener.onGetFriends(mShareType);
            }
        }
        dismiss();
    }

    private void createShareSelectItemList() {
        mShareItemList = new ArrayList<ShareSelectAdapter.ShareItem>(DEFAULT_COUNT);
//        if (mShareInfo.isLocal()) {
//            mShareItemList.add(new ShareSelectAdapter.ShareItem(R.string.share_friend, R.drawable.icon_share_sns_friend, ShareType.FRIEND));
//        }
        mShareItemList.add(new ShareItem(R.string.qq, R.drawable.icon_share_sns_qq, ShareType.QQ));
        mShareItemList.add(new ShareItem(R.string.qq_zone, R.drawable.icon_share_sns_qzone, ShareType.QZONE));
        mShareItemList.add(new ShareItem(R.string.wechat, R.drawable.icon_share_sns_weixin, ShareType.WECHAT));
        mShareItemList.add(new ShareItem(R.string.wechat_friend, R.drawable.icon_share_sns_weixinfriend, ShareType.WECHAT_FRIENDS));
        mShareItemList.add(new ShareItem(R.string.sina_weibo, R.drawable.icon_share_sns_sina, ShareType.SINA_WEIBO));
//        mShareItemList.add(new ShareItem(R.string.qq_weibo, R.drawable.icon_share_sns_tencent, ShareType.QQ_WEIBO));
        if (mType == TYPE_SHARE) {
            mShareItemList.add(new ShareItem(R.string.other, R.drawable.icon_share_sns_other, ShareType.OTHER));
        }
    }

    public interface ShareSelectListener {
        public void onShareSelect(ShareType shareType);
        public void onDeleteOauth(ShareType shareType);
        public void onDoOauth(ShareType shareType);
        public void onPlatformInfo(ShareType shareType);
        public void onGetFriends(ShareType shareType);
    }
}
