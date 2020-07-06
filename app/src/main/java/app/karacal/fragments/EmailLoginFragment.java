package app.karacal.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputLayout;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.dialogs.ForgotPasswordDialog;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.KeyboardHelper;
import app.karacal.helpers.ProfileHolder;
import app.karacal.helpers.ToastHelper;
import app.karacal.navigation.NavigationHelper;
import app.karacal.network.models.request.LoginRequest;
import app.karacal.network.models.request.ResetPasswordRequest;
import apps.in.android_logger.LogFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EmailLoginFragment extends LogFragment {

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private View progressLoading;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    ApiHelper apiHelper;

    @Inject
    ProfileHolder profileHolder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email_login, container, false);
        setupTextInputs(view);
        setupBackButton(view);
        setupForgotButton(view);
        setupRegisterButton(view);
        setupLoginButton(view);
        setupProgressLoading(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    private void setupTextInputs(View view) {
        textInputEmail = view.findViewById(R.id.textInputLayoutEmail);
        textInputPassword = view.findViewById(R.id.textInputLayoutPassword);
    }

    private void setupBackButton(View view) {
        ImageView backButton = view.findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
    }

    private void setupForgotButton(View view) {
        TextView textView = view.findViewById(R.id.textViewForgot);
        textView.setOnClickListener(v -> {
            ForgotPasswordDialog dialog = new ForgotPasswordDialog();
            dialog.setListener(email -> resetPassword(email, textView));
            dialog.show(getParentFragmentManager(), ForgotPasswordDialog.DIALOG_TAG);
        });
    }

    private void setupRegisterButton(View view) {
        TextView textView = view.findViewById(R.id.textViewRegister);
        textView.setOnClickListener(v -> NavigationHelper.startRegistrationActivity(getActivity()));
    }

    private void setupProgressLoading(View view) {
        progressLoading = view.findViewById(R.id.progressLoading);
    }

    private void setupLoginButton(View view) {
        Button button = view.findViewById(R.id.buttonLogin);
        button.setOnClickListener(v -> {
                progressLoading.setVisibility(View.VISIBLE);
                LoginRequest loginRequest = validateInputs();
                if (loginRequest != null) {
                    disposable.add(apiHelper.login(loginRequest)
                            .flatMap(apiHelper::getProfile)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(profile -> {
                                profileHolder.setProfile(profile);
                                NavigationHelper.startMainActivity(getActivity());
                                FragmentActivity activity = getActivity();
                                if (activity != null) {
                                    activity.finish();
                                }
                            }, throwable -> {
                                progressLoading.setVisibility(View.GONE);
                                ToastHelper.showToast(getContext(), throwable.getMessage());
                            }));
                }

        });
    }

    private LoginRequest validateInputs() {
        String email = textInputEmail.getEditText().getText().toString();
        if (TextUtils.isEmpty(email)) {
            textInputEmail.setError(getString(R.string.enter_email));
            return null;
        } else {
            textInputEmail.setError(null);
        }
        String password = textInputPassword.getEditText().getText().toString();
        if (TextUtils.isEmpty(password)) {
            textInputPassword.setError(getString(R.string.enter_password));
            return null;
        } else {
            textInputPassword.setError(null);
        }
        return new LoginRequest(email, password);
    }

    private void resetPassword(String email, View view){
        KeyboardHelper.hideKeyboard(getContext(), view);

        progressLoading.setVisibility(View.VISIBLE);

        disposable.add(apiHelper.resetPassword(new ResetPasswordRequest(email))
                .subscribe(response -> {
                    progressLoading.setVisibility(View.GONE);
                    if (response.isSuccess()){
                        NavigationHelper.startPasswordHasBeenResetActivity(getActivity());
                    } else {
                        ToastHelper.showToast(getContext(), response.getErrorMessage());
                    }
                }, throwable -> {
                    progressLoading.setVisibility(View.GONE);
                    ToastHelper.showToast(getContext(), getString(R.string.connection_problem));
                }));
    }

}
