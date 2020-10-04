package com.saveblue.saveblueapp.ui.accountDetails.income;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saveblue.saveblueapp.JwtHandler;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.adapters.ExpenseRecyclerAdapter;
import com.saveblue.saveblueapp.adapters.IncomeRecyclerAdapter;
import com.saveblue.saveblueapp.models.Income;

import java.util.ArrayList;
import java.util.List;

public class IncomeFragment extends Fragment {

    private IncomeViewModel incomeViewModel;
    private IncomeRecyclerAdapter incomeRecyclerAdapter;
    private List<Income> incomeList = new ArrayList<>();

    private String accountID;

    public IncomeFragment(String accountID) {
        this.accountID = accountID;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: rename fragment
        View root = inflater.inflate(R.layout.fragment_income, container, false);

        initUI(root);

        return root;
    }

    // initialise ui elements
    public void initUI(View view) {

        // initialise recycler view and its adapter
        // TODO: rename
        RecyclerView recyclerView = view.findViewById(R.id.expenseRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        incomeRecyclerAdapter = new IncomeRecyclerAdapter(getContext(), incomeList);
        recyclerView.setAdapter(incomeRecyclerAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialise viewmodel
        incomeViewModel = new ViewModelProvider(this).get(IncomeViewModel.class);
        observerSetup();

    }

    @Override
    public void onResume() {
        super.onResume();

        JwtHandler jwtHandler = new JwtHandler(getContext());
        String jwt = jwtHandler.getJwt();
        incomeViewModel.getIncomes(accountID, jwt);
    }

    // initialise observer for income list
    public void observerSetup() {
        //fetch jwt from dedicated handler class
        JwtHandler jwtHandler = new JwtHandler(getContext());
        String jwt = jwtHandler.getJwt();


        incomeViewModel.getIncomes(accountID, jwt).observe(getViewLifecycleOwner(), new Observer<List<Income>>() {
            @Override
            public void onChanged(List<Income> incomeList) {
                incomeRecyclerAdapter.setIncomeList(incomeList);
            }
        });
    }

}