package com.njnu.kai.test;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.njnu.kai.test.aidl.AIDLTestActivity;
import com.njnu.kai.test.danmaku.DanmakuMainActivity;
import com.njnu.kai.test.delloc.BadTokenActivity;
import com.njnu.kai.test.delloc.DellocActivity;
import com.njnu.kai.test.delloc.DellocBigMemoryActivity;
import com.njnu.kai.test.dynamic.DexLoadTestActivity;
import com.njnu.kai.test.expand.ExpandTestActivity;
import com.njnu.kai.test.expand.ExpandWithAnimation2Activity;
import com.njnu.kai.test.expand.ExpandWithAnimationActivity;
import com.njnu.kai.test.grid.SimpleGridActivity;
import com.njnu.kai.test.groupdrag.GroupDragableListActivity;
import com.njnu.kai.test.indicator.PageIndicatorActivity;
import com.njnu.kai.test.menu.MenuMainActivity;
import com.njnu.kai.test.next.HasNextActivity;
import com.njnu.kai.test.okhttp.OkHttpTestActivity;
import com.njnu.kai.test.owndan.DanmakuActivity;
import com.njnu.kai.test.ratiolayout.RatioFrameActivity;
import com.njnu.kai.test.share.ShareEntryActivity;
import com.njnu.kai.test.support.BaseActivity;
import com.njnu.kai.test.support.FunctionItem;
import com.njnu.kai.test.support.LogUtils;
import com.njnu.kai.test.view.KeepTopImageActivity;
import com.njnu.kai.test.view.SignalAnimationActivity;
import com.njnu.kai.test.view.WaveActivity;
import com.njnu.kai.test.wheel.activity.WheelTestActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;

import java.util.ArrayList;


/**
 * @author kai.qian
 * @version 1.0.0
 */
public class TestMainActivity extends ListActivity {

    private static final String LOG_TAG = "TrainMainActivity";

    private static final ArrayList<FunctionItem> FUNCTIONS = new ArrayList<FunctionItem>();

    static {
        FUNCTIONS.add(new FunctionItem("Htc T328W 4.0.3", HtcT328WActivity.class));
        FUNCTIONS.add(new FunctionItem("DexLoadTest", DexLoadTestActivity.class));
        FUNCTIONS.add(new FunctionItem("OkHttpTest", OkHttpTestActivity.class));
        FUNCTIONS.add(new FunctionItem("QQ_Menu", MenuMainActivity.class));
        FUNCTIONS.add(new FunctionItem("Third Share", ShareEntryActivity.class));
        FUNCTIONS.add(new FunctionItem("Danmaku OwnDraw", DanmakuActivity.class));
        FUNCTIONS.add(new FunctionItem("Danmaku", DanmakuMainActivity.class));
        FUNCTIONS.add(new FunctionItem("Wheel Test", WheelTestActivity.class));
        FUNCTIONS.add(new FunctionItem("AIDL Test", AIDLTestActivity.class));
        FUNCTIONS.add(new FunctionItem("Square View、Favorite Animation", SimpleGridActivity.class));
        FUNCTIONS.add(new FunctionItem("Custom PageIndicator", PageIndicatorActivity.class));
        FUNCTIONS.add(new FunctionItem("GroupDragableList", GroupDragableListActivity.class));
        FUNCTIONS.add(new FunctionItem("DragableList", DragableListActivity.class));
        FUNCTIONS.add(new FunctionItem("RatioFrame", RatioFrameActivity.class));
        FUNCTIONS.add(new FunctionItem("Fibonacci", FibonacciActivity.class));
        FUNCTIONS.add(new FunctionItem("KeepTopImageView", KeepTopImageActivity.class));
        FUNCTIONS.add(new FunctionItem("SignalAnimation", SignalAnimationActivity.class));
        FUNCTIONS.add(new FunctionItem("Xfermodes", XfermodesActivity.class));
        FUNCTIONS.add(new FunctionItem("Wave progress", WaveActivity.class));
        FUNCTIONS.add(new FunctionItem("MatchColor", MatchColorActivity.class));
        FUNCTIONS.add(new FunctionItem("IconText", IconTextActivity.class));
        FUNCTIONS.add(new FunctionItem("StickyGroupHeaderList", StickyGroupHeaderListActivity.class));
        FUNCTIONS.add(new FunctionItem("ActionExpandableList", ActionExpandableListActivity.class));
        FUNCTIONS.add(new FunctionItem("GroupHeaderActionExpandableList", GroupHeaderActionExpandableListActivity.class));
        FUNCTIONS.add(new FunctionItem("Delloc Test", DellocActivity.class));
        FUNCTIONS.add(new FunctionItem("DellocBigMemoryActivity", DellocBigMemoryActivity.class));
        FUNCTIONS.add(new FunctionItem("BadTokenActivity", BadTokenActivity.class));
        FUNCTIONS.add(new FunctionItem("AutoScaleTextActivity", AutoScaleTextActivity.class));
        FUNCTIONS.add(new FunctionItem("WallPaperActivity", WallPaperActivity.class));
        FUNCTIONS.add(new FunctionItem("HasNextActivity", HasNextActivity.class));
        FUNCTIONS.add(new FunctionItem("Expand Content", ExpandTestActivity.class));
        FUNCTIONS.add(new FunctionItem("Expand With Animation", ExpandWithAnimationActivity.class));
        FUNCTIONS.add(new FunctionItem("Expand With Animation2", ExpandWithAnimation2Activity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_train_entry);
        BaseAdapter arrayAdapter = new TestMainAdapter(this, FUNCTIONS);
        setListAdapter(arrayAdapter);

        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, "100424468", "c7394704798a158208a74ab60104f0ba");
        qqSsoHandler.addToSocialSDK();

        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, "100424468",
                "c7394704798a158208a74ab60104f0ba");
        qZoneSsoHandler.addToSocialSDK();

//        ListView listView = getListView();
//        listView.setDivider(new ColorDrawable(Color.CYAN));
//        listView.setDividerHeight(10);
//        listView.setHeaderDividersEnabled(true);
//        listView.setFooterDividersEnabled(true);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        FunctionItem functionItem = FUNCTIONS.get(position);
        LogUtils.d(LOG_TAG, "onListItemClick position=%d name=%s", position, functionItem.getName());
        Intent intent = new Intent(this, functionItem.getActionClass());
        intent.putExtra(BaseActivity.KEY_TITLE, functionItem.getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        moveTaskToBack(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
