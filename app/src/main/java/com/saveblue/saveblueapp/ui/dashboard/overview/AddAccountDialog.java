package com.saveblue.saveblueapp.ui.dashboard.overview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.api.SaveBlueAPI;
import com.saveblue.saveblueapp.api.ServiceGenerator;
import com.saveblue.saveblueapp.models.Account;
import com.saveblue.saveblueapp.models.RegisterUser;
import com.saveblue.saveblueapp.ui.login.RegisterDialog;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAccountDialog extends DialogFragment {

    private SaveBlueAPI api = ServiceGenerator.createService(SaveBlueAPI.class);
    private ConstraintLayout snackbarLayout;

    private EditText accountName;
    private TextInputLayout accountNameLayout;

    private NumberPicker startOfMonthPicker;

    private Button button;

    private Toolbar toolbar;

    private AddAccountDialogListener addAccountDialogListener;

    public static AddAccountDialog display(FragmentManager fragmentManager) {
        AddAccountDialog addAccountDialog = new AddAccountDialog();
        addAccountDialog.show(fragmentManager, "addAccountDialog");
        return addAccountDialog;
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
            Objects.requireNonNull(dialog.getWindow()).setWindowAnimations(R.style.AppTheme_SlideAnimation);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_add_account, container, false);

        toolbar = view.findViewById(R.id.toolbar);

        return view;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle("Create Account");

        snackbarLayout = view.findViewById(R.id.constraintLayout);

        initUI(view);
    }

    private void initUI(View view) {
        accountName = view.findViewById(R.id.accountName);
        accountNameLayout = view.findViewById(R.id.layoutAccountName);

        startOfMonthPicker = view.findViewById(R.id.startOfMonthPicker);
        startOfMonthPicker.setMaxValue(31);
        startOfMonthPicker.setMinValue(1);

        button = view.findViewById(R.id.createAccountButton);
        button.setOnClickListener(v -> {
            if (handleInputFields()) {
                sendNewAccountData();
            }
        });

        setTextListeners();
    }

    private void sendNewAccountData() {
        String accountNameStr = accountName.getText().toString();
        int startOfMonthInt = startOfMonthPicker.getValue();

        // Send user register data to activity
        addAccountDialogListener.sendNewAccountData(accountNameStr,startOfMonthInt);
        dismiss();
    }

    /*private void sendToActivity() {
        String usernameStr = accountName.getText().toString();
        int startOfMonthInt = Integer.parseInt(startOfMonth.getText().toString());

        // Send new account data to fragment and dismiss the dialog
        addAccountDialogListener.sendNewAccountData(usernameStr, startOfMonthInt);
        dismiss();
    }*/

    // --------------------------------------------------------
    // Text field handling
    // ---------------------------------------------------------

    private boolean handleInputFields() {
        boolean detectedError = false;

        if (Objects.requireNonNull(accountName.getText()).length() == 0) {
            accountNameLayout.setError(getString(R.string.fieldError));
            detectedError = true;
        }

        return !detectedError;
    }

    private void setTextListeners() {

        accountName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(accountName.getText()).length() > 0) {
                    accountNameLayout.setError(null);
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
            addAccountDialogListener = (AddAccountDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement AddAccountDialogListener");
        }
    }

    public interface AddAccountDialogListener {
        void sendNewAccountData(String accountName, int startOfMonth);
    }


}