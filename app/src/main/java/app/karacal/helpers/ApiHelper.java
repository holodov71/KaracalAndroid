package app.karacal.helpers;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;

import org.bouncycastle.jcajce.provider.symmetric.ARC4;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.karacal.App;
import app.karacal.R;
import app.karacal.models.Profile;
import app.karacal.models.Tag;
import app.karacal.models.Track;
import app.karacal.network.CommentsService;
import app.karacal.network.GuideService;
import app.karacal.network.InitService;
import app.karacal.network.StripeService;
import app.karacal.network.TourService;
import app.karacal.network.TracksService;
import app.karacal.network.models.request.CreateCardRequest;
import app.karacal.network.models.request.CreateCommentRequest;
import app.karacal.network.models.request.CreateCustomerRequest;
import app.karacal.network.models.request.CreateSubscriptionRequest;
import app.karacal.network.models.request.DonateAuthorRequest;
import app.karacal.network.models.request.GuideByEmailRequest;
import app.karacal.network.models.request.NearToursRequest;
import app.karacal.network.models.request.PaymentRequest;
import app.karacal.network.models.request.ProfileRequest;
import app.karacal.network.models.request.RenameTrackRequest;
import app.karacal.network.models.request.ResetPasswordRequest;
import app.karacal.network.models.request.SaveTourRequest;
import app.karacal.network.models.request.SerGuideRatingRequest;
import app.karacal.network.models.request.SerTourRatingRequest;
import app.karacal.network.models.request.UploadTrackRequest;
import app.karacal.network.models.response.BaseResponse;
import app.karacal.network.models.response.ChangeAvatarResponse;
import app.karacal.network.models.response.CommentsResponse;
import app.karacal.network.models.response.ContentResponse;
import app.karacal.network.models.response.CreateCardResponse;
import app.karacal.network.models.response.CreateCustomerResponse;
import app.karacal.network.models.response.CreateSubscriptionResponse;
import app.karacal.network.models.response.DonateAuthorResponse;
import app.karacal.network.models.response.GuideByEmailResponse;
import app.karacal.network.models.response.GuideResponse;
import app.karacal.network.models.request.LoginRequest;
import app.karacal.network.ProfileService;
import app.karacal.network.models.request.RegisterRequest;
import app.karacal.network.models.request.SocialLoginRequest;
import app.karacal.network.models.response.PaymentResponse;
import app.karacal.network.models.response.PurchasesResponse;
import app.karacal.network.models.response.RatingResponse;
import app.karacal.network.models.response.ResetPasswordResponse;
import app.karacal.network.models.response.SaveTourResponse;
import app.karacal.network.models.response.SubscriptionsListResponse;
import app.karacal.network.models.response.TourDetailsResponse;
import app.karacal.network.models.response.TourResponse;
import app.karacal.network.models.response.TrackResponse;
import app.karacal.network.models.response.UploadTrackResponse;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Part;

@Singleton
public class ApiHelper implements EphemeralKeyProvider {

    private App context;

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
        ephemeralKeyUpdateListener.onKeyUpdate(App.getInstance().getString(R.string.stripe_api_key));
    }

    // Profile region

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

    public Observable<BaseResponse> deleteProfile(String token) {
        return profileService.deleteProfile("Bearer " + token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PurchasesResponse> getPurchases(String token) {
        return profileService.loadPurchases("Bearer " + token, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ChangeAvatarResponse> changeAvatar(String token, String path){
        File file = new File(path);
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part image = MultipartBody.Part.createFormData("image", file.getName(), fileBody);

        return profileService.changeAvatar("Bearer " + token, image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    // Auth region

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

    public Observable<BaseResponse> register(RegisterRequest request) {
        return initService.register(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResetPasswordResponse> resetPassword(ResetPasswordRequest request) {
        return initService.resetPassword(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // Guide region

    public Observable<List<GuideResponse>> loadGuides(String token) {
        return guideService.getGuidesList("Bearer " + token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<GuideResponse> loadGuide(String token, String guideId) {
        return guideService.getGuideById("Bearer " + token, guideId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<GuideByEmailResponse> getGuide(String token, GuideByEmailRequest request) {
        return guideService.getGuideByEmail("Bearer " + token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<RatingResponse> setRatingForGuide(int guideId, int rating) {
        SerGuideRatingRequest request = new SerGuideRatingRequest(guideId, String.valueOf(rating));
        return tourService.setRatingForGuide("Bearer " + PreferenceHelper.loadToken(App.getInstance()), request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // Tours region

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

    public Observable<List<ContentResponse>> loadToursByAuthor(String token, int authorId) {
        return tourService.getToursByAuthor("Bearer " + token, authorId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<ContentResponse>> loadPagedContents(String token) {
        return tourService.getContentsList("Bearer " + token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<ContentResponse>> loadNearTours(String token, NearToursRequest request) {
        return tourService.loadNearTours("Bearer " + token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SaveTourResponse> createTour(String token, SaveTourRequest request, String imagePath) {
        MultipartBody.Part image = null;
        if (imagePath != null){
            File file = new File(imagePath);
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
            image = MultipartBody.Part.createFormData("img", file.getName(), fileBody);
        }

        MultipartBody.Part data = MultipartBody.Part.createFormData("body", new Gson().toJson(request));

        return tourService.createTour("Bearer " + token, image, data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SaveTourResponse> updateTour(String token, int id, SaveTourRequest request, String imagePath) {
        MultipartBody.Part image = null;
        if (imagePath != null){
            File file = new File(imagePath);
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
            image = MultipartBody.Part.createFormData("img", file.getName(), fileBody);
        }

        MultipartBody.Part data = MultipartBody.Part.createFormData("body", new Gson().toJson(request));

        return tourService.editTour("Bearer " + token, id, image, data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<ContentResponse> loadTourById(String token, int tourId) {
        return tourService.getTourById("Bearer " + token, String.valueOf(tourId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<RatingResponse> setRatingForTour(int tourId, int rating) {
        SerTourRatingRequest request = new SerTourRatingRequest(tourId, String.valueOf(rating));
        return tourService.setRatingForTour("Bearer " + PreferenceHelper.loadToken(App.getInstance()), request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //Tags Region
    public Observable<List<Tag>> loadTags(String token) {
        return tourService.getTags("Bearer " + token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //Tracks Region
    public Observable<List<TrackResponse>> loadTracks(String token, String tourId) {
        return tracksService.getTracksList("Bearer " + token, tourId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<UploadTrackResponse> uploadAudioToServer(String token, String guideId, String tourId, Track track){
        File file = new File(track.getFileUri());
        RequestBody fileBody = RequestBody.create(MediaType.parse("audio/*"), file);
        MultipartBody.Part audio = MultipartBody.Part.createFormData("mp3", file.getName(), fileBody);

        MultipartBody.Part data = MultipartBody.Part.createFormData("body", new Gson().toJson(new UploadTrackRequest(track.getTitle())));

        return tracksService.uploadAudio("Bearer " + token, guideId, tourId, audio, data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Observable<BaseResponse> deleteTrack(String token, String trackId) {
        return tracksService.deleteTrack("Bearer " + token, trackId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseResponse> renameTrack(String token, int trackId, RenameTrackRequest request) {
        return tracksService.renameTrack("Bearer " + token, trackId, request)
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


    // Payment Region
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

    public Observable<DonateAuthorResponse> donateAuthor(String token, DonateAuthorRequest request){
        return stripeService.donateAuthor("Bearer " + token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
