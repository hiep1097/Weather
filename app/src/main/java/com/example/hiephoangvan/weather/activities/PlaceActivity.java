package com.example.hiephoangvan.weather.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.hiephoangvan.weather.R;
import com.example.hiephoangvan.weather.Utils.UtilDrawable;
import com.example.hiephoangvan.weather.Utils.UtilPref;
import com.example.hiephoangvan.weather.adapters.PlaceAdapter;
import com.example.hiephoangvan.weather.databases.Datamanager;
import com.example.hiephoangvan.weather.databinding.ActivityPlaceBinding;
import com.example.hiephoangvan.weather.interfaces.ItemOnClick;
import com.example.hiephoangvan.weather.databases.Places;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PlaceActivity extends BaseActivity implements ItemOnClick {
    String TAG = "PLACE_ACTIVITY";
    private PlaceAdapter placeAdapter;
    private List<Places> list = new ArrayList<>();
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;
    private ActivityPlaceBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this,layoutActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setView() {
        if (UtilPref.getInstance().getInt("wallpaperpos",0)!=15) setBackground();
        else setBackground(UtilPref.getInstance().getString("wallpaperpath",""));
        setSupportActionBar(binding.toolbars.toolbar);
        binding.toolbars.tvTitleToolbar.setVisibility(View.GONE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Chỉnh sửa vị trí");
        actionBar.setDisplayHomeAsUpEnabled(true);
        Datamanager.getInstance().getAllPlaces()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Places>>() {
                    @Override
                    public void accept(List<com.example.hiephoangvan.weather.databases.Places> places)
                            throws Exception {
                        list.clear();
                        list.addAll(places);
                        placeAdapter.notifyDataSetChanged();
                    }
                });
        swapPlaceList();
        placeAdapter = new PlaceAdapter(list, this,this::onClick);
        binding.recyclerViewPlace.setAdapter(placeAdapter);
        binding.recyclerViewPlace.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.btnAddPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPlaceAutocompleteActivityIntent();
            }
        });
    }

    @Override
    public int layoutActivity() {
        return R.layout.activity_place;
    }

    public void swapPlaceList(){
        int vt = -1;
        for (int i=0;i<list.size();i++)
            if (list.get(i).getIsHome()==1){
                vt = i;
                break;
            }
        if (vt!=-1){
            com.example.hiephoangvan.weather.databases.Places p = list.get(0);
            list.set(0,list.get(vt));
            list.set(vt,p);
        }
    }

    private void callPlaceAutocompleteActivityIntent() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            //PLACE_AUTOCOMPLETE_REQUEST_CODE is integer for request code
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Places p = new Places(place.getName().toString()
                        , place.getAddress().toString(), (float) place.getLatLng().latitude,
                        (float) place.getLatLng().longitude);
                boolean dupl = false;
                for (Places pl: list){
                    if (pl.getAddress().compareTo(p.getAddress())==0){
                        dupl = true;
                        break;
                    }
                }
                if (!dupl) {
                    p.setIsHome(0);
                    Datamanager.getInstance().addPlace(p);
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    @Override
    public void onClick(View view, int position) {
        UtilPref.getInstance().setFloat("lat", list.get(position).getLat());
        UtilPref.getInstance().setFloat("lon", list.get(position).getLon());
        UtilPref.getInstance().setString("address", list.get(position).getAddress());
        binding.toolbars.tvTitleToolbar.setText(list.get(position).getAddress());
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    public void setBackground(){
        binding.layoutplace.setBackground(UtilDrawable.getInstance()
                .getDrawable("wallpaper"+UtilPref.getInstance().getInt("wallpaperpos",0)));
    }
    public void setBackground(String path){
        binding.layoutplace.setBackground(Drawable.createFromPath(path));
    }
}
