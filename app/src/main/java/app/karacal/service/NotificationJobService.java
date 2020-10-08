package app.karacal.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.activities.AudioActivity;
import app.karacal.data.LocationCache;
import app.karacal.data.NotificationsCache;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.LocationHelper;
import app.karacal.helpers.NotificationHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.models.NotificationInfo;
import app.karacal.models.NotificationScheduleModel;
import app.karacal.models.Tour;
import app.karacal.network.models.request.NearToursRequest;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class NotificationJobService extends JobService {

    public static String TAG = "NotificationJobService";
    public static String NOTIFICATION_CHANNEL_ID = "notification-chanel-id-karacal";
    public static int NOTIFICATION_ID = 3851;
    private static final long INTERVAL = 12 * 60 * 60 * 1000;// 12 hours
    private static final int GEO_CODING_TIMEOUT = 5;

    @Inject
    ApiHelper apiHelper;

    private CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    public boolean onStartJob(@NotNull JobParameters job) {
        App.getAppComponent().inject(this);
        obtainLocation();

        return true;
    }

    @Override
    public boolean onStopJob(@NotNull JobParameters job) {
        return true;
    }

    private void showNotification(Context context, Tour tour){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ) {
            int importance = NotificationManager.IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }
        assert notificationManager != null;
        notificationManager.notify(NOTIFICATION_ID , getNotification(tour));
        PreferenceHelper.setLastNotificationWasShownTime(this, System.currentTimeMillis());
        finishWork();
    }

    private void obtainLocation() {
        mDisposable.add(LocationHelper.getLastKnownLocation().timeout(GEO_CODING_TIMEOUT, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::loadNearTours, throwable -> {
                    Location location = LocationCache.getInstance(App.getInstance()).getLocation();
                    if (location != null) {
                        loadNearTours(location);
                    } else {
                        finishWork();
                    }
                }));
    }

    private void loadNearTours(Location location){
        mDisposable.add(apiHelper.loadNearTours(PreferenceHelper.loadToken(),
                new NearToursRequest(location.getLatitude(), location.getLongitude(), 0.5))
                .flatMapIterable(list -> list)
                .map(Tour::new)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tours -> {
                    if (tours.size() > 0){
                        Random random = new Random();
                        int index = random.nextInt(tours.size());
                        Tour tour = tours.get(index);
                        showNotification(this, tour);
                    } else {
                        finishWork();
                    }

                }, throwable -> finishWork()));
    }

    private void finishWork(){
        if (!mDisposable.isDisposed()) mDisposable.dispose();
        NotificationHelper.scheduleNotification(App.getInstance(), System.currentTimeMillis() + INTERVAL);
    }

    private Notification getNotification (Tour model) {
        RemoteViews contentView = new RemoteViews(App.getInstance().getPackageName() , R.layout.local_notification_layout);

        contentView.setTextViewText(R.id.textViewTourTitle, model.getTitle());
        contentView.setTextViewText(R.id.textViewTourDescription, model.getDescription());

        Intent notificationIntent = new Intent(App.getInstance(), AudioActivity.class);
        AudioActivity.Args args = new AudioActivity.Args(model.getId());
        notificationIntent.putExtras(args.toBundle());
        notificationIntent.setAction("notification_karacal");

        PendingIntent pendingIntent = PendingIntent.getActivity(App.getInstance(),
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(App.getInstance(), NOTIFICATION_CHANNEL_ID)
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
                NOTIFICATION_ID);

        Glide.with(App.getInstance())
                .asBitmap()
                .load(model.getImageUrl())
                .into(notificationTarget);

        saveToCache(new NotificationScheduleModel(getCurrentDay(), model));

        return notification;

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
                generateId(model),
                model.getTitle(),
                model.getMessage(),
                calendar.getTime(),
                model.getTourId()
        );

        notificationsCache.addNotification(App.getInstance(), notificationInfo);
    }

    private int generateId(NotificationScheduleModel model){
        long n = System.currentTimeMillis() / (model.getDay() * 100);
        Random r = new Random();

        return (int)(n + r.nextInt(10000));
    }
}
