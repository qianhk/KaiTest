package com.njnu.kai.test.groupdrag;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.DisplayUtils;
import com.njnu.kai.test.support.ToastUtils;
import com.njnu.kai.test.draglistview.DraggableExpandableListView;

import java.util.ArrayList;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-9-28
 */
public class GroupDragableListActivity extends Activity {

    private static final String LOG_TAG = "GroupDragableListActivity";

    private DraggableExpandableListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListView = new DraggableExpandableListView(this, null);
        setContentView(mListView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mListView.setDrawSelectorOnTop(true);
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return false;
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtils.showToast(GroupDragableListActivity.this, "onItemLongClick " + position + " clicked!");
                return true;
            }
        });
        mListView.addHeaderView(getLayoutInflater().inflate(R.layout.listview_group_header_list_header, null));
        mListView.addFooterView(getLayoutInflater().inflate(R.layout.listview_group_header_list_footer, null));
        mListView.setAdapter(buildDummyData());
        mListView.setDivider(new ColorDrawable(Color.BLUE));
        mListView.setDividerHeight(DisplayUtils.dp2px(10));
        mListView.setDragStartViewId(R.id.drag_handle);
//        mListView.setDragListener(new DraggableExpandableListView.DragListener() {
//            @Override
//            public void drag(int fromGroup, int from, int toGroup, int to) {
//                LogUtils.i(LOG_TAG, "DragListener from=%d_%d  to=%d_%d", fromGroup, from, toGroup, to);
//            }
//        });
//        mListView.setDropListener(new DraggableExpandableListView.DropListener() {
//            @Override
//            public void drop(int fromGroup, int from, int toGroup, int to) {
//                LogUtils.i(LOG_TAG, "DropListener from=%d_%d  to=%d_%d", fromGroup, from, toGroup, to);
//            }
//        });
    }

    public GroupAdapter buildDummyData() {
        final ArrayList<ArrayList<Data>> dataList = new ArrayList<>();
        for (int i = 0; i < 20; ++i) {
            final ArrayList<Data> childDataList = new ArrayList<>();
            for (int j = 0; j < i; ++j) {
                childDataList.add(new Data(i * 100 + j, String.format("text_%d %d", i, j)));
            }
            dataList.add(childDataList);
        }
        final GroupAdapter groupAdapter = new GroupAdapter();
        groupAdapter.mDataList = dataList;
        return groupAdapter;
    }

    public static class Data {
        private long mId;
        private String mText;

        public Data(long id, String text) {
            mId = id;
            mText = text;
        }
    }

    public static class GroupAdapter extends BaseExpandableListAdapter {

        private ArrayList<ArrayList<Data>> mDataList;

        @Override
        public int getGroupCount() {
            return mDataList != null ? mDataList.size() : 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return getGroup(groupPosition).size();
        }

        @Override
        public ArrayList<Data> getGroup(int groupPosition) {
            return mDataList.get(groupPosition);
        }

        @Override
        public Data getChild(int groupPosition, int childPosition) {
            return getGroup(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return getChild(groupPosition, childPosition).mId;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                final TextView textView = new TextView(parent.getContext());
                textView.setPadding(DisplayUtils.dp2px(48), DisplayUtils.dp2px(8), DisplayUtils.dp2px(12), DisplayUtils.dp2px(8));
                convertView = textView;
            }
            TextView tv = (TextView) convertView;
            tv.setText("group_" + groupPosition);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dragable_list, parent, false);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.title_view);
            final Data childData = getChild(groupPosition, childPosition);
            tv.setText(String.format("id:%d text=%s", childData.mId, childData.mText));
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
