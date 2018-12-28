package com.jingtuo.android.lottery.page.home;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.jingtuo.android.lottery.Constants;
import com.jingtuo.android.lottery.R;
import com.jingtuo.android.lottery.model.repo.LotteryRepo;
import com.jingtuo.android.lottery.page.base.BaseActivity;
import com.jingtuo.android.lottery.page.home.widget.LotteryAdapter;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 首页
 *
 * @author JingTuo
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener {


    private LotteryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView listView = findViewById(R.id.list_view);
        adapter = new LotteryAdapter();
        listView.setAdapter(adapter);

        Intent intent = getIntent();
        initData(intent);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        MenuItem menuSearch = menu.findItem(R.id.search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menuSearch.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnSearchClickListener(v -> query(searchView.getQuery().toString().trim()));
        searchView.setQueryRefinementEnabled(true);
        menuSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                //搜索框展开
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //搜索框折叠
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search) {
            //搜索
            return true;
        }

        if (item.getItemId() == R.id.filter) {
            //过滤
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @param text
     */
    private void query(String text) {
        mDisposable.add(LotteryRepo.getInstance().querySupportedLotteries(this, text)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lotteries -> {
                    adapter.setData(lotteries);
                    adapter.notifyDataSetChanged();
                })
        );
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initData(intent);
    }

    /**
     *
     * @param intent
     */
    private void initData(Intent intent) {
        String query = null;
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions recentSuggestions = new SearchRecentSuggestions(this, Constants.AUTHORITY_MY_SEARCH_SUGGESTIONS_PROVIDER,
                    SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES);
            recentSuggestions.saveRecentQuery(query, null);
        }
        if (TextUtils.isEmpty(query)) {
            query = "";
        }
        query(query);
    }
}
