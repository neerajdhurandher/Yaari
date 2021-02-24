package com.example.apptrail4;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottom_navigation = (BottomNavigationView)
                findViewById(R.id.bottomNavigationView);
        bottom_navigation.setOnNavigationItemSelectedListener(NavListener);
        NavController navController = Navigation.findNavController(this, R.id.fragment2);

    }
    BottomNavigationView.OnNavigationItemSelectedListener NavListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()){
                case R.id.home_frg:
              selectedFragment = new home_frg();
              break;
                case R.id.search_frg:
                    selectedFragment = new search_frg();

                    break;

                case R.id.new_post_frg:

                    selectedFragment = new new_post_frg();
                    break;

                case  R.id.chat_frg:
                    selectedFragment = new chat_frg();
                    break;
                case  R.id.profile_frg:
                    selectedFragment = new profile_frg();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment2, selectedFragment).commit();
            return true;
        }
    };

}







