package com.njnu.kai.test;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.njnu.kai.test.support.FunctionItem;

import java.util.ArrayList;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-1-27
 */
public class PracticeMainAdapter extends BaseAdapter {

    private ArrayList<FunctionItem> mFuncitonList;
    private Context mContext;
    private int mTextColor = Color.WHITE;

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    public PracticeMainAdapter(Context context, ArrayList<FunctionItem> funcitonList) {
        mContext = context;
        mFuncitonList = funcitonList;
    }

    @Override
    public int getCount() {
        return mFuncitonList.size();
    }

    @Override
    public FunctionItem getItem(int position) {
        return mFuncitonList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_train_entry, null);
            convertView.setTag("kaiTag_" + position);
            convertView.setTag(R.id.btn_add, "keyTag_" + position);
        }
        FunctionItem functionItem = getItem(position);
        TextView text = (TextView)convertView.findViewById(R.id.tv_text);
        text.setTextColor(mTextColor);
        text.setText(functionItem.getName());
        return convertView;
    }
}
