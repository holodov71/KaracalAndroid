package app.karacal.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.stripe.android.ApiResultCallback;
import com.stripe.android.CustomerSession;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.Token;
import com.stripe.android.view.AddPaymentMethodActivityStarter;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.DummyHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.helpers.ToastHelper;
import app.karacal.navigation.ActivityArgs;
import app.karacal.navigation.NavigationHelper;
import app.karacal.popups.BasePopup;
import app.karacal.popups.ReportProblemPopup;
import app.karacal.popups.SelectActionPopup;
import app.karacal.popups.SelectPlanPopup;
import app.karacal.popups.ShareImpressionPopup;
import app.karacal.retrofit.models.request.PaymentRequest;
import app.karacal.viewmodels.AudioActivityViewModel;
import apps.in.android_logger.LogActivity;
import io.reactivex.disposables.Disposable;

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

    private static final String HTTP = "http";
    private static final String HTTP1 = "http://";

    private AudioActivityViewModel viewModel;

    private ConstraintLayout layoutRoot;

    private Disposable disposable;

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
            onBackPressed();
            PaymentActivity.Args args = new PaymentActivity.Args(viewModel.getTour().getId(), viewModel.getTour().getPrice(), false);
            NavigationHelper.startPaymentActivity(AudioActivity.this, args);
        }

        @Override
        public void onButtonRegularPriceClick(BasePopup popup) {
            onBackPressed();
            PaymentActivity.Args args = new PaymentActivity.Args(viewModel.getTour().getId(), viewModel.getTour().getPrice(), true);
            NavigationHelper.startPaymentActivity(AudioActivity.this, args);
        }
    };

//    private Stripe stripe;
//    private String paymentIntentClientSecret;

    @Inject
    ApiHelper apiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        setContentView(R.layout.activity_audio);
        Args args = ActivityArgs.fromBundle(Args.class, getIntent().getExtras());
        int tourId = args.getTourId();
        viewModel = new ViewModelProvider(this, new AudioActivityViewModel.AudioActivityViewModelFactory(tourId)).get(AudioActivityViewModel.class);
        layoutRoot = findViewById(R.id.layoutRoot);

//        CustomerSession.initCustomerSession(this, apiHelper);
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
            if (requestCode == PaymentActivity.REQUEST_CODE) {
                String url = data.getStringExtra(PaymentActivity.RESULT_URL);
                if (url != null){
                    openWebLink(url);
                }
                Log.v("onActivityResult", "Payment completed");
            }
        }
    }

    private void openWebLink(String link){
        Uri uri = Uri.parse(link.contains(HTTP) ? link : HTTP1 + link);
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                        .addDefaultShareMenuItem()
                        .setShowTitle(true)
                        .setInstantAppsEnabled(true)
                        .build();
        customTabsIntent.launchUrl(this, uri);
    }
}
