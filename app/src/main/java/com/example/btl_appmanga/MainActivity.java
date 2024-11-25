package com.example.btl_appmanga;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.auth.Login;
import com.example.fragment.FollowFragment;
import com.example.fragment.HomeFragment;
import com.example.fragment.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navView;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        preferences = getSharedPreferences("UserPref", MODE_PRIVATE);

        navView = findViewById(R.id.navView);
        getSupportFragmentManager().beginTransaction().replace(R.id.main, new HomeFragment()).commit();
        navView.setSelectedItemId(R.id.home);

        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                int itemID = item.getItemId();
                if (itemID == R.id.home) {
                    fragment = new HomeFragment();
                }
                if (itemID == R.id.follow) {
                    if(preferences.getBoolean("isLogin", false)) {
                        fragment = new FollowFragment();
                    }
                    else {
                        Intent intent = new Intent(MainActivity.this, Login.class);
                        startActivity(intent);
                        return false;
                    }
                }
                if (itemID == R.id.profile) {
                    if(preferences.getBoolean("isLogin", false)) {
                        fragment = new UserFragment();
                    }
                    else {
                        Intent intent = new Intent(MainActivity.this, Login.class);
                        startActivity(intent);
                        return false;
                    }
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.main, fragment).commit();
                return true;
            }
        });
    }
}