package app.karacal.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Locale;

import app.karacal.R;
import app.karacal.activities.AudioActivity;
import app.karacal.activities.ProfileActivity;
import app.karacal.helpers.DummyHelper;
import app.karacal.helpers.ImageHelper;
import app.karacal.helpers.ShareHelper;
import app.karacal.models.Guide;
import app.karacal.navigation.NavigationHelper;
import app.karacal.viewmodels.AudioActivityViewModel;
import app.karacal.views.StarsView;
import apps.in.android_logger.LogFragment;

public class AudioDescriptionFragment extends LogFragment {

    private AudioActivityViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(AudioActivityViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio_description, container, false);
        setupBackButton(view);
        setupDownloadButton(view);
        setupBackground(view);
        setupButtons(view);
        setupPrice(view);
        setupRating(view);
        setupTitle(view);
        setupDescription(view);
        setupAddress(view);
        setupDuration(view);
        setupReviews(view);
        setupAuthor(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observeViewModel();
    }

    private void setupBackButton(View view) {
        ImageView buttonBack = view.findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> getActivity().onBackPressed());
    }

    private void setupDownloadButton(View view) {
        ImageView buttonDownload = view.findViewById(R.id.buttonDownload);
        buttonDownload.setVisibility(View.GONE);
        buttonDownload.setOnClickListener(v -> DummyHelper.dummyAction(getContext()));
    }

    private void setupBackground(View view){
        ImageView imageView = view.findViewById(R.id.imageViewBackground);
        ImageHelper.setImage(imageView, viewModel.getTour().getImageUrl(), viewModel.getTour().getImage(), false);
    }

    private void setupButtons(View view){
        ImageView buttonShare = view.findViewById(R.id.buttonShare);
        buttonShare.setOnClickListener(v -> ShareHelper.share(getActivity(), "Share tour", viewModel.getTour().getTitle(), viewModel.getTour().getDescription()));
        ImageView buttonPlay = view.findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(v -> {
            viewModel.onListenTourClicked();
//            long price = viewModel.getTour().getPrice();
//            if (price != 0){
//                Activity activity = getActivity();
//                if (activity != null) {
//                    ((AudioActivity) activity).showSelectPlanDialog(price);
//                }
//            } else {
//                NavHostFragment.findNavController(this).navigate(R.id.audioPlayerFragment);
//            }
        });
        ImageView buttonLike = view.findViewById(R.id.buttonLike);
        buttonLike.setOnClickListener(v -> buttonLike.setSelected(!buttonLike.isSelected()));
    }

    private void setupPrice(View view){
        TextView textView = view.findViewById(R.id.textViewPrice);
        ConstraintLayout constraintLayout = view.findViewById(R.id.layoutAlert);
        if (viewModel.getTour().getPrice() != 0){
            textView.setText(getContext().getString(R.string.price_format, viewModel.getTour().getDoublePrice(), getContext().getString(R.string.euro)));
            textView.setVisibility(View.VISIBLE);
            constraintLayout.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
            constraintLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void setupRating(View view){
        StarsView starsViewTop = view.findViewById(R.id.starsViewTop);
        StarsView starsViewDescription = view.findViewById(R.id.starsViewDescription);
        int rating = viewModel.getTour().getRating();
        starsViewTop.setRating(rating);
        starsViewDescription.setRating(rating);
    }

    private void setupTitle(View view){
        TextView textView = view.findViewById(R.id.textViewTitle);
        textView.setText(viewModel.getTour().getTitle());
    }

    private void setupDescription(View view){
        TextView textView = view.findViewById(R.id.textViewDescription);
        textView.setText(viewModel.getTour().getDescription());
    }

    private void setupAddress(View view){
        TextView textView = view.findViewById(R.id.textViewAddress);
        textView.setText("1,rue Beyouroux, 79008, Paris");
    }

    private void setupDuration(View view){
        TextView textView = view.findViewById(R.id.textViewDuration);
        int duration = viewModel.getTour().getDuration();
        int hours = duration / 60;
        String hoursText = hours > 0 ? String.format(Locale.getDefault(), "%d %s", hours, getContext().getString(hours > 1 ? R.string.hours : R.string.hour)) : "";
        int minutes = duration % 60;
        String minutesText = String.format(Locale.getDefault(), "%d %s", minutes, getContext().getString(minutes != 1 ? R.string.minutes : R.string.minute));
        textView.setText(String.format("%s %s", hoursText, minutesText));
    }

    private void setupReviews(View view){
        int count = 3;
        TextView textView = view.findViewById(R.id.textViewReviews);
        textView.setText(getContext().getString(R.string.comments_count_format, count, getContext().getString(count != 1 ? R.string.comments : R.string.comment)));
//        textView.setOnClickListener(v -> NavigationHelper.startCommentsActivity(getActivity()));
    }

    private void setupAuthor(View view){
//        String author = "Alexander McQueen";
        Guide author = viewModel.getAuthor();
        int count = viewModel.getCountGuides();
        ImageView imageView = view.findViewById(R.id.imageViewAuthor);
        TextView textViewAuthor = view.findViewById(R.id.textViewAuthor);
        if (author != null) {
            if (author.getAvatarId() == -1) {
                imageView.setImageResource(R.drawable.ic_person);
            } else {
                imageView.setImageResource(author.getAvatarId());
            }

            textViewAuthor.setText(author.getName());

            ConstraintLayout buttonAuthor = view.findViewById(R.id.buttonAuthor);
            buttonAuthor.setOnClickListener(v -> {
                ProfileActivity.Args args = new ProfileActivity.Args(author.getId());
                NavigationHelper.startProfileActivity(getActivity(), args);
            });
        } else {
            imageView.setImageResource(R.drawable.ic_person);
            textViewAuthor.setText(viewModel.getTour().getAuthor());
        }
        TextView textViewGuidesCount = view.findViewById(R.id.textViewGuidesCount);
        textViewGuidesCount.setText(getContext().getString(R.string.guide_count_format, count, getContext().getString(count != 1 ? R.string.guides : R.string.guide)));
        ImageView imageViewAlert = view.findViewById(R.id.imageViewAuthorAlert);
//        imageViewAlert.setOnClickListener(v -> ((AudioActivity) getActivity()).showSelectActionPopup());

    }

    private void observeViewModel() {
        viewModel.getListenAction().observe(getViewLifecycleOwner(), action ->
                NavHostFragment.findNavController(this).navigate(R.id.audioPlayerFragment));

        viewModel.getPaymentAction().observe(getViewLifecycleOwner(), tourPrice -> {
            Activity activity = getActivity();
            if (activity != null) {
                ((AudioActivity) activity).showSelectPlanDialog(tourPrice);
            }
        });
    }


}
