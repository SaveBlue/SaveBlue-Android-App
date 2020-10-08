package com.saveblue.saveblueapp.ui.login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class RegisterDialog extends DialogFragment {

    private EditText username;
    private TextInputLayout usernameLayout;

    private EditText email;
    private TextInputLayout emailLayout;

    private EditText password1;
    private TextInputLayout password1Layout;

    private EditText password2;
    private TextInputLayout password2Layout;

    private Button button;


    private Toolbar toolbar;

    private RegisterDialogListener registerDialogListener;


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

        initUI(view);
    }

    private void initUI(View view) {
        username = view.findViewById(R.id.username);
        usernameLayout = view.findViewById(R.id.layoutUsername);

        email = view.findViewById(R.id.email);
        emailLayout = view.findViewById(R.id.layoutEmail);

        password1 = view.findViewById(R.id.passwordRegister1);
        password1Layout = view.findViewById(R.id.layoutPass1);

        password2 = view.findViewById(R.id.passwordRegister2);
        password2Layout = view.findViewById(R.id.layoutPass2);

        button = view.findViewById(R.id.registerButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handleInputFields()) {
                    sendToActivity();
                    dismiss();
                }
            }
        });

        setTextListeners();
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

    public interface RegisterDialogListener {
        void sendRegisterData(String emailRegister, String usernameRegister, String passwordRegister);
    }

    private boolean handleInputFields() {
        boolean detectedError = false;

        if (Objects.requireNonNull(username.getText()).length() == 0) {
            usernameLayout.setError(getString(R.string.fieldError));
            detectedError = true;
        }

        if (Objects.requireNonNull(email.getText()).length() == 0) {
            emailLayout.setError(getString(R.string.fieldError));
            detectedError = true;
        }

        if (Objects.requireNonNull(password1.getText()).length() == 0) {
            password1Layout.setError(getString(R.string.fieldError));
            detectedError = true;
        }

        if (Objects.requireNonNull(password2.getText()).length() == 0) {
            password2Layout.setError(getString(R.string.fieldError));
            detectedError = true;
        }

        if (!Objects.requireNonNull(password2.getText().toString()).equals(password1.getText().toString())) {
            password2Layout.setError(getString(R.string.matchError));
            detectedError = true;
        }


        return !detectedError;
    }

    private void sendToActivity() {
        String emailStr = email.getText().toString();
        String usernameStr = username.getText().toString();
        String password = password1.getText().toString();

        // Send user register data to activity
        registerDialogListener.sendRegisterData(emailStr, usernameStr, password);
    }

    private void setTextListeners() {

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(username.getText()).length() == 0) {
                    usernameLayout.setError(getString(R.string.fieldError));
                } else {
                    usernameLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(email.getText()).length() == 0) {
                    emailLayout.setError(getString(R.string.fieldError));
                } else {
                    emailLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(password1.getText()).length() == 0) {
                    password1Layout.setError(getString(R.string.fieldError));
                } else {
                    password1Layout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(password2.getText()).length() == 0) {
                    password2Layout.setError(getString(R.string.fieldError));
                } else {
                    password2Layout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
