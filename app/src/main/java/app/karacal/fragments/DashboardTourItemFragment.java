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
import apps.in.android_logger.LogFragment;
import io.reactivex.disposables.Disposable;

public class DashboardTourItemFragment extends LogFragment {

    private static final String ARG_GUIDE_ID = "guide_id";

    private DashboardTourPagerAdapter.TourType tourType;
    private int guideId;


    @Inject
    TourRepository tourRepository;

    private Disposable disposable;

    public static DashboardTourItemFragment getInstance(DashboardTourPagerAdapter.TourType tourType, int guideId){
        DashboardTourItemFragment item = new DashboardTourItemFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DashboardTourPagerAdapter.TourType.class.getName(), tourType);
        bundle.putInt(ARG_GUIDE_ID, guideId);
        item.setArguments(bundle);
        return item;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        Bundle arguments = getArguments();
        tourType = (DashboardTourPagerAdapter.TourType) arguments.getSerializable(DashboardTourPagerAdapter.TourType.class.getName());
        guideId = arguments.getInt(ARG_GUIDE_ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_tour_item, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        DashboardTourListAdapter adapter = new DashboardTourListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        loadTours(adapter);
        return view;
    }

//    private void observeTours(DashboardTourListAdapter adapter){
//        tourRepository.originalToursLiveData.observe(getViewLifecycleOwner(), tours -> {
//            if (!tours.isEmpty()) {
//                adapter.setTours(tours);
//            }
//        });
//    }
    private void loadTours(DashboardTourListAdapter adapter){
        if (disposable != null){
            disposable.dispose();
        }

        disposable = tourRepository.loadToursByAuthor(guideId)
                .subscribe(
                        response -> {
                            if (!response.isEmpty()) {
                                adapter.setTours(response);
                            }
                        },
                        throwable -> adapter.setTours(new ArrayList<>())
                );
    }
}
