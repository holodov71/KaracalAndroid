package app.karacal.helpers;

import android.location.Location;

import com.google.android.gms.location.LocationServices;

import app.karacal.App;
import app.karacal.data.NotificationsSchedule;
import io.reactivex.Single;

public class LocationHelper {

    public static Single<Location> getLastKnownLocation() {
        return Single.create(emitter -> LocationServices.getFusedLocationProviderClient(App.getInstance())
                .getLastLocation()
                .addOnSuccessListener(location -> {
                    if (!emitter.isDisposed()) {
                        if (location != null) {
                            App.getInstance().setLastLocation(location);
                            emitter.onSuccess(location);
                        } else {
                            emitter.onError(new Exception("Location provider returned NULL"));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                    }
                }));
    }

}
