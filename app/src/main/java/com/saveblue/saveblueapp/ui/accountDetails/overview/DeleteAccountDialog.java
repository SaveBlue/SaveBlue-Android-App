package com.saveblue.saveblueapp.ui.accountDetails.overview;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.ui.login.RegisterDialog;

import java.util.Objects;

public class DeleteAccountDialog extends AppCompatDialogFragment {

    private DeleteAccountListener deleteAccountListener;

    // Create dialog for deletion
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(getString(R.string.deleteDialogTitle));

        builder.setMessage(getString(R.string.deleteDialogMessage))
                .setPositiveButton(getString(R.string.deleteDialogYes), (dialog, id) -> deleteAccountListener.deleteAccountConfirm())
                .setNegativeButton(getString(R.string.deleteDialogNo), (dialog, id) -> {
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            deleteAccountListener = (DeleteAccountListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DeleteAccountListener");
        }
    }


    public interface DeleteAccountListener {
        void deleteAccountConfirm();
    }
}
