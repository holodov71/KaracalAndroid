package app.karacal;

import android.app.Application;
import android.location.Location;

import app.karacal.dagger.AppComponent;
import app.karacal.dagger.DaggerAppComponent;
import apps.in.android_logger.Logger;

import com.onesignal.OneSignal;
import com.stripe.android.PaymentConfiguration;

public class App extends Application {

    public final static String TAG = "KARAKAL";
    private final static String API_BASE_URL = "http://karacal.store:8080/api2/";

    private static App instance;

    private AppComponent appComponent;

    private Location lastLocation = null;

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appComponent = DaggerAppComponent.builder().application(this).build();
        Logger.initializeLogger(this)
                .writeToConsole("KARACAL")
                .writeToFile()
                .initialize();
        Logger.log(String.format("App: %s", BuildConfig.APPLICATION_ID));
        Logger.log(String.format("Version: %s", BuildConfig.VERSION_NAME));

        // Payment Initialization
        PaymentConfiguration.init(
                getApplicationContext(),
                getString(R.string.stripe_api_key)
        );

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

    }

    public static App getInstance() {
        return instance;
    }

    public static String getResString(int resId) {
        return instance.getResources().getString(resId);
    }

    public static AppComponent getAppComponent() {
        return instance.appComponent;
    }

    public static String getApiBaseUrl(){
        return instance.API_BASE_URL;
    }
}
