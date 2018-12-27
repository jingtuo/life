package com.jingtuo.android.lottery.page.home;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.jingtuo.android.lottery.R;
import com.jingtuo.android.lottery.model.repo.LotteryRepo;
import com.jingtuo.android.lottery.page.base.BaseActivity;
import com.jingtuo.android.lottery.page.home.widget.LotteryAdapter;
import com.jingtuo.android.lottery.page.home.widget.LotteryTypeAdapter;
import com.jingtuo.android.widget.LinearLayout;
import com.jingtuo.android.widget.OnItemClickListener;
import com.jingtuo.android.widget.Spinner;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 首页
 *
 * @author JingTuo
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener {


    private AppCompatEditText etName;
    private Spinner spinnerType;
    private RadioGroup rbHot;
    private RadioGroup rbHigh;

    private LotteryAdapter adapter;
    private LinearLayout<String> dropDownView;
    private LotteryTypeAdapter typeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etName = findViewById(R.id.et_lottery_name);
        spinnerType = findViewById(R.id.spinner_lottery_type);
        rbHot = findViewById(R.id.rg_hot);
        rbHigh = findViewById(R.id.rg_high);
        findViewById(R.id.btn_query).setOnClickListener(this);

        ListView listView = findViewById(R.id.list_view);
        adapter = new LotteryAdapter();
        listView.setAdapter(adapter);

        mDisposable.add(LotteryRepo.getInstance().queryLotteryTypes(this)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lotteryTypes -> {
                    typeAdapter = new LotteryTypeAdapter();
                    typeAdapter.setData(lotteryTypes);
                    dropDownView.setAdapter(typeAdapter);
                    dropDownView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                }));
        dropDownView = new LinearLayout<>(this);
        dropDownView.setOrientation(LinearLayout.VERTICAL);
        dropDownView.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        dropDownView.setDividerDrawable(ContextCompat.getDrawable(this, R.drawable.drop_down_divider));
        dropDownView.setBackground(ContextCompat.getDrawable(this, R.drawable.spinner_drop_down_bg));
        dropDownView.setOnItemClickListener((parent, view, position) -> {
            spinnerType.setText(typeAdapter.getItem(position));
            spinnerType.dismissDropDown();
        });
        spinnerType.setDropDownView(dropDownView);
        spinnerType.setOnDropDownShowListener(() -> spinnerType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up_black_12dp, 0));

        spinnerType.setOnDropDownHideListener(() -> spinnerType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_black_12dp, 0));
    }

    @Override
    public void onClick(View v) {
        if (R.id.btn_query == v.getId()) {
            CharSequence name = etName.getText();
            if (name == null) {
                name = "";
            }
            mDisposable.add(LotteryRepo.getInstance().querySupportedLotteries(this,
                    name.toString().trim(),
                    spinnerType.getText().toString().trim(),
                    rbHot.getCheckedRadioButtonId() == R.id.rb_hot_yes,
                    rbHigh.getCheckedRadioButtonId() == R.id.rb_high_yes)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(lotteries -> {
                        adapter.setData(lotteries);
                        adapter.notifyDataSetChanged();
                    })
            );
        }
    }
}
