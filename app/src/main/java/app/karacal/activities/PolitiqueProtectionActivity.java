package app.karacal.activities;

import android.os.Bundle;
import android.widget.ImageView;

import app.karacal.R;
import apps.in.android_logger.LogActivity;

public class PolitiqueProtectionActivity extends LogActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_politique_protection);
        setupBackButton();
    }

    private void setupBackButton(){
        ImageView buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());
    }
}
