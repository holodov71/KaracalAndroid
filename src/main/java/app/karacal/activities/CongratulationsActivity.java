package app.karacal.activities;

import android.os.Bundle;

import app.karacal.R;
import app.karacal.navigation.NavigationHelper;
import apps.in.android_logger.LogActivity;

public class CongratulationsActivity extends LogActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulations);
    }

    @Override
    public void onBackPressed() {
        finish();
        NavigationHelper.startDashboardActivity(this);
    }
}
