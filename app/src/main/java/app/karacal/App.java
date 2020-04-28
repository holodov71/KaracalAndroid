package app.karacal;

import android.app.Application;

import app.karacal.dagger.AppComponent;
import app.karacal.dagger.DaggerAppComponent;
import apps.in.android_logger.Logger;

public class App extends Application {

    private final static String API_BASE_URL = "http://karacal.store:8080/api2/";

    private static App instance;

    private AppComponent appComponent;


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
    }

    public static App getInstance() {
        return instance;
    }

    public static AppComponent getAppComponent() {
        return instance.appComponent;
    }

    public static String getApiBaseUrl(){
        return instance.API_BASE_URL;
    }
}
