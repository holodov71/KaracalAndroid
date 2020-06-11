package app.karacal.retrofit.models.request;

public class PaymentRequest {

    private long amount;
    private String currency;
    private String source;
    private String description;

    public PaymentRequest(long amount, String currency, String source, String description) {
        this.amount = amount;
        this.currency = currency;
        this.source = source;
        this.description = description;
    }
}
