package com.example.learnjapanese;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.learnjapanese.Fragments.FragmentLearningItemChoose;
import com.example.learnjapanese.Fragments.FragmentLearningOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.r0adkll.slidr.Slidr;

public class ActivityLearningOptions extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeSettings themeSettings = new ThemeSettings(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learningoptions);
        Slidr.attach(this);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;

                switch (item.getItemId())   {
                    case R.id.startLearning:
                        selectedFragment = new FragmentLearningItemChoose();
                        break;
                    case R.id.learningSettings:
                        selectedFragment = new FragmentLearningOptions();
                        break;
                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                assert selectedFragment != null;
                transaction.replace(R.id.frameLayoutChoose,selectedFragment);
                transaction.commit();
                return false;
            }
        });
        setDefaultFragment();
    }

    private void setDefaultFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutChoose, new FragmentLearningItemChoose());
        transaction.commit();
    }
}
