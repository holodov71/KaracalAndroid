package app.karacal.viewmodels;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.data.repository.GuideRepository;
import app.karacal.data.repository.TourRepository;
import app.karacal.models.Guide;
import app.karacal.models.Tour;

public class MainActivityViewModel extends BaseLocationViewModel {

    public static class MainActivityViewModelFactory extends ViewModelProvider.NewInstanceFactory {

        public MainActivityViewModelFactory() {

        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == MainActivityViewModel.class) {
                return (T) new MainActivityViewModel();
            }
            return null;
        }
    }

    @Inject
    TourRepository tourRepository;

    @Inject
    GuideRepository guideRepository;

    private MutableLiveData<ArrayList<Tour>> nearTours = new MutableLiveData<>();

    public MainActivityViewModel() {
        App.getAppComponent().inject(this);
    }

    public LiveData<ArrayList<Tour>> getNearTours() {
        return nearTours;
    }

    public void obtainNearTours(Location location){
        nearTours.setValue(tourRepository.getNearTours(location));
    }

    public void loadGuides() {
        guideRepository.loadGuides();
    }

    public LiveData<List<Guide>> getGuides(){
        return guideRepository.guidesLiveData;
    }

    public void loadTours() {
        tourRepository.loadTours();
    }

    public LiveData<List<Tour>> getTours(){
        return tourRepository.toursLiveData;
    }


}
