package app.karacal.helpers;

import android.annotation.SuppressLint;
import android.location.Location;

import com.google.android.gms.location.LocationServices;

import app.karacal.App;
import app.karacal.data.LocationCache;
import io.reactivex.Single;

public class LocationHelper {

    @SuppressLint("MissingPermission")
    public static Single<Location> getLastKnownLocation() {
        return Single.create(emitter -> LocationServices.getFusedLocationProviderClient(App.getInstance())
                .getLastLocation()
                .addOnSuccessListener(location -> {
                    if (!emitter.isDisposed()) {
                        if (location != null) {
                            LocationCache.getInstance(App.getInstance()).setLocation(App.getInstance(), location);
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
