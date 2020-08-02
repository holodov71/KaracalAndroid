package app.karacal.models;

public class CardDetails {

    public CardDetails(String number, String cvc, int expMonth, int expYear, String brand, String owner) {
        this.number = number;
        this.cvc = cvc;
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.brand = brand;
        this.owner = owner;
        isDefault = false;
    }

    /**
     * the [number] of this card
     */
    private String number;

    /**
     * the [cvc] for this card
     */
    private String cvc;

    /**
     * Two-digit number representing the card’s expiration month.
     */
    private int expMonth;

    /**
     * Four-digit number representing the card’s expiration year.
     */
    private int expYear;

    /**
     * the [brand] of this card. For example, Visa or Mastercard
     */
    private String brand;

    /**
     * the [owner] of this card. For example, Vasyuk
     */
    private String owner;

    /**
     * is card default for payment
     */
    private boolean isDefault;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public int getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(int expMonth) {
        this.expMonth = expMonth;
    }

    public int getExpYear() {
        return expYear;
    }

    public void setExpYear(int expYear) {
        this.expYear = expYear;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    @Override
    public String toString() {
        return "CardDetails{" +
                "number='" + number + '\'' +
                ", cvc='" + cvc + '\'' +
                ", expMonth=" + expMonth +
                ", expYear=" + expYear +
                ", brand='" + brand + '\'' +
                ", owner='" + owner + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}
