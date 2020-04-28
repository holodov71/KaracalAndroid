package app.karacal.viewmodels;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import app.karacal.helpers.LocationHelper;
import app.karacal.models.Interest;
import app.karacal.retrofit.LoginRequest;
import app.karacal.retrofit.RegisterRequest;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RegistrationActivityViewModel extends ViewModel {

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

    public static final int MAX_INTERESTS = 10;
    private static final int GEO_CODING_TIMEOUT = 5;
    private MutableLiveData<String> activityTitle = new MutableLiveData<>();
    private MutableLiveData<Boolean> isGeoCodingInProgress = new MutableLiveData<>(false);

    private MutableLiveData<Integer> interestCount = new MutableLiveData<>();

    private LocationUpdateHandler locationUpdateHandler;

    private String firstName;
    private String secondName;
    private Date birthDate;

    private String email;
    private String location;
    private String referralCode;

    private String password;

    private ArrayList<Interest> interests;

    public RegistrationActivityViewModel() {
        interests = new ArrayList<>();
        interests.add(new Interest("Modern"));
        interests.add(new Interest("Middle Ages"));
        interests.add(new Interest("Contemporary"));
        interests.add(new Interest("Street Art"));
        interests.add(new Interest("Legendary places"));
        interests.add(new Interest("Renaissance"));
        interests.add(new Interest("Antiquity"));
        interests.add(new Interest("Arts"));
        interests.add(new Interest("History"));
        updateInterests();
        Calendar calendar = Calendar.getInstance();
        birthDate = calendar.getTime();

    }

    public LiveData<String> getActivityTitle() {
        return activityTitle;
    }

    public LiveData<Boolean> getGeoCodingState() {
        return isGeoCodingInProgress;
    }

    public LiveData<Integer> getInterestCount() {
        return interestCount;
    }

    public boolean isGeoCodingInProgress() {
        return isGeoCodingInProgress.getValue() != null ? isGeoCodingInProgress.getValue() : false;
    }

    public void subscribeLocationUpdates(LifecycleOwner lifecycleOwner, LocationObtainListener listener) {
        locationUpdateHandler = new LocationUpdateHandler(lifecycleOwner, listener);
    }

    public void setActivityTitle(String title) {
        activityTitle.postValue(title);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Interest> getInterests() {
        return interests;
    }

    public void toggleInterestState(String title){
        Interest interest = null;
        for (Interest i: interests) {
            if (i.getTitle().equals(title)){
                interest = i;
                break;
            }
        }
        if (interest == null){
            return;
        }
        if (interestCount.getValue() < MAX_INTERESTS || interest.isSelected()) {
            interest.setSelected(!interest.isSelected());
        }
        updateInterests();
    }

    public void updateInterests(){
        int count = 0;
        for (Interest interest: interests) {
            if (interest.isSelected()){
                count++;
            }
        }
        interestCount.postValue(count);
    }

    public void obtainLocation() {
        LocationHelper.getLastKnownLocation().timeout(GEO_CODING_TIMEOUT, TimeUnit.SECONDS)
                .map(location -> String.format("%f, %f", location.getLatitude(), location.getLongitude()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> isGeoCodingInProgress.postValue(true))
                .subscribe(locationText -> {
                            location = locationText;
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
                        });
    }

    public RegisterRequest getRegisterRequest(){
        return new RegisterRequest(firstName, secondName, birthDate, email, location, referralCode, password);
    }

    public LoginRequest getLoginRequest(){
        return new LoginRequest(email, password);
    }

}
