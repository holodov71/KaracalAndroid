package app.karacal.viewmodels;

import android.annotation.SuppressLint;
import android.location.Location;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.data.repository.TourRepository;
import app.karacal.helpers.LocationHelper;
import app.karacal.network.models.request.SaveTourRequest;
import app.karacal.network.models.response.SaveTourResponse;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class EditGuideActivityViewModel extends ViewModel {

    public interface LocationObtainListener {
        void onLocationUpdated(String location);
    }

    private class LocationUpdateHandler {
        private final EditGuideActivityViewModel.LocationObtainListener locationObtainListener;
        private final LifecycleOwner locationObtainListenerOwner;

        private LocationUpdateHandler(LifecycleOwner locationObtainListenerOwner, EditGuideActivityViewModel.LocationObtainListener locationObtainListener) {
            this.locationObtainListener = locationObtainListener;
            this.locationObtainListenerOwner = locationObtainListenerOwner;
        }

        private void notifyLocationUpdate(String location) {
            Lifecycle.State currentState = locationObtainListenerOwner.getLifecycle().getCurrentState();
            if (currentState == Lifecycle.State.STARTED || currentState == Lifecycle.State.RESUMED) {
                locationObtainListener.onLocationUpdated(location);
            }
        }
    }

    private LocationUpdateHandler locationUpdateHandler;

    private static final int GEO_CODING_TIMEOUT = 5;
    private MutableLiveData<Boolean> isGeoCodingInProgress = new MutableLiveData<>(false);

    public LiveData<Boolean> getGeoCodingState() {
        return isGeoCodingInProgress;
    }

    public boolean isGeoCodingInProgress() {
        return isGeoCodingInProgress.getValue() != null ? isGeoCodingInProgress.getValue() : false;
    }

    public void subscribeLocationUpdates(LifecycleOwner lifecycleOwner, EditGuideActivityViewModel.LocationObtainListener listener) {
        locationUpdateHandler = new EditGuideActivityViewModel.LocationUpdateHandler(lifecycleOwner, listener);
    }

    @Inject
    TourRepository tourRepository;

    private Location locationCoordinates;

    private String title;
    private String location;
    private String description;

    private HashSet<String> tags = new HashSet<>();

    public EditGuideActivityViewModel() {
        App.getAppComponent().inject(this);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashSet<String> getTags() {
        return tags;
    }

    public boolean addTag(String tag){
        return tags.add(tag);
    }

    public boolean removeTag(String tag){
        return tags.remove(tag);
    }

    @SuppressLint("CheckResult")
    public void obtainLocation() {
        LocationHelper.getLastKnownLocation().timeout(GEO_CODING_TIMEOUT, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> isGeoCodingInProgress.postValue(true))
                .subscribe(loc -> {
                            locationCoordinates = loc;
                            isGeoCodingInProgress.postValue(false);
                            location = String.format(Locale.ROOT, "%f, %f", loc.getLatitude(), loc.getLongitude());
                            if (locationUpdateHandler != null) {
                                locationUpdateHandler.notifyLocationUpdate(location);
                            }
                        },
                        throwable -> {
                            isGeoCodingInProgress.postValue(false);
                            if (locationUpdateHandler != null) {
                                locationUpdateHandler.notifyLocationUpdate(null);
                            }
                        });
    }

    public Observable<SaveTourResponse> saveTour() {
        SaveTourRequest request = new SaveTourRequest(
                "",
                "Paris",
                locationCoordinates!= null ? String.valueOf(locationCoordinates.getLatitude()) : "",
                locationCoordinates!= null ? String.valueOf(locationCoordinates.getLongitude()) : "",
                title,
                description,
                "",
                "0",
                "1");
        return tourRepository.saveTour(request);
    }
}
