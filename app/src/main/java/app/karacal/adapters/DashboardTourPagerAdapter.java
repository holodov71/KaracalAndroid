package app.karacal.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import app.karacal.R;
import app.karacal.fragments.DashboardTourItemFragment;

public class DashboardTourPagerAdapter extends FragmentStatePagerAdapter {

    public enum TourType {
        DRAFT,
        WAITING,
        PUBLISHED,
        SUSPENDED
    }

    private final TourType[] tourTypes = TourType.values();
    private final Context context;
    private int guideId;

    public DashboardTourPagerAdapter(Context context, @NonNull FragmentManager fm, int guideId) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
        this.guideId = guideId;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return DashboardTourItemFragment.getInstance(tourTypes[position], guideId);
    }

    @Override
    public int getCount() {
        return tourTypes.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        TourType tourType = tourTypes[position];
        switch (tourType) {
            case DRAFT:
                return context.getString(R.string.draft);
            case WAITING:
                return context.getString(R.string.waiting);
            case PUBLISHED:
                return context.getString(R.string.published);
            case SUSPENDED:
                return context.getString(R.string.suspended);
        }
        return super.getPageTitle(position);
    }
}
