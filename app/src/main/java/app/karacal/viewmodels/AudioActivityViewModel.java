package app.karacal.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.data.repository.AlbumRepository;
import app.karacal.data.repository.GuideRepository;
import app.karacal.data.repository.TourRepository;
import app.karacal.models.Album;
import app.karacal.models.Guide;
import app.karacal.models.Player;
import app.karacal.models.Tour;

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
    TourRepository tourRepository;

    @Inject
    GuideRepository guideRepository;

    @Inject
    AlbumRepository albumRepository;

    private final Tour tour;

    private final Guide author;

    private final Album album;

    private final Player player;

    public AudioActivityViewModel(int tourId) {
        Log.v("AudioActivityViewModel", "tourId = "+tourId);
        App.getAppComponent().inject(this);
        tour = tourRepository.getTourById(tourId);
        author = guideRepository.getGuide(tour.getAuthorId());
        album = albumRepository.getAlbumByTourId(tourId);
        player = new Player(album);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        player.dispose();
    }

    public Tour getTour() {
        return tour;
    }

//    public Album getAlbum() {
//        return album;
//    }

    public Player getPlayer() {
        return player;
    }

    public Guide getAuthor(){
        return author;
    }

    public int getCountGuides(){
        return tourRepository.getToursByAuthor(author.getId()).size();
    }

    public void loadTracks() {
        albumRepository.loadTracksByTour(String.valueOf(tour.getId()));
    }

    public LiveData<Album> getAlbum(){
        return albumRepository.albumLiveData;
    }

    public String getTrackTitle(int position){
        return albumRepository.getTrackTitle(position);
    }
}
