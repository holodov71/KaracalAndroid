package app.karacal.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stripe.android.ApiResultCallback;
import com.stripe.android.CustomerSession;
import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.Stripe;
import com.stripe.android.model.Address;
import com.stripe.android.model.Card;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.model.ShippingInformation;
import com.stripe.android.model.ShippingMethod;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;
import com.stripe.android.view.CardMultilineWidget;
import com.stripe.android.view.ShippingInfoWidget;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.KeyboardHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.helpers.ToastHelper;
import app.karacal.navigation.ActivityArgs;
import app.karacal.retrofit.models.request.PaymentRequest;
import apps.in.android_logger.LogActivity;
import io.reactivex.disposables.Disposable;

public class PaymentActivity extends LogActivity {

    public static final int REQUEST_CODE = 501;
    public static final String RESULT_URL = "result_url";

    public static class Args extends ActivityArgs implements Serializable {

        private final Integer tourId;

        private final Long amount;

        public Args(Integer tourId, Long amount) {
            this.tourId = tourId;
            this.amount = amount;
        }

        public Integer getTourId() {
            return tourId;
        }

        public Long getAmount() {
            return amount;
        }
    }

    @Inject
    ApiHelper apiHelper;

    private Disposable disposable;

    private Stripe stripe;
    private String paymentIntentClientSecret;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        setContentView(R.layout.activity_payment);

        Args args = ActivityArgs.fromBundle(Args.class, getIntent().getExtras());
        int tourId = args.getTourId();
        long amount = args.getAmount();

        paymentIntentClientSecret = getString(R.string.stripe_secret_api_key);
        stripe = new Stripe(getApplicationContext(), getString(R.string.stripe_publishable_api_key));

        setupBackButton();
        setupCardInput(amount);

//        CustomerSession.initCustomerSession(this, apiHelper);
    }

    private void setupBackButton() {
        ImageView buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupCardInput(long amount){
        CardMultilineWidget cardInputWidget = findViewById(R.id.cardInputWidget);
        cardInputWidget.setShouldShowPostalCode(false);

        Button payButton = findViewById(R.id.payButton);
        payButton.setOnClickListener(v -> {
            KeyboardHelper.hideKeyboard(PaymentActivity.this, v);

//            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
//            if (params != null){
//                stripe.createPaymentMethod(params, new ApiResultCallback<PaymentMethod>() {
//                    @Override
//                    public void onSuccess(PaymentMethod paymentMethod) {
//
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
                stripe.createCardToken(cardInputWidget.getCard(), new ApiResultCallback<Token>() {
                    @Override
                    public void onSuccess(Token token) {
                        pay(token.getId(), amount);
                    }

                    @Override
                    public void onError(@NotNull Exception e) {

                    }
                });



            }
        });
    }

    private void pay(String token, long amount){
        if (disposable != null){
            disposable.dispose();
        }
        PaymentRequest request = new PaymentRequest(amount, "eur", token, "Paris tour description");
        disposable = apiHelper.makePayment(PreferenceHelper.loadToken(this), request)
                .subscribe(response -> {
                    Log.e("makePayment", "Success response = " + response);
                    ToastHelper.showToast(this, "Payment Success");
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_URL, response.getReceiptUrl());
                    setResult(RESULT_OK, intent);
                    finish();

                }, throwable -> {
                    Log.e("makePayment", "Error: " +throwable.getMessage());
                    // TODO: load list from DB
                });
    }

//    @NonNull
//    private PaymentSessionConfig createPaymentSessionConfig() {
//        return new PaymentSessionConfig.Builder()
//
//                // hide the phone field on the shipping information form
//                .setHiddenShippingInfoFields(
//                        ShippingInfoWidget.CustomizableShippingField.PHONE_FIELD
//                )
//
//                // make the address line 2 field optional
//                .setOptionalShippingInfoFields(
//                        ShippingInfoWidget.CustomizableShippingField.ADDRESS_LINE_TWO_FIELD
//                )
//
//                // specify an address to pre-populate the shipping information form
//                .setPrepopulatedShippingInfo(new ShippingInformation(
//                        new Address.Builder()
//                                .setLine1("123 Market St")
//                                .setCity("San Francisco")
//                                .setState("CA")
//                                .setPostalCode("94107")
//                                .setCountry("US")
//                                .build(),
//                        "Jenny Rosen",
//                        "4158675309"
//                ))
//
//                // collect shipping information
//                .setShippingInfoRequired(true)
//
//                // collect shipping method
//                .setShippingMethodsRequired(true)
//
//                // specify the payment method types that the customer can use;
//                // defaults to PaymentMethod.Type.Card
//                .setPaymentMethodTypes(Arrays.asList(PaymentMethod.Type.Card))
//
//                // only allow US and Canada shipping addresses
////                .setAllowedShippingCountryCodes(new HashSet<>(
////                        Arrays.asList("US", "CA")
////                ))
//
//                // specify a layout to display under the payment collection form
////                .setAddPaymentMethodFooter(R.layout.add_payment_method_footer)
//
//                // specify the shipping information validation delegate
////                .setShippingInformationValidator(new AppShippingInformationValidator())
//
////                // specify the shipping methods factory delegate
////                .setShippingMethodsFactory(new AppShippingMethodsFactory())
//
//                // if `true`, will show "Google Pay" as an option on the
//                // Payment Methods selection screen
//                .setShouldShowGooglePay(true)
//
//                .build();
//    }

}
