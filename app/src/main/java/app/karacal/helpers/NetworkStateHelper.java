package app.karacal.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

public class NetworkStateHelper {

    public static boolean isNetworkAvailable(@Nullable Context context, DesiredInternet desiredInternet) {
        if(context == null)  return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            DesiredInternet availableInternet = null;

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        availableInternet = DesiredInternet.ANY_INTERNET;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        availableInternet = DesiredInternet.WIFI_INTERNET;
                    }  else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                        availableInternet = DesiredInternet.ANY_INTERNET;
                    }
                }
            } else {

                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                            availableInternet = DesiredInternet.WIFI_INTERNET;
                        } else {
                            availableInternet = DesiredInternet.ANY_INTERNET;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            Log.v("isNetworkAvailable","availableInternet = "+availableInternet);


            if (desiredInternet == DesiredInternet.WIFI_INTERNET){
                return availableInternet == DesiredInternet.WIFI_INTERNET;
            } else {
                return availableInternet != null;
            }
        }
        Log.v("isNetworkAvailable","Network is available : FALSE ");
        return false;
    }

    public enum DesiredInternet{
        ANY_INTERNET,
        WIFI_INTERNET
    }

}


