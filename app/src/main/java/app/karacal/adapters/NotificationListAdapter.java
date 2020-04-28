package app.karacal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import app.karacal.R;
import app.karacal.helpers.DummyHelper;
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

        public void bind(NotificationInfo notificationInfo){
            itemView.setOnClickListener(v -> DummyHelper.dummyAction(v.getContext()));
            imageViewTitle.setImageResource(notificationInfo.getImage());
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

    private ArrayList<NotificationInfo> notifications;

    public NotificationListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        notifications = new ArrayList<>();
        int[] images = new int[] {
                R.mipmap.notification_item_example_1,
                R.mipmap.notification_item_example_2,
                R.mipmap.notification_item_example_3,
                R.mipmap.notification_item_example_4
        };
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            notifications.add(new NotificationInfo(images[random.nextInt(images.length)], "Notification title", new Date(new Date().getTime() - random.nextInt(1000 * 60 * 60 * 24 * 2))));
        }
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
        holder.bind(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}
