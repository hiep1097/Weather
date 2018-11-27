package com.example.hiephoangvan.weather.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hiephoangvan.weather.R;
import com.example.hiephoangvan.weather.Utils.UtilPref;
import com.example.hiephoangvan.weather.adapters.ViewpagerAdapter;
import com.example.hiephoangvan.weather.databases.PlaceDatabase;
import com.example.hiephoangvan.weather.fragments.FragmentCurrently;
import com.example.hiephoangvan.weather.fragments.FragmentHourly;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import butterknife.BindView;
import butterknife.ButterKnife;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.content_layout) FrameLayout mContentLayout;
    @BindView(R.id.tabLayout) TabLayout mTabLayout;
    @BindView(R.id.viewPager) ViewPager mViewPager;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tv_title_toolbar) TextView toolbarTitle;
    @BindView(R.id.drawer) DuoDrawerLayout drawerLayout;
    @BindView(R.id.it_vitri) LinearLayout mItemLocation;
    @BindView(R.id.it_temp) ToggleSwitch mItemTemp;
    @BindView(R.id.it_wallpaper) LinearLayout mItemWallpaper;
    @BindView(R.id.mScrollNav) ScrollView mScrollNav;
    FragmentManager mFragmentManager;
    ViewpagerAdapter mViewpagerAdapter;
    public static final int REQUEST_CODE = 1;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    PlaceDetectionClient mPlaceDetectionClient;
    PlaceDatabase placeDatabase;
    public static List<com.example.hiephoangvan.weather.models.Place> list = new ArrayList<>();
    public static MainActivity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        instance = this;
        placeDatabase = new PlaceDatabase(this);
        list = placeDatabase.getAllPlaces();
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        if (!mLocationPermissionGranted) getLocationPermission();
        else setControl();
    }

    public void setControl(){
        mFragmentManager = getSupportFragmentManager();
        mViewpagerAdapter = new ViewpagerAdapter(mFragmentManager);
        mViewPager.setAdapter(mViewpagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        setSupportActionBar(mToolbar);
        DuoDrawerToggle drawerToggle = new DuoDrawerToggle(this, drawerLayout, mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
        toolbarTitle.setText(UtilPref.getString(this,"address",""));
        toolbarTitle.setSelected(true);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        mItemLocation.setOnClickListener(this::onClick);
        mItemWallpaper.setOnClickListener(this::onClick);
        toggleSwitchListener();
    }

    public void toggleSwitchListener(){
        if (UtilPref.getString(MainActivity.this,"unit","metric").compareTo("metric")==0){
            mItemTemp.setCheckedTogglePosition(0);
        } else {
            mItemTemp.setCheckedTogglePosition(1);
        }
        mItemTemp.setOnToggleSwitchChangeListener(new BaseToggleSwitch.OnToggleSwitchChangeListener() {
            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                if (position==0){
                    UtilPref.setString(MainActivity.this,"unit","metric");

                } else {
                    UtilPref.setString(MainActivity.this,"unit","imperial");
                }
                FragmentCurrently.instance.onRefresh();
                FragmentHourly.instance.onRefresh();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.it_location){
            Intent intent = new Intent(MainActivity.this,PlaceActivity.class);
            startActivityForResult(intent,REQUEST_CODE);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE) {

            // resultCode được set bởi DetailActivity
            // RESULT_OK chỉ ra rằng kết quả này đã thành công
            if(resultCode == Activity.RESULT_OK) {
                toolbarTitle.setText(UtilPref.getString(this,"address",""));
                FragmentCurrently.instance.onRefresh();
                FragmentHourly.instance.onRefresh();
            } else {
                // DetailActivity không thành công, không có data trả về.
            }
        }
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
                        boolean dupl = false;
                        for (com.example.hiephoangvan.weather.models.Place pl: list){
                            if (pl.getAddress().compareTo(p.getAddress())==0){
                                dupl = true;
                                break;
                            }
                        }
                        if (!dupl) {
                            placeDatabase.addPlace(p);
                            UtilPref.setFloat(MainActivity.this, "lat", p.getLat());
                            UtilPref.setFloat(MainActivity.this, "lon", p.getLon());
                            UtilPref.setString(MainActivity.this, "address", p.getAddress());
                            toolbarTitle.setText(p.getAddress());
                        }
                        likelyPlaces.release();
                    }
                });
            }
            setControl();
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
            getDeviceLocation();
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
                    getDeviceLocation();
                } else {
                    Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.it_vitri:
                Intent intent = new Intent(MainActivity.this,PlaceActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
                drawerLayout.closeDrawer();
                break;
            case R.id.it_wallpaper:
                Intent intent1 = new Intent(MainActivity.this,WallpaperActivity.class);
                startActivityForResult(intent1,REQUEST_CODE);
                break;
        }
    }
    public void setBackground(){
        mContentLayout.setBackground(getDrawable(this,"wallpaper"+UtilPref.getInt(this,"wallpaperpos",0)));
        mScrollNav.setBackground(getDrawable(this,"wallpaper"+UtilPref.getInt(this,"wallpaperpos",0)));
    }
    public void setBackground(String path){
        mContentLayout.setBackground(Drawable.createFromPath(path));
        mScrollNav.setBackground(Drawable.createFromPath(path));
    }
    public Drawable getDrawable(Context context, String name) {
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable",
                context.getPackageName());
        Drawable drawable = resources.getDrawable(resourceId);
        return drawable;
    }
}
