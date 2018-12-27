package com.jingtuo.android.lottery.model.repo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jingtuo.android.lottery.R;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 网络拦截器
 * @author JingTuo
 */
public class NetworkInterceptor implements Interceptor {

    private Context appContext;

    public NetworkInterceptor(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (isConnected()) {
            return chain.proceed(chain.request());
        }
        return new Response.Builder()
                .code(-200)
                .message(appContext.getString(R.string.network_seems_to_be_lost))
                .build();
    }


    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
