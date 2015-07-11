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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.saphion.stencilweather.R;
import com.saphion.stencilweather.adapters.RecyclerViewAdapter;
import com.saphion.stencilweather.fragments.WeatherFragment;
import com.saphion.stencilweather.tasks.SuggestTask;
import com.saphion.stencilweather.utilities.InitiateSearch;
import com.saphion.stencilweather.utilities.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

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
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent();
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

        findViewById(R.id.ivMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });


        initialiseThings();
    }

    View line_divider;
    EditText edit_text_search;
    ImageView image_search_back;
    ImageView clearSearch;
    ListView listView;
    CardView card_search;
    View pb;

    private void initialiseThings() {

        line_divider = findViewById(R.id.line_divider);
        edit_text_search = (EditText) findViewById(R.id.edit_text_search);
        image_search_back = (ImageView) findViewById(R.id.image_search_back);
        clearSearch = (ImageView) findViewById(R.id.clearSearch);
        listView = (ListView) findViewById(R.id.listView);

        card_search = (CardView) findViewById(R.id.card_search);

        pb = findViewById(R.id.progressSearch);

        InitiateSearch();
        HandleSearch();
        IsAdapterEmpty();
    }

    public void setToolBarColor(int color) {
        try {
            findViewById(R.id.appbar).setBackgroundColor(color);
            toolbar.setBackgroundColor(color);
        } catch (Exception ex) {
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());

        for (int i = 0; i < colors.size(); i++)
            adapter.addFragment(WeatherFragment.newInstance(colors.get(i)).setContext(MainActivity.this), "" + i);

        viewPager.setAdapter(adapter);

        CircleIndicator defaultIndicator = (CircleIndicator) findViewById(R.id.indicator_default);
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

        findViewById(R.id.flDarkenBackground).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (card_search.getVisibility() == View.VISIBLE) {
                    InitiateSearch.handleToolBar(MainActivity.this, card_search, toolbar, /*view_search,*/ listView, edit_text_search, line_divider);
                    hideDark();

                }
                return false;
            }
        });
    }

    public void hideDark(){

        YoYo.with(Techniques.FadeOut)
                .duration(500).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                findViewById(R.id.flDarkenBackground).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        })
                .playOn(findViewById(R.id.flDarkenBackground));
    }

    public void showDark(){
        findViewById(R.id.flDarkenBackground).setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeIn)
                .duration(500)
                .playOn(findViewById(R.id.flDarkenBackground));
    }

    private void setupDrawerContent() {


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

    @Override
    protected void onDestroy() {
        // Terminate extra threads here
        try {
            suggThread.shutdownNow();
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    public MyAdapter adapter;
    private Handler guiThread;
    private ExecutorService suggThread;
    private Runnable updateTask;
    private Future<?> suggPending;
    private List<String> items;
    String keys[];
    private String[] GMT;

    public class MyAdapter extends BaseAdapter {
        private Context context;
        private List<String> objects;

        public MyAdapter(Context context,
                         List<String> objects) {
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

        public void add(String item){
            objects.add(item);
            notifyDataSetChanged();
        }

        public void clear(){
            objects.clear();
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View v = LayoutInflater.from(context).inflate(R.layout.location_action_item, null);
//			((TextView) v).setTypeface(font);
            return v;
        }

        class ViewHolder {
            public TextView textView;
        }
    }

    /** Set up adapter for list view. */
    private void setAdapters() {
        items = new ArrayList<String>();
        adapter = new MyAdapter(this, items);

        listView.setAdapter(adapter);

    }

    private void InitiateSearch() {

        initThreading();

        setAdapters();

        toolbar.inflateMenu(R.menu.menu_add);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItem = item.getItemId();
                switch (menuItem) {
                    case R.id.action_search:
                        IsAdapterEmpty();
                        InitiateSearch.handleToolBar(MainActivity.this, card_search, toolbar, /*view_search,*/ listView, edit_text_search, line_divider);
                        showDark();
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
                    clearSearch.setVisibility(View.VISIBLE);
                    clearSearch.setImageResource(R.drawable.ic_microphone);
                    adapter.clear();
                    IsAdapterEmpty();
                    listView.setVisibility(View.GONE);
                    pb.setVisibility(View.GONE);

                    if (suggPending != null)
                        suggPending.cancel(true);

                } else {
//                    logQuickSearchAdapter = new LogQuickSearchAdapter(MainActivity.this, 0, LogQuickSearch.FilterByName(edit_text_search.getText().toString()));
//                    listView.setAdapter(logQuickSearchAdapter);
                    clearSearch.setImageResource(R.drawable.ic_close);
                    IsAdapterEmpty();

                    pb.setVisibility(View.VISIBLE);
                    clearSearch.setVisibility(View.GONE);
                    adapter.clear();
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
                if (edit_text_search.getText().toString().length() == 0) {

                } else {
//                    mAsyncTask.cancel(true);
                    edit_text_search.setText("");
                    listView.setVisibility(View.GONE);
                    adapter.clear();
//                    clearItems();
                    ((InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    IsAdapterEmpty();
                }
            }
        });
    }

    /**
     * Initialize multi-threading. There are two threads: 1) The main graphical
     * user interface thread already started by Android, and 2) The suggest
     * thread, which we start using an executor.
     */
    private void initThreading() {
        guiThread = new Handler();
        suggThread = Executors.newSingleThreadExecutor();

        // This task gets suggestions and updates the screen
        updateTask = new Runnable() {
            public void run() {
                // Get text to suggest
                String original = edit_text_search.getText().toString().trim();

                // Cancel previous suggestion if there was one
                if (suggPending != null)
                    suggPending.cancel(true);

                // Check to make sure there is text to work onSuggest
                if (original.length() != 0) {
                    // Let user know we're doing something
                    // setText(R.string.working);
                    keys = new String[1];
                    keys[0] = "Working...";

                    // Begin suggestion now but don't wait for it
                    try {
                        SuggestTask suggestTask = new SuggestTask(MainActivity.this, // reference
                                // to
                                // activity
                                original // original text
                        );
                        suggPending = suggThread.submit(suggestTask);
                    } catch (RejectedExecutionException e) {
                        Log.d("Suggest", "5th Exception");
                        // Unable to start new task
                        setText(R.string.error);
                    }
                } else {
                    pb.setVisibility(View.GONE);
                    clearSearch.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    /** Request an update to start after a short delay */
    private void queueUpdate(long delayMillis) {
        Log.d("Stencil", "inside queue");
        // Cancel previous update if it hasn't started yet
        guiThread.removeCallbacks(updateTask);
        // Start an update if nothing happens after a few milliseconds
        guiThread.postDelayed(updateTask, delayMillis);
    }

    /** Display a message */
    private void setText(int id) {
        adapter.clear();
        adapter.add(getResources().getString(id));
    }

    /** Display a list */
    private void setList(List<String> list) {
        adapter.clear();
        listView.setVisibility(View.VISIBLE);
        // adapter.addAll(list); // Could use if API >= 11
        for (String item : list) {
            adapter.add(item);
        }
    }

    /**
     * Modify list on the screen (called from another thread)
     */
    public void setSuggestions(List<String> suggestions, String[] key,
                               String[] gmt) {

        guiSetList(listView, suggestions);
        Log.i("Inside setSuggestions", "Inside");
        keys = new String[key.length];
        keys = key.clone();
        GMT = new String[gmt.length];
        GMT = gmt.clone();

    }

    /** All changes to the GUI must be done in the GUI thread */
    private void guiSetList(final ListView view, final List<String> list) {

        guiThread.post(new Runnable() {
            public void run() {
                setList(list);
                pb.setVisibility(View.GONE);
                clearSearch.setVisibility(View.VISIBLE);
                IsAdapterEmpty();
                listView.setVisibility(View.VISIBLE);
            }

        });

    }


    private void HandleSearch() {
        image_search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitiateSearch.handleToolBar(MainActivity.this, card_search, toolbar, /*view_search,*/ listView, edit_text_search, line_divider);
                hideDark();
//                listContainer.setVisibility(View.GONE);
//                toolbar_shadow.setVisibility(View.VISIBLE);
//                clearItems();
            }
        });
//        edit_text_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    if (edit_text_search.getText().toString().trim().length() > 0) {
////                        clearItems();
//                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(edit_text_search.getWindowToken(), 0);
//                        UpdateQuickSearch(edit_text_search.getText().toString());
//                        listView.setVisibility(View.GONE);
////                        searchFood(edit_text_search.getText().toString(), 0);
////                        toolbar_shadow.setVisibility(View.GONE);
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });
    }

    private void IsAdapterEmpty() {
        if (adapter.getCount() == 0) {
            line_divider.setVisibility(View.GONE);
        } else {
            line_divider.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onBackPressed() {
        if (card_search.getVisibility() == View.VISIBLE) {
            InitiateSearch.handleToolBar(MainActivity.this, card_search, toolbar, /*view_search,*/ listView, edit_text_search, line_divider);
            hideDark();
        } else
            super.onBackPressed();
    }
}
