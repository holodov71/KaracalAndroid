package app.karacal.retrofit.models.request;

import java.util.Date;

public class ProfileRequest {

    private String firstName;
    private String secondName;
    private Date  birthDate;
    private String email;
    private String location;

    public ProfileRequest(String firstName, String secondName, Date birthDate, String email, String location) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.birthDate = birthDate;
        this.email = email;
        this.location = location;
    }
}
