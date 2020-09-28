package app.karacal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.karacal.R;
import app.karacal.models.ItemHelpfulInfo;
import apps.in.android_logger.LogFragment;

public class ItemHelpfulInformationFragment extends LogFragment {

    private static final String IMAGE_ID_KEY = "imageId";
    private static final String TITLE_KEY = "title";
    private static final String CAPTION_KEY = "caption";

    private int imageId;
    private String title;
    private String caption;

    public static ItemHelpfulInformationFragment getInstance(ItemHelpfulInfo item){
        ItemHelpfulInformationFragment fragment = new ItemHelpfulInformationFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(IMAGE_ID_KEY, item.getImageId());
        bundle.putString(TITLE_KEY, item.getTitle());
        bundle.putString(CAPTION_KEY, item.getCaption());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        imageId = bundle.getInt(IMAGE_ID_KEY);
        title = bundle.getString(TITLE_KEY);
        caption = bundle.getString(CAPTION_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_helpful_information, container, false);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(imageId);
        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewTitle.setText(title);
        TextView textViewCaption = view.findViewById(R.id.textViewCaption);
        textViewCaption.setText(caption);
        return view;
    }
}
