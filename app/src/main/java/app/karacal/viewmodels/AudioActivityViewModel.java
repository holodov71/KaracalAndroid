package app.karacal.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.data.DownloadedToursCache;
import app.karacal.data.ProfileCache;
import app.karacal.data.SavedPaymentMethods;
import app.karacal.data.repository.AlbumRepository;
import app.karacal.data.repository.GuideRepository;
import app.karacal.data.repository.TourRepository;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.NetworkStateHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.helpers.ProfileHolder;
import app.karacal.interfaces.ActionCallback;
import app.karacal.models.Album;
import app.karacal.models.CardDetails;
import app.karacal.models.Comment;
import app.karacal.models.Guide;
import app.karacal.models.Player;
import app.karacal.models.Tour;
import app.karacal.models.Track;
import app.karacal.network.models.request.CreateCustomerRequest;
import app.karacal.network.models.request.PaymentRequest;
import app.karacal.network.models.response.CommentsResponse;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static app.karacal.App.TAG;
import static app.karacal.helpers.NetworkStateHelper.DesiredInternet.WIFI_INTERNET;

public class AudioActivityViewModel extends ViewModel {

    public static class AudioActivityViewModelFactory extends ViewModelProvider.NewInstanceFactory {

        private final int tourId;

        public AudioActivityViewModelFactory(int tourId) {
            this.tourId = tourId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == AudioActivityViewModel.class) {
                return (T) new AudioActivityViewModel(tourId);
            }
            return null;
        }
    }

    @Inject
    ProfileHolder profileHolder;

    @Inject
    TourRepository tourRepository;

    @Inject
    GuideRepository guideRepository;

    @Inject
    AlbumRepository albumRepository;

    @Inject
    ApiHelper apiHelper;

    private CompositeDisposable disposable = new CompositeDisposable();

    private final int tourId;

    private String customerId;

    private SavedPaymentMethods savedPaymentMethods;

    private MutableLiveData<Tour> tour = new MutableLiveData<>();
    private SingleLiveEvent<Integer> goToDonateAction = new SingleLiveEvent<>();
    private SingleLiveEvent<Void> listenAction = new SingleLiveEvent<>();
    private SingleLiveEvent<Long> paymentAction = new SingleLiveEvent<>();
    public SingleLiveEvent<Long> needPaymentMethodAction = new SingleLiveEvent<>();
    private SingleLiveEvent<Void> tourDownloadedAction = new SingleLiveEvent<>();
    private SingleLiveEvent<String> messageAction = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorAction = new SingleLiveEvent<>();
    private MutableLiveData<List<Comment>> commentsLiveData = new MutableLiveData<>();
    private MutableLiveData<Guide> guideLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> tourDownloadingLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isTourDownloaded = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Integer> tourCountLiveData = new MutableLiveData<>();
    public SingleLiveEvent<String> tourPayedAction = new SingleLiveEvent<>();


    public LiveData<Tour> getTour(){
        return tour;
    }

    public SingleLiveEvent<Integer> getGoToDonateAction() {
        return goToDonateAction;
    }

    public SingleLiveEvent<Void> getListenAction() {
        return listenAction;
    }

    public SingleLiveEvent<Long> getPaymentAction() {
        return paymentAction;
    }

    public SingleLiveEvent<Void> getTourDownloadedAction() {
        return tourDownloadedAction;
    }

    public LiveData<Boolean> getTourDownloading() {
        return tourDownloadingLiveData;
    }

    public LiveData<Boolean> isTourDownloaded() {
        return isTourDownloaded;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public SingleLiveEvent<String> getErrorAction() {
        return errorAction;
    }

    public SingleLiveEvent<String> getMessageAction() {
        return messageAction;
    }

    public LiveData<List<Comment>> getComments(){
        return commentsLiveData;
    }

    public LiveData<Guide> getGuide(){
        return guideLiveData;
    }

    public LiveData<Integer> getToursCount(){
        return tourCountLiveData;
    }

    private MutableLiveData<Album> album = new MutableLiveData<>();

    private Player player;

    private Stripe stripe;

    private boolean isAlbumLoaded = false;

    public AudioActivityViewModel(int tourId) {
        Log.v("AudioActivityViewModel", "tourId = "+tourId);
        App.getAppComponent().inject(this);
        stripe = new Stripe(App.getInstance().getApplicationContext(), App.getResString(R.string.stripe_api_key));
        this.tourId = tourId;
        player = new Player(album);
        player.setTourId(tourId);
        isTourDownloaded.setValue(DownloadedToursCache.getInstance(App.getInstance()).containsTour(tourId));
        loadData();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        Log.v(TAG, "setPlayer(Player player)");
        if(player.getTourId() == this.tourId) {
            this.player = player;
            this.album.postValue(player.getAlbumLiveData().getValue());
            isAlbumLoaded = true;
        }
    }

    public void onDonateClicked() {
        if (guideLiveData.getValue() != null){
            goToDonateAction.setValue(guideLiveData.getValue().getId());
        }
    }

    public void loadTracks() {
        if (!isAlbumLoaded) {
            if (tour.getValue() != null && tour.getValue().getAudio() != null) {
                album.setValue(tour.getValue().getAlbum());
            } else {
                albumRepository.loadTracksByTour(String.valueOf(tourId));
            }
        }
    }

    public LiveData<Album> getAlbum(){
        return album;
    }

    public String getTrackTitle(int position){
        Log.v("AudioActivityViewModel", "getTrackTitle position = "+position);
        if (album.getValue() != null) {
            Log.v("AudioActivityViewModel", "getTrackTitle album.getValue().getTracks() = "+album.getValue().getTracks());

            return album.getValue().getTracks().get(position).getTitle();
        } else {
            return "";
        }
    }

    public void onListenTourClicked(){
        checkAccess(() -> listenAction.call());
    }

    private void checkAccess(ActionCallback successCallback){
        if (tour.getValue() != null) {
            if (tour.getValue().getPrice() == 0) {
                successCallback.invoke();
            } else {
                if (ProfileCache.getInstance(App.getInstance()).isHasSubscription() || ProfileCache.getInstance(App.getInstance()).isPurchasesContainsTour(tour.getValue().getId())) {
                    Log.v("checkAccess", "has Subscription");
                    successCallback.invoke();
                } else {
                    paymentAction.setValue(tour.getValue().getPrice());
                }
            }
        }
    }

    private void loadData(){
        try {
            savedPaymentMethods = SavedPaymentMethods.getInstance(App.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadTour(tourId);
        loadComments();
        createCustomer();
    }

    private void loadTour(int tourId){
        disposable.add(tourRepository.loadTourById(tourId)
                .subscribe(loadedTour -> {
                    tour.setValue(loadedTour);
                    loadAuthor(loadedTour.getAuthorId());
                }, throwable -> {
                    commentsLiveData.setValue(new ArrayList<>());
                }));
    }

    public void loadComments(){
        List<Comment> list = commentsLiveData.getValue();
        if (list == null || list.isEmpty()) {
            disposable.add(apiHelper.loadComments(PreferenceHelper.loadToken(App.getInstance()), tourId)
                    .subscribe(response -> {
                        List<Comment> comments = new ArrayList<>();
                        if (response.isSuccess()) {
                            for (CommentsResponse.CommentResponse comment : response.getComments()) {
                                comments.add(new Comment(comment));
                            }
                        }
                        commentsLiveData.setValue(comments);
                    }, throwable -> {
                        commentsLiveData.setValue(new ArrayList<>());
                    }));
        }
    }

    private void loadToursCount(int guideId){
        disposable.add(tourRepository.loadToursByAuthor(guideId)
                .subscribe(
                        response -> tourCountLiveData.setValue(response.size()),
                        throwable -> tourCountLiveData.setValue(0)
                ));
    }

    public void downloadTour(Context context) {
        if(DownloadedToursCache.getInstance(context).containsTour(tourId)) {
            DownloadedToursCache.getInstance(context).deleteDownloadedTour(context, tourId);
            messageAction.setValue(App.getResString(R.string.tour_deleted));
            isTourDownloaded.setValue(false);
        } else {
            if (PreferenceHelper.isDownloadOnlyViaWifi(context) &&
                !NetworkStateHelper.isNetworkAvailable(context, WIFI_INTERNET)){
                tourDownloadingLiveData.setValue(false);
                messageAction.setValue(App.getResString(R.string.turn_on_wifi));
            } else if(tour.getValue() != null){
                checkAccess(() -> {
                    tourDownloadingLiveData.setValue(true);
                    disposable.add(Single.just(tour.getValue().getAudio())
                            .flattenAsObservable(items -> items)
                            .flatMap(this::downloadTrack)
                            .toList()
                            .map(this::processDownloading)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(downloaded -> {
                                tourDownloadingLiveData.setValue(false);
                                if (downloaded) {
                                    tourDownloadedAction.call();
                                    isTourDownloaded.setValue(true);
                                } else {
                                    messageAction.setValue(App.getResString(R.string.error_download_tour));
                                }
                            }, throwable -> {
                                tourDownloadingLiveData.setValue(false);
                                messageAction.setValue(App.getResString(R.string.connection_problem));
                            }));
                });
            } else {
                tourDownloadingLiveData.setValue(false);
                messageAction.setValue(App.getResString(R.string.error_download_tour));
            }
        }
    }

    private Boolean processDownloading(List<Track> tracks){
        boolean allTracksDownloaded = true;
        for (Track track : tracks) {
            if (track.getFileUri() == null) {
                allTracksDownloaded = false;
                break;
            } else {
                Log.v("Tracks downloaded", track.getFileUri());
            }
        }
        if (allTracksDownloaded) {
            if (tour.getValue() != null){
                tour.getValue().setAudio(tracks);
                DownloadedToursCache.getInstance(App.getInstance()).addDownloadedTour(App.getInstance(), tour.getValue());
            }
            return true;
        } else {
            return false;
        }
    }

    private Observable<Track> downloadTrack(Track track){
        Log.v("downloadTrack", track.getFilename());

        try {
            URL u = new URL(track.getFilename());
            InputStream is = u.openStream();

            DataInputStream dis = new DataInputStream(is);

            byte[] buffer = new byte[1024];
            int length;

            File imageDir = new File(App.getInstance().getExternalFilesDir(null), "audio");
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }
            File newFile = new File(imageDir, track.getTitle());


            Log.v("downloadTrack", "Path file = "+newFile.getPath());


            FileOutputStream fos = new FileOutputStream(newFile);
            while ((length = dis.read(buffer))>0) {
                fos.write(buffer, 0, length);
            }
            track.setFileUri(newFile.getPath());

        } catch (MalformedURLException mue) {
            Log.e("downloadTrack", "malformed url error", mue);
        } catch (IOException ioe) {
            Log.e("downloadTrack", "io error", ioe);
        } catch (SecurityException se) {
            Log.e("downloadTrack", "security error", se);
        }

        return Observable.just(track);
    }

    private void loadAuthor(int guideId){
            disposable.add(apiHelper.loadGuide(PreferenceHelper.loadToken(App.getInstance()), String.valueOf(guideId))
                    .subscribe(response -> {
                                guideLiveData.setValue(new Guide(response));
                                loadToursCount(guideId);
                            }
                            , throwable ->
                                    guideLiveData.setValue(null)
                    ));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        player.dispose();
        disposable.dispose();
    }


    // Payment region
    private void createCustomer(){
        String serverToken = PreferenceHelper.loadToken(App.getInstance());

        String mail = ProfileCache.getInstance(App.getInstance()).getProfile().getEmail();

        CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest(mail);
        disposable.add(apiHelper.createCustomer(serverToken, createCustomerRequest)
                .subscribe(response -> {
                    Log.v("createCustomer", "Success response = " + response);
                    if (response.isSuccess()) {
                        customerId = response.getId();
                    } else {
                        Log.e(TAG, response.getErrorMessage());
                    }
                }, throwable -> {
                    Log.v(TAG, "Can not create customer");
                }));
    }

    public void payTour() {
        if (savedPaymentMethods.getPaymentMethodsList().isEmpty()){
            needPaymentMethodAction.call();
        } else {
            obtainCardToken();
        }
    }

    private void obtainCardToken(){
        isLoading.setValue(true);

        CardDetails paymentMethod = savedPaymentMethods.getDefaultPaymentMethod();

        Card card = new Card.Builder(
                paymentMethod.getNumber(),
                paymentMethod.getExpMonth(),
                paymentMethod.getExpYear(),
                paymentMethod.getCvc())
                .build();

        stripe.createCardToken(card, new ApiResultCallback<Token>() {
            @Override
            public void onSuccess(Token token) {
                payTour(token.getId());
            }

            @Override
            public void onError(@NotNull Exception e) {
                isLoading.setValue(false);
                errorAction.setValue(App.getResString(R.string.common_error));
            }
        });
    }

    private void payTour(String token){
        PaymentRequest request = new PaymentRequest(tour.getValue().getPrice(), "eur", token, "Paris tour description", tourId);
        disposable.add(apiHelper.makePayment(PreferenceHelper.loadToken(App.getInstance()), request)
                .subscribe(response -> {
                    Log.v("makePayment", "Success response = " + response);
                    if (response.isSuccess()) {
                        ProfileCache.getInstance(App.getInstance()).addTourPurchase(App.getInstance(), tourId);
                        tourPayedAction.setValue(response.getReceiptUrl());
                    } else {
                        errorAction.setValue(response.getErrorMessage());
                    }
                    isLoading.setValue(false);
                }, throwable -> {
                    isLoading.setValue(false);
                    errorAction.setValue(App.getResString(R.string.connection_problem));
                }));
    }

}
