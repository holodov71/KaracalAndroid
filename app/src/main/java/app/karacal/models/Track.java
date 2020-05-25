package app.karacal.models;

import app.karacal.retrofit.models.TrackResponse;

public class Track {
    private int id;
    private int guideId;
    private int tourId;
    private String filename;
    private String createdAt;
    private String updatedAt;
    private String title;
    private int resId;
    private int duration = 0;

    public Track(String title, int resId) {
        this.title = title;
        this.resId = resId;
    }

    public Track(TrackResponse trackResponse){
        this.resId = -1;
        this.title = trackResponse.getTitle();
        this.filename = trackResponse.getFilename();
        this.guideId = trackResponse.getGuideId();
        this.tourId = trackResponse.getTourId();
        this.createdAt = trackResponse.getCreatedAt();
        this.updatedAt = trackResponse.getUpdatedAt();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResId() {
        return resId;
    }

    public int getId() {
        return id;
    }

    public int getGuideId() {
        return guideId;
    }

    public int getTourId() {
        return tourId;
    }

    public String getFilename() {
        return filename;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Track{" +
                "id=" + id +
                ", guideId=" + guideId +
                ", tourId=" + tourId +
                ", filename='" + filename + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", title='" + title + '\'' +
                ", resId=" + resId +
                '}';
    }
}
