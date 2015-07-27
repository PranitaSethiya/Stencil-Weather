package com.saphion.stencilweather.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.saphion.stencilweather.R;
import com.saphion.stencilweather.fragments.ForecastFragment;
import com.saphion.stencilweather.modules.WLocation;

public class ForecastActivity extends AppCompatActivity{

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_close);
        ab.setDisplayHomeAsUpEnabled(true);



        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpagerForecast);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setToolBarColor(getResources().getColor(R.color.main_button_blue_normal));
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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(ForecastFragment.newInstance(0).setContext(getBaseContext(), new WLocation("", "Asia/Kolkata", 0d, 0d, false)), "JUL 27");
        adapter.addFragment(ForecastFragment.newInstance(1).setContext(getBaseContext(), new WLocation("", "Asia/Kolkata", 0d, 0d, false)), "JUL 28");
        adapter.addFragment(ForecastFragment.newInstance(2).setContext(getBaseContext(), new WLocation("", "Asia/Kolkata", 0d, 0d, false)), "JUL 29");
        adapter.addFragment(ForecastFragment.newInstance(3).setContext(getBaseContext(), new WLocation("", "Asia/Kolkata", 0d, 0d, false)), "JUL 30");
        adapter.addFragment(ForecastFragment.newInstance(4).setContext(getBaseContext(), new WLocation("", "Asia/Kolkata", 0d, 0d, false)), "JUL 31");
        adapter.addFragment(ForecastFragment.newInstance(5).setContext(getBaseContext(), new WLocation("", "Asia/Kolkata", 0d, 0d, false)), "AUG 1");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
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
            return mFragmentTitles.get(position);
        }
    }

}
