package com.jingtuo.android.libu.model.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.jingtuo.android.libu.Constants;
import com.jingtuo.android.libu.model.repo.Repository;
import com.jingtuo.android.libu.page.event.list.EventListActivity;
import com.jingtuo.android.libu.page.login.LoginActivity;
import com.jingtuo.android.nos.NosClient;

import java.util.concurrent.Callable;

/**
 * 登录
 * @author JingTuo
 */
public class LoginService implements Callable<Boolean> {

    private Context context;

    private String userId;

    public LoginService(Context context, String userId) {
        this.context = context;
        this.userId = userId;
    }

    @Override
    public Boolean call() throws Exception {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        boolean saveToLocalSuccess = preferences.edit().putString(Constants.USER_ID, this.userId).commit();
        if (!saveToLocalSuccess) {
            return false;
        }
        boolean success = Repository.createUserIdFolder(context, userId);
        if (success) {
            String objectName = "life/libu/" + userId;
            if (NosClient.getInstance().objectExits(Constants.BUCKET_NAME, objectName)) {
                return true;
            }
            return NosClient.getInstance().createObject(Constants.BUCKET_NAME, objectName);
        }
        return false;
    }
}
