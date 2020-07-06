package app.karacal.network.models.response;

import java.util.List;

public class SubscriptionsListResponse extends BaseResponse{
    public static final String STATUS_SUBSCRIPTION_ACTIVE = "active";

    private List<SubscriptionWrapper> subscriptions;

    public List<SubscriptionWrapper> getSubscriptions() {
        return subscriptions;
    }

    @Override
    public String toString() {
        return "SubscriptionsListResponse{" +
                "subscriptions=" + subscriptions +
                '}';
    }
}