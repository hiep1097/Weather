package com.example.hiephoangvan.weather.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hiephoangvan.weather.R;
import com.example.hiephoangvan.weather.Utils.UtilPref;
import com.example.hiephoangvan.weather.adapters.PlaceAdapter;
import com.example.hiephoangvan.weather.databases.PlaceDatabase;
import com.example.hiephoangvan.weather.interfaces.ItemOnClick;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceActivity extends AppCompatActivity implements ItemOnClick {
    String TAG = "PLACE_ACTIVITY";
    PlaceDetectionClient mPlaceDetectionClient;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    @BindView(R.id.recyclerViewPlace) RecyclerView mRecyclerViewPlace;
    @BindView(R.id.btn_add_place) Button mButtonAddPlace;
    @BindView(R.id.tv_title_toolbar) TextView toolbarTitle;
    PlaceAdapter placeAdapter;
    List<com.example.hiephoangvan.weather.models.Place> list = new ArrayList<>();
    PlaceDatabase placeDatabase;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        ButterKnife.bind(this);
        placeDatabase = new PlaceDatabase(this);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        getLocationPermission();
        placeAdapter = new PlaceAdapter(list,this::onClick);
        mRecyclerViewPlace.setAdapter(placeAdapter);
        mRecyclerViewPlace.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        getDeviceLocation();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
                com.example.hiephoangvan.weather.models.Place p
                        = new com.example.hiephoangvan.weather.models.Place(list.size(),place.getName().toString()
                        ,place.getAddress().toString(),(float) place.getLatLng().latitude, (float) place.getLatLng().longitude);
                if (!list.contains(place)) {
                    placeDatabase.addPlace(p);
                    updateList();
                }
                updateList();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }
    public void updateList(){
        this.list.clear();
        this.list.addAll(placeDatabase.getAllPlaces());
        placeAdapter.notifyDataSetChanged();
    }
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
                placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                        PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                        Place place = likelyPlaces.get(0).getPlace();
                        com.example.hiephoangvan.weather.models.Place p
                                = new com.example.hiephoangvan.weather.models.Place(list.size(), place.getName().toString()
                                ,place.getAddress().toString(),(float) place.getLatLng().latitude, (float) place.getLatLng().longitude);
                        if (!list.contains(place)) {
                            placeDatabase.addPlace(p);
                            updateList();
                        }

                      //  mPlace.setText(place.getName()+" "+place.getAddress()+" "+place.getLatLng().latitude+" "+place.getLatLng().longitude);
                        likelyPlaces.release();
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public void onClick(View view, int position) {
        UtilPref.setFloat(this, "lat", list.get(position).getLat());
        UtilPref.setFloat(this, "lon", list.get(position).getLon());
        UtilPref.setString(this, "address", list.get(position).getAddress());
        toolbarTitle.setText(list.get(position).getAddress());
        finish();
    }
}
