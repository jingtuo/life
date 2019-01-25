package com.jingtuo.android.lottery.model.request;


import android.content.Context;

import com.jingtuo.android.lottery.model.db.LotteryCombination;
import com.jingtuo.android.lottery.model.db.LotteryDao;
import com.jingtuo.android.lottery.model.db.LotteryResult;
import com.jingtuo.android.lottery.model.repo.LotteryRepo;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * 彩票分析
 *
 * @author JingTuo
 */
public class QueryCombination implements Callable<List<LotteryCombination>> {

    private static final String TAG = QueryCombination.class.getSimpleName();

    private Context appContext;

    private String code;

    private int pageNo;

    private int pageSize;


    public QueryCombination(Context appContext, String code, int pageNo, int pageSize) {
        this.appContext = appContext;
        this.code = code;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    @Override
    public List<LotteryCombination> call() throws Exception {
        LotteryDao lotteryDao = LotteryRepo.getInstance().getLotteryDao(appContext);
        List<LotteryCombination> combinations = lotteryDao.queryCombinations(code, "probability DESC", pageNo, pageSize);
        List<LotteryResult> lotteryResults = lotteryDao.queryLotteryResults(code);
        long size = combinations.size();
        for (LotteryCombination item : combinations) {
            String combination = item.getCombination();
            long count = 0;
            for (LotteryResult result : lotteryResults) {
                if (combination.equals(result.getOpenCode())) {
                    count++;
                }
            }
            item.setProbability(count * 1.0f / size);
        }
        return combinations;
    }


}