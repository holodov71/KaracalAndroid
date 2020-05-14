package app.karacal.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.adapters.TourVerticalListAdapter;
import app.karacal.data.repository.GuideRepository;
import app.karacal.data.repository.TourRepository;
import app.karacal.helpers.DummyHelper;
import app.karacal.helpers.ShareHelper;
import app.karacal.models.Guide;
import app.karacal.navigation.ActivityArgs;
import app.karacal.navigation.NavigationHelper;
import app.karacal.popups.BasePopup;
import app.karacal.popups.ReportProblemPopup;
import app.karacal.popups.SelectActionPopup;
import app.karacal.popups.ShareImpressionPopup;
import app.karacal.views.NoScrollLinearLayoutManager;
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

    private SelectActionPopup.SelectActionPopupCallbacks selectActionPopupCallbacks = new SelectActionPopup.SelectActionPopupCallbacks() {
        @Override
        public void onButtonLikeClick(BasePopup popup) {
            showShareImpressionPopup();
        }

        @Override
        public void onButtonDownloadAlbumClick(BasePopup popup) {
            DummyHelper.dummyAction(ProfileActivity.this);
        }

        @Override
        public void onButtonShareTrackClick(BasePopup popup) {
            DummyHelper.dummyAction(ProfileActivity.this);
        }

        @Override
        public void onButtonReportProblemClick(BasePopup popup) {
            showReportProblemPopup();
        }
    };

    private ShareImpressionPopup.ShareImpressionPopupCallbacks shareImpressionPopupCallbacks = new ShareImpressionPopup.ShareImpressionPopupCallbacks() {
        @Override
        public void onButtonDonateClick(BasePopup popup) {
            NavigationHelper.startDontateActivity(ProfileActivity.this);
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
        public void onButtonSubmitClick(BasePopup popup) {
            DummyHelper.dummyAction(ProfileActivity.this);
            onBackPressed();
        }
    };

    private ConstraintLayout layoutRoot;

    @Inject
    TourRepository tourRepository;

    @Inject
    GuideRepository guideRepository;

    private Guide author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        setContentView(R.layout.activity_profile);
        layoutRoot = findViewById(R.id.layoutRoot);

        Args args = ActivityArgs.fromBundle(Args.class, getIntent().getExtras());
        int guideId = args.getGuideId();
        author = guideRepository.getGuide(guideId);

        setupButtons();
        setupAuthor();
        setupRecyclerView();
    }

    private void setupButtons(){
        ImageView buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());
        ImageView buttonAlert = findViewById(R.id.buttonAuthorAlert);
        buttonAlert.setOnClickListener(v -> showSelectActionPopup());
    }

    private void setupAuthor(){
//        int avatarResId = R.mipmap.avatar_example;
//        String name = "Alexander McQueen";
//        String location = "Paris, France";
//        String description = "Les considérations idéologiques d'ordre supérieur, ainsi que la mise en œuvre des objectifs planifiés, nécessitent la définition et le perfectionnement du système de formation du personnel répondant aux besoins urgents. Ainsi, la consultation avec un atout large détermine dans une large mesure la création de systèmes participatifs.";
        ImageView avatar = findViewById(R.id.imageViewAvatar);
        if (author.getAvatarId() != -1) {
            avatar.setImageResource(author.getAvatarId());
        } else {
            avatar.setImageResource(R.drawable.ic_person);
        }
        TextView textViewName = findViewById(R.id.textViewGuideName);
        textViewName.setText(author.getName());
        TextView textViewLocation = findViewById(R.id.textViewGuideLocation);
        textViewLocation.setText(author.getLocalization());
        TextView textViewDescription = findViewById(R.id.textViewAuthorDescription);
        textViewDescription.setText(author.getDescription());
        ImageView buttonShare = findViewById(R.id.buttonShare);
        buttonShare.setOnClickListener(v -> ShareHelper.share(this, "Share guide", String.format("%s (%s)", author.getName(), author.getLocalization()), author.getDescription()));
        ImageView buttonLike = findViewById(R.id.buttonLike);
        buttonLike.setOnClickListener(v -> buttonLike.setSelected(!buttonLike.isSelected()));
    }

    private void setupRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ScrollView scrollView = findViewById(R.id.scrollView);
        TourVerticalListAdapter adapter = new TourVerticalListAdapter(this);
        adapter.setTours(tourRepository.getToursByAuthor(author.getId()));
        adapter.setClickListener(tourId -> {
            AudioActivity.Args args = new AudioActivity.Args(tourId);
            NavigationHelper.startAudioActivity(this, args);
        });
        recyclerView.setAdapter(adapter);

        new Handler().postDelayed(() -> scrollView.fullScroll(View.FOCUS_UP), 300);

    }

    public void showSelectActionPopup() {
        SelectActionPopup popup = new SelectActionPopup(layoutRoot, selectActionPopupCallbacks);
        popup.show();
    }

    public void showReportProblemPopup() {
        ReportProblemPopup popup = new ReportProblemPopup(layoutRoot, new ReportProblemPopup.ReportProblemPopupDefaultCallbacks(this));
        popup.show();
    }


    public void showShareImpressionPopup() {
        ShareImpressionPopup popup = new ShareImpressionPopup(layoutRoot, shareImpressionPopupCallbacks);
        popup.show();
    }


    @Override
    public void onBackPressed() {
        if (!BasePopup.closeAllPopups(layoutRoot)) {
            super.onBackPressed();
        }
    }
}
