package app.karacal.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import app.karacal.models.SearchFilter;

public class SearchFilterActivityViewModel extends ViewModel {

    public static class SearchFilterActivityViewModelFactory extends ViewModelProvider.NewInstanceFactory {

        private final SearchFilter searchFilter;

        public SearchFilterActivityViewModelFactory(SearchFilter searchFilter) {
            this.searchFilter = searchFilter;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == SearchFilterActivityViewModel.class) {
                return (T) new SearchFilterActivityViewModel(searchFilter);
            }
            return null;
        }
    }

    private SearchFilter searchFilter;
    
    private SearchFilterActivityViewModel(SearchFilter searchFilter){
        this.searchFilter = searchFilter;
    }

    public SearchFilter getSearchFilter() {
        return searchFilter;
    }
}
