package app.karacal.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.adapters.TrackEditListAdapter;
import app.karacal.data.AlbumRepository;
import app.karacal.dialogs.AudioTitleDialog;
import app.karacal.helpers.DummyHelper;
import app.karacal.models.Track;
import app.karacal.navigation.ActivityArgs;
import app.karacal.navigation.NavigationHelper;
import app.karacal.popups.BasePopup;
import app.karacal.popups.EditAudioPopup;

public class EditAudioActivity extends PermissionActivity {

    private static final int MP3_SELECT_REQUEST_CODE = 702;

    public static class Args extends ActivityArgs implements Serializable {

        private final Integer tourId;

        public Args(Integer tourId) {
            this.tourId = tourId;
        }

        public Integer getTourId() {
            return tourId;
        }

    }

    @Inject
    AlbumRepository albumRepository;

    private ConstraintLayout layoutRoot;
    private ArrayList<Track> tracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        EditAudioActivity.Args args = EditAudioActivity.Args.fromBundle(EditAudioActivity.Args.class, getIntent().getExtras());
        Integer tourId = args.getTourId();
        if (tourId != null) {
            tracks = albumRepository.getAlbumByTourId(tourId).getTracks();
        } else {
            tracks = new ArrayList<>();
        }
        setContentView(R.layout.activity_edit_audio);
        layoutRoot = findViewById(R.id.layoutRoot);
        setupBackButton();
        setupSendButton();
        setupBottomButtons();
        setupRecyclerView();
    }

    private void setupBackButton() {
        ImageView button = findViewById(R.id.buttonBack);
        button.setOnClickListener(v -> onBackPressed());
    }

    private void setupSendButton() {
        ImageView button = findViewById(R.id.buttonSend);
        button.setOnClickListener(v -> NavigationHelper.startCongratulationsActivity(this));
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        TrackEditListAdapter adapter = new TrackEditListAdapter(this);
        adapter.setTracks(tracks);
        adapter.setClickListener(position -> {
            EditAudioPopup popup = new EditAudioPopup(layoutRoot, new EditAudioPopup.EditAudioPopupCallbacks() {
                @Override
                public void onButtonRenameClick(BasePopup popup) {
                    popup.close();
                    AudioTitleDialog dialog = AudioTitleDialog.getInstance(tracks.get(position).getTitle());
                    dialog.setListener(title -> {
                        tracks.get(position).setTitle(title);
                        adapter.setTracks(tracks);
                        adapter.notifyDataSetChanged();
                    });
                    dialog.show(getSupportFragmentManager(), AudioTitleDialog.DIALOG_TAG);
                }

                @Override
                public void onButtonEditClick(BasePopup popup) {
                    DummyHelper.dummyAction(EditAudioActivity.this);
                }

                @Override
                public void onButtonDownloadClick(BasePopup popup) {
                    DummyHelper.dummyAction(EditAudioActivity.this);
                }

                @Override
                public void onButtonDeleteClick(BasePopup popup) {
                    tracks.remove(position);
                    adapter.setTracks(tracks);
                    adapter.notifyDataSetChanged();
                    popup.close();
                }
            });
            popup.show();
        });
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(adapter.getCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setupBottomButtons() {
        ImageView buttonSound = findViewById(R.id.buttonSound);
        buttonSound.setOnClickListener(v -> DummyHelper.dummyAction(this));
        ImageView buttonRecord = findViewById(R.id.buttonRecord);
        buttonRecord.setOnClickListener(v -> permissionHelper.checkPermission(this,
                new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                () -> NavigationHelper.startAudioRecorderActivity(this),
                () -> Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show()));
        ImageView buttonUpload = findViewById(R.id.buttonUpload);
        buttonUpload.setOnClickListener(v -> permissionHelper.checkPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                () -> {
                    Intent intent;
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("audio/mp3");
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.select_audio_file)), MP3_SELECT_REQUEST_CODE);
                },
                () -> Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show())
        );
    }

    @Override
    public void onBackPressed() {
        if (!BasePopup.closeAllPopups(layoutRoot)) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == MP3_SELECT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if ((data != null) && (data.getData() != null)) {
                Uri audioFileUri = data.getData();
                //
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
