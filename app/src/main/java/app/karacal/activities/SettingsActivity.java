package app.karacal.activities;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.data.ProfileCache;
import app.karacal.dialogs.DeleteAccountDialog;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.helpers.RestartAppHelper;
import app.karacal.helpers.ToastHelper;
import app.karacal.helpers.WebLinkHelper;
import app.karacal.models.Profile;
import app.karacal.navigation.NavigationHelper;
import app.karacal.network.models.request.ProfileRequest;
import app.karacal.network.models.request.ResetPasswordRequest;
import app.karacal.service.PaymentsUpdateService;
import app.karacal.viewmodels.SettingsActivityViewModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SettingsActivity extends PermissionActivity implements DatePickerDialog.OnDateSetListener {

    private static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE);

    private SettingsActivityViewModel viewModel;

    private ImageView buttonApply;

    private TextInputLayout textInputLayoutFirstName;
    private TextInputLayout textInputLayoutSecondName;
    private TextInputLayout textInputLayoutBirthDate;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutLocation;

    private View progressLoading;


    private String firstName;
    private String secondName;
    private Date birthDate;

    private String email;
    private String location;

    @Inject
    ApiHelper apiHelper;

    private GoogleSignInClient mGoogleApiClient;

    CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        initProfile();
        viewModel = new ViewModelProvider(this).get(SettingsActivityViewModel.class);
        setContentView(R.layout.activity_settings);
        setupGoogleClient();

        setupContentContainer();
        setupBackButton();
        setupApplyButton();
        setupChangePasswordButton();
        setupPaymentMethodsButton();
        setupManageSubscriptionButton();
        setupTermsButton();
        setupPrivacyPolicyButton();
        setupDeleteAccountButton();
        setupFirstNameInput();
        setupSecondNameInput();
        setupBirthDateInput();
        setupEmailInput();
        setupLocationInput();
        setupProgressLoading();
        setupSwitchSettings();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    private void initProfile(){
        Profile profile = ProfileCache.getInstance(this).getProfile();
        firstName = profile.getFirstName();
        secondName = profile.getSecondName();
        birthDate = profile.getBirthDate();
        email = profile.getEmail();
        location = profile.getLocation();
    }

    private void setupContentContainer() {
        View contentContainer = findViewById(R.id.contentContainer);
        contentContainer.requestFocus();
    }

    private void setupBackButton() {
        ImageView buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupApplyButton() {
        buttonApply = findViewById(R.id.buttonApply);
        buttonApply.setOnClickListener(v -> updateProfile());
    }

    private void setupChangePasswordButton() {
        LinearLayout buttonChangePassword = findViewById(R.id.buttonChangePassword);
        if(ProfileCache.getInstance(this).getProfile().getSocialId() != null){
            buttonChangePassword.setVisibility(View.GONE);
        }else {
            buttonChangePassword.setOnClickListener(v -> resetPassword());
        }
    }

    private void setupPaymentMethodsButton() {
        LinearLayout buttonPaymentMethods = findViewById(R.id.buttonPaymentMethods);
        buttonPaymentMethods.setOnClickListener(v -> NavigationHelper.startPaymentMethodsActivity(this));
    }

    private void setupManageSubscriptionButton() {
        LinearLayout buttonSubscription = findViewById(R.id.buttonSubscription);
        buttonSubscription.setOnClickListener(v -> NavigationHelper.startSubscriptionActivity(this));
    }

    private void setupTermsButton() {
        LinearLayout buttonTerms = findViewById(R.id.buttonTerms);
        buttonTerms.setOnClickListener(v -> NavigationHelper.startPrivacyPolicyActivity(this));
    }

    private void setupPrivacyPolicyButton() {
        LinearLayout buttonPrivacyPolicy = findViewById(R.id.buttonPersonalDataProtectionPolicy);
        buttonPrivacyPolicy.setOnClickListener(v -> NavigationHelper.startPolitiqueProtectionActivity(this));
    }

    private void setupDeleteAccountButton() {
        LinearLayout buttonDeleteAccount = findViewById(R.id.buttonDeleteMyAccount);
        buttonDeleteAccount.setOnClickListener(v -> {
            DeleteAccountDialog dialog = new DeleteAccountDialog();
            dialog.setListener(this::deleteAccount);
            dialog.show(getSupportFragmentManager(), DeleteAccountDialog.DIALOG_TAG);
        });
    }

    @SuppressLint("CheckResult")
    private void setupFirstNameInput() {
        textInputLayoutFirstName = findViewById(R.id.textInputLayoutFirstName);
        textInputLayoutFirstName.getEditText().setText(firstName);
        textInputObservable(textInputLayoutFirstName).subscribe((s) -> {
            textInputLayoutFirstName.setError(TextUtils.isEmpty(s) ? getString(R.string.enter_first_name) : null);
            validateInputs();
            firstName = s;
        });
    }

    @SuppressLint("CheckResult")
    private void setupSecondNameInput() {
        textInputLayoutSecondName = findViewById(R.id.textInputLayoutSecondName);
        textInputLayoutSecondName.getEditText().setText(secondName);
        textInputObservable(textInputLayoutSecondName).subscribe((s) -> {
            textInputLayoutSecondName.setError(TextUtils.isEmpty(s) ? getString(R.string.enter_second_name) : null);
            validateInputs();
            secondName = s;
        });
    }

    private void setupBirthDateInput() {
        textInputLayoutBirthDate = findViewById(R.id.textInputLayoutBirthDate);
        if (birthDate != null) {
            textInputLayoutBirthDate.getEditText().setText(dateFormat.format(birthDate));
        }
        ImageView buttonCalendar = findViewById(R.id.buttonCalendar);
        buttonCalendar.setOnClickListener(v -> showDatePickerDialog());
    }

    private Observable<String> textInputObservable(TextInputLayout textInputLayout) {
        Observable<String> observable = Observable.create(emitter -> textInputLayout.getEditText()
                .addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                emitter.onNext(editable != null ? editable.toString() : "");
            }
        }));
        return observable.debounce(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        if (birthDate != null) {
            calendar.setTime(birthDate);
        }
        new DatePickerDialog(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(y, m, d);
        birthDate = calendar.getTime();
        textInputLayoutBirthDate.getEditText().setText(dateFormat.format(birthDate));
        validateInputs();
    }

    @SuppressLint("CheckResult")
    private void setupEmailInput() {
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutEmail.getEditText().setText(email);
        textInputObservable(textInputLayoutEmail).subscribe((s) -> {
            textInputLayoutEmail.setError(TextUtils.isEmpty(s) ? getString(R.string.enter_email) : null);
            validateInputs();
            email = s;
        });
    }

    @SuppressLint("CheckResult")
    private void setupLocationInput() {
        ProgressBar progressBar = findViewById(R.id.progressBarGeoCoding);
        textInputLayoutLocation = findViewById(R.id.textInputLayoutLocation);
        textInputLayoutLocation.getEditText().setText(location);
        textInputObservable(textInputLayoutLocation).subscribe((s) -> {
            textInputLayoutLocation.setError(TextUtils.isEmpty(s) ? getString(R.string.enter_location) : null);
            validateInputs();
            location = s;
        });
        ImageView buttonLocation = findViewById(R.id.buttonLocation);
        buttonLocation.setOnClickListener(v -> permissionHelper.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION,
                () -> viewModel.obtainLocation(),
                () -> Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show()));
        viewModel.subscribeLocationUpdates(this, location -> {
            if (location != null) {
                String locationText = String.format(Locale.getDefault(), "%f, %f", location.getLatitude(), location.getLongitude());
                textInputLayoutLocation.getEditText().setText(locationText);
                SettingsActivity.this.location = locationText;
            } else {
                Toast.makeText(this, R.string.error_obtaining_location, Toast.LENGTH_LONG).show();
            }
        });
        viewModel.getGeoCodingState().observe(this, isActive -> {
            textInputLayoutLocation.setEnabled((!isActive));
            buttonLocation.setEnabled((!isActive));
            progressBar.setVisibility(isActive ? View.VISIBLE : View.INVISIBLE);
            validateInputs();
        });
    }

    private void setupProgressLoading(){
        progressLoading = findViewById(R.id.progressLoading);
    }

    private void setupSwitchSettings(){
        Switch switchDownloadOnlyViaWifi = findViewById(R.id.switchDownloadOnlyViaWifi);
        switchDownloadOnlyViaWifi.setChecked(PreferenceHelper.isDownloadOnlyViaWifi(this));
        switchDownloadOnlyViaWifi.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()){
                PreferenceHelper.setDownloadOnlyViaWifi(this, isChecked);
            }
        });

        Switch switchPauseAudio = findViewById(R.id.switchPauseAudio);
        switchPauseAudio.setChecked(PreferenceHelper.isPauseAudioAfterEachSegment(this));
        switchPauseAudio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()){
                PreferenceHelper.setPauseAudioAfterEachSegment(this, isChecked);
            }
        });

        Switch switchAllowNotification = findViewById(R.id.switchAllowNotification);
        switchAllowNotification.setChecked(PreferenceHelper.isNotificationsAllowed(this));
        switchAllowNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()){
                PreferenceHelper.setNotificationsAllowed(this, isChecked);
            }
        });
    }

    private boolean validateInputs(TextInputLayout... textInputLayouts) {
        for (TextInputLayout inputLayout : textInputLayouts) {
            if (TextUtils.isEmpty(inputLayout.getEditText().getText().toString())) {
                return false;
            }
        }
        return true;
    }

    private void validateInputs() {
        boolean isValid = validateInputs(textInputLayoutFirstName, textInputLayoutSecondName, textInputLayoutEmail);
        buttonApply.setVisibility(isValid ? View.VISIBLE : View.INVISIBLE);
    }

    private void updateProfile(){
        hideKeyboardFrom(textInputLayoutBirthDate);

        progressLoading.setVisibility(View.VISIBLE);

        disposable.add(apiHelper.editProfile(
                PreferenceHelper.loadToken(this),
                new ProfileRequest(firstName, secondName, birthDate, email, location))
                .map(baseResponse -> PreferenceHelper.loadToken(this))
                .flatMap(apiHelper::loadProfile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(profile -> {
                    ProfileCache.getInstance(this).setProfile(this, profile);
                    onBackPressed();
                }, throwable -> {
                    progressLoading.setVisibility(View.GONE);
                    ToastHelper.showToast(this, getString(R.string.connection_problem));
                }));
    }

    private void resetPassword(){
        hideKeyboardFrom(textInputLayoutBirthDate);

        progressLoading.setVisibility(View.VISIBLE);

        String mail = ProfileCache.getInstance(this).getProfile().getEmail();

        disposable.add(apiHelper.resetPassword(new ResetPasswordRequest(mail))
                .subscribe(response -> {
                    progressLoading.setVisibility(View.GONE);
                    if (response.isSuccess()){
                        ToastHelper.showToast(this, response.getMsg());
                    } else {
                        ToastHelper.showToast(this, response.getErrorMessage());
                    }
                }, throwable -> {
                    progressLoading.setVisibility(View.GONE);
                    ToastHelper.showToast(this, getString(R.string.connection_problem));
                }));
    }

    private void deleteAccount(){
        hideKeyboardFrom(textInputLayoutBirthDate);

        progressLoading.setVisibility(View.VISIBLE);

        disposable.add(apiHelper.deleteProfile(PreferenceHelper.loadToken(this))
                .subscribe(response -> {
                    if (response.isSuccess()){
                        logout();
                        RestartAppHelper.restartApp(this);
                    } else {
                        progressLoading.setVisibility(View.GONE);
                        ToastHelper.showToast(this, response.getErrorMessage());
                    }
                }, throwable -> {
                    progressLoading.setVisibility(View.GONE);
                    ToastHelper.showToast(this, getString(R.string.connection_problem));
                }));
    }

    private void setupGoogleClient() {
        String serverClientId = getString(R.string.server_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(serverClientId)
                .requestEmail()
                .build();
        mGoogleApiClient = GoogleSignIn.getClient(this, gso);
    }

    private void logout(){
        try {
            LoginManager.getInstance().logOut();
            mGoogleApiClient.signOut();
            ProfileCache.getInstance(this).removeProfile(this);
            PreferenceHelper.deleteToken(this);
            PaymentsUpdateService.stopTimer();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null && resultCode == RESULT_OK) {
            if (requestCode == SubscriptionActivity.SUBSCRIPTION_REQUEST_CODE) {
                String url = data.getStringExtra(SubscriptionActivity.RESULT_URL);
                if (url != null){
                    WebLinkHelper.openWebLink(this, url);
                }
                Log.v("onActivityResult", "Subscription opened");
            }
        }
    }


}
