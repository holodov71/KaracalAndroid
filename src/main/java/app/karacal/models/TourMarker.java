package app.karacal.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class TourMarker implements ClusterItem {

    private final LatLng position;

    public TourMarker(LatLng position) {
        this.position = position;
    }

    public TourMarker(Tour tour){
        this.position = tour.getLocation();
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
