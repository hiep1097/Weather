package com.example.hiephoangvan.weather.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.hiephoangvan.weather.R;
import com.example.hiephoangvan.weather.Utils.UtilPref;
import com.example.hiephoangvan.weather.adapters.PlaceAdapter;
import com.example.hiephoangvan.weather.databases.PlaceDatabase;
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
import butterknife.ButterKnife;

public class PlaceActivity extends AppCompatActivity implements ItemOnClick {
    String TAG = "PLACE_ACTIVITY";
    @BindView(R.id.recyclerViewPlace) RecyclerView mRecyclerViewPlace;
    @BindView(R.id.btn_add_place) Button mButtonAddPlace;
    @BindView(R.id.tv_title_toolbar) TextView toolbarTitle;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.layoutplace) LinearLayout mLayoutPlace;
    private PlaceAdapter placeAdapter;
    private List<Places> list = new ArrayList<>();
    private PlaceDatabase placeDatabase;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;
    public static PlaceActivity instance;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        ButterKnife.bind(this);
        instance = this;
        if (UtilPref.getInstance().getInt("wallpaperpos",0)!=15) setBackground();
        else setBackground(UtilPref.getInstance().getString("wallpaperpath",""));
        setSupportActionBar(mToolbar);
        toolbarTitle.setVisibility(View.GONE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Chỉnh sửa vị trí");
        actionBar.setDisplayHomeAsUpEnabled(true);
        list = PlaceDatabase.getInstance().placeDAO().getAllPlaces();
        placeAdapter = new PlaceAdapter(list, this::onClick);
        mRecyclerViewPlace.setAdapter(placeAdapter);
        mRecyclerViewPlace.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mButtonAddPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPlaceAutocompleteActivityIntent();
            }
        });
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
        //autocompleteFragment.onActivityResult(requestCode, resultCode, data);
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
                    PlaceDatabase.getInstance().placeDAO().addPlace(p);
                    updateList();
                }
                updateList();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    public void updateList() {
        this.list.clear();
        this.list.addAll(PlaceDatabase.getInstance().placeDAO().getAllPlaces());
        placeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view, int position) {
        UtilPref.getInstance().setFloat("lat", list.get(position).getLat());
        UtilPref.getInstance().setFloat("lon", list.get(position).getLon());
        UtilPref.getInstance().setString("address", list.get(position).getAddress());
        toolbarTitle.setText(list.get(position).getAddress());
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
    public void setBackground(){
        mLayoutPlace.setBackground(getDrawable(this,"wallpaper"+UtilPref.getInstance().getInt("wallpaperpos",0)));
    }
    public void setBackground(String path){
        mLayoutPlace.setBackground(Drawable.createFromPath(path));
    }
    public Drawable getDrawable(Context context, String name) {
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable",
                context.getPackageName());
        Drawable drawable = resources.getDrawable(resourceId);
        return drawable;
    }
}
