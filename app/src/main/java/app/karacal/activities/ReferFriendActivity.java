package app.karacal.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import app.karacal.App;
import app.karacal.R;
import app.karacal.data.ProfileCache;
import app.karacal.helpers.ShareHelper;
import app.karacal.views.ReferralCodeEditText;
import apps.in.android_logger.LogActivity;

public class ReferFriendActivity extends LogActivity {

    private String referralCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        initReferralCode();
        setContentView(R.layout.activity_refer_friend);
        setupBackButton();
        setupReferralCode();
        setupShareButton();
        setupCopyToClipboardBoardButton();
    }

    private void initReferralCode() {
        String referralCode = ProfileCache.getInstance(this).getProfile().getReferralCode();
        this.referralCode = referralCode != null ? referralCode : "AH45GQ";
    }

    private void setupBackButton(){
        ImageView buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupReferralCode(){
        ReferralCodeEditText referralCodeEditText = findViewById(R.id.referralCodeView);
        referralCodeEditText.setReferralCode(referralCode);
    }

    private void setupShareButton(){
        Button buttonShare = findViewById(R.id.buttonShare);
        buttonShare.setOnClickListener(v -> ShareHelper.share(this, getString(R.string.share_referral_code), getString(R.string.refer_a_friend), referralCode));
    }

    private void setupCopyToClipboardBoardButton(){
        TextView buttonCopy = findViewById(R.id.textViewCopyToClipboard);
        buttonCopy.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("Referral code", referralCode);
            if (clipboardManager != null) {
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(this, R.string.referral_code_copied_to_clipboard, Toast.LENGTH_LONG).show();
            }
        });
    }
}
