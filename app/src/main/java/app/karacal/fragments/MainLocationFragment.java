package app.karacal.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.activities.CategoryActivity;
import app.karacal.adapters.TourVerticalListAdapter;
import app.karacal.data.LocationCache;
import app.karacal.data.repository.TourRepository;
import app.karacal.helpers.LocationHelper;
import app.karacal.helpers.PermissionHelper;
import app.karacal.helpers.TextInputHelper;
import app.karacal.helpers.TourMarkerRender;
import app.karacal.models.CategoryViewMode;
import app.karacal.models.Tour;
import app.karacal.models.TourCategory;
import app.karacal.models.TourMarker;
import app.karacal.navigation.NavigationHelper;
import apps.in.android_logger.Logger;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainLocationFragment extends Fragment implements OnMapReadyCallback,
           ClusterManager.OnClusterItemClickListener<TourMarker> {

    private static final int MAP_ZOOM_LEVEL = 10;

    @Inject
    TourRepository tourRepository;

    @Inject
    PermissionHelper permissionHelper;

    private ConstraintLayout layoutRoot;
    private RecyclerView recyclerView;
    private ConstraintLayout layoutSearchField;
    private ConstraintLayout layoutSearchEditText;
    private EditText editTextSearch;
    private ImageView buttonClear;

    private GoogleMap map;
    private View mapView;
    private ClusterManager<TourMarker> clusterManager;
    TourVerticalListAdapter adapter;

    LatLng parisLocation = new LatLng(48.864716, 2.349014);


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

    @SuppressLint("CheckResult")
    private void setupSearchField(View view) {
        ImageView buttonSearch = view.findViewById(R.id.buttonSearch);
        layoutSearchField = view.findViewById(R.id.layoutSearchField);
        layoutSearchEditText = view.findViewById(R.id.layoutSearchEditText);
        buttonClear = view.findViewById(R.id.buttonClear);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        buttonClear.setOnClickListener(v -> editTextSearch.setText(""));
        buttonSearch.setOnClickListener(v -> toggleSearchField());
        TextInputHelper.editTextObservable(editTextSearch).subscribe((search) -> {
            TransitionManager.beginDelayedTransition(layoutSearchField);
            buttonClear.setVisibility(TextUtils.isEmpty(search) ? View.GONE : View.VISIBLE);
            searchByText(search);
        }, (throwable) -> {
            //TODO implement
        }, () -> {
            //TODO implement
        });
    }

    private void setupToggleButton(View view) {
        ImageView buttonToggle = view.findViewById(R.id.buttonToggle);
        buttonToggle.setOnClickListener(v -> toggleSearchResults());
    }

    private void setupSearchResults(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new TourVerticalListAdapter(getContext());
        adapter.setClickListener(this::showTours);
        recyclerView.setAdapter(adapter);
        observeTours(adapter);
    }

    private void observeTours(TourVerticalListAdapter adapter){
        tourRepository.originalToursLiveData.observe(getViewLifecycleOwner(), tours -> {
            if (!tours.isEmpty()) {
                adapter.setTours(tours);
            }
        });
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.moveCamera(CameraUpdateFactory.zoomTo(MAP_ZOOM_LEVEL));
        map.moveCamera(CameraUpdateFactory.newLatLng(parisLocation));
        applyMapStyle();
        setupLocation();
        setupClusterManager();
        observeTours();
    }

    private void observeTours(){
        tourRepository.originalToursLiveData.observe(getViewLifecycleOwner(), tours -> {
            if (!tours.isEmpty()) {
                setMarkers(tours);
            }
        });
    }

    private void setMarkers(List<Tour> tours){
        clusterManager.clearItems();
        for (Tour tour : tours){
            clusterManager.addItem(new TourMarker(tour));
        }
    }

    private void setupClusterManager() {
        clusterManager = new ClusterManager<>(getContext(), map);
        clusterManager.setOnClusterItemClickListener(this);
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        clusterManager.setRenderer(new TourMarkerRender(getContext(), map, clusterManager));
    }

    @SuppressLint("CheckResult")
    private void setupLocation() {
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
                    LocationHelper.getLastKnownLocation()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .map(location -> new LatLng(location.getLatitude(), location.getLongitude()))
                            .subscribe(location -> map.moveCamera(CameraUpdateFactory.newLatLng(location)),
                                    throwable -> Logger.log(this, "Error obtaining location", throwable));
                },
                () -> {
                    LocationCache.getInstance(App.getInstance()).setHasPermission(false);
                    map.setMyLocationEnabled(false);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                    map.moveCamera(CameraUpdateFactory.newLatLng(parisLocation));
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
        if (layoutSearchEditText.getVisibility() == View.VISIBLE) {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);
            }
            constraintSet.clear(R.id.layoutSearchField, ConstraintSet.START);
            layoutSearchEditText.setVisibility(View.GONE);
        } else {
            constraintSet.connect(R.id.layoutSearchField, ConstraintSet.START, R.id.layoutRoot, ConstraintSet.START, ((ConstraintLayout.LayoutParams) layoutSearchField.getLayoutParams()).getMarginEnd());
            layoutSearchEditText.setVisibility(View.VISIBLE);
        }
        constraintSet.applyTo(layoutRoot);
    }

    private void searchByText(String query){
        ArrayList<Tour> tours = tourRepository.searchToursByText(query);
        adapter.setTours(tours);
        setMarkers(tours);
        clusterManager.cluster();
    }

    private void showTours(int tourId){
        CategoryActivity.Args args = new CategoryActivity.Args(TourCategory.CATEGORY_ORIGINAL, getString(R.string.originals), CategoryViewMode.STACK, tourId);
        NavigationHelper.startCategoryActivity(getActivity(), args);
    }

    @Override
    public boolean onClusterItemClick(TourMarker tourMarker) {
        showTours(tourMarker.getTourId());
        return true;
    }
}
