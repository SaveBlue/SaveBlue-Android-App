package com.saveblue.saveblueapp.ui.login;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.RegisterUser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterDialog extends DialogFragment {
    private SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);
    private ConstraintLayout snackbarLayout;

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

        snackbarLayout = view.findViewById(R.id.constraintLayout);

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
        button.setOnClickListener(v -> {
            if (handleInputFields()) {
                sendToRegister();
            }
        });

        setTextListeners();
    }

    private void sendToRegister() {
        String emailStr = email.getText().toString();
        String usernameStr = username.getText().toString();
        String passwordStr = password1.getText().toString();

        register(emailStr, usernameStr, passwordStr);
    }

    private void sendToActivity() {
        String usernameStr = username.getText().toString();
        String passwordStr = password1.getText().toString();

        // Send user register data to activity and dismiss the dialog
        registerDialogListener.sendRegisterData(usernameStr, passwordStr);
        dismiss();
    }

    // --------------------------------------------------------
    // Text field handling
    // ---------------------------------------------------------

    private boolean handleInputFields() {
        boolean detectedError = false;

        if (Objects.requireNonNull(username.getText()).length() == 0) {
            usernameLayout.setError(getString(R.string.fieldError));
            detectedError = true;
        }

        Pattern emailRegex = Pattern.compile("(?:[a-z0-9!#$%&'+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])");
        if (!emailRegex.matcher(Objects.requireNonNull(email.getText().toString())).matches()) {
            emailLayout.setError(getString(R.string.emailError));
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

    private void setTextListeners() {

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(username.getText()).length() > 0) {
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
                if (Objects.requireNonNull(email.getText()).length() > 0) {
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
                if (Objects.requireNonNull(password1.getText()).length() > 0) {
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
                if (Objects.requireNonNull(password2.getText()).length() > 0) {
                    password2Layout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    // --------------------------------------------------------
    // Dialog methods to send to Activity
    // ---------------------------------------------------------

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
        void sendRegisterData(String usernameRegister, String passwordRegister);
    }


    // --------------------------------------------------------
    // API calls
    // ---------------------------------------------------------

    public void register(String email, String username, String password) {

        RegisterUser registerUser = new RegisterUser(email, username, password);

        Call<ResponseBody> callRegisterUser = api.registerUser(registerUser);

        callRegisterUser.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (!response.isSuccessful() && response.code() != 409) {

                    Snackbar.make(snackbarLayout, "Error registering :(", Snackbar.LENGTH_LONG)
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                    return;
                }
                // handle displaying duplicate user data
                else if (response.code() == 409) {
                    try {
                        String responseMessage = response.errorBody().string();

                        if (responseMessage.contains("username")) {
                            usernameLayout.setError(getString(R.string.registerUsernameError));
                        } else if (responseMessage.contains("email")) {
                            emailLayout.setError(getString(R.string.registerEmailError));
                        }

                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                sendToActivity();
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Snackbar.make(snackbarLayout, "Can't connect to server :(", Snackbar.LENGTH_LONG)
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
            }
        });
    }

}
