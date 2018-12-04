package com.example.hiephoangvan.weather.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hiephoangvan.weather.R;
import com.example.hiephoangvan.weather.Utils.UtilPref;
import com.example.hiephoangvan.weather.application.App;
import com.example.hiephoangvan.weather.databases.Datamanager;
import com.example.hiephoangvan.weather.interfaces.ItemOnClick;
import com.example.hiephoangvan.weather.databases.Places;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {
    ItemOnClick mListener;
    private List<Places> list;
    int homePossition=0;
    private Context context;
    private LayoutInflater layoutInflater;
    public PlaceAdapter(List<Places> list, ItemOnClick mListener) {
        this.list = list;
        this.mListener = mListener;
//        context = App.getContext();
//        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_place, parent, false);
        homePossition = UtilPref.getInstance().getInt("homePossition",0);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mPlace.setSelected(true);
        if (position==homePossition){
            holder.mImagePlace.setImageResource(R.drawable.ic_home);
        } else {
            holder.mImagePlace.setImageResource(R.drawable.ic_location);
        }
        holder.mPlace.setText(list.get(position).getAddress());
        holder.mBtnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.mBtnMore);
                //inflating menu from xml resource
                popup.inflate(R.menu.popup_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.it_setashome:
                                UtilPref.getInstance().setInt("homePossition",position);
                                homePossition = position;
                                notifyDataSetChanged();
                                mListener.onClick(v,position);
                                return true;
                            case R.id.it_delete:
                                if (homePossition==position){
                                    homePossition=0;
                                    UtilPref.getInstance().setInt("homePossition",0);
                                }
                                Datamanager.getInstance().deletePlace(list.get(position));
                                list.clear();
                                notifyDataSetChanged();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                Menu popupMenu = popup.getMenu();
                if(position==0)
                    popupMenu.findItem(R.id.it_delete).setVisible(false);
                else
                    popupMenu.findItem(R.id.it_delete).setVisible(true);
                popup.show();

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_place) ImageView mImagePlace;
        @BindView(R.id.tv_place) TextView mPlace;
        @BindView(R.id.btn_more) ImageView mBtnMore;
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
