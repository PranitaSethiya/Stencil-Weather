package com.saphion.stencilweather.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.ObjectAnimator;
import com.saphion.stencilweather.R;
import com.saphion.stencilweather.utilities.Constant;
import com.saphion.stencilweather.utilities.PreferenceUtil;
import com.saphion.stencilweather.utilities.Utils;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    View cardDetailsGeneral, cardGeneral, cardGeneralContainer;
    View cardUnitsContainer, cardUnits, cardDetailsUnits;
    View cardNotificationContainer, cardNotifications, cardDetailsNotifications;
    View cardCustomizeContainer, cardCustomize, cardDetailsCustomize;
    private Toolbar toolbar;
    private MaterialMenuDrawable materialMenu;
    private RecyclerView recyclerViewColor;
    private View rgContainer;
    private RadioGroup rgColor;
    private RadioButton automatic;
    private RadioButton manual;

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
        setupSettingsScreen3();

    }

    private void setupSettingsScreen3() {

        automatic = (RadioButton) findViewById(R.id.rbAutomatic);
        manual = (RadioButton) findViewById(R.id.rbManual);

        automatic.setTextColor(getResources().getColor(R.color.colorTernaryText));
        manual.setTextColor(getResources().getColor(R.color.colorTernaryText));


        recyclerViewColor = (RecyclerView) findViewById(R.id.rvColors);

        LinearLayoutManager llm = new LinearLayoutManager(getBaseContext());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewColor.setLayoutManager(llm);
        recyclerViewColor.setAdapter(new ColorAdapter());
        recyclerViewColor.scrollToPosition(PreferenceUtil.getBackgroundColor(SettingsActivity.this));

        rgContainer = findViewById(R.id.cardDetailsCustomize);

        rgColor = (RadioGroup) findViewById(R.id.rgColorContainer);

        rgColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rbAutomatic:
                        PreferenceUtil.setBackgroundType(getBaseContext(), 0);
                        showRecycler(false, true);
                        break;
                    case R.id.rbManual:
                        PreferenceUtil.setBackgroundType(getBaseContext(), 1);
                        showRecycler(true, true);
                        break;
                }
            }
        });

        int backgroundType = PreferenceUtil.getBackgroundType(getBaseContext());
        if (backgroundType == 0) {
            automatic.setChecked(true);
            recyclerViewColor.setVisibility(View.INVISIBLE);
        } else {
            manual.setChecked(true);
        }

        if (backgroundType == 1) {
            showRecycler(true, true);
        } else {
            showRecycler(false, true);
        }

    }

    public void showRecycler(boolean expand, final boolean animate) {
            if (expand) {
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rgContainer, "translationY", Utils.dpToPx(30, getBaseContext()), 0);
                objectAnimator.setDuration(animate ? 500 : 1);
                objectAnimator.start();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerViewColor.setAlpha(animate ? 0 : 255);
                        recyclerViewColor.setVisibility(View.VISIBLE);
                    }
                }, 110);
                YoYo.with(Techniques.SlideInRight)
                        .duration(animate ? 500 : 1).interpolate(new BounceInterpolator()).delay(100)
                        .playOn(recyclerViewColor);

            } else {
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rgContainer, "translationY", 0, Utils.dpToPx(30, getBaseContext()));
                objectAnimator.setDuration(animate ? 500 : 1);
                objectAnimator.setStartDelay(200);
                objectAnimator.start();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerViewColor.setAlpha(0);
                        recyclerViewColor.setVisibility(View.INVISIBLE);
                    }
                }, 510);
                YoYo.with(Techniques.SlideOutRight)
                        .duration(animate ? 500 : 1).interpolate(new BounceInterpolator())
                        .playOn(recyclerViewColor);
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
            selection = PreferenceUtil.getBackgroundColor(SettingsActivity.this);
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
                    PreferenceUtil.setBackgroundColor(SettingsActivity.this, position);
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

        cardCustomizeContainer = findViewById(R.id.cardCustomizeContainer);
        cardCustomize = findViewById(R.id.cardCustomize);
        cardDetailsCustomize = findViewById(R.id.cardDetailsCustomize);

        cardGeneral.setOnClickListener(this);
        cardUnits.setOnClickListener(this);
        cardNotifications.setOnClickListener(this);
        cardCustomize.setOnClickListener(this);

        cardDetailsGeneral.setVisibility(View.GONE);
        cardDetailsUnits.setVisibility(View.GONE);
        cardDetailsNotifications.setVisibility(View.GONE);
        cardDetailsCustomize.setVisibility(View.GONE);

    }



    private void requestDisallowParentInterceptTouchEvent(View __v, Boolean __disallowIntercept) {
        while (__v.getParent() != null && __v.getParent() instanceof View) {
            if (__v.getParent() instanceof ScrollView) {
                __v.getParent().requestDisallowInterceptTouchEvent(__disallowIntercept);
            }
            __v = (View) __v.getParent();
        }
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
                cardCustomizeContainer.setVisibility(View.GONE);

                cardGeneral.setEnabled(false);

                cardDetailsGeneral.setVisibility(View.VISIBLE);

                break;
            case R.id.cardUnits:

                cardGeneralContainer.setVisibility(View.GONE);
                cardUnitsContainer.setVisibility(View.VISIBLE);
                cardNotificationContainer.setVisibility(View.GONE);
                cardCustomizeContainer.setVisibility(View.GONE);

                cardUnits.setEnabled(false);

                cardDetailsUnits.setVisibility(View.VISIBLE);
                break;
            case R.id.cardNotifications:

                cardGeneralContainer.setVisibility(View.GONE);
                cardUnitsContainer.setVisibility(View.GONE);
                cardNotificationContainer.setVisibility(View.VISIBLE);
                cardCustomizeContainer.setVisibility(View.GONE);

                cardNotifications.setEnabled(false);

                cardDetailsNotifications.setVisibility(View.VISIBLE);



                break;
            case R.id.cardCustomize:

                cardGeneralContainer.setVisibility(View.GONE);
                cardUnitsContainer.setVisibility(View.GONE);
                cardNotificationContainer.setVisibility(View.GONE);
                cardCustomizeContainer.setVisibility(View.VISIBLE);

                cardCustomize.setEnabled(false);

                cardDetailsCustomize.setVisibility(View.VISIBLE);
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
            cardCustomizeContainer.setVisibility(View.VISIBLE);

            cardGeneral.setEnabled(true);
            cardUnits.setEnabled(true);
            cardNotifications.setEnabled(true);
            cardCustomize.setEnabled(true);

            cardDetailsGeneral.setVisibility(View.GONE);
            cardDetailsUnits.setVisibility(View.GONE);
            cardDetailsNotifications.setVisibility(View.GONE);
            cardDetailsCustomize.setVisibility(View.GONE);

//            svSettings.requestDisallowInterceptTouchEvent(false);
//            isBlockedScrollView = true;
        } else {
            startActivity(new Intent(getBaseContext(), MainActivity.class));
            overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }
}