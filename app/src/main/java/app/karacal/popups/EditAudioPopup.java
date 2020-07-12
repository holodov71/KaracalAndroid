package app.karacal.popups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import app.karacal.R;

public class EditAudioPopup extends BasePopup {

    public interface EditAudioPopupCallbacks{
        void onButtonRenameClick(BasePopup popup);
        void onButtonEditClick(BasePopup popup);
        void onButtonDownloadClick(BasePopup popup);
        void onButtonDeleteClick(BasePopup popup);
    }

    private EditAudioPopupCallbacks callbacks;

    private final View view;
    private final boolean canDownload;

    public EditAudioPopup(ViewGroup parent, boolean canDownload, EditAudioPopupCallbacks callbacks) {
        super(parent);
        this.canDownload = canDownload;
        this.callbacks = callbacks;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.popup_audio_edit, parent, false);
        setup(view);
    }

    private void setup(View view) {
        LinearLayout buttonRename = view.findViewById(R.id.buttonRename);
        buttonRename.setOnClickListener(v -> callbacks.onButtonRenameClick(this));
//        LinearLayout buttonEdit = view.findViewById(R.id.buttonEdit);
//        buttonEdit.setOnClickListener(v -> callbacks.onButtonEditClick(this));
        LinearLayout buttonDownload = view.findViewById(R.id.buttonDownload);
        View downloadDivider = view.findViewById(R.id.downloadDivider);

        if (canDownload){
            buttonDownload.setOnClickListener(v -> callbacks.onButtonDownloadClick(this));
        } else {
            buttonDownload.setVisibility(View.GONE);
            downloadDivider.setVisibility(View.GONE);
        }


        LinearLayout buttonDelete = view.findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(v -> callbacks.onButtonDeleteClick(this));
    }

    @Override
    public View getView() {
        return view;
    }
}
