package com.saveblue.saveblueapp.ui.dashboard.overview;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
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

import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.adapters.DashboardAccountAdapter;
import com.saveblue.saveblueapp.models.Account;

import java.util.ArrayList;
import java.util.List;


public class OverviewFragment extends Fragment {

    private OverviewViewModel overviewViewModel;
    private DashboardAccountAdapter dashboardAccountAdapter;
    private List<Account> accountList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        overviewViewModel = ViewModelProviders.of(this).get(OverviewViewModel.class);
        View root = inflater.inflate(R.layout.fragment_overview, container, false);

        initUI(root);

        return root;
    }

    // initialise ui elements
    public void initUI(View view) {

        // initialise recycler view and its adapter
        RecyclerView recyclerView = view.findViewById(R.id.accountRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        dashboardAccountAdapter = new DashboardAccountAdapter(getContext(), accountList);
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
        //fetch and decode jwt
        String jwt = getJWTfromSharedPref();
        String id = getIdFromJWT(jwt);

        overviewViewModel.getAccounts(id, jwt).observe(getViewLifecycleOwner(), new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accountList) {
                dashboardAccountAdapter.setAccountsList(accountList);
            }
        });
    }

    /**
     * JWT functions
     */

    // gets jwt from shared preferences
    public String getJWTfromSharedPref() {
        SharedPreferences sharedPref = requireContext().getSharedPreferences("SaveBluePref", 0);

        return sharedPref.getString("JWT", "");
    }

    // decode id from jwt
    public String getIdFromJWT(String jwt) {

        String jwtPayload = jwt.split("\\.")[1];
        String body = new String(Base64.decode(jwtPayload, Base64.URL_SAFE));

        return body.split("\"")[3];
    }
}