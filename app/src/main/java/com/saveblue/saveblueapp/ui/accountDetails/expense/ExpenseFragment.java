package com.saveblue.saveblueapp.ui.accountDetails.expense;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private final String accountID;

    private TextView noExpenses;

    public ExpenseFragment(String accountID) {
        this.accountID = accountID;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_expense, container, false);

        initUI(root);

        return root;
    }

    // Initialise ui elements
    public void initUI(View view) {

        // Initialise recycler view and its adapter
        RecyclerView recyclerView = view.findViewById(R.id.expenseRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        expenseRecyclerAdapter = new ExpenseRecyclerAdapter(getContext(), expenseList);
        recyclerView.setAdapter(expenseRecyclerAdapter);

        // Init "no expenses" text
        noExpenses = view.findViewById(R.id.no_expenses);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialise viewmodel
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

    // Initialise observer for account list
    public void observerSetup() {

        // Fetch jwt from dedicated handler class
        JwtHandler jwtHandler = new JwtHandler(getContext());
        String jwt = jwtHandler.getJwt();

        expenseViewModel.getExpenses(accountID, jwt).observe(getViewLifecycleOwner(), expenseList -> {
            expenseRecyclerAdapter.setExpenseList(expenseList);

            // Show/hide "no incomes" text
            if (expenseRecyclerAdapter.getItemCount()>0){
                noExpenses.setVisibility(View.GONE);
            }
            else{
                noExpenses.setVisibility(View.VISIBLE);
            }
        });
    }
}
