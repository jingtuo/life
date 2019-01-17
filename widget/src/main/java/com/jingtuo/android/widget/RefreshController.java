package com.jingtuo.android.widget;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import lombok.Setter;

/**
 * 刷新控件
 *
 * @author JingTuo
 */
public class RefreshController implements View.OnTouchListener {

    public static final int STATUS_NONE = 0;
    public static final int STATUS_PULL_TO_REFRESH = 1;
    public static final int STATUS_RELEASE_TO_REFRESH = 2;
    public static final int STATUS_REFRESH = 3;

    private static final float ZERO = 0.0F;

    private ViewGroup parent;
    private View refreshView;

    private float lastY;
    private int lastActionIndex;

    private int status;

    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;

    private int minHeight;

    private int minPaddingTop;

    private int maxPaddingTop;

    @Setter
    private OnRefreshListener onRefreshListener;

    @Setter
    private OnStatusChangedListener onStatusChangedListener;

    @Setter
    private OnSlideDistanceListener onSlideDistanceListener;

    /**
     * @param parent 如果parent是ListView,需要将refreshView添加到ListView的HeaderView;
     *               如果parent是ScrollView,需要将refreshView添加到ScrollView内的容器（如LinearLayout）的最顶层
     */
    public RefreshController(final ViewGroup parent, View refreshView) {
        this.parent = parent;
        this.refreshView = refreshView;
        paddingLeft = refreshView.getPaddingLeft();
        paddingRight = refreshView.getPaddingRight();
        paddingTop = refreshView.getPaddingTop();
        paddingBottom = refreshView.getPaddingBottom();
        refreshView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        minHeight = refreshView.getMeasuredHeight();
        minPaddingTop = -minHeight;
        setMaxHeight(3 * minHeight);
        setRefresh(false);
        parent.setOnTouchListener(this);
    }

    public void setMaxHeight(int maxHeight) {
        maxPaddingTop = maxHeight - minHeight + paddingTop;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (STATUS_REFRESH == status || parent.canScrollVertically(-1)) {
            //正在刷新或者可以向上滑动, 忽略任何事件
            return false;
        }
        int action = event.getAction();
        if (MotionEvent.ACTION_DOWN == action) {
            lastActionIndex = event.getActionIndex();
            lastY = event.getY(lastActionIndex);
            return false;
        }
        if (MotionEvent.ACTION_MOVE == action) {
            //滑动
            return handleMoveEvent(event);
        }
        if (MotionEvent.ACTION_UP == action || MotionEvent.ACTION_CANCEL == action) {
            //抬起
            return handleUpEvent(event);
        }
        return false;
    }

    /**
     * @param refresh
     */
    public void setRefresh(boolean refresh) {
        if (refresh) {
            changeStatus(STATUS_REFRESH);
            refreshView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        } else {
            changeStatus(STATUS_NONE);
            refreshView.setPadding(paddingLeft, minPaddingTop, paddingRight, paddingBottom);
        }
    }

    public interface OnStatusChangedListener {
        /**
         * 当状态发生变更
         *
         * @param view
         * @param status
         */
        void onStatusChanged(View view, int status);
    }


    public interface OnRefreshListener {

        /**
         * 刷新
         *
         * @param view
         */
        void onRefresh(View view);
    }


    public interface OnSlideDistanceListener {
        /**
         * 滑动距离
         *
         * @param distance
         */
        void onSlideDistance(float distance);
    }

    /**
     * 处理滑动事件
     *
     * @param event
     * @return
     */
    private boolean handleMoveEvent(MotionEvent event) {
        int actionIndex = event.getActionIndex();
        if (actionIndex == lastActionIndex) {
            float previousY = lastY;
            float y = event.getY(actionIndex);
            float distanceY = y - lastY;
            lastY = y;
            if (Float.compare(previousY, ZERO) <= 0) {
                /**
                 * 经测试发现,在小米Note3手机上,会出现直接跳过DOWN,进入MOVE,这个时候计算出的distanceY会有问题
                 */
                return false;
            }
            if (onSlideDistanceListener != null) {
                onSlideDistanceListener.onSlideDistance(distanceY);
            }
            int previousPaddingTop = refreshView.getPaddingTop();
            int nextPaddingTop = previousPaddingTop;
            if (distanceY > 0) {
                //手指从上往下滑动,
                nextPaddingTop = (int) (previousPaddingTop + distanceY);
                if (previousPaddingTop == minPaddingTop) {
                    //刷新控件消失
                    changeStatus(STATUS_PULL_TO_REFRESH);
                }
                if (previousPaddingTop == maxPaddingTop) {
                    //刷新控件已经最大高度,继续下拉,什么都不做
                    return true;
                }
            }
            if (distanceY < 0) {
                //手指从下往上滑动
                nextPaddingTop = (int) (previousPaddingTop - Math.abs(distanceY));
                if (previousPaddingTop == minPaddingTop) {
                    //刷新控件消失,继续向上滑动,认为取消下拉刷新
                    changeStatus(STATUS_NONE);
                    return false;
                }

            }
            if (nextPaddingTop < minPaddingTop) {
                nextPaddingTop = minPaddingTop;
            }
            if (nextPaddingTop > maxPaddingTop) {
                nextPaddingTop = maxPaddingTop;
            }
            refreshView.setPadding(paddingLeft, nextPaddingTop, paddingRight, paddingBottom);
            if (nextPaddingTop <= this.paddingTop) {
                changeStatus(STATUS_PULL_TO_REFRESH);
            } else {
                changeStatus(STATUS_RELEASE_TO_REFRESH);
            }
            return true;
        }
        return false;
    }

    /**
     * 处理抬起事件
     *
     * @param event
     * @return
     */
    private boolean handleUpEvent(MotionEvent event) {
        lastY = ZERO;
        int actionIndex = event.getActionIndex();
        if (actionIndex == lastActionIndex) {
            //只处理最后一次按下的抬起事件
            if (STATUS_RELEASE_TO_REFRESH == status) {
                //松开刷新
                changeStatus(STATUS_REFRESH);
                refreshView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                if (onRefreshListener != null) {
                    onRefreshListener.onRefresh(parent);
                }
            } else {
                changeStatus(STATUS_NONE);
                refreshView.setPadding(paddingLeft, minPaddingTop, paddingRight, paddingBottom);
            }
            return true;
        }
        return false;
    }

    /**
     * 修改状态
     *
     * @param status
     */
    private void changeStatus(int status) {
        if (this.status != status) {
            this.status = status;
            if (onStatusChangedListener != null) {
                onStatusChangedListener.onStatusChanged(parent, this.status);
            }
        }
    }
}