package com.njnu.kai.test.grid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.njnu.kai.test.HtcT328WActivity;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.BaseActivity;
import com.njnu.kai.test.view.HotwordView;

import java.util.ArrayList;
import java.util.Random;

public class SimpleGridActivity extends BaseActivity {

    private SimpleGridView mSimpleGrid;
    private ImageView mFavoriteImageView;
    private boolean mFavorite;
    private View mBtnAdd;
    private HotwordView mHotwordView;

    private volatile boolean mStartup;
    private Button mBtnStartupActivity;
    private TextView mTvStartupCount;
    private int mCount = 0;
    private Random mRandom = new Random(System.currentTimeMillis());

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_simple_grid);

        mBtnAdd = findViewById(R.id.btn_add);
        mBtnAdd.setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_remove).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_animation).setOnClickListener(mOnClickListener);
        mFavoriteImageView = (ImageView)findViewById(R.id.img_favourite);
        mFavoriteImageView.setOnClickListener(mOnClickListener);

        mSimpleGrid = (SimpleGridView)findViewById(R.id.grid_square);

        mOnClickListener.onClick(mBtnAdd);
        mOnClickListener.onClick(mBtnAdd);

        mHotwordView = (HotwordView)findViewById(R.id.hotword_view);
        ArrayList<String> billboardsList = new ArrayList<String>();
        billboardsList.add("KaiKai");
        billboardsList.add("凯");
        billboardsList.add("测试");
        billboardsList.add("Test");
        mHotwordView.setContent(billboardsList);

        mBtnStartupActivity = (Button)findViewById(R.id.btn_startup_activity);
        mTvStartupCount = (TextView)findViewById(R.id.tv_startup_count);
        mBtnStartupActivity.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_add:
                    addOne();
                    break;

                case R.id.btn_remove:
                    removeOne();
                    break;

                case R.id.btn_animation:
                    tryAnimation();
                    break;

                case R.id.img_favourite:
                    tryFavImageAnimation();
                    break;

                case R.id.btn_startup_activity: {
                    if (mStartup) {
                        tryStopActivity();
                    } else {
                        tryStartupActivity();
                    }
                }
                break;
            }
        }
    };

    private void tryStartupActivity() {
//        mStartup = true;
//        mBtnStartupActivity.setText("停止");
//        new Thread(mStartActivity).start();
    }

    private void tryStopActivity() {
        mStartup = false;
        mBtnStartupActivity.setText("停止中...");
        mBtnStartupActivity.setEnabled(false);
    }

    private Runnable mStartActivity = new Runnable() {
        @Override
        public void run() {
            while (mStartup) {
                startActivity(new Intent(SimpleGridActivity.this, HtcT328WActivity.class));
                mBtnStartupActivity.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ++mCount;
                        mTvStartupCount.setText("共启动" + mCount + "次。");
                    }
                }, 0);
//                if (mStartup) {
                try {
                    Thread.sleep(mRandom.nextInt(500));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                }
            }
            mBtnStartupActivity.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCount = 0;
                    mBtnStartupActivity.setText("无限启动");
                    mBtnStartupActivity.setEnabled(true);
                }
            }, 0);
        }
    };

    private void tryFavImageAnimation() {
        mFavorite = !mFavorite;
        mFavoriteImageView.setSelected(mFavorite);
//        if (mFavorite) {
        mFavoriteImageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.favorite_flag_zoom_in_out));
//        }
    }

    private void tryAnimation() {
        processFavouriteAnimationEvent1(this);
    }

    private static void processFavouriteAnimationEvent2(final Activity activity) {
        final Activity topActivity = activity;
//        final Activity topActivity = ActivityUtils.getTopActivity(activity);
//        if (topActivity == null) {
//            return;
//        }

        final ViewGroup decorView = (ViewGroup)topActivity.getWindow().getDecorView();
        final ImageView v = new ImageView(activity);
        v.setImageResource(R.drawable.icon_favorite_animation);

        AnimationSet favoriteStartAnimation = (AnimationSet)AnimationUtils.loadAnimation(activity, R.anim.favorite_zoom_in_out_out);
        favoriteStartAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                decorView.post(new Runnable() {
                    @Override
                    public void run() {
                        decorView.removeView(v);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        v.startAnimation(favoriteStartAnimation);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        decorView.addView(v, layoutParams);
    }

    private static void processFavouriteAnimationEvent1(final Activity activity) {
        final Activity topActivity = activity;
//        final Activity topActivity = ActivityUtils.getTopActivity(activity);
//        if (topActivity == null) {
//            return;
//        }

        final ViewGroup decorView = (ViewGroup)topActivity.getWindow().getDecorView();
        final ImageView v = new ImageView(activity);

        v.setImageResource(R.drawable.icon_favorite_animation);
        Drawable drawable = v.getDrawable();

        final int[] locationFrom = new int[2];

        locationFrom[0] = (activity.getResources().getDisplayMetrics().widthPixels - drawable.getIntrinsicWidth()) / 2;
        locationFrom[1] = (activity.getResources().getDisplayMetrics().heightPixels - drawable.getIntrinsicHeight()) / 2;

        TranslateAnimation translateStartAnimation = new TranslateAnimation(
                TranslateAnimation.ABSOLUTE, locationFrom[0]
                , TranslateAnimation.ABSOLUTE, locationFrom[0]
                , TranslateAnimation.ABSOLUTE, locationFrom[1]
                , TranslateAnimation.ABSOLUTE, locationFrom[1]
        );
        AnimationSet favoriteStartAnimation = (AnimationSet)AnimationUtils.loadAnimation(activity, R.anim.favorite_zoom_in_out_out);
        favoriteStartAnimation.addAnimation(translateStartAnimation);
        favoriteStartAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                decorView.post(new Runnable() {
                    @Override
                    public void run() {
                        decorView.removeView(v);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        v.startAnimation(favoriteStartAnimation);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        decorView.addView(v, layoutParams);
    }

    private void removeOne() {
        int childCount = mSimpleGrid.getChildCount();
        if (childCount > 0) {
            int index = mRandom.nextInt(childCount);
            mSimpleGrid.removeViewAt(index);
        }
    }

//    private View makeOneView() {
//        View view = new View(this);
//        final int MAX_COLOR = 256;
//        view.setBackgroundColor(Color.rgb(mRandom.nextInt(MAX_COLOR), mRandom.nextInt(MAX_COLOR), mRandom.nextInt(MAX_COLOR)));
//        view.setLayoutParams(new ViewGroup.LayoutParams(80, 60));
//        return view;
//    }

    private View makeOneView() {
        View view = View.inflate(this, R.layout.part_pic_with_bottom_text, null);
        final int MAX_COLOR = 256;
        view.setBackgroundColor(Color.rgb(mRandom.nextInt(MAX_COLOR), mRandom.nextInt(MAX_COLOR), mRandom.nextInt(MAX_COLOR)));
        return view;
    }

    private void addOne() {
        int childCount = mSimpleGrid.getChildCount();
        int index = mRandom.nextInt(childCount + 1);
        mSimpleGrid.addView(makeOneView(), index);
    }
}