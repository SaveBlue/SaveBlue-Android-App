package com.saveblue.saveblueapp.adapters;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.TimestampHandler;
import com.saveblue.saveblueapp.animations.AdapterAnimations;
import com.saveblue.saveblueapp.models.Expense;
import com.saveblue.saveblueapp.ui.addExpenseIncome.AddExpenseActivity;

import java.util.List;
import java.util.Locale;

public class ExpenseRecyclerAdapter extends RecyclerView.Adapter<ExpenseRecyclerAdapter.CardViewHolder> {

    private List<Expense> expenseList;
    private final Context context;

    // Adapter init
    public ExpenseRecyclerAdapter(Context context, List<Expense> expenseList) {
        this.context = context;
        this.expenseList = expenseList;
    }

    // Replace expense list after api call
    public void setExpenseList(List<Expense> expenseList) {
        this.expenseList = expenseList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_expense_income, parent, false);
        return new CardViewHolder(view);
    }

    // Handles recycler view items content
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {

        holder.cat1.setText(expenseList.get(position).getCategory1());
        holder.cat2.setText(expenseList.get(position).getCategory2());
        holder.date.setText(TimestampHandler.parseMongoTimestamp(expenseList.get(position).getDate()));
        holder.amount.setText(String.format(Locale.getDefault(), "%.2f €", expenseList.get(position).getAmount()));
        holder.description.setText(expenseList.get(position).getDescription());

        // Card expand and collapse
        holder.card.setOnClickListener(v -> {
            if (holder.expandable.getVisibility() == View.VISIBLE) {
                holder.expandable.setVisibility(View.GONE);
                AdapterAnimations.toggleArrow(holder.arrow, false);
            } else {
                holder.expandable.setVisibility(View.VISIBLE);
                AdapterAnimations.toggleArrow(holder.arrow, true);
            }
        });

        // Edit button
        holder.editButton.setOnClickListener(v -> {
            Intent intentEditExpense = new Intent(context, AddExpenseActivity.class);
            intentEditExpense.putExtra("Task", "EDIT");
            intentEditExpense.putExtra("ExpenseID", expenseList.get(position).getId());
            context.startActivity(intentEditExpense);
        });
    }


    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    // View holder for each recycler view element
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public TextView cat1;
        public TextView cat2;
        public TextView amount;
        public TextView date;
        public TextView description;
        public CardView card;
        public ConstraintLayout expandable;
        public ImageView arrow;
        public Button editButton;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cat1 = itemView.findViewById(R.id.ExpenseIncomeCat1);
            cat2 = itemView.findViewById(R.id.ExpenseIncomeCat2);
            amount = itemView.findViewById(R.id.ExpenseIncomeAmount);
            date = itemView.findViewById(R.id.ExpenseIncomeDate);
            description = itemView.findViewById(R.id.ExpenseIncomeDescription);
            card = itemView.findViewById(R.id.cardExpenseIncome);
            expandable = itemView.findViewById(R.id.cardDetails);
            arrow = itemView.findViewById(R.id.profileArrow);
            editButton = itemView.findViewById(R.id.buttonEditExpenseIncome);

            // Required for transition animation
            LayoutTransition layoutTransition = card.getLayoutTransition();
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        }

    }

}
