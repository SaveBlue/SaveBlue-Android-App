package com.saveblue.saveblueapp.adapters;

import android.content.Context;
import android.net.VpnService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.models.Income;

import java.util.List;

public class IncomeRecyclerAdapter extends RecyclerView.Adapter<IncomeRecyclerAdapter.CardViewHolder>{
    private List<Income> incomeList;
    private Context context;

    public IncomeRecyclerAdapter(Context context, List<Income> incomeList) {
        this.context = context;
        this.incomeList = incomeList;
    }

    public void setIncomeList(List<Income> incomeList) {
        this.incomeList = incomeList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_expense_income, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {

        holder.name.setText(incomeList.get(position).getName());
        holder.description.setText(incomeList.get(position).getDescription());
        holder.date.setText(incomeList.get(position).getDate());
        holder.amount.setText(String.valueOf(incomeList.get(position).getAmount()) + " €");

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.expandable.getVisibility() == View.VISIBLE) {
                    holder.expandable.setVisibility(View.GONE);
                    holder.arrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                } else {
                    holder.expandable.setVisibility(View.VISIBLE);
                    holder.arrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                }
            }
        });

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return incomeList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView amount;
        public TextView date;
        public TextView description;
        public CardView card;
        public LinearLayout expandable;
        public ImageView arrow;
        public Button editButton;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.ExpenseIncomeName);
            amount = itemView.findViewById(R.id.ExpenseIncomeAmount);
            date = itemView.findViewById(R.id.ExpenseIncomeDate);
            description = itemView.findViewById(R.id.ExpenseIncomeDescription);
            card = itemView.findViewById(R.id.cardExpenseIncomeButton);
            expandable = itemView.findViewById(R.id.descriptionView);
            arrow = itemView.findViewById(R.id.expenseIncomeExpand);
            editButton = itemView.findViewById(R.id.buttonEditExpenseIncome);
        }
    }
}
