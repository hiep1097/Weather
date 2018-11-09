package com.example.hiephoangvan.weather.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hiephoangvan.weather.R;
import com.example.hiephoangvan.weather.models.HourlyWeather;

import com.example.hiephoangvan.weather.models.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.ViewHolder> {

    private java.util.List<List> list;


    public HourlyAdapter(java.util.List<List> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_hourly, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mHourlyTemp.setText(list.get(position).getMain().getTemp().toString());
        holder.mHourlyTime.setText(list.get(position).getDtTxt());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_hourly_temp)
        TextView mHourlyTemp;
        @BindView(R.id.tv_hourly_time)
        TextView mHourlyTime;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
