package com.njnu.kai.test.draglistview;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;


/**
 * @author chao.fan
 * @version 7.1.0
 */
public class SimpleFloatViewController implements FloatViewController {
    private Bitmap mFloatBitmap;
    private ImageView mImageView;
    private int mFloatBGColor = Color.BLACK;
    private ListView mListView;

    /**
     * @param lv ListView
     */
    public SimpleFloatViewController(ListView lv) {
        mListView = lv;
    }

    /**
     * @param color int
     */
    public void setBackgroundColor(int color) {
        mFloatBGColor = color;
    }

    @Override
    public View onCreateFloatView(int position) {
        View v = mListView.getChildAt(position + mListView.getHeaderViewsCount()
                - mListView.getFirstVisiblePosition());

        if (v == null) {
            return null;
        }

        v.setPressed(false);
        v.setDrawingCacheEnabled(true);
        mFloatBitmap = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);

        if (mImageView == null) {
            mImageView = new ImageView(mListView.getContext());
        }
        mImageView.setBackgroundColor(mFloatBGColor);
        mImageView.setPadding(0, 0, 0, 0);
        mImageView.setImageBitmap(mFloatBitmap);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(v.getWidth(), v.getHeight()));

        return mImageView;
    }

    @Override
    public void onDragFloatView(View floatView, Point position, Point touch) {
        // do nothing
    }

    @Override
    public void onDestroyFloatView(View floatView) {
        ((ImageView) floatView).setImageDrawable(null);

        mFloatBitmap.recycle();
        mFloatBitmap = null;
    }

}

