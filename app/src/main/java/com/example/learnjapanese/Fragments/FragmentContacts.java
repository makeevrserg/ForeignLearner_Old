package com.example.learnjapanese.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.learnjapanese.R;
import com.example.learnjapanese.ThemeSettings;

public class FragmentContacts extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ThemeSettings themeSettings = new ThemeSettings(getActivity());
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }
}
