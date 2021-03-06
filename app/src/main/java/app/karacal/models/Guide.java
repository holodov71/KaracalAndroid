package app.karacal.models;

import app.karacal.R;
import app.karacal.network.models.response.GuideResponse;

public class Guide {

    private int id;
    private String firstName;
    private String secondName;
    private String localization;
    private String description;
    private int countGuides;
    private int avatarId;
    private String avatarUrl;

    public Guide(int id, String firstName, String secondName, String localization, String  description, int avatarId) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.localization = localization;
        this.description = description;
        this.avatarId = avatarId;
    }

    public Guide(GuideResponse guideResponse){
        this.id = guideResponse.getId();
        this.firstName = guideResponse.getClient().getFirstName();
        this.secondName = guideResponse.getClient().getSecondName();
        this.localization = guideResponse.getClient().getLocation();
        this.description = guideResponse.getDescription();
        this.countGuides = guideResponse.getTourIndexesCount();
        this.avatarId = R.mipmap.avatar_example;
        this.avatarUrl = guideResponse.getClient().getAvatar();
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getName(){
        return firstName + " " + secondName;
    }

    public String getLocalization() {
        return localization;
    }

    public String getDescription() {
        return description;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public int getCountGuides() {
        return countGuides;
    }
}
