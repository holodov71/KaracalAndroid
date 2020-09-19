package app.karacal.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;

import app.karacal.App;
import app.karacal.R;
import app.karacal.adapters.TourVerticalListAdapter;
import app.karacal.helpers.DummyHelper;
import app.karacal.helpers.ImageHelper;
import app.karacal.helpers.ShareHelper;
import app.karacal.helpers.ToastHelper;
import app.karacal.models.Guide;
import app.karacal.navigation.ActivityArgs;
import app.karacal.navigation.NavigationHelper;
import app.karacal.popups.BasePopup;
import app.karacal.popups.ShareImpressionPopup;
import app.karacal.viewmodels.ProfileActivityViewModel;
import apps.in.android_logger.LogActivity;

public class ProfileActivity extends LogActivity {

    public static class Args extends ActivityArgs implements Serializable {

        private final int guideId;

        public Args(int guideId) {
            this.guideId = guideId;
        }

        public int getGuideId() {
            return guideId;
        }

    }

//    private SelectActionPopup.SelectActionPopupCallbacks selectActionPopupCallbacks = new SelectActionPopup.SelectActionPopupCallbacks() {
//        @Override
//        public void onButtonLikeClick(BasePopup popup) {
//            showShareImpressionPopup();
//        }
//
//        @Override
//        public void onButtonDownloadAlbumClick(BasePopup popup) {
//            DummyHelper.dummyAction(ProfileActivity.this);
//        }
//
//        @Override
//        public void onButtonShareTrackClick(BasePopup popup) {
//            DummyHelper.dummyAction(ProfileActivity.this);
//        }
//
//        @Override
//        public void onButtonReportProblemClick(BasePopup popup) {
//            showReportProblemPopup();
//        }
//    };

    private ShareImpressionPopup.ShareImpressionPopupCallbacks shareImpressionPopupCallbacks = new ShareImpressionPopup.ShareImpressionPopupCallbacks() {
        @Override
        public void onButtonDonateClick(BasePopup popup) {
            DonateActivity.Args args = new DonateActivity.Args(viewModel.getGuideId());
            NavigationHelper.startDonateActivity(ProfileActivity.this, args);
            popup.close();
        }

        @Override
        public void onButtonPutGuideInFavorClick(BasePopup popup) {
            DummyHelper.dummyAction(ProfileActivity.this);
        }

        @Override
        public void onButtonWriteCommentClick(BasePopup popup) {
            DummyHelper.dummyAction(ProfileActivity.this);
        }

        @Override
        public void onButtonSubmitRatingClick(BasePopup popup, int rating) {
            viewModel.setRating(rating);
            onBackPressed();
        }
    };

    private ConstraintLayout layoutRoot;

    private ProfileActivityViewModel viewModel;

    private TourVerticalListAdapter adapter;

    private ImageView avatarImage;
    private TextView textViewName;
    private TextView textViewLocation;
    private TextView textViewDescription;
    private ImageView buttonShare;
    private View progressLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        setContentView(R.layout.activity_profile);
        layoutRoot = findViewById(R.id.layoutRoot);

        Args args = ActivityArgs.fromBundle(Args.class, getIntent().getExtras());
        int guideId = args.getGuideId();
        viewModel = new ViewModelProvider(this, new ProfileActivityViewModel.ProfileViewModelFactory(guideId)).get(ProfileActivityViewModel.class);


        setupButtons();
        setupAuthor();
        setupRecyclerView();
        setupLoading();

        observeViewModel();
        viewModel.loadData();
    }

    private void setupButtons(){
        ImageView buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());
        ImageView buttonAlert = findViewById(R.id.buttonAuthorAlert);
        buttonAlert.setVisibility(View.GONE);
        buttonAlert.setOnClickListener(v -> showShareImpressionPopup());
    }

    private void setupLoading(){
        progressLoading = findViewById(R.id.progressLoading);
    }

    private void setupAuthor(){
        avatarImage = findViewById(R.id.imageViewAvatar);
        textViewName = findViewById(R.id.textViewGuideName);
        textViewLocation = findViewById(R.id.textViewGuideLocation);
        textViewDescription = findViewById(R.id.textViewAuthorDescription);
        buttonShare = findViewById(R.id.buttonShare);
        ImageView buttonLike = findViewById(R.id.buttonLike);
        buttonLike.setOnClickListener(v -> buttonLike.setSelected(!buttonLike.isSelected()));
    }

    private void setupRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        NestedScrollView scrollView = findViewById(R.id.scrollView);
        adapter = new TourVerticalListAdapter(this);
        adapter.setClickListener(tourId -> {
            AudioActivity.Args args = new AudioActivity.Args(tourId);
            NavigationHelper.startAudioActivity(this, args);
        });
        recyclerView.setAdapter(adapter);

        new Handler().postDelayed(() -> scrollView.fullScroll(View.FOCUS_UP), 300);

    }

    private void showLoading(){
        progressLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        progressLoading.setVisibility(View.GONE);
    }

    private void observeViewModel(){
        viewModel.getGuide().observe(this, this::setAuthorData);

        viewModel.getTours().observe(this, tours -> {
            if (adapter != null && tours != null) {
                adapter.setTours(tours);
            }
        });

        viewModel.ratingSavedAction.observe(this, action -> {
            ToastHelper.showToast(this, getString(R.string.rating_saved));
        });

        viewModel.onErrorAction.observe(this, msg -> {
            ToastHelper.showToast(this, msg);
        });

        viewModel.isLoading().observe(this, isLoading -> {
            if(isLoading){
                showLoading();
            } else {
                hideLoading();
            }
        });
    }

    private void setAuthorData(Guide guide){
        if (guide != null) {
            textViewName.setText(guide.getName());
            textViewLocation.setText(guide.getLocalization());
            ImageHelper.setImage(avatarImage, guide.getAvatarUrl(), R.drawable.ic_person, true);
            textViewDescription.setText(guide.getDescription());
            buttonShare.setOnClickListener(v -> ShareHelper.share(this, "Share guide", String.format("%s (%s)", guide.getName(), guide.getLocalization()), guide.getDescription()));
        } else {
            finish();
        }
    }

//    public void showSelectActionPopup() {
//        SelectActionPopup popup = new SelectActionPopup(layoutRoot, selectActionPopupCallbacks);
//        popup.show();
//    }
//
//    public void showReportProblemPopup() {
//        ReportProblemPopup popup = new ReportProblemPopup(layoutRoot, new ReportProblemPopup.ReportProblemPopupDefaultCallbacks(this));
//        popup.show();
//    }


    public void showShareImpressionPopup() {
        ShareImpressionPopup popup = new ShareImpressionPopup(layoutRoot, shareImpressionPopupCallbacks, false);
        popup.show();
    }


    @Override
    public void onBackPressed() {
        if (!BasePopup.closeAllPopups(layoutRoot)) {
            super.onBackPressed();
        }
    }
}
