package app.karacal.service;

import android.util.Log;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;

import java.math.BigInteger;
import java.util.Calendar;

import app.karacal.data.NotificationsCache;
import app.karacal.models.NotificationInfo;

public class CustomNotificationExtenderService extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        OverrideSettings overrideSettings = new OverrideSettings();
        overrideSettings.extender = builder -> {
            //Force remove push from Notification Center after 30 minutes
            builder.setTimeoutAfter(1800000)
                    // Sets the background notification color to Green on Android 5.0+ devices.
                    .setColor(new BigInteger("FF181B2B", 16).intValue());
            return builder;
        };

        OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);

        saveToCache(displayedResult, receivedResult);
        Log.d("OneSignalExample", "Notification received with id: " + displayedResult.androidNotificationId);

        return true;
    }

    private void saveToCache(OSNotificationDisplayedResult displayedResult, OSNotificationReceivedResult receivedResult){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        NotificationsCache notificationsCache = NotificationsCache.getInstance(this);
        NotificationInfo notificationInfo = new NotificationInfo(
                displayedResult.androidNotificationId,
                receivedResult.payload.title,
                receivedResult.payload.body,
                calendar.getTime()
        );

        Log.d("OneSignalExample", "Notification received: " + notificationInfo);//message

        notificationsCache.addNotification(this, notificationInfo);
    }
}
