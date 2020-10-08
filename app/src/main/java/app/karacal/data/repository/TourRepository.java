package app.karacal.data.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.karacal.App;
import app.karacal.data.DownloadedToursCache;
import app.karacal.data.dao.ToursDao;
import app.karacal.data.entity.TourEntity;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.NotificationHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.models.Tour;
import app.karacal.network.models.request.NearToursRequest;
import app.karacal.network.models.request.SaveTourRequest;
import app.karacal.network.models.response.SaveTourResponse;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class TourRepository {

    public static final int IMPORTANT_DISTANCE = 200;

    @Inject
    ApiHelper apiHelper;

    private ToursDao toursDao;
    private Context context;

    private final MutableLiveData<ArrayList<Tour>> toursList = new MutableLiveData<>();
    public MutableLiveData<List<Tour>> toursLiveData = new MutableLiveData<>();
    public MutableLiveData<List<Tour>> recommendedToursLiveData = new MutableLiveData<>();
    public MutableLiveData<List<Tour>> originalToursLiveData = new MutableLiveData<>();
    public MutableLiveData<List<Tour>> nearToursLiveData = new MutableLiveData<>();

    private Location lastLocation = new Location("dummyprovider");

    @Inject
    public TourRepository(ToursDao toursDao, App context) {

        this.toursDao = toursDao;
        this.context = context;


    }

    public void getOriginalToursList() {
        toursDao.getAllTours().observeForever(new Observer<List<TourEntity>>() {
            @Override
            public void onChanged(List<TourEntity> tourEntities) {
                ArrayList<Tour> tours = new ArrayList<>();
                for (TourEntity tour: tourEntities){
                    tours.add(new Tour(tour));
                }
                toursList.setValue(tours);
            }
        });
    }

    public Tour getTourById(int tourId) {
        Tour downloadedTour = DownloadedToursCache.getInstance(App.getInstance()).getDownloadedTour(tourId);

        if (originalToursLiveData.getValue() != null){
            for (Tour tour : originalToursLiveData.getValue()) {
                if (tour.getId() == tourId) {
                    if (downloadedTour != null){
                        tour.addAudioFiles(downloadedTour);
                    }
                    return tour;
                }
            }
        }

        return downloadedTour;
    }

    public Observable<Tour> loadTourById(int tourId, boolean forceLoad) {
        Tour tmpTour = null;
        if (!forceLoad){
            tmpTour = getTourById(tourId);
        }

        if(tmpTour != null){
            return Observable.just(tmpTour);
        } else {
            return apiHelper.loadTourById(PreferenceHelper.loadToken(App.getInstance()), tourId)
                    .map(Tour::new);
        }
    }

    @SuppressLint("CheckResult")
    public void updateTour(int tourId){
        apiHelper.loadTourById(PreferenceHelper.loadToken(context), tourId)
                .subscribe(tour -> {
                    if(originalToursLiveData.getValue() != null) {
                        for (int i = 0; i < originalToursLiveData.getValue().size(); i ++) {
                            Tour desiredTour = originalToursLiveData.getValue().get(i);
                            if (desiredTour.getId() == tour.getId()){
                                Tour newTour = new Tour(tour);
                                originalToursLiveData.getValue().set(i, newTour);
                                break;
                            }
                        }
                    }
                    if(nearToursLiveData.getValue() != null) {
                        for (int i = 0; i < nearToursLiveData.getValue().size(); i ++) {
                            Tour desiredTour = nearToursLiveData.getValue().get(i);
                            if (desiredTour.getId() == tour.getId()){
                                Tour newTour = new Tour(tour);
                                nearToursLiveData.getValue().set(i, newTour);
                                break;
                            }
                        }
                    }

                }, throwable -> {
                    Log.e("loadTours", "throwable "+throwable.getMessage());
                });
    }

    public ArrayList<Tour> searchToursByText(String query) {
        ArrayList<Tour> filteredTours = new ArrayList<>();
        if(originalToursLiveData.getValue() != null) {
            for (Tour tour : originalToursLiveData.getValue()) {
                String lowerQuery = query.toLowerCase();
                String lowerTitle = tour.getTitle().toLowerCase();
                if (lowerTitle.contains(lowerQuery)) {
                    filteredTours.add(tour);
                } else {
                    String lowerTags = tour.getTags().toString().toLowerCase();
                    if (lowerTags.contains(lowerQuery)) {
                        filteredTours.add(tour);
                    } else {
                        String lowerAddress = tour.getAddress().toLowerCase();
                        if (lowerAddress.contains(lowerQuery)) {
                            filteredTours.add(tour);
                        }
                    }
                }
            }
        }
        return filteredTours;
    }

    @SuppressLint("CheckResult")
    public void loadTours() {
        apiHelper.loadTours(PreferenceHelper.loadToken(context))
                .flatMapIterable(list -> list)
                .map(Tour::new)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tours -> {
                    toursLiveData.setValue(tours);
                }, throwable -> {
                    Log.e("loadTours", "throwable "+throwable.getMessage());
                });
    }

    @SuppressLint("CheckResult")
    public void loadContents() {
        apiHelper.loadContents(PreferenceHelper.loadToken(context))
                .flatMapIterable(list -> list)
                .map(Tour::new)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tours -> {
                    originalToursLiveData.setValue(tours);
                    new Handler().postDelayed(this::scheduleNotifications, 1000);
                }, throwable -> {
                    Log.e("loadTours", "throwable "+throwable.getMessage());
                });
    }

    private void scheduleNotifications(){
        long lastTime = PreferenceHelper.getLastNotificationWasShownTime(App.getInstance());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lastTime);

        int lastDay = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTimeInMillis(System.currentTimeMillis());
        int newDay = calendar.get(Calendar.DAY_OF_YEAR);

        if (lastDay != newDay) {
            NotificationHelper.scheduleNotification(App.getInstance(), System.currentTimeMillis() + 10000);
        }

    }

    @SuppressLint("CheckResult")
    public Single<List<Tour>> loadToursByAuthor(int authorId) {
        return apiHelper.loadToursByAuthor(PreferenceHelper.loadToken(context), authorId)
                .flatMapIterable(list -> list)
                .map(Tour::new)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @SuppressLint("CheckResult")
    public void loadNearTours(double latitude, double longitude) {
        Location tmpLocation = new Location("dummyprovider");
        tmpLocation.setLatitude(latitude);
        tmpLocation.setLatitude(longitude);

        if (tmpLocation.distanceTo(lastLocation) > IMPORTANT_DISTANCE) {
            lastLocation = tmpLocation;

            apiHelper.loadNearTours(PreferenceHelper.loadToken(context), new NearToursRequest(latitude, longitude, 0.5))
                    .flatMapIterable(list -> list)
                    .map(Tour::new)
                    .toList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(tours -> {
                        nearToursLiveData.setValue(tours);
                    }, throwable -> {
                        Log.e("loadTours", "throwable " + throwable.getMessage());
                    });
        }
    }

    public Observable<SaveTourResponse> saveTour(SaveTourRequest tour, String imagePath) {
        return apiHelper.createTour(PreferenceHelper.loadToken(context), tour, imagePath);
    }

    public Observable<SaveTourResponse> updateTour(int tourId, SaveTourRequest request, String imagePath) {
        return apiHelper.updateTour(PreferenceHelper.loadToken(context), tourId, request, imagePath);
    }
}
