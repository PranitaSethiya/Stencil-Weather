package com.saphion.stencilweather.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.saphion.stencilweather.R;
import com.saphion.stencilweather.activities.MainActivity;
import com.saphion.stencilweather.climacons.RainSunIV;
import com.saphion.stencilweather.modules.WLocation;
import com.saphion.stencilweather.utilities.Utils;

import java.util.Random;

public class WeatherFragment extends Fragment {

    private static final String ARG_ID = "_id";

    private int mColor;
    private long locationID;
    Context mContext;

    public static WeatherFragment newInstance(long locationID) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, locationID);
        fragment.setArguments(args);
        return fragment;
    }

    public WeatherFragment() {

    }

    public WeatherFragment setContext(Context mContext){
        this.mContext = mContext;
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            locationID = getArguments().getLong(ARG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        loadData();

        View v = inflater.inflate(R.layout.fragment_layout, null);

        FrameLayout flContainer = (FrameLayout) v.findViewById(R.id.flConditionIcon);

        RainSunIV iv = new RainSunIV(mContext, Utils.dpToPx(180, mContext), Utils.dpToPx(180, mContext), mColor);

        flContainer.addView(iv);

//        WLocation wLocation = WLocation.findById(WLocation.class, locationID);

        return v;
    }

    private void loadData() {
        Random random = new Random();
        mColor = (0xff000000 | random.nextInt(0x00ffffff));
    }

    public int getColor(){
        return this.mColor;
    }
}