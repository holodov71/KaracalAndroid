package app.karacal.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.karacal.App;

@Singleton
public class PreferenceHelper {

    private static final String PREFERENCES_FILE_NAME = "karacal.pref";
    private static final String PRIVACY_POLICY_APPLIED_KEY = "PRIVACY_POLICY_APPLIED";
    private static final String AUTH_TOKEN_KEY = "AUTH_TOKEN";
    private final SharedPreferences preferences;

    @Inject
    public PreferenceHelper(App context){
        preferences = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
    }

    public boolean isPrivacyPolicyApplied(){
        return preferences.getBoolean(PRIVACY_POLICY_APPLIED_KEY, false);
    }

    public void setPrivacyPolicyApplied(boolean isApplied){
        preferences.edit().putBoolean(PRIVACY_POLICY_APPLIED_KEY, isApplied).apply();
    }

    public String loadToken(){
        return preferences.getString(AUTH_TOKEN_KEY, null);
    }

    public void saveToken(String token){
        preferences.edit().putString(AUTH_TOKEN_KEY, token).apply();
    }
}
