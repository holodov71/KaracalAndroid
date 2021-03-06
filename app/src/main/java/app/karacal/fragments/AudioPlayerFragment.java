package app.karacal.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import app.karacal.R;
import app.karacal.activities.AudioActivity;
import app.karacal.activities.CommentsActivity;
import app.karacal.adapters.TrackListAdapter;
import app.karacal.helpers.ImageHelper;
import app.karacal.helpers.ToastHelper;
import app.karacal.models.Player;
import app.karacal.models.Tour;
import app.karacal.navigation.NavigationHelper;
import app.karacal.viewmodels.AudioActivityViewModel;
import apps.in.android_logger.LogFragment;

public class AudioPlayerFragment extends LogFragment {

    private static final String TAG = "AudioPlayerFragment";

    private AudioActivityViewModel viewModel;

    private OnPlayerScreenListener onPlayerScreenListener;

    private TrackListAdapter adapter;

    private View progressLoading;
    private ImageView imageViewTour;
    private ImageView playButton;
    private ImageView buttonPause;
    private TextView tracksTextView;
    private TextView commentsTextView;
    private ImageView imageViewAlbumTitle;
    private TextView textViewAlbumTitle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        viewModel = new ViewModelProvider(getActivity()).get(AudioActivityViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio_player, container, false);
        setupProgressLoading(view);
        setupBackButton(view);
        setupDownloadButton(view);
        setupBackground(view);
        setupTracksTextView(view);
        setupCommentsTextView(view);
        setupTracksList(view);
        setupAudioButtons(view);
        setupPlayerControls(view);

        viewModel.loadTracks();

        observeTour();
        observeTracks();
        observeDownloading();
        observeComments();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlayerScreenListener){
            onPlayerScreenListener = (OnPlayerScreenListener) context;
        } else {
            throw new RuntimeException("Activity must implement OnPlayerScreenListener interface");
        }
    }

    private void setupBackButton(View view) {
        ImageView buttonBack = view.findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> getActivity().onBackPressed());
    }

    private void setupDownloadButton(View view) {
        ImageView buttonOptions = view.findViewById(R.id.buttonOptions);
        buttonOptions.setOnClickListener(v -> {
            ((AudioActivity) getActivity()).showSelectActionPopup(viewModel.isTourDownloaded(requireContext()));
        });
    }

    private void setupProgressLoading(View view){
        progressLoading = view.findViewById(R.id.progressLoading);
    }

    private void showLoading(){
        progressLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        progressLoading.setVisibility(View.GONE);
    }

    private void setupBackground(View view){
        imageViewTour = view.findViewById(R.id.imageViewTitle);
    }

    private void setupAudioButtons(View view) {
        ImageView rewindButton = view.findViewById(R.id.buttonAudioRewind);
        rewindButton.setOnClickListener(v -> viewModel.getPlayer().seekBack());
        ImageView forwardButton = view.findViewById(R.id.buttonAudioForward);
        forwardButton.setOnClickListener(v -> viewModel.getPlayer().seekForward());
        playButton = view.findViewById(R.id.buttonPlay);
        playButton.setOnClickListener(v -> onPlayPauseClick());
    }

    private void setupTracksTextView(View view) {
        tracksTextView = view.findViewById(R.id.textViewTracks);
    }

    private void setupCommentsTextView(View view) {
        commentsTextView = view.findViewById(R.id.textViewComments);
        setCommentsCount(0);
        viewModel.loadComments();
    }

    private void setupTracksList(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new TrackListAdapter(getContext());
        adapter.setClickListener(position -> {
            onPlayerScreenListener.onPlayerPlayClicked(viewModel.getPlayer().getTourId());
            viewModel.getPlayer().playTrack(position);
        });
        viewModel.getPlayer().getPositionInfoLiveData().observe(getViewLifecycleOwner(), (positionInfo) -> adapter.updateProgress(viewModel.getPlayer().getCurrentTrack(), positionInfo));
        recyclerView.setAdapter(adapter);
    }

    private void setupPlayerControls(View view) {
        ConstraintLayout constraintLayoutPlayer = view.findViewById(R.id.constraintLayoutPlayer);
        imageViewAlbumTitle = view.findViewById(R.id.imageViewAlbumTitle);

        textViewAlbumTitle = view.findViewById(R.id.textViewAlbumTitle);

        TextView textViewTrackTitle = view.findViewById(R.id.textViewTrackTitle);
        buttonPause = view.findViewById(R.id.buttonPause);
        buttonPause.setOnClickListener(v -> onPlayPauseClick());
        ImageView buttonNext = view.findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(v -> {
            onPlayerScreenListener.onPlayerPlayClicked(viewModel.getPlayer().getTourId());
            viewModel.getPlayer().nextTrack();
        });
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        viewModel.getPlayer().getPlayerStateLiveData().observe(getViewLifecycleOwner(), playerState -> {
            TransitionManager.beginDelayedTransition(view.findViewById(R.id.layoutRoot));
            constraintLayoutPlayer.setVisibility(playerState == Player.PlayerState.IDLE ? View.GONE : View.VISIBLE);
            if (playerState == Player.PlayerState.PLAY){
                buttonPause.setImageResource(R.drawable.ic_pause);
                playButton.setImageResource(R.drawable.ic_pause);
            }else {
                buttonPause.setImageResource(R.drawable.ic_play);
                playButton.setImageResource(R.drawable.ic_play);
            }
        });
        viewModel.getPlayer().getCurrentTrackLiveData().observe(getViewLifecycleOwner(), currentTrack -> {
            if (currentTrack != null) {
                textViewTrackTitle.setText(viewModel.getTrackTitle(currentTrack));
                textViewTrackTitle.setVisibility(View.VISIBLE);
            } else {
                textViewTrackTitle.setVisibility(View.INVISIBLE);
            }
        });
        viewModel.getPlayer().getPositionInfoLiveData().observe(getViewLifecycleOwner(), positionInfo -> {
            if (positionInfo != null) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(positionInfo.getDuration());
                progressBar.setProgress(positionInfo.getCurrentPosition());
            } else {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void onPlayPauseClick(){
        onPlayerScreenListener.onPlayerPlayClicked(viewModel.getPlayer().getTourId());
        if (viewModel.getPlayer().getPlayerState() == Player.PlayerState.PLAY){
            viewModel.getPlayer().pause();
        }else {
            viewModel.getPlayer().playTrack();
        }
    }

    private void observeTour(){
        viewModel.getTour().observe(getViewLifecycleOwner(), tour -> {
            if (tour != null){
                setTourData(tour);
            }
        });
    }

    private void setTourData(Tour tour) {
        ImageHelper.setImage(imageViewTour, tour.getImageUrl(), tour.getImage(), false);

        commentsTextView.setOnClickListener(v -> {
            CommentsActivity.Args args = new CommentsActivity.Args(tour.getId());
            NavigationHelper.startCommentsActivity(getActivity(), args);
        });

        ImageHelper.setImage(imageViewAlbumTitle, tour.getImageUrl(), tour.getImage(), false);

        textViewAlbumTitle.setText(tour.getTitle());

    }

    private void observeTracks(){
        viewModel.getAlbum().observe(getViewLifecycleOwner(), album -> {
            adapter.setTracks(album.getTracks());
            int count = album.getTracks().size();
            tracksTextView.setText(getString(R.string.tracks_count_format, count, getString(count != 1 ? R.string.tracks : R.string.track)));
        });
    }

    private void observeComments(){
        viewModel.getComments().observe(getViewLifecycleOwner(), comments -> {
            if (comments != null){
                setCommentsCount(comments.size());
            }
        });
    }

    private void setCommentsCount(int count){
        commentsTextView.setText(getString(R.string.comments_count_format, count, getString(count != 1 ? R.string.comments : R.string.comment)));
    }

    private void observeDownloading(){
        viewModel.getTourDownloadedAction().observe(getViewLifecycleOwner(), action -> {
            ToastHelper.showToast(requireContext(), getString(R.string.tour_downloaded));
        });

        viewModel.getMessageAction().observe(getViewLifecycleOwner(), msg -> {
            ToastHelper.showToast(requireContext(), msg);
        });

        viewModel.getTourDownloading().observe(getViewLifecycleOwner(), isDownloading -> {
            if (isDownloading){
                onDownloadingStarted();
            } else {
                onDownloadingFinished();
            }
        });

    }

    private void onDownloadingStarted(){
        showLoading();
    }

    private void onDownloadingFinished(){
        hideLoading();
    }

    public interface OnPlayerScreenListener{
        void onPlayerPlayClicked(int tourId);
    }

}
