package app.karacal.network.models.response;

import java.util.List;

public class PaymentsListResponse extends BaseResponse{
    private List<Payment> data;
}

class Payment{
    private String id;
    private String object;
    private long amount;
    private String balanceTransaction;
    private boolean captured;
    private long created;
    private String currency;
    private String description;
    private String failureMessage;
    private boolean paid;
    private String paymentMethod;
    private String receiptUrl;
    private String status;
}