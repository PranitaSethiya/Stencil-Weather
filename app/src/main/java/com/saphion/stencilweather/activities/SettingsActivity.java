package com.saphion.stencilweather.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.saphion.stencilweather.R;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    View cardDetailsGeneral, cardGeneral, cardGeneralContainer;
    View cardUnitsContainer, cardUnits, cardDetailsUnits;
    View cardNotificationContainer, cardNotifications, cardDetailsNotifications;
    View cardFAQsContainer, cardFAQs, cardDetailsFAQs;
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

        initializeViews();

        setupSettingsScreen2();

    }

    private void initializeViews() {

        cardDetailsGeneral = findViewById(R.id.containerGeneralDetails);
        cardGeneral = findViewById(R.id.cardGeneral);
        cardGeneralContainer = findViewById(R.id.cardGeneralContainer);

        cardUnitsContainer = findViewById(R.id.cardUnitsContainer);
        cardUnits = findViewById(R.id.cardUnits);
        cardDetailsUnits = findViewById(R.id.cardDetailsUnits);


        cardNotificationContainer = findViewById(R.id.cardNotificationContainer);
        cardNotifications = findViewById(R.id.cardNotifications);
        cardDetailsNotifications = findViewById(R.id.cardDetailsNotifications);

        cardFAQsContainer = findViewById(R.id.cardFAQsContainer);
        cardFAQs = findViewById(R.id.cardFAQs);
        cardDetailsFAQs = findViewById(R.id.cardDetailsFAQs);

        cardGeneral.setOnClickListener(this);
        cardUnits.setOnClickListener(this);
        cardNotifications.setOnClickListener(this);
        cardFAQs.setOnClickListener(this);

        cardDetailsGeneral.setVisibility(View.GONE);
        cardDetailsUnits.setVisibility(View.GONE);
        cardDetailsNotifications.setVisibility(View.GONE);
        cardDetailsFAQs.setVisibility(View.GONE);
    }


    private void setupSettingsScreen2() {
        ((TextView) findViewById(R.id.tvPrefTempVal)).setTextColor(getResources().getColor(R.color.primary_light_blue));
        ((TextView) findViewById(R.id.tvPrefTempTitle)).setTextColor(getResources().getColor(R.color.my_grey));
        ((TextView) findViewById(R.id.tvPrefPrecipitationVal)).setTextColor(getResources().getColor(R.color.primary_light_blue));
        ((TextView) findViewById(R.id.tvPrefPrecipitationTitle)).setTextColor(getResources().getColor(R.color.my_grey));
        ((TextView) findViewById(R.id.tvPrefWindVal)).setTextColor(getResources().getColor(R.color.primary_light_blue));
        ((TextView) findViewById(R.id.tvPrefWindTitle)).setTextColor(getResources().getColor(R.color.my_grey));
        ((TextView) findViewById(R.id.tvPrefPressureVal)).setTextColor(getResources().getColor(R.color.primary_light_blue));
        ((TextView) findViewById(R.id.tvPrefPressureTitle)).setTextColor(getResources().getColor(R.color.my_grey));
        ((TextView) findViewById(R.id.tvPrefTimeVal)).setTextColor(getResources().getColor(R.color.primary_light_blue));
        ((TextView) findViewById(R.id.tvPrefTimeTitle)).setTextColor(getResources().getColor(R.color.my_grey));
    }

    @Override
    public void onClick(View view) {
        materialMenu.animateIconState(MaterialMenuDrawable.IconState.X);
        switch (view.getId()) {
            case R.id.cardGeneral:

                cardGeneralContainer.setVisibility(View.VISIBLE);
                cardUnitsContainer.setVisibility(View.GONE);
                cardNotificationContainer.setVisibility(View.GONE);
                cardFAQsContainer.setVisibility(View.GONE);

                cardGeneral.setEnabled(false);

                cardDetailsGeneral.setVisibility(View.VISIBLE);

                break;
            case R.id.cardUnits:

                cardGeneralContainer.setVisibility(View.GONE);
                cardUnitsContainer.setVisibility(View.VISIBLE);
                cardNotificationContainer.setVisibility(View.GONE);
                cardFAQsContainer.setVisibility(View.GONE);

                cardUnits.setEnabled(false);

                cardDetailsUnits.setVisibility(View.VISIBLE);
                break;
            case R.id.cardNotifications:

                cardGeneralContainer.setVisibility(View.GONE);
                cardUnitsContainer.setVisibility(View.GONE);
                cardNotificationContainer.setVisibility(View.VISIBLE);
                cardFAQsContainer.setVisibility(View.GONE);

                cardNotifications.setEnabled(false);

                cardDetailsNotifications.setVisibility(View.VISIBLE);



                break;
            case R.id.cardFAQs:

                cardGeneralContainer.setVisibility(View.GONE);
                cardUnitsContainer.setVisibility(View.GONE);
                cardNotificationContainer.setVisibility(View.GONE);
                cardFAQsContainer.setVisibility(View.VISIBLE);

                cardFAQs.setEnabled(false);

                cardDetailsFAQs.setVisibility(View.VISIBLE);
                break;
        }

//        svSettings.requestDisallowInterceptTouchEvent(true);
//        isBlockedScrollView = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                back();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void back() {
        if (materialMenu.getIconState() == MaterialMenuDrawable.IconState.X) {
            materialMenu.animateIconState(MaterialMenuDrawable.IconState.ARROW);
            cardGeneralContainer.setVisibility(View.VISIBLE);
            cardUnitsContainer.setVisibility(View.VISIBLE);
            cardNotificationContainer.setVisibility(View.VISIBLE);
            cardFAQsContainer.setVisibility(View.VISIBLE);

            cardGeneral.setEnabled(true);
            cardUnits.setEnabled(true);
            cardNotifications.setEnabled(true);
            cardFAQs.setEnabled(true);

            cardDetailsGeneral.setVisibility(View.GONE);
            cardDetailsUnits.setVisibility(View.GONE);
            cardDetailsNotifications.setVisibility(View.GONE);
            cardDetailsFAQs.setVisibility(View.GONE);

//            svSettings.requestDisallowInterceptTouchEvent(false);
//            isBlockedScrollView = true;
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }
}