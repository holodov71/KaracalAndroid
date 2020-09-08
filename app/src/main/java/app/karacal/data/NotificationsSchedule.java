package app.karacal.data;

import android.content.Context;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.models.NotificationScheduleModel;

public class NotificationsSchedule {

    private boolean hasLocation;
    private List<NotificationScheduleModel> notificationsList;

    private NotificationsSchedule(List<NotificationScheduleModel> notificationsList, boolean hasLocation) {
        this.notificationsList = notificationsList;
    }

    public static NotificationsSchedule getInstance(String toursCache){
        NotificationsSchedule schedule = new Gson().fromJson(toursCache, NotificationsSchedule.class);
        return new NotificationsSchedule(schedule.getNotificationsList(), schedule.hasLocation);
    }

    public static NotificationsSchedule getInstance(Context context){
        return getInstance(PreferenceHelper.getNotificationsSchedule(context));
    }

    public static NotificationsSchedule getEmptyInstance(){
        return new NotificationsSchedule(new ArrayList<>(), false);
    }

    public String retrieveStringFormat(){
        return new Gson().toJson(this);
    }

    public void addNotification(Context context, NotificationScheduleModel notification){
        notificationsList.add(notification);
        saveChanges(context);
    }

    public void setNotificationsList(Context context, List<NotificationScheduleModel> list){
        notificationsList.clear();
        notificationsList.addAll(list);
        saveChanges(context);
    }

    public void setHasLocation(Context context, boolean hasLocation){
        this.hasLocation = hasLocation;
        saveChanges(context);
    }

    public boolean isHasLocation() {
        return hasLocation;
    }

    public NotificationScheduleModel getNotification(int position){
        return notificationsList.get(position);
    }

    public void deleteNotification(Context context, int position){
        notificationsList.remove(position);
        saveChanges(context);
    }

    private void saveChanges(Context context){
        PreferenceHelper.setNotificationsSchedule(context, new Gson().toJson(this));
    }

    public void clear(Context context){
        PreferenceHelper.setNotificationsSchedule(context, getEmptyInstance().retrieveStringFormat());
    }

    public List<NotificationScheduleModel> getNotificationsList() {
        return notificationsList;
    }
}
