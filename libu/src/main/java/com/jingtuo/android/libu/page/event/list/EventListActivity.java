package com.jingtuo.android.libu.page.event.list;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.jingtuo.android.libu.Constants;
import com.jingtuo.android.libu.R;

/**
 * 事件列表
 * @author JingTuo
 */
public class EventListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences preferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        String userId = preferences.getString(Constants.USER_ID, "");
        if (TextUtils.isEmpty(userId)) {
            finish();
            return;
        }
    }

}
