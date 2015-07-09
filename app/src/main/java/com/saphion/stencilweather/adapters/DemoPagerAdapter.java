package com.saphion.stencilweather.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.saphion.stencilweather.fragments.WeatherFragment;

import java.util.Random;

public class DemoPagerAdapter extends FragmentPagerAdapter {

    private int pagerCount = 5;

    private Random random = new Random();
    private Context mContext;
    private int mColor;

    public DemoPagerAdapter(FragmentManager fm, Context mContext, int mColor) {
        super(fm);
        this.mContext = mContext;
        this.mColor = mColor;
    }

    @Override public Fragment getItem(int i) {
        return WeatherFragment.newInstance(mColor).setContext(mContext);
    }

    @Override public int getCount() {
        return pagerCount;
    }
}