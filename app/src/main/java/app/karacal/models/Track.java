package app.karacal.models;

import android.util.Log;

import java.io.Serializable;

import app.karacal.retrofit.models.response.TrackResponse;

public class Track implements Serializable {
    private int id;
    private int guideId;
    private int tourId;
    private String createdAt;
    private String updatedAt;
    private String title;
    private String filename;
    private String fileUri;
    private int resId;
    private int duration = 0;

    public Track(String title, int resId) {
        this.title = title;
        this.resId = resId;
    }

    public Track(String title, String fileUri, int duration) {
        this.title = title;
        this.fileUri = fileUri;
        this.duration = duration;
        Log.v("Track", "title = "+title+" fileUri = "+fileUri+" duration = "+duration);
    }

    public Track(TrackResponse trackResponse){
        this.resId = -1;
        this.title = trackResponse.getTitle();
        this.filename = trackResponse.getFilename();
        this.guideId = trackResponse.getGuideId();
        this.tourId = trackResponse.getTourId();
        this.createdAt = trackResponse.getCreatedAt();
        this.updatedAt = trackResponse.getUpdatedAt();
        this.duration = trackResponse.getDuration();
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

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    @Override
    public String toString() {
        return "Track{" +
                "id=" + id +
                ", guideId=" + guideId +
                ", tourId=" + tourId +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", title='" + title + '\'' +
                ", filename='" + filename + '\'' +
                ", fileUri='" + fileUri + '\'' +
                ", resId=" + resId +
                ", duration=" + duration +
                '}';
    }
}
