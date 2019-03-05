package com.jingtuo.android.nos.service;

import com.jingtuo.android.nos.NosClient;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * 创建对象
 * @author JingTuo
 */
public interface CreateObjectService {

    /**
     * 创建对象
     * @return
     */
    @PUT("/{objectName}")
    Call<Void> createObject(@Path(value = "objectName", encoded = true) String objectName, @Header(NosClient.HEADER_RESOURCE) String resource);
}
