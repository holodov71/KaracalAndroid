package app.karacal.helpers;

import android.app.Activity;
import android.content.Intent;

import app.karacal.models.Tour;

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

    public static void share(Activity context, String chooserTitle, Tour tour){
        String result = tour.getTitle();
        result += "\n";
        result += tour.getDescription();
        result += "\n";
        result += "https://www.karacal.fr/view/tour/";
        result += tour.getId();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, tour.getTitle());
        intent.putExtra(Intent.EXTRA_TEXT, result);
        Intent chooserIntent = Intent.createChooser(intent, chooserTitle);
        if (chooserIntent != null) {
            context.startActivityForResult(chooserIntent, REQUEST_CODE);
        }
    }

}
