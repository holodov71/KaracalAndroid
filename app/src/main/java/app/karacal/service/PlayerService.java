package app.karacal.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;

import org.jetbrains.annotations.NotNull;

import app.karacal.R;
import app.karacal.activities.AudioActivity;
import app.karacal.activities.MainActivity;
import app.karacal.models.Album;
import app.karacal.models.Player;
import app.karacal.models.Track;

public class PlayerService extends LifecycleService {

    public static final String ACTION_PLAY = "app.karacal.audioplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "app.karacal.audioplayer.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "app.karacal.audioplayer.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "app.karacal.audioplayer.ACTION_NEXT";
    public static final String ACTION_STOP = "app.karacal.audioplayer.ACTION_STOP";


    public static final String TAG = "PlayerService";
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static final String ARG_TOUR = "tour";

    private static final int NOTIFICATION_ID = 1;

    private Player player;
    private Player.PlayerState state;
    private Album currentAlbum;
    private int currentTrack = -1;

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.v(TAG, "onStartCommand");

        handleIncomingActions(intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // Binder given to clients
    private final IBinder iBinder = new LocalBinder();

    @Override
    public IBinder onBind(@NotNull Intent intent) {
        super.onBind(intent);
        return iBinder;
    }

    public Player getPlayer() {
        return player;
    }

    public class LocalBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }


    public void initPlayer(Player pl){
        if (player != null){
            releasePlayer();

            if(player.getTourId() != pl.getTourId()){
                player.pause();
                player.dispose();
            }
        }

        this.player = pl;

        player.getPlayerStateLiveData().observe(this, playerState -> {
            state = playerState;
            showNotification();
        });
        player.getCurrentTrackLiveData().observe(this, trackPosition -> {
            if (trackPosition != null) {
                currentTrack = trackPosition;
                showNotification();
            }
        });

        player.getAlbumLiveData().observe(this, album -> {
            currentAlbum = album;
            showNotification();
        });


    }

    private void showNotification(){

        if(state != null && currentAlbum != null && currentTrack != -1){

            Track track = currentAlbum.getTracks().get(currentTrack);
            if (track != null) {
                buildNotification(state, currentAlbum, track);
            }
        }

    }

    /**
     * Return the service to the background
     */
    public void background() {
        releasePlayer();
        stopForeground(true);
    }

    private void buildNotification(Player.PlayerState playbackStatus, Album album, Track track){

        RemoteViews contentView = new RemoteViews(getPackageName() , R.layout.player_notification_layout);

        //Build a new notification according to the current state of the MediaPlayer
        if (playbackStatus == Player.PlayerState.PLAY) {
            contentView.setImageViewResource(R.id.buttonPause, R.drawable.ic_pause_orange);
            contentView.setOnClickPendingIntent(R.id.buttonPause, playbackAction(1));
        } else {
            contentView.setImageViewResource(R.id.buttonPause, R.drawable.ic_play);
            contentView.setOnClickPendingIntent(R.id.buttonPause, playbackAction(0));
        }

        contentView.setTextViewText(R.id.textViewAlbumTitle, album.getTitle());
        contentView.setTextViewText(R.id.textViewTrackTitle, track.getTitle());

        contentView.setOnClickPendingIntent(R.id.buttonNext, playbackAction(2));
        contentView.setOnClickPendingIntent(R.id.closePlayer, playbackAction(3));

        createNotificationChannel();

        Intent notificationIntent;

        if (player != null && player.getTourId() != 0){
            notificationIntent = new Intent(this, AudioActivity.class);
            AudioActivity.Args args = new AudioActivity.Args(player.getTourId());
            notificationIntent.putExtras(args.toBundle());
            notificationIntent.setAction("played_tour");
        } else {
            notificationIntent = new Intent(this, MainActivity.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setShowWhen(false)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                .setContent(contentView)
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setColor(getColor(R.color.colorTextOrange))
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .setWhen(0)
                .setAutoCancel(true)
                .build();

        NotificationTarget notificationTarget = new NotificationTarget(
                this,
                R.id.imageViewAlbumTitle,
                contentView,
                notification,
                NOTIFICATION_ID);

        Glide.with(this.getApplicationContext())
                .asBitmap()
                .load(album.getImageUrl())
                .into(notificationTarget);

        startForeground(NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(serviceChannel);
        }
    }

    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, PlayerService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 2:
                // Next track
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 3:
                // Stop service
                playbackAction.setAction(ACTION_STOP);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            player.playTrack();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            player.pause();
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            player.nextTrack();
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            closeService();
        }
    }

    private void closeService(){
        releasePlayer();

        player.pause();
        player.dispose();

        background();
        stopSelf();
    }

    public void releasePlayer(){
        if (player != null) {
            player.getPlayerStateLiveData().removeObservers(this);
            player.getCurrentTrackLiveData().removeObservers(this);
            player.getAlbumLiveData().removeObservers(this);
        }
    }

    public boolean isPlaying(){
        try {
            return player != null && player.isPlaying();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
}
