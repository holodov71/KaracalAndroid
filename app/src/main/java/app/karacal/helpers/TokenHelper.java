package app.karacal.helpers;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TokenHelper {

    private PreferenceHelper preferenceHelper;
    private String token;

    @Inject
    public TokenHelper(PreferenceHelper preferenceHelper){
        this.preferenceHelper = preferenceHelper;
        token = preferenceHelper.loadToken();
    }

    public String getToken(){
        return token;
    }

    public void updateToken(String token){
        this.token = token;
        preferenceHelper.saveToken(token);
    }

    public void deleteToken(){
        preferenceHelper.deleteToken();
    }

}
