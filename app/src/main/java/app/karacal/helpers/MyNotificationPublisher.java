package app.karacal.helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;

import java.util.Calendar;
import java.util.List;

import app.karacal.App;
import app.karacal.R;
import app.karacal.activities.AudioActivity;
import app.karacal.activities.MainActivity;
import app.karacal.data.NotificationsCache;
import app.karacal.data.NotificationsSchedule;
import app.karacal.models.NotificationInfo;
import app.karacal.models.NotificationScheduleModel;
import app.karacal.models.Player;

public class MyNotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_CHANNEL_ID = "notification-chanel-id-karacal";
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    private final static String default_notification_channel_id = "default" ;

    public void onReceive (Context context , Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context. NOTIFICATION_SERVICE );
//        Notification notification = intent.getParcelableExtra( NOTIFICATION );
        NotificationScheduleModel model = getNotificationModel();
        if (model == null ){
            Log.v("MyNotificationPublisher", "NotificationScheduleModel NULLLLLLLLLLLLLLL");
            return;
        }

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }
        int id = intent.getIntExtra( NOTIFICATION_ID , 0);
        assert notificationManager != null;
        notificationManager.notify(id , getNotification(model));
    }

    private Notification getNotification (NotificationScheduleModel model) {
        RemoteViews contentView = new RemoteViews(App.getInstance().getPackageName() , R.layout.local_notification_layout);

        contentView.setTextViewText(R.id.textViewTourTitle, model.getTitle());
        contentView.setTextViewText(R.id.textViewTourDescription, model.getMessage());

        Intent notificationIntent;

//        if (player != null && player.getTourId() != 0){
            notificationIntent = new Intent(App.getInstance(), AudioActivity.class);
            AudioActivity.Args args = new AudioActivity.Args(model.getTourId());
            notificationIntent.putExtras(args.toBundle());
            notificationIntent.setAction("played_tour");
//        } else {
//            notificationIntent = new Intent(App.getInstance(), MainActivity.class);
//        }

        PendingIntent pendingIntent = PendingIntent.getActivity(App.getInstance(),
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(App.getInstance(), default_notification_channel_id)
                .setShowWhen(false)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                .setContent(contentView)
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .setWhen(0)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(true)
                .build();

        NotificationTarget notificationTarget = new NotificationTarget(
                App.getInstance(),
                R.id.imageViewTour,
                contentView,
                notification,
                35);

        Glide.with(App.getInstance())
                .asBitmap()
                .load(model.getLogo())
                .into(notificationTarget);

        saveToCache(model);

        return notification;

    }

    private NotificationScheduleModel getNotificationModel(){
        List<NotificationScheduleModel> notifList = NotificationsSchedule.getInstance(App.getInstance()).getNotificationsList();
        int currentDay = getCurrentDay();
        if (!notifList.isEmpty() && notifList.size() > currentDay) {
            return notifList.get(currentDay);
        } else return null;
    }

    private int getCurrentDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void saveToCache(NotificationScheduleModel model){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        NotificationsCache notificationsCache = NotificationsCache.getInstance(App.getInstance());
        NotificationInfo notificationInfo = new NotificationInfo(
                model.getDay()*model.getDay(),
                model.getTitle(),
                model.getMessage(),
                calendar.getTime()
        );

        Log.d("OneSignalExample", "Notification received: " + notificationInfo);//message

        notificationsCache.addNotification(App.getInstance(), notificationInfo);
    }
}
