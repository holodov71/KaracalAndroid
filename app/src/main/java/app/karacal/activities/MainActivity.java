package app.karacal.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.data.LocationCache;
import app.karacal.helpers.ApiHelper;
import app.karacal.viewmodels.MainActivityViewModel;

public class MainActivity extends PermissionActivity {

    private MainActivityViewModel viewModel;

    @Inject
    ApiHelper apiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, new MainActivityViewModel.MainActivityViewModelFactory()).get(MainActivityViewModel.class);
        setContentView(R.layout.activity_main);

        viewModel.processUser();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setItemIconTintList(null);
        NavController navController = Navigation.findNavController(this, R.id.fragmentHostView);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        setupLocation();

        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        String userId = status.getSubscriptionStatus().getUserId();
        Log.v(App.TAG, "OneSignal userId = "+userId);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        for (Fragment fragment : getSupportFragmentManager().getFragments()){
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupLocation() {
        permissionHelper.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION,
                () -> viewModel.startObtainLocation(),
                () -> {
                    LocationCache.getInstance(App.getInstance()).setHasPermission(false);
                    Toast.makeText(this, R.string.permission_needed, Toast.LENGTH_SHORT).show();
                });
    }


}
