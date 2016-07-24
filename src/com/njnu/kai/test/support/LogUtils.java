/**
 * @(#)LogUtils.java     2011-8-15
 * Copyright (c) 2007-2011 Shanghai ShuiDuShi Co.Ltd. All right reserved.
 */
package com.njnu.kai.test.support;
import android.util.Log;

/**
 * @since Aug 12, 2011
 */
public final class LogUtils {
	private static final String PREFIX = "KAI:";
    private static boolean mLogEnable = true;

    /**
     * 设置Log开关
     *
     * @param enable 开关项(默认为开).
     */
    public static void setEnable(boolean enable) {
        mLogEnable = enable;
    }

    /**
	 * log for debug
     *
	 * @see android.util.Log#d(String, String)
	 * @param message log message
	 * @param tag tag
	 */
	public static void d(String tag, String message) {
        if (mLogEnable) {
			Log.d(tag, PREFIX + message);
		}
	}

	/**
	 * log for debug
	 * @see android.util.Log#d(String, String, Throwable)
	 * @param message log message
	 * @param throwable throwable
	 * @param tag tag
	 */
	public static void d(String tag, String message, Throwable throwable) {
		if (mLogEnable) {
			Log.d(tag, PREFIX + message, throwable);
		}
	}

	/**
	 * log for debug
	 * @see android.util.Log#d(String, String)
     * @param tag tag
	 * @param format message format, such as "%d ..."
     * @param params message content params
	 */
	public static void d(String tag, String format, Object... params) {
		if (mLogEnable) {
			Log.d(tag, String.format(PREFIX + format, params));
		}
	}

	/**
	 * log for warning
	 * @see android.util.Log#w(String, String)
	 * @param message log message
	 * @param tag tag
	 */
	public static void w(String tag, String message) {
		if (mLogEnable) {
			Log.w(tag, PREFIX + message);
		}
	}

	/**
	 * log for warning
	 * @see android.util.Log#w(String, Throwable)
	 * @param tag tag
	 * @param throwable throwable
	 */
	public static void w(String tag, Throwable throwable) {
		if (mLogEnable) {
			Log.w(tag, throwable);
		}
	}

	/**
	 * log for warning
	 * @see android.util.Log#w(String, String, Throwable)
	 * @param message log message
	 * @param throwable throwable
	 * @param tag tag
	 */
	public static void w(String tag, String message, Throwable throwable) {
		if (mLogEnable) {
			Log.w(tag, PREFIX + message, throwable);
		}
	}

    /**
	 * log for warning
	 * @see android.util.Log#w(String, String)
     * @param tag tag
	 * @param format message format, such as "%d ..."
     * @param params message content params
	 */
	public static void w(String tag, String format, Object... params) {
		if (mLogEnable) {
			Log.w(tag, String.format(PREFIX + format, params));
		}
	}

	/**
	 * log for error
	 * @see android.util.Log#i(String, String)
	 * @param message message
	 * @param tag tag
	 */
	public static void e(String tag, String message) {
		Log.e(tag, PREFIX + message);
	}

	/**
	 * log for error
	 * @see android.util.Log#i(String, String, Throwable)
	 * @param message log message
	 * @param throwable throwable
	 * @param tag tag
	 */
	public static void e(String tag, String message, Throwable throwable) {
		Log.e(tag, PREFIX + message, throwable);
	}

    /**
	 * log for error
	 * @see android.util.Log#e(String, String)
     * @param tag tag
	 * @param format message format, such as "%d ..."
     * @param params message content params
	 */
	public static void e(String tag, String format, Object... params) {
		Log.e(tag, String.format(PREFIX + format, params));
	}

	/**
	 * log for information
	 * @see android.util.Log#i(String, String)
	 * @param message message
	 * @param tag tag
	 */
	public static void i(String tag, String message) {
		if (mLogEnable) {
			Log.i(tag, PREFIX + message);
		}
	}

	/**
	 * log for information
	 * @see android.util.Log#i(String, String, Throwable)
	 * @param message log message
	 * @param throwable throwable
	 * @param tag tag
	 */
	public static void i(String tag, String message, Throwable throwable) {
		if (mLogEnable) {
			Log.i(tag, PREFIX + message, throwable);
		}
	}

    /**
	 * log for information
	 * @see android.util.Log#i(String, String)
     * @param tag tag
	 * @param format message format, such as "%d ..."
     * @param params message content params
	 */
	public static void i(String tag, String format, Object... params) {
		if (mLogEnable) {
			Log.i(tag, String.format(PREFIX + format, params));
		}
	}

	/**
	 * log for verbos
	 * @see android.util.Log#v(String, String)
	 * @param message log message
	 * @param tag tag
	 */
	public static void v(String tag, String message) {
		if (mLogEnable) {
			Log.v(tag, PREFIX + message);
		}
	}

	/**
	 * log for verbose
	 * @see android.util.Log#v(String, String, Throwable)
	 * @param message log message
	 * @param throwable throwable
	 * @param tag tag
	 */
	public static void v(String tag, String message, Throwable throwable) {
		if (mLogEnable) {
			Log.v(tag, PREFIX + message, throwable);
		}
	}

    /**
	 * log for verbose
	 * @see android.util.Log#v(String, String)
     * @param tag tag
	 * @param format message format, such as "%d ..."
     * @param params message content params
	 */
	public static void v(String tag, String format, Object... params) {
		if (mLogEnable) {
			Log.v(tag, String.format(PREFIX + format, params));
		}
	}
}
