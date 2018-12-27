package com.jingtuo.android.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;

import lombok.Setter;


/**
 * 针对少量数据的重复组件,如tab,下拉控件的列表
 *
 * @author JingTuo
 */
public class LinearLayout<I> extends LinearLayoutCompat {

    private BaseAdapter<I> adapter;

    @Setter
    private OnItemClickListener onItemClickListener;

    public LinearLayout(Context context) {
        this(context, null);
    }

    public LinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置内容
     * @param adapter
     */
    public void setAdapter(BaseAdapter<I> adapter) {
        this.adapter = adapter;
        removeAllViews();
        if (adapter == null || adapter.getCount() <= 0) {
            return;
        }
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int position = i;
            View view = adapter.getView(i, null, this);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener!=null) {
                        onItemClickListener.onItemClick(LinearLayout.this, v, position);
                    }
                }
            });
            addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
    }
}
