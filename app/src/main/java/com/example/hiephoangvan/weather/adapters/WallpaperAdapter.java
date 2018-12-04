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

import com.example.hiephoangvan.weather.Utils.UtilDrawable;
import com.example.hiephoangvan.weather.Utils.UtilPref;
import com.example.hiephoangvan.weather.application.App;
import com.example.hiephoangvan.weather.models.Wallpaper;

import java.util.List;

public class WallpaperAdapter extends BaseAdapter<Wallpaper, WallpaperAdapter.ViewHolder> {
    private int wallpaperPos;

    public WallpaperAdapter(List<Wallpaper> data, Context context) {
        super(data, context);
        wallpaperPos = UtilPref.getInstance().getInt("wallpaperpos",0);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getViewItem(parent));
    }

    @Override
    public int layoutItem() {
        return R.layout.item_wallpaper;
    }

    public int getWallpaperPos(){
        return wallpaperPos;
    }

    public class ViewHolder extends BaseViewHolder<Wallpaper> {
        @BindView(R.id.imageWallpaper) ImageView mWallpaper;
        @BindView(R.id.imageCheck) ImageView mCheck;
        ViewHolder(View itemView) {
            super(itemView);
            int mWidth = (getWidthScreen(context)-12)/2;
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(mWidth,mWidth*6/5);
            mWallpaper.setLayoutParams(layoutParams);
        }

        @Override
        public void bindView(Wallpaper data, int position) {
            if (wallpaperPos == position){
                mCheck.setVisibility(View.VISIBLE);
            } else {
                mCheck.setVisibility(View.GONE);
            }
            mWallpaper.setImageDrawable(UtilDrawable.getInstance().getDrawable(data.getImage()));
            mWallpaper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wallpaperPos = position;
                    notifyDataSetChanged();
                }
            });
        }
    }
    public static int getWidthScreen(Context context){
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int mWidthScreen = display.getWidth();
        return mWidthScreen;
    }
}

