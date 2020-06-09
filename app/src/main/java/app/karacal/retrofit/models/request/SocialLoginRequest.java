package app.karacal.retrofit.models.request;

import com.google.gson.annotations.SerializedName;

public class SocialLoginRequest {

    @SerializedName("user_id")
    private final String userId;

    @SerializedName("first_name")
    private final String firstName;

    @SerializedName("second_name")
    private final String secondName;

    @SerializedName("email")
    private final String email;

    public SocialLoginRequest(String userId, String firstName, String secondName, String email) {
        this.userId = userId;
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
    }

    public String getUserId() {
        return userId;
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

    @Override
    public String toString() {
        return "SocialLoginRequest{" +
                "userId='" + userId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
