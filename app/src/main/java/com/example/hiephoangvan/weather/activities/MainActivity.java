package com.example.hiephoangvan.weather.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.example.hiephoangvan.weather.Utils.UtilDrawable;
import com.example.hiephoangvan.weather.Utils.UtilPref;
import com.example.hiephoangvan.weather.adapters.ViewpagerAdapter;
import com.example.hiephoangvan.weather.api.RetrofitInstance;
import com.example.hiephoangvan.weather.api.Service;
import com.example.hiephoangvan.weather.application.App;
import com.example.hiephoangvan.weather.databases.PlaceDatabase;
import com.example.hiephoangvan.weather.fragments.FragmentCurrently;
import com.example.hiephoangvan.weather.fragments.FragmentHourly;
import com.example.hiephoangvan.weather.models.Zone;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.observers.BlockingBaseObserver;
import io.reactivex.schedulers.Schedulers;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;
import retrofit2.Call;
import retrofit2.Response;

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
        if (UtilPref.getInstance().getInt("wallpaperpos",0)!=15) setBackground();
        else setBackground(UtilPref.getInstance().getString("wallpaperpath",""));
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
        toolbarTitle.setText(UtilPref.getInstance().getString("address",""));
        toolbarTitle.setSelected(true);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        mItemLocation.setOnClickListener(this::onClick);
        mItemWallpaper.setOnClickListener(this::onClick);
        toggleSwitchListener();
    }

    public void toggleSwitchListener(){
        if (UtilPref.getInstance().getString("unit","metric").compareTo("metric")==0){
            mItemTemp.setCheckedTogglePosition(0);
        } else {
            mItemTemp.setCheckedTogglePosition(1);
        }
        mItemTemp.setOnToggleSwitchChangeListener(new BaseToggleSwitch.OnToggleSwitchChangeListener() {
            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                if (position==0){
                    UtilPref.getInstance().setString("unit","metric");

                } else {
                    UtilPref.getInstance().setString("unit","imperial");
                }
                FragmentCurrently.instance.onRefresh();
                FragmentHourly.instance.onRefresh();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        MenuItem item = menu.findItem(R.id.it_location);
        ScaleDrawable scaleDrawable = (ScaleDrawable) item.getIcon();
        scaleDrawable.setLevel(3000);
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
                toolbarTitle.setText(UtilPref.getInstance().getString("address",""));
                getTimeZone(MainActivity.this);
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
                        Log.d("placeeeeeee",place.getLatLng().latitude+" "+place.getLatLng().longitude);
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
                            UtilPref.getInstance().setFloat("lat", p.getLat());
                            UtilPref.getInstance().setFloat("lon", p.getLon());
                            UtilPref.getInstance().setString("address", p.getAddress());
                            getTimeZone(MainActivity.this);
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

    public void getTimeZone(Context context){
        Service service = RetrofitInstance.getRetrofitInstance2().create(Service.class);
        Observable<Zone> observable = service.getTimeZone(
                Math.round(UtilPref.getInstance().getFloat("lat",0)),
                Math.round(UtilPref.getInstance().getFloat("lon",0)), "hiep1097");
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BlockingBaseObserver<Zone>() {
                    @Override
                    public void onNext(Zone zone) {
                        UtilPref.getInstance().setString("timezone",zone.getTimezoneId());
                        Log.d("timezoneeeeeeee",UtilPref.getInstance().getString("timezone",""));
                        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                            if (fragment != null) {
                                fragment.onActivityResult(REQUEST_CODE, Activity.RESULT_OK, null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
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
        mContentLayout.setBackground(UtilDrawable.getInstance().getDrawable("wallpaper"
                +UtilPref.getInstance().getInt("wallpaperpos",0)));
        mScrollNav.setBackground(UtilDrawable.getInstance().getDrawable("wallpaper"
                +UtilPref.getInstance().getInt("wallpaperpos",0)));
    }
    public void setBackground(String path){
        mContentLayout.setBackground(Drawable.createFromPath(path));
        mScrollNav.setBackground(Drawable.createFromPath(path));
    }
}
