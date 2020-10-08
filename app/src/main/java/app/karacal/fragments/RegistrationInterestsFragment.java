package app.karacal.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.igalata.bubblepicker.BubblePickerListener;
import com.igalata.bubblepicker.adapter.BubblePickerAdapter;
import com.igalata.bubblepicker.model.PickerItem;
import com.igalata.bubblepicker.rendering.BubblePicker;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.adapters.InterestsBubblePickerAdapter;
import app.karacal.data.ProfileCache;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.ToastHelper;
import app.karacal.navigation.NavigationHelper;
import app.karacal.network.models.request.LoginRequest;
import app.karacal.network.models.request.RegisterRequest;
import app.karacal.viewmodels.RegistrationActivityViewModel;
import apps.in.android_logger.LogFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RegistrationInterestsFragment extends LogFragment {

    private static final int BUBBLE_SIZE = 50;
    private RegistrationActivityViewModel viewModel;

    private TextView textViewInterestsCount;
    private Button buttonContinue;
    private TextView skipButton;
    private BubblePicker bubblePicker;
    private ProgressBar progressLoading;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    ApiHelper apiHelper;

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
        viewModel.setActivityTitle(getString(R.string.interests));
        View view = inflater.inflate(R.layout.fragment_registration_interests, container, false);
        setupInterestsCount(view);
        setupContinueButton(view);
        setupSkipButton(view);
        setupBubblePicker(view);
        setupProgressLoading(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        bubblePicker.onResume();
        setupBubblePickerAdapter();
    }

    @Override
    public void onPause() {
        super.onPause();
        bubblePicker.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    private void setupInterestsCount(View view) {
        textViewInterestsCount = view.findViewById(R.id.textViewInterestsCount);
        viewModel.getInterestCount().observe(getViewLifecycleOwner(), count -> {
            textViewInterestsCount.setText(String.format(Locale.getDefault(), "%02d / %02d", count, viewModel.MAX_INTERESTS));
            buttonContinue.setEnabled(count > 0);
        });
    }

    private void setupContinueButton(View view) {
        buttonContinue = view.findViewById(R.id.buttonContinue);
        buttonContinue.setOnClickListener(v -> registerUser());
    }

    private void setupProgressLoading(View view) {
        progressLoading = view.findViewById(R.id.progressLoading);
    }

    private void setupSkipButton(View view) {
        skipButton = view.findViewById(R.id.textViewSkip);
        skipButton.setOnClickListener(v -> registerUser());
    }

    private void setupBubblePicker(View view) {
        bubblePicker = view.findViewById(R.id.bubblePicker);
        bubblePicker.setBackground(Color.TRANSPARENT);
        bubblePicker.setCenterImmediately(true);
        bubblePicker.setMaxSelectedCount(RegistrationActivityViewModel.MAX_INTERESTS);
        bubblePicker.setBubbleSize(BUBBLE_SIZE);
        bubblePicker.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(@NotNull PickerItem pickerItem) {
                viewModel.toggleInterestState(pickerItem.getTitle());
            }

            @Override
            public void onBubbleDeselected(@NotNull PickerItem pickerItem) {
                viewModel.toggleInterestState(pickerItem.getTitle());
            }
        });
    }

    private void setupBubblePickerAdapter() {
        BubblePickerAdapter adapter = new InterestsBubblePickerAdapter(getContext(), viewModel.getInterests());
        bubblePicker.setAdapter(adapter);
    }

    private void showLoading(){
        progressLoading.setVisibility(View.VISIBLE);
        buttonContinue.setVisibility(View.INVISIBLE);
        skipButton.setVisibility(View.INVISIBLE);
    }

    private void hideLoading(){
        progressLoading.setVisibility(View.GONE);
        buttonContinue.setVisibility(View.VISIBLE);
        skipButton.setVisibility(View.VISIBLE);
    }

    private void registerUser(){
        showLoading();
        RegisterRequest registerRequest = viewModel.getRegisterRequest();
        disposable.add(apiHelper.register(registerRequest)
                .subscribe(response -> {
                    if (response.isSuccess()){
                        login();
                    } else {
                        hideLoading();
                        ToastHelper.showToast(getContext(), response.getErrorMessage());
                    }
                },
                throwable -> {
                    hideLoading();
                    ToastHelper.showToast(getContext(), throwable.getMessage());
                })
        );
    }

    private void login() {
        LoginRequest loginRequest = viewModel.getLoginRequest();
        disposable.add(apiHelper.login(loginRequest)
                .flatMap(apiHelper::getProfile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(profile -> {
                            FragmentActivity activity = getActivity();

                            if (activity != null) {
                                ProfileCache.getInstance(activity).setProfile(activity, profile);
                                NavigationHelper.startMainActivity(activity);
                                activity.finishAffinity();
                            }
                        },
                        throwable -> {
                            hideLoading();
                            ToastHelper.showToast(getContext(), throwable.getMessage());
                        }));
    }


}
