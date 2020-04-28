package app.karacal.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import java.io.Serializable;

import app.karacal.R;
import app.karacal.helpers.DummyHelper;
import app.karacal.navigation.ActivityArgs;
import app.karacal.navigation.NavigationHelper;
import app.karacal.popups.BasePopup;
import app.karacal.popups.ReportProblemPopup;
import app.karacal.popups.SelectActionPopup;
import app.karacal.popups.SelectPlanPopup;
import app.karacal.popups.ShareImpressionPopup;
import app.karacal.viewmodels.AudioActivityViewModel;
import apps.in.android_logger.LogActivity;

public class AudioActivity extends LogActivity {

    public static class Args extends ActivityArgs implements Serializable {

        private final int tourId;

        public Args(int tourId) {
            this.tourId = tourId;
        }

        public int getTourId() {
            return tourId;
        }

    }

    private AudioActivityViewModel viewModel;

    private ConstraintLayout layoutRoot;

    private SelectActionPopup.SelectActionPopupCallbacks selectActionPopupCallbacks = new SelectActionPopup.SelectActionPopupCallbacks() {
        @Override
        public void onButtonLikeClick(BasePopup popup) {
            showShareImpressionPopup();
        }

        @Override
        public void onButtonDownloadAlbumClick(BasePopup popup) {
            DummyHelper.dummyAction(AudioActivity.this);
        }

        @Override
        public void onButtonShareTrackClick(BasePopup popup) {
            DummyHelper.dummyAction(AudioActivity.this);
        }

        @Override
        public void onButtonReportProblemClick(BasePopup popup) {
            showReportProblemPopup();
        }
    };

    private ShareImpressionPopup.ShareImpressionPopupCallbacks shareImpressionPopupCallbacks = new ShareImpressionPopup.ShareImpressionPopupCallbacks() {
        @Override
        public void onButtonDonateClick(BasePopup popup) {
            NavigationHelper.startDontateActivity(AudioActivity.this);
        }

        @Override
        public void onButtonPutGuideInFavorClick(BasePopup popup) {
            DummyHelper.dummyAction(AudioActivity.this);
        }

        @Override
        public void onButtonWriteCommentClick(BasePopup popup) {
            DummyHelper.dummyAction(AudioActivity.this);
        }

        @Override
        public void onButtonSubmitClick(BasePopup popup) {
            DummyHelper.dummyAction(AudioActivity.this);
            onBackPressed();
        }
    };

    private SelectPlanPopup.SelectPlanPopupCallbacks selectPlanPopupCallbacks = new SelectPlanPopup.SelectPlanPopupCallbacks() {
        @Override
        public void onButtonCancelClick(BasePopup popup) {
            onBackPressed();
        }

        @Override
        public void onButtonSinglePriceClick(BasePopup popup) {
            DummyHelper.dummyAction(AudioActivity.this);
            onBackPressed();
        }

        @Override
        public void onButtonRegularPriceClick(BasePopup popup) {
            DummyHelper.dummyAction(AudioActivity.this);
            onBackPressed();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        Args args = ActivityArgs.fromBundle(Args.class, getIntent().getExtras());
        int tourId = args.getTourId();
        viewModel = new ViewModelProvider(this, new AudioActivityViewModel.AudioActivityViewModelFactory(tourId)).get(AudioActivityViewModel.class);
        layoutRoot = findViewById(R.id.layoutRoot);
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

    public void showSelectPlanDialog() {
        SelectPlanPopup popup = new SelectPlanPopup(layoutRoot, selectPlanPopupCallbacks);
        popup.show();
    }


    @Override
    public void onBackPressed() {
        if (!BasePopup.closeAllPopups(layoutRoot)) {
            super.onBackPressed();
        }
    }
}
