package app.karacal.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.models.Comment;
import app.karacal.network.models.request.CreateCommentRequest;
import app.karacal.network.models.response.CommentsResponse;
import io.reactivex.disposables.CompositeDisposable;

public class CommentsActivityViewModel extends ViewModel {

    public static class CommentsActivityViewModelFactory extends ViewModelProvider.NewInstanceFactory {

        private final int tourId;

        public CommentsActivityViewModelFactory(int tourId) {
            this.tourId = tourId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == CommentsActivityViewModel.class) {
                return (T) new CommentsActivityViewModel(tourId);
            }
            return null;
        }
    }

    @Inject
    ApiHelper apiHelper;

    private CompositeDisposable disposable = new CompositeDisposable();

    private final int tourId;
    private MutableLiveData<List<Comment>> comments = new MutableLiveData<>();
    private SingleLiveEvent<String> errorEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Comment> newCommentEvent = new SingleLiveEvent<>();

    public CommentsActivityViewModel(int tourId) {
        App.getAppComponent().inject(this);
        this.tourId = tourId;
    }

    public LiveData<List<Comment>> getCommentsLiveData() {
        return comments;
    }

    public SingleLiveEvent<String> getError() {
        return errorEvent;
    }

    public SingleLiveEvent<Comment> getNewComment() {
        return newCommentEvent;
    }

    public void loadComments(){
        disposable.add(apiHelper.loadComments(PreferenceHelper.loadToken(App.getInstance()), tourId)
                .flatMapIterable(CommentsResponse::getComments)
                .map(Comment::new)
                .toList()
                .subscribe(response -> {
                    comments.setValue(response);
                }, throwable -> {
                    errorEvent.setValue(App.getResString(R.string.connection_problem));
                }));
    }

    public void createNewComment(String text) {
        CreateCommentRequest request = new CreateCommentRequest(tourId, text);
        disposable.add(apiHelper.createNewComment(PreferenceHelper.loadToken(App.getInstance()), request)
                .subscribe(response -> {
                    if (response.isSuccess()) {
                        loadComments();
                    } else {
                        errorEvent.setValue(App.getResString(R.string.common_error));
                    }
                }, throwable -> {
                    errorEvent.setValue(App.getResString(R.string.connection_problem));
                }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }
}
