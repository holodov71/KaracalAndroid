package app.karacal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import app.karacal.R;
import app.karacal.helpers.ToastHelper;
import app.karacal.viewmodels.SubscriptionActivityViewModel;
import apps.in.android_logger.LogActivity;

public class SubscriptionActivity extends LogActivity {

    public static final int SUBSCRIPTION_REQUEST_CODE = 756;
    public static final String RESULT_URL = "result_url";

    private SubscriptionActivityViewModel viewModel;

    private TextView tvPaymentMethodMissing;
    private Button buttonSubscribe;
    private ProgressBar progressLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        viewModel = new ViewModelProvider(this).get(SubscriptionActivityViewModel.class);
        setupBackButton();
        initView();
        observeViewModel();
    }

    private void initView(){
        tvPaymentMethodMissing = findViewById(R.id.tvPaymentMethodMissing);
        buttonSubscribe = findViewById(R.id.buttonSubscribe);
        progressLoading = findViewById(R.id.progressLoading);
    }

    private void setupBackButton(){
        ImageView button = findViewById(R.id.buttonBack);
        button.setOnClickListener(v -> onBackPressed());
    }

    private void setData(boolean hasPaymentMethod){
        if (hasPaymentMethod){
            tvPaymentMethodMissing.setVisibility(View.GONE);
            buttonSubscribe.setEnabled(true);
            buttonSubscribe.setOnClickListener(v -> viewModel.toggleSubscription());
        } else {
            tvPaymentMethodMissing.setVisibility(View.VISIBLE);
            buttonSubscribe.setEnabled(false);
        }
    }

    private void setupSubscribeButtonText(boolean hasSubscription){
        String text;
        if (hasSubscription){
            text = getString(R.string.cancel_subscription);
        } else {
            text = getString(R.string.action_subscribe);
        }
        buttonSubscribe.setText(text);
    }

    private void showLoading(boolean isLoading){
        progressLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        buttonSubscribe.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
    }

    private void onSubscriptionOpened(String subscriptionReceipt){
        ToastHelper.showToast(this, getString(R.string.payment_success));
        Intent intent = new Intent();
        intent.putExtra(RESULT_URL, subscriptionReceipt);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void observeViewModel(){
        viewModel.errorEvent.observe(this, text -> ToastHelper.showToast(this, text));
        viewModel.finishEvent.observe(this, v -> finish());

        viewModel.isLoading().observe(this, this::showLoading);
        viewModel.hasPaymentMethod.observe(this, this::setData);
        viewModel.hasSubscription.observe(this, this::setupSubscribeButtonText);

        viewModel.subscriptionOpenedEvent.observe(this, this::onSubscriptionOpened);

        viewModel.subscriptionCanceledEvent.observe(this, v -> {
            ToastHelper.showToast(this, getString(R.string.subscription_canceled));
            finish();
        });
    }

}
