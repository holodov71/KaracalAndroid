package app.karacal.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.data.repository.AlbumRepository;
import app.karacal.data.repository.GuideRepository;
import app.karacal.data.repository.TourRepository;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.helpers.ProfileHolder;
import app.karacal.helpers.ToastHelper;
import app.karacal.helpers.TokenHelper;
import app.karacal.models.Album;
import app.karacal.models.Guide;
import app.karacal.models.Player;
import app.karacal.models.Tour;
import io.reactivex.disposables.Disposable;

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

    private Disposable disposable;


    private SingleLiveEvent<Void> listenAction = new SingleLiveEvent<>();
    private SingleLiveEvent<Long> paymentAction = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorAction = new SingleLiveEvent<>();

    public SingleLiveEvent<Void> getListenAction() {
        return listenAction;
    }

    public SingleLiveEvent<Long> getPaymentAction() {
        return paymentAction;
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

    public Guide getAuthor(){
        return author;
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
        if (profileHolder.isHasSubscription()){
            Log.v("onListenTourClicked", "has Subscription");
            listenAction.call();
        } else {
            if(disposable != null){
                disposable.dispose();
            }
            disposable = apiHelper.loadTourById(PreferenceHelper.loadToken(App.getInstance()), tour.getId())
                    .subscribe(response -> {
                        if (response.isSuccess()) {
                            if (response.isPaid()){
                                listenAction.call();
                            } else {
                                paymentAction.setValue(tour.getPrice());
                            }
                        } else {
                            errorAction.setValue(response.getErrorMessage());
                        }
                    }, throwable -> {
                        errorAction.setValue(App.getResString(R.string.connection_problem));
                    });
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        player.dispose();
        if (disposable != null){
            disposable.dispose();
        }
    }


}
