package com.njnu.kai.test.delloc;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import com.njnu.kai.test.R;
import com.njnu.kai.test.TestApplication;
import com.njnu.kai.test.support.LogUtils;
import com.njnu.kai.test.view.BigMemoryView;

import java.util.ArrayList;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-12-19
 */
public class DellocBigMemoryActivity extends Activity {
    private static final String TAG = "DellocBigMemoryActivity";
    private BigMemoryView mBigMemoryView;
    private ImageView mImageView;
    private ListView mListView;

    private byte[] mBytes;
    private Drawable mDrawable;

    private DellocAdapter mAdapter;
    private ArrayList<String> mStringList;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LogUtils.d(TAG, "handleMessage arrived");
        }
    };
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LogUtils.d(TAG, "onClick");
            mImageView.setBackgroundColor(Color.WHITE);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_delloc_memory);
        mListView = (ListView)findViewById(R.id.list);
        View headerView = LayoutInflater.from(this).inflate(R.layout.header_delloc_memory, null, false);
        mListView.addHeaderView(headerView, null, false);
        mBigMemoryView = (BigMemoryView)headerView.findViewById(R.id.v_bigMemory);
        mBigMemoryView.setOnClickListener(mOnClickListener);
        mImageView = (ImageView)headerView.findViewById(R.id.iv_pic);
        mDrawable = getResources().getDrawable(R.drawable.ic_mv);
        mImageView.setImageDrawable(mDrawable);
        if (mBytes == null) {
            LogUtils.d(TAG, "onCreate lookFinalize mBytes == null");
            mBytes = new byte[BigMemoryView.MEMORY_SIZE];
        } else {
            LogUtils.d(TAG, "onCreate lookFinalize mBytes != null");
        }
        mHandler.sendEmptyMessageDelayed(0, 10 * 1000);

        mAdapter = new DellocAdapter(TestApplication.getApp());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.d(TAG, "onClick");
                mImageView.setBackgroundColor(Color.GREEN);
//                startActivity(new Intent(DellocBigMemoryActivity.this, BadTokenActivity.class));
//                finish();
            }
        });
        mStringList = new ArrayList<String>();
        mStringList.add("kai Test");
        mStringList.add("kai Test2");
        mStringList.add("kai Test3");
        mStringList.add("kai Test4");
        mAdapter.refreshData(mStringList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
//        mHandler = null;
//        mBigMemoryView = null;
////        System.gc();
//        System.runFinalization();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        LogUtils.d(TAG, "finalize lookFinalize %b", (mBytes != null));
    }
}
