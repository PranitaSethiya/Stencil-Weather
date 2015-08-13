package com.saphion.stencilweather.activities;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.saphion.stencilweather.R;
import com.saphion.stencilweather.adapters.MapListAdapter;
import com.saphion.stencilweather.modules.WLocation;
import com.saphion.stencilweather.modules.WeatherItem;
import com.saphion.stencilweather.tasks.GetLocationInfo;
import com.saphion.stencilweather.utilities.InitiateLatLon;
import com.saphion.stencilweather.utilities.Utils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements View.OnClickListener {

    // Google Map
    private GoogleMap googleMap;
    MarkerOptions marker = new MarkerOptions();
    private Toolbar toolbar;
    SlidingUpPanelLayout slidingLayout;
    View pbActionMode;
    View showWeather;
    View slidingBackground;
    FloatingActionButton fabAddLocation1, fabAddLocation2;
    View fabContainer;
    TextView tvTitle, tvCoordinates;
    View closeMap;
    ArrayList<Marker> allMarkers = new ArrayList<>();


    //for lat lon search
    View search;
    EditText etLat, etLon;
//    TextInputLayout tilLat, tilLon;
    View latLonBackground;
    private MaterialMenuDrawable materialMenu;
    private boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Weather Map");

        setToolBarColor(getResources().getColor(R.color.map_blue));

        setSupportActionBar(toolbar);

//        ActionBar ab = getSupportActionBar();
//        if (ab != null) {
//            ab.setHomeAsUpIndicator(R.drawable.ic_close);
//            ab.setDisplayHomeAsUpEnabled(true);
//        }

        materialMenu = new MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
        materialMenu.setIconState(MaterialMenuDrawable.IconState.ARROW);
        toolbar.setNavigationIcon(materialMenu);

        tvTitle = (TextView) findViewById(R.id.tvMapLocationName);
        tvCoordinates = (TextView) findViewById(R.id.tvMapLocationXY);

        slidingBackground = findViewById(R.id.slideBackground);

        fabContainer = findViewById(R.id.fabContainer);
        fabAddLocation1 = (FloatingActionButton) findViewById(R.id.fabAddLocation1);
        fabAddLocation2 = (FloatingActionButton) findViewById(R.id.fabAddLocation2);

        pbActionMode = findViewById(R.id.pbActionMode);
        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingLayout.setCoveredFadeColor(Color.TRANSPARENT);
        showWeather = findViewById(R.id.tvMapShowWeather);
        showWeather.setVisibility(View.VISIBLE);

        closeMap = findViewById(R.id.llMapClose);


        //Lat Lon
        search = findViewById(R.id.card_search);
        etLat = (EditText) findViewById(R.id.etLatitude);
        etLon = (EditText) findViewById(R.id.etLongitude);

//        tilLat = (TextInputLayout) findViewById(R.id.tilLat);
//        tilLon = (TextInputLayout) findViewById(R.id.tilLon);

        latLonBackground = findViewById(R.id.flDarkenBackground);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                slidingLayout.setFloatingActionButtonVisibility(View.GONE);
            }
        }, 50);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fabContainer.setVisibility(View.GONE);
            }
        }, 50);


        try {
            // Loading map
            setUpMapIfNeeded();

        } catch (Exception e) {
            e.printStackTrace();
            startActivity(new Intent(MapActivity.this, MainActivity.class));
            finish();
            overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);
        }


    }

    private Marker getLatestMarker() {
        if (!allMarkers.isEmpty())
            return allMarkers.get(allMarkers.size() - 1);
        return null;
    }

    public void setSlidingBackgroundColor(boolean expanded) {

        if (expanded) {
            Integer colorFrom = Color.WHITE;
            Integer colorTo = getResources().getColor(R.color.map_blue);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    slidingBackground.setBackgroundColor((Integer) animator.getAnimatedValue());
                }
            });

            colorAnimation.setDuration(150);
            colorAnimation.start();
        } else {
            slidingBackground.setBackgroundColor(Color.WHITE);
        }
    }

    public void setFabColor(boolean expanded) {
        fabAddLocation1.setVisibility(expanded ? View.GONE : View.VISIBLE);
        fabAddLocation2.setVisibility(expanded ? View.VISIBLE : View.GONE);
    }

    public void setToolBarColor(int color) {
        try {
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

    public void setUpListeners() {
        findViewById(R.id.mapRight).setOnClickListener(this);
        findViewById(R.id.mapLeft).setOnClickListener(this);
        findViewById(R.id.mapLocationList).setOnClickListener(this);
        fabAddLocation1.setOnClickListener(this);
        fabAddLocation2.setOnClickListener(this);

        googleMap.setOnMapLongClickListener(new OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latlng) {

                mLoc = 0;

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latlng.latitude, latlng.longitude))
                        .zoom(12).build();

                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

                new AddMarker(latlng).execute();

            }
        });

        googleMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                removeAllMarkers();

                slidingLayout.setFloatingActionButtonVisibility(View.GONE);
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);


            }
        });

        googleMap
                .setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {

                    @Override
                    public boolean onMyLocationButtonClick() {
                        try {

                            mLoc = 1;

                            new AddMarker(new LatLng(googleMap
                                    .getMyLocation().getLatitude(), googleMap
                                    .getMyLocation().getLongitude())).execute();
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(googleMap
                                            .getMyLocation().getLatitude(),
                                            googleMap.getMyLocation()
                                                    .getLongitude())).zoom(12)
                                    .build();

                            googleMap.animateCamera(CameraUpdateFactory
                                    .newCameraPosition(cameraPosition));

                        } catch (Exception ex) {
                        }
                        return true;
                    }
                });

        googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker mrkr) {
                if (markersUnExp.contains(mrkr)) {
                    currPos = markersUnExp.indexOf(mrkr);
                    moveToAndExpand(mrkr.getPosition(), currPos);

                } else if (markersExp.contains(mrkr)) {
                    currPos = markersExp.indexOf(mrkr);
                    moveToAndExpand(mrkr.getPosition(), currPos);
                } else {

                    return false;
                }

                return true;
            }
        });

        googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition arg0) {
                zoom = arg0.zoom;
            }
        });

        closeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                removeAllMarkers();
            }
        });

        slidingLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                if (showWeather.getVisibility() == View.VISIBLE) {
                    showWeather.setVisibility(View.GONE);
                    setSlidingBackgroundColor(true);
                    setFabColor(true);
                    tvTitle.setTextColor(Color.WHITE);
                    tvCoordinates.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onPanelCollapsed(View view) {
                slidingLayout.setFloatingActionButtonVisibility(View.GONE);
            }

            @Override
            public void onPanelExpanded(View view) {

                Marker tempMarker = getLatestMarker();

                if (tempMarker != null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(tempMarker.getPosition().latitude - 0.045, tempMarker.getPosition().longitude))
                            .zoom(12).build();

                    googleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                }

            }

            @Override
            public void onPanelAnchored(View view) {
                if (showWeather.getVisibility() != View.VISIBLE) {
                    showWeather.setVisibility(View.VISIBLE);
                    slidingLayout.setFloatingActionButtonVisibility(View.VISIBLE);
                    setSlidingBackgroundColor(false);
                    setFabColor(false);
                    tvTitle.setTextColor(getResources().getColor(R.color.colorTernaryText));
                    tvCoordinates.setTextColor(getResources().getColor(R.color.colorTernaryText));


                    Marker tempMarker = getLatestMarker();

                    if (tempMarker != null) {
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(tempMarker.getPosition().latitude, tempMarker.getPosition().longitude))
                                .zoom(12).build();

                        googleMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(cameraPosition));
                    }

                }
            }

            @Override
            public void onPanelHidden(View view) {
                slidingLayout.setFloatingActionButtonVisibility(View.GONE);
                showWeather.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        removeAllMarkers();
                    }
                }, 10);
            }

            @Override
            public void onPanelHiddenExecuted(View view, Interpolator interpolator, int i) {

            }

            @Override
            public void onPanelShownExecuted(View view, Interpolator interpolator, int i) {

            }

            @Override
            public void onPanelExpandedStateY(View view, boolean b) {

            }

            @Override
            public void onPanelCollapsedStateY(View view, boolean b) {

            }

            @Override
            public void onPanelLayout(View view, SlidingUpPanelLayout.PanelState panelState) {

            }
        });

        showWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });
        findViewById(R.id.rlSlidingViewContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        latLonBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InitiateLatLon.handleToolBar(MapActivity.this, search, toolbar, etLat, etLon, latLonBackground, materialMenu);
            }
        });

        etLon.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchLocation();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.ivSearchLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchLocation();
            }
        });

    }

    private void searchLocation() {

        if (etLat == null
                || etLon == null) {
            Toast.makeText(getBaseContext(),
                    "Invalid coordinates, Please try again!!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        double lat = Double.parseDouble(etLat.getText().toString());
        double lon = Double.parseDouble(etLon.getText().toString());

        if ((lat + "").isEmpty() || (lon + "").isEmpty())
            return;

        LatLng latlng = new LatLng(lat, lon);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latlng.latitude,
                        latlng.longitude)).zoom(zoom).build();

        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        InitiateLatLon.handleToolBar(MapActivity.this, search, toolbar, etLat, etLon, latLonBackground, materialMenu);

        new AddMarker(latlng).execute();
    }

    float zoom = 12;

    ArrayList<WeatherItem> wi = new ArrayList<WeatherItem>();
    // ArrayList<LatLng> wiLL = new ArrayList<LatLng>();
    List<WLocation> mLocation;
    String mThumbIds[] = {"Pune", "Dehradun", "Udaipur", "New Delhi", "Goa"};
    int conds[] = {R.drawable.showers_light, R.drawable.rain_light,
            R.drawable.mostlycloudy_light_d, R.drawable.thunderstorm_light,
            R.drawable.cloudy_light};
    String conditions[] = {"Rain", "Hail", "Partly Cloudy", "Sleet", "Cloudy"};
    String temps[] = {"35°C", "25°C", "28°C", "30°C", "39°C"};
    int temp[] = {35, 25, 28, 30, 39};

    private void loadData() {

        mLocation = WLocation.listAll(WLocation.class);

        int k = 0;

        // int size = mLocation.size();
        for (int index = 0; index < mLocation.size(); index++) {

            wi.add(new WeatherItem(mLocation.get(index).getUniqueID(), mLocation.get(index).getName(), temp[k],
                    conditions[k], index, conds[k], "C"));
            k = (k + 1) % temp.length;

        }

        addMarkers();
        if (markersExp.size() > 0) {

            Work();
            moveToAndExpand(new LatLng(mLocation.get(0).getLatitude(), mLocation.get(0).getLongitude()), 0);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .zoom(zoom).target(new LatLng(mLocation.get(0).getLatitude(), mLocation.get(0).getLongitude())).build();

            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }

    }

    private void addMarkers() {

        for (int i = 0; i < wi.size(); i++) {
            MarkerOptions mMarkerOptions = new MarkerOptions();
            mMarkerOptions.position(new LatLng(mLocation.get(i).getLatitude(), mLocation.get(i).getLongitude()));// .title(display);
            mMarkerOptions.draggable(false);

            markersExp.add(googleMap.addMarker(mMarkerOptions));
            markersExp.get(i).setVisible(false);

            markersUnExp.add(googleMap.addMarker(mMarkerOptions));
            markersExp.get(i).setVisible(true);

        }

    }

    public void moveToAndExpand(LatLng ll, int position) {

        Log.d("Stencil Weather", "Moving to: " + ll.latitude + ", " + ll.longitude);

        CameraPosition cameraPosition = new CameraPosition.Builder().zoom(zoom)
                .target(ll).build();

        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        markersExp.get(position).setVisible(true);
        markersUnExp.get(position).setVisible(false);
        for (int i = 0; i < markersExp.size(); i++) {
            if (i == position)
                continue;
            markersExp.get(i).setVisible(false);
            markersUnExp.get(i).setVisible(true);
        }

    }

    public void Work() {

        new DoWorkExpand().execute();
        new DoWorkUnExpand().execute();

    }



    public class DoWorkExpand extends
            AsyncTask<Object, Void, ArrayList<Bitmap>> {

        @Override
        protected void onPostExecute(ArrayList<Bitmap> result) {
            for (int i = 0; i < result.size(); i++)
                markersExp.get(i).setIcon(
                        BitmapDescriptorFactory.fromBitmap(result.get(i)));

            super.onPostExecute(result);
        }

        @Override
        protected ArrayList<Bitmap> doInBackground(Object... params) {

            ArrayList<Bitmap> mbmps = new ArrayList<>();

            for (int i = 0; i < markersExp.size(); i++) {

                mbmps.add(createMyDrawableFromBitmap(MapActivity.this, wi
                        .get(i).getName(), wi.get(i).getTemp() + "°", wi.get(i)
                        .getUnit(), wi.get(i).getCondition(), wi.get(i)
                        .getCondID()));

            }
            return mbmps;
        }

    }

    public class DoWorkUnExpand extends
            AsyncTask<Object, Void, ArrayList<Bitmap>> {

        @Override
        protected void onPostExecute(ArrayList<Bitmap> result) {

            for (int i = 0; i < result.size(); i++)
                markersUnExp.get(i).setIcon(
                        BitmapDescriptorFactory.fromBitmap(result.get(i)));
            super.onPostExecute(result);
        }

        @Override
        protected ArrayList<Bitmap> doInBackground(Object... params) {

            ArrayList<Bitmap> mbmps = new ArrayList<>();

            for (int i = 0; i < markersUnExp.size(); i++) {

                mbmps.add(smallBalloon(wi.get(i).getCondID()));

            }
            return mbmps;
        }

    }

    ArrayList<Marker> markersExp = new ArrayList<>();
    ArrayList<Marker> markersUnExp = new ArrayList<>();
    int mLoc = 0;

    private Bitmap createMyDrawableFromBitmap(Context context, String cName,
                                              String temp, String unit, String condition, int mCond) {

        Bitmap bitmap = Bitmap.createBitmap(
                Utils.dpToPx(180, context),
                Utils.dpToPx(100, context),
                Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);
        Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG
                | Paint.ANTI_ALIAS_FLAG);

        NinePatchDrawable bg = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.balloon_overlay_black);
        if (bg != null) {
            bg.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            bg.setAlpha(150);
            bg.draw(c);
        }

        Bitmap cond = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), mCond),
                (int) (bitmap.getHeight() * 0.8),
                (int) (bitmap.getHeight() * 0.8), true);

        float w = (float) (bitmap.getWidth() * 0.07);
        c.drawBitmap(cond, w, (float) (((bitmap.getHeight() - Utils.dpToPx(15, context)) * 0.5) - (cond.getHeight() * 0.5)),
                mPaint);

        mPaint.setTextSize(Utils.spToPx(context, 31f));
        mPaint.setColor(0xffffffff);

        float h = (float) ((bitmap.getHeight() * 0.3) + (float) (mPaint
                .getFontSpacing() * 0.2));
        w = w + cond.getWidth();
        c.drawText(temp, w, h, mPaint);
        float w2 = (float) (w + mPaint.measureText(temp));

        mPaint.setTextSize(Utils.spToPx(context, 12.5f));

        c.drawText(unit, w2, h, mPaint);

        mPaint.setTextSize(Utils.spToPx(context, 16f));
        h = h + mPaint.getFontSpacing();
        // mPaint.setColor(0xff529bcc);

        Paint fillPaint = new Paint(Paint.FILTER_BITMAP_FLAG
                | Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(0xff529bcc);
        fillPaint.setTextSize(mPaint.getTextSize());
        c.drawText(condition, w, h, fillPaint);

        Paint stkPaint = new Paint(Paint.FILTER_BITMAP_FLAG
                | Paint.ANTI_ALIAS_FLAG);
        stkPaint.setTextSize(mPaint.getTextSize());
        stkPaint.setStyle(Paint.Style.STROKE);
        stkPaint.setStrokeWidth(Utils.spToPx(context, .1f));
        stkPaint.setColor(Color.WHITE);
        c.drawText(condition, w, h, stkPaint);

        // c.drawText(condition, w, h, mPaint);

        mPaint.setTextSize(Utils.spToPx(context, 12.5f));
        mPaint.setColor(0xffffffff);
        h = h + mPaint.getFontSpacing();
        if (cName.length() > 15) {
            if (cName.contains(",")) {
                cName = cName.substring(0, cName.indexOf(","));
            }

            if (cName.length() > 15) {
                cName = cName.substring(0, 12) + "..";
            }

        }
        c.drawText(cName, w, h, mPaint);

        return bitmap;
    }

    public class AddMarker extends AsyncTask<Void, Void, Void> {

        private LatLng latLng;

        public AddMarker(LatLng latLng) {
            this.latLng = latLng;
        }

        @Override
        protected void onPreExecute() {

            try {
                removeAllMarkers();

                marker.position(latLng).title(display);
                marker.draggable(false);

                marker.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED));

                // marker.snippet("Testing snippet");

                // adding marker
                allMarkers.add(googleMap.addMarker(marker));

                slidingBackground.setVisibility(View.INVISIBLE);

                pbActionMode.setVisibility(View.VISIBLE);

                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);

                slidingLayout.setFloatingActionButtonVisibility(View.GONE);
            } catch (Exception ignored) {
            }

            super.onPreExecute();
        }

        String display = "Loading...";

        @Override
        protected Void doInBackground(Void... params) {

            GetLocationInfo gll = new GetLocationInfo();
            try {
                display = gll.getAddress(latLng);
            } catch (Exception e) {
                display = "Loading...";
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void mVoid) {

            pbActionMode.setVisibility(View.GONE);

            if (display.equalsIgnoreCase("Loading...")) {
                Toast.makeText(getBaseContext(),
                        "Not a valid location, try again", Toast.LENGTH_LONG)
                        .show();
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                return;

            }

            slidingLayout.setFloatingActionButtonVisibility(View.VISIBLE);
            slidingLayout.setFloatingActionButtonVisibility(View.VISIBLE);
            slidingBackground.setVisibility(View.VISIBLE);


//			mode = startSupportActionMode(new ActionModes(display, latLng.latitude
//					+ "", latLng.longitude + ""));
            Marker tempMarker = getLatestMarker();
            if (tempMarker != null)
                tempMarker.setTitle(display);

            super.onPostExecute(mVoid);
        }
    }

    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(Utils.dpToPx(180, context),
                Utils.dpToPx(100, context));
        view.layout(0, 0, Utils.dpToPx(180, context),
                Utils.dpToPx(100, context));
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }


    /**
     * function to load map. If map is not created it will create it for you
     */
    private void setUpMapIfNeeded() {
        if(loading)
            return;
        loading = true;

        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            // Check if we were successful in obtaining the map.
            if (mapFragment != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        loadMap(map);
                    }
                });
            }
        }
    }

    public void loadMap(GoogleMap map) {
        if (map != null) {
            googleMap = map;
            loadData();
            googleMap.setMyLocationEnabled(true);
            setUpListeners();
            loading = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    int currPos = 0;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.mapLeft:
                currPos = (currPos - 1) % markersExp.size();
                if (currPos == -1) {
                    currPos = markersExp.size() - 1;
                }
                moveToAndExpand(markersExp.get(currPos).getPosition(), currPos);
                break;
            case R.id.mapRight:
                currPos = (currPos + 1) % markersExp.size();
                moveToAndExpand(markersExp.get(currPos).getPosition(), currPos);
                break;
            case R.id.fabAddLocation1:
            case R.id.fabAddLocation2:
                Marker tempMarker = getLatestMarker();
                if (tempMarker != null)
                    new AddLoc(tempMarker.getPosition(), tempMarker.getTitle()).execute();
                break;
            case R.id.mapLocationList:
                showLocationDialog();
                break;
        }

    }

    public class AddLoc extends AsyncTask<Object, Void, String> {

        LatLng position;
        String title;
        AlertDialog mAddDialog;

        public AddLoc(LatLng position, String title) {
            this.position = position;
            this.title = title;
        }

        @Override
        protected void onPreExecute() {

            //FIXME setSupportProgressBarIndeterminateVisibility(true);
            mAddDialog = Utils.getProgressDialog(MapActivity.this, "Adding...");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Object... params) {
            String tz = "";
            GetLocationInfo gi = new GetLocationInfo();
            try {
                tz = gi.getTimezone(position.latitude, position.longitude)
                        .getDisplayName();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return tz;
        }

        @Override
        protected void onPostExecute(String tz) {
            //FIXME setSupportProgressBarIndeterminateVisibility(false);
            WLocation newLocation = new WLocation();
            newLocation.setIsMyLocation(mLoc == 1);
            newLocation.setLatitude(position.latitude);
            newLocation.setLongitude(position.longitude);
            newLocation.setName(title);
            newLocation.setTimezone(tz);
            newLocation.setUniqueID("9999"); //FIXME add the unique part
            newLocation.checkAndSave();

            mLocation.add(newLocation);

            //FIXME add the unique part
            WeatherItem mwi = new WeatherItem("9999", mLocation.get(
                    mLocation.size() - 1).getName(), temp[temp.length - 1],
                    conditions[conditions.length - 1], wi.size(),
                    conds[conds.length - 1], "C");
            wi.add(mwi);
            MarkerOptions mMarkerOptions = new MarkerOptions();
            mMarkerOptions.position(position);// .title(display);
            mMarkerOptions.draggable(false);
            mMarkerOptions.icon(BitmapDescriptorFactory
                    .fromBitmap(createMyDrawableFromBitmap(MapActivity.this,
                            mwi.getName(), mwi.getTemp() + "°", mwi.getUnit(),
                            mwi.getCondition(), mwi.getCondID())));

            markersExp.add(googleMap.addMarker(mMarkerOptions));
            markersExp.get(markersExp.size() - 1).setVisible(false);

            mMarkerOptions.icon(BitmapDescriptorFactory
                    .fromBitmap(smallBalloon(mwi.getCondID())));
            markersUnExp.add(googleMap.addMarker(mMarkerOptions));
            markersUnExp.get(markersUnExp.size() - 1).setVisible(true);

            moveToAndExpand(new LatLng(mLocation.get(mLocation.size() - 1).getLatitude(), mLocation.get(mLocation.size() - 1).getLongitude()),
                    mLocation.size() - 1);

            removeAllMarkers();

            mAddDialog.dismiss();

            super.onPostExecute(tz);
        }

    }

    ;

    private void startAndBuildDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        final View view = LayoutInflater.from(getBaseContext()).inflate(
                R.layout.layout_add_location_point, null);
        builder.setView(view);

        builder.setPositiveButton("LOCATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (view.findViewById(R.id.etLatitude) == null
                        || view.findViewById(R.id.etLongitude) == null) {
                    Toast.makeText(getBaseContext(),
                            "Invalid coordinates, Please try again!!",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                double lat = Double.parseDouble(((EditText) view
                        .findViewById(R.id.etLatitude)).getText().toString());
                double lon = Double.parseDouble(((EditText) view
                        .findViewById(R.id.etLongitude)).getText().toString());

                if ((lat + "").isEmpty() || (lon + "").isEmpty())
                    return;

                LatLng latlng = new LatLng(lat, lon);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latlng.latitude,
                                latlng.longitude)).zoom(zoom).build();

                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

                new AddMarker(latlng).execute();
            }
        });

        builder.setNegativeButton("CANCEL", null);

        final AlertDialog ad = builder.create();
        ad.getWindow().setWindowAnimations(R.style.DialogAnimation);
        ad.show();

    }

    public Bitmap smallBalloon(int mCond) {

        Bitmap bitmap = Bitmap.createBitmap(
                Utils.dpToPx(70, getBaseContext()),
                Utils.dpToPx(70, getBaseContext()),
                Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);
        Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG
                | Paint.ANTI_ALIAS_FLAG);

        NinePatchDrawable bg = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.balloon_overlay_black);
        if (bg != null) {
            bg.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            bg.setAlpha(150);
            bg.draw(c);
        }

        Bitmap cond = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), mCond),
                (int) (bitmap.getHeight() * 0.8),
                (int) (bitmap.getHeight() * 0.8), true);

        c.drawBitmap(cond,
                (float) (((bitmap.getWidth() - cond.getWidth()) * 0.5)),
                (float) (((bitmap.getHeight() - Utils.dpToPx(15,
                        getBaseContext())) * 0.5) - (cond.getHeight() * 0.5)),
                mPaint);
        return bitmap;

    }

    public void removeAllMarkers() {
        for (Marker mark : allMarkers)
            if (mark != null)
                mark.remove();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_actions, menu);

        if (googleMap != null)
            switch (googleMap.getMapType()) {
                case GoogleMap.MAP_TYPE_NORMAL:
                    menu.findItem(R.id.submenu_normal).setChecked(true);
                    break;
                case GoogleMap.MAP_TYPE_HYBRID:
                    menu.findItem(R.id.submenu_hybrid).setChecked(true);
                    break;
                case GoogleMap.MAP_TYPE_SATELLITE:
                    menu.findItem(R.id.submenu_satellite).setChecked(true);
                    break;
                case GoogleMap.MAP_TYPE_TERRAIN:
                    menu.findItem(R.id.submenu_terrain).setChecked(true);
                    break;
            }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (latLonBackground != null && latLonBackground.getVisibility() == View.VISIBLE) {
                    InitiateLatLon.handleToolBar(MapActivity.this, search, toolbar, etLat, etLon, latLonBackground, materialMenu);
                } else {
                    startActivity(new Intent(MapActivity.this, MainActivity.class));
                    finish();
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }
                break;
            case R.id.action_goto:
                latLonBackground.setVisibility(View.VISIBLE);
                InitiateLatLon.handleToolBar(MapActivity.this, search, toolbar, etLat, etLon, latLonBackground, materialMenu);
                return true;
            case R.id.submenu_hybrid:
                item.setChecked(true);
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.submenu_normal:
                item.setChecked(true);
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.submenu_satellite:
                item.setChecked(true);
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.submenu_terrain:
                item.setChecked(true);
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (latLonBackground != null && latLonBackground.getVisibility() == View.VISIBLE) {
            InitiateLatLon.handleToolBar(MapActivity.this, search, toolbar, etLat, etLon, latLonBackground, materialMenu);
        } else {
            startActivity(new Intent(MapActivity.this, MainActivity.class));
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    public void setMenuSelection(){
        Menu menu = toolbar.getMenu();
        if (googleMap != null)
            switch (googleMap.getMapType()) {
                case GoogleMap.MAP_TYPE_NORMAL:
                    menu.findItem(R.id.submenu_normal).setChecked(true);
                    break;
                case GoogleMap.MAP_TYPE_HYBRID:
                    menu.findItem(R.id.submenu_hybrid).setChecked(true);
                    break;
                case GoogleMap.MAP_TYPE_SATELLITE:
                    menu.findItem(R.id.submenu_satellite).setChecked(true);
                    break;
                case GoogleMap.MAP_TYPE_TERRAIN:
                    menu.findItem(R.id.submenu_terrain).setChecked(true);
                    break;
            }
    }

    AlertDialog mLocationDialog;

    public void showLocationDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapActivity.this);
        mBuilder.setTitle("Locations");
        View mView = LayoutInflater.from(MapActivity.this).inflate(R.layout.recycler, null);
        mBuilder.setView(mView);
        RecyclerView locationList = (RecyclerView) mView.findViewById(R.id.recyclerView);
        locationList.setLayoutManager(new LinearLayoutManager(MapActivity.this));
        locationList.setAdapter(new MapListAdapter(mLocation, MapActivity.this));
        mLocationDialog = mBuilder.show();
    }


    public void performDialogItemClick(int position) {
        currPos = position;
        moveToAndExpand(markersExp.get(currPos).getPosition(), currPos);
        mLocationDialog.dismiss();
    }

}
