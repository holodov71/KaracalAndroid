package app.karacal.data;

import android.content.Context;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import app.karacal.helpers.PreferenceHelper;
import app.karacal.models.NotificationInfo;

public class NotificationsCache {
    private List<NotificationInfo> notificationsList;

    private NotificationsCache(List<NotificationInfo> notificationsList) {
        this.notificationsList = notificationsList;
    }

    private NotificationsCache(String toursCache) {
        this(new Gson().fromJson(toursCache, NotificationsCache.class).getNotificationsList());
    }

    public static NotificationsCache getInstance(Context context){
        return new NotificationsCache(PreferenceHelper.getNotificationsCache(context));
    }

    public static NotificationsCache getEmptyInstance(){
        return new NotificationsCache(new ArrayList<>());
    }

    public String retrieveStringFormat(){
        return new Gson().toJson(this);
    }

    public void addNotification(Context context, NotificationInfo notification){
        notificationsList.add(notification);
        saveChanges(context);
    }

    private void saveChanges(Context context){
        PreferenceHelper.setNotificationsCache(context, new Gson().toJson(this));
    }

    public void clear(Context context){
        PreferenceHelper.setNotificationsCache(context,
                NotificationsCache.getEmptyInstance().retrieveStringFormat());
    }

    public List<NotificationInfo> getNotificationsList() {
        return notificationsList;
    }
}
