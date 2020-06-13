package app.karacal.retrofit.models.response;

import java.util.List;

public class SubscriptionsListResponse extends BaseResponse{
    public static final String STATUS_SUBSCRIPTION_ACTIVE = "active";

    private List<Subscription> subscriptions;

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public class Subscription{
        private String id;
        private String object;
        private long billingCycleAnchor;
        private long created;
        private String status;
        private Plan plan;

        public String getId() {
            return id;
        }

        public String getStatus() {
            return status;
        }

        @Override
        public String toString() {
            return "Subscription{" +
                    "id='" + id + '\'' +
                    ", object='" + object + '\'' +
                    ", billingCycleAnchor=" + billingCycleAnchor +
                    ", created=" + created +
                    ", status='" + status + '\'' +
                    ", plan=" + plan +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SubscriptionsListResponse{" +
                "subscriptions=" + subscriptions +
                '}';
    }
}

class Plan{
    private String id;
    private String object;
    private long amount;
    private String currency;
    private String interval;
    private int intervalCount;
    private String product;

    @Override
    public String toString() {
        return "Plan{" +
                "id='" + id + '\'' +
                ", object='" + object + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", interval='" + interval + '\'' +
                ", intervalCount=" + intervalCount +
                ", product='" + product + '\'' +
                '}';
    }
}
