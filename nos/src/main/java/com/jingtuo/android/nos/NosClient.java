package com.jingtuo.android.nos;

import com.google.gson.Gson;
import com.jingtuo.android.nos.converter.NosConverterFactory;
import com.jingtuo.android.nos.interceptor.HeaderInterceptor;
import com.jingtuo.android.nos.model.response.Bucket;
import com.jingtuo.android.nos.model.response.ListBucketsWrapper;
import com.jingtuo.android.nos.service.BucketExistsService;
import com.jingtuo.android.nos.service.CreateBucketService;
import com.jingtuo.android.nos.service.CreateObjectService;
import com.jingtuo.android.nos.service.DeleteBucketService;
import com.jingtuo.android.nos.service.ListBucketService;
import com.jingtuo.android.nos.service.ObjectExistsService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * {@link NosClient#accessKey}和{@link NosClient#secretKey}必须设置
 *
 * @author JingTuo
 */
public class NosClient {

    public static final String ENPOINT = "nos-eastchina1.126.net";

    public static final String HEADER_AUTHORIZATION = "Authorization";

    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    public static final String HEADER_CONTENT_MD5 = "Content-MD5";

    public static final String HEADER_HOST = "Host";

    public static final String HEADER_DATE = "Date";

    public static final String HEADER_ACL = "x-nos-acl";

    public static final String HEADER_ENTITY_TYPE = "x-nos-entity-type";

    public static final String HEADER_X_NOS_PREFIX = "x-nos-";

    public static final String ALGORITHM_HMAC_SHA256 = "HmacSHA256";

    public static final String HEADER_RESOURCE = "x-nos-resource";

    public static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";

    public static final String FORMAT_DATE_TIME = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";

    public static final int CODE_SUCCESS = 200;
    public static final int CODE_NOT_FOUND = 404;

    public static final String XML_NAMESPACE = "http://nos.netease.com//";

    private static final String SCHEME = "http://";

    @Setter
    @Getter
    private String accessKey;

    @Setter
    @Getter
    private String secretKey;

    private OkHttpClient mHttpClient;

    private Retrofit mRetrofit;

    private NosClient() {

    }

    public static NosClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final NosClient INSTANCE = new NosClient();
    }

    /**
     * 创建桶
     *
     * @param name
     * @param accessControl
     * @param region
     * @return 200-创建成功,409-表示已经存在,但又包含两种情况:是自己的和不是自己的
     * @throws IOException
     */
    public int createBucket(String name, AccessControl accessControl, Region region, boolean deduplicate) throws IOException {
        Retrofit retrofit = getRetrofit(SCHEME + name + "." + ENPOINT + "/");
        CreateBucketService service = retrofit.create(CreateBucketService.class);
        /**
         * 不想引入第三方xml库,直接拼出xml
         */
        XmlWriter xml = new XmlWriter();
        xml.start("CreateBucketConfiguration", "xmlns", XML_NAMESPACE);
        if (region != null) {
            xml.start("LocationConstraint").value(region.toString()).end();
        }
        xml.start("ObjectDeduplicate").value(String.valueOf(deduplicate)).end();
        xml.end();
        Call<Void> call = service.create("/" + name + "/", accessControl.toString(), xml.toString());
        Response<Void> response = call.execute();
        return response.code();
    }

    /**
     * 查看桶是否存在
     *
     * @param name 桶名称
     * @return 200-存在,404-不存在,其他属于异常情况,如没有权限访问桶
     * @throws IOException
     */
    public int bucketExists(String name) throws IOException {
        Retrofit retrofit = getRetrofit(SCHEME + name + "." + ENPOINT + "/");
        BucketExistsService service = retrofit.create(BucketExistsService.class);
        Call<Void> call = service.exists("/" + name + "/");
        Response<Void> response = call.execute();
        return response.code();
    }


    /**
     * 列出所有的桶
     *
     * @return
     * @throws IOException
     */
    public List<Bucket> listBuckets() throws IOException {
        Retrofit retrofit = getRetrofit(SCHEME + ENPOINT + "/");
        ListBucketService service = retrofit.create(ListBucketService.class);
        Call<ListBucketsWrapper> call = service.listBuckets();
        Response<ListBucketsWrapper> response = call.execute();
        if (CODE_SUCCESS == response.code()) {
            ListBucketsWrapper wrapper = response.body();
            if (wrapper != null && wrapper.getListBuckets() != null && wrapper.getListBuckets().getBuckets() != null) {
                return wrapper.getListBuckets().getBuckets();
            }
        }
        return Collections.emptyList();
    }


    /**
     * 删除桶
     *
     * @param name 桶名称
     * @return 200-存在,404-不存在,其他属于异常情况,如没有权限访问桶
     * @throws IOException
     */
    public int deleteBucket(String name) throws IOException {
        Retrofit retrofit = getRetrofit(SCHEME + name + "." + ENPOINT + "/");
        DeleteBucketService service = retrofit.create(DeleteBucketService.class);
        Call<Void> call = service.delete("/" + name + "/");
        Response<Void> response = call.execute();
        return response.code();
    }

    /**
     * @return
     */
    private OkHttpClient getHttpClient() {
        if (mHttpClient == null) {
            mHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new HeaderInterceptor())
                    .build();
        }
        return mHttpClient;
    }

    /**
     * @param baseUrl
     * @return
     */
    private Retrofit getRetrofit(String baseUrl) {
        Retrofit.Builder builder;
        if (mRetrofit == null) {
            builder = new Retrofit.Builder()
                    .client(getHttpClient())
                    .addConverterFactory(new NosConverterFactory(new Gson()));
        } else {
            builder = mRetrofit.newBuilder();
        }
        builder.baseUrl(baseUrl);
        mRetrofit = builder.build();
        return mRetrofit;
    }

    /**
     * 创建对象
     *
     * @param bucketName
     * @param objectName
     */
    public boolean createObject(String bucketName, String objectName) throws IOException {
        Retrofit retrofit = getRetrofit(SCHEME + bucketName + "." + ENPOINT + "/");
        CreateObjectService service = retrofit.create(CreateObjectService.class);

        Call<Void> call = service.createObject(objectName, "/" + bucketName + "/" + objectName);
        Response<Void> response = call.execute();
        return CODE_SUCCESS == response.code();
    }


    /**
     * 创建对象
     *
     * @param bucketName
     * @param objectName
     */
    public boolean objectExits(String bucketName, String objectName) throws IOException {
        Retrofit retrofit = getRetrofit(SCHEME + bucketName + "." + ENPOINT + "/");
        ObjectExistsService service = retrofit.create(ObjectExistsService.class);

        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE_TIME, Locale.US);
        Call<Void> call = service.exists(objectName, "/" + bucketName + "/" + objectName, dateFormat.format(new Date(System.currentTimeMillis())));
        Response<Void> response = call.execute();
        return CODE_SUCCESS == response.code();
    }
}
