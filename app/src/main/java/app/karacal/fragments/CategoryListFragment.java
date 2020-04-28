package app.karacal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import app.karacal.R;
import app.karacal.activities.AudioActivity;
import app.karacal.adapters.TourVerticalListAdapter;
import app.karacal.navigation.NavigationHelper;
import app.karacal.viewmodels.CategoryActivityViewModel;

public class CategoryListFragment extends Fragment {

    private CategoryActivityViewModel viewModel;

    private TourVerticalListAdapter.TourClickListener tourClickListener = this::showTour;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(CategoryActivityViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        TourVerticalListAdapter adapter = new TourVerticalListAdapter(getContext());
        adapter.setTours(viewModel.getTours());
        adapter.setClickListener(tourClickListener);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void showTour(int tourId){
        AudioActivity.Args args = new AudioActivity.Args(tourId);
        NavigationHelper.startAudioActivity(getActivity(), args);
    }

}