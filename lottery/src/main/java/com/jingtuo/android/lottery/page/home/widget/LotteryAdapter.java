package com.jingtuo.android.lottery.page.home.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jingtuo.android.lottery.Constants;
import com.jingtuo.android.lottery.R;
import com.jingtuo.android.lottery.model.db.Lottery;
import com.jingtuo.android.widget.BaseAdapter;


/**
 * 彩票-Adapter
 * @author JingTuo
 */
public class LotteryAdapter extends BaseAdapter<Lottery> {

    @Override
    protected ViewHolder<BaseAdapter<Lottery>, Lottery> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LotteryViewHolder(parent, this, viewType);
    }

    class LotteryViewHolder extends ViewHolder<BaseAdapter<Lottery>, Lottery> {

        TextView tvName;
        TextView tvDescription;
        TextView tvHotLabel;
        TextView tvHighLabel;


        LotteryViewHolder(ViewGroup parent, BaseAdapter<Lottery> adapter, int viewType) {
            super(parent, adapter, viewType);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.lottery;
        }

        @Override
        protected void initView(View view) {
            super.initView(view);
            tvName = view.findViewById(R.id.tv_name);
            tvDescription = view.findViewById(R.id.tv_description);
            tvHotLabel = view.findViewById(R.id.tv_hot_label);
            tvHighLabel = view.findViewById(R.id.tv_high_frequency_label);
        }

        @Override
        public void setView(int position, Lottery data) {
            super.setView(position, data);
            tvName.setText(data.getDescr());
            tvDescription.setText(data.getNotes());
            tvHotLabel.setVisibility(Constants.TRUE.equalsIgnoreCase(data.getHots()) ? View.VISIBLE : View.GONE);
            tvHighLabel.setVisibility(Constants.TRUE.equalsIgnoreCase(data.getHigh()) ? View.VISIBLE : View.GONE);
        }
    }
}
