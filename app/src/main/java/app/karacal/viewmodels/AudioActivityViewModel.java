package app.karacal.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

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
import app.karacal.data.repository.AlbumRepository;
import app.karacal.data.repository.GuideRepository;
import app.karacal.data.repository.TourRepository;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.NetworkStateHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.helpers.ProfileHolder;
import app.karacal.interfaces.ActionCallback;
import app.karacal.models.Album;
import app.karacal.models.Comment;
import app.karacal.models.Guide;
import app.karacal.models.Player;
import app.karacal.models.Tour;
import app.karacal.models.Track;
import app.karacal.network.models.response.CommentsResponse;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

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


    private SingleLiveEvent<Void> listenAction = new SingleLiveEvent<>();
    private SingleLiveEvent<Long> paymentAction = new SingleLiveEvent<>();
    private SingleLiveEvent<Void> tourDownloadedAction = new SingleLiveEvent<>();
    private SingleLiveEvent<String> downloadingErrorAction = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorAction = new SingleLiveEvent<>();
    private MutableLiveData<List<Comment>> commentsLiveData = new MutableLiveData<>();
    private MutableLiveData<Guide> guideLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> tourDownloadingLiveData = new MutableLiveData<>();

    public SingleLiveEvent<Void> getListenAction() {
        return listenAction;
    }

    public SingleLiveEvent<Long> getPaymentAction() {
        return paymentAction;
    }

    public SingleLiveEvent<Void> getTourDownloadedAction() {
        return tourDownloadedAction;
    }

    public MutableLiveData<Boolean> getTourDownloading() {
        return tourDownloadingLiveData;
    }

    public SingleLiveEvent<String> getErrorAction() {
        return errorAction;
    }

    public SingleLiveEvent<String> getDownloadingErrorAction() {
        return downloadingErrorAction;
    }

    public LiveData<List<Comment>> getComments(){
        return commentsLiveData;
    }

    public LiveData<Guide> getGuide(){
        return guideLiveData;
    }

    private final Tour tour;

    private final Guide author;

    private final MutableLiveData<Album> album;

    private final Player player;

    public AudioActivityViewModel(int tourId) {
        Log.v("AudioActivityViewModel", "tourId = "+tourId);
        App.getAppComponent().inject(this);
        tour = tourRepository.getTourById(tourId);
        author = guideRepository.getGuide(tour.getAuthorId());
        if (tour.getAudio() != null){
            album = new MutableLiveData<>();
        } else {
            album = albumRepository.albumLiveData;
        }
        player = new Player(album);
    }

    public Tour getTour() {
        return tour;
    }

    public Player getPlayer() {
        return player;
    }

    public int getGuideId(){
        return tour.getAuthorId();
    }

    public int getCountGuides(){
        if (author != null) {
            return tourRepository.getToursByAuthor(author.getId()).size();
        } else {
            return 5;
        }
    }

    public void loadTracks() {
        if (tour.getAudio() != null){
            album.setValue(tour.getAlbum());
        } else {
            albumRepository.loadTracksByTour(String.valueOf(tour.getId()));
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
        if(tour.getPrice() == 0){
            successCallback.invoke();
        } else {
            if (profileHolder.isHasSubscription() || profileHolder.isPurchasesContainsTour(tour.getId())) {
                Log.v("checkAccess", "has Subscription");
                successCallback.invoke();
            } else {
                paymentAction.setValue(tour.getPrice());
            }
        }
    }

    public void loadComments(){
        List<Comment> list = commentsLiveData.getValue();
        if (list == null || list.isEmpty()) {
            disposable.add(apiHelper.loadComments(PreferenceHelper.loadToken(App.getInstance()), tour.getId())
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

    public void downloadTour(Context context) {
        tourDownloadingLiveData.setValue(true);
        if(DownloadedToursCache.getInstance(context).containsTour(tour.getId())) {
            tourDownloadingLiveData.setValue(false);
            downloadingErrorAction.setValue(App.getResString(R.string.tour_already_downloaded));
        } else {
            if (PreferenceHelper.isDownloadOnlyViaWifi(context) &&
                !NetworkStateHelper.isNetworkAvailable(context, WIFI_INTERNET)){
                tourDownloadingLiveData.setValue(false);
                downloadingErrorAction.setValue(App.getResString(R.string.turn_on_wifi));
            } else {

                checkAccess(() -> disposable.add(Single.just(tour.getAudio())
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
                            } else {
                                downloadingErrorAction.setValue(App.getResString(R.string.error_download_tour));
                            }
                        }, throwable -> {
                            tourDownloadingLiveData.setValue(false);
                            downloadingErrorAction.setValue(App.getResString(R.string.connection_problem));
                        })));
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
            tour.setAudio(tracks);
            DownloadedToursCache.getInstance(App.getInstance()).addDownloadedTour(App.getInstance(), tour);
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

    public void loadAuthor(){
        disposable.add(apiHelper.loadGuide(PreferenceHelper.loadToken(App.getInstance()), String.valueOf(tour.getAuthorId()))
                .subscribe(response ->
                    guideLiveData.setValue(new Guide(response))
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


}
