package com.jingtuo.android.util;

import android.util.Log;

/**
 * Log工具类
 *
 * @author JingTuo
 */
public class LogUtils {

    private LogUtils() {

    }

    /**
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }

    /**
     * @param tag
     * @param tr
     */
    public static void e(String tag, Throwable tr) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, tr.getMessage(), tr);
        }
    }


    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }
}
