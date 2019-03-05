package com.jingtuo.android.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * 应用工具类
 *
 * @author JingTuo
 */
public class AppUtils {

    private static final String TAG = AppUtils.class.getSimpleName();

    private AppUtils() {

    }

    /**
     * 获取版本名称
     * @param context
     * @param packageName
     * @return
     */
    public static String getVersionName(Context context, String packageName, String defaultValue) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);;
            return packageInfo.versionName;
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
        return defaultValue;
    }

}
