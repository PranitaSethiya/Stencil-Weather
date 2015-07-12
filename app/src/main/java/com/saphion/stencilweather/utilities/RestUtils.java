package com.saphion.stencilweather.utilities;

import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
 
public class RestUtils {
 
    public static String GET(String urlStr) {
        InputStream inputStream = null;
        Log.d("Network", "Requesting: " + urlStr);
        String result = "";
        try {
 
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection =
                    (HttpURLConnection) url.openConnection();
 
            urlConnection.setUseCaches(false);
 
            urlConnection.setRequestMethod("GET");
 
            inputStream = urlConnection.getInputStream();
 
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        Log.d("JSON request", result);
        return result;
    }
    
    
    public static String POST(String urlStr, JSONObject data) {
        InputStream inputStream = null;
        Log.d("Network", "Requesting: " + urlStr);
        Log.d("Network", "Request data: " + data);
 
        String result = "";
        try {
 
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection =
                    (HttpURLConnection) url.openConnection();
 
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
 
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
 
            OutputStream out = urlConnection.getOutputStream();
            byte[] postData = data.toString().getBytes();
            out.write(postData);
            out.close();
 
            try {
                inputStream = urlConnection.getInputStream();
            }catch (Exception ex){
            }
 
            Log.d("Network", "Status " + urlConnection.getResponseCode());
 
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
 
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        Log.d("JSON request", result);
        return result;
    }
 
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
 
    }
}