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
import app.karacal.data.repository.GuideRepository;
import app.karacal.data.repository.TourRepository;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.helpers.ProfileHolder;
import app.karacal.models.Guide;
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

    private long lastObtainedLocation = 0;
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

    @Inject
    ProfileHolder profileHolder;

    private MutableLiveData<ArrayList<Tour>> nearTours = new MutableLiveData<>();

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
                    Log.v("loadSubscriptions", "Success response = " + response);
                    if (response.getSubscriptions() != null && !response.getSubscriptions().isEmpty()){
                        for (SubscriptionWrapper subs: response.getSubscriptions()){
                            if (subs.getStatus().equalsIgnoreCase(STATUS_SUBSCRIPTION_ACTIVE)){
                                profileHolder.setSubscription(subs.getId());
                                break;
                            }
                        }
                    }
                    if (response.getIdTours() != null && !response.getIdTours().isEmpty()){
                        profileHolder.setTourPurchases(response.getIdTours());
                    }
                }, throwable -> {
                    Log.v("loadSubscriptions", "Error loading");
                }));
    }

    private void checkIsGuide(String serverToken){
        GuideByEmailRequest request = new GuideByEmailRequest(profileHolder.getProfile().getEmail());

        disposable.add(apiHelper.getGuide(serverToken, request)
                .subscribe(response -> {
                    profileHolder.setGuide(response.isGuide(), response.getId());
                }, throwable -> {
                    Log.v("checkIsGuide", "Error loading");
                }));
    }

    public void startObtainLocation(){
        locationTimerHandler.removeCallbacks(locationTimerRunnable);
        locationTimerHandler.postDelayed(locationTimerRunnable, 100);
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
