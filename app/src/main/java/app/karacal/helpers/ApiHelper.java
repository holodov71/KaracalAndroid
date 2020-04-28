package app.karacal.helpers;

import com.google.gson.JsonObject;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.karacal.App;
import app.karacal.R;
import app.karacal.models.Profile;
import app.karacal.retrofit.InitService;
import app.karacal.retrofit.LoginRequest;
import app.karacal.retrofit.ProfileService;
import app.karacal.retrofit.RegisterRequest;
import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Response;

@Singleton
public class ApiHelper {

    private App context;

    private InitService initService;

    private ProfileService profileService;

    @Inject
    public ApiHelper(App context, InitService initService, ProfileService profileService){
        this.context = context;
        this.initService = initService;
        this.profileService = profileService;
    }

    public Single<Profile> getProfile(String token){
        return Single.create(emitter -> {
            Response<Profile> response = profileService.getProfile("Bearer " + token).execute();
            if (response.isSuccessful()){
                emitter.onSuccess(response.body());
                return;
            }
            emitter.onError(new Exception(context.getString(R.string.connection_problem)));
        });
    }

    public Single<String> login(LoginRequest model) {
        return Single.create(emitter -> {
            Response<ResponseBody> response = initService.login(model).execute();
            if (response.isSuccessful()) {
                String responseString = response.body().string();
                try{
                    JsonObject jsonObject = JsonHelper.getJsonObject(responseString);
                    String token = jsonObject.get("token").getAsString();
                    emitter.onSuccess(token);
                    return;
                } catch (Exception e){
                    String errors = JsonHelper.extractStrings(responseString);
                    if (errors != null){
                        emitter.onError(new Exception(errors));
                        return;
                    }
                }
            }
            emitter.onError(new Exception(context.getString(R.string.connection_problem)));
        });
    }

    public Completable register(RegisterRequest model) {
        return Completable.create(emitter -> {
            Response<ResponseBody> response = initService.register(model).execute();
            if (response.isSuccessful()) {
                String responseString = response.body().string();
                if (responseString.equals("true")){
                    emitter.onComplete();
                    return;
                } else {
                    String errors = JsonHelper.extractStrings(responseString);
                    if (errors != null){
                        emitter.onError(new Exception(errors));
                        return;
                    }
                }
            }
            emitter.onError(new Exception(context.getString(R.string.connection_problem)));
        });
    }

}
