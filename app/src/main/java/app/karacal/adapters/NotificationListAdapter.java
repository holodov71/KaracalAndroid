package app.karacal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import app.karacal.R;
import app.karacal.data.NotificationsCache;
import app.karacal.models.NotificationInfo;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {

        private final View itemView;
        private final ImageView imageViewTitle;
        private final TextView textViewTitle;
        private final TextView textViewTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageViewTitle = itemView.findViewById(R.id.imageViewTitle);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewTime = itemView.findViewById(R.id.textViewTime);
        }

        public void bind(NotificationInfo notificationInfo, NotificationClickCallback clickCallback){
            itemView.setOnClickListener(v -> clickCallback.onNotificationClick(notificationInfo.getTourId()));
            imageViewTitle.setImageResource(R.drawable.karacal_logo);
            textViewTitle.setText(notificationInfo.getTitle());
            textViewTime.setText(getTimeText(notificationInfo.getDate()));
        }

        private String getTimeText(Date date){
            Date now = new Date();
            long timeDelta = ((now.getTime() - date.getTime()) / 1000) / 60;
            if (timeDelta < 60){
                return context.getString(R.string.notification_time_format, timeDelta < 2 ? 1 : timeDelta, context.getString(R.string.min));
            }
            timeDelta = timeDelta / 60;
            if (timeDelta < 24){
                return context.getString(R.string.notification_time_format, timeDelta, context.getString(timeDelta < 2 ? R.string.hour : R.string.hours));
            }
            timeDelta = timeDelta / 24;
            return context.getString(R.string.notification_time_format, timeDelta, context.getString(timeDelta < 2 ? R.string.day : R.string.days));
        }

    }

    private final Context context;
    private final LayoutInflater inflater;

    private List<NotificationInfo> notifications;
    private NotificationClickCallback clickCallback;

    public NotificationListAdapter(Context context, NotificationClickCallback clickCallback) {
        this.context = context;
        this.clickCallback = clickCallback;
        inflater = LayoutInflater.from(context);
        notifications = NotificationsCache.getInstance(context).getNotificationsList();
        Collections.sort(notifications, (d1, d2) -> d2.getDate().compareTo(d1.getDate()));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_notification_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(notifications.get(position), clickCallback);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public interface NotificationClickCallback{
        void onNotificationClick(int tourId);
    }
}
