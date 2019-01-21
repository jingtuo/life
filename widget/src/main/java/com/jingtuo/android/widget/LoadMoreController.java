package com.jingtuo.android.widget;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import lombok.Getter;
import lombok.Setter;

/**
 * 加载更多控件
 * <p>
 * 手指从上往下滑动->下拉
 * 手指从下往上滑动->上滑
 *
 * @author JingTuo
 */
public class LoadMoreController implements View.OnTouchListener {

    private static final String TAG = LoadMoreController.class.getSimpleName();

    public static final int STATUS_NONE = 0;
    public static final int STATUS_PULL_TO_LOAD_MORE = 1;
    public static final int STATUS_RELEASE_TO_LOAD_MORE = 2;
    public static final int STATUS_LOAD_MORE = 3;

    private static final float ZERO = 0.0F;

    private ViewGroup parent;
    private View loadMoreView;

    private float lastY;
    private int lastActionIndex;

    private int status;

    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;

    private int minHeight;

    private int minPaddingBottom;

    private int maxPaddingBottom;

    @Setter
    private OnLoadMoreListener onLoadMoreListener;

    @Setter
    private OnStatusChangedListener onStatusChangedListener;

    @Setter
    private OnSlideDistanceListener onSlideDistanceListener;


    @Setter
    @Getter
    private boolean enabled;

    /**
     * @param parent 如果parent是ListView,需要将loadMoreView添加到ListView的FooterView;
     *               如果parent是ScrollView,需要将loadMoreView添加到ScrollView内的容器（如LinearLayout）的最低端
     */
    public LoadMoreController(final ViewGroup parent, View loadMoreView) {
        this.parent = parent;
        this.loadMoreView = loadMoreView;
        paddingLeft = loadMoreView.getPaddingLeft();
        paddingRight = loadMoreView.getPaddingRight();
        paddingTop = loadMoreView.getPaddingTop();
        paddingBottom = loadMoreView.getPaddingBottom();
        loadMoreView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        minHeight = loadMoreView.getMeasuredHeight();
        minPaddingBottom = -minHeight;
        setMaxHeight(3 * minHeight);
        setLoadMore(false);
        parent.setOnTouchListener(this);
    }

    public void setMaxHeight(int maxHeight) {
        maxPaddingBottom = maxHeight - minHeight + paddingBottom;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!enabled || STATUS_LOAD_MORE == status || ViewUtils.canScrollVertically(parent, 1)) {
            //不可用,正在加载更多,忽略任何事件
            return false;
        }

        int action = event.getAction();
        if (MotionEvent.ACTION_DOWN == action) {
            lastActionIndex = event.getActionIndex();
            lastY = event.getY(lastActionIndex);
            return true;
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
     * @param loadMore
     */
    public void setLoadMore(boolean loadMore) {
        if (loadMore) {
            changeStatus(STATUS_LOAD_MORE);
            loadMoreView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        } else {
            changeStatus(STATUS_NONE);
            loadMoreView.setPadding(paddingLeft, paddingTop, paddingRight, minPaddingBottom);
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


    public interface OnLoadMoreListener {

        /**
         * 加载更多
         *
         * @param view
         */
        void onLoadMore(View view);
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
            int previousPaddingBottom = loadMoreView.getPaddingBottom();
            int nextPaddingBottom = previousPaddingBottom;
            if (distanceY > 0) {
                //手指从上往下滑动,
                nextPaddingBottom = (int) (previousPaddingBottom - distanceY);
                if (previousPaddingBottom == minPaddingBottom) {
                    //控件消失,继续从上往下滑动,认为取消加载更多
                    changeStatus(STATUS_NONE);
                    return false;
                }
            }
            if (distanceY < 0) {
                //手指从下往上滑动
                nextPaddingBottom = (int) (previousPaddingBottom + Math.abs(distanceY));
                if (previousPaddingBottom == minPaddingBottom) {
                    //控件消失
                    changeStatus(STATUS_PULL_TO_LOAD_MORE);
                }
                if (previousPaddingBottom == maxPaddingBottom) {
                    //控件已经最大高度,继续从下往上滑动,什么都不做
                    return false;
                }
            }
            if (nextPaddingBottom < minPaddingBottom) {
                nextPaddingBottom = minPaddingBottom;
            }
            if (nextPaddingBottom > maxPaddingBottom) {
                nextPaddingBottom = maxPaddingBottom;
            }
            loadMoreView.setPadding(paddingLeft, paddingTop, paddingRight, nextPaddingBottom);
            if (nextPaddingBottom <= this.paddingBottom) {
                changeStatus(STATUS_PULL_TO_LOAD_MORE);
            } else {
                changeStatus(STATUS_RELEASE_TO_LOAD_MORE);
            }
            return false;
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
            if (STATUS_RELEASE_TO_LOAD_MORE == status) {
                //松开刷新
                loadMoreView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                changeStatus(STATUS_LOAD_MORE);
                if (onLoadMoreListener != null) {
                    onLoadMoreListener.onLoadMore(parent);
                }
            } else {
                loadMoreView.setPadding(paddingLeft, paddingTop, paddingRight, minPaddingBottom);
                changeStatus(STATUS_NONE);
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
        this.status = status;
        if (onStatusChangedListener != null) {
            onStatusChangedListener.onStatusChanged(parent, this.status);
        }
    }




}