package app.karacal.network.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.karacal.models.Profile;

public class GuideResponse {

    private int id;
    private Profile client;
    @SerializedName("desc")
    private String description;
    private List<Integer> tourIndexes;

    public int getId() {
        return id;
    }

    public Profile getClient() {
        return client;
    }

    public String getDescription() {
        return description;
    }

    public int getTourIndexesCount() {
        if (tourIndexes != null) return tourIndexes.size();
        else return 0;
    }

    @Override
    public String toString() {
        return "GuideResponse{" +
                "id=" + id +
                ", client=" + client +
                '}';
    }
}
