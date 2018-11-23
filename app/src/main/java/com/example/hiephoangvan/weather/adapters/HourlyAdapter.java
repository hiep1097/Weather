package com.example.hiephoangvan.weather.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hiephoangvan.weather.R;
import com.example.hiephoangvan.weather.models.HourlyWeather;

import com.example.hiephoangvan.weather.models.List;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.ViewHolder> {

    private java.util.List<List> list;
    private Context context;

    public HourlyAdapter(java.util.List<List> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_hourly, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String gioPhut = list.get(position).getDtTxt().split(" ")[1];
        StringBuilder sb = new StringBuilder(gioPhut);
        sb.delete(5,8);
        holder.mHourlyTime.setText(sb.toString());
        holder.mHourlyImageWeather.setImageDrawable(getDrawable(context,"ic_"+list.get(position).getWeather().get(0).getIcon()));
        holder.mHourlyTemp.setText(Math.round(list.get(position).getMain().getTemp())+"");
        try {
            if (list.get(position).getRain().get3h()==null){
                list.get(position).getRain().set3h(0.00);
            }
            holder.mHourlyRain.setText(" "+(double) Math.round(list.get(position).getRain().get3h() * 100) / 100+" mm");
        } catch (NullPointerException e){
            holder.mHourlyRain.setText(" "+0.00+" mm");
        }

        holder.mHourlyHumidity.setText(" "+list.get(position).getMain().getHumidity()+" %");
        //layout thu ngay
        String ngayThang = list.get(position).getDtTxt().split(" ")[0];
        String ngayThangBefore = null;
        if (position!=0) {
            ngayThangBefore = list.get(position-1).getDtTxt().split(" ")[0];
        }
        if (position==0 || (position!=0 && ngayThang.compareTo(ngayThangBefore)!=0)){
            holder.mLayoutThuNgay.setVisibility(View.VISIBLE);
            String [] s = ngayThang.split("-");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR,Integer.parseInt(s[0]));
            calendar.set(Calendar.MONTH,Integer.parseInt(s[1])-1);
            calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(s[2]));
            String thuNgay = null;
            if (calendar.get(Calendar.DAY_OF_WEEK)==1){
                thuNgay = new String("Chủ nhật, "+s[2]+" tháng "+s[1]);
            } else {
                thuNgay = new String("Thứ "+calendar.get(Calendar.DAY_OF_WEEK)+", "+s[2]+" tháng "+s[1]);
            }

            holder.mThuNgay.setText(thuNgay);
        }
        else {
            holder.mLayoutThuNgay.setVisibility(View.GONE);
        }
    }

    public Drawable getDrawable(Context context, String name){
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable",
                context.getPackageName());
        Drawable drawable = resources.getDrawable(resourceId);
        return drawable;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_hourly_time) TextView mHourlyTime;
        @BindView(R.id.image_weather) ImageView mHourlyImageWeather;
        @BindView(R.id.tv_hourly_temp) TextView mHourlyTemp;
        @BindView(R.id.tv_luongmua) TextView mHourlyRain;
        @BindView(R.id.tv_humidity) TextView mHourlyHumidity;
        @BindView(R.id.ln_thu_ngay) LinearLayout mLayoutThuNgay;
        @BindView(R.id.tv_thu_ngay) TextView mThuNgay;
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
