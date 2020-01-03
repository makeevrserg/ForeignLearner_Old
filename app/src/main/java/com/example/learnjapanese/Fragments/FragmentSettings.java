package com.example.learnjapanese.Fragments;

import android.R.layout;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.learnjapanese.ActivityImportExport;
import com.example.learnjapanese.Adapters.DBHelper;
import com.example.learnjapanese.Adapters.Options;
import com.example.learnjapanese.R;
import com.example.learnjapanese.ThemeSettings;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class FragmentSettings extends Fragment  {
    Button buttonImportExport;
    Spinner spinnerThemes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }




    @SuppressLint("CommitPrefEdits")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spinnerThemes = view.findViewById(R.id.spinnerThemes);

        final ThemeSettings themeSettings = new ThemeSettings(getActivity());
        spinnerThemes.setSelection(themeSettings.CHOOSED_THEME_SETTING);
        spinnerThemes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                themeSettings.SaveTheme(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        buttonImportExport = view.findViewById(R.id.buttonImportExport);
        buttonImportExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ActivityImportExport.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.menu.right_slide_in, R.menu.right_slide_out);
            }
        });

    }




}
