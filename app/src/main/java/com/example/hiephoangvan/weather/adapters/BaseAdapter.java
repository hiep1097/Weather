package com.example.hiephoangvan.weather.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class BaseAdapter<M,V extends BaseViewHolder<M>> extends RecyclerView.Adapter<V> {
    protected List<M> mData;
    protected Context context;
    protected LayoutInflater layoutInflater;
    public BaseAdapter(List<M> data, Context context) {
        this.mData = data;
        this.context = context;
        layoutInflater=LayoutInflater.from(context);
    }

    protected View getViewItem(ViewGroup viewGroup){
        return layoutInflater.inflate(layoutItem(),viewGroup,false);
    }

    @Override
    public void onBindViewHolder(@NonNull V v, int i) {
        v.bindView(mData.get(i),i);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public abstract int layoutItem();
}
