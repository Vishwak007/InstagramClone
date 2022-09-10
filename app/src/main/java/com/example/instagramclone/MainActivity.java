package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.instagramclone.fragment.HomeFragment;
import com.example.instagramclone.fragment.NotificationFragment;
import com.example.instagramclone.fragment.ProfileFragment;
import com.example.instagramclone.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottom_nav_bar;
    private Fragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottom_nav_bar = findViewById(R.id.bottom_nav_bar);

        bottom_nav_bar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_home :
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.nav_search:
                        selectedFragment = new SearchFragment();
                        break;
                    case R.id.nav_heart:
                        selectedFragment = new NotificationFragment();
                        break;
                    case R.id.nav_add:
                        selectedFragment = null;
                        Intent intent =  new Intent(MainActivity.this, PostActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_profile:
                        selectedFragment = new ProfileFragment();

                }
                if (selectedFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                }

                return true;

            }
        });
        Bundle intent = getIntent().getExtras();  // saara material Bundle ke andar jaata hai jo bhi intent se aata hai..
        if (intent != null){
            String profileId = intent.getString("publisherId");
            getSharedPreferences("PROFILE", MODE_PRIVATE).edit().putString("profileId", profileId).apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            bottom_nav_bar.setSelectedItemId(R.id.nav_profile);
        }
        else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }



    }
}