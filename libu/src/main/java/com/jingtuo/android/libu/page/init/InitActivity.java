package com.jingtuo.android.libu.page.init;

import android.os.Bundle;

import com.jingtuo.android.libu.R;
import com.jingtuo.android.libu.model.repo.Repository;
import com.jingtuo.android.libu.page.login.LoginActivity;
import com.jingtuo.android.rx.BaseActivity;
import com.jingtuo.android.util.IntentUtils;

import io.reactivex.functions.Consumer;

/**
 * 初始化页面
 * @author JingTuo
 */
public class InitActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDisposable.add(config(Repository.getInstance().init(this))
                .subscribe(aClass -> {
                    IntentUtils.startActivityWithNormal(InitActivity.this, aClass);
                    finish();
                }, throwable -> {
                    IntentUtils.startActivityWithNormal(InitActivity.this, LoginActivity.class);
                    finish();
                }));
    }
}
