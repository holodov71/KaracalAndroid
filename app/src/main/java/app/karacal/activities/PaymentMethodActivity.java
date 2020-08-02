package app.karacal.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import app.karacal.R;
import app.karacal.viewmodels.PaymentMethodViewModel;
import apps.in.android_logger.LogActivity;

public class PaymentMethodActivity extends LogActivity {

    private PaymentMethodViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
        viewModel = new ViewModelProvider(this).get(PaymentMethodViewModel.class);
        setupTitle();
        setupBackButton();
    }

    private void setupTitle(){
        TextView textView = findViewById(R.id.textViewAppBarTitle);
        viewModel.getActivityTitle().observe(this, title -> textView.setText(title != null ? title : ""));
    }

    private void setupBackButton(){
        ImageView button = findViewById(R.id.buttonBack);
        button.setOnClickListener(v -> onBackPressed());
    }
}
