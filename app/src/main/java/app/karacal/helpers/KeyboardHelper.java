package app.karacal.helpers;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;

public class KeyboardHelper {
    public static void hideKeyboard(@Nullable Context context, View view){
        if (context != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            IBinder windowToken = view.getRootView().getWindowToken();
            if (imm != null) {
                imm.hideSoftInputFromWindow(windowToken, 0);
            }
        }
    }
}
