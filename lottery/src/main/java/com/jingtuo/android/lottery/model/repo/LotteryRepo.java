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

import io.reactivex.Completable;

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

    @Getter
    private LotteryDao lotteryDao;

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
    public Completable initSupportedLottery(final Context appContext) {
        return Completable.fromAction(() -> {
            //服务接口
            OkHttpClient httpClient = new OkHttpClient
                    .Builder()
                    .addNetworkInterceptor(new NetworkInterceptor(appContext))
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://route.showapi.com/")
                    .client(httpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            lotteryService = retrofit.create(LotteryService.class);
            //数据库上
            LotteryDatabase database = Room.databaseBuilder(appContext, LotteryDatabase.class, Constants.LOTTERY).build();
            lotteryDao = database.lotteryDao();
            List<Lottery> lotteries = lotteryDao.querySupportedLotteries();
            if (lotteries != null && !lotteries.isEmpty()) {
                //由于易源数据平台每天请求次数有限,支持的彩票仅请求一次
                return;
            }
            Response<YiYuanResponse<ArrayList<Lottery>>> response = lotteryService.querySupportedLotteries(LotteryUtils.createQuerySupportedLotteryFields()).execute();
            lotteries = LotteryUtils.getData(response);
            if (lotteries == null || lotteries.isEmpty()) {
                //未知情况,可能是网络错误或者易源数据平台出现问题
                return;
            }
            lotteryDao.insertLotteries(lotteries);
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

    /**
     * @param lottery
     * @param time
     * @return
     */
    public Observable<LotteryResultWrapper> queryLotteryResults(Lottery lottery, String time) {
        return Observable.fromCallable(new QueryLotteryResult(lottery.getCode(), time));
    }
}
