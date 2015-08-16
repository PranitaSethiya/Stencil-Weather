package com.saphion.stencilweather.modules;


import android.util.Log;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

/**
 * Created by sachin on 9/7/15.
 */
public class WLocation extends SugarRecord<WLocation> {

    private String name;
    private String timezone;
    private Double latitude;
    private Double longitude;
    private Boolean isMyLocation;
    private String uniqueID;

    public WLocation() {
    }


    public WLocation(String name, String timezone, Double latitude, Double longitude, Boolean isMyLocation, String uniqueID) {
        this.name = name;
        this.timezone = timezone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isMyLocation = isMyLocation;
        this.uniqueID = uniqueID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public boolean isMyLocation() {
        return isMyLocation;
    }

    public void setIsMyLocation(boolean isMyLocation) {
        this.isMyLocation = isMyLocation;
    }

    @Override
    public String toString() {
        return "[WLocation] " + "Name: " + name +
                ", timezone: " + timezone +
                ", latitude: " + latitude +
                ", longitude: " + longitude +
                ", isMyLocation: " + isMyLocation + ", UniqueID: " + uniqueID +
                (id != null ? ", id: " + id : "");
    }


    public long checkAndSave() {

        Log.e("Stencil", "Trying to save: " + this.toString());

        List<WLocation> duplicates = Select.from(WLocation.class)
                .where(Condition.prop("UNIQUE_ID").eq(this.uniqueID)).list();
        if (duplicates.size() == 0) {

            Log.e("Stencil", "Pass1");

            if (this.isMyLocation) {
                Log.e("Stencil", "Pass2");
                List<WLocation> myLocationDuplicate = Select.from(WLocation.class)
                        .where(Condition.prop("IS_MY_LOCATION").eq(1)).list();
                if (myLocationDuplicate.size() > 0) {
                    Log.e("Stencil", "Pass3");
                    WLocation newLocation = myLocationDuplicate.get(0);
                    newLocation.setLongitude(this.getLongitude());
                    newLocation.setLatitude(this.getLatitude());
                    newLocation.setUniqueID(this.getUniqueID());
                    newLocation.setTimezone(this.getTimezone());
                    newLocation.setName(this.getName());
                    newLocation.save();
                    return newLocation.getId();
                } else {
                    Log.e("Stencil", "Pass4");
                    this.save();
                }
            } else {
                Log.e("Stencil", "Pass5");
                this.save();
            }
            Log.e("Stencil", "Pass6");
            //Check if saved and return the saved ID
            List<WLocation> duplicates2 = Select.from(WLocation.class)
                    .where(Condition.prop("UNIQUE_ID").eq(this.uniqueID)).list();
            if (duplicates2.size() > 0) return duplicates2.get(0).getId();
            Log.e("Stencil", "Pass7");
            return -1;
        }

        Log.e("Stencil", "Pass8");

        WLocation duplicate = duplicates.get(0);
        if (!duplicate.isMyLocation() && this.isMyLocation()) {
            Log.e("Stencil", "Pass9");
            duplicate.delete();
            List<WLocation> myLocationDuplicate = Select.from(WLocation.class)
                    .where(Condition.prop("IS_MY_LOCATION").eq(1)).list();

            if (myLocationDuplicate.size() > 0) {
                Log.e("Stencil", "Pass10");
                WLocation newLocation = myLocationDuplicate.get(0);
                newLocation.setLongitude(this.getLongitude());
                newLocation.setLatitude(this.getLatitude());
                newLocation.setUniqueID(this.getUniqueID());
                newLocation.setTimezone(this.getTimezone());
                newLocation.setName(this.getName());
                newLocation.save();
                return newLocation.getId();
            }
            Log.e("Stencil", "Pass11");
            this.save();
            List<WLocation> duplicates2 = Select.from(WLocation.class)
                    .where(Condition.prop("UNIQUE_ID").eq(this.uniqueID)).list();
            if (duplicates2.size() > 0) return duplicates2.get(0).getId();
            Log.e("Stencil", "Pass12");
            return -1;
        }
        Log.e("Stencil", "Pass13");
        return -1;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }
}
