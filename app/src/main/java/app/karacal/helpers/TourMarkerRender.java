package app.karacal.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import app.karacal.R;
import app.karacal.models.TourMarker;

public class TourMarkerRender extends DefaultClusterRenderer<TourMarker> {

    private static final int MARKER_WIDTH = 36;
    private static final int MARKER_HEIGHT = 48;
    private final IconGenerator iconGenerator;
    private final IconGenerator clusterIconGenerator;
    private final ImageView imageView;

    public TourMarkerRender(Context context, GoogleMap map, ClusterManager<TourMarker> clusterManager) {
        super(context, map, clusterManager);
        iconGenerator = new IconGenerator(context);
        clusterIconGenerator = new IconGenerator(context);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_map_cluster, null, false);
        clusterIconGenerator.setBackground(new ColorDrawable(Color.TRANSPARENT));
        clusterIconGenerator.setContentView(view);

        imageView = new ImageView(context);
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MARKER_WIDTH, displayMetrics);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MARKER_HEIGHT, displayMetrics);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        imageView.setImageResource(R.drawable.ic_map_marker);
        iconGenerator.setBackground(new ColorDrawable(Color.TRANSPARENT));
        iconGenerator.setContentView(imageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(TourMarker item, MarkerOptions markerOptions) {
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<TourMarker> cluster, MarkerOptions markerOptions) {
        Bitmap icon = clusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }
}
