package com.jingtuo.android.nos.model.response;


import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ListBucketsWrapper {
    @SerializedName("ListAllMyBucketsResult")
    private ListBuckets listBuckets;
}
