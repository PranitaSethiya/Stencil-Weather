package com.saphion.stencilweather.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.saphion.stencilweather.R;
import com.saphion.stencilweather.activities.MainActivity;
import com.saphion.stencilweather.climacons.RainSunIV;
import com.saphion.stencilweather.modules.WLocation;
import com.saphion.stencilweather.utilities.TimeHelpers;
import com.saphion.stencilweather.utilities.Utils;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

public class WeatherFragment extends Fragment {

    private static final String ARG_ID = "_id";

    private int mColor;
    private long locationID;
    Context mContext;
    private WLocation location;
    private boolean expanded;

    public static WeatherFragment newInstance(long locationID) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, locationID);
        fragment.setArguments(args);
        return fragment;
    }

    public WeatherFragment() {

    }

    public WeatherFragment setContext(Context mContext, WLocation location) {
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
    Calendar calendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        loadData();

        v = inflater.inflate(R.layout.fragment_layout, null);

        FrameLayout flContainer = (FrameLayout) v.findViewById(R.id.flConditionIcon);
        try {
            ImageView iv = new RainSunIV(mContext, Utils.dpToPx(140, mContext), Utils.dpToPx(140, mContext), mColor, Color.WHITE);
            flContainer.addView(iv);


            calendar = Calendar
                    .getInstance(TimeZone.getTimeZone(location.getTimezone()));

            final SeekBar sb = (SeekBar) v.findViewById(R.id.sbTimeSeek);

            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), Color.TRANSPARENT, mColor);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    sb.setThumb(new BitmapDrawable(mContext.getResources(), Utils.getTimeThumb(mContext, (Integer)animator.getAnimatedValue(), calendar.get(Calendar.HOUR), 0)));
                }

            });
            colorAnimation.setDuration(350);
            colorAnimation.start();


            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    seekBar.setThumb(new BitmapDrawable(mContext.getResources(), Utils.getTimeThumb(mContext, mColor, calendar.get(Calendar.HOUR) + (progress * 3), 0)));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            ((MainActivity)getActivity()).setToolBarColor(getColor());





            callHandler();
            setTime(null);

            expanded = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("expanded", false);

            v.findViewById(R.id.ivExpand).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setExpand(!expanded, true);
                    PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean("expanded", !expanded).commit();
                    expanded = !expanded;
                }
            });

            setExpand(expanded, false);

            onFragmentUnSelected();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onFragmentSelected();
                }
            }, 100);

        } catch (Exception ignored) {
            ignored.printStackTrace();
        }


        return v;
    }

    private void setExpand(boolean expand, final boolean animate) {
        if(v != null){
            RotateAnimation anim = new RotateAnimation(expand?0:180, expand?180:360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(animate?500:1);
            anim.setFillAfter(true);
            v.findViewById(R.id.ivExpand).startAnimation(anim);
            if(expand) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        (v.findViewById(R.id.tlWeatherContainer)).setAlpha(animate?0:255);
                        (v.findViewById(R.id.tlWeatherContainer)).setVisibility(View.VISIBLE);
                    }
                }, 210);
                YoYo.with(Techniques.FadeInDown)
                        .duration(animate?500:1).interpolate(new BounceInterpolator()).delay(200)
                        .playOn(v.findViewById(R.id.tlWeatherContainer));

//                TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, 0);
//                translateAnimation.setDuration(500);
//                translateAnimation.setFillEnabled(true);
//                translateAnimation.setFillAfter(true);
//                v.findViewById(R.id.llMainWeatherContainer).startAnimation(translateAnimation);

                ObjectAnimator objectAnimator= ObjectAnimator.ofFloat(v.findViewById(R.id.llMainWeatherContainer), "translationY", Utils.dpToPx(20, mContext), 0);
                objectAnimator.setDuration(animate?500:1);
                objectAnimator.start();


            }else {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        (v.findViewById(R.id.tlWeatherContainer)).setAlpha(0);
                        (v.findViewById(R.id.tlWeatherContainer)).setVisibility(View.INVISIBLE);
                    }
                }, 510);
                YoYo.with(Techniques.FadeOutUp)
                        .duration(animate?500:1).interpolate(new BounceInterpolator()).delay(200)
                        .playOn(v.findViewById(R.id.tlWeatherContainer));

//                TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, Utils.dpToPx(20, mContext));
//                translateAnimation.setDuration(500);
//                translateAnimation.setFillEnabled(true);
//                translateAnimation.setFillAfter(true);
//                v.findViewById(R.id.llMainWeatherContainer).startAnimation(translateAnimation);

                ObjectAnimator objectAnimator= ObjectAnimator.ofFloat(v.findViewById(R.id.llMainWeatherContainer), "translationY", 0, Utils.dpToPx(20, mContext));
                objectAnimator.setDuration(animate?500:1);
                objectAnimator.start();

            }
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        try {
            System.gc();
        }catch(Exception ignored){}
    }

    private void loadData() {
//        Random random = new Random();
//        mColor = (0xff000000 | random.nextInt(0x00ffffff));
//        mColor = 0xff1e8bd4;
        Random r = new Random();
        this.mColor = Utils.getTemperatureColor(getActivity(), r.nextInt(47) - 11);
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
                    (v.findViewById(R.id.llWeatherContainer0)).setAlpha(0);
                    (v.findViewById(R.id.llWeatherContainer0)).setVisibility(View.VISIBLE);
                }
            }, 110);
            YoYo.with(Techniques.FadeInLeft)
                    .duration(500).interpolate(new BounceInterpolator()).delay(100)
                    .playOn(v.findViewById(R.id.llWeatherContainer0));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    (v.findViewById(R.id.llWeatherContainer1)).setAlpha(0);
                    (v.findViewById(R.id.llWeatherContainer1)).setVisibility(View.VISIBLE);
                }
            }, 210);
            YoYo.with(Techniques.FadeInLeft)
                    .duration(500).interpolate(new BounceInterpolator()).delay(200)
                    .playOn(v.findViewById(R.id.llWeatherContainer1));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    (v.findViewById(R.id.llWeatherContainer2)).setAlpha(0);
                    (v.findViewById(R.id.llWeatherContainer2)).setVisibility(View.VISIBLE);
                }
            }, 310);
            YoYo.with(Techniques.FadeInLeft)
                    .duration(500).interpolate(new BounceInterpolator()).delay(300)
                    .playOn(v.findViewById(R.id.llWeatherContainer2));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    (v.findViewById(R.id.llWeatherContainer3)).setAlpha(0);
                    (v.findViewById(R.id.llWeatherContainer3)).setVisibility(View.VISIBLE);
                }
            }, 410);
            YoYo.with(Techniques.FadeInLeft)
                    .duration(500).interpolate(new BounceInterpolator()).delay(400)
                    .playOn(v.findViewById(R.id.llWeatherContainer3));
        }
    }

    public void onFragmentUnSelected() {
        if(v != null){
            (v.findViewById(R.id.containerTemperatureAndCondition)).setVisibility(View.INVISIBLE);
            (v.findViewById(R.id.llWeatherContainer0)).setVisibility(View.INVISIBLE);
            (v.findViewById(R.id.llWeatherContainer1)).setVisibility(View.INVISIBLE);
            (v.findViewById(R.id.llWeatherContainer2)).setVisibility(View.INVISIBLE);
            (v.findViewById(R.id.llWeatherContainer3)).setVisibility(View.INVISIBLE);
        }
    }

    public void setTime(String subTitle) {
        try {
            if (subTitle == null) {
                Calendar c = Calendar
                            .getInstance(TimeZone.getTimeZone(location.getTimezone()));


                ((TextView)v.findViewById(R.id.tvTime)).setText(TimeHelpers.getWeek(c.get(Calendar.DAY_OF_WEEK))
                        + ", "
                        + TimeHelpers.getMonth(c
                        .get(Calendar.MONTH))
                        + " "
                        + c.get(Calendar.DAY_OF_MONTH)
                        + " | "
                        + (c.get(Calendar.HOUR) == 0 ? "12" : c
                        .get(Calendar.HOUR))
                        + ":"
                        + ((c.get(Calendar.MINUTE) + "").length() == 1 ? "0"
                        + c.get(Calendar.MINUTE)
                        : c.get(Calendar.MINUTE))
                        + (c.get(Calendar.AM_PM) == Calendar.AM ? " AM"
                        : " PM"));
            } else {
                ((TextView)v.findViewById(R.id.tvTime)).setText(subTitle);
            }


        } catch (Exception ignored) {
        }

    }

    private void callHandler() {
        long callback = (60 - Calendar.getInstance().get(Calendar.SECOND)) * 1000;
        handler.removeCallbacks(drawRunner);
        if (callback == 0)
            handler.postDelayed(drawRunner, 60000);
        else
            handler.postDelayed(drawRunner, callback);

    }

    private final Handler handler = new Handler();
    private final Runnable drawRunner = new Runnable() {
        public void run() {
            setTime(null);
            callHandler();

        }

    };
}