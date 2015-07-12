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

    public WLocation() {
    }


    public WLocation(String name, String timezone, Double latitude, Double longitude, Boolean isMyLocation) {
        this.name = name;
        this.timezone = timezone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isMyLocation = isMyLocation;
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
        return longitude;
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
        return "[WLocation] " + "Name: "  + name +
        ", timezone: " +  timezone +
        ", latitude: " + latitude +
        ", longitude: " + longitude +
        ",isMyLocation: " + isMyLocation;
    }


    public boolean checkAndSave() {
        List<WLocation> duplicates = Select.from(WLocation.class)
                .where(Condition.prop("NAME").eq(this.name),
                        Condition.prop("LATITUDE").eq(this.latitude),
                        Condition.prop("LONGITUDE").eq(this.longitude)).list();
        Log.d("Stencil", "duplicates: " + duplicates.size() + "");
        if(duplicates.size() == 0) {
            this.save();
            return true;
        }
        return false;
    }
}
