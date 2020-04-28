package app.karacal.fragments;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.maps.android.clustering.ClusterManager;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.activities.AudioActivity;
import app.karacal.adapters.TourVerticalListAdapter;
import app.karacal.data.TourRepository;
import app.karacal.helpers.PermissionHelper;
import app.karacal.helpers.TextInputHelper;
import app.karacal.helpers.TourMarkerRender;
import app.karacal.models.Tour;
import app.karacal.models.TourMarker;
import app.karacal.navigation.NavigationHelper;
import apps.in.android_logger.Logger;

public class MainLocationFragment extends Fragment implements OnMapReadyCallback {


    @Inject
    TourRepository tourRepository;

    @Inject
    PermissionHelper permissionHelper;

    private ConstraintLayout layoutRoot;
    private ImageView buttonSearch;
    private ImageView buttonToggle;
    private RecyclerView recyclerView;
    private ConstraintLayout layoutSearchField;
    private ConstraintLayout layourSearchEditText;
    private EditText editTextSearch;
    private ImageView buttonClear;

    private GoogleMap map;
    private View mapView;
    private ClusterManager<TourMarker> clusterManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_location, container, false);
        layoutRoot = (ConstraintLayout) view;
        setupSearchField(view);
        setupToggleButton(view);
        setupSearchResults(view);
        setupMap();
        return view;
    }

    private void setupSearchField(View view) {
        buttonSearch = view.findViewById(R.id.buttonSearch);
        layoutSearchField = view.findViewById(R.id.layoutSearchField);
        layourSearchEditText = view.findViewById(R.id.layoutSearchEditText);
        buttonClear = view.findViewById(R.id.buttonClear);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        buttonClear.setOnClickListener(v -> editTextSearch.setText(""));
        buttonSearch.setOnClickListener(v -> toggleSearchField());
        TextInputHelper.editTextObservable(editTextSearch).subscribe((search) -> {
            //TODO implement
            TransitionManager.beginDelayedTransition(layoutSearchField);
            buttonClear.setVisibility(TextUtils.isEmpty(search) ? View.GONE : View.VISIBLE);
        }, (throwable) -> {
            //TODO implement
        }, () -> {
            //TODO implement
        });
    }

    private void setupToggleButton(View view) {
        buttonToggle = view.findViewById(R.id.buttonToggle);
        buttonToggle.setOnClickListener(v -> toggleSearchResults());
    }

    private void setupSearchResults(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        TourVerticalListAdapter adapter = new TourVerticalListAdapter(getContext());
        adapter.setTours(tourRepository.getAllTours());
        adapter.setClickListener(this::showTour);
        recyclerView.setAdapter(adapter);
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        applyMapStyle();
        setupMyLocationButton();
        setupClusterManager();
        setMarkers();
        //TODO replace
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(48.853636, 2.348795), 10));
    }

    private void setMarkers(){
        clusterManager.clearItems();
        for (Tour tour : tourRepository.getAllTours()){
            clusterManager.addItem(new TourMarker(tour));
        }
    }

    private void setupClusterManager() {
        clusterManager = new ClusterManager<>(getContext(), map);
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        clusterManager.setRenderer(new TourMarkerRender(getContext(), map, clusterManager));
    }

    private void setupMyLocationButton() {
        permissionHelper.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION,
                () -> {
                    map.setMyLocationEnabled(true);
                    map.getUiSettings().setMyLocationButtonEnabled(true);
                    if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
                        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                        layoutParams.setMargins(0, 0, 30, 90);
                    }
                    //TODO uncomment
//                    LocationHelper.getLastKnownLocation()
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .map(location -> new LatLng(location.getLatitude(), location.getLongitude()))
//                            .subscribe(location -> map.moveCamera(CameraUpdateFactory.newLatLng(location)),
//                                    throwable -> Logger.log(this, "Error obtaining location", throwable));
                },
                () -> {
                    map.setMyLocationEnabled(false);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                });
    }

    private void applyMapStyle() {
        try {
            boolean success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.style_json));
            if (!success) {
                Logger.log(this, "Google maps style parsing failed");
            }
        } catch (Exception e) {
            Logger.log(this, "Google maps style parsing failed", e);
        }
    }

    private void toggleSearchResults() {
        TransitionManager.beginDelayedTransition(layoutRoot);
        recyclerView.setVisibility(recyclerView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    private void toggleSearchField() {

        TransitionManager.beginDelayedTransition(layoutRoot);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layoutRoot);
        if (layourSearchEditText.getVisibility() == View.VISIBLE) {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);
            }
            constraintSet.clear(R.id.layoutSearchField, ConstraintSet.START);
            layourSearchEditText.setVisibility(View.GONE);
        } else {
            constraintSet.connect(R.id.layoutSearchField, ConstraintSet.START, R.id.layoutRoot, ConstraintSet.START, ((ConstraintLayout.LayoutParams) layoutSearchField.getLayoutParams()).getMarginEnd());
            layourSearchEditText.setVisibility(View.VISIBLE);
        }
        constraintSet.applyTo(layoutRoot);
    }

    private void showTour(int tourId){
        AudioActivity.Args args = new AudioActivity.Args(tourId);
        NavigationHelper.startAudioActivity(getActivity(), args);
    }


}
