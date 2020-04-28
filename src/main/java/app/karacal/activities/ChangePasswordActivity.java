package app.karacal.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.List;

import app.karacal.R;
import app.karacal.helpers.DummyHelper;
import app.karacal.helpers.TextInputHelper;
import apps.in.android_logger.LogActivity;
import io.reactivex.Observable;

public class ChangePasswordActivity extends LogActivity {

    private ImageView buttonApply;
    private Button buttonUpdate;
    private TextInputLayout textInputLayoutOldPassword;
    private TextInputLayout textInputLayoutNewPassword;
    private TextInputLayout textInputLayoutNewPasswordRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setupBackButton();
        setupApplyButton();
        setupUpdateButton();
        setupCancelButton();
        setupInputs();
        validate();
    }

    private void setupBackButton() {
        ImageView button = findViewById(R.id.buttonBack);
        button.setOnClickListener(v -> onBackPressed());
    }

    private void setupApplyButton() {
        buttonApply = findViewById(R.id.buttonApply);
        buttonApply.setOnClickListener(v -> proceed());
    }

    private void setupUpdateButton() {
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(v -> proceed());
    }

    private void setupCancelButton() {
        TextView button = findViewById(R.id.buttonCancel);
        button.setOnClickListener(v -> onBackPressed());
    }

    private void setupInputs() {
        textInputLayoutOldPassword = findViewById(R.id.textInputLayoutOldPassword);
        textInputLayoutNewPassword = findViewById(R.id.textInputLayoutNewPassword);
        textInputLayoutNewPasswordRepeat = findViewById(R.id.textInputLayoutNewPasswordRepeat);
        List<Observable<String>> observables = Arrays.asList(TextInputHelper.editTextObservable(textInputLayoutOldPassword), TextInputHelper.editTextObservable(textInputLayoutNewPassword), TextInputHelper.editTextObservable(textInputLayoutNewPasswordRepeat));
        Observable.merge(observables).subscribe(s -> validate());
    }

    private void validate(){
        String oldPassword = textInputLayoutOldPassword.getEditText().getText().toString();
        String newPassword = textInputLayoutNewPassword.getEditText().getText().toString();
        String newPasswordRepeat = textInputLayoutNewPasswordRepeat.getEditText().getText().toString();
        boolean isValid = !TextUtils.isEmpty(oldPassword) && !TextUtils.isEmpty(newPassword) && newPassword.equals(newPasswordRepeat);
        buttonApply.setVisibility(isValid ? View.VISIBLE : View.INVISIBLE);
        buttonUpdate.setEnabled(isValid);
    }

    private void proceed() {
        DummyHelper.dummyAction(this);
        finish();
    }
}
