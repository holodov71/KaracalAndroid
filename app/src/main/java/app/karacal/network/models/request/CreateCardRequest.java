package app.karacal.network.models.request;

public class CreateCardRequest {
    private String idCustomer;
    private String tokenCard;

    public CreateCardRequest(String idCustomer, String tokenCard) {
        this.idCustomer = idCustomer;
        this.tokenCard = tokenCard;
    }
}
