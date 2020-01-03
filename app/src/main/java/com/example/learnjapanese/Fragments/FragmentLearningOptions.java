package com.example.learnjapanese.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnjapanese.Adapters.ChoosedTagItemAdapter;
import com.example.learnjapanese.Adapters.Options;
import com.example.learnjapanese.R;
import com.example.learnjapanese.ThemeSettings;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class FragmentLearningOptions extends Fragment {
    private static final String TAG = "FragmentLearningOptions";

    ImageButton imageButtonTagChoose;
    Spinner spinnerFirstToShow;
    Switch switchShowLearned;
    Switch switchInfRepeat;
    Options mOptions;
    ArrayList<String> mTagName;
    TextView textViewWordCount;
    private RecyclerView recyclerViewTags;
    ChoosedTagItemAdapter adapter;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_learningoptions, container, false);
    }
    public static void setNumberPickerTextColor(NumberPicker numberPicker, int color)
    {

        try{
            Field selectorWheelPaintField = numberPicker.getClass()
                    .getDeclaredField("mSelectorWheelPaint");
            selectorWheelPaintField.setAccessible(true);
            ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
        }
        catch(NoSuchFieldException e){
            //Log.w("setNumberPickerTextColor", e);
        }
        catch(IllegalAccessException e){
            //Log.w("setNumberPickerTextColor", e);
        }
        catch(IllegalArgumentException e){
            //Log.w("setNumberPickerTextColor", e);
        }

        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText)
                ((EditText)child).setTextColor(color);
        }
        numberPicker.invalidate();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ThemeSettings themeSettings = new ThemeSettings(getActivity());
        mContext = view.getContext();
        Log.d(TAG, "View:Created");
        mOptions = new Options(getActivity());
        mOptions.LoadJsonSettings();
        mOptions.LoadJsonWords();
        mOptions.LoadJsonSettings();
        NumberPicker numberPicker = view.findViewById(R.id.numberPickerOptions);
        numberPicker.setMinValue(4);
        numberPicker.setMaxValue(20);
        setNumberPickerTextColor(numberPicker,0xFFEEEEEE);
        numberPicker.setValue(mOptions.showWordsMatchWordsCount);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int value) {
                mOptions.showWordsMatchWordsCount=value;
                mOptions.SaveJsonSettings();
            }
        });

        imageButtonTagChoose=view.findViewById(R.id.imageButtonTagSelect);
        textViewWordCount=view.findViewById(R.id.textViewWordCountSelected);
        switchShowLearned=view.findViewById(R.id.switchLearnedShow);
        switchInfRepeat=view.findViewById(R.id.switchInfRepeat);
        if (mOptions.boolShowLearnedWords)
            switchShowLearned.setChecked(true);

        if (mOptions.boolInfRepeat)
            switchInfRepeat.setChecked(true);

        imageButtonTagChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setTitle("Выберите теги");
                Log.d(TAG, "onClick: Started");
                mBuilder.setMultiChoiceItems(mOptions.ArrayStringOfAllTags, mOptions.boolCheckedTags, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        mOptions.boolCheckedTags[i]=b;
                        Log.d(TAG, "mBuilder: Started");

                        if (!b)
                            for(int j=0;j<mTagName.size();++j)
                                if (mTagName.get(j).contains(mOptions.ArrayStringOfAllTags[i]))
                                    mTagName.remove(j);

                        if (mOptions.boolCheckedTags[i]){
                            if (!mTagName.contains(mOptions.ArrayStringOfAllTags[i]))
                                mTagName.add(mOptions.ArrayStringOfAllTags[i]);
                        }else{

                        }
                        adapter.notifyDataSetChanged();
                        textViewWordCount.setText(Integer.toString(mOptions.CountWords(mTagName)));
                        mOptions.SaveJsonSettings();
                    }

                });

                mBuilder.setCancelable(true);
                mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                Log.d(TAG, "mBuilder: Ended");
                mBuilder.show();
                Log.d(TAG, "mBuilder: Showed");
            }
        });

        switchShowLearned.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mOptions.boolShowLearnedWords = b;
                mOptions.SaveJsonSettings();

            }
        });
        switchInfRepeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mOptions.boolInfRepeat = b;
                mOptions.SaveJsonSettings();

            }
        });
        recyclerViewTags = view.findViewById(R.id.recyclerViewTagsSelected);
        InitLearningOptionAdapter();
        super.onViewCreated(view, savedInstanceState);
    }


    private void InitLearningOptionAdapter() {

        Log.d(TAG, "initLearningAdapter:started");
        mTagName = new ArrayList<>();
        if (mOptions.boolCheckedTags!=null) {
            for (int i = 0; i < mOptions.ArrayStringOfAllTags.length; ++i) {
                if (mOptions.boolCheckedTags[i]) {
                    mTagName.add(mOptions.ArrayStringOfAllTags[i]);
                }

            }
        }

        initRecyclerViewOption();
    }



    @SuppressLint("SetTextI18n")
    private void initRecyclerViewOption() {

        Log.d(TAG, "initRecyclerView:started");
        if (mContext != null) {
            Log.d(TAG, "mContext=" + mContext);
        }
        recyclerViewTags.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new ChoosedTagItemAdapter(mTagName);
        //recyclerViewTags.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerViewTags.setAdapter(adapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                String tagName=adapter.getTagAt(viewHolder.getAdapterPosition());
                if (mOptions.ArrayStringOfAllTags !=null)
                    for(int i = 0; i<mOptions.ArrayStringOfAllTags.length; ++i){
                        if (mOptions.ArrayStringOfAllTags[i].contains(tagName)){
                            mOptions.boolCheckedTags[i]=false;
                            mOptions.SaveJsonSettings();
                        }
                    }
                mTagName.remove(tagName);
                adapter.notifyDataSetChanged();
                textViewWordCount.setText(Integer.toString(mOptions.CountWords(mTagName)));
                mOptions.SaveJsonSettings();

            }
        }).attachToRecyclerView(recyclerViewTags);
        textViewWordCount.setText(Integer.toString(mOptions.CountWords(mTagName)));
    }

}
