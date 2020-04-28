package app.karacal.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.functions.Action;

@Singleton
public class PermissionHelper {

    private static class Callbacks {
        private final Runnable granted;
        private final Runnable denied;

        public Callbacks(Runnable granted, Runnable denied) {
            this.granted = granted;
            this.denied = denied;
        }
    }

    private final HashMap<Integer, Callbacks> callbacksHashMap;
    private final Random random;

    @Inject
    public PermissionHelper() {
        callbacksHashMap = new HashMap<>();
        random = new Random();
    }

    public void checkPermission(Activity activity, String permission, Runnable grantedAction, Runnable deniedAction) {
        if (check(activity, new String[]{permission})) {
            grantedAction.run();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, registerCallbacks(grantedAction, deniedAction));
        }
    }

    public void checkPermission(Activity activity, String[] permission, Runnable grantedAction, Runnable deniedAction) {
        if (check(activity, permission)) {
            grantedAction.run();
        } else {
            ActivityCompat.requestPermissions(activity, permission, registerCallbacks(grantedAction, deniedAction));
        }
    }

    public void handlePermissionRequestResult(int requestCode, int[] grantResults) {
        if (callbacksHashMap.containsKey(requestCode)) {
            Callbacks callbacks = callbacksHashMap.get(requestCode);
            removeCallbacks(requestCode);
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    callbacks.denied.run();
                    return;
                }
            }
            callbacks.granted.run();
        }
    }

    private boolean check(Activity activity, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private synchronized int registerCallbacks(Runnable granted, Runnable denied) {
        int requestCode;
        do {
            requestCode = random.nextInt(10000);
        } while (callbacksHashMap.containsKey(requestCode));
        callbacksHashMap.put(requestCode, new Callbacks(granted, denied));
        return requestCode;
    }

    private synchronized void removeCallbacks(int requestCode) {
        callbacksHashMap.remove(requestCode);
    }


}
