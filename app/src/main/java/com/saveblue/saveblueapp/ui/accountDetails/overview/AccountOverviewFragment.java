package com.saveblue.saveblueapp.ui.accountDetails.overview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.saveblue.saveblueapp.JwtHandler;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.models.Account;
import com.saveblue.saveblueapp.models.Income;
import com.saveblue.saveblueapp.ui.accountDetails.income.IncomeViewModel;
import com.saveblue.saveblueapp.ui.dashboard.overview.AddAccountDialog;
import com.saveblue.saveblueapp.ui.dashboard.overview.OverviewFragment;

import java.util.List;
import java.util.Locale;

public class AccountOverviewFragment extends Fragment {

    private AccountOverviewViewModel accountOverviewViewModel;
    private Account account;

    private TextView availableBalance;
    private TextView totalBalance;
    private TextView startOfMonth;

    private String accountID;

    public AccountOverviewFragment(String accountID) {
        this.accountID = accountID;
    }

    JwtHandler jwtHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_account_overview, container, false);

        jwtHandler = new JwtHandler(getContext());

        initUI(root);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialise viewmodel
        accountOverviewViewModel = new ViewModelProvider(this).get(AccountOverviewViewModel.class);
        observerSetup();

    }

    @Override
    public void onResume() {
        super.onResume();

        String jwt = jwtHandler.getJwt();
        accountOverviewViewModel.getAccount(accountID, jwt);
    }

    private void initUI(View view) {

        // Init text views
        availableBalance = view.findViewById(R.id.availableBalance);
        totalBalance = view.findViewById(R.id.totalBalance);
        startOfMonth = view.findViewById(R.id.accountStart);
    }

    // Initialise observer for account list
    public void observerSetup() {

        // Fetch jwt from dedicated handler class
        String jwt = jwtHandler.getJwt();

        accountOverviewViewModel.getAccount(accountID, jwt).observe(getViewLifecycleOwner(), account -> {
            availableBalance.setText(String.format(Locale.getDefault(), "%.2f €", account.getAvailableBalance()));
            totalBalance.setText(String.format(Locale.getDefault(), "%.2f €", account.getTotalBalance()));
            startOfMonth.setText(String.valueOf(account.getStartOfMonth()));

        });

    }

}
