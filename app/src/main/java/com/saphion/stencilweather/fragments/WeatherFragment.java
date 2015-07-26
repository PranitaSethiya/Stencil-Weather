package com.saphion.stencilweather.fragments;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.saphion.stencilweather.R;
import com.saphion.stencilweather.activities.MainActivity;
import com.saphion.stencilweather.climacons.RainSunIV;
import com.saphion.stencilweather.utilities.Utils;

import java.util.Calendar;
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

    public WeatherFragment setContext(Context mContext) {
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

    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        loadData();

        v = inflater.inflate(R.layout.fragment_layout, null);

        FrameLayout flContainer = (FrameLayout) v.findViewById(R.id.flConditionIcon);
        try {
            ImageView iv = new RainSunIV(mContext, Utils.dpToPx(160, mContext), Utils.dpToPx(160, mContext), mColor);
            flContainer.addView(iv);

            SeekBar sb = (SeekBar) v.findViewById(R.id.sbTimeSeek);
            sb.setThumb(new BitmapDrawable(mContext.getResources(), Utils.getTimeThumb(mContext, Calendar.getInstance().get(Calendar.HOUR), 0)));
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    seekBar.setThumb(new BitmapDrawable(mContext.getResources(), Utils.getTimeThumb(mContext, Calendar.getInstance().get(Calendar.HOUR) + (progress * 3), 0)));
                    ((MainActivity)getActivity()).setToolBarSubTitle(null);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });


        } catch (Exception ignored) {
        }

//        WLocation wLocation = WLocation.findById(WLocation.class, locationID);




        return v;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        try {
            System.gc();
        }catch(Exception ignored){}
    }

    private void loadData() {
        Random random = new Random();
        mColor = (0xff000000 | random.nextInt(0x00ffffff));
    }

    public int getColor() {
        return this.mColor;
    }

    public void onFragmentSelected() {
        if(v != null){
            (v.findViewById(R.id.containerTemperatureAndCondition)).setVisibility(View.VISIBLE);
            YoYo.with(Techniques.FadeInLeft)
                    .duration(500).interpolate(new BounceInterpolator())
                    .playOn(v.findViewById(R.id.containerTemperatureAndCondition));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    (v.findViewById(R.id.llWeatherContainer1)).setAlpha(0);
                    (v.findViewById(R.id.llWeatherContainer1)).setVisibility(View.VISIBLE);
                }
            }, 110);
            YoYo.with(Techniques.FadeInLeft)
                    .duration(500).interpolate(new BounceInterpolator()).delay(100)
                    .playOn(v.findViewById(R.id.llWeatherContainer1));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    (v.findViewById(R.id.llWeatherContainer2)).setAlpha(0);
                    (v.findViewById(R.id.llWeatherContainer2)).setVisibility(View.VISIBLE);
                }
            }, 220);
            YoYo.with(Techniques.FadeInLeft)
                    .duration(500).interpolate(new BounceInterpolator()).delay(200)
                    .playOn(v.findViewById(R.id.llWeatherContainer2));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    (v.findViewById(R.id.llWeatherContainer3)).setAlpha(0);
                    (v.findViewById(R.id.llWeatherContainer3)).setVisibility(View.VISIBLE);
                }
            }, 310);
            YoYo.with(Techniques.FadeInLeft)
                    .duration(500).interpolate(new BounceInterpolator()).delay(300)
                    .playOn(v.findViewById(R.id.llWeatherContainer3));
        }
    }

    public void onFragmentUnSelected() {
        if(v != null){
            (v.findViewById(R.id.containerTemperatureAndCondition)).setVisibility(View.INVISIBLE);
            (v.findViewById(R.id.llWeatherContainer1)).setVisibility(View.INVISIBLE);
            (v.findViewById(R.id.llWeatherContainer2)).setVisibility(View.INVISIBLE);
            (v.findViewById(R.id.llWeatherContainer3)).setVisibility(View.INVISIBLE);
        }
    }
}