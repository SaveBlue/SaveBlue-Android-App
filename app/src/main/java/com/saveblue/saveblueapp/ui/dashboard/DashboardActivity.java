package com.saveblue.saveblueapp.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.saveblue.saveblueapp.JwtHandler;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.animations.ViewAnimation;
import com.saveblue.saveblueapp.models.Account;
import com.saveblue.saveblueapp.ui.dashboard.add.AddAccountDialog;
import com.saveblue.saveblueapp.ui.dashboard.add.AddIncomeActivity;
import com.saveblue.saveblueapp.ui.dashboard.add.AddExpenseActivity;
import com.saveblue.saveblueapp.ui.dashboard.overview.OverviewFragment;
import com.saveblue.saveblueapp.ui.dashboard.overview.OverviewViewModel;
import com.saveblue.saveblueapp.ui.login.RegisterDialog;

import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DashboardActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private boolean rotatedFAB = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_overview, R.id.nav_profile, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_account, menu);
        return true;
    }*/

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }



}