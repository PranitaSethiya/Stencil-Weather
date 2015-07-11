/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.saphion.stencilweather.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.saphion.stencilweather.R;
import com.saphion.stencilweather.adapters.RecyclerViewAdapter;
import com.saphion.stencilweather.fragments.WeatherFragment;
import com.saphion.stencilweather.utilities.InitiateSearch;
import com.saphion.stencilweather.utilities.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.relex.circleindicator.CircleIndicator;

/**
 * TODO
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    ArrayList<Integer> colors = new ArrayList<>();
    ViewPager viewPager;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Dehradun, India");
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        Random random = new Random();
        colors.add((0xff000000 | random.nextInt(0x00ffffff)));
        colors.add((0xff000000 | random.nextInt(0x00ffffff)));
        colors.add((0xff000000 | random.nextInt(0x00ffffff)));
        colors.add((0xff000000 | random.nextInt(0x00ffffff)));


        setToolBarColor(colors.get(0));

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        findViewById(R.id.ivMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager);

        initialiseThings();
    }

    View line_divider;
    EditText edit_text_search;
    ImageView image_search_back;
    ImageView clearSearch;
    ListView listView;
    CardView card_search;
    InitiateSearch initiateSearch;

    private void initialiseThings() {


        initiateSearch = new InitiateSearch();
//        view_search = (RelativeLayout) findViewById(R.id.view_search);

        line_divider = findViewById(R.id.line_divider);
//        toolbar_shadow = findViewById(R.id.toolbar_shadow);
        edit_text_search = (EditText) findViewById(R.id.edit_text_search);
        image_search_back = (ImageView) findViewById(R.id.image_search_back);
        clearSearch = (ImageView) findViewById(R.id.clearSearch);
        listView = (ListView) findViewById(R.id.listView);

        card_search = (CardView) findViewById(R.id.card_search);
//        listContainer = (ListView) findViewById(R.id.listContainer);
//        marker_progress = (ProgressBar) findViewById(R.id.marker_progress);
//        marker_progress.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FFFFFF"),//Pink color
//                android.graphics.PorterDuff.Mode.MULTIPLY);

        InitiateSearch();
    }

    public void setToolBarColor(int color){
        try {
            findViewById(R.id.appbar).setBackgroundColor(color);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
        }catch(Exception ex){}
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());

        for(int i = 0 ; i < colors.size() ; i++)
            adapter.addFragment(WeatherFragment.newInstance(colors.get(i)).setContext(MainActivity.this), "" + i);

        viewPager.setAdapter(adapter);

        CircleIndicator defaultIndicator = (CircleIndicator) findViewById(R.id.indicator_default);
//        DemoPagerAdapter defaultPagerAdapter = new DemoPagerAdapter(getSupportFragmentManager(), getBaseContext());
//        viewPager.setAdapter(defaultPagerAdapter);
        defaultIndicator.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                    setToolBarColor(colors.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupDrawerContent(NavigationView navigationView) {

//        navigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem menuItem) {
//                menuItem.setChecked(true);
//                mDrawerLayout.closeDrawers();
//                return true;
//            }
//        });

        RecyclerView rv = (RecyclerView) findViewById(R.id.rvLocation);
        rv.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<String> items = new ArrayList<>();
        items.add("Pune");
        items.add("Dehradun");
        items.add("New Delhi");
        items.add("Riverside");
//        items.add("New York");
//        items.add("Los Angeles");

        rv.setAdapter(new RecyclerViewAdapter(getBaseContext(), items, items, MainActivity.this));


    }

    public void setViewPagerPosition(int viewPagerPosition) {
        viewPager.setCurrentItem(viewPagerPosition, true);
        mDrawerLayout.closeDrawers();
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


    private void showPopupMenu(View view) {


        // Create a PopupMenu, giving it the clicked view for an anchor
        PopupMenu popup = new PopupMenu(MainActivity.this, view, Gravity.END);

        // Inflate our menu resource into the PopupMenu's Menu
        popup.getMenuInflater().inflate(R.menu.navbar_actions, popup.getMenu());


        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {


                switch (menuItem.getItemId()) {
                    case R.id.action_about:
//                        startActivity(new Intent(getBaseContext(), AboutClass.class));
//                        overridePendingTransition(R.anim.slide_in_left,
//                                R.anim.slide_out_right);
//                        finish();
                        return true;
                    case R.id.action_morebydev:
                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/developer?id=sachin+shinde")));
                        return true;
                    case R.id.action_rate:
                        Utils.launchMarket(getBaseContext(), getPackageName());
                        return true;
                    case R.id.action_share:
                        startActivity(Utils.createShareIntent(getBaseContext()));
                        return true;
                }
                return false;
            }
        });

        // Finally show the PopupMenu
        popup.show();
    }


    private void InitiateSearch() {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItem = item.getItemId();
                switch (menuItem) {
                    case R.id.action_search:
                        IsAdapterEmpty();
                        InitiateSearch.handleToolBar(MainActivity.this, card_search, toolbar, /*view_search,*/ listView, edit_text_search, line_divider);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                LogQuickSearch logQuickSearch = logQuickSearchAdapter.getItem(position);
//                edit_text_search.setText(logQuickSearch.getName());
//                listView.setVisibility(View.GONE);
//                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(edit_text_search.getWindowToken(), 0);
//                toolbar_shadow.setVisibility(View.GONE);
//                searchFood(logQuickSearch.getName(), 0);
            }
        });
        edit_text_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edit_text_search.getText().toString().length() == 0) {
//                    logQuickSearchAdapter = new LogQuickSearchAdapter(MainActivity.this, 0, LogQuickSearch.all());
//                    listView.setAdapter(logQuickSearchAdapter);
                    clearSearch.setImageResource(R.drawable.ic_microphone);
//                    IsAdapterEmpty();
                } else {
//                    logQuickSearchAdapter = new LogQuickSearchAdapter(MainActivity.this, 0, LogQuickSearch.FilterByName(edit_text_search.getText().toString()));
//                    listView.setAdapter(logQuickSearchAdapter);
                    clearSearch.setImageResource(R.drawable.ic_close);
                    IsAdapterEmpty();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_text_search.getText().toString().length() == 0) {

                } else {
//                    mAsyncTask.cancel(true);
                    edit_text_search.setText("");
                    listView.setVisibility(View.VISIBLE);
//                    clearItems();
                    ((InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    IsAdapterEmpty();
                }
            }
        });
    }

    private void UpdateQuickSearch(String item) {
//        for (int i = 0; i < logQuickSearchAdapter.getCount(); i++) {
//            LogQuickSearch ls = logQuickSearchAdapter.getItem(i);
//            String name = ls.getName();
//            set.add(name.toUpperCase());
//        }
//        if (set.add(item.toUpperCase())) {
//            LogQuickSearch recentLog = new LogQuickSearch();
//            recentLog.setName(item);
//            recentLog.setDate(new Date());
//            recentLog.save();
//            logQuickSearchAdapter.add(recentLog);
//            logQuickSearchAdapter.notifyDataSetChanged();
//        }
    }

    private void HandleSearch() {
        image_search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitiateSearch.handleToolBar(MainActivity.this, card_search, toolbar, /*view_search,*/ listView, edit_text_search, line_divider);
//                listContainer.setVisibility(View.GONE);
//                toolbar_shadow.setVisibility(View.VISIBLE);
//                clearItems();
            }
        });
        edit_text_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (edit_text_search.getText().toString().trim().length() > 0) {
//                        clearItems();
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(edit_text_search.getWindowToken(), 0);
                        UpdateQuickSearch(edit_text_search.getText().toString());
                        listView.setVisibility(View.GONE);
//                        searchFood(edit_text_search.getText().toString(), 0);
//                        toolbar_shadow.setVisibility(View.GONE);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void IsAdapterEmpty() {
//        if (logQuickSearchAdapter.getCount() == 0) {
            line_divider.setVisibility(View.GONE);
//        } else {
//            line_divider.setVisibility(View.VISIBLE);
//        }
    }

}
