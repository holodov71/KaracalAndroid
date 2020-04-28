package app.karacal.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.data.AlbumRepository;
import app.karacal.data.TourRepository;
import app.karacal.models.Album;
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
    AlbumRepository albumRepository;

    private final Tour tour;

    private final Album album;

    private final Player player;

    public AudioActivityViewModel(int tourId) {
        App.getAppComponent().inject(this);
        tour = tourRepository.getTourById(tourId);
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

    public Album getAlbum() {
        return album;
    }

    public Player getPlayer() {
        return player;
    }
}
