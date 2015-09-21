package com.saphion.stencilweather.utilities;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.saphion.stencilweather.activities.MainActivity;

/**
 * Created by sachin on 15/8/15.
 */
public class PreferenceUtil {

    public static void setTemperatureUnit(Context mContext, int value){
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putInt("temperature_unit", value).commit();
    }

    public static void setPressureUnit(Context mContext, int value){
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putInt("pressure_unit", value).commit();
    }

    public static void setTimeUnit(Context mContext, int value){
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putInt("time_unit", value).commit();
    }

    public static void setPrecipitationUnit(Context mContext, int value){
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putInt("precipitation_unit", value).commit();
    }

    public static void setWindUnit(Context mContext, int value){
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putInt("wind_unit", value).commit();
    }

    public static int getTemperatureUnit(Context mContext){
        return PreferenceManager.getDefaultSharedPreferences(mContext).getInt("temperature_unit", 0);
    }

    public static int getPressureUnit(Context mContext){
        return PreferenceManager.getDefaultSharedPreferences(mContext).getInt("pressure_unit", 0);
    }

    public static int getTimeUnit(Context mContext){
        return PreferenceManager.getDefaultSharedPreferences(mContext).getInt("time_unit", 0);
    }

    public static int getPrecipitationUnit(Context mContext){
        return PreferenceManager.getDefaultSharedPreferences(mContext).getInt("precipitation_unit", 0);
    }

    public static int getWindUnit(Context mContext){
        return PreferenceManager.getDefaultSharedPreferences(mContext).getInt("wind_unit", 0);
    }

    public static int getNextValue(int currentVal, String[] arr){
        return (currentVal + 1) % arr.length;
    }


    public static void setBackgroundType(Context mContext, int value){
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putInt("background_type", value).commit();
    }

    public static int getBackgroundType(Context mContext){
        return PreferenceManager.getDefaultSharedPreferences(mContext).getInt("background_type", 0);
    }

    public static void setBackgroundColor(Context mContext, int value){
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putInt("background_color", value).commit();
    }

    public static int getBackgroundColor(Context mContext){
        return PreferenceManager.getDefaultSharedPreferences(mContext).getInt("background_color", 0);
    }

    public static boolean getShowDialog(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("show_dialog", true);
    }

    public static void setShowDialog(Context mContext, boolean value) {
        saveBool(mContext, "show_dialog", value);
    }

    public static int getLanguage(Context mContext){
        return PreferenceManager.getDefaultSharedPreferences(mContext).getInt("app_language", 0);
    }

    public static void setLanguage(Context mContext, int value){
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putInt("app_language", value).commit();
    }

    public static void setRefreshOnLaunch(Context mContext, boolean value) {
        saveBool(mContext, "refresh_on_launch", value);
    }

    public static boolean getRefreshOnLaunch(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("refresh_on_launch", true);
    }


    /**
     * Self use functions
     */


    private static void saveBool(Context mContext, String key, Boolean value){
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean(key, value).commit();
        Log.d("Stencil", "saving " + key + " value: " + value);
    }
 //TODO may be later
//    private static boolean getBool(Context mContext)
}
