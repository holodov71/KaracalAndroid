package app.karacal.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import app.karacal.R;
import app.karacal.helpers.DummyHelper;
import apps.in.android_logger.LogActivity;

public class DonateActivity extends LogActivity {

    private static final int AMOUNT_SMALL = 3;
    private static final int AMOUNT_MEDIUM = 5;
    private static final int AMOUNT_BIG = 7;
    private Button buttonDonate;
    private TextView textViewDonateAmountSmall;
    private TextView textViewDonateAmountMedium;
    private TextView textViewDonateAmountBig;
    TextInputLayout inputLayoutAmount;

    private Double donation = Double.NEGATIVE_INFINITY;

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
                donation = d;
                validate();
            } catch (Exception e) {
                donation = Double.NEGATIVE_INFINITY;
                s.clear();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        setupBackButton();
        setupGuideInfo();
        setupAmountButtons();
        setupAmountInput();
        setupDonateButton();
        validate();
    }

    private void setupBackButton(){
        ImageView button = findViewById(R.id.buttonBack);
        button.setOnClickListener(v -> onBackPressed());
    }

    private void setupGuideInfo(){
        int avatarResId = R.mipmap.avatar_example;
        String name = "Alexander McQueen";
        String location = "Paris, France";
        ImageView avatar = findViewById(R.id.imageViewAvatar);
        avatar.setImageResource(avatarResId);
        TextView textViewName = findViewById(R.id.textViewGuideName);
        textViewName.setText(name);
        TextView textViewLocation = findViewById(R.id.textViewGuideLocation);
        textViewLocation.setText(location);
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
        inputLayoutAmount.getEditText().addTextChangedListener(watcher);
    }

    private void setupDonateButton(){
        buttonDonate = findViewById(R.id.buttonDonate);
        buttonDonate.setOnClickListener(v -> DummyHelper.dummyAction(this));
    }

    private void unCheckAmountButtons(){
        textViewDonateAmountSmall.setSelected(false);
        textViewDonateAmountMedium.setSelected(false);
        textViewDonateAmountBig.setSelected(false);
    }

    private void setDonationAmount(View v, int amount){
        unCheckAmountButtons();
        v.setSelected(true);
        donation = (double) amount;
        inputLayoutAmount.getEditText().removeTextChangedListener(watcher);
        inputLayoutAmount.getEditText().setText("");
        inputLayoutAmount.getEditText().addTextChangedListener(watcher);
        validate();
    }

    private void validate(){
        buttonDonate.setEnabled(donation > 0);
    }
}
