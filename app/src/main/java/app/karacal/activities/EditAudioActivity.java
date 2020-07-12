package app.karacal.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.adapters.TrackEditListAdapter;
import app.karacal.data.repository.AlbumRepository;
import app.karacal.data.repository.TourRepository;
import app.karacal.dialogs.AudioTitleDialog;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.DummyHelper;
import app.karacal.helpers.FileHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.helpers.ProfileHolder;
import app.karacal.helpers.ToastHelper;
import app.karacal.models.Player;
import app.karacal.models.Tour;
import app.karacal.models.Track;
import app.karacal.navigation.ActivityArgs;
import app.karacal.navigation.NavigationHelper;
import app.karacal.network.models.request.RenameTrackRequest;
import app.karacal.popups.BasePopup;
import app.karacal.popups.EditAudioPopup;
import app.karacal.network.models.response.UploadTrackResponse;
import app.karacal.viewmodels.EditAudioViewModel;
import io.reactivex.disposables.CompositeDisposable;

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

    private EditAudioViewModel viewModel;

    @Inject
    AlbumRepository albumRepository;

    @Inject
    TourRepository tourRepository;

    @Inject
    ProfileHolder profileHolder;

    @Inject
    ApiHelper apiHelper;

    Integer tourId;

    private ConstraintLayout layoutRoot;
    private View progressLoading;
    private ArrayList<Track> tracks = new ArrayList<>();
    private TrackEditListAdapter adapter;

    CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        EditAudioActivity.Args args = EditAudioActivity.Args.fromBundle(EditAudioActivity.Args.class, getIntent().getExtras());

        tourId = args.getTourId();

        viewModel = new ViewModelProvider(this).get(EditAudioViewModel.class);


        setContentView(R.layout.activity_edit_audio);
        layoutRoot = findViewById(R.id.layoutRoot);
        setupBackButton();
        setupSendButton();
        setupBottomButtons();
        setupRecyclerView();
        setupProgressLoading();

        loadData(tourId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null){
            disposable.dispose();
        }
    }

    private void setupBackButton() {
        ImageView button = findViewById(R.id.buttonBack);
        button.setOnClickListener(v -> onBackPressed());
    }

    private void setupSendButton() {
        ImageView button = findViewById(R.id.buttonSend);
        button.setOnClickListener(v -> uploadTracks());
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new TrackEditListAdapter(this);
        adapter.setTracks(tracks);
        adapter.setClickListener(position -> {
            boolean canDownload = tracks.get(position).getFilename() != null;
            EditAudioPopup popup = new EditAudioPopup(layoutRoot, canDownload, new EditAudioPopup.EditAudioPopupCallbacks() {
                @Override
                public void onButtonRenameClick(BasePopup popup) {
                    popup.close();
                    AudioTitleDialog dialog = AudioTitleDialog.getInstance(tracks.get(position).getTitle());
                    dialog.setListener(title -> {
                        renameTrack(position, title);
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
                    deleteTrack(position);
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
                    intent.setType("audio/*");
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.select_audio_file)), MP3_SELECT_REQUEST_CODE);
                },
                () -> Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show())
        );
    }

    private void setupProgressLoading(){
        progressLoading = findViewById(R.id.progressLoading);
    }

    @Override
    public void onBackPressed() {
        if (!BasePopup.closeAllPopups(layoutRoot)) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri audioFileUri = null;
            if (requestCode == MP3_SELECT_REQUEST_CODE || requestCode == AudioRecorderActivity.REQUEST_CODE) {
                if (data.getData() != null) {
                    audioFileUri = data.getData();
                    if (audioFileUri != null){
                        Track newTrack = new Track(
                                FileHelper.getRealFileName(this, audioFileUri).replace(".mp3", ""),
                                FileHelper.getRealAudioPathFromUri(this, audioFileUri),
                                FileHelper.getAudioDuration(this, audioFileUri));
                        tracks.add(newTrack);
                        adapter.setTracks(tracks);
                    }else {
                        ToastHelper.showToast(this, "Can not access audio");
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadData(int tourId){
        progressLoading.setVisibility(View.VISIBLE);

        Tour tour = tourRepository.getTourById(tourId);
        Log.v(App.TAG, "Edit audio tour = "+tour);
        if (tour != null){
            tracks.clear();
            tracks.addAll(tour.getAudio());
            adapter.setTracks(tracks);
        }
        progressLoading.setVisibility(View.GONE);

    }

    private void deleteTrack(int position){

        int trackId = tracks.get(position).getId();

        if (trackId != 0) {
            progressLoading.setVisibility(View.VISIBLE);

            disposable.add(apiHelper.deleteTrack(PreferenceHelper.loadToken(this), String.valueOf(trackId))
                    .subscribe(response -> {
                                tracks.remove(position);
                                adapter.setTracks(tracks);
                                tourRepository.updateTour(tourId);
                                progressLoading.setVisibility(View.GONE);
                            },
                            throwable -> {
                                progressLoading.setVisibility(View.GONE);
                                ToastHelper.showToast(this, getString(R.string.connection_problem));
                            }));
        } else {
            tracks.remove(position);
            adapter.setTracks(tracks);
        }
    }

    private void renameTrack(int position, String title){

        int trackId = tracks.get(position).getId();

        if (trackId != 0) {
            progressLoading.setVisibility(View.VISIBLE);

            disposable.add(apiHelper.renameTrack(PreferenceHelper.loadToken(this), trackId, new RenameTrackRequest(title))
                    .subscribe(response -> {
                                if (response.isSuccess()) {
                                    tracks.get(position).setTitle(title);
                                    adapter.setTracks(tracks);
                                    tourRepository.updateTour(tourId);
                                } else {
                                    ToastHelper.showToast(this, response.getErrorMessage());
                                }
                                progressLoading.setVisibility(View.GONE);
                            },
                            throwable -> {
                                progressLoading.setVisibility(View.GONE);
                                ToastHelper.showToast(this, getString(R.string.connection_problem));
                            }));
        } else {
            tracks.get(position).setTitle(title);
            adapter.setTracks(tracks);
        }
    }

    private void uploadTracks (){
        if (tourId != null) {
            progressLoading.setVisibility(View.VISIBLE);

            int guideId = profileHolder.getGuideId();

            List<Track> tracksToUpload = new ArrayList<>();

            for (Track track: tracks){
                if (track.getFilename() == null){
                    tracksToUpload.add(track);
                    Log.v(App.TAG, "tracksToUpload track = "+track);

                }
            }

            if (tracksToUpload.isEmpty()){
                NavigationHelper.startCongratulationsActivity(EditAudioActivity.this);
            } else {

                disposable.add(albumRepository.uploadAudioToServer(String.valueOf(guideId), tourId.toString(), tracksToUpload)
                        .subscribe(
                                (responseList) -> {
                                    boolean isSuccess = true;
                                    for (UploadTrackResponse response : responseList) {
                                        if (!response.isSuccess()) {
                                            isSuccess = false;
                                            break;
                                        }
                                    }

                                    if (isSuccess) {
                                        tourRepository.updateTour(tourId);
                                        NavigationHelper.startCongratulationsActivity(EditAudioActivity.this);
                                    } else {
                                        progressLoading.setVisibility(View.GONE);
                                        Toast.makeText(EditAudioActivity.this, "One ore more audio was not uploaded!", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                (e) -> {
                                    progressLoading.setVisibility(View.GONE);
                                    Toast.makeText(EditAudioActivity.this, "Error uploading audio", Toast.LENGTH_SHORT).show();
                                }
                        ));
            }
        } else {
            ToastHelper.showToast(this, "Can not upload audio!");
        }
    }

}
