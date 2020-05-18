package app.karacal.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.content.ContextCompat;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.karacal.App;

public class PreferenceHelper {

    private static final String PREFERENCES_FILE_NAME = "karacal.pref";
    private static final String PRIVACY_POLICY_APPLIED_KEY = "PRIVACY_POLICY_APPLIED";
    private static final String AUTH_TOKEN_KEY = "AUTH_TOKEN";

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
}
