package com.example.learnjapanese.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnjapanese.ActivityFindTranslationTraining;
import com.example.learnjapanese.ActivityLearningOptions;
import com.example.learnjapanese.Adapters.LearningWordListItemAdapter;
import com.example.learnjapanese.Adapters.Options;
import com.example.learnjapanese.EditWordActivity;
import com.example.learnjapanese.R;
import com.example.learnjapanese.ThemeSettings;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class FragmentLearnWords extends Fragment implements LearningWordListItemAdapter.OnNoteListener {
    private static final String TAG = "FragmentLearnWords";
    private Context mContext;
    private RecyclerView recyclerViewDialogLearn;
    private FloatingActionButton floatingActionButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_learnwords, container, false);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    Options mOptions;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.drawer_mlearn,menu);

        MenuItem item = menu.findItem(R.id.action_search_words);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_startlearning:
                StartLearn();
                break;
            case R.id.action_search_words:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private ProgressDialog progressDialog;

    private void ShowProgressDialog(){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Идет загрузка слов");
        progressDialog.setMessage("Пожалуйста подождите");
        progressDialog.show();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ThemeSettings themeSettings = new ThemeSettings(getActivity());

        mContext = view.getContext();
        floatingActionButton = view.findViewById(R.id.floatingButtonAddWord);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditWordActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.menu.right_slide_in,R.menu.right_slide_out);
            }
        });
        ShowProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: Thread Started");
                mOptions = new Options(getActivity());
                mOptions.LoadJsonSettings();
                mOptions.LoadJsonWords();
                Looper.prepare();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerViewDialogLearn= Objects.requireNonNull(getView()).findViewById(R.id.recyclerViewLearnWords);
                        InitLearningOptionAdapter();
                        progressDialog.dismiss();
                        progressDialog.cancel();
                    }
                });
            }
        }).start();


        super.onViewCreated(view, savedInstanceState);
    }
    private void InitLearningOptionAdapter() {
        Log.d(TAG, "initLearningAdapter:started");
        initRecyclerViewOption();
    }



    public void StartLearn() {
        Intent intent = new Intent(getContext(), ActivityLearningOptions.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.menu.right_slide_in,R.menu.right_slide_out);
    }
    LearningWordListItemAdapter adapter;
    private void initRecyclerViewOption() {
        Log.d(TAG, "initRecyclerView:started");
        adapter = new LearningWordListItemAdapter(mOptions.mWordElement,this);
        recyclerViewDialogLearn.setAdapter(adapter);
        recyclerViewDialogLearn.setLayoutManager(new LinearLayoutManager(mContext));
    }


    @Override
    public void onNoteClick(int position) {

        Log.d(TAG, "onNoteClick: "+adapter.getFilter().toString());
        Log.d(TAG, "onNoteClick: "+mOptions.mWordElement.get(position).word);
        Intent intent = new Intent(mContext, EditWordActivity.class);
        intent.putExtra("position",position);
        startActivity(intent);
    }
}
