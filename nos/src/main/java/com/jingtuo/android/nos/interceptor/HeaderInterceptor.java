package com.jingtuo.android.nos.interceptor;

import android.text.TextUtils;
import android.util.Base64;

import com.jingtuo.android.nos.NosClient;
import com.jingtuo.android.util.LogUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author JingTuo
 */
public class HeaderInterceptor implements Interceptor {

    private static final String TAG = HeaderInterceptor.class.getSimpleName();

    @Override
    public Response intercept(Chain chain) throws IOException {
        String accessKey = NosClient.getInstance().getAccessKey();
        String secretKey = NosClient.getInstance().getSecretKey();

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dateStr = dateFormat.format(date);

        Request originRequest = chain.request();

        Headers headers = originRequest.headers();
        String contentMd5 = "";
        String contentType = "";
        String canonicalizedHeaders;
        String canonicalizedResource = "";

        List<String> nosHeaders = new ArrayList<>();
        Iterator<String> iterator = headers.names().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (NosClient.HEADER_CONTENT_MD5.equals(key)) {
                contentMd5 = headers.get(key);
            }
            if (NosClient.HEADER_CONTENT_TYPE.equals(key)) {
                contentType = headers.get(key);
            }
            if (NosClient.HEADER_RESOURCE.equals(key)) {
                canonicalizedResource = headers.get(key);
            }

            if (key.startsWith(NosClient.HEADER_X_NOS_PREFIX)) {
                List<String> values = headers.values(key);
                StringBuilder builder = new StringBuilder(key.toLowerCase(Locale.getDefault()).trim());
                builder.append(":");
                for (String value : values) {
                    builder.append(value.trim()).append(",");
                }
                String result = builder.toString();
                result = result.substring(0, result.length() - 1);
                nosHeaders.add(result);
            }
        }
        nosHeaders.add(NosClient.HEADER_ENTITY_TYPE + ":json");

        /**
         * 根据body设置content-type
         */
        if (TextUtils.isEmpty(contentType)) {
            if (originRequest.body() != null) {
                MediaType mediaType = originRequest.body().contentType();
                if (mediaType != null) {
                    contentType = mediaType.toString();
                }
            }
        }

        Collections.sort(nosHeaders, String::compareTo);

        StringBuilder builder = new StringBuilder();
        for (String header : nosHeaders) {
            builder.append(header).append("\n");
        }
        canonicalizedHeaders = builder.toString();

        String source = originRequest.method() + "\n"
                + contentMd5 + "\n"
                + contentType + "\n"
                + dateStr + "\n" + canonicalizedHeaders + canonicalizedResource;

        String signature = null;

        try {
            Mac hmacSHA256 = Mac.getInstance(NosClient.ALGORITHM_HMAC_SHA256);
            SecretKeySpec var10 = new SecretKeySpec(secretKey.getBytes(), NosClient.ALGORITHM_HMAC_SHA256);
            hmacSHA256.init(var10);
            byte[] data = hmacSHA256.doFinal(source.getBytes());
            signature = Base64.encodeToString(data, Base64.NO_WRAP);
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
        String authorization = "NOS " + accessKey + ":" + signature;

        /**
         * 官方文档要求header中增加Host,OkHttpClient自动会在header中添加Host:域名
         */
        Request request = originRequest.newBuilder()
                .header(NosClient.HEADER_AUTHORIZATION, authorization)
                .header(NosClient.HEADER_DATE, dateStr)
                .header(NosClient.HEADER_ENTITY_TYPE, "json")
                .build();
        return chain.proceed(request);
    }
}
