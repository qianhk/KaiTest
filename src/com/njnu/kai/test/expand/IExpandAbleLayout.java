package com.njnu.kai.test.expand;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-9-29
 */
public interface IExpandAbleLayout {

    int getMaxShowCount();

    void setMaxShowCount(int maxShowCount);

    boolean isExpanded() ;

    void collapse();

    void expand();

}
