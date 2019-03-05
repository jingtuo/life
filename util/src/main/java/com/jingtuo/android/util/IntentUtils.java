package com.jingtuo.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Intent工具类
 *
 * @author JingTuo
 */
public class IntentUtils {

    private IntentUtils() {

    }

    public static void startActivityWithNormal(Context context, Class<? extends Activity> activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
