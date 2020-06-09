package app.karacal.retrofit.models.request;

import java.util.Date;

public class RegisterRequest {

    private final String firstName;
    private final String secondName;
    private final String email;
    private final String referralCode;
    private final String password;

    public RegisterRequest(String firstName, String secondName, String email, String referralCode, String password) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.referralCode = referralCode;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getEmail() {
        return email;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public String getPassword() {
        return password;
    }
}
