package com.saphion.stencilweather.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.saphion.stencilweather.R;
import com.saphion.stencilweather.modules.WLocation;
import com.saphion.stencilweather.utilities.Constant;
import com.saphion.stencilweather.utilities.LocationUtils;
import com.saphion.stencilweather.utilities.PreferenceUtil;
import com.saphion.stencilweather.utilities.SplashView;
import com.saphion.stencilweather.utilities.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sachin on 5/8/15.
 */
public class SplashActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private GoogleApiClient mGoogleApiClient;
    View mBackground;
    RadioButton rb1, rb2, rb3, rb4;
    private ViewPager viewPager;
    TextView tvGetStartedDone, tvGetStartedSkip;
    View ivGetStartedNext;
    boolean isAnimComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            int color = (getResources().getColor(R.color.main_button_blue_normal));

            color = (color & 0xfefefefe) >> 1;

            window.setStatusBarColor(color);
        }

        setContentView(R.layout.activity_splash);

        rb1 = (RadioButton) findViewById(R.id.rb1);
        rb2 = (RadioButton) findViewById(R.id.rb2);
        rb3 = (RadioButton) findViewById(R.id.rb3);
        rb4 = (RadioButton) findViewById(R.id.rb4);

        tvGetStartedDone = (TextView) findViewById(R.id.tvGetStartedDone);
        tvGetStartedSkip = (TextView) findViewById(R.id.tvGetStartedSkip);
        ivGetStartedNext = findViewById(R.id.bGetStartedNext);

        rb1.setChecked(true);
        mBackground = findViewById(R.id.mainSplashContainer);

        setupViewPager(viewPager = (ViewPager) findViewById(R.id.viewpagerSplash));

        startLocationWork();

    }

    boolean fromDialog = false;

    Handler dialogHandler = new Handler();
    Runnable dialogRunnable = new Runnable() {
        @Override
        public void run() {
            if (isAnimComplete) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                builder.setTitle("Alert");
                builder.setMessage(Html.fromHtml("Location Services are not enabled.<br/><br/><i>Location Services help us determine your current location.</i><br/><br/>Enable them now?"));
                builder.setPositiveButton("SURE", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        fromDialog = true;

                    }
                });
                builder.setNegativeButton("CANCEL", null);
                builder.setCancelable(false);
                builder.show();
            } else {
                dialogHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (fromDialog) {
            startLocationWork();
            fromDialog = false;
        }
    }

    private void startLocationWork() {
        if (Utils.isLocationEnabled(SplashActivity.this))
            buildGoogleApiClient();
        else {
            dialogHandler.post(dialogRunnable);
        }
    }

    Adapter viewPagerAdapter;

    private void setupViewPager(ViewPager viewPager) {

        viewPagerAdapter = new Adapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(Fragment1.newInstance());

        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        rb1.setChecked(true);
                        mBackground.setBackgroundColor(getResources().getColor(R.color.sunny_yellow));
                        ((Fragment1) viewPagerAdapter.getItem(0)).onFragmentSelected();

                        ivGetStartedNext.setVisibility(View.GONE);
                        tvGetStartedDone.setVisibility(View.GONE);
                        tvGetStartedSkip.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        rb2.setChecked(true);
                        setBackgroundColor(mBackground, getResources().getColor(R.color.option2));
                        ((Fragment1) viewPagerAdapter.getItem(0)).onFragmentUnselected();
                        ((Fragment2) viewPagerAdapter.getItem(1)).onFragmentSelected();

                        ivGetStartedNext.setVisibility(View.VISIBLE);
                        tvGetStartedDone.setVisibility(View.GONE);
                        tvGetStartedSkip.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        rb3.setChecked(true);
                        setBackgroundColor(mBackground, getResources().getColor(R.color.option8));
                        ((Fragment3) viewPagerAdapter.getItem(2)).onFragmentSelected();
                        ivGetStartedNext.setVisibility(View.VISIBLE);
                        tvGetStartedDone.setVisibility(View.GONE);
                        tvGetStartedSkip.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        rb4.setChecked(true);
                        setBackgroundColor(mBackground, getResources().getColor(R.color.option10));
                        ((Fragment1) viewPagerAdapter.getItem(0)).onFragmentUnselected();

                        if (WLocation.count(WLocation.class, null, null) > 0) {
                            ivGetStartedNext.setVisibility(View.GONE);
                            tvGetStartedDone.setVisibility(View.VISIBLE);
                            tvGetStartedSkip.setVisibility(View.GONE);
                            ((Fragment4) viewPagerAdapter.getItem(3)).onFragmentSelected();
                        } else {
                            ivGetStartedNext.setVisibility(View.GONE);
                            tvGetStartedDone.setVisibility(View.GONE);
                            tvGetStartedSkip.setVisibility(View.GONE);
                            ((Fragment4) viewPagerAdapter.getItem(3)).onFragmentSelectedLocation();

                        }
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setOffscreenPageLimit(4);

    }

    public void setViewPagerPosition(int viewPagerPosition) {
        viewPager.setCurrentItem(viewPagerPosition, true);
    }

    class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Stencil", "OnConnected");
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d("Stencil", "Lat: " + String.valueOf(mLastLocation.getLatitude()) + " Lng: " + String.valueOf(mLastLocation.getLongitude()));
            new AsyncTask<Location, Void, WLocation>() {

                @Override
                protected WLocation doInBackground(Location... locations) {
                    WLocation location = LocationUtils.getLocationFromLatLng(locations[0].getLatitude(), locations[0].getLongitude());
                    if (location != null) {
                        location.setIsMyLocation(true);
                        location.checkAndSave();
                    }
                    return location;
                }

                @Override
                protected void onPostExecute(WLocation wLocation) {
                    super.onPostExecute(wLocation);

                }
            }.execute(mLastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Stencil", "OnConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Stencil", "OnConnectionFailed");
    }


    public void setBackgroundColor(final View v, int color) {
        Integer colorFrom = Color.TRANSPARENT;
        Drawable background = v.getBackground();
        if (background instanceof ColorDrawable)
            colorFrom = ((ColorDrawable) background).getColor();
        Integer colorTo = color;
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                v.setBackgroundColor((Integer) animator.getAnimatedValue());
            }

        });
        colorAnimation.setDuration(350);
        colorAnimation.start();
    }

    private void onFirstFragmentAnimationComplete() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                (findViewById(R.id.rgContainer)).setAlpha(0);
                (findViewById(R.id.rgContainer)).setVisibility(View.VISIBLE);
            }
        }, 10);
        YoYo.with(Techniques.FadeIn).interpolate(new OvershootInterpolator()).duration(500).playOn(findViewById(R.id.rgContainer));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tvGetStartedSkip.setAlpha(0);
                tvGetStartedSkip.setVisibility(View.VISIBLE);
            }
        }, 10);
        YoYo.with(Techniques.FadeIn).interpolate(new OvershootInterpolator()).duration(500).playOn(tvGetStartedSkip);

        ivGetStartedNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currPos = viewPager.getCurrentItem();
                if (currPos < viewPagerAdapter.getCount() - 1)
                    setViewPagerPosition(currPos + 1);
            }
        });

        tvGetStartedSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewPagerPosition(viewPagerAdapter.getCount() - 1);
            }
        });

        tvGetStartedDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.incAppCount(SplashActivity.this);
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        isAnimComplete = true;

        viewPagerAdapter.addFragment(Fragment2.newInstance());
        viewPagerAdapter.addFragment(Fragment3.newInstance());
        viewPagerAdapter.addFragment(Fragment4.newInstance());

        findViewById(R.id.viewSplash).setVisibility(View.VISIBLE);
    }


    //Fragment 1

    public static class Fragment1 extends Fragment {

        boolean animationInComplete = true;

        public static Fragment1 newInstance() {
            return new Fragment1();
        }

        public Fragment1() {

        }

        View v;
        SplashView splashView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            v = inflater.inflate(R.layout.splash_frag1, null);
            float height = (float) (getResources().getDisplayMetrics().heightPixels * 0.75);
            float width = getResources().getDisplayMetrics().widthPixels;


            splashView = new SplashView(Fragment1.this,
                    height, width,
                    getResources().getColor(R.color.brown), getResources().getColor(R.color.sunny_yellow));

            LayoutParams lp = new LayoutParams((int) width, (int) height);
            splashView.setLayoutParams(lp);

            FrameLayout flContainer = (FrameLayout) v.findViewById(R.id.sunContainer);
            flContainer.addView(splashView);

            setupViews();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onAnimationComplete();
                }
            }, 10000);

            return v;

        }

        private void setupViews() {
            v.findViewById(R.id.tvHello).setVisibility(View.INVISIBLE);
            v.findViewById(R.id.tvWelcome).setVisibility(View.INVISIBLE);
            v.findViewById(R.id.tvTo).setVisibility(View.INVISIBLE);
            v.findViewById(R.id.rlAppNameContainer).setVisibility(View.INVISIBLE);
            v.findViewById(R.id.rlGetStartedContainer).setVisibility(View.INVISIBLE);

            v.findViewById(R.id.fabGetStarted).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((SplashActivity) getActivity()).setViewPagerPosition(1);
                }
            });
        }


        public void onAnimationComplete() {
            if (animationInComplete) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        (v.findViewById(R.id.tvHello)).setAlpha(0);
                        (v.findViewById(R.id.tvHello)).setVisibility(View.VISIBLE);
                    }
                }, 210);
                YoYo.with(Techniques.SlideInLeft).interpolate(new OvershootInterpolator()).duration(800).delay(200).playOn(v.findViewById(R.id.tvHello));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        (v.findViewById(R.id.tvWelcome)).setAlpha(0);
                        (v.findViewById(R.id.tvWelcome)).setVisibility(View.VISIBLE);
                    }
                }, 260);
                YoYo.with(Techniques.SlideInRight).interpolate(new OvershootInterpolator()).duration(950).delay(250).playOn(v.findViewById(R.id.tvWelcome));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        (v.findViewById(R.id.tvTo)).setAlpha(0);
                        (v.findViewById(R.id.tvTo)).setVisibility(View.VISIBLE);
                    }
                }, 310);
                YoYo.with(Techniques.SlideInLeft).interpolate(new OvershootInterpolator()).duration(800).delay(300).playOn(v.findViewById(R.id.tvTo));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        (v.findViewById(R.id.rlAppNameContainer)).setAlpha(0);
                        (v.findViewById(R.id.rlAppNameContainer)).setVisibility(View.VISIBLE);
                    }
                }, 360);
                YoYo.with(Techniques.SlideInRight).interpolate(new OvershootInterpolator()).duration(950).delay(350).playOn(v.findViewById(R.id.rlAppNameContainer));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        (v.findViewById(R.id.rlGetStartedContainer)).setAlpha(0);
                        (v.findViewById(R.id.rlGetStartedContainer)).setVisibility(View.VISIBLE);
                    }
                }, 410);
                YoYo.with(Techniques.BounceInUp).interpolate(new OvershootInterpolator()).duration(1000).delay(1000).playOn(v.findViewById(R.id.rlGetStartedContainer));


                animationInComplete = false;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((SplashActivity) getActivity()).onFirstFragmentAnimationComplete();
                    }
                }, 2200);
            }

        }

        public void onFragmentSelected() {
            if (splashView != null)
                if (splashView.getVisibility() != View.VISIBLE)
                    splashView.setVisibility(View.VISIBLE);

        }

        public void onFragmentUnselected() {
            if (splashView != null)
                if (splashView.getVisibility() == View.VISIBLE)
                    splashView.setVisibility(View.INVISIBLE);
        }
    }

    //Fragment 2

    public static class Fragment2 extends Fragment implements View.OnClickListener {


        public static Fragment2 newInstance() {
            return new Fragment2();
        }

        public Fragment2() {

        }

        View v;
        View title;
        TextView tvPrefTemp, tvPrefPrecipitation, tvPrefWind, tvPrefPressure, tvPrefTime;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            v = inflater.inflate(R.layout.splash_frag2, null);
            title = v.findViewById(R.id.tvUnitsTitle);

            tvPrefTemp = (TextView) v.findViewById(R.id.tvPrefTempVal);
            tvPrefPrecipitation = (TextView) v.findViewById(R.id.tvPrefPrecipitationVal);
            tvPrefWind = (TextView) v.findViewById(R.id.tvPrefWindVal);
            tvPrefPressure = (TextView) v.findViewById(R.id.tvPrefPressureVal);
            tvPrefTime = (TextView) v.findViewById(R.id.tvPrefTimeVal);

            v.findViewById(R.id.prefTempContainer).setOnClickListener(this);
            v.findViewById(R.id.prefPrecipitationContainer).setOnClickListener(this);
            v.findViewById(R.id.prefWindContainer).setOnClickListener(this);
            v.findViewById(R.id.prefPressureContainer).setOnClickListener(this);
            v.findViewById(R.id.prefTimeContainer).setOnClickListener(this);

            tvPrefTemp.setText(Constant.TEMPERATURE_UNITS[PreferenceUtil.getTemperatureUnit(getActivity())]);
            tvPrefPrecipitation.setText(Constant.PRECIPITATION_UNITS[PreferenceUtil.getPrecipitationUnit(getActivity())]);
            tvPrefWind.setText(Constant.WIND_UNITS[PreferenceUtil.getWindUnit(getActivity())]);
            tvPrefPressure.setText(Constant.PRESSURE_UNITS[PreferenceUtil.getPressureUnit(getActivity())]);
            tvPrefTime.setText(Constant.TIME_UNITS[PreferenceUtil.getTimeUnit(getActivity())]);

            setupViews();
            return v;

        }

        private void setupViews() {
            title.setVisibility(View.INVISIBLE);
        }


        public void onFragmentSelected() {

            if (title.getVisibility() == View.INVISIBLE) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        title.setAlpha(0);
                        title.setVisibility(View.VISIBLE);
                    }
                }, 60);
                YoYo.with(Techniques.SlideInLeft).interpolate(new OvershootInterpolator()).duration(800).delay(50).playOn(title);
                bounceScaleAnimation(v.findViewById(R.id.prefTempContainer), 900);
                bounceScaleAnimation(v.findViewById(R.id.prefPrecipitationContainer), 900);
                bounceScaleAnimation(v.findViewById(R.id.prefWindContainer), 950);
                bounceScaleAnimation(v.findViewById(R.id.prefPressureContainer), 950);
                bounceScaleAnimation(v.findViewById(R.id.prefTimeContainer), 950);
            }

        }

        public void bounceScaleAnimation(View mView, int delay) {
            Animation pulse = AnimationUtils.loadAnimation(getActivity(), R.anim.pulse);
            pulse.setStartOffset(delay);
            mView.startAnimation(pulse);
        }

        @Override
        public void onClick(View view) {
            RotateAnimation anim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            int duration = 200;
            anim.setDuration(duration);
            anim.setFillAfter(true);
            switch (view.getId()) {
                case R.id.prefTempContainer:
                    tvPrefTemp.startAnimation(anim);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int nextVal = PreferenceUtil.getNextValue(PreferenceUtil.getTemperatureUnit(getActivity()), Constant.TEMPERATURE_UNITS);
                            tvPrefTemp.setText(
                                    Constant.TEMPERATURE_UNITS[nextVal]);
                            PreferenceUtil.setTemperatureUnit(getActivity(), nextVal);
                        }
                    }, duration / 2);
                    break;
                case R.id.prefPrecipitationContainer:
                    tvPrefPrecipitation.startAnimation(anim);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int nextVal = PreferenceUtil.getNextValue(PreferenceUtil.getPrecipitationUnit(getActivity()), Constant.PRECIPITATION_UNITS);
                            tvPrefPrecipitation.setText(
                                    Constant.PRECIPITATION_UNITS[nextVal]);
                            PreferenceUtil.setPrecipitationUnit(getActivity(), nextVal);
                        }
                    }, duration / 2);
                    break;
                case R.id.prefPressureContainer:
                    tvPrefPressure.startAnimation(anim);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int nextVal = PreferenceUtil.getNextValue(PreferenceUtil.getPressureUnit(getActivity()), Constant.PRESSURE_UNITS);
                            tvPrefPressure.setText(
                                    Constant.PRESSURE_UNITS[nextVal]);
                            PreferenceUtil.setPressureUnit(getActivity(), nextVal);
                        }
                    }, duration / 2);
                    break;
                case R.id.prefTimeContainer:
                    tvPrefTime.startAnimation(anim);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int nextVal = PreferenceUtil.getNextValue(PreferenceUtil.getTimeUnit(getActivity()), Constant.TIME_UNITS);
                            tvPrefTime.setText(
                                    Constant.TIME_UNITS[nextVal]);
                            PreferenceUtil.setTimeUnit(getActivity(), nextVal);
                        }
                    }, duration / 2);
                    break;
                case R.id.prefWindContainer:
                    tvPrefWind.startAnimation(anim);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int nextVal = PreferenceUtil.getNextValue(PreferenceUtil.getWindUnit(getActivity()), Constant.WIND_UNITS);
                            tvPrefWind.setText(
                                    Constant.WIND_UNITS[nextVal]);
                            PreferenceUtil.setWindUnit(getActivity(), nextVal);
                        }
                    }, duration / 2);
                    break;
            }
        }
    }

    //Fragment 3

    public static class Fragment3 extends Fragment {


        public static Fragment3 newInstance() {
            return new Fragment3();
        }

        public Fragment3() {

        }

        View v;
        View title, subtitle;
        View rgContainer;
        RadioButton automatic, manual;
        RecyclerView recyclerView;
        RadioGroup rg;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            v = inflater.inflate(R.layout.splash_frag3, null);
            title = v.findViewById(R.id.tvColorTitle);
            subtitle = v.findViewById(R.id.tvColorSubtitle);
            automatic = (RadioButton) v.findViewById(R.id.rbAutomatic);
            manual = (RadioButton) v.findViewById(R.id.rbManual);

            recyclerView = (RecyclerView) v.findViewById(R.id.rvColors);

            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(llm);
            recyclerView.setAdapter(new ColorAdapter());
            recyclerView.scrollToPosition(PreferenceUtil.getBackgroundColor(getActivity()));

            rgContainer = v.findViewById(R.id.frag3Container);

            rg = (RadioGroup) v.findViewById(R.id.rgColorContainer);

            setupViews();
            return v;

        }

        private void setupViews() {
            title.setVisibility(View.INVISIBLE);
            subtitle.setVisibility(View.INVISIBLE);
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.rbAutomatic:
                            PreferenceUtil.setBackgroundType(getActivity(), 0);
                            showRecycler(false, true);
                            break;
                        case R.id.rbManual:
                            PreferenceUtil.setBackgroundType(getActivity(), 1);
                            showRecycler(true, true);
                            break;
                    }
                }
            });

        }


        public void showRecycler(boolean expand, final boolean animate){
            if(v != null){
                if(expand) {
                    ObjectAnimator objectAnimator= ObjectAnimator.ofFloat(rgContainer, "translationY", Utils.dpToPx(30, getActivity()), 0);
                    objectAnimator.setDuration(animate?500:1);
                    objectAnimator.start();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setAlpha(animate ? 0 : 255);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }, 110);
                    YoYo.with(Techniques.SlideInRight)
                            .duration(animate ? 500 : 1).interpolate(new BounceInterpolator()).delay(100)
                            .playOn(recyclerView);

                } else {
                    ObjectAnimator objectAnimator= ObjectAnimator.ofFloat(rgContainer, "translationY", 0, Utils.dpToPx(30, getActivity()));
                    objectAnimator.setDuration(animate?500:1);
                    objectAnimator.setStartDelay(200);
                    objectAnimator.start();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setAlpha(0);
                            recyclerView.setVisibility(View.INVISIBLE);
                        }
                    }, 510);
                    YoYo.with(Techniques.SlideOutRight)
                            .duration(animate?500:1).interpolate(new BounceInterpolator())
                            .playOn(recyclerView);
                }
            }

        }


        public void onFragmentSelected() {

            if (title.getVisibility() == View.INVISIBLE) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        title.setAlpha(0);
                        title.setVisibility(View.VISIBLE);
                    }
                }, 60);
                YoYo.with(Techniques.SlideInLeft).interpolate(new OvershootInterpolator()).duration(800).delay(50).playOn(title);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        subtitle.setAlpha(0);
                        subtitle.setVisibility(View.VISIBLE);
                    }
                }, 60);
                YoYo.with(Techniques.SlideInLeft).interpolate(new OvershootInterpolator()).duration(800).delay(200).playOn(subtitle);

                int backgroundType = PreferenceUtil.getBackgroundType(getActivity());
                if(backgroundType == 0) {
                    automatic.setChecked(true);
                    recyclerView.setVisibility(View.INVISIBLE);
                } else {
                    manual.setChecked(true);
                }

                if (backgroundType == 1) {
                    showRecycler(true, true);
                }
                else {
                    showRecycler(false, true);
                }

            }

        }

        private class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.SimpleViewHolder> {

            public class SimpleViewHolder extends RecyclerView.ViewHolder {
                ImageView colorIV;
                View container;


                public SimpleViewHolder(View itemView) {
                    super(itemView);
                    colorIV = (ImageView) itemView.findViewById(R.id.ivColorItem);
                    container = itemView.findViewById(R.id.colorItemContainer);
                }

            }

            int selection;
            SimpleViewHolder selectedHolder;

            public ColorAdapter() {
                selection = PreferenceUtil.getBackgroundColor(getActivity());
            }

            @Override
            public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_item, parent, false);
                return new SimpleViewHolder(view);
            }

            @Override
            public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
                if (position == selection) {
                    selectedHolder = viewHolder;
                    viewHolder.colorIV.setImageResource(R.drawable.ic_check);
                } else {
                    viewHolder.colorIV.setImageResource(R.color.transparent);
                }
                viewHolder.colorIV.setBackgroundColor(getResources().getColor(Constant.BACKGROUND_COLORS[position]));
                viewHolder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PreferenceUtil.setBackgroundColor(getActivity(), position);
                        viewHolder.colorIV.setImageResource(R.drawable.ic_check);
                        selectedHolder.colorIV.setImageResource(R.color.transparent);
                        selectedHolder = viewHolder;
                        selection = position;
                    }
                });
            }

            @Override
            public int getItemCount() {
                return Constant.BACKGROUND_COLORS.length;
            }
        }
    }


    //Fragment 4

    public static class Fragment4 extends Fragment {


        public static Fragment4 newInstance() {
            return new Fragment4();
        }

        public Fragment4() {

        }

        View v;
        View title, subtitle;
        View locationTitle, locationSubtitle;
        View container1, container2, card_search;
        EditText edit_text_search;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            v = inflater.inflate(R.layout.splash_frag4, null);
            title = v.findViewById(R.id.tvFrag4Title);
            locationTitle = v.findViewById(R.id.tvFrag4LastBit);
            subtitle = v.findViewById(R.id.tvFrag4Subtitle);
            locationSubtitle = v.findViewById(R.id.tvFrag4LastBitSubtitle);
            container1 = v.findViewById(R.id.frag4Container1);
            container2 = v.findViewById(R.id.frag4Container2);
            card_search = v.findViewById(R.id.card_search);
            edit_text_search = (EditText) v.findViewById(R.id.edit_text_search);

            setupViews();
            return v;

        }

        private void setupViews() {
            title.setVisibility(View.INVISIBLE);
            subtitle.setVisibility(View.INVISIBLE);
            locationSubtitle.setVisibility(View.INVISIBLE);
            locationTitle.setVisibility(View.INVISIBLE);
            container1.setVisibility(View.INVISIBLE);
            container2.setVisibility(View.INVISIBLE);
            card_search.setVisibility(View.INVISIBLE);
        }

        public void onFragmentSelectedLocation() {

            if (container1.getVisibility() == View.INVISIBLE) {

                container2.setVisibility(View.INVISIBLE);
                container1.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        locationTitle.setAlpha(0);
                        locationTitle.setVisibility(View.VISIBLE);
                    }
                }, 60);
                YoYo.with(Techniques.SlideInLeft).interpolate(new OvershootInterpolator()).duration(800).delay(50).playOn(locationTitle);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        locationSubtitle.setAlpha(0);
                        locationSubtitle.setVisibility(View.VISIBLE);
                    }
                }, 60);
                YoYo.with(Techniques.SlideInLeft).interpolate(new OvershootInterpolator()).duration(800).delay(200).playOn(locationSubtitle);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        card_search.setAlpha(0);
                        card_search.setVisibility(View.VISIBLE);
                    }
                }, 510);
                YoYo.with(Techniques.FlipInX).interpolate(new OvershootInterpolator()).duration(800).delay(400).playOn(card_search);




            }

        }


        public void onFragmentSelected() {

            if (container2.getVisibility() == View.INVISIBLE) {

                container1.setVisibility(View.INVISIBLE);
                container2.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        title.setAlpha(0);
                        title.setVisibility(View.VISIBLE);
                    }
                }, 60);
                YoYo.with(Techniques.SlideInLeft).interpolate(new OvershootInterpolator()).duration(800).delay(50).playOn(title);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        subtitle.setAlpha(0);
                        subtitle.setVisibility(View.VISIBLE);
                    }
                }, 60);
                YoYo.with(Techniques.SlideInLeft).interpolate(new OvershootInterpolator()).duration(800).delay(200).playOn(subtitle);
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

}
