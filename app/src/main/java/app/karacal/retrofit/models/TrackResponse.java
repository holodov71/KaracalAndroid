package app.karacal.retrofit.models;

public class TrackResponse {
    private int id;
    private int guideId;
    private int tourId;
    private String filename;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getTitle(){
        return "Fake title";
    }
}
