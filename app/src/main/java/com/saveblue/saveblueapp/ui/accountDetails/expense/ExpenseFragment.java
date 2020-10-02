package com.saveblue.saveblueapp.ui.accountDetails.expense;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saveblue.saveblueapp.JwtHandler;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.adapters.ExpenseRecyclerAdapter;
import com.saveblue.saveblueapp.models.Expense;

import java.util.ArrayList;
import java.util.List;

public class ExpenseFragment extends Fragment {

    private ExpenseViewModel expenseViewModel;
    private ExpenseRecyclerAdapter expenseRecyclerAdapter;
    private List<Expense> expenseList = new ArrayList<>();

    private String accountID;

    public ExpenseFragment(String accountID) {
        this.accountID = accountID;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_expense, container, false);

        initUI(root);

        return root;
    }

    // initialise ui elements
    public void initUI(View view) {

        // initialise recycler view and its adapter
        RecyclerView recyclerView = view.findViewById(R.id.expenseRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        expenseRecyclerAdapter = new ExpenseRecyclerAdapter(getContext(), expenseList);
        recyclerView.setAdapter(expenseRecyclerAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialise viewmodel
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        observerSetup();

    }

    @Override
    public void onResume() {
        super.onResume();

        JwtHandler jwtHandler = new JwtHandler(getContext());
        String jwt = jwtHandler.getJwt();
        expenseViewModel.getExpenses(accountID, jwt);
    }

    // initialise observer for account list
    public void observerSetup() {
        //fetch jwt from dedicated handler class
        JwtHandler jwtHandler = new JwtHandler(getContext());
        String jwt = jwtHandler.getJwt();

        expenseViewModel.getExpenses(accountID, jwt).observe(getViewLifecycleOwner(), new Observer<List<Expense>>() {
            @Override
            public void onChanged(List<Expense> expenseList) {
                expenseRecyclerAdapter.setExpenseList(expenseList);
            }
        });
    }

}