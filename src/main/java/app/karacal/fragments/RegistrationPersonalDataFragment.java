package app.karacal.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import app.karacal.R;
import app.karacal.helpers.TextInputHelper;
import app.karacal.viewmodels.RegistrationActivityViewModel;
import apps.in.android_logger.LogFragment;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RegistrationPersonalDataFragment extends LogFragment implements DatePickerDialog.OnDateSetListener {

    private static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE);

    private RegistrationActivityViewModel viewModel;

    private TextInputLayout textInputLayoutFirstName;
    private TextInputLayout textInputLayoutSecondName;
    private TextInputLayout textInputLayoutBirthDate;
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
        setupBirthDateInput(view);
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

    private void setupBirthDateInput(View view){
        textInputLayoutBirthDate = view.findViewById(R.id.textInputLayoutBirthDate);
        textInputLayoutBirthDate.getEditText().setText(viewModel.getBirthDate() != null ? dateFormat.format(viewModel.getBirthDate()) : "");
        ImageView buttonCalendar = view.findViewById(R.id.buttonCalendar);
        buttonCalendar.setOnClickListener(v -> showDatePickerDialog());
    }

    private void setupContinueButton(View view){
        buttonContinue = view.findViewById(R.id.buttonContinue);
        buttonContinue.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(R.id.registrationContactsFragment));
    }

    private void validateInputs(){
        buttonContinue.setEnabled(viewModel.getFirstName() != null && viewModel.getSecondName() != null && viewModel.getBirthDate() != null);
    }

    private void showDatePickerDialog(){
        Calendar calendar = Calendar.getInstance();
        if (viewModel.getBirthDate() != null){
            calendar.setTime(viewModel.getBirthDate());
        }
        new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(y, m, d);
        viewModel.setBirthDate(calendar.getTime());
        textInputLayoutBirthDate.getEditText().setText(viewModel.getBirthDate() != null ? dateFormat.format(viewModel.getBirthDate()) : "");
        validateInputs();
    }
}
