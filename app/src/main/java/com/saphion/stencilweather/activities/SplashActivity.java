package com.saphion.stencilweather.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.saphion.stencilweather.R;
import com.saphion.stencilweather.utilities.SplashView;
import com.saphion.stencilweather.utilities.Utils;
import android.view.ViewGroup.LayoutParams;

/**
 * Created by sachin on 5/8/15.
 */
public class SplashActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    boolean animationInComplete = true;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            int color = (getResources().getColor(R.color.main_button_blue_normal));

            window.setStatusBarColor(color);
        }

        setContentView(R.layout.splash_units);

//        setUpFragment1();

        if(Utils.isLocationEnabled(SplashActivity.this))
            buildGoogleApiClient();
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alert");
            builder.setMessage("Location Services are not enabled.\n<i>Location Services help us to determine your current location.</i>\n\nEnable them now?");
            builder.setPositiveButton("SURE", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(
                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                }
            });
            builder.setNegativeButton("CANCEL", null);

            builder.show();
        }

    }

    private void setUpFragment1() {
        float height = (float) (getResources().getDisplayMetrics().heightPixels * 0.75);
        float width = getResources().getDisplayMetrics().widthPixels;

        SplashView splashView = new SplashView(SplashActivity.this,
                height, width,
                getResources().getColor(R.color.brown), getResources().getColor(R.color.sunny_yellow));

        LayoutParams lp = new LayoutParams((int)width, (int) height);
        splashView.setLayoutParams(lp);

        FrameLayout flContainer = (FrameLayout) findViewById(R.id.sunContainer);
        flContainer.addView(splashView);

        setupViews();
    }

    private void setupViews() {
        findViewById(R.id.tvHello).setVisibility(View.INVISIBLE);
        findViewById(R.id.tvWelcome).setVisibility(View.INVISIBLE);
        findViewById(R.id.tvTo).setVisibility(View.INVISIBLE);
        findViewById(R.id.rlAppNameContainer).setVisibility(View.INVISIBLE);
    findViewById(R.id.rlGetStartedContainer).setVisibility(View.INVISIBLE);

        findViewById(R.id.fabGetStarted).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }


    public void onAnimationComplete() {
        if(animationInComplete) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    (findViewById(R.id.tvHello)).setAlpha(0);
                    (findViewById(R.id.tvHello)).setVisibility(View.VISIBLE);
                }
            }, 210);
            YoYo.with(Techniques.SlideInLeft).interpolate(new OvershootInterpolator()).duration(800).delay(200).playOn(findViewById(R.id.tvHello));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    (findViewById(R.id.tvWelcome)).setAlpha(0);
                    (findViewById(R.id.tvWelcome)).setVisibility(View.VISIBLE);
                }
            }, 260);
            YoYo.with(Techniques.SlideInRight).interpolate(new OvershootInterpolator()).duration(950).delay(250).playOn(findViewById(R.id.tvWelcome));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    (findViewById(R.id.tvTo)).setAlpha(0);
                    (findViewById(R.id.tvTo)).setVisibility(View.VISIBLE);
                }
            }, 310);
            YoYo.with(Techniques.SlideInLeft).interpolate(new OvershootInterpolator()).duration(800).delay(300).playOn(findViewById(R.id.tvTo));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    (findViewById(R.id.rlAppNameContainer)).setAlpha(0);
                    (findViewById(R.id.rlAppNameContainer)).setVisibility(View.VISIBLE);
                }
            }, 360);
            YoYo.with(Techniques.SlideInRight).interpolate(new OvershootInterpolator()).duration(950).delay(350).playOn(findViewById(R.id.rlAppNameContainer));


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    (findViewById(R.id.rlGetStartedContainer)).setAlpha(0);
                    (findViewById(R.id.rlGetStartedContainer)).setVisibility(View.VISIBLE);
                }
            }, 410);
            YoYo.with(Techniques.BounceInUp).interpolate(new OvershootInterpolator()).duration(1000).delay(1000).playOn(findViewById(R.id.rlGetStartedContainer));
            animationInComplete = false;
        }

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if(mGoogleApiClient!= null){
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
}
