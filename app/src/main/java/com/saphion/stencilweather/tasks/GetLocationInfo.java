package com.saphion.stencilweather.tasks;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.saphion.stencilweather.utilities.RestUtils;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.TimeZone;


public class GetLocationInfo {

    final static String URL1 = "http://maps.google.com/maps/api/geocode/json?address=";
    final static String URL2 = "&sensor=false";

    public static LatLng getll(String city) throws
            IOException, JSONException {

        city = city.trim().replaceAll(" ", "+");

        StringBuilder url = new StringBuilder((URL1 + city + URL2).trim());

        Log.d("Full lat lon URL", url.toString());


        String data = RestUtils.GET(url.toString());
        Log.d("json", data);
        JSONObject jObject = new JSONObject(data);
        Log.d("Length", jObject.length() + "");

        JSONObject jo = jObject.getJSONArray("results").getJSONObject(0);
        jo = jo.getJSONObject("geometry").getJSONObject("location");

        Log.d("Lat", jo.getString("lat"));
        Log.d("lon", jo.getString("lng"));

        return new LatLng(Double.parseDouble(jo.getString("lat")),
                Double.parseDouble(jo.getString("lng")));


    }

    public static String getAddress(LatLng latlon)
            throws IOException, JSONException {

        String city = latlon.latitude + "," + latlon.longitude;
        city = city.trim().replaceAll(" ", "+");

        StringBuilder url = new StringBuilder((URL1 + city + URL2).trim());

        Log.d("Full lat lon URL", url.toString());
        String data = RestUtils.GET(url.toString());
        Log.d("json", data);
        JSONObject jObject = new JSONObject(data);
        Log.d("Length", jObject.length() + "");
        JSONArray jArray = jObject.getJSONArray("results");
        if (jArray.length() <= 0)
            return null;
        JSONObject jo = jArray.getJSONObject(0);
        jArray = jo.getJSONArray("address_components");
        if (jArray.length() <= 0)
            return null;
        String toRet = "";
        String subl = "", local = "", country = "", admin1 = "", admin2 = "";
        for (int i = 0; i < jArray.length(); i++) {
            String check = jArray.getJSONObject(i).getJSONArray("types")
                    .getString(0);
            if (check.equalsIgnoreCase("sublocality")) {
                subl = jArray.getJSONObject(i).getString("long_name");

            }
            if (check.equalsIgnoreCase("locality")) {
                local = jArray.getJSONObject(i).getString("long_name");
            }

            if (check.equalsIgnoreCase("country")) {
                country = jArray.getJSONObject(i).getString("long_name");
            }

            if (check.equalsIgnoreCase("administrative_area_level_2")) {
                admin2 = jArray.getJSONObject(i).getString("long_name");
            }
            if (check.equalsIgnoreCase("administrative_area_level_1")) {
                admin1 = jArray.getJSONObject(i).getString("long_name");
            }
        }

        Log.d("sub", subl);
        Log.d("local", local);
        Log.d("country", country);
        Log.d("admin1", admin1);
        Log.d("admin2", admin2);

        if (!subl.isEmpty()) {
            toRet = subl;
        }

        if (!toRet.isEmpty() && !local.isEmpty()) {
            toRet = toRet + ", " + local;
        } else if (!local.isEmpty()) {

            toRet = local;
        }

        if (subl.isEmpty() && !admin2.isEmpty()) {
            if (!admin2.equalsIgnoreCase(local))
                if (toRet.isEmpty())
                    toRet = admin2;
                else
                    toRet = toRet + ", " + admin2;
        }

        if (local.isEmpty() && !admin1.isEmpty()) {
            if (!admin1.equalsIgnoreCase(admin2)
                    && !admin1.equalsIgnoreCase(subl))
                if (toRet.isEmpty())
                    toRet = admin1;
                else
                    toRet = toRet + ", " + admin1;
        }

        if (!toRet.isEmpty() && !country.isEmpty()) {
            if (!country.equalsIgnoreCase(admin2)
                    && !country.equalsIgnoreCase(subl)
                    && !country.equalsIgnoreCase(admin1)
                    && !country.equalsIgnoreCase(local))
                toRet = toRet + ", " + country;
        } else if (!country.isEmpty()) {
            toRet = country;
        }

        return toRet;


    }

    static String URL3 = "http://api.worldweatheronline.com/free/v1/tz.ashx?key=45kcy9gjhkcxdayzgta7nwvx&q=";
    static String URL4 = "&format=json";

    public static TimeZone getTimezone(double latitude, double longitude)
            throws IOException, JSONException {

        String latitudeStr = latitude + "";
        String longitudeStr = longitude + "";

        Log.d("Weather", "Lat " + latitudeStr + " & lon " + longitudeStr);
        if (latitudeStr.contains("E"))
            if (latitudeStr.contains(".")) {

                if (latitudeStr.indexOf(".") != 2 || latitudeStr.indexOf(".") != 3) {

                    latitudeStr = latitudeStr.substring(0, latitudeStr.indexOf("."))
                            + latitudeStr.substring(latitudeStr.indexOf(".") + 1);

                    latitudeStr = latitudeStr.substring(0, 2) + "." + latitudeStr.substring(2);
                    Log.d("Weather", latitudeStr);
                }
            } else {
                latitudeStr = latitudeStr.substring(0, 2) + "." + latitudeStr.substring(2);
            }
        if (longitudeStr.contains("E"))
            if (longitudeStr.contains(".")) {
                if (longitudeStr.indexOf(".") != 2 || longitudeStr.indexOf(".") != 3) {

                    longitudeStr = longitudeStr.substring(0, longitudeStr.indexOf("."))
                            + longitudeStr.substring(longitudeStr.indexOf(".") + 1);
                    ;

                    longitudeStr = longitudeStr.substring(0, 2) + "." + longitudeStr.substring(2);
                    Log.d("Weather", longitudeStr);
                }
            } else {
                longitudeStr = longitudeStr.substring(0, 2) + "." + longitudeStr.substring(2);
            }

        if (latitudeStr.contains("E")) {
            latitudeStr = latitudeStr.substring(0, latitudeStr.indexOf("E"))
                    + latitudeStr.substring(latitudeStr.indexOf("E") + 1);
        }
        if (longitudeStr.contains("E")) {
            longitudeStr = longitudeStr.substring(0, longitudeStr.indexOf("E"))
                    + longitudeStr.substring(longitudeStr.indexOf("E") + 1);
        }

        String url = URL3 + latitudeStr + "," + longitudeStr + URL4;

        Log.d("getTimezone", url);


        String data = RestUtils.GET(url);
        Log.d("Stencil", data);
        JSONObject jObject = new JSONObject(data);
        // Log.d("Length", jObject.length() + "");

        JSONObject jo = jObject.getJSONObject("data");
        JSONObject jobj = jo.getJSONArray("time_zone").getJSONObject(0);
        String mytzone = jobj.getString("utcOffset");
        String hour = "", minutes = "";
        String sign = "+";
        if (mytzone.contains(".")) {
            if (mytzone.contains("-")) {
                sign = "-";
                hour = (mytzone.substring(mytzone.indexOf("-"),
                        mytzone.indexOf(".")));
            } else {
                hour = mytzone.substring(0, mytzone.indexOf("."));
                sign = "+";
            }
            minutes = mytzone.substring(mytzone.indexOf(".") + 1);
        }
        if ((hour).length() == 1) {
            hour = "0" + hour;
        }
        if (Integer.parseInt(minutes) == 50) {
            minutes = "30";
        } else {
            minutes = "00";
        }

        return TimeZone.getTimeZone("GMT" + sign + hour + minutes);


    }
}
