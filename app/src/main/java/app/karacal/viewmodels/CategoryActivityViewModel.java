package app.karacal.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.data.DownloadedToursCache;
import app.karacal.data.repository.TourRepository;
import app.karacal.models.Tour;
import app.karacal.models.TourCategory;

public class CategoryActivityViewModel extends ViewModel {

    public static class CategoryActivityViewModelFactory extends ViewModelProvider.NewInstanceFactory {

        private final TourCategory category;

        public CategoryActivityViewModelFactory(TourCategory category) {
            this.category = category;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == CategoryActivityViewModel.class) {
                return (T) new CategoryActivityViewModel(category);
            }
            return null;
        }
    }

    public enum CategoryViewMode{
        LIST,
        STACK
    }

    @Inject
    TourRepository tourRepository;

    private LiveData<List<Tour>> tours;

    private final MutableLiveData<CategoryViewMode> viewModeMutableLiveData = new MutableLiveData<>(CategoryViewMode.LIST);

    public CategoryActivityViewModel(TourCategory category) {
        App.getAppComponent().inject(this);
        switch (category){
            case CATEGORY_RECOMMENDED:
                tours = tourRepository.recommendedToursLiveData;
                break;
            case CATEGORY_NEAR:
                tours = tourRepository.nearToursLiveData;
                break;
            case CATEGORY_ORIGINAL:
                tours = tourRepository.originalToursLiveData;
                break;
            case CATEGORY_DOWNLOADED:
                MutableLiveData<List<Tour>> tmpTours = new MutableLiveData<>();
                tmpTours.setValue(DownloadedToursCache.getInstance(App.getInstance()).getToursList());
                tours = tmpTours;
                break;
        }
    }

    public LiveData<List<Tour>> getTours() {
        return tours;
    }

    public LiveData<CategoryViewMode> getViewModeLiveData() {
        return viewModeMutableLiveData;
    }

    public void setViewMode(CategoryViewMode viewMode){
        if (viewMode != viewModeMutableLiveData.getValue()){
            viewModeMutableLiveData.setValue(viewMode);
        }
    }
}
