package com.saveblue.saveblueapp.ui.accountDetails;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.View;

import com.google.android.material.tabs.TabLayoutMediator;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.adapters.SectionsPagerAdapter;
import com.saveblue.saveblueapp.models.Account;

public class AccountDetailsActivity extends AppCompatActivity {

    /* The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager2 viewPager;

    //The pager adapter, which provides the pages to the view pager widget.
    private FragmentStateAdapter sectionsPagerAdapter;


    private String accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        accountId = getIntent().getStringExtra("accountId");

        initUI();
    }


    public void initUI(){

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
            }

        }).attach();


        // TODO : Lan change
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
}