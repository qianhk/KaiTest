package com.njnu.kai.test.danmaku.danmaku.model;

public interface IDanmakuIterator {

    public BaseDanmaku next();

    public boolean hasNext();

    public void reset();

    public void remove();

}
