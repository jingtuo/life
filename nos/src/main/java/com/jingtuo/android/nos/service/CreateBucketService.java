package com.jingtuo.android.nos.service;

import com.jingtuo.android.nos.NosClient;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;

/**
 * 创建Bucket
 * @author JingTuo
 */
public interface CreateBucketService {

    /**
     *
     * @param resource
     * @param accessControl
     * @param body
     * @return
     */
    @PUT("/")
    Call<Void> create(@Header(NosClient.HEADER_RESOURCE) String resource, @Header(NosClient.HEADER_ACL) String accessControl, @Body String body);
}
