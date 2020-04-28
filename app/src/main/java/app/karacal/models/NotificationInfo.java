package app.karacal.models;

import java.util.Date;

public class NotificationInfo {

    private final int image;
    private final String title;
    private final Date date;

    public NotificationInfo(int image, String title, Date date) {
        this.image = image;
        this.title = title;
        this.date = date;
    }

    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }
}
