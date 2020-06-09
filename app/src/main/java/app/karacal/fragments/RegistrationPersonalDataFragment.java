package app.karacal.fragments;

import android.os.Bundle;
import android.text.TextUtils;
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

public class RegistrationPersonalDataFragment extends LogFragment{


    private RegistrationActivityViewModel viewModel;

    private TextInputLayout textInputLayoutFirstName;
    private TextInputLayout textInputLayoutSecondName;
    private Button buttonContinue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        viewModel = new ViewModelProvider(getActivity()).get(RegistrationActivityViewModel.class);
        viewModel.setActivityTitle(getString(R.string.registration));
        View view = inflater.inflate(R.layout.fragment_registration_personal_data, container, false);
        setupFirstNameInput(view);
        setupSecondNameInput(view);
        setupContinueButton(view);
        validateInputs();
        return view;
    }

    private void setupFirstNameInput(View view){
        textInputLayoutFirstName = view.findViewById(R.id.textInputLayoutFirstName);
        textInputLayoutFirstName.getEditText().setText(viewModel.getFirstName() != null ? viewModel.getFirstName() : "");
        TextInputHelper.editTextObservable(textInputLayoutFirstName).subscribe((s) -> {
            viewModel.setFirstName(TextUtils.isEmpty(s) ? null : s);
            validateInputs();
        });
    }

    private void setupSecondNameInput(View view){
        textInputLayoutSecondName = view.findViewById(R.id.textInputLayoutSecondName);
        textInputLayoutSecondName.getEditText().setText(viewModel.getSecondName() != null ? viewModel.getSecondName() : "");
        TextInputHelper.editTextObservable(textInputLayoutSecondName).subscribe((s) -> {
            viewModel.setSecondName(TextUtils.isEmpty(s) ? null : s);
            validateInputs();
        });
    }

    private void setupContinueButton(View view){
        buttonContinue = view.findViewById(R.id.buttonContinue);
        buttonContinue.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(R.id.registrationContactsFragment));
    }

    private void validateInputs(){
        buttonContinue.setEnabled(viewModel.getFirstName() != null && viewModel.getSecondName() != null);
    }

}
