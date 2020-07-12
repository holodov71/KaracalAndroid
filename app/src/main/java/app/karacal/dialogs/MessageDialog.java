package app.karacal.dialogs;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import app.karacal.R;
import apps.in.android_logger.LogDialogFragment;

public class MessageDialog extends LogDialogFragment {

    public static final String DIALOG_TAG = "MessageDialog";

    public interface ActionListener{
        void onActionClicked();
    }

    private ActionListener listener;
    private String message;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
        AlertDialog dialog = builder.setTitle(R.string.attention)
                .setMessage(message)
                .setPositiveButton(R.string.ok, (di, which) -> {
                    if (listener != null) {
                        listener.onActionClicked();
                    }
                    dismiss();
                })
                .create();
        return dialog;
    }

    public MessageDialog setListener(ActionListener listener) {
        this.listener = listener;
        return this;
    }

    public MessageDialog setMessage(String message) {
        this.message = message;
        return this;
    }
}
