package app.karacal.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.visualizer.amplitude.AudioRecordView;

import java.util.Locale;

import app.karacal.R;
import app.karacal.dialogs.AudioTitleDialog;
import app.karacal.helpers.DummyHelper;
import app.karacal.viewmodels.AudioRecorderActivityViewModel;
import apps.in.android_logger.LogActivity;

public class AudioRecorderActivity extends LogActivity {

    private AudioRecorderActivityViewModel viewModel;

    private ImageView buttonBack;
    private ImageView buttonCancel;
    private ImageView buttonSave;
    private ImageView buttonApply;
    private ImageView buttonRecord;
    private AudioRecordView recordView;
    private TextView textViewDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AudioRecorderActivityViewModel.class);
        setContentView(R.layout.activity_audio_recorder);
        setupBackButton();
        setupSaveButton();
        setupCancelButton();
        setupRecordButton();
        setupApplyButton();
        setupRecorderView();
        updateDuration();
        viewModel.bindLifecycle(this);
    }

    private void setupBackButton(){
        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupSaveButton(){
        buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(v -> save());
        viewModel.getHasData().observe(this, hasData -> buttonSave.setVisibility(hasData ? View.VISIBLE : View.INVISIBLE));
    }

    private void setupCancelButton(){
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(v -> {
            viewModel.reset();
            recordView.recreate();
            updateDuration();
        });
        viewModel.getHasData().observe(this, hasData -> buttonCancel.setEnabled(hasData));
    }

    private void setupApplyButton(){
        buttonApply = findViewById(R.id.buttonApply);
        buttonApply.setOnClickListener(v -> save());
        viewModel.getHasData().observe(this, hasData -> buttonApply.setEnabled(hasData));
    }

    private void setupRecordButton(){
        buttonRecord = findViewById(R.id.buttonRecord);
        buttonRecord.setOnClickListener(v -> viewModel.startPause());
        viewModel.getIsRecording().observe(this, isRecording -> buttonRecord.setSelected(isRecording));
    }

    private void setupRecorderView(){
        recordView = findViewById(R.id.recordView);
        textViewDuration = findViewById(R.id.textViewDuration);
        viewModel.setAmplitudeListener(amplitude -> {
            recordView.update(amplitude);
            textViewDuration.post(() -> updateDuration());
        });
    }

    private String formatDuration(int duration){
        int minutes = duration / 1000 / 60;
        int seconds = duration / 1000 % 60;
        int millis = duration % 1000 / 100;
        return String.format(Locale.US, "%02d:%02d,%d", minutes, seconds, millis);
    }

    private void updateDuration(){
        textViewDuration.setText(formatDuration(recordView.getDuration()));
    }

    private void save(){
        AudioTitleDialog dialog = AudioTitleDialog.getInstance(null);
        dialog.setListener(title -> {
            viewModel.save();
            finish();
        });
        dialog.show(getSupportFragmentManager(), AudioTitleDialog.DIALOG_TAG);
    }
}
