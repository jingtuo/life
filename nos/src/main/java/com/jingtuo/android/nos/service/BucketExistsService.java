package com.jingtuo.android.nos.service;

import com.jingtuo.android.nos.NosClient;

import retrofit2.Call;
import retrofit2.http.HEAD;
import retrofit2.http.Header;

/**
 * 检测Bucket是否存在
 * @author JingTuo
 */
public interface BucketExistsService {

    /**
     * 是否存在
     *
     * @param name 格式:/bucketName/
     * @return
     */
    @HEAD("/")
    Call<Void> exists(@Header(NosClient.HEADER_RESOURCE) String name);
}
