package com.example.learnjapanese.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnjapanese.ActivityFindTranslationTraining;
import com.example.learnjapanese.ActivityLearningOptions;
import com.example.learnjapanese.ActivityMatchWords;
import com.example.learnjapanese.Adapters.DialogLearningItemAdapter;
import com.example.learnjapanese.Adapters.Options;
import com.example.learnjapanese.R;
import com.example.learnjapanese.ThemeSettings;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentLearningItemChoose extends Fragment implements DialogLearningItemAdapter.OnNoteListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_learningitemchoose, container, false);
    }

    Context mContext;
    Options mOptions;
    private static final String TAG = "FragmentLearnWords";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ThemeSettings themeSettings = new ThemeSettings(getActivity());
        super.onViewCreated(view, savedInstanceState);

        mContext = view.getContext();
        recyclerViewDialogLearn = Objects.requireNonNull(getView()).findViewById(R.id.recyclerViewItemLearn);
        Log.d(TAG, "View:Created");
        mOptions = new Options(getActivity());
        mOptions.LoadJsonSettings();
        mOptions.LoadJsonWords();
        mOptions.LoadJsonSettings();
        InitLearningOptionAdapter();
    }

    private ArrayList<Integer> mItemImage;
    private ArrayList<String> mItemOption;

    private void InitLearningOptionAdapter() {

        Log.d(TAG, "initLearningAdapter:started");
        mItemImage = new ArrayList<>();
        mItemOption = new ArrayList<>();

        mItemOption.add("Поиск перевода");
        mItemOption.add("Посик слова");
        mItemOption.add("Сопоставление слов");

        mItemImage.add(R.drawable.ic_match);
        mItemImage.add(R.drawable.ic_message);
        mItemImage.add(R.drawable.ic_contacts);
        initRecyclerViewOption();
    }

    private RecyclerView recyclerViewDialogLearn;

    private void initRecyclerViewOption() {
        Log.d(TAG, "initRecyclerView:started");

        DialogLearningItemAdapter adapter = new DialogLearningItemAdapter(mItemImage, mItemOption, Integer.toString(mOptions.countSelectedWods), this);
        recyclerViewDialogLearn.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerViewDialogLearn.setHasFixedSize(true);
        recyclerViewDialogLearn.setAdapter(adapter);

    }

    @Override
    public void onNoteClick(int position) {
        System.out.println((position) + "====Position");

        if (mOptions.countSelectedWods<4){
            Toast.makeText(mContext, "Необходимо хотя бы 4 слова", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mOptions.mWordsToLearn.size() > 0) {
            Intent intent;
            switch (position) {
                case 0:
                    intent = new Intent(mContext, ActivityFindTranslationTraining.class);
                    intent.putExtra("Type",0);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.menu.right_slide_in, R.menu.right_slide_out);
                    break;
                case 1:
                    intent = new Intent(mContext, ActivityFindTranslationTraining.class);
                    intent.putExtra("Type",1);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.menu.right_slide_in, R.menu.right_slide_out);
                    break;
                case 2:
                    intent = new Intent(mContext, ActivityMatchWords.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.menu.right_slide_in, R.menu.right_slide_out);
                    break;


            }
        } else {
            Toast.makeText(mContext, "Не выбраны теги!", Toast.LENGTH_SHORT).show();
        }


    }
}
