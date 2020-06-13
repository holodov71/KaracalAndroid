package app.karacal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.KeyboardHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.helpers.ProfileHolder;
import app.karacal.helpers.ToastHelper;
import app.karacal.navigation.ActivityArgs;
import app.karacal.retrofit.models.request.CreateCardRequest;
import app.karacal.retrofit.models.request.CreateCustomerRequest;
import app.karacal.retrofit.models.request.CreateSubscriptionRequest;
import app.karacal.retrofit.models.request.PaymentRequest;
import apps.in.android_logger.LogActivity;
import io.reactivex.disposables.CompositeDisposable;

public class PaymentActivity extends LogActivity {

    public static final int REQUEST_CODE = 501;
    public static final String RESULT_URL = "result_url";

    public static class Args extends ActivityArgs implements Serializable {

        private final Integer tourId;

        private final Long amount;

        private final String subscriptionId;

        public Args(Integer tourId, Long amount, String subscriptionId) {
            this.tourId = tourId;
            this.amount = amount;
            this.subscriptionId = subscriptionId;
        }

        public Integer getTourId() {
            return tourId;
        }

        public Long getAmount() {
            return amount;
        }

        public String getSubscriptionId() {
            return subscriptionId;
        }
    }

    @Inject
    ApiHelper apiHelper;

    @Inject
    ProfileHolder profileHolder;

    private CompositeDisposable disposable = new CompositeDisposable();

    private Stripe stripe;
    private String paymentIntentClientSecret;

    private long amount;
    private int tourId;
    private String subscriptionId;

    private Button payButton;
    private ProgressBar progressLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        setContentView(R.layout.activity_payment);

        Args args = ActivityArgs.fromBundle(Args.class, getIntent().getExtras());
        tourId = args.getTourId();
        amount = args.getAmount();
        subscriptionId = args.getSubscriptionId();

        paymentIntentClientSecret = getString(R.string.stripe_secret_api_key);
        stripe = new Stripe(getApplicationContext(), getString(R.string.stripe_publishable_api_key));

        setupLoading();
        setupBackButton();
        setupCardInput();
    }

    private void setupBackButton() {
        ImageView buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupLoading() {
        progressLoading = findViewById(R.id.progressLoading);
    }

    private void setupCardInput(){
        CardMultilineWidget cardInputWidget = findViewById(R.id.cardInputWidget);
        cardInputWidget.setShouldShowPostalCode(false);

        payButton = findViewById(R.id.payButton);
        payButton.setOnClickListener(v -> {
            KeyboardHelper.hideKeyboard(PaymentActivity.this, v);

//            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
//            if (params != null){
//
//                stripe.createPaymentMethod(params, new ApiResultCallback<PaymentMethod>() {
//                    @Override
//                    public void onSuccess(PaymentMethod paymentMethod) {
//                    }
//
//                    @Override
//                    public void onError(@NotNull Exception e) {
//
//                    }
//                });
//
//            }

            if (cardInputWidget.getCard() != null) {
                Log.v("PaymentMethodCreate", "cardInputWidget.getCard() = "+cardInputWidget.getCard());

//                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
//                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
//                stripe.confirmPayment(this, confirmParams);

//                Card card = Card.create("4242424242424242", params., paymentMethod.card.expiryYear, "111");
//
                showLoading();
                stripe.createCardToken(cardInputWidget.getCard(), new ApiResultCallback<Token>() {
                    @Override
                    public void onSuccess(Token token) {
                        makePayment(token.getId());
                    }

                    @Override
                    public void onError(@NotNull Exception e) {
                        hideLoading();
                        ToastHelper.showToast(PaymentActivity.this, getString(R.string.common_error));
                    }
                });



            }
        });
    }

    private void makePayment(String cardToken){
        if (subscriptionId != null){
            createCustomer(cardToken);
        }else {
            payTour(cardToken);
        }
    }

    private void payTour(String token){
        PaymentRequest request = new PaymentRequest(amount, "eur", token, "Paris tour description", tourId);
        disposable.add(apiHelper.makePayment(PreferenceHelper.loadToken(this), request)
                .subscribe(response -> {
                    Log.v("makePayment", "Success response = " + response);
                    if (response.isSuccess()) {
                        ToastHelper.showToast(this, getString(R.string.payment_success));
                        Intent intent = new Intent();
                        intent.putExtra(RESULT_URL, response.getReceiptUrl());
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        hideLoading();
                        ToastHelper.showToast(this, response.getErrorMessage());
                    }
                }, throwable -> {
                    hideLoading();
                    ToastHelper.showToast(this, getString(R.string.connection_problem));
                }));
    }

    private void createCustomer(String cardToken){

        String serverToken = PreferenceHelper.loadToken(this);

        CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest(profileHolder.getProfile().getEmail());
        disposable.add(apiHelper.createCustomer(serverToken, createCustomerRequest)
                .subscribe(response -> {
                    Log.v("createCustomer", "Success response = " + response);
                    if (response.isSuccess()) {
                        createCard(response.getId(), cardToken);
                    } else {
                        hideLoading();
                        ToastHelper.showToast(this, response.getErrorMessage());
                    }
                }, throwable -> {
                    hideLoading();
                    ToastHelper.showToast(this, getString(R.string.connection_problem));
                }));
    }

    private void createCard(String customerId, String cardToken){
        String serverToken = PreferenceHelper.loadToken(this);

        CreateCardRequest createCardRequest = new CreateCardRequest(customerId, cardToken);
        disposable.add(apiHelper.createCard(serverToken, createCardRequest)
                .subscribe(response -> {
                    Log.v("createCard", "Success response = " + response);
                    if (response.isSuccess()) {
                        createSubscription(customerId);
                    } else {
                        hideLoading();
                        ToastHelper.showToast(this, response.getErrorMessage());
                    }
                }, throwable -> {
                    hideLoading();
                    ToastHelper.showToast(this, getString(R.string.connection_problem));
                }));
    }

    private void createSubscription(String customerId){

        CreateSubscriptionRequest createSubscriptionRequest = new CreateSubscriptionRequest(customerId, subscriptionId);
        disposable.add(apiHelper.createSubscription(PreferenceHelper.loadToken(this), createSubscriptionRequest)
                .subscribe(response -> {
                    Log.v("createCustomer", "Success response = " + response);
                    if (response.isSuccess()) {
                        profileHolder.setSubscription(response.getSubscriptionId());
                        ToastHelper.showToast(this, getString(R.string.payment_success));
                        Intent intent = new Intent();
                        intent.putExtra(RESULT_URL, response.getSubscription());
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        hideLoading();
                        ToastHelper.showToast(this, response.getErrorMessage());
                    }
                }, throwable -> {
                    hideLoading();
                    ToastHelper.showToast(this, getString(R.string.connection_problem));
                }));
    }

    private void showLoading(){
        payButton.setVisibility(View.GONE);
        progressLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        payButton.setVisibility(View.VISIBLE);
        progressLoading.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) disposable.dispose();
    }
}
