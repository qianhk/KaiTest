package com.njnu.kai.test.delloc;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.njnu.kai.test.*;
import com.njnu.kai.test.support.FunctionItem;
import com.njnu.kai.test.support.ToastUtils;

import java.util.ArrayList;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-11-27
 */
public abstract class AbsMediaListFragment extends BaseFragment {

    public ListView mListView;
    private PracticeMainAdapter mAdapter;
    private int mTest = 8;
    private static long sStatic = 88;
    private static final long FINAL_NUMBER = 888;
    private boolean mLoaded;
    private char mChar;

    private final OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ToastUtils.showToast(getActivity(), mAdapter.getItem(position - mListView.getHeaderViewsCount()).toString());
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_medialist, null);
        mListView = (ListView)mainView.findViewById(R.id.list);
        View headerView = onCreateHeaderView(inflater);
        if (headerView != null) {
            mListView.addHeaderView(headerView, null, false);
        }
        View footerView = onCreateFooterView(inflater);
        if (footerView != null) {
            mListView.addFooterView(footerView, null, false);
        }
        ArrayList<FunctionItem> dataList = new ArrayList<FunctionItem>();
        for (int idx = 1; idx <= 26; ++idx) {
            dataList.add(new FunctionItem("Song Title_" + idx, Object.class));
        }

        mAdapter = new PracticeMainAdapter(inflater.getContext(), dataList);
        mAdapter.setTextColor(Color.BLACK);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mOnItemClickListener);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = mAdapter.getItem(position - mListView.getHeaderViewsCount());
                ToastUtils.showToast(getActivity(), "long " + item.toString());
                return false;
            }
        });
        mLoaded = true;
        mChar = 'K';
        return mainView;
    }

    abstract protected View onCreateHeaderView(LayoutInflater inflater);
    abstract protected View onCreateFooterView(LayoutInflater inflater);
}
