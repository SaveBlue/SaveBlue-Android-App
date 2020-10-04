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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayoutMediator;
import com.saveblue.saveblueapp.JwtHandler;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.adapters.SectionsPagerAdapter;
import com.saveblue.saveblueapp.animations.ViewAnimation;
import com.saveblue.saveblueapp.models.Account;
import com.saveblue.saveblueapp.models.Income;
import com.saveblue.saveblueapp.ui.accountDetails.overview.AccountOverviewFragment;
import com.saveblue.saveblueapp.ui.accountDetails.overview.AccountOverviewViewModel;
import com.saveblue.saveblueapp.ui.accountDetails.overview.DeleteAccountDialog;
import com.saveblue.saveblueapp.ui.accountDetails.overview.EditAccountDialog;
import com.saveblue.saveblueapp.ui.addExpenseIncome.AddExpenseActivity;
import com.saveblue.saveblueapp.ui.addExpenseIncome.AddIncomeActivity;
import com.saveblue.saveblueapp.ui.dashboard.overview.OverviewViewModel;
import com.saveblue.saveblueapp.ui.dashboard.profile.EditProfileActivity;

import java.util.List;
import java.util.Objects;

public class AccountDetailsActivity extends AppCompatActivity {

    /* The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager2 viewPager;
    private Toolbar toolbar;
    private AccountOverviewViewModel accountOverviewViewModel;

    //The pager adapter, which provides the pages to the view pager widget.
    private FragmentStateAdapter sectionsPagerAdapter;


    private String accountId;

    private boolean rotatedFAB = false;
    private JwtHandler jwtHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        accountId = getIntent().getStringExtra("accountId");

        accountOverviewViewModel = new ViewModelProvider(this).get(AccountOverviewViewModel.class);
        jwtHandler = new JwtHandler(getApplicationContext());

        initUI();

        initFAB();

        observerSetup();
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
            intentAddIncome.putExtra("Task", "ADD");
            intentAddIncome.putExtra("CallingActivity", "Details");
            intentAddIncome.putExtra("BaseAccountID", accountId);
            startActivity(intentAddIncome);
        });

        // Expenses Activity
        fabExpense.setOnClickListener(v -> {
            Intent intentAddExpense = new Intent(getApplicationContext(), AddExpenseActivity.class);
            intentAddExpense.putExtra("Task", "ADD");
            intentAddExpense.putExtra("CallingActivity", "Details");
            intentAddExpense.putExtra("BaseAccountID", accountId);
            startActivity(intentAddExpense);
        });
    }


    public void initUI(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Account Details");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            // back button
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

                    // TODO: remove
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
    protected void onRestart() {
        super.onRestart();

        accountOverviewViewModel.getAccount(accountId, jwtHandler.getJwt());
    }

    // initialise observer for account list
    public void observerSetup() {
        accountOverviewViewModel.getAccount(accountId, jwtHandler.getJwt()).observe(this, new Observer<Account>() {
            @Override
            public void onChanged(Account account) {
                toolbar.setTitle(account.getName());
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // Back button
        if (id == android.R.id.home) {
            finish();
        }

        // Edit button
        if (id == R.id.action_item_edit_account){
            Intent editAccountIntent = new Intent(getApplicationContext(), EditAccountActivity.class);
            editAccountIntent.putExtra("accountID", accountId);
            startActivity(editAccountIntent);
        }

        if (id == R.id.item_delete_account){
            DeleteAccountDialog deleteAccountDialog = new DeleteAccountDialog();
            deleteAccountDialog.show(getSupportFragmentManager(), "remove account dialog");
        }

        return super.onOptionsItemSelected(item);
    }

}