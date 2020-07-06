package app.karacal.viewmodels;

import android.location.Location;
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
import app.karacal.network.models.response.SubscriptionWrapper;
import io.reactivex.disposables.Disposable;

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

    private Disposable disposable;

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

    public void checkSubscriptions() {
        if (disposable != null){
            disposable.dispose();
        }

        String serverToken = PreferenceHelper.loadToken(App.getInstance());

        disposable = apiHelper.getPurchases(serverToken)
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
                });

//        CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest(profileHolder.getProfile().getEmail());
//        disposable = apiHelper.createCustomer(serverToken, createCustomerRequest)
//                .map(CreateCustomerResponse::getId)
//                .flatMap(customerId -> apiHelper.loadSubscriptions(serverToken, customerId))
//                .subscribe(response -> {
//                    Log.v("loadSubscriptions", "Success response = " + response);
//                    if (response.isSuccess()) {
//                        if (response.getSubscriptions() != null && !response.getSubscriptions().isEmpty()){
//                            for (SubscriptionsListResponse.Subscription subs: response.getSubscriptions()){
//                                if (subs.getStatus().equalsIgnoreCase(STATUS_SUBSCRIPTION_ACTIVE)){
//                                    profileHolder.setSubscription(subs.getId());
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                }, throwable -> {
//                    Log.v("loadSubscriptions", "Error loading");
//                });

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null){
            disposable.dispose();
        }
    }
}
