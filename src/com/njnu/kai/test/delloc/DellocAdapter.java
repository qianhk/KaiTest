package com.njnu.kai.test.delloc;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-12-29
 */
public class DellocAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<String> mList;

    public DellocAdapter(Context context) {
        mContext = context;
    }

    public void refreshData(ArrayList<String> arrayList) {
        mList = arrayList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public String getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            TextView textView = new TextView(mContext);
            textView.setTextColor(Color.BLACK);
            convertView = textView;
        }
        ((TextView)convertView).setText(getItem(position));
        return convertView;
    }
}
