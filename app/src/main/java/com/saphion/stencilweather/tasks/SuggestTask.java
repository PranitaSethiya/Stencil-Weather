package com.saphion.stencilweather.tasks;

import android.util.Log;

import com.saphion.stencilweather.activities.MainActivity;

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
	String[] key, GMT;

	public SuggestTask(MainActivity context, String original) {
		this.suggest = context;
		this.original = original;
	}

	public void run() {
		// Get suggestions for the original text
		List<String> suggestions = doSuggest(original);
		suggest.setSuggestions(suggestions, key, GMT);
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
	private List<String> doSuggest(String original) {
		List<String> messages = new LinkedList<String>();
		String error = null;
		int i;
		// HttpURLConnection con = null;
		Log.d(TAG, "doSuggest(" + original + ")");
		int status = 0;
		original = original.replace(" ", "+");
		//original.replaceAll(" ", "%20");
		Log.d(TAG, "doSuggest(" + original + ")");
		try {
			status = lastTweet("http://autocomplete.wunderground.com/aq?query="
					+ original.trim() + "&format=json");
			Log.d("status", status + "");
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (status == 200) {

			try {
				Log.d("Inside if", "if");
				Log.d("Code Length", Code.length + "");
				Log.d("Names Length", Names.length + "");
				key = new String[Code.length];
				GMT = new String[Code.length];
				for (i = 0; i < Code.length; i++) {

					key[i] = Code[i];
					Log.i("Id at Position " + i, key[i]);
					GMT[i] = gmt[i];
				}

				
				for (i = 0; i < Names.length; i++) {

					Log.i("values: ", Names[i]);
					messages.add(Names[i]);

				}
				// Check if task has been interrupted
				if (Thread.interrupted())
					throw new InterruptedException();

			} catch (InterruptedException e) {
				Log.d(TAG, "InterruptedException", e);
				error = "Loading...";
			} /*
			 * finally { if (con != null) { con.disconnect(); } }
			 */

			// If there was an error, return the error by itself
			if (error != null) {
				messages.clear();
				messages.add(error);
			}
			// Print something if we got nothing
			if (messages.size() == 0) {
				messages.add("No suggestions");
				key = new String[1];
				key[0] = "Try Again With Different City";
			}
		} else {
			error = "Unable To Connect to Internet, Please Check Your Network Settings...";
			messages.clear();
			messages.add(error);
			key = new String[1];
			key[0] = "Unable To Connect..";

		}
		// All done
		Log.d(TAG, "   -> returned " + messages);
		return messages;
	}

	HttpClient client;
	JSONObject json;
	String Names[], Code[], gmt[];

	public int lastTweet(String URL) throws ClientProtocolException,
			IOException, JSONException {
		StringBuilder url = new StringBuilder(URL);
		// url.append(username);

		client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url.toString());
		
		HttpResponse r = client.execute(get);
		int status = r.getStatusLine().getStatusCode();
		if (status == 200) {
			HttpEntity e = r.getEntity();
			String data = EntityUtils.toString(e);
			Log.d("json", data);
			JSONObject jObject = new JSONObject(data);
			JSONArray timeline = jObject.getJSONArray("RESULTS");
			Log.d("JSON ARRay", timeline + "");
			Names = new String[timeline.length()];
			Code = new String[timeline.length()];
			gmt = new String[timeline.length()];
			// new JSONArray(data);
			for (int i = 0; i < timeline.length(); i++) {
				JSONObject oneObject = timeline.getJSONObject(i);
				// Pulling items from the array
				Names[i] = oneObject.getString("name");
				Code[i] = oneObject.getString("zmw");
				gmt[i] = oneObject.getString("tz");
			}

		}
		return status;
	}

}
