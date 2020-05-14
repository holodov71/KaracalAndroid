package app.karacal.models;

public class Guide {

    private int id;
    private String name;
    private String localization;
    private String description;
    private int avatarId;

    public Guide(int id, String name, String localization, String  description, int avatarId) {
        this.id = id;
        this.name = name;
        this.localization = localization;
        this.description = description;
        this.avatarId = avatarId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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
}
