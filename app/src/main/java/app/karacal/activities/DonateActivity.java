package app.karacal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;

import java.io.Serializable;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.ImageHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.helpers.WebLinkHelper;
import app.karacal.models.Guide;
import app.karacal.navigation.ActivityArgs;
import app.karacal.navigation.NavigationHelper;
import apps.in.android_logger.LogActivity;
import io.reactivex.disposables.CompositeDisposable;

public class DonateActivity extends LogActivity {

    public static class Args extends ActivityArgs implements Serializable {

        private final int guideId;

        public Args(int guideId) {
            this.guideId = guideId;
        }

        public int getGuideId() {
            return guideId;
        }

    }

    private static final int AMOUNT_SMALL = 3;
    private static final int AMOUNT_MEDIUM = 5;
    private static final int AMOUNT_BIG = 7;
    private Button buttonDonate;
    private TextView textViewDonateAmountSmall;
    private TextView textViewDonateAmountMedium;
    private TextView textViewDonateAmountBig;
    TextInputLayout inputLayoutAmount;

    private ImageView avatar;
    private TextView textViewName;
    private TextView textViewLocation;

    private int guideId;

    private Integer donation = -1;

    @Inject
    ApiHelper apiHelper;

    private CompositeDisposable disposable = new CompositeDisposable();

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //do nothing
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //do nothing
        }

        @Override
        public void afterTextChanged(Editable s) {
            String amount = s.toString();
            try {
                Double d = Double.parseDouble(amount);
                unCheckAmountButtons();
//                donation = d;
                validate();
            } catch (Exception e) {
                donation = -1;
                s.clear();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        setContentView(R.layout.activity_donate);

        Args args = ActivityArgs.fromBundle(Args.class, getIntent().getExtras());
        guideId = args.getGuideId();

        setupBackButton();
        setupGuideInfo();
        setupAmountButtons();
        setupAmountInput();
        setupDonateButton();
        validate();

        loadGuide();
    }

    private void setupBackButton(){
        ImageView button = findViewById(R.id.buttonBack);
        button.setOnClickListener(v -> onBackPressed());
    }

    private void setupGuideInfo(){
        avatar = findViewById(R.id.imageViewAvatar);
        textViewName = findViewById(R.id.textViewGuideName);
        textViewLocation = findViewById(R.id.textViewGuideLocation);
    }

    private void setupAmountButtons(){
        textViewDonateAmountSmall = findViewById(R.id.buttonDonateSmall);
        textViewDonateAmountSmall.setText(getString(R.string.donate_amount_format, AMOUNT_SMALL, getString(R.string.euro)));
        textViewDonateAmountSmall.setOnClickListener(v -> setDonationAmount(v, AMOUNT_SMALL));
        textViewDonateAmountMedium = findViewById(R.id.buttonDonateMedium);
        textViewDonateAmountMedium.setText(getString(R.string.donate_amount_format, AMOUNT_MEDIUM, getString(R.string.euro)));
        textViewDonateAmountMedium.setOnClickListener(v -> setDonationAmount(v, AMOUNT_MEDIUM));
        textViewDonateAmountBig = findViewById(R.id.buttonDonateBig);
        textViewDonateAmountBig.setText(getString(R.string.donate_amount_format, AMOUNT_BIG, getString(R.string.euro)));
        textViewDonateAmountBig.setOnClickListener(v -> setDonationAmount(v, AMOUNT_BIG));
    }

    private void setupAmountInput(){
        inputLayoutAmount = findViewById(R.id.textInputLayoutDonateAmount);
        inputLayoutAmount.setVisibility(View.GONE);
//        inputLayoutAmount.getEditText().addTextChangedListener(watcher);
    }

    private void setupDonateButton(){
        buttonDonate = findViewById(R.id.buttonDonate);
        buttonDonate.setOnClickListener(v -> {
            PaymentActivity.Args args = new PaymentActivity.Args(null, guideId, (long)(donation * 100), null);
            NavigationHelper.startPaymentActivity(DonateActivity.this, args);
        });
    }

    private void unCheckAmountButtons(){
        textViewDonateAmountSmall.setSelected(false);
        textViewDonateAmountMedium.setSelected(false);
        textViewDonateAmountBig.setSelected(false);
    }

    private void setDonationAmount(View v, int amount){
        unCheckAmountButtons();
        v.setSelected(true);
        donation = amount;
//        inputLayoutAmount.getEditText().removeTextChangedListener(watcher);
        inputLayoutAmount.getEditText().setText(String.valueOf(amount));
//        inputLayoutAmount.getEditText().addTextChangedListener(watcher);
        validate();
    }

    private void validate(){
        buttonDonate.setEnabled(donation > 0);
    }

    private void setGuideData(Guide guide) {
        textViewName.setText(guide.getName());
        textViewLocation.setText(guide.getLocalization());
        ImageHelper.setImage(avatar, guide.getAvatarUrl(), R.drawable.ic_person, true);
    }

    private void loadGuide(){
        disposable.add(apiHelper.loadGuide(PreferenceHelper.loadToken(App.getInstance()), String.valueOf(guideId))
                .subscribe(response -> setGuideData(new Guide(response)),
                        throwable -> finish()
                ));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null && resultCode == RESULT_OK) {
            if (requestCode == PaymentActivity.REQUEST_CODE) {
                String url = data.getStringExtra(PaymentActivity.RESULT_URL);
                if (url != null){
                    WebLinkHelper.openWebLink(this, url);
                    new Handler().postDelayed(this::finish, 100);
                }
                Log.v("onActivityResult", "Payment completed");
            }
        }
    }
}
