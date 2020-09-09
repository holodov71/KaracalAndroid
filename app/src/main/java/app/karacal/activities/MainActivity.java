package app.karacal.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
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
import app.karacal.helpers.ApiHelper;
import app.karacal.receivers.MyNotificationPublisher;
import app.karacal.helpers.NotificationHelper;
import app.karacal.viewmodels.MainActivityViewModel;

public class MainActivity extends PermissionActivity {

    private MainActivityViewModel viewModel;
    private final static String default_notification_channel_id = "default" ;

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

//        scheduleNotifications();
//        NotificationHelper.scheduleNotification(App.getInstance(), System.currentTimeMillis() + 10000);


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
                () -> Toast.makeText(this, R.string.permission_needed, Toast.LENGTH_SHORT).show());
    }

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    private void scheduleNotifications() {
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyNotificationPublisher.class);
//        intent.putExtra(MyNotificationPublisher. NOTIFICATION_ID , 1 ) ;
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

//        // Set the alarm to start at approximately 2:00 p.m.
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 14);
//
//        // With setInexactRepeating(), you have to use one of the AlarmManager interval
//        // constants--in this case, AlarmManager.INTERVAL_DAY.
//        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, alarmIntent);

        //        // Set the alarm to start at 8:30 a.m.
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 40);
//
//        // setRepeating() lets you specify a precise custom interval--in this case,
//        // 20 minutes.
        alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, alarmIntent);

//        // Set the alarm to start at 8:30 a.m.
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 40);
//
//        // setRepeating() lets you specify a precise custom interval--in this case,
//        // 20 minutes.
//        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                1000 * 60 * 2, alarmIntent);
    }



}
