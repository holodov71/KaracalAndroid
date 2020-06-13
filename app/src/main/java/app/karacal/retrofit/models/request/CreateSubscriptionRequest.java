package app.karacal.retrofit.models.request;

public class CreateSubscriptionRequest {
    private String idCustomer;
    private String price;

    public CreateSubscriptionRequest(String idCustomer, String price) {
        this.idCustomer = idCustomer;
        this.price = price;
    }
}
