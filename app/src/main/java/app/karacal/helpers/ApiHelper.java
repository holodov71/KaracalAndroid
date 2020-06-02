package app.karacal.helpers;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.karacal.App;
import app.karacal.R;
import app.karacal.models.Profile;
import app.karacal.retrofit.GuideService;
import app.karacal.retrofit.InitService;
import app.karacal.retrofit.TourService;
import app.karacal.retrofit.TracksService;
import app.karacal.retrofit.models.GuideResponse;
import app.karacal.retrofit.models.LoginRequest;
import app.karacal.retrofit.ProfileService;
import app.karacal.retrofit.models.RegisterRequest;
import app.karacal.retrofit.models.SocialLoginRequest;
import app.karacal.retrofit.models.TourResponse;
import app.karacal.retrofit.models.TrackResponse;
import app.karacal.retrofit.models.UploadTrackResponse;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class ApiHelper {

    private App context;

    private InitService initService;
    private ProfileService profileService;
    private GuideService guideService;
    private TourService tourService;
    private TracksService tracksService;

    @Inject
    public ApiHelper(App context,
                     InitService initService,
                     ProfileService profileService,
                     GuideService guideService,
                     TourService tourService,
                     TracksService tracksService){
        this.context = context;
        this.initService = initService;
        this.profileService = profileService;
        this.guideService = guideService;
        this.tourService = tourService;
        this.tracksService = tracksService;
    }

    public Single<Profile> getProfile(String token){
        return Single.create(emitter -> {
            Log.v("TAG", "getProfile token = "+token);
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

    public Single<String> socialLogin(SocialLoginRequest model) {
        return Single.create(emitter -> {
            Response<ResponseBody> response = initService.socialLogin(model).execute();
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

    public Observable<List<GuideResponse>> loadGuides(String token) {
        return guideService.getGuidesList("Bearer " + token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<TourResponse>> loadTours(String token) {
        return tourService.getToursList("Bearer " + token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<TrackResponse>> loadTracks(String token, String tourId) {
        return tracksService.getTracksList("Bearer " + token, tourId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<UploadTrackResponse> uploadAudioToServer(String token, String guideId, String tourId, String path){
        Log.v("uploadAudio", "upload file " + path);
        File file = new File(path);
        Log.d("uploadAudio", "file size "+ file.length());
        RequestBody fileBody = RequestBody.create(MediaType.parse("audio/*"), file);

        return tracksService.uploadAudio("Bearer " + token, guideId, tourId, fileBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

}
