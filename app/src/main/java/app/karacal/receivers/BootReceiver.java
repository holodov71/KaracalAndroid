package app.karacal.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import app.karacal.App;
import app.karacal.helpers.NotificationHelper;

public class BootReceiver extends BroadcastReceiver {

    private static final long INTERVAL = 4 * 60 * 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            NotificationHelper.scheduleNotification(App.getInstance(), System.currentTimeMillis() + INTERVAL);
        }
    }

}
