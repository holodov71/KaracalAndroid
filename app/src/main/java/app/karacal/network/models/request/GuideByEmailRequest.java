package app.karacal.network.models.request;

public class GuideByEmailRequest {
    private String email;

    public GuideByEmailRequest(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "GuideByEmailRequest{" +
                "email='" + email + '\'' +
                '}';
    }
}
