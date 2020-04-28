package app.karacal.popups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import app.karacal.R;
import app.karacal.views.RatingView;

public class ShareImpressionPopup extends BasePopup {

    public interface ShareImpressionPopupCallbacks{
        void onButtonDonateClick(BasePopup popup);
        void onButtonPutGuideInFavorClick(BasePopup popup);
        void onButtonWriteCommentClick(BasePopup popup);
        void onButtonSubmitClick(BasePopup popup);
    }

    private final ShareImpressionPopup.ShareImpressionPopupCallbacks callbacks;

    private final View view;

    public ShareImpressionPopup(ViewGroup parent, ShareImpressionPopup.ShareImpressionPopupCallbacks callbacks) {
        super(parent);
        this.callbacks = callbacks;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.popup_share_impression, parent, false);
        setup(view);
    }

    private void setup(View view){
        LinearLayout buttonDonateGuide = view.findViewById(R.id.buttonDonateToTheGuide);
        buttonDonateGuide.setOnClickListener(v -> callbacks.onButtonDonateClick(this));
        LinearLayout buttonPutGuideInFavor = view.findViewById(R.id.buttonPutGuideInFavor);
        buttonPutGuideInFavor.setOnClickListener(v -> callbacks.onButtonPutGuideInFavorClick(this));
        LinearLayout buttonWriteComment = view.findViewById(R.id.buttonWriteComment);
        buttonWriteComment.setOnClickListener(v -> callbacks.onButtonWriteCommentClick(this));
        Button buttonSubmit = view.findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(v -> callbacks.onButtonSubmitClick(this));
        RatingView ratingView = view.findViewById(R.id.ratingView);
        ratingView.setRatingChangeListener(rating -> buttonSubmit.setEnabled(rating > 0));
    }

    @Override
    public View getView() {
        return view;
    }
}