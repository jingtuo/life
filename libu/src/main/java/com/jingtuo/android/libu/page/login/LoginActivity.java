package com.jingtuo.android.libu.page.login;

import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jingtuo.android.libu.R;
import com.jingtuo.android.libu.model.repo.Repository;
import com.jingtuo.android.libu.page.event.list.EventListActivity;
import com.jingtuo.android.libu.util.Utils;
import com.jingtuo.android.rx.BaseActivity;
import com.jingtuo.android.util.IntentUtils;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * 登录
 *
 * @author JingTuo
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private AppCompatEditText etUserId;

    private MaterialButton btnLogin;

    private TextView tvLoggingIn;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etUserId = findViewById(R.id.et_user_id);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
        tvLoggingIn = findViewById(R.id.tv_logging_in);

        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {
            if (etUserId.getText() == null) {
                return;
            }
            String mobileNo = etUserId.getText().toString().trim();
            if (!Utils.checkMobileNo(mobileNo)) {
                Snackbar.make(v, R.string.mobile_no_invalid, Snackbar.LENGTH_SHORT).show();
                return;
            }
            showLoggingIn();
            mDisposable.add(config(Repository.getInstance().login(this, mobileNo))
                    .subscribe(aBoolean -> {
                        if (aBoolean) {
                            IntentUtils.startActivityWithNormal(LoginActivity.this, EventListActivity.class);
                            finish();
                        }
                    }, throwable -> Snackbar.make(etUserId, throwable.getMessage(), Snackbar.LENGTH_SHORT).show(), this::showLogin));
        }
    }

    /**
     * 显示正在登录
     */
    private void showLoggingIn() {
        btnLogin.setClickable(false);
        btnLogin.setText("");
        tvLoggingIn.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * 显示登录
     */
    private void showLogin() {
        btnLogin.setClickable(true);
        btnLogin.setText(R.string.login);
        tvLoggingIn.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }
}
