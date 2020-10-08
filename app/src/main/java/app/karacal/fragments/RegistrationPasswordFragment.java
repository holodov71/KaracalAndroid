package app.karacal.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputLayout;

import app.karacal.R;
import app.karacal.helpers.TextInputHelper;
import app.karacal.viewmodels.RegistrationActivityViewModel;
import apps.in.android_logger.LogFragment;

public class RegistrationPasswordFragment extends LogFragment {

    private RegistrationActivityViewModel viewModel;

    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutConfirmPassword;
    private Button buttonContinue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        viewModel = new ViewModelProvider(getActivity()).get(RegistrationActivityViewModel.class);
        viewModel.setActivityTitle(getString(R.string.registration));
        View view = inflater.inflate(R.layout.fragment_registration_password, container, false);
        setupPasswordInput(view);
        setupPasswordConfirmInput(view);
        setupContinueButton(view);
        validateInputs();
        return view;
    }

    @SuppressLint("CheckResult")
    private void setupPasswordInput(View view){
        textInputLayoutPassword = view.findViewById(R.id.textInputLayoutPassword);
        textInputLayoutPassword.getEditText().setText(viewModel.getPassword() != null ? viewModel.getPassword() : "");
        TextInputHelper.editTextObservable(textInputLayoutPassword).subscribe((s) -> validateInputs());
    }

    @SuppressLint("CheckResult")
    private void setupPasswordConfirmInput(View view){
        textInputLayoutConfirmPassword = view.findViewById(R.id.textInputLayoutConfirmPassword);
        textInputLayoutConfirmPassword.getEditText().setText(viewModel.getPassword() != null ? viewModel.getPassword() : "");
        TextInputHelper.editTextObservable(textInputLayoutConfirmPassword).subscribe((s) -> validateInputs());
    }


    private void setupContinueButton(View view){
        buttonContinue = view.findViewById(R.id.buttonContinue);
        buttonContinue.setOnClickListener(v -> {
            String password = textInputLayoutPassword.getEditText().getText().toString();
            viewModel.setPassword(password);
            NavHostFragment.findNavController(this).navigate(R.id.registrationInterestsFragment);
        });
    }

    private void validateInputs(){
        String password = textInputLayoutPassword.getEditText().getText().toString();
        String passwordConfirmation = textInputLayoutConfirmPassword.getEditText().getText().toString();
        buttonContinue.setEnabled(!password.isEmpty() && password.equals(passwordConfirmation));
    }

}
