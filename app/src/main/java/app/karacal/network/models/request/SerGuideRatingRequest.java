package app.karacal.network.models.request;

public class SerGuideRatingRequest {
    private final int guideId;
    private final String rating;

    public SerGuideRatingRequest(int guideId, String rating) {
        this.guideId = guideId;
        this.rating = rating;
    }

    public int getGuideId() {
        return guideId;
    }

    public String getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return "SerGuideRatingRequest{" +
                "guideId=" + guideId +
                ", rating='" + rating + '\'' +
                '}';
    }
}
