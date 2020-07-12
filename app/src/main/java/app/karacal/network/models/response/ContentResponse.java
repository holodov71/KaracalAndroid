package app.karacal.network.models.response;

import java.util.Date;
import java.util.List;

import app.karacal.models.Tag;

public class ContentResponse {
    private int id;
    private String title;
    private String author;
    private int guideId;
    private String date;
    private List<Tag> arrTags;
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
    private Date createdAt;
    private Date updatedAt;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getGuideId() {
        return guideId;
    }

    public String getDate() {
        return date;
    }

    public List<Tag> getTags() {
        return arrTags;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
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
                ", guideId='" + guideId + '\'' +
                ", date='" + date + '\'' +
                ", tags='" + arrTags + '\'' +
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
