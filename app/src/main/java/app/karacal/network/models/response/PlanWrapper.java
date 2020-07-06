package app.karacal.network.models.response;

public class PlanWrapper {
    private String id;
    private String object;
    private long amount;
    private String currency;
    private String interval;
    private int intervalCount;
    private String product;

    @Override
    public String toString() {
        return "PlanWrapper{" +
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
