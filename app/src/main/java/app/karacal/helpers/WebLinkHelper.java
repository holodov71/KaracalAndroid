package app.karacal.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsIntent;

public class WebLinkHelper {

    private static final String HTTP = "http";
    private static final String HTTP1 = "http://";
    private static final String HOW_WE_CHOOSE_URL = "https://www.karacal.fr/Comment-nous-choisissons-nos-guides--.html";

    public static void howWeChooseOurGuide(Activity activity){
        openUrl(activity, HOW_WE_CHOOSE_URL);
    }

    private static void openUrl(Activity activity, String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        activity.startActivity(intent);
    }

    public static void openWebLink(Context context, String link){
        Uri uri = Uri.parse(link.contains(HTTP) ? link : HTTP1 + link);
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .addDefaultShareMenuItem()
                .setShowTitle(true)
                .setInstantAppsEnabled(true)
                .build();
        customTabsIntent.launchUrl(context, uri);
    }

}
