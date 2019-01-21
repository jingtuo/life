package com.jingtuo.android.widget;

import android.graphics.Rect;
import android.os.Build;
import android.support.v4.widget.ListViewCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.lang.reflect.Method;

/**
 * 控件工具类
 *
 * @author JingTuo
 */
public class ViewUtils {

    private static final String TAG = ViewUtils.class.getSimpleName();


    /**
     * 手指从上往下滑动->下拉
     * 手指从下往上滑动->上滑
     *
     * @param viewGroup
     * @param direction -1:表示是否可以下拉;1:表示是否可以上滑
     * @return
     */
    public static boolean canScrollVertically(ViewGroup viewGroup, int direction) {
        if (viewGroup instanceof ListView) {
            ListView listView = (ListView) viewGroup;
            /**
             * childCount是可见的控件数量
             */
            int childCount = listView.getChildCount();
            if (childCount == 0) {
                return false;
            } else {
                int firstPosition = listView.getFirstVisiblePosition();
                int firstTop;
                if (direction > 0) {
                    /**
                     * 加载更多功能需要在ListView的FooterView增加View,
                     * 所以此处用childCount-2
                     */
                    firstTop = listView.getChildAt(childCount - 2).getBottom();
                    int lastPosition = firstPosition + childCount;
                    /**
                     * headerView的数量 + adapter.getCount() + footerView的数量
                     */
                    int count = listView.getCount();
                    return lastPosition < count - 1 || firstTop > listView.getHeight() - listView.getListPaddingBottom();
                } else {
                    /**
                     * 下拉刷新功能需要在ListView的HeaderView增加View,
                     * 所以此处用1
                     */
                    firstTop = listView.getChildAt(1).getTop();
                    return firstPosition > 1 || firstTop < listView.getListPaddingTop();
                }
            }
        }
        final int offset = call(viewGroup, "computeVerticalScrollOffset");
        final int range = call(viewGroup, "computeVerticalScrollRange") - call(viewGroup, "computeVerticalScrollExtent");
        if (range == 0) {
            return false;
        }
        if (direction < 0) {
            return offset > 0;
        } else {
            return offset < range - 1;
        }
    }

    /**
     * @param view
     * @param methodName
     * @return
     */
    private static int call(View view, String methodName) {
        Class<? extends View> cls = view.getClass();
        try {
            Method method = cls.getDeclaredMethod(methodName);
            Object obj = method.invoke(view);
            return (Integer) obj;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return 0;
    }
}
