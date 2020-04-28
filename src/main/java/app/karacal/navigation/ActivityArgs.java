package app.karacal.navigation;

import android.os.Bundle;

import java.io.Serializable;

public abstract class ActivityArgs implements Serializable {

    public Bundle toBundle(){
        Bundle bundle = new Bundle();
        bundle.putSerializable(this.getClass().getName(), this);
        return bundle;
    }

    public static <T> T fromBundle(Class clazz, Bundle bundle){
        return  (T) bundle.getSerializable(clazz.getName());
    }

}
