package app.karacal.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.data.repository.GuideRepository;
import app.karacal.data.repository.TourRepository;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.helpers.ProfileHolder;
import app.karacal.models.Guide;
import app.karacal.models.Tour;
import io.reactivex.disposables.CompositeDisposable;

import static app.karacal.App.getResString;

public class ProfileActivityViewModel extends ViewModel {

    public static class ProfileViewModelFactory extends ViewModelProvider.NewInstanceFactory {

        private final int guideId;

        public ProfileViewModelFactory(int guideId) {
            this.guideId = guideId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == ProfileActivityViewModel.class) {
                return (T) new ProfileActivityViewModel(guideId);
            }
            return null;
        }
    }

    @Inject
    ProfileHolder profileHolder;

    @Inject
    TourRepository tourRepository;

    @Inject
    ApiHelper apiHelper;

    private CompositeDisposable disposable = new CompositeDisposable();

    private final int guideId;
    private MutableLiveData<Guide> guideLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Tour>> toursLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public SingleLiveEvent<Void> ratingSavedAction = new SingleLiveEvent<>();
    public SingleLiveEvent<String> onErrorAction = new SingleLiveEvent<>();


    public ProfileActivityViewModel(int guideId) {
        Log.v("ProfileViewModel", "guideId = "+guideId);
        App.getAppComponent().inject(this);
        this.guideId = guideId;
    }

    public LiveData<Guide> getGuide(){
        return guideLiveData;
    }

    public int getGuideId() {
        return guideId;
    }

    public LiveData<List<Tour>> getTours(){
        return toursLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }


    public void loadData(){
        loadGuide();
        loadTours();
    }

    private void loadGuide(){
        disposable.add(apiHelper.loadGuide(PreferenceHelper.loadToken(App.getInstance()), String.valueOf(guideId))
            .subscribe(response -> guideLiveData.setValue(new Guide(response)),
                throwable -> guideLiveData.setValue(null)
            ));
    }

    private void loadTours(){
        disposable.add(tourRepository.loadToursByAuthor(guideId)
                .subscribe(
                        response -> toursLiveData.setValue(response),
                        throwable -> toursLiveData.setValue(new ArrayList<>())
                ));

//        toursLiveData.setValue(tourRepository.getToursByAuthor(guideId));
    }

    public void setRating(int rating) {
        isLoading.setValue(true);
        disposable.add(apiHelper.setRatingForGuide(guideId, rating)
                .subscribe(response -> {
                            if (response.isSuccess()){
                                ratingSavedAction.call();
                            } else {
                                onErrorAction.setValue(getResString(R.string.common_error));
                            }
                            isLoading.setValue(false);
                        },
                        throwable -> {
                            onErrorAction.setValue(getResString(R.string.common_error));
                            isLoading.setValue(false);
                        }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }
}
