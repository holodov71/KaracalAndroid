package app.karacal.popups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import app.karacal.R;

public class SelectActionPopup extends BasePopup {

    public interface SelectActionPopupCallbacks{
        void onButtonLikeClick(BasePopup popup);
        void onButtonDownloadAlbumClick(BasePopup popup);
        void onButtonShareTrackClick(BasePopup popup);
        void onButtonReportProblemClick(BasePopup popup);
    }

    private final SelectActionPopupCallbacks callbacks;

    private final View view;

    public SelectActionPopup(ViewGroup parent, SelectActionPopupCallbacks callbacks) {
        super(parent);
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.popup_select_action, parent, false);
        setup(view);
        this.callbacks = callbacks;
    }

    private void setup(View view) {
        LinearLayout buttonLikeIt = view.findViewById(R.id.buttonLikeIt);
        buttonLikeIt.setOnClickListener(v -> callbacks.onButtonLikeClick(this));
        LinearLayout buttonDownloadAlbum = view.findViewById(R.id.buttonDownloadAllAlbum);
        buttonDownloadAlbum.setOnClickListener(v -> callbacks.onButtonDownloadAlbumClick(this));
        LinearLayout buttonShareTrack = view.findViewById(R.id.buttonShareThisTrack);
        buttonShareTrack.setOnClickListener(v -> callbacks.onButtonShareTrackClick(this));
        LinearLayout buttonReportProblem = view.findViewById(R.id.buttonReportProblem);
        buttonReportProblem.setOnClickListener(v -> callbacks.onButtonReportProblemClick(this));
    }

    @Override
    public View getView() {
        return view;
    }
}
