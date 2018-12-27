package com.jingtuo.android.widget;

import android.view.View;

/**
 * 当某一项点击
 * @author JingTuo
 */
public interface OnItemClickListener {
    /**
     * 当某项点击
     * @param parent
     * @param view
     * @param position
     */
    void onItemClick(View parent, View view, int position);
}
