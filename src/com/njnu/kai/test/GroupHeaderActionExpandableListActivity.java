package com.njnu.kai.test;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import com.njnu.kai.test.grid.AZSideBar;
import com.njnu.kai.test.groupheader.StickyGroupHeaderListView;
import com.njnu.kai.test.support.LogUtils;
import com.njnu.kai.test.support.ToastUtils;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-9-28
 */
public class GroupHeaderActionExpandableListActivity extends Activity
        implements AdapterView.OnItemClickListener, StickyGroupHeaderListView.OnHeaderClickListener {

    private static final String LOG_TAG = "GroupHeaderActionExpandableListActivity";

    private GroupHeaderActionBaseTestAdapter mAdapter;
    private StickyGroupHeaderListView mListView;

    private AZSideBar mAZSideBar;
    private Button mBtnOrderWithGroup;
    private Button mBtnOrderNoGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_group_header_action_list);

        mAdapter = new GroupHeaderActionBaseTestAdapter(this);

        mListView = (StickyGroupHeaderListView)findViewById(R.id.list);
        mListView.setOnItemClickListener(this);
        mListView.setOnHeaderClickListener(this);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtils.showToast(GroupHeaderActionExpandableListActivity.this, "onItemLongClick " + position + " clicked!");
                return true;
            }
        });
//        mListView.setOnStickyHeaderChangedListener(this);
//        mListView.setOnStickyHeaderOffsetChangedListener(this);
        mListView.addHeaderView(getLayoutInflater().inflate(R.layout.listview_group_header_list_header, null));
        mListView.addFooterView(getLayoutInflater().inflate(R.layout.listview_group_header_list_footer, null));
        mListView.setEmptyView(findViewById(R.id.empty));
        mListView.setDrawingListUnderStickyHeader(true);
        mListView.setAreHeadersSticky(false);
        mListView.setAdapter(mAdapter, R.id.expandable_toggle_button, R.id.expandable);
        mListView.setDragStartViewId(R.id.view_drag);
        mListView.setDivider(new ColorDrawable(Color.RED));
        mListView.setDividerHeight(10);

        mAZSideBar = (AZSideBar)findViewById(R.id.azsidebar);
        mAZSideBar.setOnLetterChangedListener(new AZSideBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                int row = mAdapter.getPositionForSection(letter.charAt(0) - 'A');
                row = Math.max(row, 0);
                LogUtils.d(LOG_TAG, "onLetterChanged row=%d letter=%s list_headerView=%d", row, letter, mListView.getHeaderViewsCount());
                selectRow(row + mListView.getHeaderViewsCount());
            }
        });
        mBtnOrderWithGroup = (Button)findViewById(R.id.btn_order_with_group);
        mBtnOrderWithGroup.setOnClickListener(mBtnListener);
        mBtnOrderNoGroup = (Button)findViewById(R.id.btn_order_no_group);
        mBtnOrderNoGroup.setOnClickListener(mBtnListener);
    }

    private void orderWithGroup() {
        mAZSideBar.setVisibility(View.VISIBLE);
        mAdapter.invertData(true);
        mListView.collapse();
        mListView.setGroupHeaderEnabled(true);
    }

    private void orderNoGroup() {
        mAZSideBar.setVisibility(View.GONE);
        mAdapter.invertData(false);
        mListView.collapse();
        mListView.setGroupHeaderEnabled(false);
    }

    private View.OnClickListener mBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_order_with_group:
                    orderWithGroup();
                    break;
                case R.id.btn_order_no_group:
                    orderNoGroup();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ToastUtils.showToast(this, "Item " + position + " clicked!");
    }

    @Override
    public void onHeaderClick(StickyGroupHeaderListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
        ToastUtils.showToast(this, "Header " + headerId + " currentlySticky ? " + currentlySticky);
    }

    /**
     * select row
     *
     * @param row row index
     */
    public void selectRow(final int row) {
        mListView.requestFocus();
        mListView.setSelection(row);
    }
}
