package app.karacal.network.models.response;

public class TourDetailsResponse extends BaseResponse {
    private TourResponse tour;
    private boolean isPaid;

    public TourResponse getTour() {
        return tour;
    }

    public boolean isPaid() {
        return isPaid;
    }
}
