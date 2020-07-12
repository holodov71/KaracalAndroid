package app.karacal.models;

import java.util.List;

public class SearchTagCategory {

    private String title;
    private List<Tag> tags;

    public SearchTagCategory(String title, List<Tag> tags) {
        this.title = title;
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public List<Tag> getTags() {
        return tags;
    }
}
