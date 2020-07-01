package app.karacal.data;

import android.content.Context;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.karacal.helpers.PreferenceHelper;
import app.karacal.models.Tour;

public class DownloadedToursCache {

    private HashMap<Integer, Tour> tourHashMap;

    private DownloadedToursCache(HashMap<Integer, Tour> tourHashMap) {
        this.tourHashMap = tourHashMap;
    }

    private DownloadedToursCache(String toursCache) {
        this(new Gson().fromJson(toursCache, DownloadedToursCache.class).getTourCache());
    }

    public static DownloadedToursCache getInstance(Context context){
        return new DownloadedToursCache(PreferenceHelper.getDownloadedToursCache(context));
    }

    public static DownloadedToursCache getEmptyInstance(){
        return new DownloadedToursCache(new HashMap<>());
    }

    public String retrieveStringFormat(){
        return new Gson().toJson(this);
    }

    public HashMap<Integer, Tour> getTourCache() {
        return tourHashMap;
    }

    public void addDownloadedTour(Context context, Tour tour){
        tourHashMap.put(tour.getId(), tour);
        saveChanges(context);
    }

    public boolean containsTour(int tourId){
        return tourHashMap.containsKey(tourId);
    }

    public Tour getDownloadedTour(int tourId){
        return tourHashMap.get(tourId);
    }

    public void deleteDownloadedTour(Context context, int tourId){
        tourHashMap.remove(tourId);
        saveChanges(context);
    }

    private void saveChanges(Context context){
        PreferenceHelper.setDownloadedToursCache(context, new Gson().toJson(this));
    }

    public void clear(Context context){
        PreferenceHelper.setDownloadedToursCache(context,
                DownloadedToursCache.getEmptyInstance().retrieveStringFormat());
    }

    public ArrayList<Tour> getToursList() {
        return new ArrayList<>(tourHashMap.values());
    }
}
