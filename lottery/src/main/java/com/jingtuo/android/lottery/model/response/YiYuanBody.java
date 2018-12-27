package com.jingtuo.android.lottery.model.response;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 易源数据的response的body
 * @author JingTuo
 */
@Data
public class YiYuanBody<T> {

    /**
     * 扣费标识,0表示操作成功并扣费,其他值不扣费
     */
    @SerializedName("ret_code")
    private int code;

    /**
     * 数据
     */
    private T result;
}
