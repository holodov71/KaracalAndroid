package app.karacal.data.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.karacal.App;
import app.karacal.data.DownloadedToursCache;
import app.karacal.data.NotificationsSchedule;
import app.karacal.data.dao.ToursDao;
import app.karacal.data.entity.TourEntity;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.NotificationHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.models.NotificationScheduleModel;
import app.karacal.models.Tour;
import app.karacal.network.models.request.NearToursRequest;
import app.karacal.network.models.request.SaveTourRequest;
import app.karacal.network.models.response.ContentResponse;
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
        Log.v(App.TAG, "updateTour tourId = "+tourId);
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
                    Log.v("loadTours", "throwable "+throwable.getMessage());
                    // TODO: load list from DB
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

    public ArrayList<Tour> getToursByAuthor(int guideId) {
        ArrayList<Tour> filteredTours = new ArrayList<>();
        if(originalToursLiveData.getValue() != null) {
            for (Tour tour : originalToursLiveData.getValue()) {
                if (tour.getAuthorId() == guideId) {
                    filteredTours.add(tour);
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
                    Log.v("loadTours", "toursLiveData.setValue(tours) size = "+tours.size());
                    toursLiveData.setValue(tours);
                    // TODO: save to DB

                }, throwable -> {
                    Log.v("loadTours", "throwable "+throwable.getMessage());
                    // TODO: load list from DB
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
                    Log.v("loadTours", "contentsLiveData.setValue(tours) size = "+tours.size());
//                    for (Tour tour: tours){
//                        if (tour.getId() == 1 || tour.getId() == 2 || tour.getId() == 7 || tour.getId() == 8){
//                            tour.setPrice(999);
//                        }
//                    }
                    originalToursLiveData.setValue(tours);
                    new Handler().postDelayed(() -> scheduleNotifications(tours), 1000);
                    // TODO: save to DB

                }, throwable -> {
                    Log.v("loadTours", "throwable "+throwable.getMessage());
                    // TODO: load list from DB
                });
    }

    private void scheduleNotifications(List<Tour> tours){
        List<Tour> mTours = new ArrayList<>(tours);
        Location lastLocation = App.getInstance().getLastLocation();

        if (!NotificationsSchedule.getInstance(App.getInstance()).getNotificationsList().isEmpty() &&
                (NotificationsSchedule.getInstance(App.getInstance()).isHasLocation() || lastLocation == null)){
            return;
        }

        if (lastLocation != null){

            Collections.sort(mTours, (obj1, obj2) -> {
                // ## Ascending order
                Float dist1 = obj1.getTourLocation().distanceTo(lastLocation);
                Float dist2 = obj2.getTourLocation().distanceTo(lastLocation);

                return dist1.compareTo(dist2); // To compare string values
            });

        }

        List<NotificationScheduleModel> notifList = new ArrayList<>();

        int lastIndex = 32;
        if (mTours.size() < 32){
            lastIndex = mTours.size();
        }

        for (int i = 0; i < 32; i++){
            notifList.add(new NotificationScheduleModel(i, mTours.get(i)));
        }

        NotificationsSchedule.getInstance(App.getInstance()).setNotificationsList(App.getInstance(), notifList);
        NotificationsSchedule.getInstance(App.getInstance()).setHasLocation(App.getInstance(), lastLocation != null);
        NotificationHelper.scheduleNotification(App.getInstance(), System.currentTimeMillis() + 10000);
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

        Log.v(App.TAG, "tmpLocation.distanceTo(lastLocation) = "+tmpLocation.distanceTo(lastLocation));

        if (tmpLocation.distanceTo(lastLocation) > IMPORTANT_DISTANCE) {
            lastLocation = tmpLocation;

            apiHelper.loadNearTours(PreferenceHelper.loadToken(context), new NearToursRequest(latitude, longitude, 0.5))
                    .flatMapIterable(list -> list)
                    .map(Tour::new)
                    .toList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(tours -> {
                        Log.v("loadTours", "contentsLiveData.setValue(tours) size = " + tours.size());
                        nearToursLiveData.setValue(tours);
                        // TODO: save to DB

                    }, throwable -> {
                        Log.v("loadTours", "throwable " + throwable.getMessage());
                        // TODO: load list from DB
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
