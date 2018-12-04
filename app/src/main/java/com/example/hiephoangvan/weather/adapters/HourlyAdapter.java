package com.example.hiephoangvan.weather.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hiephoangvan.weather.R;
import com.example.hiephoangvan.weather.Utils.UtilDate;
import com.example.hiephoangvan.weather.Utils.UtilDrawable;
import com.example.hiephoangvan.weather.Utils.UtilPref;

import com.example.hiephoangvan.weather.models.Lists;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hiephoangvan.weather.Utils.Config.FORMAT_1;

public class HourlyAdapter extends BaseAdapter<Lists, HourlyAdapter.ViewHolder> {

    public HourlyAdapter(List<Lists> data, Context context) {
        super(data, context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getViewItem(parent));
    }

    @Override
    public int layoutItem() {
        return R.layout.item_hourly;
    }

    public class ViewHolder extends BaseViewHolder<Lists> {
        @BindView(R.id.tv_hourly_time)
        TextView mHourlyTime;
        @BindView(R.id.image_weather)
        ImageView mHourlyImageWeather;
        @BindView(R.id.tv_hourly_temp)
        TextView mHourlyTemp;
        @BindView(R.id.tv_luongmua)
        TextView mHourlyRain;
        @BindView(R.id.tv_humidity)
        TextView mHourlyHumidity;
        @BindView(R.id.ln_thu_ngay)
        LinearLayout mLayoutThuNgay;
        @BindView(R.id.tv_thu_ngay)
        TextView mThuNgay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void bindView(Lists data, int position) {
            String gioPhut = UtilDate.getInstance().getDateFormat(FORMAT_1)
                    .format(new Date(data.getDt() * 1000L)).split(" ")[1];
            StringBuilder sb = new StringBuilder(gioPhut);
            sb.delete(5, 8);
            mHourlyTime.setText(sb.toString());
            mHourlyImageWeather.setImageDrawable(UtilDrawable.getInstance().getDrawable("ic_" + data.getWeather().get(0).getIcon()));
            mHourlyTemp.setText(Math.round(data.getMain().getTemp()) + context.getResources().getString(R.string._do));
            try {
                if (data.getRain().get3h() == null) {
                    data.getRain().set3h(0.00);
                }
                mHourlyRain.setText(" " + (double) Math.round(data.getRain().get3h() * 100) / 100 + " mm");
            } catch (NullPointerException e) {
                mHourlyRain.setText(" " + 0.00 + " mm");
            }

            mHourlyHumidity.setText(" " + data.getMain().getHumidity() + " %");
            //layout thu ngay
            String ngayThang = UtilDate.getInstance().getDateFormat(FORMAT_1)
                    .format(new Date(data.getDt() * 1000L)).split(" ")[0];
            Log.d("ngaythangg", UtilDate.getInstance().getDateFormat(FORMAT_1)
                    .format(new Date(data.getDt() * 1000L)));
            String ngayThangBefore = null;
            if (position != 0) {
                ngayThangBefore = UtilDate.getInstance().getDateFormat(FORMAT_1)
                        .format(new Date(mData.get(position - 1).getDt() * 1000L)).split(" ")[0];
            }
            if (position == 0 || (position != 0 && ngayThang.compareTo(ngayThangBefore) != 0)) {
                mLayoutThuNgay.setVisibility(View.VISIBLE);
                String[] s = ngayThang.split("-");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(TimeZone
                        .getTimeZone(UtilPref.getInstance().getString("timezone", "")));
                calendar.set(Calendar.YEAR, Integer.parseInt(s[0]));
                calendar.set(Calendar.MONTH, Integer.parseInt(s[1]) - 1);
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s[2]));
                String thuNgay = null;
                if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
                    thuNgay = new String("Chủ nhật, " + s[2] + " tháng " + s[1]);
                } else {
                    thuNgay = new String("Thứ " + calendar.get(Calendar.DAY_OF_WEEK) + ", " + s[2] + " tháng " + s[1]);
                }

                mThuNgay.setText(thuNgay);
            } else {
                mLayoutThuNgay.setVisibility(View.GONE);
            }
        }
    }
}
