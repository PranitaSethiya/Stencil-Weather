package com.saphion.stencilweather.modules;

/**
 * Created by sachin on 9/7/15.
 */
public class WLocation {

    private String name;
    private String timezone;
    private long latitude;
    private long longitude;
    private boolean isMyLocation;

    public WLocation() {}


    public WLocation(String name, String timezone, long latitude, long longitude, boolean isMyLocation) {
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

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public boolean isMyLocation() {
        return isMyLocation;
    }

    public void setIsMyLocation(boolean isMyLocation) {
        this.isMyLocation = isMyLocation;
    }
}
