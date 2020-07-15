package app.karacal.helpers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import app.karacal.App;
import app.karacal.R;

public class EmailHelper {

    private static final String EMAIL_CONTACT = "hello@karacal.fr";
    private static final String EMAIL_HELP = "help@karacal.fr";
    private static final String EMAIL_SUPPORT = "support@karacal.fr";

    public static void contactKaracal(Activity context) {
        sendEmail(context, EMAIL_CONTACT);
    }

    public static void supportKaracal(Activity context) {
        sendEmail(context, EMAIL_SUPPORT);
    }

    public static void reportProblem(Activity activity, String subject){
        sendEmail(activity, EMAIL_HELP, subject);
    }

    private static void sendEmail(Activity context, String mailTo) {
        sendEmail(context, mailTo, null);
    }

    private static void sendEmail(Activity context, String mailTo, String subject) {
        sendEmail(context, mailTo, subject, null);
    }

    private static void sendEmail(Activity context, String mailTo, String subject, String text) {
        Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
        selectorIntent.setData(Uri.parse("mailto:"));

        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mailTo});
        if (subject != null) {
            Log.v(App.TAG, "subject = "+subject);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        if (text != null) {
            emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        }
        emailIntent.setSelector( selectorIntent );

        context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.choose_e_mail_app)));
    }

}
