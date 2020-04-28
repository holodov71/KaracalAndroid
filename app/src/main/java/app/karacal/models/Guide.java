package app.karacal.models;

public class Guide {

    private String name;
    private int avatarId;

    public Guide(String name, int avatarId) {
        this.name = name;
        this.avatarId = avatarId;
    }

    public String getName() {
        return name;
    }

    public int getAvatarId() {
        return avatarId;
    }
}
