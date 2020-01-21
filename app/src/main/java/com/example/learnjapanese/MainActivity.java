package com.example.learnjapanese;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.learnjapanese.Fragments.FragmentContacts;
import com.example.learnjapanese.Fragments.FragmentLearnWords;
import com.example.learnjapanese.Fragments.FragmentLogin;
import com.example.learnjapanese.Fragments.FragmentSettings;
import com.example.learnjapanese.Fragments.FragmentStatistic;
import com.example.learnjapanese.Fragments.FragmentUserPage;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    Toolbar toolbar;
    private int STORAGE_PERMISSION_CODE = 1;
    private static final String TAG = "MainActivity";
    FragmentManager fragmentManager;
    private boolean IS_STORAGE_GRANTED;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeSettings themeSettings = new ThemeSettings(MainActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final LinearLayout holder=findViewById(R.id.linearLayoutMainActivity);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {

                //this code-block is the real player behind this beautiful ui
                // basically, it's a mathemetical calculation which handles the shrinking of
                // our content view.

                float scaleFactor = 7f;
                float slideX = drawerView.getWidth() * slideOffset;

                holder.setTranslationX(slideX);
                holder.setScaleX(1 - (slideOffset / scaleFactor));
                holder.setScaleY(1 - (slideOffset / scaleFactor));

                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.addDrawerListener(toggle);






        toggle.syncState();

        if (savedInstanceState == null) {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container,
                    new FragmentLearnWords()).commit();
//            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//            if (firebaseAuth.getCurrentUser()!=null){
//                toolbar.setTitle("Ваша страница");
//                fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.fragment_container,
//                        new FragmentUserPage()).commit();
//            }else {
//                fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.fragment_container,
//                        new FragmentLogin()).commit();
//            }
        }

        CheckPermissionStorage();




    }

    private void CheckPermissionStorage(){
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            IS_STORAGE_GRANTED=true;
        }
        else{
            new AlertDialog.Builder(this)
                    .setTitle("Необходимо разрешение ")
                    .setMessage("Необходимо разрешение на хранение и запись информации")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestStoragePermission();
                        }
                    })
                    .create().show();
        }
    }
    private void requestStoragePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale((this),
                Manifest.permission.READ_EXTERNAL_STORAGE)){

            ActivityCompat.requestPermissions((this),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE){
            Log.d(TAG, "onRequestPermissionsResult: RequestCode==");
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "grantResults.length>0");
                IS_STORAGE_GRANTED=true;
                //Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }
            else {
                IS_STORAGE_GRANTED=false;
                finish();
                System.exit(0);
                Log.d(TAG, "grantResults.length<0");
                //Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }



    private static final String SHARED_PREFS = "sharedPrefs";
    public static String SHARED_PREFS_LANGUAGE="LANGUAGE";
    public static String ENGLISH="ENGLISH";
    public static String JAPANESE="JAPANESE";
    private void SetLanguage(String Language){
        SharedPreferences sharedPreferences = Objects.requireNonNull(this).getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFS_LANGUAGE,Language);
        editor.apply();
    }

    Fragment selectedFragment;
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        selectedFragment = null;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        switch (item.getItemId()) {
//            case R.id.nav_contacts:
//                toolbar.setTitle("Contacts");
//                selectedFragment = new FragmentContacts();
//                break;
            case R.id.nav_startlearning_japanese:
                toolbar.setTitle("Japanese");
                SetLanguage(JAPANESE);
                selectedFragment = new FragmentLearnWords();
                break;
            case R.id.nav_startlearning_english:
                toolbar.setTitle("English");
                SetLanguage(ENGLISH);
                selectedFragment = new FragmentLearnWords();
                break;
//            case R.id.nav_login:
//                if (firebaseAuth.getCurrentUser()!=null){
//                    toolbar.setTitle("Ваша страница");
//                    selectedFragment = new FragmentUserPage();
//                    break;
//                }else {
//                    toolbar.setTitle("Login");
//                    selectedFragment = new FragmentLogin();
//                    break;
//                }
            case R.id.nav_settings:
                toolbar.setTitle("Settings");
                selectedFragment=new FragmentSettings();
                break;
            case R.id.nav_statistic:
                toolbar.setTitle("Statistic");
                selectedFragment = new FragmentStatistic();
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }




}
