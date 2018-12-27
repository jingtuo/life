package com.jingtuo.android.lottery.model.request;

import com.jingtuo.android.lottery.model.db.Lottery;
import com.jingtuo.android.lottery.model.db.LotteryResult;
import com.jingtuo.android.lottery.model.response.YiYuanResponse;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * 彩票服务
 * @author JingTuo
 */
public interface LotteryService {
    /**
     * 查询支持的彩票
     *
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST("44-6")
    Call<YiYuanResponse<ArrayList<Lottery>>> querySupportedLotteries(@FieldMap Map<String, String> fields);


    /**
     * 查询彩票的开奖结果
     *
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST("44-2")
    Call<YiYuanResponse<ArrayList<LotteryResult>>> queryLotteryResults(@FieldMap Map<String, String> fields);
}
