package com.njnu.kai.test.menu.draglayout;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.BaseActivity;
import com.njnu.kai.test.view.SlidingTabHost;

import java.util.Random;


public class MenuWithViewPagerActivity extends BaseActivity {

    private static final String TAG = "MenuWithViewPagerActivity";
    private SlidingMenu dl;
    private GridView gv_img;
    private TextView tv_noimg;
    private ImageView iv_icon;

    protected SlidingTabHost mSlidingTabHost;
    protected ViewPager mViewPager;

    private Random mRandom = new Random(System.currentTimeMillis());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_viewpager_main);

        mSlidingTabHost = (SlidingTabHost)findViewById(R.id.slidingtabhost);
        flushBackgroundColor(mRandom.nextInt() | 0xFF000000, mRandom.nextInt() | 0xFF000000);
        mSlidingTabHost.setTabLayoutAverageSpace(true);
        mSlidingTabHost.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int page, float percent, int offset) {
//                LogUtils.d(TAG, "lookSlidingTabHost onPageScrolled page=%d percent=%.4f offset=%d", page, percent, offset);
                if (page == 0) {
                    mSlidingTabHost.setBackgroundColor(SlidingMenu.evaluateColor(percent, mColor1, mColor2));
                } else {
                    mSlidingTabHost.setBackgroundColor(mColor2);
                }
            }

            @Override
            public void onPageSelected(int page) {
//                LogUtils.d(TAG, "lookSlidingTabHost onPageSelected page=%d", page);
            }

            @Override
            public void onPageScrollStateChanged(int page) {
//                LogUtils.d(TAG, "lookSlidingTabHost onPageScrollStateChanged page=%d", page);
            }
        });
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return o == view;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                TextView textView = new TextView(MenuWithViewPagerActivity.this);
                textView.setText("Text Kai Test " + position);
                textView.setTextColor(mColor1);
                textView.setBackgroundColor(mColor2);
                container.addView(textView);
                return textView;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "Title " + position;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View)object);
            }
        });
        mSlidingTabHost.setViewPager(mViewPager);

    }

    private int mColor1;
    private int mColor2;

    private void flushBackgroundColor(int color1, int color2) {
        mColor1 = color1;
        mColor2 = color2;
        mSlidingTabHost.setBackgroundColor(mColor1);
    }


    private void initDragLayout() {
        dl = (SlidingMenu)findViewById(R.id.dl);
        dl.setDragListener(new SlidingMenu.DragListener() {
            @Override
            public void onOpen() {
//				lv.smoothScrollToPosition(new Random().nextInt(30));
            }

            @Override
            public void onClose() {
//                shake();
            }

            @Override
            public void onDrag(float percent) {
//                ViewHelper.setAlpha(iv_icon, 1 - percent);
            }
        });
    }


}
