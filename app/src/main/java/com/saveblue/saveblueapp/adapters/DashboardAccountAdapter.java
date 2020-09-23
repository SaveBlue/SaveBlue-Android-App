package com.saveblue.saveblueapp.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.models.Account;

import java.util.List;

public class DashboardAccountAdapter extends RecyclerView.Adapter<DashboardAccountAdapter.CardViewHolder> {
    private List<Account> accountList;
    private Context context;

    public DashboardAccountAdapter(Context context, List<Account> accountListlist){
        this.context = context;
        this.accountList = accountListlist;
    }

    public void setAccountsList(List<Account> accountsList){
        this.accountList = accountsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DashboardAccountAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_account_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardAccountAdapter.CardViewHolder holder, int position) {
        holder.accountTitle.setText(accountList.get(position).getName());
        holder.accountBalance.setText(accountList.get(position).getCurrentBalance());
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public TextView accountTitle;
        public TextView accountBalance;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            accountTitle = itemView.findViewById(R.id.AccountTitle);
            accountBalance = itemView.findViewById(R.id.AccountBalance);
        }
    }
}
