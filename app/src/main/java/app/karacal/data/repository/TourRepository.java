package app.karacal.data.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.karacal.App;
import app.karacal.data.DownloadedToursCache;
import app.karacal.data.dao.ToursDao;
import app.karacal.data.entity.TourEntity;
import app.karacal.helpers.ApiHelper;
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

    public ArrayList<Tour> searchToursByText(String query) {
        ArrayList<Tour> filteredTours = new ArrayList<>();
        if(originalToursLiveData.getValue() != null) {
            for (Tour tour : originalToursLiveData.getValue()) {
                String loverQuery = query.toLowerCase();
                String loverTitle = tour.getTitle().toLowerCase();
                if (loverTitle.contains(loverQuery)) {
                    filteredTours.add(tour);
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
                    for (Tour tour: tours){
                        if (tour.getId() == 1 || tour.getId() == 2 || tour.getId() == 7 || tour.getId() == 8){
                            tour.setPrice(999);
                        }
                    }
                    originalToursLiveData.setValue(tours);
                    // TODO: save to DB

                }, throwable -> {
                    Log.v("loadTours", "throwable "+throwable.getMessage());
                    // TODO: load list from DB
                });
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

    public Observable<SaveTourResponse> saveTour(SaveTourRequest tour) {
        return apiHelper.createTour(PreferenceHelper.loadToken(context), tour);
    }
}
