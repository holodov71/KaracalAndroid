package app.karacal.retrofit.models.response;

public class SubscriptionWrapper {
    private String id;
    private String object;
    private long billingCycleAnchor;
    private long created;
    private String status;
    private PlanWrapper plan;

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "SubscriptionWrapper{" +
                "id='" + id + '\'' +
                ", object='" + object + '\'' +
                ", billingCycleAnchor=" + billingCycleAnchor +
                ", created=" + created +
                ", status='" + status + '\'' +
                ", plan=" + plan +
                '}';
    }
}
