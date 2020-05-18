package app.karacal.helpers;

import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.karacal.models.Profile;

@Singleton
public class ProfileHolder {

    private TokenHelper tokenHelper;

    private Profile profile;

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
        tokenHelper.deleteToken(context);
    }
}
