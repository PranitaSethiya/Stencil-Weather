package com.saphion.stencilweather.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.ObjectAnimator;
import com.saphion.stencilweather.R;
import com.saphion.stencilweather.climacons.RainSunIV;
import com.saphion.stencilweather.modules.WLocation;
import com.saphion.stencilweather.utilities.Utils;

public class ForecastFragment extends Fragment {

    private static final String ARG_ID = "_id";

    private int mColor;
    private long locationID;
    Context mContext;
    private WLocation location;
    private boolean expanded;

    public static ForecastFragment newInstance(long locationID) {
        ForecastFragment fragment = new ForecastFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, locationID);
        fragment.setArguments(args);
        return fragment;
    }

    public ForecastFragment() {

    }

    public ForecastFragment setContext(Context mContext, WLocation location) {
        this.mContext = mContext;
        this.location = location;
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

        v = inflater.inflate(R.layout.fragment_forecast, null);

        FrameLayout flContainer = (FrameLayout) v.findViewById(R.id.flConditionIcon);
        try {
            ImageView iv = new RainSunIV(getActivity(), Utils.dpToPx(150, getActivity()), Utils.dpToPx(150, getActivity()), getResources().getColor(R.color.forecast_frag_top), getResources().getColor(R.color.forecast_tint_widget_top));
            flContainer.addView(iv);

            expanded = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("expanded_forecast", false);

            v.findViewById(R.id.ivExpand).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setExpand(!expanded, true);
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("expanded_forecast", !expanded).commit();
                    expanded = !expanded;
                }
            });

            setBackgroundColor(mColor);

            setExpand(expanded, false);

        } catch (Exception ignored) {
            ignored.printStackTrace();
        }


        return v;
    }

    private void setBackgroundColor(int mColor) {
        if (v != null) {
            v.findViewById(R.id.llMainWeatherContainer).setBackgroundColor(mColor);
        }
    }

    private void setExpand(boolean expand, final boolean animate) {
        if (v != null) {
            if (expand) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        (v.findViewById(R.id.tlTempContainer)).setAlpha(animate?0:255);
                        (v.findViewById(R.id.tlTempContainer)).setVisibility(View.VISIBLE);
                    }
                }, 10);
                YoYo.with(Techniques.FadeInDown)
                        .duration(animate?850:0).interpolate(new BounceInterpolator())
                        .playOn(v.findViewById(R.id.tlTempContainer));
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(v.findViewById(R.id.tlWeatherContainer), "translationY", -Utils.dpToPx(60, getActivity()), 0);
                objectAnimator.setDuration(animate?500:0);
                objectAnimator.start();

                ObjectAnimator rotate = ObjectAnimator.ofFloat(v.findViewById(R.id.ivExpand), "rotation", 0, 180);
                rotate.setDuration(animate?500:0);
                rotate.start();

            } else {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        (v.findViewById(R.id.tlTempContainer)).setAlpha(0);
                        (v.findViewById(R.id.tlTempContainer)).setVisibility(View.INVISIBLE);
                    }
                }, 510);
                YoYo.with(Techniques.FadeOutUp)
                        .duration(animate?300:0).interpolate(new BounceInterpolator())
                        .playOn(v.findViewById(R.id.tlTempContainer));

                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(v.findViewById(R.id.tlWeatherContainer), "translationY", 0, -Utils.dpToPx(60, getActivity()));
                objectAnimator.setDuration(animate?500:0);
                objectAnimator.start();

                ObjectAnimator rotate = ObjectAnimator.ofFloat(v.findViewById(R.id.ivExpand), "rotation", 180, 0);
                rotate.setDuration(animate?500:0);
                rotate.start();

            }
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        try {
            System.gc();
        } catch (Exception ignored) {
        }
    }

    private void loadData() {
        mColor = getResources().getColor(R.color.forecast_frag_background);
    }

    public int getColor() {
        return this.mColor;
    }

}