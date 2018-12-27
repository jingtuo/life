package com.jingtuo.android.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import lombok.Getter;
import lombok.Setter;

/**
 * 用于替代系统提供的{@link android.widget.Spinner}
 *
 * @author JingTuo
 */
public class Spinner extends AppCompatTextView implements View.OnClickListener {

    private PopupWindow popupWindow;

    @Setter
    @Getter
    private int dropDownXOffset;

    @Setter
    @Getter
    private int dropDownYOffset;

    @Getter
    private int dropDownHeight = WindowManager.LayoutParams.WRAP_CONTENT;

    @Getter
    private View dropDownView;

    @Setter
    private OnDropDownShowListener onDropDownShowListener;

    @Setter
    private OnDropDownHideListener onDropDownHideListener;

    public Spinner(Context context) {
        this(context, null);
    }

    public Spinner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Spinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setFocusable(true);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        requestFocus();
        if (popupWindow == null) {
            popupWindow = new PopupWindow(getContext());
            popupWindow.setContentView(dropDownView);
            popupWindow.setWidth(getWidth());
            popupWindow.setHeight(dropDownHeight);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setElevation(5f);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (onDropDownHideListener != null) {
                        onDropDownHideListener.onDropDownHide();
                    }
                }
            });
        }
        popupWindow.showAsDropDown(this, dropDownXOffset, dropDownYOffset);
        if (onDropDownShowListener != null) {
            onDropDownShowListener.onDropDownShow();
        }
    }

    /**
     * @param dropDownView
     */
    public void setDropDownView(View dropDownView) {
        this.dropDownView = dropDownView;
        if (popupWindow != null) {
            popupWindow.setContentView(dropDownView);
        }
    }

    public void setDropDownHeight(int dropDownHeight) {
        this.dropDownHeight = dropDownHeight;
        if (popupWindow != null) {
            popupWindow.setHeight(dropDownHeight);
        }
    }

    public void dismissDropDown() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }


    public interface OnDropDownShowListener {
        void onDropDownShow();
    }

    public interface OnDropDownHideListener {
        void onDropDownHide();
    }
}
