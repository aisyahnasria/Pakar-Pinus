package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class DashboardActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Inisialisasi BottomNavigationView
        bottomNavigationView = findViewById(R.id.btn_dasboard); // Sesuaikan dengan ID dari XML

        // Set listener yang benar
        bottomNavigationView.setOnItemSelectedListener(item -> handleNavigation(item));

        // Pilih item default
        bottomNavigationView.setSelectedItemId(R.id.home_menu);
    }

    // Inisialisasi fragmen
    HomeFragment homeFragment = new HomeFragment();
    HistoryFragment historyFragment = new HistoryFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    // Metode untuk menangani navigasi
    private boolean handleNavigation(MenuItem item) {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.home_menu) {
            selectedFragment = homeFragment;
        } else if (itemId == R.id.history_menu) {
            selectedFragment = historyFragment;
        } else if (itemId == R.id.profile_menu) {
            selectedFragment = profileFragment;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, selectedFragment).commit();
            return true;
        }

        return false;
    }
}
