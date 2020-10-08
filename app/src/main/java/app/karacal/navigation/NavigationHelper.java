package app.karacal.navigation;

import android.app.Activity;
import android.content.Intent;

import app.karacal.activities.AddEditTourActivity;
import app.karacal.activities.AudioActivity;
import app.karacal.activities.AudioRecorderActivity;
import app.karacal.activities.CategoryActivity;
import app.karacal.activities.ChangePasswordActivity;
import app.karacal.activities.CommentsActivity;
import app.karacal.activities.CongratulationsActivity;
import app.karacal.activities.GuideDashboardActivity;
import app.karacal.activities.DonateActivity;
import app.karacal.activities.EditAudioActivity;
import app.karacal.activities.FollowMyListeningsActivity;
import app.karacal.activities.LoginActivity;
import app.karacal.activities.MainActivity;
import app.karacal.activities.PasswordHasBeenResetActivity;
import app.karacal.activities.PaymentMethodActivity;
import app.karacal.activities.PolitiqueProtectionActivity;
import app.karacal.activities.PrivacyPolicyActivity;
import app.karacal.activities.ProfileActivity;
import app.karacal.activities.ReferFriendActivity;
import app.karacal.activities.RegistrationActivity;
import app.karacal.activities.SearchFilterActivity;
import app.karacal.activities.SettingsActivity;
import app.karacal.activities.SubscriptionActivity;
import app.karacal.service.PaymentsUpdateService;

public class NavigationHelper {

    public static void startDonateActivity(Activity context, DonateActivity.Args args) {
        Intent intent = new Intent(context, DonateActivity.class);
        intent.putExtras(args.toBundle());
        context.startActivity(intent);
    }

    public static void startCommentsActivity(Activity context, CommentsActivity.Args args) {
        Intent intent = new Intent(context, CommentsActivity.class);
        intent.putExtras(args.toBundle());
        context.startActivity(intent);
    }

    public static void startProfileActivity(Activity context, ProfileActivity.Args args) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtras(args.toBundle());
        context.startActivity(intent);
    }

    public static void startMainActivity(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);

        PaymentsUpdateService.startTimer();
    }

    public static void startRegistrationActivity(Activity context) {
        Intent intent = new Intent(context, RegistrationActivity.class);
        context.startActivity(intent);
    }

    public static void startLoginActivity(Activity context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void startPrivacyPolicyActivity(Activity context) {
        Intent intent = new Intent(context, PrivacyPolicyActivity.class);
        context.startActivity(intent);
    }

    public static void startPolitiqueProtectionActivity(Activity context) {
        Intent intent = new Intent(context, PolitiqueProtectionActivity.class);
        context.startActivity(intent);
    }

    public static void startAudioActivity(Activity context, AudioActivity.Args args) {
        Intent intent = new Intent(context, AudioActivity.class);
        intent.putExtras(args.toBundle());
        context.startActivity(intent);
    }

    public static void startCategoryActivity(Activity context, CategoryActivity.Args args) {
        Intent intent = new Intent(context, CategoryActivity.class);
        intent.putExtras(args.toBundle());
        context.startActivity(intent);
    }

    public static void startSearchFilterActivity(Activity context) {
        Intent intent = new Intent(context, SearchFilterActivity.class);
        context.startActivityForResult(intent, SearchFilterActivity.REQUEST_CODE);
    }

    public static void startSettingsActivity(Activity context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public static void startReferFriendActivity(Activity context) {
        Intent intent = new Intent(context, ReferFriendActivity.class);
        context.startActivity(intent);
    }

    public static void startDashboardActivity(Activity context) {
        Intent intent = new Intent(context, GuideDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void startFollowMyListeningsActivity(Activity context) {
        Intent intent = new Intent(context, FollowMyListeningsActivity.class);
        context.startActivity(intent);
    }

    public static void startEditGuideActivity(Activity context, AddEditTourActivity.Args args) {
        Intent intent = new Intent(context, AddEditTourActivity.class);
        intent.putExtras(args.toBundle());
        context.startActivity(intent);
    }

    public static void startEditAudioActivity(Activity context, EditAudioActivity.Args args) {
        Intent intent = new Intent(context, EditAudioActivity.class);
        intent.putExtras(args.toBundle());
        context.startActivity(intent);
    }

    public static void startPasswordHasBeenResetActivity(Activity context) {
        Intent intent = new Intent(context, PasswordHasBeenResetActivity.class);
        context.startActivity(intent);
    }

    public static void startChangePasswordActivity(Activity context) {
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        context.startActivity(intent);
    }

    public static void startPaymentMethodsActivity(Activity context) {
        Intent intent = new Intent(context, PaymentMethodActivity.class);
        context.startActivity(intent);
    }

    public static void startSubscriptionActivity(Activity context) {
        Intent intent = new Intent(context, SubscriptionActivity.class);
        context.startActivityForResult(intent, SubscriptionActivity.SUBSCRIPTION_REQUEST_CODE);
    }

    public static void startAudioRecorderActivity(Activity context) {
        Intent intent = new Intent(context, AudioRecorderActivity.class);
        context.startActivityForResult(intent, AudioRecorderActivity.REQUEST_CODE);
    }

    public static void startCongratulationsActivity(Activity context) {
        Intent intent = new Intent(context, CongratulationsActivity.class);
        context.startActivity(intent);
    }

}
