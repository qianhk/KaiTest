package com.njnu.kai.test.owndan;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.transitions.Scene;
import android.support.transitions.TransitionInflater;
import android.support.transitions.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.BaseActivity;
import com.njnu.kai.test.support.LogUtils;
import com.njnu.kai.test.support.ToastUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-2-10
 */
public class DanmakuActivity extends BaseActivity {

    private static final String TAG = "DanmakuActivity";
    private List<List<String>> mBarrageList = new ArrayList<List<String>>();
    private ViewGroup mSceneRoot;
    private Scene mScene1;
    private Scene mScene2;
    private TransitionManager mTransitionManager;

    private Random mRandom;
    private DanmakuView mBarrageView;
    private int mIndex;
    private int mCurSpeed = 5;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Configuration.ORIENTATION_LANDSCAPE) {
                TransitionManager.go(mScene2);
            } else {
                TransitionManager.go(mScene1);
            }
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            if (viewId == R.id.btn_bkg_color) {
                changeBkgColor();
            } else if (viewId == R.id.btn_speed_up) {
                ++mCurSpeed;
                if (mCurSpeed > 10) {
                    mCurSpeed = 10;
                } else {
                    ToastUtils.showToast(DanmakuActivity.this, "速度已调整至" + mCurSpeed);
                }
                mBarrageView.setSpeed(mCurSpeed);
            } else if (viewId == R.id.btn_speed_reset) {
                mBarrageView.setSpeed(5);
                mCurSpeed = 5;
                ToastUtils.showToast(DanmakuActivity.this, "速度已重置至" + 5);
            } else if (viewId == R.id.btn_speed_down) {
                --mCurSpeed;
                if (mCurSpeed < 1) {
                    mCurSpeed = 1;
                } else {
                    ToastUtils.showToast(DanmakuActivity.this, "速度已调整至" + mCurSpeed);
                }
                mBarrageView.setSpeed(mCurSpeed);
            } else if (viewId == R.id.btn_add_sentence) {
                if (mIndex >= mBarrageList.size()) {
                    mIndex = 0;
                }
                mBarrageView.append(mBarrageList.get(mIndex++));
            }
        }
    };

    private void changeBkgColor() {
        mBarrageView.setBackgroundColor(Color.rgb(mRandom.nextInt(255), mRandom.nextInt(255), mRandom.nextInt(255)));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View mainView = LayoutInflater.from(this).inflate(R.layout.main_barrage, null);
        setContentView(mainView);
        prepareClickListener(mainView);
        mRandom = new Random(System.currentTimeMillis());
        mBarrageView = (DanmakuView)findViewById(R.id.barrage_view);
        changeBkgColor();

        mSceneRoot = (ViewGroup)findViewById(R.id.scene_root);
        mScene1 = new Scene(mSceneRoot, mSceneRoot.findViewById(R.id.scene_self_container));
//        mScene2 = Scene.getSceneForLayout(mSceneRoot, viewScene2);
        View viewScene2 = LayoutInflater.from(this).inflate(R.layout.main_barrage_scene2, null);
        prepareClickListener(viewScene2);
        mScene2 = new Scene(mSceneRoot, viewScene2);
        mTransitionManager = TransitionInflater.from(this)
                .inflateTransitionManager(R.anim.scene_transition_manager, mSceneRoot);

        Resources resources = getResources();
        mBarrageList.add(Arrays.asList(resources.getStringArray(R.array.barrage1)));
        mBarrageList.add(Arrays.asList(resources.getStringArray(R.array.barrage2)));
        mBarrageList.add(Arrays.asList(resources.getStringArray(R.array.barrage3)));
        mBarrageList.add(Arrays.asList(resources.getStringArray(R.array.barrage4)));
        mBarrageList.add(Arrays.asList(resources.getStringArray(R.array.barrage5)));

    }

    private void prepareClickListener(View rootView) {
        rootView.findViewById(R.id.btn_speed_up).setOnClickListener(mOnClickListener);
        rootView.findViewById(R.id.btn_speed_reset).setOnClickListener(mOnClickListener);
        rootView.findViewById(R.id.btn_speed_down).setOnClickListener(mOnClickListener);
        rootView.findViewById(R.id.btn_bkg_color).setOnClickListener(mOnClickListener);
        rootView.findViewById(R.id.btn_add_sentence).setOnClickListener(mOnClickListener);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtils.d(TAG, "onConfigurationChanged=" + newConfig);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessageDelayed(newConfig.orientation, 160);

    }
}
