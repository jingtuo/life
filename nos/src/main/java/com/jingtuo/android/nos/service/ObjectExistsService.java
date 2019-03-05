package com.jingtuo.android.nos.service;

import com.jingtuo.android.nos.NosClient;

import retrofit2.Call;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * 检测对象是否存在
 *
 * @author JingTuo
 */
public interface ObjectExistsService {

    /**
     * 检测对象是否存在,因为objectName存在"/",需要encoded
     * @param path
     * @param resource
     * @param dateStr
     * @return
     */
    @HEAD("/{path}")
    Call<Void> exists(@Path(value = "path", encoded = true) String path,
                      @Header(NosClient.HEADER_RESOURCE) String resource,
                      @Header(NosClient.HEADER_IF_MODIFIED_SINCE) String dateStr);
}
