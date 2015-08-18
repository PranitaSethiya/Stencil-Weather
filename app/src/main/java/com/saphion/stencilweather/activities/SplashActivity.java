package com.saphion.stencilweather.activities;

import android.app.Activity;
import android.content.Context;
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
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.saphion.stencilweather.tasks.GetLocationInfo;
import com.saphion.stencilweather.tasks.SuggestTask;
import com.saphion.stencilweather.utilities.Constant;
import com.saphion.stencilweather.utilities.LocationUtils;
import com.saphion.stencilweather.utilities.PreferenceUtil;
import com.saphion.stencilweather.utilities.SplashView;
import com.saphion.stencilweather.utilities.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

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
                dialogHandler.postDelayed(this, 500);
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
                        setBackgroundColor(mBackground, getResources().getColor(R.color.option2), 350);
                        ((Fragment1) viewPagerAdapter.getItem(0)).onFragmentUnselected();
                        ((Fragment2) viewPagerAdapter.getItem(1)).onFragmentSelected();

                        ivGetStartedNext.setVisibility(View.VISIBLE);
                        tvGetStartedDone.setVisibility(View.GONE);
                        tvGetStartedSkip.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        rb3.setChecked(true);
                        setBackgroundColor(mBackground, getResources().getColor(R.color.option8), 350);
                        ((Fragment3) viewPagerAdapter.getItem(2)).onFragmentSelected();
                        ivGetStartedNext.setVisibility(View.VISIBLE);
                        tvGetStartedDone.setVisibility(View.GONE);
                        tvGetStartedSkip.setVisibility(View.VISIBLE);
                        ((Fragment4) viewPagerAdapter.getItem(3)).hideKeyboard();
                        break;
                    case 3:
                        rb4.setChecked(true);
                        setBackgroundColor(mBackground, getResources().getColor(R.color.option10), 350);
                        ((Fragment1) viewPagerAdapter.getItem(0)).onFragmentUnselected();

                        if (adding) {
                            ivGetStartedNext.setVisibility(View.GONE);
                            tvGetStartedDone.setVisibility(View.VISIBLE);
                            tvGetStartedSkip.setVisibility(View.GONE);
                            ((Fragment4) viewPagerAdapter.getItem(3)).onFragmentSelected();
                        } else{
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

    boolean adding = false;

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
                    adding = true;
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
                    adding = false;

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


    public void setBackgroundColor(final View v, int color, int duration) {
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
        colorAnimation.setDuration(duration);
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
        YoYo.with(Techniques.FadeIn).interpolate(new OvershootInterpolator()).duration(250).playOn(findViewById(R.id.rgContainer));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tvGetStartedSkip.setAlpha(0);
                tvGetStartedSkip.setVisibility(View.VISIBLE);
            }
        }, 10);
        YoYo.with(Techniques.FadeIn).interpolate(new OvershootInterpolator()).duration(250).playOn(tvGetStartedSkip);

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
                final Handler locationHandler = new Handler();
                final Handler extraTimeHandler = new Handler();

                new AsyncTask<Void, Void, Long>() {

                    Runnable extraRunnable;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        setContentView(R.layout.progress_layout);
                        final LinearLayout rl = (LinearLayout) findViewById(R.id.containerSplashLoading);

                        ImageView iv = (ImageView) findViewById(R.id.imageViewSplashLoader);
                        iv.setImageBitmap(Utils.loader(SplashActivity.this));
                        iv.startAnimation(Utils.createRotateAnimation());

                        extraRunnable = new Runnable() {
                            @Override
                            public void run() {
                                int color = getResources().getColor(Constant.BACKGROUND_COLORS[new Random().nextInt(Constant.BACKGROUND_COLORS.length)]);
                                setBackgroundColor(rl, color, 1300);
                                extraTimeHandler.postDelayed(this, 1300);
                            }
                        };

                        extraTimeHandler.post(extraRunnable);
                    }

                    @Override
                    protected Long doInBackground(Void... voids) {
                        return (Long)WLocation.count(WLocation.class, null, null);
                    }

                    @Override
                    protected void onPostExecute(Long integer) {
                        super.onPostExecute(integer);
                        if (integer > 0) {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } else {
                            locationHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (WLocation.count(WLocation.class, null, null) > 0) {
                                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                        finish();
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        locationHandler.removeCallbacks(this);
                                        extraTimeHandler.removeCallbacks(extraRunnable);
                                    } else {
                                        locationHandler.postDelayed(this, 500);
                                    }
                                }
                            }, 500);
                        }
                    }
                }.execute();
                Utils.incAppCount(SplashActivity.this);
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


        public void showRecycler(boolean expand, final boolean animate) {
            if (v != null) {
                if (expand) {
                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rgContainer, "translationY", Utils.dpToPx(30, getActivity()), 0);
                    objectAnimator.setDuration(animate ? 500 : 1);
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
                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rgContainer, "translationY", 0, Utils.dpToPx(30, getActivity()));
                    objectAnimator.setDuration(animate ? 500 : 1);
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
                            .duration(animate ? 500 : 1).interpolate(new BounceInterpolator())
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
                if (backgroundType == 0) {
                    automatic.setChecked(true);
                    recyclerView.setVisibility(View.INVISIBLE);
                } else {
                    manual.setChecked(true);
                }

                if (backgroundType == 1) {
                    showRecycler(true, true);
                } else {
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


        private MyAdapter suggestionAdapter;
        private ListView listView;
        private View pb;

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
        ImageView clearSearch;
        View line_divider;
        private Handler guiThread;
        private ExecutorService suggestionThread;
        private Runnable updateTask;
        private Future<?> suggestionPending;

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
            listView = (ListView) v.findViewById(R.id.listViewFrag4);
            clearSearch = (ImageView) v.findViewById(R.id.clearSearch);
            pb = v.findViewById(R.id.progressSearch);
            line_divider = v.findViewById(R.id.line_divider);


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (!(parent.getItemAtPosition(position).toString()
                            .equalsIgnoreCase("Loading...")
                            || parent.getItemAtPosition(position).toString()
                            .equalsIgnoreCase("No suggestions") || parent
                            .getItemAtPosition(position)
                            .toString()
                            .equalsIgnoreCase(
                                    "Unable To Connect to Internet, Please Check Your Network Settings."))) {


                        new AsyncTask<WLocation, Integer, Void>() {

                            @Override
                            protected Void doInBackground(WLocation... objects) {
                                WLocation location = objects[0];
                                if (location.getTimezone().equalsIgnoreCase("MISSING")) {
                                    try {
                                        location.setTimezone(GetLocationInfo.getTimezone(location.getLatitude(), location.getLongitude())
                                                .getDisplayName());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                location.checkAndSave();
                                return null;
                            }

                        }.execute((WLocation) parent.getItemAtPosition(position));

//                        new GetLL(getActivity(), (WLocation) parent.getItemAtPosition(position)).execute();
                        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(edit_text_search.getWindowToken(), 0);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onFragmentSelected();
                            }
                        }, 200);

                    }
                }
            });

            edit_text_search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (edit_text_search.getText().toString().length() == 0) {
                        clearSearch.setVisibility(View.GONE);
                        suggestionAdapter.clear();
                        IsAdapterEmpty();
                        listView.setVisibility(View.GONE);
                        pb.setVisibility(View.GONE);

                        if (suggestionPending != null)
                            suggestionPending.cancel(true);

                    } else {
                        clearSearch.setImageResource(R.drawable.ic_close);
                        IsAdapterEmpty();

                        pb.setVisibility(View.VISIBLE);
                        clearSearch.setVisibility(View.GONE);
                        suggestionAdapter.clear();
                        queueUpdate(1000 /* milliseconds */);

                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            clearSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (edit_text_search.getText().toString().length() > 0) {
                        edit_text_search.setText("");
                        listView.setVisibility(View.GONE);
                        suggestionAdapter.clear();
                        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        IsAdapterEmpty();
                    }
                }
            });

            setupViews();
            initThreading();
            setAdapters();
            return v;

        }

        /**
         * Initialize multi-threading. There are two threads: 1) The main graphical
         * user interface thread already started by Android, and 2) The suggest
         * thread, which we start using an executor.
         */
        private void initThreading() {
            guiThread = new Handler();
            suggestionThread = Executors.newSingleThreadExecutor();

            // This task gets suggestions and updates the screen
            updateTask = new Runnable() {
                public void run() {
                    // Get text to suggest
                    final String original = edit_text_search.getText().toString().trim();

                    // Cancel previous suggestion if there was one
                    if (suggestionPending != null)
                        suggestionPending.cancel(true);

                    // Check to make sure there is text to work onSuggest
                    if (original.length() != 0) {
                        // Let user know we're doing something
                        // setText(R.string.working);

                        // Begin suggestion now but don't wait for it
                        try {
                            Runnable suggestTask = new Runnable() {

                                @Override
                                public void run() {
                                    setSuggestions(SuggestTask.doSuggest(original));
                                }
                            };
                            suggestionPending = suggestionThread.submit(suggestTask);
                        } catch (RejectedExecutionException e) {
                            Log.d("Suggest", "5th Exception");
                            // Unable to start new task
//                        setText(R.string.error);
                            Toast.makeText(getActivity(), "Unable to process request, please try again.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        pb.setVisibility(View.GONE);
                        clearSearch.setVisibility(View.VISIBLE);
                    }
                }
            };
        }

        /**
         * Request an update to start after a short delay
         */
        private void queueUpdate(long delayMillis) {
            Log.d("Stencil", "inside queue");
            // Cancel previous update if it hasn't started yet
            guiThread.removeCallbacks(updateTask);
            // Start an update if nothing happens after a few milliseconds
            guiThread.postDelayed(updateTask, delayMillis);
        }


        /**
         * Display a list
         */
        private void setList(List<WLocation> list) {
            suggestionAdapter.clear();
            listView.setVisibility(View.VISIBLE);
//             suggestionAdapter.addAll(list); // Could use if API >= 11
            for (WLocation item : list) {
                suggestionAdapter.add(item);
            }
        }

        /**
         * Modify list on the screen (called from another thread)
         */
        public void setSuggestions(List<WLocation> suggestions) {

            guiSetList(listView, suggestions);

        }

        /**
         * All changes to the GUI must be done in the GUI thread
         */
        private void guiSetList(final ListView view, final List<WLocation> list) {

            guiThread.post(new Runnable() {
                public void run() {
                    setList(list);
                    pb.setVisibility(View.GONE);
                    clearSearch.setVisibility(View.VISIBLE);
                    IsAdapterEmpty();
                    listView.setVisibility(View.VISIBLE);
                    Utils.hideKeyboard(edit_text_search, getActivity());
                }

            });

        }

        private void IsAdapterEmpty() {
            if (suggestionAdapter.getCount() == 0) {
                line_divider.setVisibility(View.GONE);
            } else {
                line_divider.setVisibility(View.VISIBLE);
            }

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

        public void hideKeyboard() {
            Utils.hideKeyboard(edit_text_search, getActivity());
        }

        public class MyAdapter extends BaseAdapter {
            private Context context;
            private List<WLocation> objects;

            public MyAdapter(Context context,
                             List<WLocation> objects) {
                this.context = context;
                this.objects = objects;
            }

            @Override
            public int getCount() {
                return objects.size();
            }

            @Override
            public Object getItem(int i) {
                return objects.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            public void add(WLocation item) {
                objects.add(item);
                notifyDataSetChanged();
            }

            public void clear() {
                objects.clear();
                notifyDataSetChanged();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                ViewHolder viewHolder;

                if (convertView == null) {

                    // inflate the layout
                    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                    convertView = inflater.inflate(R.layout.location_action_item, parent, false);

                    // well set up the ViewHolder
                    viewHolder = new ViewHolder();
                    viewHolder.textView = (TextView) convertView.findViewById(R.id.tvLocationName);

                    // store the holder with the view.
                    convertView.setTag(viewHolder);

                } else {
                    // we've just avoided calling findViewById() on resource everytime
                    // just use the viewHolder
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                String name = objects.get(position).getName();
                String splitWord = edit_text_search.getText().toString();

                Log.d("Stencil Weather", "Split Word: " + splitWord);
                Log.d("Stencil Weather", "Complete Word: " + name);

                try {
                    if (!splitWord.isEmpty())
                        viewHolder.textView.setText(Html.fromHtml("<b>" + (splitWord.charAt(0) + "").toUpperCase(Locale.getDefault()) + splitWord.substring(1, splitWord.length()) + "</b>"
                                + name.substring(name.indexOf(splitWord) + splitWord.length())));
                    else
                        viewHolder.textView.setText(name);
                } catch (Exception ex) {
                    viewHolder.textView.setText(name);
                    ex.printStackTrace();
                    Log.e("Stencil Error", ex.getLocalizedMessage());
                }

                return convertView;
            }

            class ViewHolder {
                public TextView textView;
            }
        }

        /**
         * Set up suggestionAdapter for list view.
         */
        private void setAdapters() {
            List<WLocation> items = new ArrayList<WLocation>();
            suggestionAdapter = new MyAdapter(getActivity(), items);

            listView.setAdapter(suggestionAdapter);

        }


        public void onFragmentSelected() {

            if (container2.getVisibility() == View.INVISIBLE) {

                container1.setVisibility(View.GONE);
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

            ((SplashActivity) getActivity()).tvGetStartedDone.setVisibility(View.VISIBLE);

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
