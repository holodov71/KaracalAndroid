package app.karacal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.adapters.DashboardTourListAdapter;
import app.karacal.adapters.DashboardTourPagerAdapter;
import app.karacal.data.repository.TourRepository;
import app.karacal.models.Tour;
import apps.in.android_logger.LogFragment;

public class DashboardTourItemFragment extends LogFragment {

    private DashboardTourPagerAdapter.TourType tourType;

    @Inject
    TourRepository tourRepository;

    public static DashboardTourItemFragment getInstance(DashboardTourPagerAdapter.TourType tourType){
        DashboardTourItemFragment item = new DashboardTourItemFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DashboardTourPagerAdapter.TourType.class.getName(), tourType);
        item.setArguments(bundle);
        return item;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        Bundle arguments = getArguments();
        tourType = (DashboardTourPagerAdapter.TourType) arguments.getSerializable(DashboardTourPagerAdapter.TourType.class.getName());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_tour_item, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        DashboardTourListAdapter adapter = new DashboardTourListAdapter(getActivity());
        ArrayList<Tour> allTours = tourRepository.getAllTours();
        adapter.setTours(allTours);
        recyclerView.setAdapter(adapter);
        return view;
    }
}
