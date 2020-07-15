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

import app.karacal.R;
import app.karacal.activities.AudioActivity;
import app.karacal.activities.CommentsActivity;
import app.karacal.activities.ProfileActivity;
import app.karacal.helpers.ImageHelper;
import app.karacal.helpers.ShareHelper;
import app.karacal.helpers.ToastHelper;
import app.karacal.models.Guide;
import app.karacal.models.Tour;
import app.karacal.navigation.NavigationHelper;
import app.karacal.viewmodels.AudioActivityViewModel;
import app.karacal.views.StarsView;
import apps.in.android_logger.LogFragment;

public class AudioDescriptionFragment extends LogFragment {

    private AudioActivityViewModel viewModel;
    private ImageView imageViewTour;
    private TextView textViewComments;
    private View progressLoading;
    private TextView textViewPrice;
    private ConstraintLayout constraintLayoutPrice;
    private ImageView buttonShare;
    private ProgressBar progressDownloading;
    private ImageView buttonDownload;
    private ImageView imageViewAuthor;
    private TextView textViewAuthor;
    private TextView textViewGuidesCount;
    private View buttonAuthor;
    private StarsView starsViewTop;
    private StarsView starsViewDescription;
    private TextView textViewTitle;
    private TextView textViewDescription;
    private TextView textViewAddress;
    private TextView textViewDuration;

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
    public void onResume() {
        super.onResume();
        viewModel.loadComments();
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
            viewModel.downloadTour(requireContext());
        });
    }

    private void setupBackground(View view){
        imageViewTour = view.findViewById(R.id.imageViewBackground);
    }

    private void setupButtons(View view){
        buttonShare = view.findViewById(R.id.buttonShare);
        ImageView buttonPlay = view.findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(v -> {
            showLoading();
            viewModel.onListenTourClicked();
        });
        ImageView buttonLike = view.findViewById(R.id.buttonLike);
        buttonLike.setOnClickListener(v -> buttonLike.setSelected(!buttonLike.isSelected()));
    }

    private void setupPrice(View view){
        textViewPrice = view.findViewById(R.id.textViewPrice);
        constraintLayoutPrice = view.findViewById(R.id.layoutAlert);

    }

    private void setupRating(View view){
        starsViewTop = view.findViewById(R.id.starsViewTop);
        starsViewDescription = view.findViewById(R.id.starsViewDescription);
    }

    private void setupTitle(View view){
        textViewTitle = view.findViewById(R.id.textViewTitle);
    }

    private void setupDescription(View view){
        textViewDescription = view.findViewById(R.id.textViewDescription);
    }

    private void setupAddress(View view){
        textViewAddress = view.findViewById(R.id.textViewAddress);
    }

    private void setupDuration(View view){
        textViewDuration = view.findViewById(R.id.textViewDuration);
    }

    private void setupReviews(View view){
        textViewComments = view.findViewById(R.id.textViewReviews);
        setCommentsCount(0);
    }

    private void setupAuthor(View view){
        imageViewAuthor = view.findViewById(R.id.imageViewAuthor);
        textViewAuthor = view.findViewById(R.id.textViewAuthor);
        buttonAuthor = view.findViewById(R.id.buttonAuthor);

        textViewGuidesCount = view.findViewById(R.id.textViewGuidesCount);
        ImageView imageViewAlert = view.findViewById(R.id.imageViewAuthorAlert);
        imageViewAlert.setOnClickListener(v -> ((AudioActivity) getActivity()).showSelectActionPopup());
    }

    private void setAuthorData(Guide guide){
        if (guide != null) {
            textViewAuthor.setText(guide.getName());
            ImageHelper.setImage(imageViewAuthor, guide.getAvatarUrl(), R.drawable.ic_person, false);

            buttonAuthor.setOnClickListener(v -> {
                ProfileActivity.Args args = new ProfileActivity.Args(guide.getId());
                NavigationHelper.startProfileActivity(getActivity(), args);
            });
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
        viewModel.getTour().observe(getViewLifecycleOwner(), tour -> {
            if (tour != null){
                setData(tour);
            }
        });

        viewModel.getTourDownloadedAction().observe(getViewLifecycleOwner(), action -> {
            ToastHelper.showToast(requireContext(), getString(R.string.tour_downloaded));
        });

        viewModel.getDownloadingErrorAction().observe(getViewLifecycleOwner(), errorMsg -> {
            ToastHelper.showToast(requireContext(), errorMsg);
        });

        viewModel.getTourDownloading().observe(getViewLifecycleOwner(), isDownloading -> {
            if (isDownloading){
                onDownloadingStarted();
            } else {
                onDownloadingFinished();
            }
        });

        viewModel.getToursCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null){
                textViewGuidesCount.setText(getString(R.string.guide_count_format, count, getString(count != 1 ? R.string.guides : R.string.guide)));
            }
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

    private void setData(Tour tour) {
        ImageHelper.setImage(imageViewTour, tour.getImageUrl(), tour.getImage(), false);

        if (tour.getPrice() != 0){
            textViewPrice.setText(getContext().getString(R.string.price_format, tour.getDoublePrice(), getContext().getString(R.string.euro)));
            textViewPrice.setVisibility(View.VISIBLE);
            constraintLayoutPrice.setVisibility(View.VISIBLE);
        } else {
            textViewPrice.setVisibility(View.INVISIBLE);
            constraintLayoutPrice.setVisibility(View.INVISIBLE);
        }

        buttonShare.setOnClickListener(v -> ShareHelper.share(getActivity(), "Share tour", tour.getTitle(), tour.getDescription()));

        int rating = tour.getRating();
        starsViewTop.setRating(rating);
        starsViewDescription.setRating(rating);

        textViewTitle.setText(tour.getTitle());
        textViewDescription.setText(tour.getDescription());
        textViewAddress.setText(tour.getAddress());
        if (getContext() != null) {
            textViewDuration.setText(tour.getFormattedTourDuration(getContext()));
        }

        textViewComments.setOnClickListener(v -> {
            CommentsActivity.Args args = new CommentsActivity.Args(tour.getId());
            NavigationHelper.startCommentsActivity(getActivity(), args);
        });

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
