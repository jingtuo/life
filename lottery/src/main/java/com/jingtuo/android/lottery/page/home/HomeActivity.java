package com.jingtuo.android.lottery.page.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jingtuo.android.lottery.Constants;
import com.jingtuo.android.lottery.R;
import com.jingtuo.android.lottery.model.repo.LotteryRepo;
import com.jingtuo.android.lottery.page.base.BaseActivity;
import com.jingtuo.android.lottery.page.home.widget.LotteryAdapter;
import com.jingtuo.android.lottery.page.lottery.result.LotteryResultActivity;
import com.jingtuo.android.lottery.util.SimpleLog;



import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 首页
 *
 * @author JingTuo
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private AppCompatCheckedTextView ctvHot;
    private AppCompatCheckedTextView ctvHigh;

    private LotteryAdapter lotteryAdapter;

    private String mSearchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ctvHot = findViewById(R.id.ctv_hot);
        ctvHigh = findViewById(R.id.ctv_high);

        ctvHot.setOnClickListener(this);
        ctvHigh.setOnClickListener(this);

        ListView listView = findViewById(R.id.list_view);
        lotteryAdapter = new LotteryAdapter();
        listView.setAdapter(lotteryAdapter);
        listView.setOnItemClickListener(this);

        query(mSearchText, ctvHot.isChecked(), ctvHigh.isChecked());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ctv_hot) {
            ctvHot.setChecked(!ctvHot.isChecked());
            query(mSearchText, ctvHot.isChecked(), ctvHigh.isChecked());
        }
        if (v.getId() == R.id.ctv_high) {
            ctvHigh.setChecked(!ctvHigh.isChecked());
            query(mSearchText, ctvHot.isChecked(), ctvHigh.isChecked());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        //搜索菜单
        MenuItem menuSearch = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuSearch.getActionView();
        searchView.setQueryHint(getString(R.string.app_name));
        searchView.setImeOptions(EditorInfo.IME_ACTION_NONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.equals(mSearchText)) {
                    return false;
                }
                mSearchText = s;
                query(s, ctvHot.isChecked(), ctvHigh.isChecked());
                return true;
            }
        });
        searchView.setQueryRefinementEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search) {
            //搜索
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param text
     * @param hot
     * @param high
     */
    private void query(String text, boolean hot, boolean high) {
        mDisposable.add(LotteryRepo.getInstance().querySupportedLotteries(this, text, "", hot, high)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lotteries -> {
                    lotteryAdapter.setData(lotteries);
                    lotteryAdapter.notifyDataSetChanged();
                })
        );
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, LotteryResultActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.LOTTERY, lotteryAdapter.getItem(position));
        startActivity(intent);
    }
}
