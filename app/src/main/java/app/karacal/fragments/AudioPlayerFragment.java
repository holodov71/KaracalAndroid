package app.karacal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import app.karacal.R;
import app.karacal.adapters.TrackListAdapter;
import app.karacal.helpers.DummyHelper;
import app.karacal.models.Player;
import app.karacal.navigation.NavigationHelper;
import app.karacal.viewmodels.AudioActivityViewModel;
import apps.in.android_logger.LogFragment;

public class AudioPlayerFragment extends LogFragment {

    private AudioActivityViewModel viewModel;

    private TrackListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(AudioActivityViewModel.class);
        viewModel.getPlayer().bindLifecycle(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio_player, container, false);
        setupBackButton(view);
        setupDownloadButton(view);
        setupBackground(view);
        setupTracksTextView(view);
        setupCommentsTextView(view);
        setupTracksList(view);
        setupAudioButtons(view);
        setupPlayerControls(view);
        return view;
    }

    private void setupBackButton(View view) {
        ImageView buttonBack = view.findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> getActivity().onBackPressed());
    }

    private void setupDownloadButton(View view) {
        ImageView buttonDownload = view.findViewById(R.id.buttonDownload);
        buttonDownload.setOnClickListener(v -> DummyHelper.dummyAction(getContext()));
    }


    private void setupBackground(View view){
        ImageView imageView = view.findViewById(R.id.imageViewTitle);
        imageView.setImageResource(viewModel.getTour().getImage());
    }

    private void setupAudioButtons(View view) {
        ImageView rewindButton = view.findViewById(R.id.buttonAudioRewind);
        rewindButton.setOnClickListener(v -> viewModel.getPlayer().seekBack());
        ImageView forwardButton = view.findViewById(R.id.buttonAudioForward);
        forwardButton.setOnClickListener(v -> viewModel.getPlayer().seekForward());
        ImageView playButton = view.findViewById(R.id.buttonPlay);
        playButton.setOnClickListener(v -> viewModel.getPlayer().playTrack());
    }

    private void setupTracksTextView(View view) {
        TextView tracksTextView = view.findViewById(R.id.textViewTracks);
        int count = viewModel.getAlbum().getTracks().size();
        tracksTextView.setText(getString(R.string.tracks_count_format, count, getString(count != 1 ? R.string.tracks : R.string.track)));
    }

    private void setupCommentsTextView(View view) {
        TextView commentsTextView = view.findViewById(R.id.textViewComments);
        int count = 3;
        commentsTextView.setText(getString(R.string.comments_count_format, count, getString(count != 1 ? R.string.comments : R.string.comment)));
        commentsTextView.setOnClickListener(v -> {
            NavigationHelper.startCommentsActivity(getActivity());
        });
    }

    private void setupTracksList(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new TrackListAdapter(getContext());
        adapter.setTracks(viewModel.getAlbum().getTracks());
        adapter.setClickListener(position -> viewModel.getPlayer().playTrack(position));
        viewModel.getPlayer().getPositionInfoLiveData().observe(this, (positionInfo) -> adapter.updateProgress(viewModel.getPlayer().getCurrentTrack(), positionInfo));
        recyclerView.setAdapter(adapter);
    }

    private void setupPlayerControls(View view) {
        ConstraintLayout constraintLayoutPlayer = view.findViewById(R.id.constraintLayoutPlayer);
        ImageView imageView = view.findViewById(R.id.imageViewAlbumTitle);
        imageView.setImageResource(viewModel.getTour().getImage());
        TextView textViewAlbumTitle = view.findViewById(R.id.textViewAlbumTitle);
        textViewAlbumTitle.setText(viewModel.getAlbum().getTitle());
        TextView textViewTrackTitle = view.findViewById(R.id.textViewTrackTitle);
        ImageView buttonPause = view.findViewById(R.id.buttonPause);
        buttonPause.setOnClickListener(v -> viewModel.getPlayer().pause());
        ImageView buttonNext = view.findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(v -> viewModel.getPlayer().nextTrack());
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        viewModel.getPlayer().getPlayerStateLiveData().observe(this, playerState -> {
            TransitionManager.beginDelayedTransition(view.findViewById(R.id.layoutRoot));
            constraintLayoutPlayer.setVisibility(playerState == Player.PlayerState.IDLE ? View.GONE : View.VISIBLE);
        });
        viewModel.getPlayer().getCurrentTrackLiveData().observe(this, currentTrack -> {
            if (currentTrack != null) {
                textViewTrackTitle.setText(viewModel.getAlbum().getTracks().get(currentTrack).getTitle());
                textViewTrackTitle.setVisibility(View.VISIBLE);
            } else {
                textViewTrackTitle.setVisibility(View.INVISIBLE);
            }
        });
        viewModel.getPlayer().getPositionInfoLiveData().observe(this, positionInfo -> {
            if (positionInfo != null) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(positionInfo.getDuration());
                progressBar.setProgress(positionInfo.getCurrentPosition());
            } else {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }



}
