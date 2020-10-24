package com.saveblue.saveblueapp.ui.dashboard.overview;

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
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.saveblue.saveblueapp.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AddAccountDialog extends DialogFragment {

    private EditText accountName;
    private TextInputLayout accountNameLayout;

    private NumberPicker startOfMonthPicker;

    private Toolbar toolbar;

    private AddAccountDialogListener addAccountDialogListener;

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
        toolbar.setTitle(getString(R.string.addAccDialogTitle));

        initUI(view);
    }

    // initialise UI elements
    private void initUI(View view) {
        accountName = view.findViewById(R.id.accountName);
        accountNameLayout = view.findViewById(R.id.layoutAccountName);

        startOfMonthPicker = view.findViewById(R.id.startOfMonthPicker);
        startOfMonthPicker.setMaxValue(31);
        startOfMonthPicker.setMinValue(1);

        Button button = view.findViewById(R.id.createAccountButton);
        button.setOnClickListener(v -> {
            if (handleInputFields()) {
                sendNewAccountData();
            }
        });

        setTextListeners();
    }

    // send input field data to fragment for api call
    private void sendNewAccountData() {
        String accountNameStr = accountName.getText().toString();
        int startOfMonthInt = startOfMonthPicker.getValue();

        // Send user register data to activity
        addAccountDialogListener.sendNewAccountData(accountNameStr,startOfMonthInt);
        dismiss();
    }

    // --------------------------------------------------------
    // Text field handling
    // ---------------------------------------------------------

    // handle input field correctness
    private boolean handleInputFields() {
        boolean detectedError = false;

        if (Objects.requireNonNull(accountName.getText()).length() == 0) {
            accountNameLayout.setError(getString(R.string.fieldError));
            detectedError = true;
        }

        return !detectedError;
    }

    // clears error message from text fields
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