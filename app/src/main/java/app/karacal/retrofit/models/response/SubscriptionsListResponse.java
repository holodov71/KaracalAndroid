package app.karacal.retrofit.models.response;

import java.util.List;

public class SubscriptionsListResponse extends BaseResponse{
    private List<Subscription> subscriptions;
}

class Subscription{
    private String id;
    private String object;
    private long billingCycleAnchor;
    private long created;
    private String status;
    private Plan plan;
}

class Plan{
    private String id;
    private String object;
    private long amount;
    private String currency;
    private String interval;
    private int intervalCount;
    private String product;
}
