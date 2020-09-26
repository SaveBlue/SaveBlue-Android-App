package com.saveblue.saveblueapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.models.Account;
import com.saveblue.saveblueapp.ui.dashboard.DashboardActivity;
import com.saveblue.saveblueapp.ui.dashboard.add.AddAccountDialog;
import com.saveblue.saveblueapp.ui.dashboard.add.OnAddAccountListener;

import java.util.List;

public class DashboardAccountAdapter extends RecyclerView.Adapter<DashboardAccountAdapter.CardViewHolder> {
    private List<Account> accountList;
    private Context context;
    private OnAddAccountListener addAccountListener;

    public DashboardAccountAdapter(Context context, List<Account> accountListlist, OnAddAccountListener addAccountListener) {
        this.context = context;
        this.accountList = accountListlist;
        this.addAccountListener = addAccountListener;
    }

    public void setAccountsList(List<Account> accountsList) {
        this.accountList = accountsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DashboardAccountAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_account_card, parent, false);

        if(viewType == R.layout.dashboard_account_card)
           view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_account_add_button, parent, false);


        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardAccountAdapter.CardViewHolder holder, int position) {

       try {
           if (position == accountList.size()) {
            holder.addAccountButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Button Clicked", Toast.LENGTH_LONG).show();

                    addAccountListener.onClick();
                }
            });

           } else {

               holder.accountTitle.setText(accountList.get(position).getName());
               holder.accountBalance.setText(String.valueOf(accountList.get(position).getCurrentBalance()) + " €");
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
        return (position == accountList.size()) ? R.layout.dashboard_account_card : R.layout.dashboard_account_add_button;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public TextView accountTitle;
        public TextView accountBalance;
        public CardView addAccountButton;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            accountTitle = itemView.findViewById(R.id.AccountTitle);
            accountBalance = itemView.findViewById(R.id.AccountBalance);
            addAccountButton = itemView.findViewById(R.id.addAccountButton);
        }
    }
}
