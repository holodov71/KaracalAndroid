package app.karacal.helpers;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.karacal.models.Profile;

@Singleton
public class ProfileHolder {

    private TokenHelper tokenHelper;

    private Profile profile;

//    private String subscriptionId = null;

    private List<Integer> tourPurchases = new ArrayList<>();

//    private boolean isGuide = false;
//
//    private Integer guideId = null;

    @Inject
    public ProfileHolder(TokenHelper tokenHelper){
        this.tokenHelper = tokenHelper;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
        tokenHelper.updateToken(profile.getAuthKey());
    }

    public void removeProfile(Context context){
        this.profile = null;
//        tokenHelper.deleteToken(context);
    }

//    public boolean isHasSubscription() {
//        return subscriptionId != null;
//    }
//
//    public void setSubscription(String subscriptionId) {
//        this.subscriptionId = subscriptionId;
//    }
//
//    public String getSubscriptionId() {
//        return subscriptionId;
//    }

//    public List<Integer> getTourPurchases() {
//        return tourPurchases;
//    }

//    public void setTourPurchases(List<Integer> tourPurchases) {
//        this.tourPurchases.clear();
//        this.tourPurchases.addAll(tourPurchases);
//    }
//
//    public boolean isPurchasesContainsTour(int tourId){
//        return tourPurchases.contains(tourId);
//    }
//
//    public void addTourPurchase(int tourId){
//        tourPurchases.add(tourId);
//    }

//    public boolean isGuide() {
//        return isGuide;
//    }
//
//    public void setGuide(boolean guide, int guideId) {
//        isGuide = guide;
//        this.guideId = guideId;
//    }
//
//    public Integer getGuideId (){
//        return guideId;
//    }


}
