package app.karacal.popups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private final boolean isTourDownloaded;

    public SelectActionPopup(ViewGroup parent, SelectActionPopupCallbacks callbacks, boolean isTourDownloaded) {
        super(parent);
        this.callbacks = callbacks;
        this.isTourDownloaded = isTourDownloaded;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.popup_select_action, parent, false);
        setup(view);
    }

    private void setup(View view) {
        LinearLayout buttonLikeIt = view.findViewById(R.id.buttonLikeIt);
        buttonLikeIt.setOnClickListener(v -> callbacks.onButtonLikeClick(this));
        LinearLayout buttonDownloadAlbum = view.findViewById(R.id.buttonDownloadAllAlbum);
        buttonDownloadAlbum.setOnClickListener(v -> callbacks.onButtonDownloadAlbumClick(this));
        ImageView imgDownloadAlbum = view.findViewById(R.id.imgDownloadAlbum);
        TextView tvDownloadAlbum = view.findViewById(R.id.tvDownloadAlbum);

        if (isTourDownloaded){
            imgDownloadAlbum.setImageResource(R.drawable.ic_delete);
            tvDownloadAlbum.setText(R.string.delete_album);
        }

        LinearLayout buttonShareTrack = view.findViewById(R.id.buttonShareThisTrack);
        buttonShareTrack.setVisibility(View.GONE);
        buttonShareTrack.setOnClickListener(v -> callbacks.onButtonShareTrackClick(this));
        LinearLayout buttonReportProblem = view.findViewById(R.id.buttonReportProblem);
        buttonReportProblem.setOnClickListener(v -> callbacks.onButtonReportProblemClick(this));
    }

    @Override
    public View getView() {
        return view;
    }
}
