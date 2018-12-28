package com.jingtuo.android.lottery.provider;

import android.content.SearchRecentSuggestionsProvider;

import com.jingtuo.android.lottery.Constants;

/**
 * 搜索建议的Provider
 *
 * @author JingTuo
 */
public class MySearchSuggestionsProvider extends SearchRecentSuggestionsProvider {

    public MySearchSuggestionsProvider() {
        setupSuggestions(Constants.AUTHORITY_MY_SEARCH_SUGGESTIONS_PROVIDER, DATABASE_MODE_QUERIES);
    }
}
