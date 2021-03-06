package app.karacal.viewmodels;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.data.ProfileCache;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.FileHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.models.Profile;
import io.reactivex.disposables.CompositeDisposable;

public class DashboardActivityViewModel extends ViewModel {

    private CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    ApiHelper apiHelper;

    private SingleLiveEvent<String> avatarUploadedAction = new SingleLiveEvent<>();
    private SingleLiveEvent<String> avatarUploadingErrorAction = new SingleLiveEvent<>();
    private MutableLiveData<Boolean> avatarUploadingLiveData = new MutableLiveData<>();
    private SingleLiveEvent<String> errorAction = new SingleLiveEvent<>();

    public SingleLiveEvent<String> getAvatarUploadedAction() {
        return avatarUploadedAction;
    }

    public SingleLiveEvent<String> getAvatarUploadingErrorAction() {
        return avatarUploadingErrorAction;
    }

    public MutableLiveData<Boolean> getAvatarUploadingLiveData() {
        return avatarUploadingLiveData;
    }

    public DashboardActivityViewModel() {
        App.getAppComponent().inject(this);
    }

    public Profile getProfile(Context context){
        return ProfileCache.getInstance(context).getProfile();
    }


    public void changeAvatar(Context context, Uri resultUri) {
        String filePath = FileHelper.getRealImagePathFromUri(context, resultUri);
        avatarUploadingLiveData.setValue(true);
        disposable.add(apiHelper.changeAvatar(PreferenceHelper.loadToken(context), filePath)
                .subscribe(response -> {
                            avatarUploadingLiveData.setValue(false);
                            if (response.isSuccess()){
                                avatarUploadedAction.setValue(response.getUrl());
                                Profile profile = ProfileCache.getInstance(context).getProfile();
                                profile.setAvatar(response.getUrl());
                                ProfileCache.getInstance(context).setProfile(context, profile);
                            } else {
                                avatarUploadingErrorAction.setValue(response.getErrorMessage());
                            }
                },
                throwable -> {
                    avatarUploadingLiveData.setValue(false);
                    avatarUploadingErrorAction.setValue(App.getResString(R.string.error_uploading_avatar));
                }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }
}
