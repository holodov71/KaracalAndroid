package app.karacal.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import java.io.Serializable;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.fragments.AudioPlayerFragment;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.DummyHelper;
import app.karacal.helpers.WebLinkHelper;
import app.karacal.models.Player;
import app.karacal.models.Tour;
import app.karacal.navigation.ActivityArgs;
import app.karacal.navigation.NavigationHelper;
import app.karacal.popups.BasePopup;
import app.karacal.popups.ReportProblemPopup;
import app.karacal.popups.SelectActionPopup;
import app.karacal.popups.SelectPlanPopup;
import app.karacal.popups.ShareImpressionPopup;
import app.karacal.service.PlayerService;
import app.karacal.viewmodels.AudioActivityViewModel;
import apps.in.android_logger.LogActivity;

public class AudioActivity extends LogActivity implements AudioPlayerFragment.OnPlayerScreenListener {

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
            viewModel.downloadTour(AudioActivity.this);
            onBackPressed();
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
            viewModel.onDonateClicked();
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
            onBackPressed();
            viewModel.payTour();
        }

        @Override
        public void onButtonRegularPriceClick(BasePopup popup) {
            onBackPressed();
            NavigationHelper.startSubscriptionActivity(AudioActivity.this);
        }
    };

    @Inject
    ApiHelper apiHelper;

    private boolean serviceBound;
    private PlayerService playerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        setContentView(R.layout.activity_audio);

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        Log.v(App.TAG, "Link to Activity = "+data);

        Args args = ActivityArgs.fromBundle(Args.class, getIntent().getExtras());

            int tourId = args.getTourId();
        viewModel = new ViewModelProvider(this, new AudioActivityViewModel.AudioActivityViewModelFactory(tourId)).get(AudioActivityViewModel.class);
        layoutRoot = findViewById(R.id.layoutRoot);
        observeViewModel();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, PlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send media with BroadcastReceiver
        }
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
        ShareImpressionPopup popup = new ShareImpressionPopup(layoutRoot, shareImpressionPopupCallbacks, true);
        popup.show();
    }

    public void showSelectPlanDialog(long price) {
        SelectPlanPopup popup = new SelectPlanPopup(layoutRoot, selectPlanPopupCallbacks, price);
        popup.show();
    }


    @Override
    public void onBackPressed() {
        if (!BasePopup.closeAllPopups(layoutRoot)) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null && resultCode == RESULT_OK) {
            if (requestCode == SubscriptionActivity.SUBSCRIPTION_REQUEST_CODE) {
                String url = data.getStringExtra(SubscriptionActivity.RESULT_URL);
                if (url != null){
                    WebLinkHelper.openWebLink(this, url);
                }
                Log.v("onActivityResult", "Subscription opened");
            }
        }
    }


    private void observeViewModel(){
        viewModel.getGoToDonateAction().observe(this, guideId -> {
            if(guideId != null){
                DonateActivity.Args args = new DonateActivity.Args(guideId);
                NavigationHelper.startDonateActivity(AudioActivity.this, args);
            }
        });

    }

    @Override
    public void onPlayerPlayClicked(int tourId) {
        Log.v(App.TAG, "onPlayerPlayClicked");
        if (serviceBound) {
            if (playerService.getPlayer() != null && playerService.getPlayer().getTourId() != tourId) {
                Log.v(App.TAG, "onPlayerPlayClicked getTourId() != tourId");
                playerService.releasePlayer();
                playerService.getPlayer().pause();
//                playerService.background();
            }
        }
    }

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder) service;
            playerService = binder.getService();
            playerService.background();
            if (playerService.isPlaying()) {
                viewModel.setPlayer(playerService.getPlayer());
            }
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (serviceBound) {
            if (viewModel.getPlayer().isPlaying()) {
                playerService.initPlayer(viewModel.getPlayer());
            }

            unbindService(serviceConnection);
            serviceBound = false;
            //service is active
            if (!playerService.isPlaying()){
                playerService.stopSelf();
            }
        }
    }

}
