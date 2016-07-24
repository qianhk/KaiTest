package com.njnu.kai.test;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.njnu.kai.test.groupheader.StickyGroupHeaderListView;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-9-28
 */
public class StickyGroupHeaderListActivity extends Activity implements
        AdapterView.OnItemClickListener, StickyGroupHeaderListView.OnHeaderClickListener,
        StickyGroupHeaderListView.OnStickyHeaderOffsetChangedListener,
        StickyGroupHeaderListView.OnStickyHeaderChangedListener {

    private StickyGroupHeaderBaseTestAdapter mAdapter;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean fadeHeader = true;

    private StickyGroupHeaderListView stickyList;
    private SwipeRefreshLayout refreshLayout;

    private Button restoreButton;
    private Button updateButton;
    private Button clearButton;

    private CheckBox stickyCheckBox;
    private CheckBox fadeCheckBox;
    private CheckBox drawBehindCheckBox;
    private CheckBox fastScrollCheckBox;
    private Button openExpandableListButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_group_header_list);

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_layout);
        refreshLayout.setEnabled(false);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        mAdapter = new StickyGroupHeaderBaseTestAdapter(this);

        stickyList = (StickyGroupHeaderListView)findViewById(R.id.list);
        stickyList.setOnItemClickListener(this);
        stickyList.setOnHeaderClickListener(this);
//        stickyList.setOnStickyHeaderChangedListener(this);
//        stickyList.setOnStickyHeaderOffsetChangedListener(this);
        stickyList.addHeaderView(getLayoutInflater().inflate(R.layout.listview_group_header_list_header, null));
        stickyList.addFooterView(getLayoutInflater().inflate(R.layout.listview_group_header_list_footer, null));
        stickyList.setEmptyView(findViewById(R.id.empty));
        stickyList.setDrawingListUnderStickyHeader(true);
        stickyList.setAreHeadersSticky(false);
        stickyList.setAdapter(mAdapter, 0, 0);
        stickyList.setDivider(new ColorDrawable(Color.RED));
        stickyList.setDivider2(new ColorDrawable(Color.GREEN));
        stickyList.setDividerHeight(10);
        stickyList.setGroupDivider(new ColorDrawable(Color.BLUE));
        stickyList.postDelayed(new Runnable() {
            @Override
            public void run() {
                stickyList.setDivider(new ColorDrawable(Color.CYAN));
                stickyList.setDividerHeight(5);
                stickyList.setGroupDivider(new ColorDrawable(Color.GREEN));
            }
        }, 2000);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        );

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);

        restoreButton = (Button)findViewById(R.id.restore_button);
        restoreButton.setOnClickListener(buttonListener);
        openExpandableListButton = (Button)findViewById(R.id.open_expandable_list_button);
        openExpandableListButton.setOnClickListener(buttonListener);
        updateButton = (Button)findViewById(R.id.update_button);
        updateButton.setOnClickListener(buttonListener);
        clearButton = (Button)findViewById(R.id.clear_button);
        clearButton.setOnClickListener(buttonListener);

        stickyCheckBox = (CheckBox)findViewById(R.id.sticky_checkBox);
        stickyCheckBox.setOnCheckedChangeListener(checkBoxListener);
        fadeCheckBox = (CheckBox)findViewById(R.id.fade_checkBox);
        fadeCheckBox.setOnCheckedChangeListener(checkBoxListener);
        drawBehindCheckBox = (CheckBox)findViewById(R.id.draw_behind_checkBox);
        drawBehindCheckBox.setOnCheckedChangeListener(checkBoxListener);
        fastScrollCheckBox = (CheckBox)findViewById(R.id.fast_scroll_checkBox);
        fastScrollCheckBox.setOnCheckedChangeListener(checkBoxListener);

        stickyList.setStickyHeaderTopOffset(0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.sticky_checkBox:
                    stickyList.setAreHeadersSticky(isChecked);
                    break;
                case R.id.fade_checkBox:
                    fadeHeader = isChecked;
                    break;
                case R.id.draw_behind_checkBox:
                    stickyList.setDrawingListUnderStickyHeader(isChecked);
                    break;
                case R.id.fast_scroll_checkBox:
                    stickyList.setFastScrollEnabled(isChecked);
                    stickyList.setFastScrollAlwaysVisible(isChecked);
                    break;
            }
        }
    };

    View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.restore_button:
                    mAdapter.restore();
                    break;
                case R.id.update_button:
                    mAdapter.notifyDataSetChanged();
                    break;
                case R.id.clear_button:
                    mAdapter.clear();
                    break;
                case R.id.open_expandable_list_button:
                    Intent intent = new Intent(StickyGroupHeaderListActivity.this, ExpandableStickyGroupHeaderListActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "Item " + position + " clicked!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHeaderClick(StickyGroupHeaderListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
        Toast.makeText(this, "Header " + headerId + " currentlySticky ? " + currentlySticky, Toast.LENGTH_SHORT).show();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onStickyHeaderOffsetChanged(StickyGroupHeaderListView l, View header, int offset) {
        if (fadeHeader && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            header.setAlpha(1 - (offset / (float)header.getMeasuredHeight()));
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onStickyHeaderChanged(StickyGroupHeaderListView l, View header, int itemPosition, long headerId) {
        header.setAlpha(1);
    }

}
