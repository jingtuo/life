package com.jingtuo.android.lottery.util;

import android.util.Log;

import com.jingtuo.android.lottery.BuildConfig;

/**
 * 工具类
 *
 * @author JingTuo
 */
public class SimpleLog {

    private SimpleLog() {

    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }
}
