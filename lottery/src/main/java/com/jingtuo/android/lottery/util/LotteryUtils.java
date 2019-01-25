package com.jingtuo.android.lottery.util;

import com.jingtuo.android.lottery.model.response.YiYuanResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Response;

/**
 * 彩票工具类
 *
 * @author JingTuo
 */
public class LotteryUtils {

    private LotteryUtils() {

    }

    /**
     * 创建查询支持股票的请求参数
     *
     * @return
     */
    public static Map<String, String> createQuerySupportedLotteryFields() {
        Map<String, String> fields = new HashMap<>();
        fields.put("showapi_appid", "81044");
        fields.put("showapi_sign", "ee863ff12f2e4ed9845e2ee527f75bef");
        fields.put("showapi_timestamp", "");
        fields.put("showapi_res_gzip", "1");
        return fields;
    }


    /**
     * 创建查询股票开奖结果的请求参数
     *
     * @param code
     * @param endTime
     * @return
     */
    public static Map<String, String> createQueryLotteryResultFields(String code, String endTime) {
        Map<String, String> map = createQuerySupportedLotteryFields();
        map.put("code", code);
        map.put("endTime", endTime);
        map.put("count", "50");
        return map;
    }


    /**
     * @param response
     * @param <T>
     * @return
     */
    public static <T> T getData(Response<YiYuanResponse<T>> response) {
        if (response == null || 200 != response.code() || response.body() == null || 0 != response.body().getCode()) {
            return null;
        }
        return response.body().getBody().getResult();
    }

    /**
     * 阶乘：n!=n * (n - 1) .... * 2 * 1; 0! = 1;
     *
     * @param n 数值
     * @return
     */
    public static int factorial(int n) {
        if (n < 0) {
            return 0;
        }
        if (n == 0) {
            return 1;
        }
        int result = 1;
        for (int i = n; i >= 1; i--) {
            result = result * i;
        }
        return result;
    }


    /**
     * 从n个元素中提取m个元素进行组合, C(n, m) = n! / m!(n-m)!
     *
     * @param n n个元素
     * @param m m个元素
     * @return
     */
    public static int combine(int n, int m) {
        int mf = factorial(m);
        int nmf = factorial(n - m);
        if (mf == 0 || nmf == 0) {
            return 0;
        }
        return factorial(n) / mf * nmf;
    }


    /**
     * 从n个元素中提取m个元素进行排列, A(n, m) = n! / (n-m)!
     *
     * @param n n个元素
     * @param m m个元素
     * @return
     */
    public static int arrangement(int n, int m) {
        int nmf = factorial(m - n);
        if (nmf == 0) {
            return 0;
        }
        return factorial(n) / nmf;
    }
}
