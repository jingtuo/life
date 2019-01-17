package com.jingtuo.android.lottery.page.lottery.result.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jingtuo.android.lottery.R;
import com.jingtuo.android.lottery.model.db.LotteryResult;
import com.jingtuo.android.widget.BaseAdapter;

/**
 * 彩票结果
 * @author JingTuo
 */
public class LotteryResultAdapter extends BaseAdapter<LotteryResult> {
    @Override
    protected ViewHolder<BaseAdapter<LotteryResult>, LotteryResult> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LotteryResultViewHolder(parent, this, viewType);
    }

    class LotteryResultViewHolder extends ViewHolder<BaseAdapter<LotteryResult>, LotteryResult> {

        private TextView tvExpect;
        private TextView tvResult;
        private TextView tvTime;


        LotteryResultViewHolder(ViewGroup parent, BaseAdapter<LotteryResult> adapter, int viewType) {
            super(parent, adapter, viewType);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.lottery_result;
        }

        @Override
        protected void initView(View view) {
            super.initView(view);
            tvExpect = view.findViewById(R.id.tv_expect);
            tvResult = view.findViewById(R.id.tv_result);
            tvTime = view.findViewById(R.id.tv_time);
        }

        @Override
        public void setView(int position, LotteryResult data) {
            super.setView(position, data);
            tvExpect.setText(data.getExpect());
            tvResult.setText(data.getOpenCode());
            tvTime.setText(data.getTime());
        }
    }
}
