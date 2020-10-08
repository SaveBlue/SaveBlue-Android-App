package com.saveblue.saveblueapp.ui.login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputLayout;
import com.saveblue.saveblueapp.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RegisterDialog  extends DialogFragment {

    private EditText username;
    private TextInputLayout usernameLayout;

    private EditText email;
    private TextInputLayout emailLayout;

    private EditText password1;
    private TextInputLayout password1Layout;

    private EditText password2;
    private TextInputLayout password2Layout;


    private Toolbar toolbar;

    //private RegisterDialogListener registerDialogListener;


    public static RegisterDialog display(FragmentManager fragmentManager) {
        RegisterDialog registerDialog = new RegisterDialog();
        registerDialog.show(fragmentManager, "registerDialog");
        return registerDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_register, container, false);

        toolbar = view.findViewById(R.id.toolbar);

        return view;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle("Register");

    }

    private void initUI(View view){
        username = view.findViewById(R.id.username);
        usernameLayout = view.findViewById(R.id.layoutUsername);

        email = view.findViewById(R.id.email);
        emailLayout = view.findViewById(R.id.layoutEmail);

        password1 = view.findViewById(R.id.passwordRegister1);
        password1Layout = view.findViewById(R.id.layoutPass1);

        password2 = view.findViewById(R.id.passwordRegister2);
        password2Layout = view.findViewById(R.id.layoutPass2);
    }


    /*@NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_register, null);

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

        /*emailRegister = view.findViewById(R.id.emailRegister);
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
    }*/
}
