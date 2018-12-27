package com.jingtuo.android.lottery.model.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 彩票
 * @author JingTuo
 */
@Entity
@Data
public class Lottery {

    /**
     * 彩票种类
     * 如：k3
     */
    private String series;

    /**
     * 所属地区
     * 如：上海
     */
    private String area;

    /**
     * 彩票分类
     * 如：福彩
     */
    private String issuer;

    /**
     * 每天开奖次数，零为不定
     * 如：85
     */
    private String times;

    /**
     * true或false，表式是否热门彩票
     */
    private String hots;

    /**
     * 是否高频彩票
     */
    private String high;

    /**
     * 彩票的code，查询彩票开奖信息时用
     * 如：shk3
     */
    @NonNull
    @PrimaryKey
    private String code;

    /**
     * 彩票描述
     */
    private String notes;

    /**
     * 彩票名称
     */
    @SerializedName("descr")
    private String descr;

    public Lottery(@NonNull String code) {
        this.code = code;
    }
}
