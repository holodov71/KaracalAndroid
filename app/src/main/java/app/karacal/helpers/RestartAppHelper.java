package app.karacal.helpers;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.Nullable;

public class RestartAppHelper {
    public static void restartApp(@Nullable Activity activity){
        if(activity != null && activity.getBaseContext() != null){
            Intent i = activity.getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage( activity.getBaseContext().getPackageName() );
            if (i != null) {
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(i);
            }
        }
    }
}
