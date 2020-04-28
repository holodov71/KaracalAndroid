package app.karacal.helpers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class WebLinkHelper {

    private static final String HOW_WE_CHOOSE_URL = "https://www.karacal.fr/Comment-nous-choisissons-nos-guides--.html";

    public static void howWeChooseOurGuide(Activity activity){
        openUrl(activity, HOW_WE_CHOOSE_URL);
    }

    private static void openUrl(Activity activity, String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        activity.startActivity(intent);
    }

}
