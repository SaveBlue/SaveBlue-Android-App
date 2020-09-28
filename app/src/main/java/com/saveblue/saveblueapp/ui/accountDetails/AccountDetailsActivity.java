package com.saveblue.saveblueapp.ui.accountDetails;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.MenuItem;
import android.view.View;

import com.google.android.material.tabs.TabLayoutMediator;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.adapters.SectionsPagerAdapter;
import com.saveblue.saveblueapp.animations.ViewAnimation;
import com.saveblue.saveblueapp.models.Account;
import com.saveblue.saveblueapp.ui.addExpenseIncome.AddExpenseActivity;
import com.saveblue.saveblueapp.ui.addExpenseIncome.AddIncomeActivity;

public class AccountDetailsActivity extends AppCompatActivity {

    /* The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager2 viewPager;

    //The pager adapter, which provides the pages to the view pager widget.
    private FragmentStateAdapter sectionsPagerAdapter;


    private String accountId;

    private boolean rotatedFAB = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        accountId = getIntent().getStringExtra("accountId");

        initUI();

        initFAB();
    }

    private void initFAB() {
        //FAB-related
        FloatingActionButton fab = findViewById(R.id.fab);
        FloatingActionButton fabIncome = findViewById(R.id.fabIncome);
        FloatingActionButton fabExpense = findViewById(R.id.fabExpense);

        // Handle animations for FAB
        ViewAnimation.init(findViewById(R.id.fabIncome));
        ViewAnimation.init(findViewById(R.id.fabExpense));

        // Set onClickListeners for FABs
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotatedFAB = ViewAnimation.rotateFab(view,!rotatedFAB);
                if(rotatedFAB){
                    // TODO: bug on first touch
                    ViewAnimation.showIn(findViewById(R.id.fabIncome));
                    ViewAnimation.showIn(findViewById(R.id.fabExpense));
                }else{
                    ViewAnimation.showOut(findViewById(R.id.fabIncome));
                    ViewAnimation.showOut(findViewById(R.id.fabExpense));
                }
            }
        });

        // Incomes Activity
        fabIncome.setOnClickListener(v -> {
            Intent intentAddIncome = new Intent(getApplicationContext(), AddIncomeActivity.class);
            startActivity(intentAddIncome);
        });

        // Expenses Activity
        fabExpense.setOnClickListener(v -> {
            Intent intentAddExpense = new Intent(getApplicationContext(), AddExpenseActivity.class);
            startActivity(intentAddExpense);
        });
    }


    public void initUI(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Account Details");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        viewPager = findViewById(R.id.view_pager);
        sectionsPagerAdapter = new SectionsPagerAdapter(this, accountId);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position){
                case 0:
                    String titleOverview = "Overview";
                    tab.setText(titleOverview.toUpperCase());
                    break;
                case 1:
                    String titleExpenses = "Expenses";
                    tab.setText(titleExpenses.toUpperCase());
                    break;
                case 2:
                    String titleIncomes = "Incomes";
                    tab.setText(titleIncomes.toUpperCase());
                    break;

                case 3:
                    String titleBudgets = "Budgets/Goals";
                    tab.setText(titleBudgets.toUpperCase());
                    break;

                /*case 4:
                    String titleGoals = "Goals";
                    tab.setText(titleGoals.toUpperCase());
                    break;*/
            }

        }).attach();

    }

   @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first fragment, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous fragment.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}