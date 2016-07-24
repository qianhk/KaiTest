package com.njnu.kai.test.menu.draglayout;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.njnu.kai.test.R;
import com.njnu.kai.test.support.BaseActivity;
import com.njnu.kai.test.support.LogUtils;


public class DragLayoutMenuActivity extends BaseActivity {

    private static final String TAG = "DragLayoutMenuActivity";
    private SlidingMenu dl;
    private GridView gv_img;
    private TextView tv_noimg;
    private ImageView iv_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_layout_main);
        initDragLayout();
        initView();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        loadImage();
    }

    private void flushBackgroundColor(int darkColor, int brightColor) {
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{darkColor, brightColor});
        dl.setBackgroundDrawable(drawable);
    }

    private void initDragLayout() {
        dl = (SlidingMenu) findViewById(R.id.dl);
//        flushBackgroundColor(Color.CYAN, Color.MAGENTA);
        dl.setBackgroundColor(Color.WHITE);
        dl.setDragListener(new SlidingMenu.DragListener() {
            @Override
            public void onOpen() {
//				lv.smoothScrollToPosition(new Random().nextInt(30));
                LogUtils.d(TAG, "lookMenu onOpen");
            }

            @Override
            public void onClose() {
                LogUtils.d(TAG, "lookMenu onClose");
                shake();
            }

            @Override
            public void onDrag(float percent) {
                iv_icon.setAlpha(1 - percent);
            }
        });
    }

    private void initView() {
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        gv_img = (GridView) findViewById(R.id.gv_img);
        tv_noimg = (TextView) findViewById(R.id.iv_noimg);
        gv_img.setFastScrollEnabled(true);
        gv_img.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//				Intent intent = new Intent(DragLayoutMenuActivity.this,
//						ImageActivity.class);
//				intent.putExtra("path", adapter.getItem(position));
//				startActivity(intent);
            }
        });
        iv_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dl.open();
            }
        });
    }

    private void loadImage() {
//        new Invoker(new Callback() {
//            @Override
//            public boolean onRun() {
//                adapter.addAll(Util.getGalleryPhotos(DragLayoutMenuActivity.this));
//                return adapter.isEmpty();
//            }
//
//            @Override
//            public void onBefore() {
//                // 转菊花
//            }
//
//            @Override
//            public void onAfter(boolean b) {
//                adapter.notifyDataSetChanged();
//                if (b) {
//                    tv_noimg.setVisibility(View.VISIBLE);
//                } else {
//                    tv_noimg.setVisibility(View.GONE);
//                    String s = "file://" + adapter.getItem(0);
//                    ImageLoader.getInstance().displayImage(s, iv_icon);
//                }
//                shake();
//            }
//        }).start();

    }

    private void shake() {
        iv_icon.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
    }

}
