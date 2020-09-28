package app.karacal.models;

public class ItemHelpfulInfo {
    private int imageId;
    private String title;
    private String caption;

    public ItemHelpfulInfo(int imageId, String title, String caption) {
        this.imageId = imageId;
        this.title = title;
        this.caption = caption;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public String toString() {
        return "ItemHelpfulInfo{" +
                "imageId=" + imageId +
                ", title='" + title + '\'' +
                ", caption='" + caption + '\'' +
                '}';
    }
}
