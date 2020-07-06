package app.karacal.helpers;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class ToastHelper {

    public static void showToast(@Nullable Context context, String message){
        if (context !=null) Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
