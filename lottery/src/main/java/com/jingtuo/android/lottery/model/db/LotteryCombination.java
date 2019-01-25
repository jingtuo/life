package com.jingtuo.android.lottery.model.db;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.Data;

/**
 * 组合
 * @author JingTuo
 */
@Data
@Entity(primaryKeys = {"code", "combination"})
public class LotteryCombination {

    /**
     * 彩票代码
     */
    @NonNull
    private String code;

    /**
     * 组合
     */
    @NonNull
    private String combination;

    /**
     * 概率
     */
    private float probability;

    public LotteryCombination(@NonNull String code, @NonNull String combination) {
        this.code = code;
        this.combination = combination;
    }
}
