package com.jingtuo.android.lottery.page.init;

import android.content.Intent;
import android.os.Bundle;

import com.jingtuo.android.lottery.R;
import com.jingtuo.android.lottery.model.repo.LotteryRepo;
import com.jingtuo.android.lottery.page.home.HomeActivity;
import com.jingtuo.android.lottery.util.SimpleLog;
import com.jingtuo.android.rx.BaseActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author JingTuo
 */
public class InitActivity extends BaseActivity {

    private static final String TAG = InitActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        mDisposable.add(LotteryRepo.getInstance().initSupportedLottery(this)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> enterHome(), throwable -> {
                    SimpleLog.e(TAG, throwable.getMessage());
                    enterHome();
                }));
    }

    /**
     * 进入主页
     */
    private void enterHome() {
        Intent intent = new Intent();
        intent.setClass(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}
