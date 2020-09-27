package com.saveblue.saveblueapp.ui.dashboard.overview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
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
    private List<Account> accountList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_overview, container, false);

        initUI(root);

        return root;
    }

    // initialise ui elements
    public void initUI(View view) {

        // initialise recycler view and its adapter
        RecyclerView recyclerView = view.findViewById(R.id.accountRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        dashboardAccountAdapter = new DashboardAccountAdapter(getContext(), accountList, new OnAddAccountListener() {
            @Override
            public void onClick() {
                showAddAccountDialog();
            }
        });
        recyclerView.setAdapter(dashboardAccountAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialise viewmodel
        overviewViewModel = new ViewModelProvider(this).get(OverviewViewModel.class);
        observerSetup();

    }

    // initialise observer for account list
    public void observerSetup() {
        //fetch jwt from dedicated handler class
        JwtHandler jwtHandler = new JwtHandler(getContext());
        String jwt = jwtHandler.getJwt();
        String id = jwtHandler.getId();

        overviewViewModel.getAccounts(id, jwt).observe(getViewLifecycleOwner(), new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accountList) {
                dashboardAccountAdapter.setAccountsList(accountList);
            }
        });
    }


    //dialog
    @Override
    public void sendNewAccountData(String accountName, float accountBalance, int accountStart) {
        JwtHandler jwtHandler = new JwtHandler(getContext());
        String jwt = jwtHandler.getJwt();
        String userId = jwtHandler.getId();

        overviewViewModel.addNewAccount(jwt, userId, new Account(accountName, accountBalance, accountStart));
    }


    public void showAddAccountDialog(){
        AddAccountDialog addAccountDialog = new AddAccountDialog();
        addAccountDialog.setTargetFragment(OverviewFragment.this, 420);
        addAccountDialog.show(getFragmentManager(), "add account dialog");
    }

}