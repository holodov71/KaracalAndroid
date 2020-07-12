package app.karacal.network.models.request;

import java.util.Date;
import java.util.List;

public class SaveTourRequest {

    private final String title;
    private final String author;
    private final String date;
    private final String latitude;
    private final String longitude;
    private final List<Integer> arr_tags;
    private final String desc;
    private final String address;
    private final String city;
    private final String price;
    private final int guide_id;

    public SaveTourRequest(String title, String author, String date, String latitude, String longitude, List<Integer> tags, String desc, String address, String city, String price, int guideId) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.arr_tags = tags;
        this.desc = desc;
        this.address = address;
        this.city = city;
        this.price = price;
        this.guide_id = guideId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public List<Integer> getTags() {
        return arr_tags;
    }

    public String getDesc() {
        return desc;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getPrice() {
        return price;
    }

    public int getGuideId() {
        return guide_id;
    }

    @Override
    public String toString() {
        return "SaveTourRequest{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", date='" + date + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", tags='" + arr_tags + '\'' +
                ", desc='" + desc + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", price='" + price + '\'' +
                ", guideId='" + guide_id + '\'' +
                '}';
    }
}
