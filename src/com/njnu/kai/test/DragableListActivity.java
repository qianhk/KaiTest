package com.njnu.kai.test;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import com.njnu.kai.test.support.LogUtils;
import com.njnu.kai.test.support.ToastUtils;
import com.njnu.kai.test.draglistview.DraggableListView;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-9-28
 */
public class DragableListActivity extends Activity
        implements AdapterView.OnItemClickListener {

    private static final String LOG_TAG = "DragableListActivity";

    private DraggableListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListView = new DraggableListView(this, null);
        setContentView(mListView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mListView.setDrawSelectorOnTop(true);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtils.showToast(DragableListActivity.this, "onItemLongClick " + position + " clicked!");
                return true;
            }
        });
        mListView.addHeaderView(getLayoutInflater().inflate(R.layout.listview_group_header_list_header, null));
        mListView.addFooterView(getLayoutInflater().inflate(R.layout.listview_group_header_list_footer, null));
        mListView.setAdapter(buildDummyData());
        mListView.setDivider(new ColorDrawable(Color.RED));
        mListView.setDividerHeight(10);
        mListView.setDragStartViewId(R.id.drag_handle);
        mListView.setDragListener(new DraggableListView.DragListener() {
            @Override
            public void drag(int from, int to) {
                LogUtils.i(LOG_TAG, "DragListener from=%d to=%d", from, to);
            }
        });
//        mListView.setDropListener(new DraggableListView.DropListener() {
//            @Override
//            public void drop(int from, int to) {
//
//            }
//        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ToastUtils.showToast(this, "Item " + position + " clicked!");
    }

    public ListAdapter buildDummyData() {
        final int SIZE = 20;
        String[] values = new String[SIZE];
        for (int i = 0; i < SIZE; i++) {
            values[i] = "Song " + i;
        }
        return new ArrayAdapter<String>(
                this,
                R.layout.item_dragable_list,
                R.id.title_view,
                values
        );
    }
}
