package com.jingtuo.android.lottery.model.db;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import lombok.Data;

/**
 * 彩票结果
 *
 * @author JingTuo
 */
@Entity(primaryKeys = {"code", "expect"})
@Data


public class LotteryResult {

    /**
     * 彩票名称
     * 如：双色球
     */
    private String name;

    /**
     * 彩票类型编码
     * 如：ssq
     */
    @NonNull
    private String code;

    /**
     * 多少区开奖编号
     * 如：2015022743
     */
    @NonNull
    private String expect;

    /**
     * 开奖时间字符串
     * 如：2015-02-27 15:41:30
     */
    private String time;

    /**
     * 开奖时间戳
     * 如：1425022890000
     */
    private String timestamp;
    /**
     * 中将号码
     * 如：03,04,11,09,05
     */
    private String openCode;

    public LotteryResult(@NonNull String code, @NonNull String expect) {
        this.code = code;
        this.expect = expect;
    }
}
