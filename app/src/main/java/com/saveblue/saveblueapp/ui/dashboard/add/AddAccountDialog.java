package com.saveblue.saveblueapp.ui.dashboard.add;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.saveblue.saveblueapp.R;

public class AddAccountDialog  extends AppCompatDialogFragment {

    private EditText editTextNameAddAccount;

    private EditText editTextBalanceAddAccount;

    private EditText editTextStartAddAccount;

    private AddAccountDialogListener addAccountDialogListener;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.register_dialog, null);

        builder.setView(view)
                .setTitle("Add Account")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String name = editTextNameAddAccount.getText().toString();
                        float balance = Float.parseFloat(editTextBalanceAddAccount.getText().toString());
                        int  start = Integer.parseInt(editTextStartAddAccount.getText().toString());

                        // Send user register data to activity
                        addAccountDialogListener.sendNewAccountData(name,balance,start);
                    }
                });

        editTextNameAddAccount = view.findViewById(R.id.editTextDateAddAccount);
        editTextBalanceAddAccount = view.findViewById(R.id.editTextBalanceAddAccount);
        editTextStartAddAccount = view.findViewById(R.id.editTextStartAddAccount);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            addAccountDialogListener = (AddAccountDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement ExampleDialogListener");
        }
    }

    public interface AddAccountDialogListener{
        void sendNewAccountData (String accountName, float accountBalance, int accountStart);
    }
}