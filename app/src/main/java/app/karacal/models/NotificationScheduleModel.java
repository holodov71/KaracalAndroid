package app.karacal.models;

public class NotificationScheduleModel {
    private final int day;
    private final int tourId;
    private final String title;
    private final String message;
    private final String logo;

    public NotificationScheduleModel(int day, int tourId, String title, String message, String logo) {
        this.day = day;
        this.tourId = tourId;
        this.title = title;
        this.message = message;
        this.logo = logo;
    }

    public NotificationScheduleModel(int day, Tour tour){
        this( day, tour.getId(), tour.getTitle(), tour.getDescription(), tour.getImageUrl());
    }

    public int getDay() {
        return day;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public int getTourId() {
        return tourId;
    }

    public String getLogo() {
        return logo;
    }

    @Override
    public String toString() {
        return "NotificationScheduleModel{" +
                "day=" + day +
                ", tourId=" + tourId +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", logo='" + logo + '\'' +
                '}';
    }
}
