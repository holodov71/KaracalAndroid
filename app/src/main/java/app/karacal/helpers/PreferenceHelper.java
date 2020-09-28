package app.karacal.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.karacal.App;
import app.karacal.data.DownloadedToursCache;
import app.karacal.data.LocationCache;
import app.karacal.data.NotificationsCache;
import app.karacal.data.NotificationsSchedule;
import app.karacal.data.ProfileCache;
import app.karacal.data.SavedPaymentMethods;
import app.karacal.models.Tour;

public class PreferenceHelper {

    private static final String PREFERENCES_FILE_NAME = "karacal.pref";
    private static final String PRIVACY_POLICY_APPLIED_KEY = "PRIVACY_POLICY_APPLIED";
    private static final String AUTH_TOKEN_KEY = "AUTH_TOKEN";
    private static final String DOWNLOADED_TOURS_KEY = "DOWNLOADED_TOURS";
    private static final String PREF_PROFILE_KEY = "PREF_PROFILE_KEY";
    private static final String NOTIFICATIONS_KEY = "NOTIFICATIONS_KEY";
    private static final String LOCATIONS_KEY = "LOCATIONS_KEY";
    private static final String NOTIFICATION_SCHEDULE_KEY = "NOTIFICATION_SCHEDULE_KEY";
    private static final String PAYMENT_METHODS_KEY = "PAYMENT_METHODS_KEY";
    private static final String DOWNLOAD_ONLY_VIA_WIFI_KEY = "DOWNLOAD_ONLY_VIA_WIFI";
    private static final String PAUSE_AUDIO_AFTER_EACH_SEGMENT_KEY = "PAUSE_AUDIO_AFTER_EACH_SEGMENT";
    private static final String NOTIFICATIONS_ALLOW_KEY = "NOTIFICATIONS_ALLOW";
    private static final String NOTIFICATION_SHOWN_LAST_TIME = "NOTIFICATION_SHOWN_LAST_TIME";


    private static SharedPreferences getSharedPreferences() {
        return getSharedPreferences(App.getInstance());
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isPrivacyPolicyApplied(){
        return getSharedPreferences().getBoolean(PRIVACY_POLICY_APPLIED_KEY, false);
    }

    public static void setPrivacyPolicyApplied(boolean isApplied){
        getSharedPreferences().edit().putBoolean(PRIVACY_POLICY_APPLIED_KEY, isApplied).apply();
    }

    public static String loadToken(){
        return loadToken(App.getInstance());
    }

    public static String loadToken(Context context){
        return getSharedPreferences(context).getString(AUTH_TOKEN_KEY, null);
    }

    public static void saveToken(String token){
        getSharedPreferences().edit().putString(AUTH_TOKEN_KEY, token).apply();
    }

    public static void deleteToken(Context context){
        getSharedPreferences(context).edit().remove(AUTH_TOKEN_KEY).apply();
    }

    public static String getProfileCache(Context context) {
        String defValue = ProfileCache.getEmptyInstance().retrieveStringFormat();
        return getSharedPreferences(context).getString(PREF_PROFILE_KEY, defValue);
    }

    public static void setProfileCache(Context context, String cache) {
        getSharedPreferences(context).edit().putString(PREF_PROFILE_KEY, cache).apply();
    }

    public static String getDownloadedToursCache(Context context) {
        String defValue = DownloadedToursCache.getEmptyInstance().retrieveStringFormat();
        return getSharedPreferences(context).getString(DOWNLOADED_TOURS_KEY, defValue);
    }

    public static void setDownloadedToursCache(Context context, String downloadedToursCache) {
        getSharedPreferences(context).edit().putString(DOWNLOADED_TOURS_KEY, downloadedToursCache).apply();
    }

    public static String getNotificationsCache(Context context) {
        String defValue = NotificationsCache.getEmptyInstance().retrieveStringFormat();
        return getSharedPreferences(context).getString(NOTIFICATIONS_KEY, defValue);
    }

    public static void setNotificationsCache(Context context, String notificationsCache) {
        getSharedPreferences(context).edit().putString(NOTIFICATIONS_KEY, notificationsCache).apply();
    }

    public static String getNotificationsSchedule(Context context) {
        String defValue = NotificationsSchedule.getEmptyInstance().retrieveStringFormat();
        return getSharedPreferences(context).getString(NOTIFICATION_SCHEDULE_KEY, defValue);
    }

    public static void setNotificationsSchedule(Context context, String notificationsSchedule) {
        getSharedPreferences(context).edit().putString(NOTIFICATION_SCHEDULE_KEY, notificationsSchedule).apply();
    }

    public static String getPaymentMethods(Context context) {
        String defValue = "";
        try {
            defValue = SavedPaymentMethods.getEmptyInstance().retrieveStringFormat();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return getSharedPreferences(context).getString(PAYMENT_METHODS_KEY, defValue);
    }

    public static void setPaymentMethods(Context context, String paymentMethods) {
        getSharedPreferences(context).edit().putString(PAYMENT_METHODS_KEY, paymentMethods).apply();
    }

    public static boolean isDownloadOnlyViaWifi(Context context){
        return getSharedPreferences(context).getBoolean(DOWNLOAD_ONLY_VIA_WIFI_KEY, false);
    }

    public static void setDownloadOnlyViaWifi(Context context, boolean value){
        getSharedPreferences(context).edit().putBoolean(DOWNLOAD_ONLY_VIA_WIFI_KEY, value).apply();
    }

    public static boolean isPauseAudioAfterEachSegment(Context context){
        return getSharedPreferences(context).getBoolean(PAUSE_AUDIO_AFTER_EACH_SEGMENT_KEY, false);
    }

    public static void setPauseAudioAfterEachSegment(Context context, boolean value){
        getSharedPreferences(context).edit().putBoolean(PAUSE_AUDIO_AFTER_EACH_SEGMENT_KEY, value).apply();
    }

    public static boolean isNotificationsAllowed(Context context){
        return getSharedPreferences(context).getBoolean(NOTIFICATIONS_ALLOW_KEY, true);
    }

    public static void setNotificationsAllowed(Context context, boolean value){
        getSharedPreferences(context).edit().putBoolean(NOTIFICATIONS_ALLOW_KEY, value).apply();
    }

    public static long getLastNotificationWasShownTime(Context context){
        return getSharedPreferences(context).getLong(NOTIFICATION_SHOWN_LAST_TIME, 0);
    }

    public static void setLastNotificationWasShownTime(Context context, long value){
        getSharedPreferences(context).edit().putLong(NOTIFICATION_SHOWN_LAST_TIME, value).apply();
    }

    public static String getLocationsCache(Context context) {
        String defValue = LocationCache.getEmptyInstance().retrieveStringFormat();
        return getSharedPreferences(context).getString(LOCATIONS_KEY, defValue);
    }

    public static void setLocationsCache(Context context, String locationsCache) {
        getSharedPreferences(context).edit().putString(LOCATIONS_KEY, locationsCache).apply();
    }
}
