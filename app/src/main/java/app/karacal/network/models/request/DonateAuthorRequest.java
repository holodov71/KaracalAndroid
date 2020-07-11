package app.karacal.network.models.request;

public class DonateAuthorRequest {

    private int guideId;
    private String source;
    private long amount;

    public DonateAuthorRequest(int guideId, String source, long amount) {
        this.guideId = guideId;
        this.source = source;
        this.amount = amount;
    }
}
