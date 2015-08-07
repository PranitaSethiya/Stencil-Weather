package com.saphion.stencilweather.activities;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.saphion.stencilweather.R;
import com.saphion.stencilweather.utilities.SplashView;
import com.saphion.stencilweather.utilities.Utils;
import android.view.ViewGroup.LayoutParams;

/**
 * Created by sachin on 5/8/15.
 */
public class SplashActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            int color = (getResources().getColor(R.color.main_button_blue_normal));

            window.setStatusBarColor(color);
        }

        setContentView(R.layout.activity_splash);

        float height = (float) (getResources().getDisplayMetrics().heightPixels * 0.75);
        float width = getResources().getDisplayMetrics().widthPixels;

        SplashView splashView = new SplashView(SplashActivity.this,
                height, width,
                getResources().getColor(R.color.brown), getResources().getColor(R.color.sunny_yellow));

        LayoutParams lp = new LayoutParams((int)width, (int) height);
        splashView.setLayoutParams(lp);

        FrameLayout flContainer = (FrameLayout) findViewById(R.id.sunContainer);



        flContainer.addView(splashView);

    }


}
