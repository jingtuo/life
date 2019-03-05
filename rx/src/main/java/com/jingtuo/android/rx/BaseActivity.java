package com.jingtuo.android.rx;

import android.support.v7.app.AppCompatActivity;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


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

    protected <T> Observable<T> config(Observable<T> observable){
        return observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
