package com.example.quickconnect_employee_cc;

import static com.example.quickconnect.R.id.nav_e_cc_home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.quickconnect.R;
import com.example.quickconnect.databinding.ActivityEmployeeCallCentreMainBinding;
import com.google.android.material.navigation.NavigationView;

public class Employee_CallCentre_Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    ActivityEmployeeCallCentreMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployeeCallCentreMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.navView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.open_nav, R.string.close_nav);

         binding.drawerLayout.addDrawerListener(toggle);

         toggle.syncState();

         if(savedInstanceState == null) {
             getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Employee_Home_Fragment()).commit();
             binding.navView.setCheckedItem(nav_e_cc_home);
         }

    }



    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == nav_e_cc_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Employee_Home_Fragment()).commit();
        } else if (itemId == R.id.nav_e_cc_call) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Employee_Call_Fragment()).commit();
        } else if (itemId == R.id.nav_e_cc_language) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Employee_Language_Fragment()).commit();
        } else if (itemId == R.id.nav_e_cc_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Employee_Profile_Fragment()).commit();
        } else if (itemId == R.id.nav_e_cc_logout) {
            Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
        }
      binding.drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}