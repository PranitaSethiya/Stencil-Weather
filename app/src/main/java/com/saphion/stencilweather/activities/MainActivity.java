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
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
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
import android.text.Html;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.saphion.stencilweather.R;
import com.saphion.stencilweather.adapters.RecyclerViewAdapter;
import com.saphion.stencilweather.adapters.ShareItemViewAdapter;
import com.saphion.stencilweather.adapters.WeatherCardAdapter;
import com.saphion.stencilweather.fragments.WeatherFragment;
import com.saphion.stencilweather.modules.WLocation;
import com.saphion.stencilweather.tasks.GetLocationInfo;
import com.saphion.stencilweather.tasks.SuggestTask;
import com.saphion.stencilweather.utilities.InitiateSearch;
import com.saphion.stencilweather.utilities.Utils;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private DrawerLayout mDrawerLayout;
    //    ViewPager viewPager;
    Toolbar toolbar;
    List<WLocation> drawerItems;
    private RecyclerViewAdapter recyclerAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Utils.isFirstStart(MainActivity.this)){
//          TODO uncomment this  Utils.incAppCount(MainActivity.this);
            startActivity(new Intent(MainActivity.this, SplashActivity.class));
            finish();
            return;
        }

        Utils.incAppCount(MainActivity.this);

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
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (!hidden)
                    initiateActions(true);
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        setupDrawerContent();

        setupNavigationMode();

        findViewById(R.id.ivMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });


        initialiseThings();

        // Clear Cache
        new DeleteCache().execute();

        if(Utils.isLocationEnabled(MainActivity.this))
        buildGoogleApiClient();
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alert");
            builder.setMessage("Location Services are not enabled.\n<i>Location Services help us to determine your current location.</i>\n\nEnable them now?");
            builder.setPositiveButton("SURE", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(
                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                }
            });
            builder.setNegativeButton("CANCEL", null);

            builder.show();
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if(mGoogleApiClient!= null){
            mGoogleApiClient.connect();
        }
    }

    View line_divider;
    EditText edit_text_search;
    ImageView image_search_back;
    ImageView clearSearch;
    ListView listView;
    CardView card_search;
    View pb;
    View actionsContainer;

    private void initialiseThings() {


        line_divider = findViewById(R.id.line_divider);
        edit_text_search = (EditText) findViewById(R.id.edit_text_search);
        image_search_back = (ImageView) findViewById(R.id.image_search_back);
        clearSearch = (ImageView) findViewById(R.id.clearSearch);
        listView = (ListView) findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh_common);

        card_search = (CardView) findViewById(R.id.card_search);

        actionsContainer = findViewById(R.id.container_actions);
        actionsContainer.setVisibility(View.INVISIBLE);

        findViewById(R.id.fab_graph).setOnClickListener(this);
        findViewById(R.id.fab_map).setOnClickListener(this);
        findViewById(R.id.fab_share).setOnClickListener(this);

        pb = findViewById(R.id.progressSearch);

        findViewById(R.id.flActionsFab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hidden)
                    initiateActions(true);
            }
        });

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

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(listView.getAdapter() != null)
                    if(!listView.getAdapter().isEmpty())
                        Utils.hideKeyboard(edit_text_search, MainActivity.this);
            }
        });

        InitiateSearch();
        HandleSearch();
        IsAdapterEmpty();
    }


    public void setToolBarColor(int color) {
        try {
//            findViewById(R.id.appbar).setBackgroundColor(color);
            toolbar.setBackgroundColor(Color.TRANSPARENT);

            Integer colorFrom = Color.TRANSPARENT;
            Drawable background = findViewById(R.id.appbar).getBackground();
            if (background instanceof ColorDrawable)
                colorFrom = ((ColorDrawable) background).getColor();
            Integer colorTo = color;
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    findViewById(R.id.appbar).setBackgroundColor((Integer) animator.getAnimatedValue());
                }

            });
            colorAnimation.setDuration(350);
            colorAnimation.start();

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
                } catch (Exception ignored) {
                }
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

    public WLocation getSelectedLocation() {
        return drawerItems.get(navSpinner.getSelectedItemPosition());
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
            navSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void changePosition(final int position, final WLocation wLocation) {
        mDrawerLayout.closeDrawers();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (navSpinner.getSelectedItemPosition() == position)
                    navSpinner.setSelection(position, true);
                try {
                    navAdapter.remove(navAdapter.getItem(position));
                    navAdapter.notifyDataSetChanged();
                    drawerItems.remove(wLocation);
                } catch (Exception ignored) {
                }
            }
        }, 350);

    }

    @Override
    public void onClick(final View view) {
        initiateActions(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (view.getId()) {
                    case R.id.fab_graph:

                        break;
                    case R.id.fab_map:

                        startActivity(new Intent(MainActivity.this, MapActivity.class));
                        finish();

                        break;
                    case R.id.fab_share:

                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);

                        View mView = LayoutInflater.from(MainActivity.this).inflate(R.layout.share_weather_layout, null);

                        mBuilder.setView(mView);

                        shareContainer = mView.findViewById(R.id.shareableContainer);


                        final List<Drawable> packageIcons = new ArrayList<>();
                        final List<String> appNames = new ArrayList<>();
                        final List<String> packageNames = new ArrayList<>();
                        final List<String> classNames = new ArrayList<>();
                        final RecyclerView shareRecyclerView = (RecyclerView) mView.findViewById(R.id.rvWeatherShare);
                        final View flShareableApps = mView.findViewById(R.id.flShareableApps);
                        flShareableApps.setAlpha(0);
                        shareRecyclerView.setVisibility(View.VISIBLE);

                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... voids) {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.setType("image/jpeg");
                                List<ResolveInfo> resolveInfoList = getPackageManager()
                                        .queryIntentActivities(sendIntent, 0);

                                Collections.sort(resolveInfoList,
                                        new ResolveInfo.DisplayNameComparator(getPackageManager()));

                                for (ResolveInfo resolveInfo : resolveInfoList) {
                                    Drawable icon = resolveInfo.loadIcon(getPackageManager());
                                    String name = resolveInfo.loadLabel(getPackageManager()).toString();

                                    packageIcons.add(icon);
                                    appNames.add(name);
                                    packageNames.add(resolveInfo.activityInfo.packageName);
                                    classNames.add(resolveInfo.activityInfo.name);

                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);

                                ShareItemViewAdapter shareAdapter = new ShareItemViewAdapter(MainActivity.this, packageIcons, appNames, packageNames, classNames);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                shareRecyclerView.setLayoutManager(layoutManager);
                                shareRecyclerView.setAdapter(shareAdapter);

                                YoYo.with(Techniques.SlideInDown).duration(500).interpolate(new OvershootInterpolator()).playOn(flShareableApps);

                            }
                        }.execute();



                        mShareDialog = mBuilder.create();
                        mShareDialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
                        mShareDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        mShareDialog.show();

                        break;
                }
            }
        }, 200);

    }

    View shareContainer;
    AlertDialog mShareDialog;
    public void share(String packageName, String className) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);

        String name = drawerItems.get(navSpinner.getSelectedItemPosition()).getName();
        if(name.contains(",")){
            name = name.split(",")[0];
        }

        try {

            shareContainer.setDrawingCacheEnabled(true);
            shareContainer.buildDrawingCache();
            Bitmap icon = shareContainer.getDrawingCache();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File f = new File(getExternalCacheDir() + File.separator + "shared" + File.separator + name + "_weather_" + System.currentTimeMillis()  + ".jpg");
            try {
                f.getParentFile().mkdirs();
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
            sendIntent.putExtra(Intent.EXTRA_TEXT, name + " Weather shared by Stencil Weather");

            Log.d("Stencil Weather", "tmp file URI " + f.getAbsolutePath());
            sendIntent
                    .setComponent(new ComponentName(
                            packageName,
                            className));
            sendIntent.setType("image/jpeg");
            startActivity(sendIntent);
            shareContainer = null;
            mShareDialog.dismiss();
            mShareDialog = null;
        } catch (Exception ignored){}

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Stencil", "OnConnected");
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d("Stencil", "Lat: " + String.valueOf(mLastLocation.getLatitude()) + " Lng: " + String.valueOf(mLastLocation.getLongitude()));
        }


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Stencil", "OnConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Stencil", "OnConnectionFailed");
    }


    public class DeleteCache extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Utils.deleteFile(new File(getExternalCacheDir() + File.separator + "shared" + File.separator));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
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

            String name = objects.get(position).getName();
            String splitWord = edit_text_search.getText().toString();

            Log.d("Stencil Weather", "Split Word: " + splitWord);
            Log.d("Stencil Weather", "Complete Word: " + name);

            try {
                if(!splitWord.isEmpty())
                viewHolder.textView.setText(Html.fromHtml("<b>" + (splitWord.charAt(0) + "").toUpperCase(Locale.getDefault()) + splitWord.substring(1, splitWord.length()) + "</b>"
                        + name.substring(name.indexOf(splitWord) + splitWord.length())));
                else
                    viewHolder.textView.setText(name);
            }catch(Exception ex){
                viewHolder.textView.setText(name);
                ex.printStackTrace();
                Log.e("Stencil Error", ex.getLocalizedMessage());
            }

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
                    case R.id.action_more:
                        initiateActions(true);
                        return true;
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
                    clearSearch.setVisibility(View.GONE);
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
                if (edit_text_search.getText().toString().length() > 0) {
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

    boolean hidden = true;

    public void bounceScaleAnimation(View mView, int delay){

//        YoYo.with(Techniques.ZoomIn).interpolate(new BounceInterpolator()).duration(180).delay(delay).playOn(mView);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        pulse.setStartOffset(delay);
        mView.startAnimation(pulse);
    }


    public void initiateActions(boolean animate) {

        // get the center for the clipping circle
        int cx = actionsContainer.getRight() - Utils.dpToPx(25, MainActivity.this);
        int cy = actionsContainer.getTop() + Utils.dpToPx(60, MainActivity.this);

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(actionsContainer.getWidth(), actionsContainer.getHeight());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            android.animation.Animator animator = android.view.ViewAnimationUtils.createCircularReveal(actionsContainer, cx, cy, 0, finalRadius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(animate?300:1);

            android.animation.Animator animator_reverse = android.view.ViewAnimationUtils.createCircularReveal(actionsContainer, cx, cy, finalRadius, 0);
            animator_reverse.setInterpolator(new AccelerateDecelerateInterpolator());
            animator_reverse.setDuration(animate?300:1);

            if (hidden) {
                actionsContainer.setVisibility(View.VISIBLE);
                animator.start();
                bounceScaleAnimation(findViewById(R.id.containerFabShare), 80);
                bounceScaleAnimation(findViewById(R.id.containerFabMap), 120);
                bounceScaleAnimation(findViewById(R.id.containerFabGraph), 160);
                hidden = false;
            } else {
                animator_reverse.addListener(new android.animation.Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(android.animation.Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(android.animation.Animator animator) {
                        actionsContainer.setVisibility(View.GONE);
                        hidden = true;
                    }

                    @Override
                    public void onAnimationCancel(android.animation.Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(android.animation.Animator animator) {

                    }
                });
                animator_reverse.start();
            }

        } else {

            SupportAnimator animator =
                    ViewAnimationUtils.createCircularReveal(actionsContainer, cx, cy, 0, finalRadius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(animate?300:1);

            SupportAnimator animator_reverse = animator.reverse();
//            animator.start();
//
            if (hidden) {
                actionsContainer.setVisibility(View.VISIBLE);
                animator.start();

                bounceScaleAnimation(findViewById(R.id.containerFabShare), 80);
                bounceScaleAnimation(findViewById(R.id.containerFabMap), 120);
                bounceScaleAnimation(findViewById(R.id.containerFabGraph), 160);

                hidden = false;
            } else {
                animator_reverse.addListener(new SupportAnimator.AnimatorListener() {

                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {
                        actionsContainer.setVisibility(View.GONE);
                        hidden = true;
                    }

                    @Override
                    public void onAnimationCancel() {

                    }

                    @Override
                    public void onAnimationRepeat() {

                    }
                });
                animator_reverse.start();
            }
        }

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
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else if (card_search.getVisibility() == View.VISIBLE) {
            InitiateSearch.handleToolBar(MainActivity.this, card_search, toolbar, /*view_search,*/ listView, edit_text_search);
            hideDark();
        } else if (!hidden){
            initiateActions(true);
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
