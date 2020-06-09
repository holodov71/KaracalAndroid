package app.karacal.retrofit.models.response;

import com.google.gson.annotations.SerializedName;

import app.karacal.models.Profile;

public class GuideResponse {

    private int id;
    private Profile client;
    @SerializedName("desc")
    private String description;

    public int getId() {
        return id;
    }

    public Profile getClient() {
        return client;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "GuideResponse{" +
                "id=" + id +
                ", client=" + client +
                '}';
    }
}
