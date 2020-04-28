package app.karacal.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


import java.util.ArrayList;

import app.karacal.fragments.TourStackItemFragment;
import app.karacal.models.Tour;

public class TourStackPagerAdapter extends FragmentStatePagerAdapter {

    private final ArrayList<Tour> tours;

    public TourStackPagerAdapter(@NonNull FragmentManager fm, ArrayList<Tour> tours) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.tours = tours;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return TourStackItemFragment.create(tours.get(position));
    }

    @Override
    public int getCount() {
        return tours.size();
    }

    public Tour getTour(int position){
        return tours.get(position);
    }
}
