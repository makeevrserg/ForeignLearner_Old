package com.example.learnjapanese;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnjapanese.Adapters.ChoosedTagItemAdapter;
import com.example.learnjapanese.Adapters.DBHelper;
import com.example.learnjapanese.Adapters.Options;
import com.example.learnjapanese.Adapters.WordEditInputElementAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class EditWordActivity extends AppCompatActivity {

    ImageView imageViewClose;
    Button buttonSave;
    EditText editTextWord;
    ImageView imageViewSpeakWord;
    ArrayList<EditText> editTextTranscription;
    ArrayList<EditText> editTextsTranslation;
    RecyclerView recyclerViewSelectedTags;
    TextView textViewAddTranslation;
    TextToSpeech textToSpeech;
    ImageView imageViewSelectTags;
    TextView textViewAddTranscription;
    ArrayList<EditText> editTextsTags;
    Options mOptions;
    RecyclerView recyclerViewEditTextTranscription;
    RecyclerView recyclerViewEditTextTranslation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeSettings themeSettings = new ThemeSettings(this);
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int POSITION = intent.getIntExtra("position", -1);
        setContentView(R.layout.activity_edit_word);
        mOptions = new Options(this);
        mOptions.LoadJsonSettings();
        mOptions.LoadJsonWords();
        recyclerViewEditTextTranscription = findViewById(R.id.recycler_viewEditTextTranscription);
        recyclerViewEditTextTranslation = findViewById(R.id.recycler_viewEditTextTranslation);
        imageViewClose = findViewById(R.id.imageViewClose);

        buttonSave = findViewById(R.id.buttonChangeWord);

        editTextWord = findViewById(R.id.editTextWord);

        imageViewSpeakWord = findViewById(R.id.imageViewSpeakWord);

        editTextTranscription = new ArrayList<>();
        editTextTranscription.add(new EditText(this));



        recyclerViewSelectedTags = findViewById(R.id.recyclerViewTagsSelected_word_edit);

        imageViewSelectTags = findViewById(R.id.imageButtonTagSelect_word_edit);

        textViewAddTranscription = findViewById(R.id.textViewAddTranscription);

        editTextsTags = new ArrayList<>();


        textViewAddTranslation = findViewById(R.id.textViewAddTranslation);


        editTextsTranslation = new ArrayList<>();
        editTextsTranslation.add(new EditText(this));

        stringsOfTags = Arrays.copyOf(mOptions.ArrayStringOfAllTags,mOptions.ArrayStringOfAllTags.length);
        boolSelectedTags = new boolean[stringsOfTags.length];

        mTagName = new ArrayList<>();
        Log.d(TAG, "onCreate: "+POSITION);
        if (POSITION!=-1){
            WordElement word = mOptions.mWordElement.get(POSITION);
            editTextsTranslation = new ArrayList<>();
            editTextTranscription.add(new EditText(this));
            editTextTranscription.get(0).setText(word.transcription);
            editTextWord.setText(word.word);
            for(String translation:word.translation){
                EditText editText = new EditText(this);
                editText.setText(translation);
                editTextsTranslation.add(editText);
            }
            for(int i =0;i<stringsOfTags.length;++i){
                for(String tag:word.tags){
                    if (stringsOfTags[i].replaceAll(" ","").equals(tag.replaceAll(" ",""))) {
                        boolSelectedTags[i] = true;
                        AddToMTag(i,boolSelectedTags[i]);
                    }
                }
            }

        }

//        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int i) {
//                if (i!=TextToSpeech.ERROR){
//                    textToSpeech.setLanguage(Locale.JAPAN);
//                }else{
//                    Toast.makeText(EditWordActivity.this, "Synth ERROR", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
        initRecyclerViewOption(recyclerViewEditTextTranscription,"Транскрипция",editTextTranscription);
        initRecyclerViewOption(recyclerViewEditTextTranslation,"Перевод",editTextsTranslation);
        initRecyclerViewSelectedTagsOption();
    }



    private void AddToMTag(int i,boolean b){
        boolSelectedTags[i]=b;
        Log.d(TAG, "mBuilder: Started");
        if (!b)
            for(int j = 0; j< mTagName.size(); ++j)
                if (mTagName.get(j).contains(stringsOfTags[i]))
                    mTagName.remove(j);

        if (boolSelectedTags[i]){
            if (!mTagName.contains(mOptions.ArrayStringOfAllTags[i]))
                mTagName.add(mOptions.ArrayStringOfAllTags[i]);
        }
    }


    private static final String TAG = "EditWordActivity";
    public void onClickSelectTags(View view) {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Выберите теги");
        Log.d(TAG, "onClick: Started");
        mBuilder.setMultiChoiceItems(stringsOfTags, boolSelectedTags, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                AddToMTag(i,b);
                adapter.notifyDataSetChanged();
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


    ArrayList<String> mTagName;
    ChoosedTagItemAdapter adapter;
    @SuppressLint("SetTextI18n")
    private void initRecyclerViewSelectedTagsOption() {
        Log.d(TAG, "initRecyclerView:started");

        recyclerViewSelectedTags.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChoosedTagItemAdapter(mTagName);
        //recyclerViewTags.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerViewSelectedTags.setAdapter(adapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                String tagName=adapter.getTagAt(viewHolder.getAdapterPosition());
                if (stringsOfTags !=null)
                    for(int i = 0; i<stringsOfTags.length; ++i){
                        if (stringsOfTags[i].contains(tagName)){
                            boolSelectedTags[i]=false;
                        }
                    }
                mTagName.remove(tagName);
                adapter.notifyDataSetChanged();


            }
        }).attachToRecyclerView(recyclerViewSelectedTags);
    }


    WordEditInputElementAdapter adapterTranslation;
    WordEditInputElementAdapter adapterTranscription;
    @SuppressLint("SetTextI18n")
    private void initRecyclerViewOption(RecyclerView recyclerView, final String hintText, final ArrayList<EditText> editTexts) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (hintText.equals("Перевод")){
            adapterTranslation = new WordEditInputElementAdapter(hintText,editTexts);
            recyclerView.setAdapter(adapterTranslation);
        } else{
            adapterTranscription = new WordEditInputElementAdapter(hintText,editTexts);
            recyclerView.setAdapter(adapterTranscription);
        }
        recyclerView.setItemViewCacheSize(editTexts.size());
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (editTexts.size()<2) {
                    if (hintText.equals("Перевод"))
                        adapterTranslation.notifyDataSetChanged();
                    else
                        adapterTranscription.notifyDataSetChanged();
                    return;
                }

                editTexts.remove(viewHolder.getAdapterPosition());
                if (hintText.equals("Перевод"))
                    adapterTranslation.notifyDataSetChanged();
                else
                    adapterTranscription.notifyDataSetChanged();


            }
        }).attachToRecyclerView(recyclerView);
    }

    boolean [] boolSelectedTags;
    String [] stringsOfTags;
    public void onClickAddTag(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите новый тег");
        final EditText editTextInput = new EditText(this);
        builder.setView(editTextInput);
        builder.setCancelable(true);
        builder.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final AlertDialog.Builder builderConf = new AlertDialog.Builder(EditWordActivity.this);
                builderConf.setTitle("Подтвердите создание нового тега");
                builderConf.setMessage(editTextInput.getText().toString());
                builderConf.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mOptions.ArrayStringOfAllTags = Arrays.copyOf(mOptions.ArrayStringOfAllTags,mOptions.ArrayStringOfAllTags.length+1);
                        mOptions.boolCheckedTags = Arrays.copyOf(mOptions.boolCheckedTags,mOptions.boolCheckedTags.length+1);
                        mOptions.ArrayStringOfAllTags[mOptions.ArrayStringOfAllTags.length-1] = editTextInput.getText().toString();
                        mOptions.boolCheckedTags[mOptions.boolCheckedTags.length-1]=false;
                        Toast.makeText(EditWordActivity.this, editTextInput.getText().toString()+" создан", Toast.LENGTH_SHORT).show();
                        boolSelectedTags=Arrays.copyOf(mOptions.boolCheckedTags,mOptions.boolCheckedTags.length);
                        stringsOfTags=Arrays.copyOf(mOptions.ArrayStringOfAllTags,mOptions.ArrayStringOfAllTags.length);
                        adapter.notifyDataSetChanged();
                        mOptions.SaveJsonSettings();
                    }
                });
                builderConf.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                final AlertDialog adConf=builderConf.create();
                adConf.show();
            }
        });
        final AlertDialog ad=builder.create();
        ad.show();

    }


    public void onClickSpeakWord(View view) {
        if (!textToSpeech.isSpeaking())
            textToSpeech.speak(editTextWord.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
    }

    public void onClickCloseEditor(View view) {
    }

    public void onClickSaveWord(View view) {

        String word=editTextWord.getText().toString();
        String []translations= new String[editTextsTranslation.size()];
        for(int i =0;i<translations.length;++i){
            View mView = recyclerViewEditTextTranslation.getChildAt(i);
            EditText nameEditText = (EditText) mView.findViewById(R.id.editTextInputElementAdapter);
            String name = nameEditText.getText().toString();
            translations[i]=name;
        }
        String []transcriptions= new String[editTextTranscription.size()];
        for(int i =0;i<transcriptions.length;++i){
            View mView = recyclerViewEditTextTranscription.getChildAt(i);
            EditText nameEditText = (EditText) mView.findViewById(R.id.editTextInputElementAdapter);
            String name = nameEditText.getText().toString();
            transcriptions[i]=name;
        }
        int countTags=0;
        ArrayList<String> tags = new ArrayList<>();
        for(int i =0;i<boolSelectedTags.length;++i) {
            if (boolSelectedTags[i])
                tags.add(stringsOfTags[i]);
        }

        Log.d(TAG, "onClickSaveWord: Word="+word);
        Log.d(TAG, "onClickSaveWord: Transcription="+ Arrays.toString(transcriptions));
        Log.d(TAG, "onClickSaveWord: Translations="+ Arrays.toString(translations));
        Log.d(TAG, "onClickSaveWord: Tags="+tags.toString());

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

            String translation="[";
            String tagsToPut="[";
            for(int k =0;k<translations.length;++k)
                if (k!=translations.length-1)
                    translation+=("\""+translations[k]+"\",");
                else
                    translation+=("\""+translations[k]+"\"]");


        if (tags.size()==0)
            tags.add("Untagged");
            for(int k =0;k<tags.size();++k)
                if (k!=tags.size()-1)
                    tagsToPut+=("\""+tags.get(k)+"\",");
                else
                    tagsToPut+=("\""+tags.get(k)+"\"]");

            contentValues.put(DBHelper.KEY_WORD,word);
            contentValues.put(DBHelper.KEY_TRANSCRIPTION, Arrays.toString(transcriptions));
            contentValues.put(DBHelper.KEY_TRANSLATION, translation);
            contentValues.put(DBHelper.KEY_TAGS, tagsToPut);
            contentValues.put(DBHelper.KEY_PROGRESS, "0");

        Cursor cursor;

        String TABLE=(!mOptions.SELECTED_LANGUAGE.equals("JAPANESE"))?DBHelper.TABLE_WORD_ENGLISH:DBHelper.TABLE_WORD;

        cursor = database.query(TABLE,
                    null,null,null,null,null,null);

        boolean isFound=false;
        if (cursor.moveToFirst()){
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int wordIndex = cursor.getColumnIndex(DBHelper.KEY_WORD);
            int transcriptionIndex = cursor.getColumnIndex(DBHelper.KEY_TRANSCRIPTION);
            int translationIndex = cursor.getColumnIndex(DBHelper.KEY_TRANSLATION);
            int tagsIndex = cursor.getColumnIndex(DBHelper.KEY_TAGS);
            int progressIndex = cursor.getColumnIndex(DBHelper.KEY_PROGRESS);

            do{
                Log.d(TAG, "TableText=: "
                        +"ID="+cursor.getInt(idIndex)
                        +",word="+cursor.getString(wordIndex)
                        +",transcription="+cursor.getString(transcriptionIndex)
                        +",translation="+cursor.getString(translationIndex)
                        +",tags="+cursor.getString(tagsIndex)
                        +",progress="+cursor.getString(progressIndex)+"\n");

                if (word.replaceAll(" ","").equals(cursor.getString(wordIndex).replaceAll(" ",""))){

                    database.delete(TABLE, DBHelper.KEY_WORD + "='"+cursor.getString(wordIndex)+"'", null);

                    isFound=true;
                    break;
                }
            } while (cursor.moveToNext());
        }else{
            Log.d(TAG, "CsvToSqlite: NothingToShow");
        }

        database.insert(TABLE,null,contentValues);
        cursor.close();
        dbHelper.close();
    }

    private void AddEditText(String hint,ArrayList<EditText> editTextArrayList,LinearLayout linearLayout){


    }

    public void onClickAddTranscription(View view) {
        editTextTranscription.add(new EditText(this));
        adapterTranscription.notifyDataSetChanged();
    }

    public void onClickAddTranslation(View view) {
        editTextsTranslation.add(new EditText(this));
        adapterTranslation.notifyDataSetChanged();
    }
}
