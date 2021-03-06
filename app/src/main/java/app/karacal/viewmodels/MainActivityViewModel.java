package app.karacal.viewmodels;

import android.location.Location;
import android.os.Handler;
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
import app.karacal.data.ProfileCache;
import app.karacal.data.repository.GuideRepository;
import app.karacal.data.repository.TourRepository;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.models.Guide;
import app.karacal.models.Tag;
import app.karacal.models.Tour;
import app.karacal.network.models.request.GuideByEmailRequest;
import app.karacal.network.models.response.SubscriptionWrapper;
import io.reactivex.disposables.CompositeDisposable;

import static app.karacal.network.models.response.SubscriptionsListResponse.STATUS_SUBSCRIPTION_ACTIVE;

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

    public static final int OBTAIN_LOCATION_INTERVAL = 60 * 1000;// one minute

    Handler locationTimerHandler = new Handler();
    Runnable locationTimerRunnable = new Runnable() {
        @Override
        public void run() {
            obtainLocation();
            locationTimerHandler.postDelayed(this, OBTAIN_LOCATION_INTERVAL);
        }
    };

    private CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    TourRepository tourRepository;

    @Inject
    GuideRepository guideRepository;

    @Inject
    ApiHelper apiHelper;

    private MutableLiveData<ArrayList<Tour>> nearTours = new MutableLiveData<>();
    private MutableLiveData<List<Tag>> tagsLiveData = new MutableLiveData<>();

    public MainActivityViewModel() {
        App.getAppComponent().inject(this);
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
        return tourRepository.recommendedToursLiveData;
    }

    public LiveData<List<Tour>> getNearTours() {
        return tourRepository.nearToursLiveData;
    }

    public LiveData<List<Tag>> getTags() {
        return tagsLiveData;
    }

    public void loadNearTours(Location location){
        tourRepository.loadNearTours(location.getLatitude(), location.getLongitude());
    }

    public LiveData<List<Tour>> getOriginalTours() {
        return tourRepository.originalToursLiveData;
    }

    public void loadOriginalTours() {
        tourRepository.loadContents();
    }

    public void processUser(){
        String serverToken = PreferenceHelper.loadToken(App.getInstance());
        checkSubscriptions(serverToken);
        checkIsGuide(serverToken);
    }

    private void checkSubscriptions(String serverToken) {

        disposable.add(apiHelper.getPurchases(serverToken)
                .subscribe(response -> {
                    if (response.getSubscriptions() != null && !response.getSubscriptions().isEmpty()){
                        for (SubscriptionWrapper subs: response.getSubscriptions()){
                            if (subs.getStatus().equalsIgnoreCase(STATUS_SUBSCRIPTION_ACTIVE)){
                                ProfileCache.getInstance(App.getInstance()).setSubscriptionId(App.getInstance(), subs.getId());
                                break;
                            }
                        }
                    }
                    if (response.getIdTours() != null && !response.getIdTours().isEmpty()){
                        ProfileCache.getInstance(App.getInstance()).setTourPurchases(App.getInstance(), response.getIdTours());
                    }
                }, throwable -> {
                    Log.e("loadSubscriptions", "Error loading");
                }));
    }

    private void checkIsGuide(String serverToken){
        String email = ProfileCache.getInstance(App.getInstance()).getProfile().getEmail();

        GuideByEmailRequest request = new GuideByEmailRequest(email);

        disposable.add(apiHelper.getGuide(serverToken, request)
                .subscribe(response -> {
                    ProfileCache.getInstance(App.getInstance()).setGuide(App.getInstance(), response.isGuide(), response.getId());
                }, throwable -> {
                    Log.e("checkIsGuide", "Error loading");
                }));
    }

    public void startObtainLocation(){
        locationTimerHandler.removeCallbacks(locationTimerRunnable);
        locationTimerHandler.postDelayed(locationTimerRunnable, 100);
    }

    public void obtainTags() {
        disposable.add(apiHelper.loadTags(PreferenceHelper.loadToken(App.getInstance()))
                .subscribe(response -> {
                    tagsLiveData.setValue(response);
                }, throwable -> {
                    Log.e("checkIsGuide", "Error loading");
                }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null){
            disposable.dispose();
        }
        locationTimerHandler.removeCallbacks(locationTimerRunnable);
    }
}
