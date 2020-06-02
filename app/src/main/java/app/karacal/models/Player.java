package app.karacal.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.TimeUnit;

import app.karacal.App;
import apps.in.android_logger.Logger;
import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Player implements MediaPlayer.OnPreparedListener{

    private static final int POSITION_INFO_UPDATE_INTERVAL = 1;     //seconds
    private static final int SEEK_AMOUNT = 15_000;                  //milliseconds

    public static class PositionInfo {
        private final int currentPosition;
        private final int duration;

        public PositionInfo(int currentPosition, int duration) {
            this.currentPosition = currentPosition / 1000;
            this.duration = duration / 1000;
        }

        public int getCurrentPosition() {
            return currentPosition;
        }

        public int getDuration() {
            return duration;
        }
    }

    public static class DurationInfo {
        private final int currentPosition;
        private final int trackDuration;

        public DurationInfo(int currentPosition, int trackDuration) {
            this.currentPosition = currentPosition / 1000;
            this.trackDuration = trackDuration;
        }

        public int getCurrentPosition() {
            return currentPosition;
        }

        public int getTrackDuration() {
            return trackDuration;
        }
    }

    public enum PlayerState {
        IDLE,
        PLAY,
        PAUSE,
    }

    private MediaPlayer mediaPlayer;
    private int currentPosition;
    private Disposable positionUpdater;
    private LiveData<Album> album;

    private MutableLiveData<PlayerState> playerStateMutableLiveData = new MutableLiveData<>(PlayerState.IDLE);
    private MutableLiveData<Integer> currentTrackMutableLiveData = new MutableLiveData<>(null);
    private MutableLiveData<PositionInfo> positionInfoMutableLiveData = new MutableLiveData<>(null);

    public Player(LiveData<Album> album) {
        this.album = album;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(mp -> {
            Integer currentTrack = getCurrentTrack();
            if (currentTrack != null && currentTrack < album.getValue().getTracks().size() - 1) {
                playTrack(currentTrack + 1);
            } else {
                setPlayerState(PlayerState.IDLE);
                currentTrackMutableLiveData.postValue(null);
            }
        });
    }

    public void bindLifecycle(LifecycleOwner lifecycleOwner){
        lifecycleOwner.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_PAUSE) {
                pause();
            }
        });
    }

    public void playTrack() {
        if (getPlayerState() == PlayerState.PAUSE) {
            mediaPlayer.start();
            playerStateMutableLiveData.postValue(PlayerState.PLAY);
        } else if (getPlayerState() == PlayerState.IDLE && album.getValue() != null && album.getValue().getTracks().size() > 0) {
            playTrack(0);
        }
    }

    @SuppressLint("CheckResult")
    public void playTrack(int position) {
        Log.v("Player", "Play position = "+position);
        currentPosition = position;
//        try{
//
//            mediaPlayer.reset();
//            if (album.getValue() != null) {
//                Track track = album.getValue().getTracks().get(position);
//                Log.v("Player", "Play track = "+track.getTitle());
//                Context context = App.getInstance();
//                Uri mediaPath;
//                if (track.getResId() == -1) { // audio from network
//                    mediaPath = Uri.parse(track.getFilename());
//                } else {
//                    mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + track.getResId());
//                }
//                mediaPlayer.setDataSource(context, mediaPath);
//
//                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                mediaPlayer.prepare();
//                mediaPlayer.setOnPreparedListener(this);
//            } else {
//                throw new Exception("Tracks in album is not available");
//            }
//        }catch (Exception ex){
//            Logger.log(Player.this, "Error playing track", ex);
//            setPlayerState(PlayerState.IDLE);
//        }
//        currentTrackMutableLiveData.postValue(position);
        Completable.fromAction(() -> {
            mediaPlayer.reset();
            if (album.getValue() != null) {
                Track track = album.getValue().getTracks().get(position);
                Log.v("Player", "Play track = "+track.getTitle());
                Context context = App.getInstance();
                Uri mediaPath;
                if (track.getResId() == -1) { // audio from network
                    mediaPath = Uri.parse(track.getFilename());
                } else {
                    mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + track.getResId());
                }
                mediaPlayer.setDataSource(context, mediaPath);

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setOnPreparedListener(this);
                if (track.getResId() == -1) {
                    mediaPlayer.prepare();
                } else {
                    mediaPlayer.prepareAsync();
                }
            } else {
                throw new Exception("Tracks in album is not available");
            }
        }).subscribeOn(Schedulers.io()).subscribe(() -> {
            Logger.log(Player.this, "Play Track");
        }, (throwable -> {
            Logger.log(Player.this, "Error playing track", throwable);
            setPlayerState(PlayerState.IDLE);
        }));
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            setPlayerState(PlayerState.PAUSE);
        }
    }

    public void nextTrack() {
        Integer currentPosition = getCurrentTrack();
        if (currentPosition != null && album.getValue() != null) {
            playTrack((currentPosition + 1) % album.getValue().getTracks().size());
        }
    }

    public void seekBack() {
        if (getPlayerState() == PlayerState.PLAY) {
            int newPosition = Math.max(0, mediaPlayer.getCurrentPosition() - SEEK_AMOUNT);
            mediaPlayer.seekTo(newPosition);
        }
    }

    public void seekForward() {
        if (getPlayerState() == PlayerState.PLAY) {
            int newPosition = mediaPlayer.getCurrentPosition() + SEEK_AMOUNT;
            if (newPosition < mediaPlayer.getDuration()) {
                mediaPlayer.seekTo(newPosition);
            } else {
                nextTrack();
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
        setPlayerState(PlayerState.PLAY);
        currentTrackMutableLiveData.postValue(currentPosition);
    }

    public LiveData<PlayerState> getPlayerStateLiveData() {
        return playerStateMutableLiveData;
    }

    public PlayerState getPlayerState() {
        return playerStateMutableLiveData.getValue();
    }

    public LiveData<Integer> getCurrentTrackLiveData() {
        return currentTrackMutableLiveData;
    }

    public LiveData<PositionInfo> getPositionInfoLiveData() {
        return positionInfoMutableLiveData;
    }

    public Integer getCurrentTrack() {
        return currentTrackMutableLiveData.getValue();
    }

    private void setPlayerState(PlayerState newState) {
        playerStateMutableLiveData.postValue(newState);
        if (newState == PlayerState.PLAY) {
            positionUpdater = Completable.timer(POSITION_INFO_UPDATE_INTERVAL, TimeUnit.SECONDS)
                    .doOnComplete(() -> positionInfoMutableLiveData.postValue(new PositionInfo(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration())))
                    .repeat()
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            () -> Logger.log(Player.this, "Starting position update"),
                            (throwable -> Logger.log(Player.this, "Position update error", throwable)));
        } else {
            if (positionUpdater != null) {
                positionUpdater.dispose();
                positionUpdater = null;
            }
        }
    }

    public void dispose() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
