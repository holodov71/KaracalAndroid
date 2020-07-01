package app.karacal.fragments;

import android.app.Activity;
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
import androidx.navigation.fragment.NavHostFragment;

import java.util.Locale;

import app.karacal.R;
import app.karacal.activities.AudioActivity;
import app.karacal.activities.CommentsActivity;
import app.karacal.activities.ProfileActivity;
import app.karacal.helpers.ImageHelper;
import app.karacal.helpers.ShareHelper;
import app.karacal.helpers.ToastHelper;
import app.karacal.models.Guide;
import app.karacal.navigation.NavigationHelper;
import app.karacal.viewmodels.AudioActivityViewModel;
import app.karacal.views.StarsView;
import apps.in.android_logger.LogFragment;

public class AudioDescriptionFragment extends LogFragment {

    private AudioActivityViewModel viewModel;
    private TextView textViewComments;
    private View progressLoading;
    private ProgressBar progressDownloading;
    private ImageView buttonDownload;
    private ImageView imageViewAuthor;
    private TextView textViewAuthor;
    private TextView textViewGuidesCount;
    private View buttonAuthor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(AudioActivityViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio_description, container, false);
        setupBackButton(view);
        setupDownloadButton(view);
        setupBackground(view);
        setupButtons(view);
        setupPrice(view);
        setupRating(view);
        setupTitle(view);
        setupDescription(view);
        setupAddress(view);
        setupDuration(view);
        setupReviews(view);
        setupAuthor(view);
        setupProgressLoading(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observeViewModel();
    }

    private void setupBackButton(View view) {
        ImageView buttonBack = view.findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> getActivity().onBackPressed());
    }

    private void setupDownloadButton(View view) {
        buttonDownload = view.findViewById(R.id.buttonDownload);
        progressDownloading = view.findViewById(R.id.progressDownloading);
        buttonDownload.setOnClickListener(v -> {
            onDownloadingStarted();
            viewModel.downloadTour(requireContext());
        });
    }

    private void setupBackground(View view){
        ImageView imageView = view.findViewById(R.id.imageViewBackground);
        ImageHelper.setImage(imageView, viewModel.getTour().getImageUrl(), viewModel.getTour().getImage(), false);
    }

    private void setupButtons(View view){
        ImageView buttonShare = view.findViewById(R.id.buttonShare);
        buttonShare.setOnClickListener(v -> ShareHelper.share(getActivity(), "Share tour", viewModel.getTour().getTitle(), viewModel.getTour().getDescription()));
        ImageView buttonPlay = view.findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(v -> {
            showLoading();
            viewModel.onListenTourClicked();
        });
        ImageView buttonLike = view.findViewById(R.id.buttonLike);
        buttonLike.setOnClickListener(v -> buttonLike.setSelected(!buttonLike.isSelected()));
    }

    private void setupPrice(View view){
        TextView textView = view.findViewById(R.id.textViewPrice);
        ConstraintLayout constraintLayout = view.findViewById(R.id.layoutAlert);
        if (viewModel.getTour().getPrice() != 0){
            textView.setText(getContext().getString(R.string.price_format, viewModel.getTour().getDoublePrice(), getContext().getString(R.string.euro)));
            textView.setVisibility(View.VISIBLE);
            constraintLayout.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
            constraintLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void setupRating(View view){
        StarsView starsViewTop = view.findViewById(R.id.starsViewTop);
        StarsView starsViewDescription = view.findViewById(R.id.starsViewDescription);
        int rating = viewModel.getTour().getRating();
        starsViewTop.setRating(rating);
        starsViewDescription.setRating(rating);
    }

    private void setupTitle(View view){
        TextView textView = view.findViewById(R.id.textViewTitle);
        textView.setText(viewModel.getTour().getTitle());
    }

    private void setupDescription(View view){
        TextView textView = view.findViewById(R.id.textViewDescription);
        textView.setText(viewModel.getTour().getDescription());
    }

    private void setupAddress(View view){
        TextView textView = view.findViewById(R.id.textViewAddress);
        textView.setText(viewModel.getTour().getAddress());
    }

    private void setupDuration(View view){
        TextView textView = view.findViewById(R.id.textViewDuration);
        int duration = viewModel.getTour().getDuration();
        int durationInMinutes = duration / 60;
        int hours = durationInMinutes / 60;
        String hoursText = hours > 0 ? String.format(Locale.getDefault(), "%d %s", hours, getContext().getString(hours > 1 ? R.string.hours : R.string.hour)) : "";
        int minutes = durationInMinutes % 60;
        String minutesText = String.format(Locale.getDefault(), "%d %s", minutes, getContext().getString(minutes != 1 ? R.string.minutes : R.string.minute));
        textView.setText(String.format("%s %s", hoursText, minutesText));
    }

    private void setupReviews(View view){
        textViewComments = view.findViewById(R.id.textViewReviews);
        setCommentsCount(0);
        textViewComments.setOnClickListener(v -> {
            CommentsActivity.Args args = new CommentsActivity.Args(viewModel.getTour().getId());
            NavigationHelper.startCommentsActivity(getActivity(), args);
        });
        viewModel.loadComments();
    }

    private void setupAuthor(View view){
        imageViewAuthor = view.findViewById(R.id.imageViewAuthor);
        textViewAuthor = view.findViewById(R.id.textViewAuthor);
        buttonAuthor = view.findViewById(R.id.buttonAuthor);
        buttonAuthor.setOnClickListener(v -> {
            ProfileActivity.Args args = new ProfileActivity.Args(viewModel.getGuideId());
            NavigationHelper.startProfileActivity(getActivity(), args);
        });
        textViewGuidesCount = view.findViewById(R.id.textViewGuidesCount);
        ImageView imageViewAlert = view.findViewById(R.id.imageViewAuthorAlert);
//        imageViewAlert.setOnClickListener(v -> ((AudioActivity) getActivity()).showSelectActionPopup());

        viewModel.loadAuthor();
    }

    private void setAuthorData(Guide guide){
        if (guide != null) {
            textViewAuthor.setText(guide.getName());
            ImageHelper.setImage(imageViewAuthor, guide.getAvatarUrl(), R.drawable.ic_person, false);
            textViewGuidesCount.setText(getString(R.string.guide_count_format, guide.getCountGuides(), getString(guide.getCountGuides() != 1 ? R.string.guides : R.string.guide)));
        } else {
            buttonAuthor.setVisibility(View.GONE);
        }
    }

    private void setupProgressLoading(View view){
        progressLoading = view.findViewById(R.id.progressLoading);
    }

    private void showLoading(){
        progressLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        Log.v("hideLoading", "hideLoading");
        progressLoading.setVisibility(View.GONE);
    }

    private void observeViewModel() {
        viewModel.getTourDownloadedAction().observe(getViewLifecycleOwner(), action -> {
            onDownloadingFinished();
            ToastHelper.showToast(requireContext(), getString(R.string.tour_downloaded));
        });

        viewModel.getTourAlreadyDownloadedAction().observe(getViewLifecycleOwner(), action -> {
            onDownloadingFinished();
            ToastHelper.showToast(requireContext(), getString(R.string.tour_already_downloaded));
        });

        viewModel.getDownloadingErrorAction().observe(getViewLifecycleOwner(), errorMsg -> {
            onDownloadingFinished();
            ToastHelper.showToast(requireContext(), errorMsg);
        });

        viewModel.getListenAction().observe(getViewLifecycleOwner(), action ->{
                hideLoading();
                NavHostFragment.findNavController(this).navigate(R.id.audioPlayerFragment);
        });

        viewModel.getPaymentAction().observe(getViewLifecycleOwner(), tourPrice -> {
            hideLoading();
            Activity activity = getActivity();
            if (activity != null) {
                ((AudioActivity) activity).showSelectPlanDialog(tourPrice);
            }
        });

        viewModel.getComments().observe(getViewLifecycleOwner(), comments -> {
            if (comments != null){
                setCommentsCount(comments.size());
            }
        });

        viewModel.getErrorAction().observe(getViewLifecycleOwner(), errorMsg -> {
            hideLoading();
            ToastHelper.showToast(requireContext(), errorMsg);
        });

        viewModel.getGuide().observe(getViewLifecycleOwner(), this::setAuthorData);
    }

    private void setCommentsCount(int count){
        textViewComments.setText(getString(R.string.comments_count_format, count, getString(count != 1 ? R.string.comments : R.string.comment)));
    }

    private void onDownloadingStarted(){
        progressDownloading.setVisibility(View.VISIBLE);
        buttonDownload.setImageDrawable(null);
    }

    private void onDownloadingFinished(){
        progressDownloading.setVisibility(View.GONE);
        buttonDownload.setImageResource(R.drawable.ic_download);
    }

}
