package com.jingtuo.android.nos.service;

import com.jingtuo.android.nos.NosClient;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.PUT;

/**
 * 删除Bucket
 *
 * @author JingTuo
 */
public interface DeleteBucketService {

    /**
     * @param resource
     * @return
     */
    @DELETE("/")
    Call<Void> delete(@Header(NosClient.HEADER_RESOURCE) String resource);
}
