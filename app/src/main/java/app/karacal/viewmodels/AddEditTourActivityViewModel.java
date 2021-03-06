package app.karacal.viewmodels;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.data.ProfileCache;
import app.karacal.data.repository.TourRepository;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.LocationHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.models.Tag;
import app.karacal.models.Tour;
import app.karacal.network.models.request.SaveTourRequest;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class AddEditTourActivityViewModel extends ViewModel {



    public interface LocationObtainListener {
        void onLocationUpdated(String location);
    }

    private class LocationUpdateHandler {
        private final AddEditTourActivityViewModel.LocationObtainListener locationObtainListener;
        private final LifecycleOwner locationObtainListenerOwner;

        private LocationUpdateHandler(LifecycleOwner locationObtainListenerOwner, AddEditTourActivityViewModel.LocationObtainListener locationObtainListener) {
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

    public void subscribeLocationUpdates(LifecycleOwner lifecycleOwner, AddEditTourActivityViewModel.LocationObtainListener listener) {
        locationUpdateHandler = new AddEditTourActivityViewModel.LocationUpdateHandler(lifecycleOwner, listener);
    }

    @Inject
    TourRepository tourRepository;

    @Inject
    ApiHelper apiHelper;

    CompositeDisposable disposable = new CompositeDisposable();

    private MutableLiveData<List<Tag>> tagsLiveData = new MutableLiveData<>();
    private SingleLiveEvent<String> onErrorEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> onTourSavedEvent = new SingleLiveEvent<>();

    public LiveData<List<Tag>> getTagsList() {
        return tagsLiveData;
    }

    public SingleLiveEvent<String> getErrorEvent(){
        return onErrorEvent;
    }

    public SingleLiveEvent<Integer> getTourSavedEvent(){
        return onTourSavedEvent;
    }

    private Tour currentTour;

    private MutableLiveData<Location> locationCoordinates = new MutableLiveData<>();

    private String imagePath;
    private String title;
    private String latitude = "";
    private String longitude = "";
    private String address;
    private String description;

    private HashSet<Tag> selectedTags = new HashSet<>();

    public AddEditTourActivityViewModel() {
        App.getAppComponent().inject(this);
    }

    public void setTour(Tour tour){
        this.currentTour = tour;
        selectedTags.addAll(currentTour.getTags());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;

        try {
            double lat = Double.parseDouble(latitude);
            if (locationCoordinates.getValue() != null){
                locationCoordinates.getValue().setLatitude(lat);
            } else {
                Location location = new Location("dummyProvider");
                location.setLatitude(lat);
                locationCoordinates.setValue(location);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;

        try {
            double lgn = Double.parseDouble(longitude);
            if (locationCoordinates.getValue() != null){
                locationCoordinates.getValue().setLongitude(lgn);
            } else {
                Location location = new Location("dummyProvider");
                location.setLongitude(lgn);
                locationCoordinates.setValue(location);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void setLocation(Location location) {
        this.locationCoordinates.setValue(location);
    }

    public LiveData<Location> getLocationCoordinates(){
        return locationCoordinates;
    }

    public String getAddress() {
        return address;
    }


    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashSet<Tag> getTags() {
        return selectedTags;
    }

    public boolean addTag(Tag tag){
        return selectedTags.add(tag);
    }

    public boolean removeTag(Tag tag){
        return selectedTags.remove(tag);
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @SuppressLint("CheckResult")
    public void obtainLocation() {
        LocationHelper.getLastKnownLocation().timeout(GEO_CODING_TIMEOUT, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> isGeoCodingInProgress.postValue(true))
                .subscribe(loc -> {
                            locationCoordinates.setValue(loc);
                            isGeoCodingInProgress.postValue(false);
                        },
                        throwable -> {
                            isGeoCodingInProgress.postValue(false);
                            if (locationUpdateHandler != null) {
                                locationUpdateHandler.notifyLocationUpdate(null);
                            }
                        });
    }

    public void loadTags(){
        disposable.add(apiHelper.loadTags(PreferenceHelper.loadToken(App.getInstance()))
                .subscribe(response -> {
                    tagsLiveData.setValue(response);
                }, throwable -> {
                    Log.e("loadTags", "throwable " + throwable.getMessage());
                }));
    }

    public void saveTour(){
        if (currentTour == null){
            createTour();
        } else {
            updateTour();
        }
    }

    private void createTour() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String dateStr = dateFormat.format(Calendar.getInstance().getTime());

        List<Integer> tags = new ArrayList<>();
        for(Tag tag: selectedTags){
            tags.add(tag.getId());
        }

        SaveTourRequest request = new SaveTourRequest(
                title,
                ProfileCache.getInstance(App.getInstance()).getProfile().getName(),
                dateStr,
                locationCoordinates!= null ? String.valueOf(locationCoordinates.getValue().getLatitude()) : "",
                locationCoordinates!= null ? String.valueOf(locationCoordinates.getValue().getLongitude()) : "",
                tags,
                description,
                address,
                "0",
                "0",
                ProfileCache.getInstance(App.getInstance()).getGuideId());

        disposable.add(tourRepository.saveTour(request, imagePath)
                .subscribe(response -> {
                    if (response.isSuccess()) {
                        onTourSavedEvent.setValue(response.getId());
                        tourRepository.updateTour(response.getId());
                    } else {
                        onErrorEvent.setValue(response.getErrorMessage());
                    }
                }, throwable -> {
                    onErrorEvent.setValue(App.getResString(R.string.can_not_save_tour));
                }));
    }

    private void updateTour() {

        if (validateChanges()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String dateStr = dateFormat.format(Calendar.getInstance().getTime());

            List<Integer> tags = new ArrayList<>();
            for (Tag tag : selectedTags) {
                tags.add(tag.getId());
            }

            SaveTourRequest request = new SaveTourRequest(
                    title,
                    ProfileCache.getInstance(App.getInstance()).getProfile().getName(),
                    dateStr,
                    locationCoordinates != null ? String.valueOf(locationCoordinates.getValue().getLatitude()) : "",
                    locationCoordinates != null ? String.valueOf(locationCoordinates.getValue().getLongitude()) : "",
                    tags,
                    description,
                    address,
                    "0",
                    "0",
                    ProfileCache.getInstance(App.getInstance()).getGuideId());

            disposable.add(tourRepository.updateTour(currentTour.getId(), request, imagePath)
                    .subscribe(response -> {
                        if (response.isSuccess()) {
                            onTourSavedEvent.setValue(currentTour.getId());
                            tourRepository.updateTour(currentTour.getId());
                        } else {
                            onErrorEvent.setValue(response.getErrorMessage());
                        }
                    }, throwable -> {
                        onErrorEvent.setValue(App.getResString(R.string.can_not_save_tour));
                    }));
        } else {
            onTourSavedEvent.setValue(currentTour.getId());
        }
    }

    private boolean validateChanges(){
        if(imagePath != null) return true;

        if (title != null && !title.equals(currentTour.getTitle())) return true;

        if(locationCoordinates != null){
            if(locationCoordinates.getValue().getLatitude() != currentTour.getTourLocation().getLatitude()) return true;
            if(locationCoordinates.getValue().getLongitude() != currentTour.getTourLocation().getLongitude()) return true;
        }

        if (!selectedTags.containsAll(currentTour.getTags())) return true;
        if (!currentTour.getTags().containsAll(selectedTags)) return true;

        if (description != null && !description.equals(currentTour.getDescription())) return true;
        if (address != null && !address.equals(currentTour.getAddress())) return true;

        return false;
    }
}
