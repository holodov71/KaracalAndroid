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

public class ForgotPasswordDialog extends LogDialogFragment {

    public static final String DIALOG_TAG = "ForgotPasswordDialog";

    public interface ForgotPasswordDialogListener {
        void onReset(String email);
    }

    private ForgotPasswordDialogListener listener;

    private AlertDialog dialog;
    private TextInputLayout textInputLayout;
    private Button buttonReset;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
        dialog = builder.setTitle(R.string.enter_your_email)
                .setView(R.layout.dialog_forgot_password)
                .setPositiveButton(R.string.reset, (di, which) -> {
                    if (listener != null) {
                        listener.onReset(textInputLayout.getEditText().getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, (di, which) -> di.dismiss())
                .create();
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        textInputLayout = dialog.findViewById(R.id.textInputLayoutEmail);
        buttonReset = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        TextInputHelper.editTextObservable(textInputLayout).subscribe((s) -> buttonReset.setEnabled(!TextUtils.isEmpty(s)));
        textInputLayout.getEditText().setText("");
    }

    public void setListener(ForgotPasswordDialogListener listener) {
        this.listener = listener;
    }

}
