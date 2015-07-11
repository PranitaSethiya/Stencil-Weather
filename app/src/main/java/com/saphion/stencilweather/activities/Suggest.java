package com.saphion.stencilweather.activities;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.saphion.stencilweather.R;
import com.saphion.stencilweather.db.DatabaseCity;
import com.saphion.stencilweather.tasks.GetLocationInfo;
import com.saphion.stencilweather.utilities.Utils;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

public class Suggest extends AppCompatActivity {
//	private EditText origText;
//	private ListView suggList;
//	private Handler guiThread;
//	private ExecutorService suggThread;
//	private Runnable updateTask;
//	private Future<?> suggPending;
//	private List<String> items;
//	String keys[];
//	ProgressBar pb;
//	private String[] GMT;
//	boolean go = true;
//	public MyAdapter adapter;
//
//	@SuppressLint("NewApi")
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//
////		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
////		getSupportActionBar().setBackgroundDrawable(
////				getResources().getDrawable(R.drawable.actionbar_back));
//
////		setSupportProgressBarIndeterminateVisibility(false);
//		super.onCreate(savedInstanceState);
//
//		// ////////////////
//		super.setContentView(R.layout.layout_add_location);
//
//
//
//		// ThemeManager.setDefaultTheme(ThemeManager.LIGHT);
//		getSupportActionBar().setTitle("Add City");
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//		ViewGroup parent = (ViewGroup) findViewById(R.id.container);
//		findViews();
//		if (isNetworkAvailable()) {
//
//			Log.d("Saphion: ", "Network Available");
//			go = true;
//			pb = ((ProgressBar) findViewById(R.id.pbsuggest));
//			initThreading();
//
//			setListeners();
//			setAdapters();
//		} else {
//			Log.d("Saphion: ", "NetWork Unavailable");
//			Snackbar.make(parent, "No internet connection", Snackbar.LENGTH_LONG).show();
//			go = false;
//		}
//
//	}
//
//	boolean isNetworkAvailable() {
//
//		boolean isWifi = false;
//		boolean isMobile = false;
//		ConnectivityManager connMgr = (ConnectivityManager) getBaseContext()
//				.getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo ni = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//		if (ni != null)
//			isWifi = ni.isConnected();
//		ni = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//		if (ni != null)
//			isMobile = ni.isConnected();
//		return isWifi || isMobile;
//	}
//
//	@Override
//	protected void onDestroy() {
//		// Terminate extra threads here
//		try {
//			if (go)
//				suggThread.shutdownNow();
//		} catch (NullPointerException e) {
//			Log.d("Suggest", "1st Exception");
//		}
//		super.onDestroy();
//	}
//
//	/** Get a handle to all user interface elements */
//	private void findViews() {
//		origText = (EditText) findViewById(R.id.original_text);
//		suggList = (ListView) findViewById(R.id.result_list);
//		((ImageButton) findViewById(R.id.ibLocateMe))
//				.setOnClickListener(new View.OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						LocationManager lm = null;
//						boolean gps_enabled = false, network_enabled = false;
//						if (lm == null)
//							lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//						try {
//							gps_enabled = lm
//									.isProviderEnabled(LocationManager.GPS_PROVIDER);
//						} catch (Exception ex) {
//						}
//						try {
//							network_enabled = lm
//									.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//						} catch (Exception ex) {
//						}
//
//						if (!gps_enabled && !network_enabled) {
//
//							showLocationDialog();
//
//						} else {
////							startActivity(new Intent(Suggest.this,
////									LocRetrieveClass.class));
//						}
//					}
//				});
//
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//			case android.R.id.home:
//				return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//	public class MyAdapter extends ArrayAdapter<String> {
////		private Typeface font;
//
//		public MyAdapter(Context context, int textViewResourceId,
//				List<String> objects) {
//			super(context, textViewResourceId, objects);
//		}
//
//		@Override
//		public View getView(int position, View view, ViewGroup viewGroup) {
//			View v = super.getView(position, view, viewGroup);
////			((TextView) v).setTypeface(font);
//			return v;
//		}
//	}
//
//	public void showLocationDialog() {
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle("Alert");
//		builder.setIcon(R.drawable.ic_map_marker_radius);
//		builder.setMessage("Location Services are not enabled, Enable them now?");
//		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//
//			public void onClick(DialogInterface dialog, int which) {
//				startActivity(new Intent(
//						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//
//			}
//		});
//		builder.setNegativeButton("Cancel", null);
//
//		builder.show();
//	}
//
//	@Override
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
////		SharedPreferences getPrefs = PreferenceManager
////				.getDefaultSharedPreferences(getBaseContext());
////		// getBaseContext().registerReceiver(mConnReceiver, new
////		// IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
////		if (getPrefs.getBoolean("added", false)) {
////			getPrefs.edit().putBoolean("added", false).commit();
////			finish();
////			startActivity(new Intent(getBaseContext(), AllCities.class));
////			overridePendingTransition(R.anim.slide_in_right,
////					R.anim.slide_out_left);
////		}
//	}
//
//	@Override
//	protected void onPause() {
//		// getBaseContext().unregisterReceiver(mConnReceiver);
//		super.onPause();
//	}
//
//	/** Setup user interface event handlers */
//	private void setListeners() {
//		// Define listener for text change
//		TextWatcher textWatcher = new TextWatcher() {
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//				/* Do nothing */
//			}
//
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//
//				pb.setVisibility(View.VISIBLE);
//				adapter.clear();
//				queueUpdate(1000 /* milliseconds */);
//			}
//
//			public void afterTextChanged(Editable s) {
//				/* Do nothing */
//			}
//		};
//
//		// Set listener on the original text field
//		origText.addTextChangedListener(textWatcher);
//
//		// Define listener for clicking on an item
//		AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// String query = (String) parent.getItemAtPosition(position);
//				// doSearch(query);
//				if (!(parent.getItemAtPosition(position).toString()
//						.equalsIgnoreCase("Loading...")
//						|| parent.getItemAtPosition(position).toString()
//								.equalsIgnoreCase("No suggestions") || parent
//						.getItemAtPosition(position)
//						.toString()
//						.equalsIgnoreCase(
//								"Unable To Connect to Internet, Please Check Your Network Settings."))) {
//
//					String name = parent.getItemAtPosition(position).toString();
//					// Toast.makeText(Suggest.this, "You selected: " + name,
//					// Toast.LENGTH_SHORT).show();
//
////					new GetLL(getBaseContext(), name, GMT[position]).execute();
//					// new myasync().execute(name);
//
//				}
//
//			}
//		};
//
//		// Set listener on the suggestion list
//		suggList.setOnItemClickListener(clickListener);
//	}
//
////	public class myasync extends AsyncTask<String, Void, Boolean> {
////
////		@Override
////		protected Boolean doInBackground(String... params) {
////			try {
////				new AsyncClass().lastTweet(getBaseContext(), params[0]);
////			} catch (ClientProtocolException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			} catch (IOException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			} catch (JSONException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
////			return null;
////		}
////
////	}
//
//	public class GetLL extends AsyncTask<Object, Integer, Boolean> {
//
//		String name;
//		String gmt;
//		Context mContext;
//		LatLng ll;
//
//		@Override
//		protected void onPreExecute() {
//			Utils.hideKeyboard(origText, getBaseContext());
//			Toast.makeText(getBaseContext(), "Please wait, Adding Location.",
//					Toast.LENGTH_LONG).show();
//			super.onPreExecute();
//		}
//
//		public GetLL(Context mContext, String name, String gmt) {
//			this.name = name;
//			this.gmt = gmt;
//			this.mContext = mContext;
//		}
//
//		@Override
//		protected Boolean doInBackground(Object... arg) {
//
//			GetLocationInfo gl = new GetLocationInfo();
//			try {
//				ll = gl.getll(name);
//			} catch (ClientProtocolException e) {
//				Log.d("Suggest", "2nd Exception");
//
//			} catch (IOException e) {
//				Log.d("Suggest", "3rd Exception");
//			} catch (JSONException e) {
//				Log.d("Suggest", "4th Exception");
//
//			}
//
//			if (gmt.equalsIgnoreCase("MISSING")) {
//				try {
//					gmt = gl.getTimezone(ll.latitude, ll.longitude)
//							.getDisplayName();
//				} catch (ClientProtocolException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Boolean result) {
//
//			DatabaseCity entry = new DatabaseCity(mContext);
//			entry.open();
//			entry.createorUpdateEntry(name, 0, gmt, ll);
//			entry.close();
//			finish();
//			startActivity(new Intent(getBaseContext(), MainActivity.class));
//			overridePendingTransition(R.anim.slide_in_right,
//					R.anim.slide_out_left);
//			super.onPostExecute(result);
//		}
//
//	}
//
//	/** Set up adapter for list view. */
//	private void setAdapters() {
//		items = new ArrayList<String>();
//		adapter = new MyAdapter(this, android.R.layout.simple_list_item_1,
//				items);
//
//		suggList.setAdapter(adapter);
//
//	}
//
//	public void back() {
//		try {
//			Utils.hideKeyboard(origText, Suggest.this);
//		} catch (Exception ex) {
//		}
//		DatabaseCity db = new DatabaseCity(getBaseContext());
//		db.open();
//		if (db.getSize() > 0) {
//			startActivity(new Intent(getBaseContext(), MainActivity.class));
//			db.close();
//			finish();
//			overridePendingTransition(R.anim.slide_in_right,
//					R.anim.slide_out_left);
//		} else {
//			db.close();
//			finish();
//			overridePendingTransition(R.anim.slide_in_right,
//					R.anim.slide_out_left);
//
//		}
//	}
//
//	@Override
//	public void onBackPressed() {
//		back();
//		super.onBackPressed();
//	}
//
//	/**
//	 * Initialize multi-threading. There are two threads: 1) The main graphical
//	 * user interface thread already started by Android, and 2) The suggest
//	 * thread, which we start using an executor.
//	 */
//	private void initThreading() {
//		guiThread = new Handler();
//		suggThread = Executors.newSingleThreadExecutor();
//
//		// This task gets suggestions and updates the screen
//		updateTask = new Runnable() {
//			public void run() {
//				// Get text to suggest
//				String original = origText.getText().toString().trim();
//
//				// Cancel previous suggestion if there was one
//				if (suggPending != null)
//					suggPending.cancel(true);
//
//				// Check to make sure there is text to work on
//				if (original.length() != 0) {
//					// Let user know we're doing something
//					// setText(R.string.working);
//					keys = new String[1];
//					keys[0] = "Working...";
//
//					// Begin suggestion now but don't wait for it
//					try {
//						SuggestTask suggestTask = new SuggestTask(Suggest.this, // reference
//																				// to
//																				// activity
//								original // original text
//						);
//						suggPending = suggThread.submit(suggestTask);
//					} catch (RejectedExecutionException e) {
//						Log.d("Suggest", "5th Exception");
//						// Unable to start new task
//						setText(R.string.error);
//					}
//				} else {
//					pb.setVisibility(View.GONE);
//				}
//			}
//		};
//	}
//
//	/** Request an update to start after a short delay */
//	private void queueUpdate(long delayMillis) {
//		// Cancel previous update if it hasn't started yet
//		guiThread.removeCallbacks(updateTask);
//		// Start an update if nothing happens after a few milliseconds
//		guiThread.postDelayed(updateTask, delayMillis);
//	}
//
//	/** Display a message */
//	private void setText(int id) {
//		adapter.clear();
//		adapter.add(getResources().getString(id));
//	}
//
//	/** Display a list */
//	private void setList(List<String> list) {
//		adapter.clear();
//		// adapter.addAll(list); // Could use if API >= 11
//		for (String item : list) {
//			adapter.add(item);
//		}
//	}
//
//	/**
//	 * Modify list on the screen (called from another thread)
//	 */
//	public void setSuggestions(List<String> suggestions, String[] key,
//			String[] gmt) {
//
//		guiSetList(suggList, suggestions);
//		Log.i("Inside setSuggestions", "Inside");
//		keys = new String[key.length];
//		keys = key.clone();
//		GMT = new String[gmt.length];
//		GMT = gmt.clone();
//
//	}
//
//	/** All changes to the GUI must be done in the GUI thread */
//	private void guiSetList(final ListView view, final List<String> list) {
//
//		guiThread.post(new Runnable() {
//			public void run() {
//				setList(list);
//				pb.setVisibility(View.GONE);
//			}
//
//		});
//
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuItem map = menu.add("Add using map");
//		map.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
////		map.setIcon(R.drawable.loc);
////		map.setOnMenuItemClickListener(new OnMenuItemClickListener() {
////
////			@Override
////			public boolean onMenuItemClick(MenuItem item) {
////				finish();
////				startActivity(new Intent(getBaseContext(), MapActivity.class));
////				overridePendingTransition(R.anim.slide_in_right,
////						R.anim.slide_out_left);
////				return true;
////			}
////		});
////
////		MenuItem ll = menu.add("Add using Latitude and Longitude");
////		ll.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
////		ll.setIcon(R.drawable.goto_icon);
////		ll.setOnMenuItemClickListener(new OnMenuItemClickListener() {
////
////			@Override
////			public boolean onMenuItemClick(MenuItem item) {
////				startAndBuildDialog();
////				return true;
////			}
////		});
//		return super.onCreateOptionsMenu(menu);
//	}
//
//	AlertDialog ad;
//	AlertDialog.Builder builder;
//
//	private void startAndBuildDialog() {
//
//        builder = new AlertDialog.Builder(Suggest.this);
//		final View view = LayoutInflater.from(getBaseContext()).inflate(
//				R.layout.latlon_dialog, null);
//		final View view2 = Utils.getProgressView(Suggest.this, "Adding...");
//		builder.setView(view);
//
//        builder.setPositiveButton("LOCATE", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                if (((EditText) view.findViewById(R.id.tvlat))
//                        .getText().toString().length() <= 0
//                        || ((EditText) view.findViewById(R.id.tvlon))
//                        .getText().toString().length() <= 0) {
//                    Toast.makeText(getBaseContext(),
//                            "Invalid coordinates, Please try again!!",
//                            Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                Utils.hideKeyboard(
//                        ((EditText) view.findViewById(R.id.tvlat)),
//                        getBaseContext());
//                Utils.hideKeyboard(
//                        ((EditText) view.findViewById(R.id.tvlon)),
//                        getBaseContext());
//
//                double lat = Double.parseDouble(((EditText) view
//                        .findViewById(R.id.tvlat)).getText().toString());
//                double lon = Double.parseDouble(((EditText) view
//                        .findViewById(R.id.tvlon)).getText().toString());
//
//                if ((lat + "").isEmpty() || (lon + "").isEmpty()) {
//                    Toast.makeText(getBaseContext(),
//                            "Invalid coordinates, Please try again!!",
//                            Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                LatLng latlng = new LatLng(lat, lon);
//
//                builder = new AlertDialog.Builder(Suggest.this);
//                builder.setView(view2);
//                ad = builder.create();
//                ad.show();
//
//                new FindAddress(view2, latlng).execute(latlng);
//            }
//        });
//
//		builder.setNegativeButton("CANCEL", null);
//
//		builder.show();
//
//	}
//
//	private void setNewListeners(final View view, final LatLng latlng,
//			final String display) {
//
//		view.findViewById(R.id.mylocbadd).setOnClickListener(
//				new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//
//						AsyncTask<Object, Void, String> mAysnc = new AsyncTask<Object, Void, String>() {
//
//							@Override
//							protected void onPreExecute() {
//								Toast.makeText(getBaseContext(),
//										"Please wait, adding location.",
//										Toast.LENGTH_LONG).show();
//								view.findViewById(R.id.mylocProgressBar)
//										.setVisibility(View.VISIBLE);
//								super.onPreExecute();
//							}
//
//							@Override
//							protected String doInBackground(Object... params) {
//								String tz = "";
//								GetInfo gi = new GetInfo();
//								try {
//									tz = gi.getTimezone(latlng.latitude,
//											latlng.longitude).getDisplayName();
//								} catch (ClientProtocolException e) {
//									e.printStackTrace();
//								} catch (IOException e) {
//									e.printStackTrace();
//								} catch (JSONException e) {
//									e.printStackTrace();
//								}
//								return tz;
//							}
//
//							@Override
//							protected void onPostExecute(String tz) {
//								DatabaseCity db = new DatabaseCity(Suggest.this);
//								db.open();
//								db.createorUpdateEntry(display, 0, tz, latlng);
//								db.close();
//								view.findViewById(R.id.mylocProgressBar)
//										.setVisibility(View.GONE);
//								try {
//									ad.cancel();
//								} catch (Exception ex) {
//								}
//								finish();
//								startActivity(new Intent(getBaseContext(),
//										AllCities.class));
//								overridePendingTransition(
//										R.anim.slide_in_right,
//										R.anim.slide_out_left);
//
//								super.onPostExecute(tz);
//							}
//
//						};
//
//						mAysnc.execute();
//
//					}
//				});
//		view.findViewById(R.id.mylocbcancel).setOnClickListener(
//				new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						try {
//							ad.cancel();
//						} catch (Exception ex) {
//						}
//					}
//				});
//
//	}
//
//	public class FindAddress extends AsyncTask<LatLng, Void, LatLng> {
//
//		View v;
//		LatLng llng;
//
//		public FindAddress(View v, LatLng param) {
//			this.v = v;
//			this.llng = param;
//		}
//
//		@Override
//		protected void onPreExecute() {
//
//			v.findViewById(R.id.mylocProgressBar).setVisibility(View.VISIBLE);
//			v.findViewById(R.id.myLocTV).setVisibility(View.GONE);
//			v.findViewById(R.id.mylocbadd).setVisibility(View.GONE);
//			v.findViewById(R.id.mylocbcancel).setVisibility(View.GONE);
//			v.findViewById(R.id.myLoclatlon).setVisibility(View.VISIBLE);
//			((TextView) v.findViewById(R.id.myLoclatlon)).setText("Latitude: "
//					+ llng.latitude + ", Longitude: " + llng.longitude);
//
//			super.onPreExecute();
//		}
//
//		String display = "empty";
//
//		@Override
//		protected LatLng doInBackground(LatLng... params) {
//
//			GetInfo gll = new GetInfo();
//			try {
//				display = gll.getAddress(Suggest.this, params[0]);
//			} catch (ClientProtocolException e) {
//				display = "empty";
//				e.printStackTrace();
//			} catch (IOException e) {
//				display = "empty";
//				e.printStackTrace();
//			} catch (JSONException e) {
//				display = "empty";
//				e.printStackTrace();
//			}
//
//			return params[0];
//		}
//
//		@Override
//		protected void onPostExecute(LatLng latlng) {
//
//			if (display.equalsIgnoreCase("empty")) {
//				Toast.makeText(getBaseContext(),
//						"Not a valid location, try again", Toast.LENGTH_LONG)
//						.show();
//				try {
//					ad.cancel();
//				} catch (Exception ex) {
//				}
//				return;
//
//			}
//
//			setNewListeners(v, latlng, display);
//
//			v.findViewById(R.id.mylocProgressBar).setVisibility(View.GONE);
//			v.findViewById(R.id.myLocTV).setVisibility(View.VISIBLE);
//
//			((TextView) v.findViewById(R.id.myLocTV)).setText(display);
//			((TextView) v.findViewById(R.id.myLoclatlon)).setText("Latitude: "
//					+ latlng.latitude + ", Longitude: " + latlng.longitude);
//			v.findViewById(R.id.mylocbadd).setVisibility(View.VISIBLE);
//			v.findViewById(R.id.mylocbcancel).setVisibility(View.VISIBLE);
//
//			super.onPostExecute(latlng);
//		}
//	}

}
