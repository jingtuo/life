package com.jingtuo.android.lottery.page.lottery.result.widget;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingtuo.android.lottery.R;
import com.jingtuo.android.widget.LoadMoreController;

public class LoadMoreFooterViewController implements LoadMoreController.OnSlideDistanceListener, LoadMoreController.OnStatusChangedListener {

    private ImageView ivIcon;

    private TextView tvMessage;

    private Animation animation;

    public LoadMoreFooterViewController(View view) {
        ivIcon = view.findViewById(R.id.iv_icon);
        tvMessage = view.findViewById(R.id.tv_message);
        animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotating);
    }

    @Override
    public void onSlideDistance(float distance) {
        float fromRotation = ivIcon.getRotation();
        ivIcon.setRotation(fromRotation + distance);
    }

    @Override
    public void onStatusChanged(View view, int status) {
        if (LoadMoreController.STATUS_PULL_TO_LOAD_MORE == status) {
            tvMessage.setText(R.string.pull_to_load_more);
        } else if (LoadMoreController.STATUS_RELEASE_TO_LOAD_MORE == status) {
            tvMessage.setText(R.string.release_to_load_more);
        } else if (LoadMoreController.STATUS_LOAD_MORE == status) {
            tvMessage.setText(R.string.loading);
            ivIcon.startAnimation(animation);
        } else if (LoadMoreController.STATUS_NONE == status) {
            ivIcon.clearAnimation();
        }

    }
}
