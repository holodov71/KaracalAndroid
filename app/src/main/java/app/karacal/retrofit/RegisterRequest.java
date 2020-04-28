package app.karacal.retrofit;

import java.util.Date;

public class RegisterRequest {

    private final String firstName;
    private final String secondName;
    private final Date birthDate;
    private final String email;
    private final String location;
    private final String referralCode;
    private final String password;

    public RegisterRequest(String firstName, String secondName, Date birthDate, String email, String location, String referralCode, String password) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.birthDate = birthDate;
        this.email = email;
        this.location = location;
        this.referralCode = referralCode;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getEmail() {
        return email;
    }

    public String getLocation() {
        return location;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public String getPassword() {
        return password;
    }
}
