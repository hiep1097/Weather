package com.example.hiephoangvan.weather.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hiephoangvan.weather.R;
import com.example.hiephoangvan.weather.Utils.UtilPref;
import com.example.hiephoangvan.weather.adapters.PlaceAdapter;
import com.example.hiephoangvan.weather.databases.PlaceDatabase;
import com.example.hiephoangvan.weather.interfaces.ItemOnClick;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
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
    @BindView(R.id.recyclerViewPlace)
    RecyclerView mRecyclerViewPlace;
    @BindView(R.id.btn_add_place)
    Button mButtonAddPlace;
    @BindView(R.id.tv_title_toolbar)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    PlaceAdapter placeAdapter;
    List<com.example.hiephoangvan.weather.models.Place> list = new ArrayList<>();
    PlaceDatabase placeDatabase;
    private static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        toolbarTitle.setVisibility(View.GONE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Chỉnh sửa vị trí");
        actionBar.setDisplayHomeAsUpEnabled(true);
        placeDatabase = new PlaceDatabase(this);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        getLocationPermission();
        placeAdapter = new PlaceAdapter(list, this::onClick);
        mRecyclerViewPlace.setAdapter(placeAdapter);
        mRecyclerViewPlace.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        getDeviceLocation();
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
                com.example.hiephoangvan.weather.models.Place p
                        = new com.example.hiephoangvan.weather.models.Place(list.size(), place.getName().toString()
                        , place.getAddress().toString(), (float) place.getLatLng().latitude, (float) place.getLatLng().longitude);
                if (!list.contains(p)) {
                    placeDatabase.addPlace(p);
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
        this.list.addAll(placeDatabase.getAllPlaces());
        placeAdapter.notifyDataSetChanged();
    }

    private void getDeviceLocation() {
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
                                , place.getAddress().toString(), (float) place.getLatLng().latitude, (float) place.getLatLng().longitude);
                        if (!list.contains(place)) {
                            placeDatabase.addPlace(p);
                            updateList();
                        }
                        likelyPlaces.release();
                    }
                });
            }
        } catch (SecurityException e) {
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
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
