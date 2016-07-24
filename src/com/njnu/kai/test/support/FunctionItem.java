package com.njnu.kai.test.support;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 14-4-29
 */
public class FunctionItem {

    private String mName;
    private Class<?> mActionClass;

    public FunctionItem(String name, Class<?> actionClass) {
        mName = name;
        mActionClass = actionClass;
    }

    public String getName() {
        return mName;
    }

    public Class<?> getActionClass() {
        return mActionClass;
    }
}
