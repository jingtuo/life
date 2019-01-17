package com.jingtuo.android.lottery.model.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;

/**
 * @author JingTuo
 */
@Dao
public interface LotteryDao {

    /**
     * 查询支持的彩票
     *
     * @return
     */
    @Query("SELECT * FROM lottery")
    List<Lottery> querySupportedLotteries();

    /**
     * 查询彩票开奖结果
     *
     * @param code
     * @param orderBy
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Query("SELECT * FROM LotteryResult WHERE code=:code ORDER BY time (:orderBy) LIMIT ((:pageNo - 1) * :pageSize), :pageSize")
    List<LotteryResult> queryLotteryResults(String code, String orderBy, int pageNo, int pageSize);

    /**
     * 插入彩票开奖结果
     *
     * @param results
     * @return
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insertLotteryResults(List<LotteryResult> results);


    /**
     * 插入彩票
     *
     * @param lotteries
     * @return
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insertLotteries(List<Lottery> lotteries);


    /**
     * 查询支持的彩票
     *
     * @param name
     * @param type
     * @param hots
     * @param high
     * @return
     */
    @Query("SELECT * FROM lottery WHERE descr LIKE '%'||:name||'%' and issuer = :type and hots = :hots and high = :high")
    Flowable<List<Lottery>> querySupportedLotteries(String name, String type, String hots, String high);


    /**
     * 查询支持的彩票
     *
     * @param text
     * @param hots
     * @param high
     * @return
     */
    @Query("SELECT * FROM lottery WHERE (descr LIKE '%'||:text||'%' OR notes LIKE '%'||:text||'%' OR area LIKE '%'||:text||'%') and hots = :hots and high = :high")
    Flowable<List<Lottery>> querySupportedLotteries(String text, String hots, String high);

    /**
     * 查询彩票类型
     *
     * @return
     */
    @Query("SELECT issuer FROM lottery GROUP BY issuer ORDER BY issuer")
    Flowable<List<String>> queryLotteryTypes();


    /**
     * 查询支持的彩票
     *
     * @param text
     * @return
     */
    @Query("SELECT * FROM lottery WHERE descr LIKE '%'||:text||'%' OR notes LIKE '%'||:text||'%' OR area LIKE '%'||:text||'%'")
    Flowable<List<Lottery>> querySupportedLotteries(String text);


    /**
     * 查询彩票开奖结果
     * @param code
     * @param time
     * @return
     */
    @Query("SELECT * FROM LotteryResult WHERE code=:code AND time <= :time ORDER BY time DESC LIMIT 0, 50")
    List<LotteryResult> queryLotteryResults(String code, String time);
}
