package com.jingtuo.android.nos.model.response;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 *
 * @author JingTuo
 */
@Data
public class BucketOwner {

    @SerializedName("ID")
    private String id;

    @SerializedName("DisplayName")
    private String displayName;
}
