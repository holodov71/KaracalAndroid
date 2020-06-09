package app.karacal.retrofit.models.request;

public class NearToursRequest {

    private final String latitude;
    private final String longitude;
    private final String radius;

    public NearToursRequest(double latitude, double longitude, double radius) {
        this.latitude = String.valueOf(latitude);
        this.longitude = String.valueOf(longitude);
        this.radius = String.valueOf(radius);
    }
}
