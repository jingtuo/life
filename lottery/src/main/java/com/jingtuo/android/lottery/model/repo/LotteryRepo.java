package com.jingtuo.android.lottery.model.repo;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.text.TextUtils;

import com.jingtuo.android.lottery.Constants;
import com.jingtuo.android.lottery.model.db.Lottery;
import com.jingtuo.android.lottery.model.db.LotteryDao;
import com.jingtuo.android.lottery.model.db.LotteryDatabase;
import com.jingtuo.android.lottery.model.db.LotteryResult;
import com.jingtuo.android.lottery.model.request.LotteryService;
import com.jingtuo.android.lottery.model.response.YiYuanResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Completable;

import io.reactivex.Flowable;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 彩票仓库
 *
 * @author JingTuo
 */
public class LotteryRepo {


    public static LotteryRepo getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private LotteryRepo() {
    }

    public static class SingletonHolder {
        private static final LotteryRepo INSTANCE = new LotteryRepo();

        private SingletonHolder() {
        }
    }

    /**
     * 初始化彩票数据
     *
     * @param context
     */
    public Completable initSupportedLottery(final Context context) {
        return Completable.fromAction(() -> {
            LotteryDatabase database = Room.databaseBuilder(context, LotteryDatabase.class, "lottery").build();
            LotteryDao lotteryDao = database.lotteryDao();
            List<Lottery> lotteries = lotteryDao.querySupportedLotteries();
            if (lotteries != null && !lotteries.isEmpty()) {
                //由于易源数据平台每天请求次数有限,支持的彩票仅请求一次
                return;
            }
            LotteryService service = createLotteryService(context.getApplicationContext());
            Response<YiYuanResponse<ArrayList<Lottery>>> response = service.querySupportedLotteries(createQuerySupportedLotteryFields()).execute();
            lotteries = getData(response);
            if (lotteries == null || lotteries.isEmpty()) {
                //未知情况,可能是网络错误或者易源数据平台出现问题
                return;
            }
            lotteryDao.insertLotteries(lotteries);
        });
    }

    /**
     * 创建彩票服务
     *
     * @param context
     * @return
     */
    private LotteryService createLotteryService(Context context) {
        OkHttpClient httpClient = new OkHttpClient
                .Builder()
                .addNetworkInterceptor(new NetworkInterceptor(context.getApplicationContext()))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://route.showapi.com/")
                .client(httpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(LotteryService.class);
    }

    /**
     * @param response
     * @param <T>
     * @return
     */
    private <T> T getData(Response<YiYuanResponse<T>> response) {
        if (response == null || 200 != response.code() || response.body() == null || 0 != response.body().getCode()) {
            return null;
        }
        return response.body().getBody().getResult();
    }


    /**
     *
     */
    private void insertLotteryResults(Lottery lottery, LotteryService lotteryService, LotteryDao lotteryDao) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String endTime = dateFormat.format(new Date(System.currentTimeMillis()));
        Calendar calendar = Calendar.getInstance();
        Date date;
        while (true) {
            ArrayList<LotteryResult> lotteryResults = getData(lotteryService.queryLotteryResults(createQueryLotteryResultFields(lottery.getCode(), endTime)).execute());
            if (lotteryResults == null) {
                lotteryResults = new ArrayList<>();
            }
            int size = lotteryResults.size();
            long[] ids = lotteryDao.insertLotteryResults(lotteryResults);
            int count = insertCount(ids);
            if (count <= 0 || size < 50) {
                /**
                 * count<=0表示服务器请求的数据,本地都有了,不需要再请求了
                 * size<=50表示服务器有的数据不超过50,不需要再请求了
                 */
                break;
            }
            if (count == size) {
                //没有服务器的数据,全部是新数据
                endTime = lotteryResults.get(size - 1).getTime();
            } else {
                //本地有部分服务器的数据
                LotteryResult lotteryResult = lotteryDao.queryLotteryResults(lottery.getCode(), "ASC", 1, 1).get(0);
                endTime = lotteryResult.getTime();
            }
            date = dateFormat.parse(endTime);
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, -1);
            endTime = dateFormat.format(calendar.getTime());
        }
    }

    /**
     * @param ids
     * @return
     */
    private int insertCount(long[] ids) {
        if (ids == null) {
            return 0;
        }
        int count = 0;
        for (long id : ids) {
            if (id != -1) {
                count++;
            }
        }
        return count;
    }

    /**
     * 创建查询支持股票的请求参数
     *
     * @return
     */
    private Map<String, String> createQuerySupportedLotteryFields() {
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
    private Map<String, String> createQueryLotteryResultFields(String code, String endTime) {
        Map<String, String> map = createQuerySupportedLotteryFields();
        map.put("code", code);
        map.put("endTime", endTime);
        map.put("count", "50");
        return map;
    }

    /**
     * @param context
     * @param text
     * @param type
     * @param hot
     * @param high
     * @return
     */
    public Flowable<List<Lottery>> querySupportedLotteries(Context context, String text, String type, boolean hot, boolean high) {
        LotteryDatabase database = Room.databaseBuilder(context, LotteryDatabase.class, "lottery").build();
        LotteryDao lotteryDao = database.lotteryDao();
        if (TextUtils.isEmpty(type)) {
            return lotteryDao.querySupportedLotteries(text, hot ? Constants.TRUE : Constants.FALSE, high ? Constants.TRUE : Constants.FALSE);
        }
        return lotteryDao.querySupportedLotteries(text, type, hot ? Constants.TRUE : Constants.FALSE, high ? Constants.TRUE : Constants.FALSE);
    }


    public Flowable<List<String>> queryLotteryTypes(Context context) {
        LotteryDatabase database = Room.databaseBuilder(context, LotteryDatabase.class, "lottery").build();
        LotteryDao lotteryDao = database.lotteryDao();
        return lotteryDao.queryLotteryTypes();
    }


    /**
     * @param context
     * @param name
     * @return
     */
    public Flowable<List<Lottery>> querySupportedLotteries(Context context, String name) {
        LotteryDatabase database = Room.databaseBuilder(context, LotteryDatabase.class, "lottery").build();
        LotteryDao lotteryDao = database.lotteryDao();
        return lotteryDao.querySupportedLotteries(name);
    }
}
