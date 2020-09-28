package app.karacal.data;

import android.content.Context;
import android.location.Location;

import com.google.gson.Gson;

import app.karacal.helpers.PreferenceHelper;

public class LocationCache {

    private double longitude = 0;
    private double latitude = 0;
    private boolean hasPermission;

    private LocationCache(double longitude, double latitude, boolean hasPermission) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.hasPermission = hasPermission;
    }

    public static LocationCache getInstance(Context context){
        String str = PreferenceHelper.getLocationsCache(context);
        return new Gson().fromJson(str, LocationCache.class);
    }

    public static LocationCache getEmptyInstance(){
        return new LocationCache(0, 0, false);
    }

    public String retrieveStringFormat(){
        return new Gson().toJson(this);
    }

    public void setLocation(Context context, Location location){
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        hasPermission = true;
        saveChanges(context);
    }

    public void setHasPermission(boolean hasPermission) {
        this.hasPermission = hasPermission;
    }

    public Location getLocation(){
        if (latitude > 0.0001 && longitude > 0.0001 && hasPermission) {

            Location location = new Location("dummyProvider");
            location.setLatitude(latitude);
            location.setLongitude(longitude);

            return location;
        }

        return null;
    }

    private void saveChanges(Context context){
        PreferenceHelper.setLocationsCache(context, new Gson().toJson(this));
    }

    public void clear(Context context){
        PreferenceHelper.setLocationsCache(context, LocationCache.getEmptyInstance().retrieveStringFormat());
    }

}
