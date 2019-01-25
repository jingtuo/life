package com.jingtuo.android.lottery.page.lottery.result;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.jingtuo.android.lottery.Constants;
import com.jingtuo.android.lottery.R;
import com.jingtuo.android.lottery.model.db.Lottery;
import com.jingtuo.android.lottery.model.db.LotteryResult;
import com.jingtuo.android.lottery.model.repo.LotteryRepo;
import com.jingtuo.android.lottery.page.base.BaseActivity;
import com.jingtuo.android.lottery.page.lottery.combination.LotteryCombinationActivity;
import com.jingtuo.android.lottery.page.lottery.result.widget.LoadMoreFooterViewController;
import com.jingtuo.android.lottery.page.lottery.result.widget.LotteryResultAdapter;
import com.jingtuo.android.lottery.util.SimpleLog;
import com.jingtuo.android.widget.LoadMoreController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 彩票结果
 *
 * @author JingTuo
 */
public class LotteryResultActivity extends BaseActivity implements LoadMoreController.OnLoadMoreListener {

    private static final String TAG = LotteryResultActivity.class.getSimpleName();

    private Lottery mLottery;

    private LotteryResultAdapter resultAdapter;

    private LoadMoreController loadMoreController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_result);

        Intent intent = getIntent();
        mLottery = intent.getParcelableExtra(Constants.LOTTERY);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mLottery.getDescr() + " → " + getString(R.string.lottery_result));
        }

        ListView listView = findViewById(R.id.list_view);
        View loadMoreView = View.inflate(this, R.layout.load_more, null);
        LoadMoreFooterViewController loadMoreFooterViewController = new LoadMoreFooterViewController(loadMoreView);
        listView.addFooterView(loadMoreView);
        loadMoreController = new LoadMoreController(listView, loadMoreView);
        loadMoreController.setOnSlideDistanceListener(loadMoreFooterViewController);
        loadMoreController.setOnStatusChangedListener(loadMoreFooterViewController);
        loadMoreController.setOnLoadMoreListener(this);

        resultAdapter = new LotteryResultAdapter();
        listView.setAdapter(resultAdapter);

        mDisposable.add(LotteryRepo.getInstance().queryLotteryResults(getApplicationContext(), mLottery, "")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lotteryResultWrapper -> {
                    loadMoreController.setEnabled(!lotteryResultWrapper.isNoMore());
                    resultAdapter.setData(lotteryResultWrapper.getResult());
                    resultAdapter.notifyDataSetChanged();
                    loadMoreController.setLoadMore(false);
                }));

    }

    @Override
    public void onLoadMore(View view) {
        LotteryResult lotteryResult = resultAdapter.getItem(resultAdapter.getCount() - 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String time;
        try {
            Date date = dateFormat.parse(lotteryResult.getTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, -1);
            time = dateFormat.format(calendar.getTime());
        } catch (ParseException e) {
            SimpleLog.e(TAG, e.getMessage());
            return;
        }
        mDisposable.add(LotteryRepo.getInstance().queryLotteryResults(getApplicationContext(), mLottery, time)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lotteryResultWrapper -> {
                    loadMoreController.setEnabled(!lotteryResultWrapper.isNoMore());
                    resultAdapter.getData().addAll(lotteryResultWrapper.getResult());
                    resultAdapter.notifyDataSetChanged();
                    loadMoreController.setLoadMore(false);
                }));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.result_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.analysis == item.getItemId()) {
            Intent intent = new Intent(this, LotteryCombinationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Constants.LOTTERY, mLottery);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
