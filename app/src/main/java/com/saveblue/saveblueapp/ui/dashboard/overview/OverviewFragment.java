package com.saveblue.saveblueapp.ui.dashboard.overview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saveblue.saveblueapp.JwtHandler;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.adapters.DashboardAccountAdapter;
import com.saveblue.saveblueapp.models.Account;

import java.util.ArrayList;
import java.util.List;


public class OverviewFragment extends Fragment implements AddAccountDialog.AddAccountDialogListener {

    private OverviewViewModel overviewViewModel;
    private DashboardAccountAdapter dashboardAccountAdapter;
    private JwtHandler jwtHandler;
    private List<Account> accountList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_overview, container, false);

        jwtHandler = new JwtHandler(getContext());

        initUI(root);

        return root;
    }

    // initialise ui elements
    public void initUI(View view) {

        // initialise recycler view and its adapter
        RecyclerView recyclerView = view.findViewById(R.id.accountRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        dashboardAccountAdapter = new DashboardAccountAdapter(getContext(), accountList, this::showAddAccountDialog);
        recyclerView.setAdapter(dashboardAccountAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialise viewmodel
        overviewViewModel = new ViewModelProvider(this).get(OverviewViewModel.class);
        observerSetup();

    }

    // fetch fresh account list
    @Override
    public void onResume() {
        super.onResume();

        //fetch jwt from dedicated handler class
        String jwt = jwtHandler.getJwt();
        String id = jwtHandler.getId();

        overviewViewModel.getAccounts(id, jwt);
    }

    // initialise observer for account list
    public void observerSetup() {
        //fetch jwt from dedicated handler class
        String jwt = jwtHandler.getJwt();
        String id = jwtHandler.getId();

        overviewViewModel.getAccounts(id, jwt).observe(getViewLifecycleOwner(), accountList -> dashboardAccountAdapter.setAccountsList(accountList));
    }


    // handle input data from dialog
    @Override
    public void sendNewAccountData(String accountName, int accountStart) {
        JwtHandler jwtHandler = new JwtHandler(getContext());
        String jwt = jwtHandler.getJwt();
        String userId = jwtHandler.getId();

        overviewViewModel.addNewAccount(jwt, userId, new Account(accountName, accountStart));
    }

    // display add account dialog
    public void showAddAccountDialog(){
        AddAccountDialog addAccountDialog = new AddAccountDialog();
        addAccountDialog.setTargetFragment(OverviewFragment.this, 420);
        addAccountDialog.show(getParentFragmentManager(), "add account dialog");
    }

}