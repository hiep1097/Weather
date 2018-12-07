package com.example.hiephoangvan.weather.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.example.hiephoangvan.weather.databases.Datamanager;
import com.example.hiephoangvan.weather.databinding.ActivityMainBinding;
import com.example.hiephoangvan.weather.models.Zone;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.observers.BlockingBaseObserver;
import io.reactivex.schedulers.Schedulers;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private FragmentManager mFragmentManager;
    private ViewpagerAdapter mViewpagerAdapter;
    private final int REQUEST_CODE_PLACE = 1;
    private final int REQUEST_CODE_WALLPAPER = 5;
    private boolean mLocationPermissionGranted;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private PlaceDetectionClient mPlaceDetectionClient;
    private List<com.example.hiephoangvan.weather.databases.Places> list = new ArrayList<>();
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this,layoutActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setView() {
        setBackground();
        Datamanager.getInstance().getAllPlaces().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<com.example.hiephoangvan.weather.databases.Places>>() {
                    @Override
                    public void accept(List<com.example.hiephoangvan.weather.databases.Places> places)
                            throws Exception {
                        list.clear();
                        list.addAll(places);
                    }
                });
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        if (!mLocationPermissionGranted) getLocationPermission();
        else setControl();
    }

    @Override
    public int layoutActivity() {
        return R.layout.activity_main;
    }

    public void setControl() {
        mFragmentManager = getSupportFragmentManager();
        mViewpagerAdapter = new ViewpagerAdapter(mFragmentManager);
        binding.viewPager.setAdapter(mViewpagerAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        setSupportActionBar(binding.toolbars.toolbar);
        DuoDrawerToggle drawerToggle = new DuoDrawerToggle(this, binding.drawer, binding.toolbars.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        binding.drawer.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
        binding.toolbars.tvTitleToolbar.setText(UtilPref.getInstance().getString("address", ""));
        binding.toolbars.tvTitleToolbar.setSelected(true);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        binding.itVitri.setOnClickListener(this::onClick);
        binding.itWallpaper.setOnClickListener(this::onClick);
        toggleSwitchListener();
    }

    public void toggleSwitchListener() {
        if (UtilPref.getInstance().getString("unit", "metric").compareTo("metric") == 0) {
            binding.itTemp.setCheckedTogglePosition(0);
        } else {
            binding.itTemp.setCheckedTogglePosition(1);
        }
        binding.itTemp.setOnToggleSwitchChangeListener(new BaseToggleSwitch.OnToggleSwitchChangeListener() {
            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                if (position == 0) {
                    UtilPref.getInstance().setString("unit", "metric");

                } else {
                    UtilPref.getInstance().setString("unit", "imperial");
                }
                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment != null) {
                        fragment.onActivityResult(REQUEST_CODE_PLACE, Activity.RESULT_OK, null);
                    }
                }
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
        if (id == R.id.it_location) {
            Intent intent = new Intent(MainActivity.this, PlaceActivity.class);
            startActivityForResult(intent, REQUEST_CODE_PLACE);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PLACE) {
            if (resultCode == Activity.RESULT_OK) {
                binding.toolbars.tvTitleToolbar.setText(UtilPref.getInstance().getString("address", ""));
                getTimeZone();
            } else {
            }
        } else if (requestCode == REQUEST_CODE_WALLPAPER) {
            if (resultCode == Activity.RESULT_OK) {
                setBackground();
            } else {
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
                        Log.d("placeeeeeee", place.getLatLng().latitude + " " + place.getLatLng().longitude);
                        com.example.hiephoangvan.weather.databases.Places p
                                = new com.example.hiephoangvan.weather.databases.Places(place.getName().toString()
                                , place.getAddress().toString(), (float) place.getLatLng().latitude, (float) place.getLatLng().longitude);
                        boolean dupl = false;
                        for (com.example.hiephoangvan.weather.databases.Places pl : list) {
                            if (pl.getAddress().compareTo(p.getAddress()) == 0) {
                                dupl = true;
                                break;
                            }
                        }
                        if (!dupl) {
                            p.setIsHome(1);
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getIsHome() == 1) {
                                    com.example.hiephoangvan.weather.databases.Places places = list.get(i);
                                    places.setIsHome(0);
                                    Datamanager.getInstance().updatePlace(places);
                                    break;
                                }
                            }
                            Datamanager.getInstance().addPlace(p);
                            UtilPref.getInstance().setFloat("lat", p.getLat());
                            UtilPref.getInstance().setFloat("lon", p.getLon());
                            UtilPref.getInstance().setString("address", p.getAddress());
                            getTimeZone();
                            binding.toolbars.tvTitleToolbar.setText(p.getAddress());
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

    public void getTimeZone() {
        Service service = RetrofitInstance.getRetrofitInstance2().create(Service.class);
        Map<String, String> data = new HashMap<>();
        data.put("lat", UtilPref.getInstance().getFloat("lat", 0) + "");
        data.put("lng", UtilPref.getInstance().getFloat("lon", 0) + "");
        data.put("username", "hiep1097");
        Observable<Zone> observable = service.getTimeZone(data);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BlockingBaseObserver<Zone>() {
                    @Override
                    public void onNext(Zone zone) {
                        UtilPref.getInstance().setString("timezone", zone.getTimezoneId());
                        Log.d("timezoneeeeeeee", UtilPref.getInstance().getString("timezone", ""));
                        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                            if (fragment != null) {
                                fragment.onActivityResult(REQUEST_CODE_PLACE, Activity.RESULT_OK, null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private void getLocationPermission() {
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
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.it_vitri:
                Intent intent = new Intent(MainActivity.this, PlaceActivity.class);
                startActivityForResult(intent, REQUEST_CODE_PLACE);
                binding.drawer.closeDrawer();
                break;
            case R.id.it_wallpaper:
                Intent intent1 = new Intent(MainActivity.this, WallpaperActivity.class);
                startActivityForResult(intent1, REQUEST_CODE_WALLPAPER);
                break;
        }
    }

    public void setBackground() {
        if (UtilPref.getInstance().getInt("wallpaperpos", 0) != 15) {
            binding.contentLayout.setBackground(UtilDrawable.getInstance().getDrawable("wallpaper"
                    + UtilPref.getInstance().getInt("wallpaperpos", 0)));
            binding.mScrollNav.setBackground(UtilDrawable.getInstance().getDrawable("wallpaper"
                    + UtilPref.getInstance().getInt("wallpaperpos", 0)));
        } else {
            String path = UtilPref.getInstance().getString("wallpaperpath", "");
            binding.contentLayout.setBackground(Drawable.createFromPath(path));
            binding.mScrollNav.setBackground(Drawable.createFromPath(path));
        }

    }
}
