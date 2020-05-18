package app.karacal.helpers;

import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TokenHelper {

    private String token;

    @Inject
    public TokenHelper(){
        token = PreferenceHelper.loadToken();
    }

    public String getToken(){
        return token;
    }

    public void updateToken(String token){
        this.token = token;
        PreferenceHelper.saveToken(token);
    }

    public void deleteToken(Context context){
        PreferenceHelper.deleteToken(context);
    }

}
