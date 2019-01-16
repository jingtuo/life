package com.jingtuo.android.lottery.util;

import com.jingtuo.android.lottery.Constants;
import com.jingtuo.android.lottery.model.db.Lottery;

import io.reactivex.functions.Predicate;

/**
 * 彩票过滤
 *
 * @author JingTuo
 */
public class LotteryPredicate implements Predicate<Lottery> {

    /**
     * 热门
     */
    private boolean hot;

    /**
     * 高频
     */
    private boolean high;

    public LotteryPredicate(boolean hot, boolean high) {
        this.hot = hot;
        this.high = high;
    }

    @Override
    public boolean test(Lottery lottery) {
        return (hot && Constants.TRUE.equals(lottery.getHots()) || !hot && !Constants.TRUE.equals(lottery.getHots()))
                && (high && Constants.TRUE.equals(lottery.getHigh()) || !high && !Constants.TRUE.equals(lottery.getHigh()));
    }
}
