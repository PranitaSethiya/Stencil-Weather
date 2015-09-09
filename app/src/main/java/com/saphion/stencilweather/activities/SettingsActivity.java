package com.saphion.stencilweather.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.saphion.stencilweather.R;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private MaterialMenuDrawable materialMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Settings");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }

        setSupportActionBar(toolbar);

        materialMenu = new MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
        materialMenu.setIconState(MaterialMenuDrawable.IconState.ARROW);
        toolbar.setNavigationIcon(materialMenu);


        setupSettingsScreen2();

    }

    private void setupSettingsScreen2() {
        ((TextView)findViewById(R.id.tvPrefTempVal)).setTextColor(getResources().getColor(R.color.primary_light_blue));
        ((TextView)findViewById(R.id.tvPrefTempTitle)).setTextColor(getResources().getColor(R.color.my_grey));
        ((TextView)findViewById(R.id.tvPrefPrecipitationVal)).setTextColor(getResources().getColor(R.color.primary_light_blue));
        ((TextView)findViewById(R.id.tvPrefPrecipitationTitle)).setTextColor(getResources().getColor(R.color.primary_light_blue));
        ((TextView)findViewById(R.id.tvPrefWindVal)).setTextColor(getResources().getColor(R.color.primary_light_blue));
        ((TextView)findViewById(R.id.tvPrefWindTitle)).setTextColor(getResources().getColor(R.color.primary_light_blue));
        ((TextView)findViewById(R.id.tvPrefPressureVal)).setTextColor(getResources().getColor(R.color.primary_light_blue));
        ((TextView)findViewById(R.id.tvPrefPressureTitle)).setTextColor(getResources().getColor(R.color.primary_light_blue));
        ((TextView)findViewById(R.id.tvPrefTimeVal)).setTextColor(getResources().getColor(R.color.primary_light_blue));
        ((TextView)findViewById(R.id.tvPrefTimeTitle)).setTextColor(getResources().getColor(R.color.primary_light_blue));
    }
}