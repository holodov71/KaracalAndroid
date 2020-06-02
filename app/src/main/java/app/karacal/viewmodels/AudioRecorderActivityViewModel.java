package app.karacal.viewmodels;

import android.app.Application;
import android.media.AudioFormat;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.squti.androidwaverecorder.AmplitudeListener;
import com.github.squti.androidwaverecorder.WaveConfig;
import com.github.squti.androidwaverecorder.WaveRecorder;

import java.io.File;

public class AudioRecorderActivityViewModel extends AndroidViewModel {

    private static final int SAMPLE_RATE = 44100;

    private MutableLiveData<Boolean> isRecording = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> hasData = new MutableLiveData<>(false);
    private MutableLiveData<Uri> savedFileUri = new MutableLiveData<>();

    private WaveRecorder waveRecorder;
    private String path;
    private String directory;

    public AudioRecorderActivityViewModel(@NonNull Application application) {
        super(application);
        directory = application.getFilesDir().getPath();
        path = directory + "/temp.mp3";
        waveRecorder = new WaveRecorder(path);
        WaveConfig waveConfig = new WaveConfig(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        waveRecorder.setWaveConfig(waveConfig);
    }

    public LiveData<Boolean> getIsRecording() {
        return isRecording;
    }

    public LiveData<Boolean> getHasData() {
        return hasData;
    }

    public LiveData<Uri> getSavedFileUri(){
        return savedFileUri;
    }

    public void setAmplitudeListener(AmplitudeListener amplitudeListener) {
        waveRecorder.setAmplitudeListener(amplitudeListener);
    }

    public void startPause(){
        Boolean recordingValue = isRecording.getValue();
        if (recordingValue != null ? recordingValue : false){
            pause();
        } else {
            start();
        }
    }

    private void start() {
        Boolean paused = hasData.getValue();
        hasData.postValue(false);
        isRecording.postValue(true);
        if (paused != null ? paused : false) {
            waveRecorder.resumeRecording();
        } else {
            waveRecorder.startRecording();
        }
    }

    private void pause() {
        hasData.postValue(true);
        isRecording.postValue(false);
        waveRecorder.pauseRecording();
    }

    public void save(String title){
        waveRecorder.stopRecording();

        String fileName = title + ".mp3";

        File from = new File(path);
        File to = new File(directory , fileName);
        from.renameTo(to);
        savedFileUri.setValue(Uri.fromFile(to));
    }

    public void reset(){
        waveRecorder.stopRecording();
        hasData.postValue(false);
        isRecording.postValue(false);
    }

    public void bindLifecycle(LifecycleOwner lifecycleOwner){
        lifecycleOwner.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_PAUSE){
                Boolean isRecording = this.isRecording.getValue();
                if (isRecording != null ? isRecording : false){
                    pause();
                }
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Boolean isRecording = this.isRecording.getValue();
        Boolean hasData = this.hasData.getValue();
        if ((isRecording != null ? isRecording : false) || (hasData != null ? hasData : false)){
            waveRecorder.stopRecording();
        }
    }
}
