package com.jingtuo.android.lottery.page.lottery.combination;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.jingtuo.android.lottery.Constants;
import com.jingtuo.android.lottery.R;
import com.jingtuo.android.lottery.model.db.Lottery;
import com.jingtuo.android.lottery.model.repo.LotteryRepo;
import com.jingtuo.android.lottery.page.base.BaseActivity;
import com.jingtuo.android.lottery.page.lottery.combination.widget.LotteryCombinationAdapter;
import com.jingtuo.android.lottery.page.lottery.result.widget.LoadMoreFooterViewController;
import com.jingtuo.android.widget.LoadMoreController;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 彩票分析页面
 *
 * @author JingTuo
 */
public class LotteryCombinationActivity extends BaseActivity implements LoadMoreController.OnLoadMoreListener {

    private Lottery mLottery;

    private LotteryCombinationAdapter mAdapter;

    private int pageNo = 1;

    private LoadMoreController loadMoreController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_analysis);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView listView = findViewById(R.id.list_view);
        View loadMoreView = View.inflate(this, R.layout.load_more, null);
        LoadMoreFooterViewController loadMoreFooterViewController = new LoadMoreFooterViewController(loadMoreView);
        listView.addFooterView(loadMoreView);
        loadMoreController = new LoadMoreController(listView, loadMoreView);
        loadMoreController.setOnSlideDistanceListener(loadMoreFooterViewController);
        loadMoreController.setOnStatusChangedListener(loadMoreFooterViewController);
        loadMoreController.setOnLoadMoreListener(this);


        mAdapter = new LotteryCombinationAdapter();
        listView.setAdapter(mAdapter);

        Intent intent = getIntent();
        mLottery = intent.getParcelableExtra(Constants.LOTTERY);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mLottery.getDescr() + " → " + getString(R.string.lottery_analysis));
        }
        mDisposable.add(LotteryRepo.getInstance().queryCombinations(getApplicationContext(), mLottery.getCode(), pageNo, Constants.PAGE_SIZE)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(combinations -> {
                    loadMoreController.setEnabled(combinations.size() >= Constants.PAGE_SIZE);
                    mAdapter.setData(combinations);
                    mAdapter.notifyDataSetChanged();
                }));
    }

    @Override
    public void onLoadMore(View view) {
        mDisposable.add(LotteryRepo.getInstance().queryCombinations(getApplicationContext(), mLottery.getCode(), pageNo + 1, Constants.PAGE_SIZE)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(combinations -> {
                    pageNo++;
                    loadMoreController.setEnabled(combinations.size() >= Constants.PAGE_SIZE);
                    loadMoreController.setLoadMore(false);
                    mAdapter.getData().addAll(combinations);
                    mAdapter.notifyDataSetChanged();
                }, throwable -> loadMoreController.setLoadMore(false)));
    }
}
