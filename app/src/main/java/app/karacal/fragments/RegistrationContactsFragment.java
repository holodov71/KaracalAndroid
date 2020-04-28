package app.karacal.fragments;

import android.Manifest;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.helpers.PermissionHelper;
import app.karacal.helpers.TextInputHelper;
import app.karacal.viewmodels.RegistrationActivityViewModel;
import apps.in.android_logger.LogFragment;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RegistrationContactsFragment extends LogFragment {

    private RegistrationActivityViewModel viewModel;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutLocation;
    private TextInputLayout textInputLayoutReferralCode;
    private Button buttonContinue;

    @Inject
    PermissionHelper permissionHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        viewModel = new ViewModelProvider(getActivity()).get(RegistrationActivityViewModel.class);
        viewModel.setActivityTitle(getString(R.string.registration));
        View view = inflater.inflate(R.layout.fragment_registration_contacts, container, false);
        setupEmailInput(view);
        setupLocationInput(view);
        setupReferralCodeInput(view);
        setupContinueButton(view);
        validateInputs();
        return view;
    }

    private void setupEmailInput(View view) {
        textInputLayoutEmail = view.findViewById(R.id.textInputLayoutEmail);
        textInputLayoutEmail.getEditText().setText(viewModel.getEmail() != null ? viewModel.getEmail() : "");
        TextInputHelper.editTextObservable(textInputLayoutEmail).subscribe((s) -> {
            viewModel.setEmail(TextUtils.isEmpty(s) ? null : s);
            validateInputs();
        });
    }

    private void setupLocationInput(View view) {
        ProgressBar progressBar = view.findViewById(R.id.progressBarGeoCoding);
        textInputLayoutLocation = view.findViewById(R.id.textInputLayoutLocation);
        textInputLayoutLocation.getEditText().setText(viewModel.getLocation() != null ? viewModel.getLocation() : "");
        TextInputHelper.editTextObservable(textInputLayoutLocation).subscribe((s) -> {
            viewModel.setLocation(TextUtils.isEmpty(s) ? null : s);
            validateInputs();
        });
        ImageView buttonLocation = view.findViewById(R.id.buttonLocation);
        buttonLocation.setOnClickListener(v -> permissionHelper.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION,
                () -> viewModel.obtainLocation(),
                () -> Toast.makeText(getContext(), R.string.permission_denied, Toast.LENGTH_LONG).show()));
        viewModel.subscribeLocationUpdates(this, location -> {
            if (location != null) {
                textInputLayoutLocation.getEditText().setText(location);
            } else {
                Toast.makeText(getContext(), R.string.error_obtaining_location, Toast.LENGTH_LONG).show();
            }
        });
        viewModel.getGeoCodingState().observe(this, isActive -> {
            textInputLayoutLocation.setEnabled((!isActive));
            buttonLocation.setEnabled((!isActive));
            progressBar.setVisibility(isActive ? View.VISIBLE : View.INVISIBLE);
            validateInputs();
        });
    }

    private void setupReferralCodeInput(View view) {
        textInputLayoutReferralCode = view.findViewById(R.id.textInputLayoutReferralCode);
        textInputLayoutReferralCode.getEditText().setText(viewModel.getReferralCode() != null ? viewModel.getReferralCode() : "");
        ImageView buttonReferral = view.findViewById(R.id.buttonReferral);
        ImageView buttonConfirmed = view.findViewById(R.id.buttonCheck);
        if (viewModel.getReferralCode() == null) {
            buttonReferral.setVisibility(View.VISIBLE);
            buttonReferral.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(R.id.registrationReferalCodeFragment));
        } else {
            buttonConfirmed.setVisibility(View.VISIBLE);
        }
    }

    private void setupContinueButton(View view) {
        buttonContinue = view.findViewById(R.id.buttonContinue);
        buttonContinue.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(R.id.registrationPasswordFragment));
    }

    private void validateInputs() {
        buttonContinue.setEnabled(viewModel.getEmail() != null && viewModel.getLocation() != null && !viewModel.isGeoCodingInProgress());
    }

}
