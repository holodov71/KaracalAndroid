package app.karacal.network.models.response;

public class CreateSubscriptionResponse extends BaseResponse {

    private String subscription;
    private String id;

    public String getSubscription() {
        return subscription;
    }

    public String getSubscriptionId() {
        return id;
    }
}
