package com.example.hiephoangvan.weather.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.hiephoangvan.weather.R;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.example.hiephoangvan.weather.Utils.UtilPref;
import com.example.hiephoangvan.weather.application.App;
import com.example.hiephoangvan.weather.models.Wallpaper;

import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.ViewHolder> {

    private List<Wallpaper> list;
    private Context context;
    private int wallpaperPos;
    private LayoutInflater layoutInflater;

    public WallpaperAdapter(List<Wallpaper> list) {
        this.list = list;
        wallpaperPos = UtilPref.getInstance().getInt("wallpaperpos",0);
        context = App.getContext();
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_wallpaper, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (wallpaperPos == position){
            holder.mCheck.setVisibility(View.VISIBLE);
        } else {
            holder.mCheck.setVisibility(View.GONE);
        }
        holder.mWallpaper.setImageDrawable(getDrawable(context,list.get(position).getImage()));
        holder.mWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallpaperPos = position;
                notifyDataSetChanged();
            }
        });
    }

    public Drawable getDrawable(Context context, String name) {
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

    public int getWallpaperPos(){
        return wallpaperPos;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageWallpaper) ImageView mWallpaper;
        @BindView(R.id.imageCheck) ImageView mCheck;
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            int mWidth = (getWidthScreen(context)-12)/2;
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(mWidth,mWidth*6/5);
            mWallpaper.setLayoutParams(layoutParams);
        }
    }
    public static int getWidthScreen(Context context){
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int mWidthScreen = display.getWidth();
        return mWidthScreen;
    }
}

