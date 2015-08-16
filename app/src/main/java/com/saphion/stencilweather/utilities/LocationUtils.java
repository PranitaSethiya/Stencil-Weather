package com.saphion.stencilweather.utilities;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.saphion.stencilweather.modules.WLocation;
import com.saphion.stencilweather.tasks.GetLocationInfo;
import com.saphion.stencilweather.tasks.SuggestTask;

import java.util.List;

/**
 * Created by sachin on 15/8/15.
 */
public class LocationUtils {
    public static WLocation getLocationFromLatLng(double latitude, double longitude) {
        try {
            String address = GetLocationInfo.getAddress(new LatLng(latitude, longitude));

            List<WLocation> locationList = SuggestTask.doSuggest(address);

            if (locationList.size() > 0) {
                for (int i = 0; i < locationList.size(); i++) {
                    WLocation location = locationList.get(i);
                    if ((int) latitude == (int) location.getLatitude() &&
                            (int) longitude == (int) location.getLongitude()) {
                        location.setLatitude(latitude);
                        location.setLongitude(longitude);
                        return location;
                    }
                }
                WLocation location = locationList.get(0);
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                return location;
            } else {
                if (address != null)
                    if (address.contains(",")) {
                        String newAddress = address.split(",")[0];
                        Log.d("Stencil", "newAddress: " + newAddress);
                        List<WLocation> newLocationList = SuggestTask.doSuggest(newAddress);

                        if (newLocationList.size() > 0) {
                            for (int i = 0; i < newLocationList.size(); i++) {
                                WLocation location = newLocationList.get(i);
                                if ((int) latitude == (int) location.getLatitude() &&
                                        (int) longitude == (int) location.getLongitude()) {
                                    location.setLatitude(latitude);
                                    location.setLongitude(longitude);
                                    return location;
                                }
                            }
                            WLocation location = newLocationList.get(0);
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);
                            return location;
                        } else {
                            if (address.contains(",")) {
                                newAddress = address.split(",")[1];
                                Log.d("Stencil", "newAddress: " + newAddress);
                                newLocationList = SuggestTask.doSuggest(newAddress);

                                if (newLocationList.size() > 0) {
                                    for (int i = 0; i < newLocationList.size(); i++) {
                                        WLocation location = newLocationList.get(i);
                                        if ((int) latitude == (int) location.getLatitude() &&
                                                (int) longitude == (int) location.getLongitude()) {
                                            location.setLatitude(latitude);
                                            location.setLongitude(longitude);
                                            return location;
                                        }
                                    }
                                    WLocation location = newLocationList.get(0);
                                    location.setLatitude(latitude);
                                    location.setLongitude(longitude);
                                    return location;
                                }
                            }
                        }
                    }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
