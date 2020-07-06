package app.karacal.network.models.request;

public class PaymentRequest {

    private long amount;
    private String currency;
    private String source;
    private String description;
    private int tourId;

    public PaymentRequest(long amount, String currency, String source, String description, int tourId) {
        this.amount = amount;
        this.currency = currency;
        this.source = source;
        this.description = description;
        this.tourId = tourId;
    }
}
