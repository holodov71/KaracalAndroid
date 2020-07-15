package app.karacal.models;

import java.util.Date;

public class NotificationInfo {

    private final int id;
    private final String title;
    private final String message;
    private final Date date;

    public NotificationInfo(int id, String title, String message, Date date) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.date = date;
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

    @Override
    public String toString() {
        return "NotificationInfo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", date=" + date +
                '}';
    }
}
