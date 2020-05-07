package app.karacal.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.dialogs.DeleteAccountDialog;
import app.karacal.helpers.DummyHelper;
import app.karacal.helpers.PermissionHelper;
import app.karacal.helpers.ProfileHolder;
import app.karacal.models.Profile;
import app.karacal.navigation.NavigationHelper;
import app.karacal.viewmodels.SettingsActivityViewModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
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


    private String firstName;
    private String secondName;
    private Date birthDate;

    private String email;
    private String location;

    @Inject
    ProfileHolder profileHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        initProfile();
        viewModel = new ViewModelProvider(this).get(SettingsActivityViewModel.class);
        setContentView(R.layout.activity_settings);

        setupBackButton();
        setupApplyButton();
        setupChangePasswordButton();
        setupTermsButton();
        setupPrivacyPolicyButton();
        setupDeleteAccountButton();
        setupFirstNameInput();
        setupSecondNameInput();
        setupBirthDateInput();
        setupEmailInput();
        setupLocationInput();
    }

    private void initProfile(){
        Profile profile = profileHolder.getProfile();
        firstName = profile.getFirstName();
        secondName = profile.getSecondName();
        birthDate = profile.getBirthDate();
        email = profile.getEmail();
        location = profile.getLocation();
    }

    private void setupBackButton() {
        ImageView buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupApplyButton() {
        buttonApply = findViewById(R.id.buttonApply);
        buttonApply.setOnClickListener(v -> onBackPressed());
    }

    private void setupChangePasswordButton() {
        LinearLayout buttonChangePassword = findViewById(R.id.buttonChangePassword);
        if(profileHolder.getProfile().getSocialId() != null){
            buttonChangePassword.setVisibility(View.GONE);
        }else {
            buttonChangePassword.setOnClickListener(v -> NavigationHelper.startChangePasswordActivity(this));
        }
    }

    private void setupTermsButton() {
        LinearLayout buttonTerms = findViewById(R.id.buttonTerms);
        buttonTerms.setOnClickListener(v -> {
            Intent intent = new Intent(this, PrivacyPolicyActivity.class);
            startActivity(intent);
        });
    }

    private void setupPrivacyPolicyButton() {
        LinearLayout buttonPrivacyPolicy = findViewById(R.id.buttonPersonalDataProtectionPolicy);
        buttonPrivacyPolicy.setOnClickListener(v -> {
            Intent intent = new Intent(this, PrivacyPolicyActivity.class);
            startActivity(intent);
        });
    }

    private void setupDeleteAccountButton() {
        LinearLayout buttonDeleteAccount = findViewById(R.id.buttonDeleteMyAccount);
        buttonDeleteAccount.setOnClickListener(v -> {
            DeleteAccountDialog dialog = new DeleteAccountDialog();
            dialog.setListener(() -> DummyHelper.dummyAction(this));
            dialog.show(getSupportFragmentManager(), DeleteAccountDialog.DIALOG_TAG);
        });
    }

    private void setupFirstNameInput() {
        textInputLayoutFirstName = findViewById(R.id.textInputLayoutFirstName);
        textInputLayoutFirstName.getEditText().setText(firstName);
        textInputObservable(textInputLayoutFirstName).subscribe((s) -> {
            textInputLayoutFirstName.setError(TextUtils.isEmpty(s) ? getString(R.string.enter_first_name) : null);
            validateInputs();
        });
    }

    private void setupSecondNameInput() {
        textInputLayoutSecondName = findViewById(R.id.textInputLayoutSecondName);
        textInputLayoutSecondName.getEditText().setText(secondName);
        textInputObservable(textInputLayoutSecondName).subscribe((s) -> {
            textInputLayoutSecondName.setError(TextUtils.isEmpty(s) ? getString(R.string.enter_second_name) : null);
            validateInputs();
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
        Observable<String> observable = Observable.create(emitter -> textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
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
                String s = editable.toString();
                emitter.onNext(s != null ? s : "");
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

    private void setupEmailInput() {
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutEmail.getEditText().setText(email);
        textInputObservable(textInputLayoutEmail).subscribe((s) -> {
            textInputLayoutEmail.setError(TextUtils.isEmpty(s) ? getString(R.string.enter_email) : null);
            validateInputs();
        });
    }

    private void setupLocationInput() {
        ProgressBar progressBar = findViewById(R.id.progressBarGeoCoding);
        textInputLayoutLocation = findViewById(R.id.textInputLayoutLocation);
        textInputLayoutLocation.getEditText().setText(location);
        textInputObservable(textInputLayoutLocation).subscribe((s) -> {
            textInputLayoutLocation.setError(TextUtils.isEmpty(s) ? getString(R.string.enter_location) : null);
            validateInputs();
        });
        ImageView buttonLocation = findViewById(R.id.buttonLocation);
        buttonLocation.setOnClickListener(v -> permissionHelper.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION,
                () -> viewModel.obtainLocation(),
                () -> Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show()));
        viewModel.subscribeLocationUpdates(this, location -> {
            if (location != null) {
                String locationText = String.format(Locale.getDefault(), "%f, %f", location.getLatitude(), location.getLongitude());
                textInputLayoutLocation.getEditText().setText(locationText);
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

    private boolean validateInputs(TextInputLayout... textInputLayouts) {
        for (TextInputLayout inputLayout : textInputLayouts) {
            if (TextUtils.isEmpty(inputLayout.getEditText().getText().toString())) {
                return false;
            }
        }
        return true;
    }

    private void validateInputs() {
        boolean isValid = validateInputs(textInputLayoutFirstName, textInputLayoutSecondName, textInputLayoutBirthDate, textInputLayoutEmail, textInputLayoutLocation);
        buttonApply.setVisibility(isValid ? View.VISIBLE : View.INVISIBLE);
    }
}
