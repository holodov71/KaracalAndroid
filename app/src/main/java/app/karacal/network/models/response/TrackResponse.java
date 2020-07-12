package app.karacal.network.models.response;

public class TrackResponse {
    private int id;
    private int guideId;
    private int tourId;
    private String trackTitle;
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
        if (trackTitle != null){
            return trackTitle;
        } else {
            int index = filename.lastIndexOf("/");
            if (index == -1) return filename;
            return filename.substring(index + 1).replace(".mp3", "");
        }
    }
}
