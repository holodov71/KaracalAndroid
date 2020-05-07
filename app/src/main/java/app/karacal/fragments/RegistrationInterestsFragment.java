package app.karacal.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.igalata.bubblepicker.BubblePickerListener;
import com.igalata.bubblepicker.adapter.BubblePickerAdapter;
import com.igalata.bubblepicker.model.PickerItem;
import com.igalata.bubblepicker.rendering.BubblePicker;

import java.util.Locale;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.adapters.InterestsBubblePickerAdapter;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.ProfileHolder;
import app.karacal.helpers.ToastHelper;
import app.karacal.navigation.NavigationHelper;
import app.karacal.retrofit.models.LoginRequest;
import app.karacal.retrofit.models.RegisterRequest;
import app.karacal.viewmodels.RegistrationActivityViewModel;
import apps.in.android_logger.LogFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RegistrationInterestsFragment extends LogFragment {

    private static final int BUBBLE_SIZE = 50;
    private RegistrationActivityViewModel viewModel;

    private TextView textViewInterestsCount;
    private Button buttonContinue;
    private BubblePicker bubblePicker;

    private Disposable registrationDisposable;

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
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        viewModel = new ViewModelProvider(getActivity()).get(RegistrationActivityViewModel.class);
        viewModel.setActivityTitle(getString(R.string.interests));
        View view = inflater.inflate(R.layout.fragment_registration_interests, container, false);
        setupInterestsCount(view);
        setupContinueButton(view);
        setupSkipButton(view);
        setupBubblePicker(view);
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

    private void setupInterestsCount(View view) {
        textViewInterestsCount = view.findViewById(R.id.textViewInterestsCount);
        viewModel.getInterestCount().observe(this, count -> {
            textViewInterestsCount.setText(String.format(Locale.getDefault(), "%02d / %02d", count, viewModel.MAX_INTERESTS));
            buttonContinue.setEnabled(count > 0);
        });
    }

    private void setupContinueButton(View view) {
        buttonContinue = view.findViewById(R.id.buttonContinue);
        buttonContinue.setOnClickListener(v -> {
            if (registrationDisposable == null) {
                finishRegistration();
            }
        });
    }

    private void setupSkipButton(View view) {
        TextView textView = view.findViewById(R.id.textViewSkip);
        textView.setOnClickListener(v -> {
            if (registrationDisposable == null) {
                finishRegistration();
            }
        });
    }

    private void setupBubblePicker(View view) {
        bubblePicker = view.findViewById(R.id.bubblePicker);
        bubblePicker.setBackground(Color.TRANSPARENT);
        bubblePicker.setCenterImmediately(true);
        bubblePicker.setMaxSelectedCount(RegistrationActivityViewModel.MAX_INTERESTS);
        bubblePicker.setBubbleSize(BUBBLE_SIZE);
        bubblePicker.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(PickerItem pickerItem) {
                viewModel.toggleInterestState(pickerItem.getTitle());
            }

            @Override
            public void onBubbleDeselected(PickerItem pickerItem) {
                viewModel.toggleInterestState(pickerItem.getTitle());
            }
        });
    }

    private void setupBubblePickerAdapter() {
        BubblePickerAdapter adapter = new InterestsBubblePickerAdapter(getContext(), viewModel.getInterests());
        bubblePicker.setAdapter(adapter);
    }

    private void finishRegistration() {
        RegisterRequest registerRequest = viewModel.getRegisterRequest();
        LoginRequest loginRequest = viewModel.getLoginRequest();
        registrationDisposable = apiHelper.register(registerRequest).andThen(apiHelper.login(loginRequest)).flatMap(apiHelper::getProfile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(profile -> {
                            profileHolder.setProfile(profile);
                            NavigationHelper.startMainActivity(getActivity());
                            getActivity().finishAffinity();
                            registrationDisposable = null;
                        },
                        throwable -> {
                            ToastHelper.showToast(getContext(), throwable.getMessage());
                            registrationDisposable = null;
                        });
    }


}
