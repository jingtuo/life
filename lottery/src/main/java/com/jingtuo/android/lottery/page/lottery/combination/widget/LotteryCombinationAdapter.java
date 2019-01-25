package com.jingtuo.android.lottery.page.lottery.combination.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jingtuo.android.lottery.R;
import com.jingtuo.android.lottery.model.db.LotteryCombination;
import com.jingtuo.android.widget.BaseAdapter;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author JingTuo
 */
public class LotteryCombinationAdapter extends BaseAdapter<LotteryCombination> {

    private NumberFormat numberFormat = NumberFormat.getPercentInstance(Locale.getDefault());

    public LotteryCombinationAdapter() {
        numberFormat.setMinimumFractionDigits(4);
    }

    @Override
    protected ViewHolder<BaseAdapter<LotteryCombination>, LotteryCombination> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LotteryAnalysisViewHolder(parent, this, viewType);
    }

    class LotteryAnalysisViewHolder extends ViewHolder<BaseAdapter<LotteryCombination>, LotteryCombination> {

        private TextView tvResult;
        private TextView tvProbability;

        LotteryAnalysisViewHolder(ViewGroup parent, LotteryCombinationAdapter adapter, int viewType) {
            super(parent, adapter, viewType);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.lottery_analysis;
        }

        @Override
        protected void initView(View view) {
            super.initView(view);
            tvResult = view.findViewById(R.id.tv_result);
            tvProbability = view.findViewById(R.id.tv_probability);
        }

        @Override
        public void setView(int position, LotteryCombination data) {
            super.setView(position, data);
            tvResult.setText(data.getCombination());
            tvProbability.setText(numberFormat.format(data.getProbability()));
        }
    }

}
