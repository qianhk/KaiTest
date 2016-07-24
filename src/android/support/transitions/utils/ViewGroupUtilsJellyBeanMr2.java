package android.support.transitions.utils;

import android.annotation.TargetApi;
import android.os.Build.VERSION_CODES;
import android.view.ViewGroup;

import java.lang.reflect.Method;

@TargetApi(VERSION_CODES.JELLY_BEAN_MR2)
class ViewGroupUtilsJellyBeanMr2 {
    private static final Method METHOD_suppressLayout = ReflectionUtils.getMethod(ViewGroup.class, "suppressLayout",
            boolean.class);

    public static void suppressLayout(ViewGroup group, boolean suppress) {
        ReflectionUtils.invoke(group, null, METHOD_suppressLayout, suppress);
    }
}
