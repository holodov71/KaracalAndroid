package app.karacal.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.helpers.PreferenceHelper;
import apps.in.android_logger.LogActivity;

public class ApplyPrivacyPolicyActivity extends LogActivity {

    @Inject
    PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        if (preferenceHelper.isPrivacyPolicyApplied()){
            proceed();
        } else {
            setContentView(R.layout.activity_apply_privacy_policy);
            setupBackButton();
            setupApplyButtons();
        }
    }

    private void setupBackButton(){
        ImageView buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupApplyButtons(){
        View.OnClickListener clickListener = v -> apply();
        ImageView buttonApply = findViewById(R.id.buttonApply);
        Button buttonApplyBig = findViewById(R.id.buttonApplyBig);
        buttonApply.setOnClickListener(clickListener);
        buttonApplyBig.setOnClickListener(clickListener);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void apply(){
        preferenceHelper.setPrivacyPolicyApplied(true);
        proceed();
    }

    private void proceed(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
