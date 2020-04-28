package app.karacal.viewmodels;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.TimeUnit;

import app.karacal.helpers.LocationHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SettingsActivityViewModel extends ViewModel {

    public interface LocationObtainListener {
        void onLocationUpdated(String location);
    }

    private class LocationUpdateHandler {
        private final LocationObtainListener locationObtainListener;
        private final LifecycleOwner locationObtainListenerOwner;

        private LocationUpdateHandler(LifecycleOwner locationObtainListenerOwner, LocationObtainListener locationObtainListener) {
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

    private static final int GEO_CODING_TIMEOUT = 5;
    private MutableLiveData<Boolean> isGeoCodingInProgress = new MutableLiveData<>(false);
    private LocationUpdateHandler locationUpdateHandler;

    public LiveData<Boolean> getGeoCodingState() {
        return isGeoCodingInProgress;
    }

    public boolean isGeoCodingInProgress() {
        return isGeoCodingInProgress.getValue() != null ? isGeoCodingInProgress.getValue() : false;
    }

    public void subscribeLocationUpdates(LifecycleOwner lifecycleOwner, LocationObtainListener listener) {
        locationUpdateHandler = new LocationUpdateHandler(lifecycleOwner, listener);
    }

    public void obtainLocation() {
        LocationHelper.getLastKnownLocation().timeout(GEO_CODING_TIMEOUT, TimeUnit.SECONDS)
                .map(location -> String.format("%f, %f", location.getLatitude(), location.getLongitude()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> isGeoCodingInProgress.postValue(true))
                .subscribe(locationText -> {
                            isGeoCodingInProgress.postValue(false);
                            if (locationUpdateHandler != null) {
                                locationUpdateHandler.notifyLocationUpdate(locationText);
                            }
                        },
                        throwable -> {
                            isGeoCodingInProgress.postValue(false);
                            if (locationUpdateHandler != null) {
                                locationUpdateHandler.notifyLocationUpdate(null);
                            }
                        });
    }
}
