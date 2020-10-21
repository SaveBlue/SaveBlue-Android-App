package com.saveblue.saveblueapp.adapters;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.TimestampHandler;
import com.saveblue.saveblueapp.animations.AdapterAnimations;
import com.saveblue.saveblueapp.animations.ViewAnimation;
import com.saveblue.saveblueapp.models.Expense;
import com.saveblue.saveblueapp.ui.addExpenseIncome.AddExpenseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseRecyclerAdapter extends RecyclerView.Adapter<ExpenseRecyclerAdapter.CardViewHolder> {

    private List<Expense> expenseList;
    private Context context;

    public ExpenseRecyclerAdapter(Context context, List<Expense> expenseList) {
        this.context = context;
        this.expenseList = expenseList;
    }

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

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {

        holder.name.setText(expenseList.get(position).getName());
        holder.description.setText(expenseList.get(position).getDescription());
        holder.date.setText(TimestampHandler.parseMongoTimestamp(expenseList.get(position).getDate()));
        holder.amount.setText(String.valueOf(expenseList.get(position).getAmount()) + " €");

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.expandable.getVisibility() == View.VISIBLE) {
                    holder.expandable.setVisibility(View.GONE);
                    AdapterAnimations.toggleArrow(holder.arrow, false);
                } else {
                    holder.expandable.setVisibility(View.VISIBLE);
                    AdapterAnimations.toggleArrow(holder.arrow, true);
                }
            }
        });

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEditExpense = new Intent(context, AddExpenseActivity.class);
                intentEditExpense.putExtra("Task", "EDIT");
                intentEditExpense.putExtra("ExpenseID", expenseList.get(position).getId());
                context.startActivity(intentEditExpense);
            }
        });
    }


    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public  class CardViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView amount;
        public TextView date;
        public TextView description;
        public CardView card;
        public ConstraintLayout expandable;
        public ImageView arrow;
        public Button editButton;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.ExpenseIncomeName);
            amount = itemView.findViewById(R.id.ExpenseIncomeAmount);
            date = itemView.findViewById(R.id.ExpenseIncomeDate);
            description = itemView.findViewById(R.id.ExpenseIncomeDescription);
            card = itemView.findViewById(R.id.cardExpenseIncome);
            expandable = itemView.findViewById(R.id.cardDetails);
            arrow = itemView.findViewById(R.id.profileArrow);
            editButton = itemView.findViewById(R.id.buttonEditExpenseIncome);

            // required for transition animation
            LayoutTransition layoutTransition = card.getLayoutTransition();
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        }

    }

}
