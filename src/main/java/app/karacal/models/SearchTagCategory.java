package app.karacal.models;

import java.util.ArrayList;

public class SearchTagCategory {

    private String title;
    private ArrayList<String> tags;

    public SearchTagCategory(String title, ArrayList<String> tags) {
        this.title = title;
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String> getTags() {
        return tags;
    }
}
