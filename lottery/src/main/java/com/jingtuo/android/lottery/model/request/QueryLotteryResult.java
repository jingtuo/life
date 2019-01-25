package com.jingtuo.android.lottery.model.request;

import android.content.Context;
import android.text.TextUtils;

import com.jingtuo.android.lottery.Constants;
import com.jingtuo.android.lottery.model.db.LotteryDao;
import com.jingtuo.android.lottery.model.db.LotteryResult;
import com.jingtuo.android.lottery.model.repo.LotteryRepo;
import com.jingtuo.android.lottery.model.response.LotteryResultWrapper;
import com.jingtuo.android.lottery.model.response.YiYuanResponse;
import com.jingtuo.android.lottery.util.LotteryUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Response;

/**
 * 查询彩票结果
 *
 * @author JingTuo
 */
public class QueryLotteryResult implements Callable<LotteryResultWrapper> {


    private Context appContext;

    private String code;

    private String time;

    public QueryLotteryResult(Context appContext, String code, String time) {
        this.appContext = appContext;
        this.code = code;
        this.time = time;
    }

    @Override
    public LotteryResultWrapper call() throws Exception {
        LotteryDao lotteryDao = LotteryRepo.getInstance().getLotteryDao(appContext);
        if (TextUtils.isEmpty(time)) {
            //时间为空,从服务器查询最新数据
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            List<LotteryResult> result = queryLotteryResultFromService(code, dateFormat.format(new Date(System.currentTimeMillis())));
            //优先保存到数据
            lotteryDao.insertLotteryResults(result);
            LotteryResult lotteryResult = result.get(0);
            result = lotteryDao.queryLotteryResults(code, lotteryResult.getTime());
            if (result.size() < Constants.SIZE) {
                //没有更多数据
                LotteryResultWrapper wrapper = new LotteryResultWrapper();
                wrapper.setNoMore(true);
                wrapper.setResult(result);
                return wrapper;
            }
            LotteryResultWrapper wrapper = new LotteryResultWrapper();
            wrapper.setNoMore(false);
            wrapper.setResult(result);
            return wrapper;
        }
        List<LotteryResult> result = lotteryDao.queryLotteryResults(code, time);
        if (result.size() < Constants.SIZE) {
            //本地数据不足,从服务器查询
            result = queryLotteryResultFromService(code, time);
            //优先保存到数据
            lotteryDao.insertLotteryResults(result);
            if (result.size() < Constants.SIZE) {
                LotteryResultWrapper wrapper = new LotteryResultWrapper();
                wrapper.setNoMore(true);
                wrapper.setResult(result);
                return wrapper;
            }
            LotteryResultWrapper wrapper = new LotteryResultWrapper();
            wrapper.setNoMore(false);
            wrapper.setResult(result);
            return wrapper;

        }
        LotteryResultWrapper wrapper = new LotteryResultWrapper();
        wrapper.setNoMore(false);
        wrapper.setResult(result);
        return wrapper;
    }

    /**
     * @param code
     * @param time
     * @return
     * @throws Exception
     */
    private List<LotteryResult> queryLotteryResultFromService(String code, String time) throws IOException {
        Call<YiYuanResponse<ArrayList<LotteryResult>>> call = LotteryRepo.getInstance().getLotteryService().queryLotteryResults(LotteryUtils.createQueryLotteryResultFields(code, time));
        Response<YiYuanResponse<ArrayList<LotteryResult>>> response = call.execute();
        List<LotteryResult> result = LotteryUtils.getData(response);
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }
}
