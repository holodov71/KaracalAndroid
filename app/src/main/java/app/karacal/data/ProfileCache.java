package app.karacal.data;

import android.content.Context;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import app.karacal.helpers.PreferenceHelper;
import app.karacal.models.Profile;

public class ProfileCache {

    private Profile profile;

    private String subscriptionId;

    private List<Integer> tourPurchases;

    private boolean isGuide;

    private Integer guideId;

    public ProfileCache(Profile profile, String subscriptionId, List<Integer> tourPurchases, boolean isGuide, Integer guideId) {
        this.profile = profile;
        this.subscriptionId = subscriptionId;
        this.tourPurchases = tourPurchases;
        this.isGuide = isGuide;
        this.guideId = guideId;
    }

    public static ProfileCache getInstance(Context context){
        return ProfileCache.getInstance(PreferenceHelper.getProfileCache(context));
    }

    private static ProfileCache getInstance(String cache){
        return new Gson().fromJson(cache, ProfileCache.class);
    }

    public static ProfileCache getEmptyInstance(){
        return new ProfileCache(null, null, new ArrayList<>(), false, null);
    }

    public String retrieveStringFormat(){
        return new Gson().toJson(this);
    }

    private void saveChanges(Context context){
        PreferenceHelper.setProfileCache(context, new Gson().toJson(this));
    }

    public void clear(Context context){
        PreferenceHelper.setProfileCache(context,
                ProfileCache.getEmptyInstance().retrieveStringFormat());
    }

    public void setProfile(Context context, Profile profile) {
        this.profile = profile;
        PreferenceHelper.saveToken(profile.getAuthKey());
        saveChanges(context);
    }

    public Profile getProfile() {
        return profile;
    }

    public void removeProfile(Context context){
        this.profile = null;
        saveChanges(context);
    }

    public void setSubscriptionId(Context context, String subscriptionId) {
        this.subscriptionId = subscriptionId;
        saveChanges(context);
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public boolean isHasSubscription() {
        return subscriptionId != null;
    }

    public void setTourPurchases(Context context, List<Integer> tourPurchases) {
        this.tourPurchases.clear();
        this.tourPurchases.addAll(tourPurchases);
        saveChanges(context);
    }

    public boolean isPurchasesContainsTour(int tourId){
        return tourPurchases.contains(tourId);
    }

    public void addTourPurchase(Context context, int tourId){
        tourPurchases.add(tourId);
        saveChanges(context);
    }

    public void setGuide(Context context, boolean guide, int guideId) {
        isGuide = guide;
        this.guideId = guideId;
        saveChanges(context);
    }

    public boolean isGuide() {
        return isGuide;
    }

    public Integer getGuideId() {
        return guideId;
    }
}
