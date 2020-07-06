package app.karacal.network.models.response;

import java.util.List;

public class PurchasesResponse extends BaseResponse {
    private List<Integer> idTours;
    private List<SubscriptionWrapper> subscriptions;

    public List<Integer> getIdTours() {
        return idTours;
    }

    public List<SubscriptionWrapper> getSubscriptions() {
        return subscriptions;
    }

    @Override
    public String toString() {
        return "PurchasesResponse{" +
                "idTours=" + idTours +
                ", subscriptions=" + subscriptions +
                '}';
    }
}
