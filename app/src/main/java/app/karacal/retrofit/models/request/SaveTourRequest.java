package app.karacal.retrofit.models.request;

public class SaveTourRequest {

    private final String placeName;
    private final String placeCity;
    private final String placeCoordinates;
    private final String latitude;
    private final String longitude;
    private final String title;
    private final String description;
    private final String address;
    private final String price;
    private final String guideId;

    public SaveTourRequest(String placeName, String placeCity, String latitude, String longitude, String title, String description, String address, String price, String guideId) {
        this.placeName = placeName;
        this.placeCity = placeCity;
        this.placeCoordinates = latitude + ", " + longitude;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.description = description;
        this.address = address;
        this.price = price;
        this.guideId = guideId;
    }

    @Override
    public String toString() {
        return "SaveTourRequest{" +
                "placeName='" + placeName + '\'' +
                ", placeCity='" + placeCity + '\'' +
                ", placeCoordinates='" + placeCoordinates + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", price='" + price + '\'' +
                ", guideId='" + guideId + '\'' +
                '}';
    }
}
