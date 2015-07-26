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

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.saphion.stencilweather.R;
import com.saphion.stencilweather.adapters.RecyclerViewAdapter;
import com.saphion.stencilweather.adapters.WeatherCardAdapter;
import com.saphion.stencilweather.fragments.WeatherFragment;
import com.saphion.stencilweather.modules.WLocation;
import com.saphion.stencilweather.tasks.GetLocationInfo;
import com.saphion.stencilweather.tasks.SuggestTask;
import com.saphion.stencilweather.utilities.InitiateSearch;
import com.saphion.stencilweather.utilities.Utils;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    //    ViewPager viewPager;
    Toolbar toolbar;
    List<WLocation> drawerItems;
    private RecyclerViewAdapter recyclerAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("Stencil Weather");
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

//        toolbar.setTitleTextColor();


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        setupDrawerContent();


//        viewPager = (ViewPager) findViewById(R.id.viewpager);
//        if (viewPager != null) {
        setupNavigationMode();
//        }

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
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh_common);

        card_search = (CardView) findViewById(R.id.card_search);

        pb = findViewById(R.id.progressSearch);

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

        InitiateSearch();
        HandleSearch();
        IsAdapterEmpty();
    }

    public void setToolBarColor(int color) {
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


    Spinner navSpinner;
    ArrayAdapter<String> navAdapter;

    private void setupNavigationMode() {

        navSpinner = (Spinner) findViewById(R.id.spinner_toolbar);

        ArrayList<String> navBarItems = new ArrayList<>();

        for (int i = 0; i < drawerItems.size(); i++)
            navBarItems.add(drawerItems.get(i).getName());


        navAdapter = new ArrayAdapter<>(toolbar.getContext(), android.R.layout.simple_list_item_1, navBarItems);

        navSpinner.setAdapter(navAdapter);

        navSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    WeatherFragment fragment = WeatherFragment.newInstance(drawerItems.get(i).getId()).setContext(getBaseContext(), drawerItems.get(i));
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                }catch(Exception ignored){}
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        findViewById(R.id.flDarkenBackground).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (card_search.getVisibility() == View.VISIBLE) {
                    InitiateSearch.handleToolBar(MainActivity.this, card_search, toolbar, /*view_search,*/ listView, edit_text_search);
                    hideDark();

                }
                return false;
            }
        });

        initialiseWeatherCards();
    }

    public void setNavPosition(final int position) {
        mDrawerLayout.closeDrawers();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navSpinner.setSelection(position, true);
            }
        }, 350);

    }

    RecyclerView weatherCardList;

    private void initialiseWeatherCards() {
        weatherCardList = (RecyclerView) findViewById(R.id.rvWeatherCards);
        ArrayList<String> weatherCards = new ArrayList<>();
        weatherCards.add("");
        weatherCards.add("");
        weatherCards.add("");
        weatherCards.add("");
        weatherCards.add("");
        weatherCards.add("");
        WeatherCardAdapter cardAdapter = new WeatherCardAdapter(MainActivity.this, weatherCards, MainActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        weatherCardList.setLayoutManager(layoutManager);
        weatherCardList.setAdapter(cardAdapter);
    }

    public void hideDark() {

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

    public void showDark() {
        findViewById(R.id.flDarkenBackground).setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeIn)
                .duration(500)
                .playOn(findViewById(R.id.flDarkenBackground));
    }

    private void setupDrawerContent() {


        RecyclerView rv = (RecyclerView) findViewById(R.id.rvLocation);
        rv.setLayoutManager(new LinearLayoutManager(this));

        drawerItems = WLocation.listAll(WLocation.class);
        recyclerAdapter = new RecyclerViewAdapter(getBaseContext(), drawerItems, MainActivity.this);
        rv.setAdapter(recyclerAdapter);
    }

    public void addItemToDrawer(WLocation wLocation, boolean isMyLocation) {
        if (isMyLocation) {
            drawerItems.add(0, wLocation);
            recyclerAdapter.notifyDataSetChanged();
            //TODO Add for my location
        } else {
            drawerItems.add(wLocation);
            recyclerAdapter.notifyDataSetChanged();
            navAdapter.add(wLocation.getName());
            navAdapter.notifyDataSetChanged();
        }
    }

    public void removeItemFromDrawer(WLocation wLocation) {
        drawerItems.remove(wLocation);
        recyclerAdapter.remove(wLocation);
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
            suggestionThread.shutdownNow();
            Utils.hideKeyboard(edit_text_search, getBaseContext());
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    public MyAdapter suggestionAdapter;
    private Handler guiThread;
    private ExecutorService suggestionThread;
    private Runnable updateTask;
    private Future<?> suggestionPending;

    public void actionBarContent(boolean show) {
        if (navSpinner != null)
            navSpinner.setVisibility(show?View.VISIBLE:View.GONE);
    }

    public void changePosition(final int position, final WLocation wLocation) {
        mDrawerLayout.closeDrawers();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(navSpinner.getSelectedItemPosition() == position)
                    navSpinner.setSelection(position, true);
                try {
                    navAdapter.remove(navAdapter.getItem(position));
                    navAdapter.notifyDataSetChanged();
                    drawerItems.remove(wLocation);
                }catch (Exception ignored){}
            }
        }, 350);

    }


    public class MyAdapter extends BaseAdapter {
        private Context context;
        private List<WLocation> objects;

        public MyAdapter(Context context,
                         List<WLocation> objects) {
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

        public void add(WLocation item) {
            objects.add(item);
            notifyDataSetChanged();
        }

        public void clear() {
            objects.clear();
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if (convertView == null) {

                // inflate the layout
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                convertView = inflater.inflate(R.layout.location_action_item, parent, false);

                // well set up the ViewHolder
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.tvLocationName);

                // store the holder with the view.
                convertView.setTag(viewHolder);

            } else {
                // we've just avoided calling findViewById() on resource everytime
                // just use the viewHolder
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.textView.setText(objects.get(position).getName());

            return convertView;
        }

        class ViewHolder {
            public TextView textView;
        }
    }

    /**
     * Set up suggestionAdapter for list view.
     */
    private void setAdapters() {
        List<WLocation> items = new ArrayList<WLocation>();
        suggestionAdapter = new MyAdapter(this, items);

        listView.setAdapter(suggestionAdapter);

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
                        InitiateSearch.handleToolBar(MainActivity.this, card_search, toolbar, /*view_search,*/ listView, edit_text_search);
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

                if (!(parent.getItemAtPosition(position).toString()
                        .equalsIgnoreCase("Loading...")
                        || parent.getItemAtPosition(position).toString()
                        .equalsIgnoreCase("No suggestions") || parent
                        .getItemAtPosition(position)
                        .toString()
                        .equalsIgnoreCase(
                                "Unable To Connect to Internet, Please Check Your Network Settings."))) {


                    new GetLL(getBaseContext(), (WLocation) parent.getItemAtPosition(position)).execute();

                    if (card_search.getVisibility() == View.VISIBLE) {
                        InitiateSearch.handleToolBar(MainActivity.this, card_search, toolbar, /*view_search,*/ listView, edit_text_search);
                        hideDark();
                    }
                    listView.setVisibility(View.GONE);
                    edit_text_search.setText("");
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(edit_text_search.getWindowToken(), 0);

                }
            }
        });


        edit_text_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edit_text_search.getText().toString().length() == 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                    clearSearch.setImageResource(R.drawable.goto_icon);
                    suggestionAdapter.clear();
                    IsAdapterEmpty();
                    listView.setVisibility(View.GONE);
                    pb.setVisibility(View.GONE);

                    if (suggestionPending != null)
                        suggestionPending.cancel(true);

                } else {
                    clearSearch.setImageResource(R.drawable.ic_close);
                    IsAdapterEmpty();

                    pb.setVisibility(View.VISIBLE);
                    clearSearch.setVisibility(View.GONE);
                    suggestionAdapter.clear();
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

//                    Utils.hideKeyboard(edit_text_search, MainActivity.this);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InitiateSearch.handleToolBar(MainActivity.this, card_search, toolbar, /*view_search,*/ listView, edit_text_search);
                            hideDark();
                            showDialog();
                        }
                    }, 200);


                } else {
//                    mAsyncTask.cancel(true);
                    edit_text_search.setText("");
                    listView.setVisibility(View.GONE);
                    suggestionAdapter.clear();
//                    clearItems();
                    ((InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    IsAdapterEmpty();
                }
            }
        });


    }

    private void showDialog(){
        final View dialogView = View.inflate(MainActivity.this, R.layout.layout_add_location_point, null);

        AlertDialog.Builder builder =  new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Location");
        builder.setView(dialogView);

        builder.setNegativeButton("CANCEL", null);
        builder.setPositiveButton("LOCATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Utils.hideKeyboard(edit_text_search, MainActivity.this);
                    }
                }, 200);
            }
        });

        builder.show();

    }


    /**
     * Initialize multi-threading. There are two threads: 1) The main graphical
     * user interface thread already started by Android, and 2) The suggest
     * thread, which we start using an executor.
     */
    private void initThreading() {
        guiThread = new Handler();
        suggestionThread = Executors.newSingleThreadExecutor();

        // This task gets suggestions and updates the screen
        updateTask = new Runnable() {
            public void run() {
                // Get text to suggest
                String original = edit_text_search.getText().toString().trim();

                // Cancel previous suggestion if there was one
                if (suggestionPending != null)
                    suggestionPending.cancel(true);

                // Check to make sure there is text to work onSuggest
                if (original.length() != 0) {
                    // Let user know we're doing something
                    // setText(R.string.working);

                    // Begin suggestion now but don't wait for it
                    try {
                        SuggestTask suggestTask = new SuggestTask(MainActivity.this, // reference to activity
                                original // original text
                        );
                        suggestionPending = suggestionThread.submit(suggestTask);
                    } catch (RejectedExecutionException e) {
                        Log.d("Suggest", "5th Exception");
                        // Unable to start new task
//                        setText(R.string.error);
                        Toast.makeText(MainActivity.this, "Unable to process request, please try again.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    pb.setVisibility(View.GONE);
                    clearSearch.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    /**
     * Request an update to start after a short delay
     */
    private void queueUpdate(long delayMillis) {
        Log.d("Stencil", "inside queue");
        // Cancel previous update if it hasn't started yet
        guiThread.removeCallbacks(updateTask);
        // Start an update if nothing happens after a few milliseconds
        guiThread.postDelayed(updateTask, delayMillis);
    }

//    /** Display a message */
//    private void setText(int id) {
//        suggestionAdapter.clear();
//        suggestionAdapter.add(getResources().getString(id));
//    }

    /**
     * Display a list
     */
    private void setList(List<WLocation> list) {
        suggestionAdapter.clear();
        listView.setVisibility(View.VISIBLE);
        // suggestionAdapter.addAll(list); // Could use if API >= 11
        for (WLocation item : list) {
            suggestionAdapter.add(item);
        }
    }

    /**
     * Modify list on the screen (called from another thread)
     */
    public void setSuggestions(List<WLocation> suggestions) {

        guiSetList(listView, suggestions);

    }

    /**
     * All changes to the GUI must be done in the GUI thread
     */
    private void guiSetList(final ListView view, final List<WLocation> list) {

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
                InitiateSearch.handleToolBar(MainActivity.this, card_search, toolbar, /*view_search,*/ listView, edit_text_search);
                hideDark();
                edit_text_search.setText("");
                listView.setVisibility(View.GONE);
            }
        });
    }

    private void IsAdapterEmpty() {
        if (suggestionAdapter.getCount() == 0) {
            line_divider.setVisibility(View.GONE);
        } else {
            line_divider.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else if (card_search.getVisibility() == View.VISIBLE) {
            InitiateSearch.handleToolBar(MainActivity.this, card_search, toolbar, /*view_search,*/ listView, edit_text_search);
            hideDark();
        } else
            super.onBackPressed();
    }

    public class GetLL extends AsyncTask<Object, Integer, Boolean> {

        WLocation location;
        Context mContext;

        AlertDialog ad;

        public GetLL(Context mContext, WLocation location) {
            this.mContext = mContext;
            this.location = location;
        }

        @Override
        protected void onPreExecute() {
            Utils.hideKeyboard(edit_text_search, getBaseContext());
            ad = Utils.getProgressDialog(MainActivity.this, "Adding...");
            ad.show();
            ad.setCancelable(false);
            ad.getWindow().setLayout(Utils.dpToPx(200, MainActivity.this), Utils.dpToPx(125, MainActivity.this));
            super.onPreExecute();
        }


        @Override
        protected Boolean doInBackground(Object... arg) {


            if (location.getTimezone().equalsIgnoreCase("MISSING")) {
                try {
                    GetLocationInfo gl = new GetLocationInfo();
                    location.setTimezone(gl.getTimezone(location.getLatitude(), location.getLongitude())
                            .getDisplayName());
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            ad.dismiss();

            long id = location.checkAndSave();

            if (id != -1) {
                Toast.makeText(MainActivity.this, location.getName() + " added", Toast.LENGTH_LONG).show();
                location.setId(id);
                addItemToDrawer(location, location.isMyLocation());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setNavPosition(navAdapter.getCount() - 1);
                    }
                }, 200);

            } else {
                Toast.makeText(MainActivity.this, location.getName() + " already exists.", Toast.LENGTH_LONG).show();
            }

            super.onPostExecute(result);
        }

    }
}
