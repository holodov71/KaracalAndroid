package app.karacal.network.models.request;

public class SerTourRatingRequest {

    private final int contentId;
    private final String rating;

    public SerTourRatingRequest(int contentId, String rating) {
        this.contentId = contentId;
        this.rating = rating;
    }

    public int getContentId() {
        return contentId;
    }

    public String getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return "SerTourRatingRequest{" +
                "contentId=" + contentId +
                ", rating='" + rating + '\'' +
                '}';
    }
}
