package com.jingtuo.android.nos.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lombok.Data;

@Data
public class ListBuckets {
    @SerializedName("Owner")
    private BucketOwner owner;

    @SerializedName("Buckets")
    private ArrayList<Bucket> buckets;
}
