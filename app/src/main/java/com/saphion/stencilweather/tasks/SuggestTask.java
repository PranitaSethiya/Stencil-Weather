package com.saphion.stencilweather.tasks;

import android.location.Location;
import android.util.Log;

import com.saphion.stencilweather.activities.MainActivity;
import com.saphion.stencilweather.modules.WLocation;
import com.saphion.stencilweather.utilities.RestUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SuggestTask implements Runnable {
	private static final String TAG = "SuggestTask";
	private final MainActivity suggest;
	private final String original;

	public SuggestTask(MainActivity context, String original) {
		this.suggest = context;
		this.original = original;
	}

	public void run() {
		// Get suggestions for the original text
		suggest.setSuggestions(doSuggest(original));
	}

	/**
	 * Call the Google Suggest API to create a list of suggestions from a
	 * partial string.
	 * 
	 * Note: This isn't really a supported API so if it breaks, try the Yahoo
	 * one instead:
	 * 
	 * http://ff.search.yahoo.com/gossip?output=xml&command=WORD or
	 * http://ff.search.yahoo.com/gossip?output=fxjson&command=WORD
	 * 
	 * @param original
	 */
	public static List<WLocation> doSuggest(String original) {
		List<WLocation> locations = new LinkedList<WLocation>();

		original = original.trim();
		original = original.replace(" ", "+");
		Log.d(TAG, "doSuggest(" + original + ")");
		String URL = ("http://autocomplete.wunderground.com/aq?query="
					+ original.trim() + "&format=json");

		JSONObject jObject;
		try {
			jObject = new JSONObject(RestUtils.GET(URL));
			JSONArray results = jObject.getJSONArray("RESULTS");
			Log.d("JSON Array", results + "");
			for (int i = 0; i < results.length(); i++) {
				JSONObject oneObject = results.getJSONObject(i);
				// Pulling items from the array
				WLocation location = new WLocation();
				location.setIsMyLocation(false);
				location.setName(oneObject.getString("name"));
				location.setLatitude(oneObject.getDouble("lat"));
				location.setLongitude(oneObject.getDouble("lon"));
				location.setTimezone(oneObject.getString("tz"));
				location.setUniqueID(oneObject.getString("zmw"));
				locations.add(location);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return locations;
	}

}
