package com.example.hiephoangvan.weather.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.hiephoangvan.weather.fragments.FragmentHourly;
import com.example.hiephoangvan.weather.fragments.FragmentToday;

public class ViewpagerAdapter extends FragmentStatePagerAdapter {
    public ViewpagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new FragmentToday();
                break;
            case 1:
                fragment = new FragmentHourly();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0:
                title = "Today";
                break;
            case 1:
                title = "Hourly";
                break;
        }
        return title;
    }
}