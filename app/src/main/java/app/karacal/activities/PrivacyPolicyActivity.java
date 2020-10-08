package app.karacal.activities;

import android.os.Bundle;
import android.widget.ImageView;

import app.karacal.R;
import apps.in.android_logger.LogActivity;

public class PrivacyPolicyActivity extends LogActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        setupBackButton();
    }

    private void setupBackButton(){
        ImageView buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());
    }
}
