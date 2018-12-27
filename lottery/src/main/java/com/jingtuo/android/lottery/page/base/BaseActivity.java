package com.jingtuo.android.lottery.page.base;

import android.support.v7.app.AppCompatActivity;

import io.reactivex.disposables.CompositeDisposable;

/**
 * @author JingTuo
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected final CompositeDisposable mDisposable = new CompositeDisposable();


    @Override
    protected void onPause() {
        super.onPause();
        mDisposable.clear();
    }
}
