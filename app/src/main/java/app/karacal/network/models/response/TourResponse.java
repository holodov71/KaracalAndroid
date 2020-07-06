package app.karacal.network.models.response;

public class TourResponse {
    private int id;
    private int clientId;
    private String placeName;
    private String placeCity;
    private String placeCoordinates; //""48.858222, 2.2945",
    private double latitude; // уточнити тип
    private double longitude; // уточнити тип
    private String image;
    private String title;
    private String description;
    private String address;
    private String review; // уточнити тип
    private String duration;
    private int stars;
    private String price;
    private String time;
    private String category;
    private int placeId;
    private int guideId;
    private int status;

    public int getId() {
        return id;
    }

    public int getClientId() {
        return clientId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getPlaceCity() {
        return placeCity;
    }

    public String getPlaceCoordinates() {
        return placeCoordinates;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public String getReview() {
        return review;
    }

    public String getDuration() {
        return duration;
    }

    public int getStars() {
        return stars;
    }

    public String getPrice() {
        return price;
    }

    public String getTime() {
        return time;
    }

    public String getCategory() {
        return category;
    }

    public int getPlaceId() {
        return placeId;
    }

    public int getGuideId() {
        return guideId;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "TourResponse{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", placeName='" + placeName + '\'' +
                ", placeCity='" + placeCity + '\'' +
                ", placeCoordinates='" + placeCoordinates + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", image='" + image + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", review='" + review + '\'' +
                ", duration='" + duration + '\'' +
                ", stars=" + stars +
                ", price='" + price + '\'' +
                ", time='" + time + '\'' +
                ", category='" + category + '\'' +
                ", placeId=" + placeId +
                ", guideId=" + guideId +
                ", status=" + status +
                '}';
    }
}
