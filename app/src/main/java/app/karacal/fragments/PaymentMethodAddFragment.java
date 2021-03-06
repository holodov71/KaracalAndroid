package app.karacal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.stripe.android.model.Card;
import com.stripe.android.view.CardMultilineWidget;

import app.karacal.App;
import app.karacal.R;
import app.karacal.data.ProfileCache;
import app.karacal.helpers.KeyboardHelper;
import app.karacal.models.CardDetails;
import app.karacal.viewmodels.PaymentMethodViewModel;
import io.reactivex.disposables.CompositeDisposable;

public class PaymentMethodAddFragment extends Fragment {

    private PaymentMethodViewModel viewModel;

    private CompositeDisposable disposable = new CompositeDisposable();

    private Button createButton;
    private ProgressBar progressLoading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        viewModel = new ViewModelProvider(getActivity()).get(PaymentMethodViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_method_add, container, false);
        viewModel.setActivityTitle(getString(R.string.add_payment_method_title));
        setupLoading(view);
        setupCardInput(view);
        observePaymentMethods();
        return view;
    }

    private void setupLoading(View view) {
        progressLoading = view.findViewById(R.id.progressLoading);
    }

    private void setupCardInput(View view){
        CardMultilineWidget cardInputWidget = view.findViewById(R.id.cardInputWidget);
        cardInputWidget.setShouldShowPostalCode(false);
        cardInputWidget.setCardHint(getString(R.string.card_hint));

        createButton = view.findViewById(R.id.payButton);
        createButton.setOnClickListener(v -> {
            KeyboardHelper.hideKeyboard(getContext(), v);

            Card card = cardInputWidget.getCard();

            if (card != null) {
                Integer expiryYear = card.getExpYear();
                Integer expiryMonth = card.getExpMonth();

                if (expiryYear != null && expiryMonth != null) {

                    showLoading();

                    if (getActivity() != null) {
                        String secondName = ProfileCache.getInstance(getActivity()).getProfile().getSecondName();

                        CardDetails cardDetails = new CardDetails(
                                card.getNumber(),
                                card.getCvc(),
                                expiryMonth,
                                expiryYear,
                                card.getBrand().getDisplayName(),
                                secondName);
                        viewModel.addPaymentMethod(getContext(), cardDetails);
                        hideLoading();
                        getActivity().onBackPressed();
                    }
                }
            }
        });
    }

    private void observePaymentMethods(){
    }

    private void showLoading(){
        createButton.setVisibility(View.GONE);
        progressLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        createButton.setVisibility(View.VISIBLE);
        progressLoading.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null) disposable.dispose();
    }

}
