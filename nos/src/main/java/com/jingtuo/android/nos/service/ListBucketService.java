package com.jingtuo.android.nos.service;

import com.jingtuo.android.nos.NosClient;
import com.jingtuo.android.nos.model.response.ListBucketsWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * 列出所有的桶
 *
 * @author JingTuo
 */
public interface ListBucketService {

    /**
     * @return
     */
    @Headers({
            NosClient.HEADER_RESOURCE + ":/"
    })
    @GET("/")
    Call<ListBucketsWrapper> listBuckets();
}
