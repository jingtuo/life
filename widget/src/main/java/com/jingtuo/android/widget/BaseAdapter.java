package com.jingtuo.android.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * 封装BaseAdapter,增加内容:
 * 1,setData(),getData();
 * 2,ViewHolder
 * </pre>
 *
 * @author JingTuo
 * @date 16/4/20
 */
public abstract class BaseAdapter<I> extends android.widget.BaseAdapter {

    @Setter
    @Getter
    protected List<I> data;

    public BaseAdapter() {
    }

    public BaseAdapter(List<I> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public I getItem(int position) {
        return position >= 0 && position < getCount() ? data.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder<BaseAdapter<I>, I> holder;
        View view;
        int viewType = getViewType(position);
        if (convertView == null) {
            holder = onCreateViewHolder(parent, viewType);
            view = holder.getView();
        } else {
            //noinspection unchecked
            holder = (ViewHolder<BaseAdapter<I>, I>) convertView.getTag();
            if (viewType == holder.getViewType()) {
                view = convertView;
            } else {
                holder = onCreateViewHolder(parent, viewType);
                view = holder.getView();
            }
        }
        holder.setView(position, getItem(position));
        return view;
    }

    protected abstract ViewHolder<BaseAdapter<I>, I> onCreateViewHolder(ViewGroup parent, int viewType);

    protected int getViewType(int position) {
        return 0;
    }


    public class ViewHolder<A, I> {

        @Getter
        private View view;

        @Getter
        private int viewType;

        @Getter
        private int position;

        @Getter
        private I data;

        @Getter
        private A adapter;

        public ViewHolder(ViewGroup parent, A adapter, int viewType) {
            this.adapter = adapter;
            this.view = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(), parent, false);
            this.viewType = viewType;
            this.view.setTag(this);
            initView(view);
        }

        protected int getLayoutId() {
            return android.R.layout.simple_list_item_1;
        }

        protected void initView(View view) {
        }

        public void setView(int position, I data) {
            this.position = position;
            this.data = data;
        }
    }
}
