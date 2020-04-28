package app.karacal.dialogs;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import app.karacal.R;
import apps.in.android_logger.LogDialogFragment;

public class DeleteAccountDialog extends LogDialogFragment {

    public static final String DIALOG_TAG = "DeleteAccountDialog";

    public interface DeleteAccountListener{
        void onDelete();
    }

    private DeleteAccountListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
        AlertDialog dialog = builder.setTitle(R.string.delete_account)
                .setView(R.layout.dialog_delete_account)
                .setPositiveButton(R.string.delete, (di, which) -> {
                    if (listener != null) {
                        listener.onDelete();
                    }
                })
                .setNegativeButton(R.string.cancel, (di, which) -> di.dismiss())
                .create();
        return dialog;
    }

    public void setListener(DeleteAccountListener listener) {
        this.listener = listener;
    }
}
