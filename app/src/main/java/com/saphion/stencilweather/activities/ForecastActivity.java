package com.saphion.stencilweather.activities;

import java.util.ArrayList;
import java.util.List;

import android.os.Build;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.saphion.stencilweather.R;
import com.saphion.stencilweather.fragments.ForecastFragment;
import com.saphion.stencilweather.modules.WLocation;

public class ForecastActivity extends AppCompatActivity{

    Toolbar toolbar;
    int prevSelected = 0;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);


        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Forecast");
        toolbar.setSubtitle(getIntent().getStringExtra("subtitle"));

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_close);
        ab.setDisplayHomeAsUpEnabled(true);



        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpagerForecast);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        setToolBarColor(getResources().getColor(R.color.forecast_action_bar));

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh_forecast);

        swipeRefreshLayout.setColorSchemeResources(R.color.main_button_red_normal, R.color.main_button_green_normal, R.color.main_button_blue_normal);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
    }

    private void setToolBarColor(int color) {
        try {
            findViewById(R.id.appbar).setBackgroundColor(color);
            toolbar.setBackgroundColor(color);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                color = (color & 0xfefefefe) >> 1;

                window.setStatusBarColor(color);
            }

        } catch (Exception ignored) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        final Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(ForecastFragment.newInstance(0).setContext(getBaseContext(), new WLocation("", "Asia/Kolkata", 0d, 0d, false)), "MON", "27", "July, 2015");
        adapter.addFragment(ForecastFragment.newInstance(1).setContext(getBaseContext(), new WLocation("", "Asia/Kolkata", 0d, 0d, false)), "TUE", "28", "July, 2015");
        adapter.addFragment(ForecastFragment.newInstance(2).setContext(getBaseContext(), new WLocation("", "Asia/Kolkata", 0d, 0d, false)), "WED", "29", "July, 2015");
        adapter.addFragment(ForecastFragment.newInstance(3).setContext(getBaseContext(), new WLocation("", "Asia/Kolkata", 0d, 0d, false)), "THU", "30", "July, 2015");
        adapter.addFragment(ForecastFragment.newInstance(4).setContext(getBaseContext(), new WLocation("", "Asia/Kolkata", 0d, 0d, false)), "FRI", "31", "July, 2015");
        adapter.addFragment(ForecastFragment.newInstance(5).setContext(getBaseContext(), new WLocation("", "Asia/Kolkata", 0d, 0d, false)), "SAT", "1", "August, 2015");
        adapter.addFragment(ForecastFragment.newInstance(6).setContext(getBaseContext(), new WLocation("", "Asia/Kolkata", 0d, 0d, false)), "SAT", "2", "August, 2015");
        adapter.addFragment(ForecastFragment.newInstance(7).setContext(getBaseContext(), new WLocation("", "Asia/Kolkata", 0d, 0d, false)), "SUN", "3", "August, 2015");
        adapter.addFragment(ForecastFragment.newInstance(8).setContext(getBaseContext(), new WLocation("", "Asia/Kolkata", 0d, 0d, false)), "MON", "4", "August, 2015");
        adapter.addFragment(ForecastFragment.newInstance(9).setContext(getBaseContext(), new WLocation("", "Asia/Kolkata", 0d, 0d, false)), "TUE", "5", "August, 2015");
        viewPager.setAdapter(adapter);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Iterate over all tabs and set the custom view
        int tabColor = getResources().getColor(R.color.colorSecondaryText);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(adapter.getTabView(i));
        }

        adapter.setTabColor(prevSelected, getResources().getColor(R.color.colorPrimaryText));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((TextView)findViewById(R.id.tvTabMonth)).setText(adapter.getMonth(position));
                adapter.setTabColor(position, getResources().getColor(R.color.colorPrimaryText));
                adapter.setTabColor(prevSelected, getResources().getColor(R.color.colorSecondaryText));
                prevSelected = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setOffscreenPageLimit(1);

    }


    class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentDay = new ArrayList<>();
        private final List<String> mFragmentDate = new ArrayList<>();
        private final List<String> mFragmentMonth = new ArrayList<>();
        private final List<View> mFragmentViews = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String day, String date, String month) {
            mFragments.add(fragment);
            mFragmentDay.add(day);
            mFragmentDate.add(date);
            mFragmentMonth.add(month);
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
            return mFragmentDay.get(position);
        }


        public View getTabView(int i) {
            View v = LayoutInflater.from(ForecastActivity.this).inflate(R.layout.tabhost, null);
            ((TextView)v.findViewById(R.id.tvTabDate)).setText(mFragmentDate.get(i));
            ((TextView)v.findViewById(R.id.tvTabDay)).setText(mFragmentDay.get(i));
            mFragmentViews.add(v);
            return v;
        }

        public void setTabColor(int position, int color){
            View v = mFragmentViews.get(position);
            ((TextView)v.findViewById(R.id.tvTabDate)).setTextColor(color);
            ((TextView)v.findViewById(R.id.tvTabDay)).setTextColor(color);
        }

        public String getMonth(int position){
            return mFragmentMonth.get(position);
        }
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
        super.onBackPressed();
    }
}
