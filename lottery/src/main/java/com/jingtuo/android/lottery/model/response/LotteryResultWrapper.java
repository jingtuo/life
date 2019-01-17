package com.jingtuo.android.lottery.model.response;

import com.jingtuo.android.lottery.model.db.LotteryResult;

import java.util.List;

import lombok.Data;

/**
 * @author JingTuo
 */
@Data
public class LotteryResultWrapper {

    private boolean noMore;

    private List<LotteryResult> result;
}
