package app.karacal.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class TourMarker implements ClusterItem {

    private final LatLng position;
    private final int tourId;

    public TourMarker(Tour tour){
        this.tourId = tour.getId();
        this.position = tour.getLocation();
    }

    public int getTourId() {
        return tourId;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }
}
