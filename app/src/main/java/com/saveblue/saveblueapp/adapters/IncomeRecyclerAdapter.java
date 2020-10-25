package com.saveblue.saveblueapp.adapters;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
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
import com.saveblue.saveblueapp.models.Income;
import com.saveblue.saveblueapp.ui.addExpenseIncome.AddIncomeActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        holder.cat1.setText(incomeList.get(position).getCategory1());
        holder.cat2.setVisibility(View.GONE);
        holder.description.setText(incomeList.get(position).getDescription());
        holder.date.setText(TimestampHandler.parseMongoTimestamp(incomeList.get(position).getDate()));
        holder.amount.setText(String.valueOf(incomeList.get(position).getAmount()) + " €");

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.expandable.getVisibility() == View.VISIBLE) {
                    holder.expandable.setVisibility(View.GONE);
                    AdapterAnimations.toggleArrow(holder.arrow, false);
                } else {
                    holder.expandable.setVisibility(View.VISIBLE);
                    AdapterAnimations.toggleArrow(holder.arrow, false);
                }
            }
        });

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEditIncome = new Intent(context, AddIncomeActivity.class);
                intentEditIncome.putExtra("Task", "EDIT");
                intentEditIncome.putExtra("IncomeID", incomeList.get(position).getId());
                context.startActivity(intentEditIncome);
            }
        });
    }


    @Override
    public int getItemCount() {
        return incomeList.size();
    }

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

            // required for transition animation
            LayoutTransition layoutTransition = card.getLayoutTransition();
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        }
    }

}
