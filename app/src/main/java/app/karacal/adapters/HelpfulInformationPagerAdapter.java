package app.karacal.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import app.karacal.R;
import app.karacal.fragments.ItemHelpfulInformationFragment;

public class HelpfulInformationPagerAdapter extends FragmentStatePagerAdapter {

    private static final int COUNT = 10;

    public HelpfulInformationPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        ItemHelpfulInformationFragment fragment = ItemHelpfulInformationFragment.getInstance(R.mipmap.image_helpful_information, "Lorem Ipsum", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum egestas hendrerit ex, non vestibulum velit efficitur eget. Etiam cursus eget metus a pulvinar. Proin lobortis laoreet magna, in eleifend risus dignissim id. Pellentesque accumsan eros non mattis elementum. Fusce semper orci sed leo interdum, quis hendrerit libero sodales.");
        return fragment;
    }

    @Override
    public int getCount() {
        return COUNT;
    }
}
