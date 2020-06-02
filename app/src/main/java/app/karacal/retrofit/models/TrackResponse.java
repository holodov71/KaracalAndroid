package app.karacal.retrofit.models;

public class TrackResponse {
    private int id;
    private int guideId;
    private int tourId;
    private String filename;
    private int duration;
    private String createdAt;
    private String updatedAt;

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

    public int getDuration() {
        return duration;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getTitle(){
//        http://karacal.store:8080/admin/uploads/mp3/LesarnesdeLutce-1590661203.mp3
        int index = filename.lastIndexOf("/");
        if (index == -1) return filename;
        return filename.substring(index+1);
    }
}
