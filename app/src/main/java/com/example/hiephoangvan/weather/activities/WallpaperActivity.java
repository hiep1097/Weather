package com.example.hiephoangvan.weather.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.hiephoangvan.weather.R;
import com.example.hiephoangvan.weather.Utils.UtilPath;
import com.example.hiephoangvan.weather.Utils.UtilPref;
import com.example.hiephoangvan.weather.adapters.WallpaperAdapter;
import com.example.hiephoangvan.weather.decoration.MyItemDecoration;
import com.example.hiephoangvan.weather.models.Wallpaper;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;

public class WallpaperActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.recyclerViewWallpaper) RecyclerView mRecyclerViewWallpaper;
    @BindView(R.id.floatButton) FloatingActionButton mFloatingActionButton;
    @BindView(R.id.btnChoose) Button mBtnChoose;
    @BindView(R.id.btnExit) Button mBtnExit;
    private WallpaperAdapter adapter;
    private List<Wallpaper> list;
    private final int PICK_IMAGE = 3;
    boolean mReadExternalPermissionGranted;

    @Override
    public void setView() {
        list = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            Wallpaper wallpaper = new Wallpaper(i, "wallpaper" + i);
            list.add(wallpaper);
        }
        adapter = new WallpaperAdapter(list,this);
        mRecyclerViewWallpaper.setAdapter(adapter);
        mRecyclerViewWallpaper.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerViewWallpaper.addItemDecoration(new MyItemDecoration(this, R.dimen.item_offset));
        mBtnChoose.setOnClickListener(this::onClick);
        mBtnExit.setOnClickListener(this::onClick);
        mFloatingActionButton.setOnClickListener(this::onClick);
    }

    @Override
    public int layoutActivity() {
        return R.layout.activity_wallpaper;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mReadExternalPermissionGranted = false;
        if (requestCode == 4) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(WallpaperActivity.this, "Permision Write File is Granted", Toast.LENGTH_SHORT).show();
                mReadExternalPermissionGranted = true;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            } else {
                Toast.makeText(WallpaperActivity.this, "Permision Write File is Denied", Toast.LENGTH_SHORT).show();

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void initPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //Permisson don't granted
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(WallpaperActivity.this, "Permission isn't granted ", Toast.LENGTH_SHORT).show();
                }
                // Permisson don't granted and dont show dialog again.
                else {
                    Toast.makeText(WallpaperActivity.this, "Permisson don't granted and dont show dialog again ", Toast.LENGTH_SHORT).show();
                }
                //Register permission
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 4);

            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChoose:
                int pos = adapter.getWallpaperPos();
                UtilPref.getInstance().setInt("wallpaperpos",pos);
                setResult(Activity.RESULT_OK);
                finish();
                break;
            case R.id.btnExit:
                finish();
                break;
            case R.id.floatButton:
                initPermission();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode==Activity.RESULT_OK) {
            //TODO: action
            String path = UtilPath.getInstance().getPath(this,data.getData());
            UtilPref.getInstance().setInt("wallpaperpos", 15);
            UtilPref.getInstance().setString("wallpaperpath",path);
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
