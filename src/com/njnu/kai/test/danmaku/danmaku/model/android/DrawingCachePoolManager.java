package com.njnu.kai.test.danmaku.danmaku.model.android;


import com.njnu.kai.test.danmaku.danmaku.model.objectpool.PoolableManager;

public class DrawingCachePoolManager implements PoolableManager<DrawingCache> {

    @Override
    public DrawingCache newInstance() {
        return null;
    }

    @Override
    public void onAcquired(DrawingCache element) {

    }

    @Override
    public void onReleased(DrawingCache element) {

    }

}
