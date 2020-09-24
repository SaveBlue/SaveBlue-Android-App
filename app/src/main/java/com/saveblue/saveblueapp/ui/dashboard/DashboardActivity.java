package com.saveblue.saveblueapp.ui.dashboard;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.saveblue.saveblueapp.R;
import com.saveblue.saveblueapp.animations.ViewAnimation;

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

        // Handle animations for FAB
        ViewAnimation.init(findViewById(R.id.fabIncome));
        ViewAnimation.init(findViewById(R.id.fabExpense));

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
                //TODO Lan 2: add Expenses and Incomes Activities
            }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_account, menu);
        return true;
    }

    //TODO Lan 1: Add "+" icon instead of onCreateOptionsMenu NOK

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}