package app.karacal.network.models.request;

public class CreateSubscriptionRequest {
    private String idCustomer;
    private String price;

    public CreateSubscriptionRequest(String idCustomer, String price) {
        this.idCustomer = idCustomer;
        this.price = price;
    }
}
