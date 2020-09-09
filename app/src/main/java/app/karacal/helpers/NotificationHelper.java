package app.karacal.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import app.karacal.receivers.MyNotificationPublisher;

public class NotificationHelper {
    public static void scheduleNotification(Context context, long time) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MyNotificationPublisher.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        if (alarmMgr != null)
            alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, alarmIntent);
    }
}
