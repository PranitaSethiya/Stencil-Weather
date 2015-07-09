package com.saphion.stencilweather.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.saphion.stencilweather.R;
import com.saphion.stencilweather.climacons.RainSunIV;
import com.saphion.stencilweather.utilities.Utils;

public class WeatherFragment extends Fragment {

    private static final String ARG_COLOR = "color";

    private int mColor;
    Context mContext;

    public static WeatherFragment newInstance(int param1) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLOR, param1);
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
            mColor = getArguments().getInt(ARG_COLOR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_layout, null);

        FrameLayout flContainer = (FrameLayout) v.findViewById(R.id.flConditionIcon);

        RainSunIV iv = new RainSunIV(mContext, Utils.dpToPx(180, mContext), Utils.dpToPx(180, mContext), mColor);

        flContainer.addView(iv);

//        v.setBackgroundColor(mColor);

        return v;
    }
}