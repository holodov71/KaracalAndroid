package app.karacal.helpers;

import android.app.Activity;
import android.content.Intent;

public class ShareHelper {

    private static final int REQUEST_CODE = 2701;

    public static void share(Activity context, String chooserTitle, String subject, String text){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        Intent chooserIntent = Intent.createChooser(intent, chooserTitle);
        if (chooserIntent != null) {
            context.startActivityForResult(chooserIntent, REQUEST_CODE);
        }
    }

}
