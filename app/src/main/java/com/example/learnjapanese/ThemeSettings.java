package com.example.learnjapanese;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Objects;

import static java.security.AccessController.getContext;

public class ThemeSettings {

    private SharedPreferences.Editor editor;
    private static String THEME_SETTING="THEME_SETTING";
    private SharedPreferences sharedPreferences;
    public int CHOOSED_THEME_SETTING=0;
    private FragmentActivity fragmentActivity;
    private static final String TAG="ThemeSettings";

    public ThemeSettings(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
        LoadTheme();
    }
    public void setPopupToolbarTheme(Toolbar toolbar){
        switch (CHOOSED_THEME_SETTING) {
            case 0:
                //toolbar.setPopupTheme(R.style.AppTheme_SliderActivityTheme);
                break;
            case 1:
                //toolbar.setPopupTheme(R.style.GreenTheme);
                break;
        }
    }
    public void LoadTheme(){
        sharedPreferences = Objects.requireNonNull(this.fragmentActivity).getSharedPreferences(THEME_SETTING, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        CHOOSED_THEME_SETTING = sharedPreferences.getInt(THEME_SETTING, 0);
        Log.d(TAG, "LoadTheme: "+CHOOSED_THEME_SETTING);
        SetTheme();
    }
    private void SetTheme(){
        switch (CHOOSED_THEME_SETTING) {
            case 0:
                fragmentActivity.setTheme(R.style.AppTheme_Red);
                break;
            case 1:
                fragmentActivity.setTheme(R.style.AppTheme_Dark);
                break;
            case 2:
                fragmentActivity.setTheme(R.style.AppTheme_Sunset);
                break;
        }
    }
    public void SaveTheme(int i){
        if (i==CHOOSED_THEME_SETTING)
            return;
        CHOOSED_THEME_SETTING=i;
        sharedPreferences = Objects.requireNonNull(this.fragmentActivity).getSharedPreferences(THEME_SETTING,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(THEME_SETTING,CHOOSED_THEME_SETTING);
        editor.apply();

        Intent intent = new Intent(fragmentActivity, MainActivity.class);
        fragmentActivity.finish();
        fragmentActivity.overridePendingTransition(0, 0);
        fragmentActivity.startActivity(intent);
        fragmentActivity.overridePendingTransition(0, 0);
        SetTheme();
    }
}
