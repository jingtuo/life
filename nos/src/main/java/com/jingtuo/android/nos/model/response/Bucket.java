package com.jingtuo.android.nos.model.response;


import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 网易云-桶,所有的云对象存储必须放到“桶”下
 * @author JingTuo
 */
@Data
public class Bucket {

    /**
     * 桶名称
     */
    @SerializedName("Name")
    private String name;

    /**
     * 创建日期
     */
    @SerializedName("CreationDate")
    private String creationDate;
}
