package com.saveblue.saveblueapp.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.saveblue.saveblueapp.R;

public class RegisterDialog  extends AppCompatDialogFragment {

    private EditText emailRegister;

    private EditText usernameRegister;

    private EditText passwordRegister;

    private RegisterDialogListener registerDialogListener;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.register_dialog, null);

        builder.setView(view)
                .setTitle("Register")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String email = emailRegister.getText().toString();
                        String username = usernameRegister.getText().toString();
                        String password = passwordRegister.getText().toString();

                        // Send user register data to activity
                        registerDialogListener.sendRegisterData(email,username,password);
                    }
                });

        emailRegister = view.findViewById(R.id.emailRegister);
        usernameRegister = view.findViewById(R.id.usernameRegister);
        passwordRegister = view.findViewById(R.id.passwordRegister);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            registerDialogListener = (RegisterDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement ExampleDialogListener");
        }
    }

    public interface RegisterDialogListener{
        void sendRegisterData (String emailRegister, String usernameRegister, String passwordRegister);
    }
}
