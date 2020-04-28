package app.karacal.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputLayout;

import app.karacal.R;
import app.karacal.helpers.TextInputHelper;
import apps.in.android_logger.LogDialogFragment;

public class AudioTitleDialog extends LogDialogFragment {

    public static final String DIALOG_TAG = "AudioTitleDialog";

    private static final String OLD_TITLE_KEY = "oldTitle";

    public interface AudioTitleDialogListener {
        void onTitleInput(String title);
    }

    private AudioTitleDialogListener listener;

    private String oldTitle;

    private AlertDialog dialog;
    private TextInputLayout textInputLayout;
    private Button buttonSave;

    public static AudioTitleDialog getInstance(String oldTitle){
        AudioTitleDialog dialog = new AudioTitleDialog();
        Bundle bundle = new Bundle();
        bundle.putString(OLD_TITLE_KEY, oldTitle);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        oldTitle = bundle.getString(OLD_TITLE_KEY, "");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
        dialog = builder.setTitle(R.string.enter_audio_title)
                .setView(R.layout.dialog_audio_title)
                .setPositiveButton(R.string.save, (di, which) -> {
                    if (listener != null) {
                        listener.onTitleInput(textInputLayout.getEditText().getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, (di, which) -> di.dismiss())
                .create();
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        textInputLayout = dialog.findViewById(R.id.textInputLayoutTitle);
        buttonSave = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        textInputLayout.getEditText().setText(oldTitle);
        TextInputHelper.editTextObservable(textInputLayout).subscribe((s) -> validateInput(s));
        validateInput(textInputLayout.getEditText().getText().toString());
    }

    private void validateInput(String s) {
        buttonSave.setEnabled(!TextUtils.isEmpty(s));
    }

    public void setListener(AudioTitleDialogListener listener) {
        this.listener = listener;
    }

}
