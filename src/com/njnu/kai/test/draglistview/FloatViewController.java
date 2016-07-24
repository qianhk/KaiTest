package com.njnu.kai.test.draglistview;

import android.graphics.Point;
import android.view.View;

/**
 * 悬浮View创建, 释放相关接口
 *
 * @author chao.fan
 * @version 7.1.0
 */
public interface FloatViewController {
    /**
     * 创建悬浮View
     * @param position int
     * @return View
     */
    public View onCreateFloatView(int position);

    /**
     * 拖拽
     * @param floatView View
     * @param location Point
     * @param touch Point
     */
    public void onDragFloatView(View floatView, Point location, Point touch);

    /**
     *
     * @param floatView View
     */
    public void onDestroyFloatView(View floatView);
}
