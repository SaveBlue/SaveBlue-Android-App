package com.saveblue.saveblueapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.models.Account;
import com.saveblue.saveblueapp.ui.accountDetails.AccountDetailsActivity;
import com.saveblue.saveblueapp.ui.dashboard.overview.OnAddAccountListener;

import java.util.List;
import java.util.Locale;

public class DashboardAccountAdapter extends RecyclerView.Adapter<DashboardAccountAdapter.CardViewHolder> {

    private List<Account> accountList;
    private final Context context;
    private final OnAddAccountListener addAccountListener;

    // Adapter init
    public DashboardAccountAdapter(Context context, List<Account> accountListlist, OnAddAccountListener addAccountListener) {
        this.context = context;
        this.accountList = accountListlist;
        this.addAccountListener = addAccountListener;
    }

    // Replace account list after api call
    public void setAccountsList(List<Account> accountsList) {
        this.accountList = accountsList;
        notifyDataSetChanged();
    }

    // Handles recycler view items content
    @NonNull
    @Override
    public DashboardAccountAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_dashboard_account, parent, false);

        if(viewType == R.layout.card_dashboard_account)
           view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_dashboard_account_add_button, parent, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardAccountAdapter.CardViewHolder holder, int position) {

       try {
           if (position == accountList.size()) {
            holder.addAccountButton.setOnClickListener(v -> addAccountListener.onClick());

           } else {
               holder.accountTitle.setText(accountList.get(position).getName());
               holder.accountBalance.setText(String.format(Locale.getDefault(), "%.2f €", accountList.get(position).getTotalBalance()));
               holder.accountDetailsButton.setOnClickListener(v -> {
                   Intent accountDetailsIntent = new Intent(context, AccountDetailsActivity.class);
                   accountDetailsIntent.putExtra("accountId", accountList.get(position).getId());
                   context.startActivity(accountDetailsIntent);
               });
           }
       }
       catch (Exception e){
           e.printStackTrace();
       }
    }

    @Override
    public int getItemCount() {
        return accountList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == accountList.size()) ? R.layout.card_dashboard_account : R.layout.card_dashboard_account_add_button;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public TextView accountTitle;
        public TextView accountBalance;
        public CardView addAccountButton;
        public CardView accountDetailsButton;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            accountTitle = itemView.findViewById(R.id.AccountTitle);
            accountBalance = itemView.findViewById(R.id.AccountBalance);
            addAccountButton = itemView.findViewById(R.id.addAccountButton);
            accountDetailsButton = itemView.findViewById(R.id.accountDetailsButton);
        }
    }
}
