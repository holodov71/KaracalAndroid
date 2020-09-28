package app.karacal.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

import app.karacal.R;
import app.karacal.fragments.ItemHelpfulInformationFragment;
import app.karacal.models.ItemHelpfulInfo;

public class HelpfulInformationPagerAdapter extends FragmentStatePagerAdapter {

    private static final int COUNT = 10;
    private final List<ItemHelpfulInfo> items;

    public HelpfulInformationPagerAdapter(@NonNull FragmentManager fm, List<ItemHelpfulInfo> items) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.items = items;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        ItemHelpfulInformationFragment fragment = ItemHelpfulInformationFragment.getInstance(items.get(position));
        return fragment;
    }

    @Override
    public int getCount() {
        return items.size();
    }
}
