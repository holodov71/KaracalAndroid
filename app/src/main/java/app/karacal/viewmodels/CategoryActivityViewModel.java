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
import app.karacal.data.repository.TourRepository;
import app.karacal.models.Tour;

public class CategoryActivityViewModel extends ViewModel {

    public static class CategoryActivityViewModelFactory extends ViewModelProvider.NewInstanceFactory {

        private final int categoryId;

        public CategoryActivityViewModelFactory(int categoryId) {
            this.categoryId = categoryId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == CategoryActivityViewModel.class) {
                return (T) new CategoryActivityViewModel(categoryId);
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

    public CategoryActivityViewModel(int categoryId) {
        App.getAppComponent().inject(this);
        switch (categoryId){
            case 0:
                tours = tourRepository.recommendedToursLiveData;
                break;
            case 1:
                tours = tourRepository.nearToursLiveData;
                break;
            case 2:
                tours = tourRepository.originalToursLiveData;
                break;
            default:
                throw new RuntimeException("Unknown category");
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
