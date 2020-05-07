package app.karacal.viewmodels;

import android.location.Location;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.TimeUnit;

import app.karacal.helpers.LocationHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class BaseLocationViewModel extends ViewModel {

    public interface LocationObtainListener {
        void onLocationUpdated(Location location);
    }

    private class LocationUpdateHandler {
        private final LocationObtainListener locationObtainListener;
        private final LifecycleOwner locationObtainListenerOwner;

        private LocationUpdateHandler(LifecycleOwner locationObtainListenerOwner, LocationObtainListener locationObtainListener) {
            this.locationObtainListener = locationObtainListener;
            this.locationObtainListenerOwner = locationObtainListenerOwner;
        }

        private void notifyLocationUpdate(Location location) {
            Lifecycle.State currentState = locationObtainListenerOwner.getLifecycle().getCurrentState();
            if (currentState == Lifecycle.State.STARTED || currentState == Lifecycle.State.RESUMED) {
                locationObtainListener.onLocationUpdated(location);
            }
        }
    }

    private static final int GEO_CODING_TIMEOUT = 5;
    private MutableLiveData<Boolean> isGeoCodingInProgress = new MutableLiveData<>(false);
    private LocationUpdateHandler locationUpdateHandler;
    private CompositeDisposable mDisposable = new CompositeDisposable();

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
        mDisposable.add(LocationHelper.getLastKnownLocation().timeout(GEO_CODING_TIMEOUT, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> isGeoCodingInProgress.postValue(true))
                .subscribe(location -> {
                            isGeoCodingInProgress.postValue(false);
                            if (locationUpdateHandler != null) {
                                locationUpdateHandler.notifyLocationUpdate(location);
                            }
                        },
                        throwable -> {
                            isGeoCodingInProgress.postValue(false);
                            if (locationUpdateHandler != null) {
                                locationUpdateHandler.notifyLocationUpdate(null);
                            }
                        }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mDisposable != null){
            mDisposable.dispose();
        }
    }
}
