package app.karacal.network.models.request;

public class CreateCommentRequest {
    private int tourId;
    private String text;

    public CreateCommentRequest(int tourId, String text) {
        this.tourId = tourId;
        this.text = text;
    }
}
