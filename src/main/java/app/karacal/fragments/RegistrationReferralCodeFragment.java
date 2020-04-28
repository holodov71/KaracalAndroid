package app.karacal.fragments;

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
import androidx.navigation.fragment.NavHostFragment;

import app.karacal.R;
import app.karacal.viewmodels.RegistrationActivityViewModel;
import app.karacal.views.ReferralCodeEditText;
import apps.in.android_logger.LogFragment;

public class RegistrationReferralCodeFragment extends LogFragment {

    private static final int REFERRAL_CODE_LENGTH = 6;
    private RegistrationActivityViewModel viewModel;
    private Button buttonContinue;
    private ReferralCodeEditText referralCodeView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        viewModel = new ViewModelProvider(getActivity()).get(RegistrationActivityViewModel.class);
        viewModel.setActivityTitle(getString(R.string.referral_code));
        View view = inflater.inflate(R.layout.fragment_registration_referral_code, container, false);
        setupIHaveNoPromoCodeButton(view);
        setupContinueButton(view);
        setupReferralCodeView(view);
        return view;
    }

    private void setupIHaveNoPromoCodeButton(View view){
        TextView textView = view.findViewById(R.id.textViewIHaveNoPromoCode);
        textView.setOnClickListener(v -> {
            viewModel.setReferralCode(null);
            NavHostFragment.findNavController(this).popBackStack();
        });
    }

    private void setupContinueButton(View view){
        buttonContinue = view.findViewById(R.id.buttonContinue);
        buttonContinue.setOnClickListener(v -> {
            viewModel.setReferralCode(referralCodeView.getReferralCode());
            NavHostFragment.findNavController(this).popBackStack();
        });
    }

    private void setupReferralCodeView(View view){
        referralCodeView = view.findViewById(R.id.referralCodeView);
        referralCodeView.setReferralCodeChangeListener(code -> buttonContinue.setEnabled(code.length() == REFERRAL_CODE_LENGTH));
        referralCodeView.setReferralCode("");
    }
}
