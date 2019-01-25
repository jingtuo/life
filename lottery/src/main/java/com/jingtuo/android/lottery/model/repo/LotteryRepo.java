package com.jingtuo.android.lottery.model.repo;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.text.TextUtils;

import com.jingtuo.android.lottery.Constants;
import com.jingtuo.android.lottery.model.db.LotteryCombination;
import com.jingtuo.android.lottery.model.db.Lottery;
import com.jingtuo.android.lottery.model.db.LotteryDao;
import com.jingtuo.android.lottery.model.db.LotteryDatabase;
import com.jingtuo.android.lottery.model.db.LotteryResult;
import com.jingtuo.android.lottery.model.db.Migration1To2;
import com.jingtuo.android.lottery.model.request.QueryCombination;
import com.jingtuo.android.lottery.model.request.LotteryService;
import com.jingtuo.android.lottery.model.request.QueryLotteryResult;
import com.jingtuo.android.lottery.model.response.LotteryResultWrapper;
import com.jingtuo.android.lottery.model.response.YiYuanResponse;
import com.jingtuo.android.lottery.util.LotteryUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import io.reactivex.Flowable;
import io.reactivex.Observable;
import lombok.Getter;
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

    @Getter
    private LotteryService lotteryService;

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
     * @param appContext
     * @return
     */
    public Observable<String> initSupportedLottery(final Context appContext) {
        return Observable.fromCallable(() -> {
            //服务接口
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addNetworkInterceptor(new NetworkInterceptor(appContext))
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://route.showapi.com/")
                    .client(httpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            lotteryService = retrofit.create(LotteryService.class);
            //数据库上
            LotteryDao lotteryDao = getLotteryDao(appContext);
            List<Lottery> lotteries = lotteryDao.querySupportedLotteries();
            if (lotteries != null && !lotteries.isEmpty()) {
                //由于易源数据平台每天请求次数有限,支持的彩票仅请求一次
                //初始化双色球的组合数据
                initSSQCombination(lotteryDao);
                return "success";
            }
            Response<YiYuanResponse<ArrayList<Lottery>>> response = lotteryService.querySupportedLotteries(LotteryUtils.createQuerySupportedLotteryFields()).execute();
            lotteries = LotteryUtils.getData(response);
            if (lotteries == null || lotteries.isEmpty()) {
                //未知情况,可能是网络错误或者易源数据平台出现问题
                return "success";
            }
            lotteryDao.insertLotteries(lotteries);
            return "success";
        });
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
            ArrayList<LotteryResult> lotteryResults = LotteryUtils.getData(lotteryService.queryLotteryResults(LotteryUtils.createQueryLotteryResultFields(lottery.getCode(), endTime)).execute());
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
     *
     * @param appContext
     * @param text
     * @param type
     * @param hot
     * @param high
     * @return
     */
    public Flowable<List<Lottery>> querySupportedLotteries(Context appContext, String text, String type, boolean hot, boolean high) {
        LotteryDao lotteryDao = getLotteryDao(appContext);
        if (TextUtils.isEmpty(type)) {
            return lotteryDao.querySupportedLotteries(text, hot ? Constants.TRUE : Constants.FALSE, high ? Constants.TRUE : Constants.FALSE);
        }
        return lotteryDao.querySupportedLotteries(text, type, hot ? Constants.TRUE : Constants.FALSE, high ? Constants.TRUE : Constants.FALSE);
    }

    /**
     *
     * @param appContext
     * @return
     */
    public Flowable<List<String>> queryLotteryTypes(Context appContext) {
        LotteryDao lotteryDao = getLotteryDao(appContext);
        return lotteryDao.queryLotteryTypes();
    }


    /**
     * @param name
     * @return
     */
    public Flowable<List<Lottery>> querySupportedLotteries(Context appContext, String name) {
        LotteryDao lotteryDao = getLotteryDao(appContext);
        return lotteryDao.querySupportedLotteries(name);
    }

    /**
     * 查询彩票结果
     * @param appContext
     * @param lottery
     * @param time
     * @return
     */
    public Observable<LotteryResultWrapper> queryLotteryResults(Context appContext, Lottery lottery, String time) {
        return Observable.fromCallable(new QueryLotteryResult(appContext, lottery.getCode(), time));
    }

    /**
     * 查询彩票组合
     * @param appContext
     * @param code
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Observable<List<LotteryCombination>> queryCombinations(Context appContext, String code, int pageNo, int pageSize) {
        return Observable.fromCallable(new QueryCombination(appContext, code, pageNo, pageSize));
    }

    /**
     * 双色球规则:
     * “双色球”每注投注号码由6个红色球号码和1个蓝色球号码组成。红色球号码从1--33中选择；蓝色球号码从1--16中选择。
     * “双色球”每注2元。
     * “双色球”采取全国统一奖池计奖。
     * “双色球”每周销售三期，期号以开奖日界定，按日历年度编排。
     * 此处采用组合进行统计分析
     */
    private void initSSQCombination(LotteryDao lotteryDao) {
        int count = lotteryDao.queryCombination(Constants.SSQ);
        if (count > 0) {
            //已经初始化
            return;
        }
        int redBallMaxNo = 33;
        int blueBallMaxNo = 16;
        List<String> redBalls = new ArrayList<>();
        for (int i = 0; i < redBallMaxNo; i++) {
            redBalls.add(String.format(Locale.getDefault(), "%02d", i + 1));
        }
        List<String> ballGroups = new ArrayList<>();
        combine(redBalls, 0, 6, ballGroups, "");

        List<String> blueBalls = new ArrayList<>();
        for (int i = 0; i < blueBallMaxNo; i++) {
            blueBalls.add(String.format(Locale.getDefault(), "%02d", i + 1));
        }

        List<LotteryCombination> combinations = new ArrayList<>();
        for (String ballGroup : ballGroups) {
            for (String blueBall : blueBalls) {
                LotteryCombination combination = new LotteryCombination(Constants.SSQ, ballGroup + Constants.SPLIT_PLUS + blueBall);
                combinations.add(combination);
            }
        }
        lotteryDao.insertCombination(combinations);
    }


    /**
     * 组合
     *
     * @param balls
     * @param index
     * @param count
     * @param ballGroups
     * @param ballGroup
     */
    private void combine(List<String> balls, int index, int count, List<String> ballGroups, String ballGroup) {
        if (balls == null || balls.isEmpty()) {
            return;
        }
        int size = balls.size();
        if (count <= 0 || count > size) {
            //取零个元素进行组合,没有意义
            //取大于元素总个数的数量进行组合,没有意义
            return;
        }
        if (count == size) {
            if (index != 0) {
                return;
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < size; i++) {
                builder.append(balls.get(i));
                if (index != size - 1) {
                    builder.append(Constants.SPLIT_COMMA);
                }
            }
            ballGroup = builder.toString();
            ballGroups.add(ballGroup);
            return;
        }

        if (count == 1) {
            for (int i = index; i < size; i++) {
                String ball = balls.get(i);
                StringBuilder builder = new StringBuilder();
                if (TextUtils.isEmpty(ballGroup)) {
                    builder.append(ball);
                } else {
                    builder.append(ballGroup);
                    builder.append(Constants.SPLIT_COMMA);
                    builder.append(ball);
                }
                ballGroups.add(builder.toString());
            }
            return;
        }


        /**
         * count个元素,最大起始索引是size - count;
         */
        int fSize = size - count;
        for (int i = index; i <= fSize; i++) {
            String ball = balls.get(i);
            StringBuilder builder = new StringBuilder();
            if (TextUtils.isEmpty(ballGroup)) {
                builder.append(ball);
            } else {
                builder.append(ballGroup);
                builder.append(Constants.SPLIT_COMMA);
                builder.append(ball);
            }
            combine(balls, i + 1, count - 1, ballGroups, builder.toString());
        }
    }

    /**
     * 多线程之间不能共享一个database对象,不能共享一个dao对象
     *
     * @param appContext
     * @return
     */
    public LotteryDao getLotteryDao(Context appContext) {
        LotteryDatabase database = Room.databaseBuilder(appContext, LotteryDatabase.class, Constants.LOTTERY)
                .addMigrations(new Migration1To2(1, 2))
                .fallbackToDestructiveMigration()
                .build();
        return database.lotteryDao();
    }
}
