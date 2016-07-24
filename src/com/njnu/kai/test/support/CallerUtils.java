package com.njnu.kai.test.support;

import java.util.ArrayList;

/**
 * @version 1.0.0
 */
public class CallerUtils {

    private static final ArrayList<String> SYSTEM_PKG_PREFIX = new ArrayList<>();

    static {
        SYSTEM_PKG_PREFIX.add("android.");
        SYSTEM_PKG_PREFIX.add("java.");
        SYSTEM_PKG_PREFIX.add("javax.");
        SYSTEM_PKG_PREFIX.add("dalvik.");
        SYSTEM_PKG_PREFIX.add("assets.");
        SYSTEM_PKG_PREFIX.add("com.android.");
        SYSTEM_PKG_PREFIX.add("junit.");
        SYSTEM_PKG_PREFIX.add("org.apache");
        SYSTEM_PKG_PREFIX.add("org.json");
        SYSTEM_PKG_PREFIX.add("org.w3c.");
        SYSTEM_PKG_PREFIX.add("org.xml");
    }

    public static boolean isSystemPkg(String pkgName) {
        for (String prefix : SYSTEM_PKG_PREFIX) {
            if (pkgName.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return a String describing the calling method and location at a particular stack depth.
     *
     * @param callStack the Thread stack
     * @param depth     the depth of stack to return information for.
     * @return the String describing the caller at that depth.
     */
    private static String getCaller(StackTraceElement[] callStack, int depth) {
        // callStack[4] is the caller of the method that called getCallers()
        if (4 + depth >= callStack.length) {
            return "<bottom of call stack>";
        }
        StackTraceElement caller = callStack[4 + depth];
        return caller.getClassName() + "." + caller.getMethodName() + ":" + caller.getLineNumber();
    }

    private static String getAllCaller(StackTraceElement[] callStack) {
        final StringBuilder builder = new StringBuilder();
        for (int idx = 3; idx < callStack.length; ++idx) {
            StackTraceElement caller = callStack[idx];
            if (isSystemPkg(caller.getClassName())) {
                break;
            } else {
                builder.append("\n" + caller.getClassName() + "." + caller.getMethodName() + ":" + caller.getLineNumber());
            }
        }
        return builder.toString();
    }

    /**
     * @return a String describing the immediate caller of the calling method.
     */
    public static String getCaller() {
        return getCaller(Thread.currentThread().getStackTrace(), 0);
    }

    public static String getAllCaller() {
        return getAllCaller(Thread.currentThread().getStackTrace());
    }
}
