package com.jingtuo.android.libu.model.service;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.jingtuo.android.libu.Constants;
import com.jingtuo.android.libu.model.repo.Repository;
import com.jingtuo.android.libu.page.event.list.EventListActivity;
import com.jingtuo.android.libu.page.login.LoginActivity;
import com.jingtuo.android.nos.NosClient;

import java.util.concurrent.Callable;

/**
 * 初始化
 *
 * @author JingTuo
 */
public class InitService implements Callable<Class<? extends Activity>> {

    private Context context;

    public InitService(Context context) {
        this.context = context;
    }

    @Override
    public Class<? extends Activity> call() throws Exception {
        NosClient.getInstance().setAccessKey(Constants.ACCESS_KEY);
        NosClient.getInstance().setSecretKey(Constants.SECRET_KEY);
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        String userId = preferences.getString(Constants.USER_ID, "");
        if (TextUtils.isEmpty(userId)) {
            //未登录,进入登录页面
            return LoginActivity.class;
        }
        boolean success = Repository.createUserIdFolder(context, userId);
        if (success) {
            String objectName = "libu";
            if (NosClient.getInstance().objectExits(Constants.BUCKET_NAME, objectName)) {
                return EventListActivity.class;
            }
            boolean createObjSuccess = NosClient.getInstance().createObject(Constants.BUCKET_NAME, objectName);
            return createObjSuccess ? EventListActivity.class : LoginActivity.class;
        }
        return LoginActivity.class;
    }
}
