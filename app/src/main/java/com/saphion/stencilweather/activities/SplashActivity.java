package com.saphion.stencilweather.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

import com.saphion.stencilweather.R;
import com.saphion.stencilweather.utilities.SplashView;
import com.saphion.stencilweather.utilities.Utils;

/**
 * Created by sachin on 5/8/15.
 */
public class SplashActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        float height = (float) (getResources().getDisplayMetrics().heightPixels * 0.75);
        float width = getResources().getDisplayMetrics().widthPixels;

        SplashView splashView = new SplashView(SplashActivity.this,
                height, width,
                getResources().getColor(R.color.brown), getResources().getColor(R.color.sunny_yellow));

        FrameLayout flContainer = (FrameLayout) findViewById(R.id.sunContainer);

        flContainer.addView(splashView);

    }


}
