package app.karacal.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputLayout;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.helpers.PermissionHelper;
import app.karacal.helpers.TextInputHelper;
import app.karacal.viewmodels.RegistrationActivityViewModel;
import apps.in.android_logger.LogFragment;

public class RegistrationContactsFragment extends LogFragment {

    private RegistrationActivityViewModel viewModel;

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
        setupReferralCodeInput(view);
        setupContinueButton(view);
        validateInputs();
        return view;
    }

    @SuppressLint("CheckResult")
    private void setupEmailInput(View view) {
        TextInputLayout textInputLayoutEmail = view.findViewById(R.id.textInputLayoutEmail);
        textInputLayoutEmail.getEditText().setText(viewModel.getEmail() != null ? viewModel.getEmail() : "");
        TextInputHelper.editTextObservable(textInputLayoutEmail).subscribe((s) -> {
            viewModel.setEmail(TextUtils.isEmpty(s) ? null : s);
            validateInputs();
        });
    }

    private void setupReferralCodeInput(View view) {
        TextInputLayout textInputLayoutReferralCode = view.findViewById(R.id.textInputLayoutReferralCode);
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
        buttonContinue.setEnabled(viewModel.getEmail() != null);
    }

}
