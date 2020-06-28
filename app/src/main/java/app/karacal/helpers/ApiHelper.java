package app.karacal.helpers;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.google.gson.JsonObject;
import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.karacal.App;
import app.karacal.R;
import app.karacal.models.Profile;
import app.karacal.retrofit.CommentsService;
import app.karacal.retrofit.GuideService;
import app.karacal.retrofit.InitService;
import app.karacal.retrofit.StripeService;
import app.karacal.retrofit.TourService;
import app.karacal.retrofit.TracksService;
import app.karacal.retrofit.models.request.CreateCardRequest;
import app.karacal.retrofit.models.request.CreateCommentRequest;
import app.karacal.retrofit.models.request.CreateCustomerRequest;
import app.karacal.retrofit.models.request.CreateSubscriptionRequest;
import app.karacal.retrofit.models.request.NearToursRequest;
import app.karacal.retrofit.models.request.PaymentRequest;
import app.karacal.retrofit.models.request.ProfileRequest;
import app.karacal.retrofit.models.request.SaveTourRequest;
import app.karacal.retrofit.models.response.BaseResponse;
import app.karacal.retrofit.models.response.CommentsResponse;
import app.karacal.retrofit.models.response.ContentResponse;
import app.karacal.retrofit.models.response.CreateCardResponse;
import app.karacal.retrofit.models.response.CreateCustomerResponse;
import app.karacal.retrofit.models.response.CreateSubscriptionResponse;
import app.karacal.retrofit.models.response.GuideResponse;
import app.karacal.retrofit.models.request.LoginRequest;
import app.karacal.retrofit.ProfileService;
import app.karacal.retrofit.models.request.RegisterRequest;
import app.karacal.retrofit.models.request.SocialLoginRequest;
import app.karacal.retrofit.models.response.PaymentResponse;
import app.karacal.retrofit.models.response.SaveTourResponse;
import app.karacal.retrofit.models.response.SubscriptionsListResponse;
import app.karacal.retrofit.models.response.TourDetailsResponse;
import app.karacal.retrofit.models.response.TourResponse;
import app.karacal.retrofit.models.response.TrackResponse;
import app.karacal.retrofit.models.response.UploadTrackResponse;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

@Singleton
public class ApiHelper implements EphemeralKeyProvider {

    private App context;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private InitService initService;
    private ProfileService profileService;
    private GuideService guideService;
    private TourService tourService;
    private CommentsService commentsService;
    private TracksService tracksService;
    private StripeService stripeService;

    @Inject
    public ApiHelper(App context,
                     InitService initService,
                     ProfileService profileService,
                     GuideService guideService,
                     TourService tourService,
                     CommentsService commentsService,
                     TracksService tracksService,
                     StripeService stripeService){
        this.context = context;
        this.initService = initService;
        this.profileService = profileService;
        this.guideService = guideService;
        this.tourService = tourService;
        this.commentsService = commentsService;
        this.tracksService = tracksService;
        this.stripeService = stripeService;
    }

    @Override
    public void createEphemeralKey(@NonNull @Size(min = 4) String apiVersion, @NotNull EphemeralKeyUpdateListener ephemeralKeyUpdateListener) {
        final Map<String, String> apiParamMap = new HashMap<>();
        apiParamMap.put("api_version", apiVersion);
        ephemeralKeyUpdateListener.onKeyUpdate(App.getInstance().getString(R.string.stripe_publishable_api_key));

//        compositeDisposable.add(stripeService.createEphemeralKey(apiParamMap)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        response -> {
//                            try {
//                                final String rawKey = response.string();
//                                ephemeralKeyUpdateListener.onKeyUpdate(rawKey);
//                            } catch (IOException ignored) {
//                            }
//                        }));
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

    public Observable<Profile> loadProfile(String token){
        return profileService.loadProfile("Bearer " + token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseResponse> editProfile(String token, ProfileRequest request) {
        return profileService.editProfile("Bearer " + token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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

    public Observable<List<ContentResponse>> loadContents(String token) {
        return tourService.getContentsList("Bearer " + token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<ContentResponse>> loadNearTours(String token, NearToursRequest request) {
        return tourService.loadNearTours("Bearer " + token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SaveTourResponse> createTour(String token, SaveTourRequest request) {
        return tourService.createTour("Bearer " + token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<TourDetailsResponse> loadTourById(String token, int tourId) {
        return tourService.getTourById("Bearer " + token, String.valueOf(tourId))
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
        MultipartBody.Part audio = MultipartBody.Part.createFormData("mp3", file.getName(), fileBody);

        return tracksService.uploadAudio("Bearer " + token, guideId, tourId, audio)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    //Comments Region
    public Observable<CommentsResponse> loadComments(String token, int tourId) {
        return commentsService.getCommentsByTour("Bearer " + token, tourId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseResponse> createNewComment(String token, CreateCommentRequest request){
        return commentsService.createNewComment("Bearer " + token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    //    Payment Region
    public Observable<PaymentResponse> makePayment(String token, PaymentRequest request){
        return stripeService.makePayment("Bearer " + token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Observable<CreateCustomerResponse> createCustomer(String token, CreateCustomerRequest request){
        return stripeService.createCustomer("Bearer " + token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Observable<CreateCardResponse> createCard(String token, CreateCardRequest request){
        return stripeService.createCard("Bearer " + token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CreateSubscriptionResponse> createSubscription(String token, CreateSubscriptionRequest request){
        return stripeService.createSubscription("Bearer " + token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SubscriptionsListResponse> loadSubscriptions(String token, String customerId){
        return stripeService.getSubscriptions("Bearer " + token, customerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseResponse> cancelSubscription(String token, String subscriptionId){
        return stripeService.cancelSubscription("Bearer " + token, subscriptionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
