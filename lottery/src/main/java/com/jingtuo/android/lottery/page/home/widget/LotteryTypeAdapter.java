package com.jingtuo.android.lottery.page.home.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jingtuo.android.lottery.R;
import com.jingtuo.android.widget.BaseAdapter;

/**
 * 彩票类型-Adapter
 * @author JingTuo
 */
public class LotteryTypeAdapter extends BaseAdapter<String> {
    @Override
    protected ViewHolder<BaseAdapter<String>, String> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LotteryTypeViewHolder(parent, this, viewType);
    }

    class LotteryTypeViewHolder extends ViewHolder<BaseAdapter<String>, String> {

        TextView textView;


        LotteryTypeViewHolder(ViewGroup parent, BaseAdapter<String> adapter, int viewType) {
            super(parent, adapter, viewType);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.spinner_item;
        }

        @Override
        protected void initView(View view) {
            super.initView(view);
            textView = (TextView) view;
        }

        @Override
        public void setView(int position, String data) {
            super.setView(position, data);
            textView.setText(data);
        }
    }
}
