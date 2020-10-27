package com.saveblue.saveblueapp.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.animations.ViewAnimation;
import com.saveblue.saveblueapp.ui.addExpenseIncome.AddIncomeActivity;
import com.saveblue.saveblueapp.ui.addExpenseIncome.AddExpenseActivity;

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

        initFAB();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_overview, R.id.nav_profile, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    // Initialise floating action button
    private void initFAB() {

        // FAB-related
        FloatingActionButton fab = findViewById(R.id.fab);
        FloatingActionButton fabIncome = findViewById(R.id.fabIncome);
        FloatingActionButton fabExpense = findViewById(R.id.fabExpense);

        // Handle animations for FAB
        ViewAnimation.init(findViewById(R.id.fabIncome));
        ViewAnimation.init(findViewById(R.id.fabExpense));

        // Set onClickListeners for FABs
        fab.setOnClickListener(view -> {

            rotatedFAB = ViewAnimation.rotateFab(view, !rotatedFAB);
            if (rotatedFAB) {
                ViewAnimation.showIn(findViewById(R.id.fabIncome));
                ViewAnimation.showIn(findViewById(R.id.fabExpense));
            } else {
                ViewAnimation.showOut(findViewById(R.id.fabIncome));
                ViewAnimation.showOut(findViewById(R.id.fabExpense));
            }
        });

        // Incomes Activity
        fabIncome.setOnClickListener(v -> {
            Intent intentAddIncome = new Intent(getApplicationContext(), AddIncomeActivity.class);
            intentAddIncome.putExtra("Task", "ADD");
            intentAddIncome.putExtra("CallingActivity", "Dashboard");
            startActivity(intentAddIncome);
        });

        // Expenses Activity
        fabExpense.setOnClickListener(v -> {
            Intent intentAddExpense = new Intent(getApplicationContext(), AddExpenseActivity.class);
            intentAddExpense.putExtra("Task", "ADD");
            intentAddExpense.putExtra("CallingActivity", "Dashboard");
            startActivity(intentAddExpense);
        });
    }

    // handles navigation
    @Override
    public boolean onSupportNavigateUp() {

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}
