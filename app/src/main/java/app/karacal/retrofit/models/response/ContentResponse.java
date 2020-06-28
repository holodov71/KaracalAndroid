package app.karacal.retrofit.models.response;

import java.util.List;

public class ContentResponse {
    private int id;
    private String title;
    private String author;
    private String date;
    private String tags;
    private String address;
    private String city;
    private String desc;
    private String img;
    private String latitude;
    private String longitude;
    private List<TrackResponse> audio;
    private int rating;
    private String type;
    private int status;
    private String createdAt;
    private String updatedAt;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getTags() {
        return tags;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getDesc() {
        return desc;
    }

    public String getImg() {
        return img;
    }

    public List<TrackResponse> getAudio() {
        return audio;
    }

    public int getRating() {
        return rating;
    }

    public String getType() {
        return type;
    }

    public int getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public double parseLatitude(){
        return parseCoordinate(latitude);
    }

    public double parseLongitude(){
        return parseCoordinate(longitude);
    }

    private double parseCoordinate(String coordinate){
        double result = 0.0;
        try{
            result = Double.parseDouble(coordinate);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

    public int parseDuration(){
        int result = 0;
        if (audio != null && !audio.isEmpty()){
            for(TrackResponse trackResponse: audio){
                result += trackResponse.getDuration();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "ContentResponse{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", date='" + date + '\'' +
                ", tags='" + tags + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", desc='" + desc + '\'' +
                ", img='" + img + '\'' +
                ", audio=" + audio +
                ", rating='" + rating + '\'' +
                ", type='" + type + '\'' +
                ", status=" + status +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
