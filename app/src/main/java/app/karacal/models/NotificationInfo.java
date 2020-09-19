package app.karacal.models;

import java.util.Date;

public class NotificationInfo {

    private final int id;
    private final String title;
    private final String message;
    private final Date date;
    private final int tourId;

    public NotificationInfo(int id, String title, String message, Date date, int tourId) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.date = date;
        this.tourId = tourId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

    public int getTourId() {
        return tourId;
    }

    @Override
    public String toString() {
        return "NotificationInfo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", date=" + date +
                ", tourId=" + tourId +
                '}';
    }
}
